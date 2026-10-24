package com.claviger.primalmagickextended.common.events;

import com.claviger.primalmagickextended.common.capabilities.IPlayerFamiliar;
import com.claviger.primalmagickextended.common.familiars.FamiliarBinding;
import com.claviger.primalmagickextended.common.familiars.FamiliarCandidates;
import com.claviger.primalmagickextended.common.familiars.FamiliarManager;
import com.claviger.primalmagickextended.common.familiars.OcelotConversion;
import com.claviger.primalmagickextended.common.familiars.StoredFamiliar;
import com.claviger.primalmagickextended.common.items.familiars.FamiliarCollarItem;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Platform-neutral handlers for the events that drive familiars. The NeoForge listeners in
 * FamiliarEventListeners only forward to these.
 */
public class FamiliarEvents {
    /** Ticks between periodic stale-copy checks of each bound mob. */
    private static final int STALE_CHECK_INTERVAL = 20;

    /**
     * Applies a held Familiar Collar to the target. On the client this only predicts whether the
     * server will consume the click, so the client swings and skips the pet's own interaction; it
     * never changes any state there. On the server, any interaction with a stale or duplicate
     * familiar copy, collar or not, removes that copy instead, so its inventory can never be used.
     * Fake players (any server player that is not the one in the server's player list under its UUID,
     * such as a machine using items) never bind or convert anything.
     *
     * @return true if the interaction was consumed and the event should be cancelled with SUCCESS
     */
    public static boolean onEntityInteract(Player player, InteractionHand hand, Entity target) {
        if (!player.level().isClientSide() && target instanceof Mob mob && !mob.isRemoved() && staleCopy(mob, player.level())) {
            FamiliarManager.discardStale(mob, false);
            return true;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof FamiliarCollarItem)) {
            return false;
        }
        if (player.level().isClientSide()) {
            return target instanceof Ocelot
                    || (FamiliarCandidates.isCandidateType(target) && target instanceof Mob mob && FamiliarCandidates.looksOwnedOnClient(mob, player));
        }
        if (!(player instanceof ServerPlayer serverPlayer) || !isRealPlayer(serverPlayer)) {
            return false;
        }
        if (target instanceof Mob mob && ServicesPMX.CAPABILITIES.binding(mob).isPresent()) {
            // bind() reports already-bound and foreign familiars itself; the click is consumed either way
            FamiliarManager.bind(serverPlayer, mob, stack);
            return true;
        }
        if (target instanceof Ocelot ocelot) {
            OcelotConversion.convertAndBind(serverPlayer, ocelot, stack);
            return true;
        }
        if (target instanceof Mob mob && FamiliarCandidates.isEligible(player, mob)) {
            FamiliarManager.bind(serverPlayer, mob, stack);
            return true;
        }
        return false;
    }

    /**
     * Protects a familiar from damage. A harmless hit (zero or less, such as a snowball) is simply
     * cancelled; any real damage dismisses the familiar. A stale or duplicate copy is removed instead.
     *
     * @return true if the damage event should be cancelled
     */
    public static boolean onIncomingDamage(LivingEntity entity, DamageSource source, float amount) {
        return protect(entity, amount);
    }

    /**
     * Safety net: a familiar never dies.
     *
     * @return true if the death event should be cancelled
     */
    public static boolean onDeath(LivingEntity entity) {
        return protect(entity, Float.MAX_VALUE);
    }

    private static boolean protect(LivingEntity entity, float amount) {
        if (entity.level().isClientSide() || !(entity instanceof Mob mob)) {
            return false;
        }
        Optional<FamiliarBinding> binding = ServicesPMX.CAPABILITIES.binding(mob);
        if (binding.isEmpty()) {
            return false;
        }
        if (amount <= 0.0F) {
            return true;
        }
        ServerPlayer owner = owner(mob.level(), binding.get());
        if (owner == null || staleCopy(mob, mob.level())) {
            FamiliarManager.discardStale(mob, true);
        } else {
            FamiliarManager.dismiss(owner, mob);
        }
        return true;
    }

    /**
     * Reconciles a familiar entity joining a level with its owner's data: stale copies and duplicates
     * are discarded, a current copy is adopted as the active instance.
     *
     * @return true if the entity should be discarded and the join cancelled
     */
    public static boolean onEntityJoin(Entity entity, Level level) {
        if (level.isClientSide() || !(entity instanceof Mob mob)) {
            return false;
        }
        Optional<FamiliarBinding> binding = ServicesPMX.CAPABILITIES.binding(mob);
        if (binding.isEmpty()) {
            return false;
        }
        if (staleCopy(mob, level)) {
            // Familiars travel with their owners; copies left behind while the owner is offline are stale too
            FamiliarManager.discardStale(mob, false);
            return true;
        }
        ServerPlayer owner = owner(level, binding.get());
        IPlayerFamiliar familiar = FamiliarManager.data(owner).orElseThrow();
        StoredFamiliar stored = familiar.getStored().orElseThrow();
        if (!stored.activeEntityId().equals(Optional.of(mob.getUUID()))) {
            familiar.setStored(Optional.of(stored.withActiveEntityId(Optional.of(mob.getUUID()))));
            ServicesPMX.CAPABILITIES.syncFamiliar(owner);
        }
        return false;
    }

    /**
     * Stores an in-play familiar with the departing player so nothing is orphaned in the world. A parrot
     * familiar perched on the player's shoulder is put back into the world first so it is dismissed too.
     * {@code Player.removeEntitiesOnShoulder} ignores parrots that landed less than 20 ticks ago;
     * such a parrot stays on the shoulder and is saved with the player, which keeps it with its owner anyway.
     */
    public static void onPlayerLogout(ServerPlayer player) {
        if (FamiliarManager.isFamiliarOnShoulder(player)) {
            player.removeEntitiesOnShoulder();
        }
        FamiliarManager.findActive(player).ifPresent(mob -> FamiliarManager.dismiss(player, mob));
    }

    /**
     * When a familiar's chunk unloads while its owner stays online, refreshes the owner's snapshot from the live
     * entity, marks the familiar dismissed and rotates the generation. The copy saved in the chunk is then stale and
     * is discarded when it reloads, so a later summon never duplicates the familiar or its inventory.
     * <p>
     * In a real chunk unload the level stops tracking the chunk's entities (firing the leave-level event) when the
     * chunk becomes hidden, while they are still unremoved; only afterwards does it save them and mark them
     * {@link Entity.RemovalReason#UNLOADED_TO_CHUNK}, with no further event. So a familiar that leaves the level
     * without being removed is treated as unloading, as is one removed with that reason. Every other removal
     * (death, discard, dimension change) is handled elsewhere. Stale copies and duplicates of the loaded active
     * familiar are ignored, and a second call for the same unload finds the generation already rotated and does
     * nothing. This handler never removes anything.
     */
    public static void onEntityLeaveLevel(Entity entity, Level level) {
        if (level.isClientSide() || !(entity instanceof Mob mob)) {
            return;
        }
        if (mob.isRemoved() && mob.getRemovalReason() != Entity.RemovalReason.UNLOADED_TO_CHUNK) {
            return;
        }
        Optional<FamiliarBinding> binding = ServicesPMX.CAPABILITIES.binding(mob);
        if (binding.isEmpty() || staleCopy(mob, level)) {
            return;
        }
        ServerPlayer owner = owner(level, binding.get());
        IPlayerFamiliar familiar = FamiliarManager.data(owner).orElseThrow();
        familiar.setStored(Optional.of(new StoredFamiliar(mob.getType(), FamiliarManager.snapshot(mob), UUID.randomUUID(), Optional.empty())));
        familiar.setFamiliarNearby(false);
        ServicesPMX.CAPABILITIES.syncFamiliar(owner);
    }

    /** Keeps every online player's nearby flag current. Called at the end of each server tick. */
    public static void onServerTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            FamiliarManager.updateProximity(player);
        }
    }

    /**
     * Periodically removes stale and duplicate familiar copies that are already in the world, for example a
     * copy that was tracked again without rejoining the level. Called for every server-side, non-player entity
     * that ticks on its own (a root entity). Passengers get no tick event of their own, so a root's indirect
     * passengers are checked with it; this covers familiars riding boats, minecarts and other non-mob vehicles.
     */
    public static void onEntityTick(Entity entity) {
        if (entity.level().isClientSide() || (entity.tickCount + entity.getId()) % STALE_CHECK_INTERVAL != 0) {
            return;
        }
        List<Mob> candidates = new ArrayList<>();
        if (entity instanceof Mob mob) {
            candidates.add(mob);
        }
        if (entity.isVehicle()) {
            for (Entity passenger : entity.getIndirectPassengers()) {
                if (passenger instanceof Mob passengerMob) {
                    candidates.add(passengerMob);
                }
            }
        }
        for (Mob candidate : candidates) {
            if (!candidate.isRemoved() && staleCopy(candidate, candidate.level())) {
                FamiliarManager.discardStale(candidate, false);
            }
        }
    }

    /**
     * True if the mob carries a familiar binding but is not its owner's current familiar: the owner is
     * offline, the owner's stored familiar is empty or of a different generation, or the mob is a duplicate
     * of the stored active familiar while that one is loaded. False for unbound mobs.
     */
    private static boolean staleCopy(Mob mob, Level level) {
        Optional<FamiliarBinding> binding = ServicesPMX.CAPABILITIES.binding(mob);
        if (binding.isEmpty()) {
            return false;
        }
        ServerPlayer owner = owner(level, binding.get());
        if (owner == null) {
            return true;
        }
        StoredFamiliar stored = FamiliarManager.data(owner).flatMap(IPlayerFamiliar::getStored).orElse(null);
        if (stored == null || !stored.generation().equals(binding.get().generation())) {
            return true;
        }
        Optional<UUID> active = stored.activeEntityId();
        return active.isPresent() && !active.get().equals(mob.getUUID())
                && FamiliarManager.findEntity(owner.level().getServer(), active.get()).isPresent();
    }

    /**
     * True if the player is the connected player the server lists under its UUID. Fake players are absent
     * from the list, or share a real player's UUID without being that player.
     */
    private static boolean isRealPlayer(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        return server != null && server.getPlayerList().getPlayer(player.getUUID()) == player;
    }

    @Nullable
    private static ServerPlayer owner(Level level, FamiliarBinding binding) {
        MinecraftServer server = level.getServer();
        return server == null ? null : server.getPlayerList().getPlayer(binding.ownerId());
    }
}
