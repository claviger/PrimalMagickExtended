package com.claviger.primalmagickextended.common.familiars;

import com.claviger.primalmagickextended.common.capabilities.IPlayerFamiliar;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.mojang.logging.LogUtils;
import com.verdantartifice.primalmagick.common.capabilities.IEntitySwappers;
import com.verdantartifice.primalmagick.common.misc.EntitySwapper;
import com.verdantartifice.primalmagick.platform.Services;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * The single place that reads and writes familiar state. All operations run on the server. Every
 * operation that changes a player's familiar data ends by syncing it to that player's client.
 */
public class FamiliarManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    /** Distance, in blocks, within which an in-play familiar grants its owner the mana bonus. */
    public static final double NEARBY_RANGE = 32.0D;
    /** Percentage points added to the mana cost modifier: 5, a little over two levels of Mana Efficiency (2 per level). */
    public static final int MANA_EFFICIENCY_BONUS = 5;

    /**
     * Minimum ticks between two toggles of the same player's familiar. A Projectile spell with the Fork mod
     * spawns independent projectiles that can land on different ticks, even seconds apart, and each would
     * otherwise toggle.
     */
    public static final int TOGGLE_DEBOUNCE_TICKS = 40;
    private static final double NEARBY_RANGE_SQR = NEARBY_RANGE * NEARBY_RANGE;
    /** The compound NeoForge writes an entity's serializable attachments into ({@code AttachmentHolder.ATTACHMENTS_NBT_KEY}). */
    private static final String ATTACHMENTS_KEY = "neoforge:attachments";
    /** The id of the familiar binding attachment, as NeoForge keys it inside {@link #ATTACHMENTS_KEY}. */
    private static final String BINDING_ATTACHMENT_KEY = "primalmagickextended:familiar_binding";
    /** Where Primal Magick's {@code EntitySwapper.serializeNBT} keeps the saved data of the pre-swap entity. */
    private static final String SWAPPER_ORIGINAL_DATA_KEY = "OriginalData";
    /** Test seam consulted before a familiar is added to the level; always accepts in production. */
    private static volatile Predicate<Mob> addFilter = mob -> true;

    /** The player's familiar data, or empty if the player is null or has no attachment. */
    public static Optional<IPlayerFamiliar> data(@Nullable Player player) {
        return ServicesPMX.CAPABILITIES.familiar(player);
    }

    /** True while the player's familiar is in play within {@link #NEARBY_RANGE}; valid on both sides. */
    public static boolean isFamiliarNearby(@Nullable Player player) {
        return data(player).map(IPlayerFamiliar::isFamiliarNearby).orElse(false);
    }

    /**
     * Saves the mob without its id, exactly as Primal Magick's entity swapper does, minus its
     * passengers: a familiar's riders are never part of the familiar.
     */
    public static CompoundTag snapshot(Mob mob) {
        CompoundTag tag = mob.saveWithoutId(new CompoundTag());
        tag.remove(Entity.PASSENGERS_TAG);
        return tag;
    }

    /** Finds a loaded, live mob with the given UUID in any level of the server. */
    public static Optional<Mob> findEntity(MinecraftServer server, UUID uuid) {
        for (ServerLevel level : server.getAllLevels()) {
            if (level.getEntity(uuid) instanceof Mob mob && !mob.isRemoved()) {
                return Optional.of(mob);
            }
        }
        return Optional.empty();
    }

    /** The player's in-play familiar entity, or empty if dismissed or not loaded. */
    public static Optional<Mob> findActive(ServerPlayer player) {
        return data(player).flatMap(IPlayerFamiliar::getStored).flatMap(StoredFamiliar::activeEntityId)
                .flatMap(uuid -> findEntity(player.server, uuid));
    }

    /**
     * Binds the mob to the player as their familiar, releasing any previous familiar first.
     *
     * @return true if the mob is now the player's familiar
     */
    public static boolean bind(ServerPlayer player, Mob mob, ItemStack collarStack) {
        Optional<FamiliarBinding> existing = ServicesPMX.CAPABILITIES.binding(mob);
        if (existing.isPresent()) {
            player.sendSystemMessage(existing.get().isOwnedBy(player.getUUID()) ? FamiliarMessages.already(mob) : FamiliarMessages.notYours());
            return false;
        }
        IPlayerFamiliar familiar = data(player).orElse(null);
        if (familiar == null) {
            return false;
        }
        if (familiar.getStored().isPresent() && !release(player)) {
            // Never harm or lose the old familiar: if it cannot be released, do not bind a new one
            return false;
        }

        UUID generation = UUID.randomUUID();
        FamiliarBinding binding = new FamiliarBinding(player.getUUID(), generation);
        ServicesPMX.CAPABILITIES.setBinding(mob, binding);
        rewriteQueuedSwapperBindings(mob, Optional.of(binding));
        mob.setPersistenceRequired();
        familiar.setStored(Optional.of(new StoredFamiliar(mob.getType(), snapshot(mob), generation, Optional.of(mob.getUUID()))));
        ServicesPMX.CAPABILITIES.syncFamiliar(player);
        FamiliarFx.poof(mob.level(), mob);
        if (!player.hasInfiniteMaterials()) {
            collarStack.shrink(1);
        }
        player.sendSystemMessage(FamiliarMessages.bound(mob));
        return true;
    }

    /**
     * Turns the player's current familiar back into an ordinary pet, summoning it first if it is
     * dismissed. The pet keeps its name, owner, collar and everything else. A parrot familiar on the
     * player's shoulder is put back into the world first, as in {@link #toggle}.
     * <p>
     * If the stored snapshot cannot produce a mob at all (for example its entity type no longer makes
     * one), the familiar is logged and dropped and the player is told the bond has faded, so a broken
     * snapshot never blocks a new bind. If the snapshot loads but the level refuses to add the pet (for
     * example another mod vetoes the join), nothing changes: the player keeps the familiar, is told it
     * could not answer, and the caller must not bind a new one.
     *
     * @return true if the player no longer has a familiar; false if the level refused the released pet
     */
    public static boolean release(ServerPlayer player) {
        IPlayerFamiliar familiar = data(player).orElse(null);
        if (familiar == null || familiar.getStored().isEmpty()) {
            return true;
        }
        StoredFamiliar stored = familiar.getStored().get();
        Mob mob = findActiveReleasingShoulder(player, familiar).orElse(null);
        if (mob == null) {
            mob = loadFamiliar(player, stored).orElse(null);
            if (mob == null) {
                LOGGER.error("Could not restore {}'s familiar of type {} to release it; dropping it", player.getName().getString(), EntityType.getKey(stored.type()));
                familiar.setStored(Optional.empty());
                familiar.setFamiliarNearby(false);
                ServicesPMX.CAPABILITIES.syncFamiliar(player);
                player.sendSystemMessage(FamiliarMessages.lost());
                return true;
            }
            // A recreated pet must never join the level carrying a binding
            if (!addFamiliar(player.serverLevel(), mob, FamiliarManager::unbind)) {
                player.sendSystemMessage(FamiliarMessages.summonFailed());
                return false;
            }
        }
        unbind(mob);
        familiar.setStored(Optional.empty());
        familiar.setFamiliarNearby(false);
        ServicesPMX.CAPABILITIES.syncFamiliar(player);
        player.sendSystemMessage(FamiliarMessages.released(mob));
        return true;
    }

    /**
     * Snapshots the familiar into the player's data and removes it from the world with the poof effect.
     * Does nothing, apart from logging a warning, if the mob is not bound to the player, the player has
     * no stored familiar, or the mob's binding generation does not match the player's stored familiar.
     * The live entity type is stored, since a Primal Magick Polymorph can change the type while keeping
     * the binding.
     */
    public static void dismiss(ServerPlayer player, Mob mob) {
        Optional<FamiliarBinding> binding = ServicesPMX.CAPABILITIES.binding(mob);
        if (binding.isEmpty() || !binding.get().isOwnedBy(player.getUUID())) {
            LOGGER.warn("Refusing to dismiss entity {}, which is not {}'s familiar", mob.getUUID(), player.getName().getString());
            return;
        }
        IPlayerFamiliar familiar = data(player).orElse(null);
        StoredFamiliar stored = familiar == null ? null : familiar.getStored().orElse(null);
        if (stored == null) {
            LOGGER.warn("Refusing to dismiss entity {}, since {} has no stored familiar", mob.getUUID(), player.getName().getString());
            return;
        }
        if (!binding.get().generation().equals(stored.generation())) {
            LOGGER.warn("Refusing to dismiss entity {}, a stale copy of {}'s familiar", mob.getUUID(), player.getName().getString());
            return;
        }
        if (mob.isVehicle()) {
            mob.ejectPassengers();
        }
        if (mob.isPassenger()) {
            mob.stopRiding();
        }
        if (mob.isLeashed()) {
            // Drops the lead, as a leash broken any other way would
            mob.dropLeash(true, true);
        }
        familiar.setStored(Optional.of(new StoredFamiliar(mob.getType(), snapshot(mob), stored.generation(), Optional.empty())));
        familiar.setFamiliarNearby(false);
        ServicesPMX.CAPABILITIES.syncFamiliar(player);
        FamiliarFx.poof(mob.level(), mob);
        mob.discard();
    }

    /** Removes a familiar entity whose owner data cannot be updated (owner offline, or a stale copy). */
    public static void discardStale(Mob mob, boolean withFx) {
        if (withFx) {
            FamiliarFx.poof(mob.level(), mob);
        }
        mob.discard();
    }

    /**
     * Recreates the player's stored familiar beside the player under a new generation; no-op if the
     * familiar is already in play.
     *
     * @return true if the familiar is now in play
     */
    public static boolean summon(ServerPlayer player) {
        IPlayerFamiliar familiar = data(player).orElse(null);
        if (familiar == null || familiar.getStored().isEmpty()) {
            player.sendSystemMessage(FamiliarMessages.none());
            return false;
        }
        if (findActive(player).isPresent()) {
            return true;
        }
        StoredFamiliar stored = familiar.getStored().get();
        UUID generation = UUID.randomUUID();
        Optional<Mob> mob = loadFamiliar(player, stored);
        boolean added = mob.isPresent() && addFamiliar(player.serverLevel(), mob.get(), m -> {
            // The binding and the stored generation must agree before the entity joins the level,
            // or the join handler would treat it as a stale copy
            FamiliarBinding binding = new FamiliarBinding(player.getUUID(), generation);
            ServicesPMX.CAPABILITIES.setBinding(m, binding);
            // A polymorphed familiar's queued swap-back must restore the new binding, not the one it was saved with
            rewriteQueuedSwapperBindings(m, Optional.of(binding));
            familiar.setStored(Optional.of(stored.withGeneration(generation).withActiveEntityId(Optional.of(m.getUUID()))));
        });
        if (!added) {
            familiar.setStored(Optional.of(stored));
            player.sendSystemMessage(FamiliarMessages.summonFailed());
            return false;
        }
        ServicesPMX.CAPABILITIES.syncFamiliar(player);
        return true;
    }

    /**
     * Dismisses the familiar if it is in play, otherwise summons it. A toggle within
     * {@link #TOGGLE_DEBOUNCE_TICKS} ticks of the player's previous one is ignored, so toggles that arrive
     * together (such as the projectiles of one forked cast) are collapsed into one.
     * A parrot familiar on the player's shoulder is put back into the world first, but
     * {@code Player.removeEntitiesOnShoulder} ignores parrots that landed less than 20 ticks
     * ago; a toggle in that window summons a second parrot, and the shoulder copy is discarded as
     * stale when it hops off.
     */
    public static void toggle(ServerPlayer player) {
        IPlayerFamiliar familiar = data(player).orElse(null);
        if (familiar == null) {
            return;
        }
        long now = player.serverLevel().getGameTime();
        long last = familiar.getLastToggleGameTime();
        if (last >= 0L && now >= last && now - last < TOGGLE_DEBOUNCE_TICKS) {
            return;
        }
        familiar.setLastToggleGameTime(now);

        Optional<Mob> active = findActiveReleasingShoulder(player, familiar);
        if (active.isPresent()) {
            dismiss(player, active.get());
        } else {
            summon(player);
        }
    }

    /** True if the player has a bound familiar, whether it is in play or dismissed. */
    public static boolean hasBondedFamiliar(Player player) {
        return data(player).flatMap(IPlayerFamiliar::getStored).isPresent();
    }

    /** True if the player's bound familiar is currently in the world (loaded, or perched on a shoulder). */
    public static boolean isFamiliarInPlay(ServerPlayer player) {
        return data(player).filter(familiar -> familiar.getStored().isPresent())
                .map(familiar -> findActiveReleasingShoulder(player, familiar).isPresent()).orElse(false);
    }

    /**
     * Dismisses the player's familiar if it is in play. Unlike {@link #toggle} it never summons and is never
     * debounced. A parrot familiar on the shoulder is put back into the world first, as in {@link #toggle}.
     *
     * @return true if a familiar was dismissed
     */
    public static boolean dismissActive(ServerPlayer player) {
        IPlayerFamiliar familiar = data(player).orElse(null);
        if (familiar == null || familiar.getStored().isEmpty()) {
            return false;
        }
        Optional<Mob> active = findActiveReleasingShoulder(player, familiar);
        active.ifPresent(mob -> dismiss(player, mob));
        return active.isPresent();
    }

    /** Recomputes the nearby flag for the player and syncs it when it changes. Called every server tick. */
    public static void updateProximity(ServerPlayer player) {
        IPlayerFamiliar familiar = data(player).orElse(null);
        if (familiar == null || familiar.getStored().isEmpty()) {
            return;
        }
        Optional<UUID> activeId = familiar.getStored().get().activeEntityId();
        boolean nearby = activeId.isPresent() && (isOnShoulder(player, activeId.get()) || Optional.ofNullable(player.serverLevel().getEntity(activeId.get()))
                .filter(entity -> entity instanceof Mob && !entity.isRemoved() && entity.distanceToSqr(player) <= NEARBY_RANGE_SQR)
                .isPresent());
        if (nearby != familiar.isFamiliarNearby()) {
            familiar.setFamiliarNearby(nearby);
            ServicesPMX.CAPABILITIES.syncFamiliar(player);
        }
    }

    /** True if the player's in-play familiar is perched on one of their shoulders. */
    public static boolean isFamiliarOnShoulder(ServerPlayer player) {
        return data(player).flatMap(IPlayerFamiliar::getStored).flatMap(StoredFamiliar::activeEntityId)
                .filter(uuid -> isOnShoulder(player, uuid)).isPresent();
    }

    /**
     * True if either shoulder holds the entity with the given UUID. Shoulder tags are full saves that
     * keep the entity's UUID, which {@code Player.respawnEntityOnShoulder} restores via addWithUUID.
     */
    private static boolean isOnShoulder(ServerPlayer player, UUID uuid) {
        return Stream.of(player.getShoulderEntityLeft(), player.getShoulderEntityRight())
                .anyMatch(tag -> tag.hasUUID(Entity.UUID_TAG) && tag.getUUID(Entity.UUID_TAG).equals(uuid));
    }

    private static boolean mayBePerched(ServerPlayer player, IPlayerFamiliar familiar) {
        boolean parrot = familiar.getStored().map(stored -> stored.type() == EntityType.PARROT).orElse(false);
        return parrot && (!player.getShoulderEntityLeft().isEmpty() || !player.getShoulderEntityRight().isEmpty());
    }

    /**
     * The player's in-play familiar, like {@link #findActive}; a parrot familiar that may be sitting on the
     * player's shoulder is put back into the world first so it can be found.
     */
    private static Optional<Mob> findActiveReleasingShoulder(ServerPlayer player, IPlayerFamiliar familiar) {
        Optional<Mob> active = findActive(player);
        if (active.isEmpty() && mayBePerched(player, familiar)) {
            player.removeEntitiesOnShoulder();
            active = findActive(player);
        }
        return active;
    }

    /** Removes the mob's familiar binding, including from any Polymorph swap-back queued on it. */
    private static void unbind(Mob mob) {
        ServicesPMX.CAPABILITIES.clearBinding(mob);
        rewriteQueuedSwapperBindings(mob, Optional.empty());
    }

    /**
     * Makes every Primal Magick entity swapper queued on the mob carry the given familiar binding, or none.
     * A Polymorph spell saves its target's full data, attachments included, as the swapper's original data,
     * and each swap merges that data over the entity it replaces; without this, a swap-back would restore
     * whatever binding the mob had when it was polymorphed, which is stale once the binding has changed.
     * Each affected swapper is rebuilt through its NBT form, since its original data is not exposed.
     * Must be called whenever the manager changes a mob's binding.
     *
     * @param mob the mob whose binding just changed
     * @param binding the mob's new binding, or empty if it is no longer a familiar
     */
    public static void rewriteQueuedSwapperBindings(Mob mob, Optional<FamiliarBinding> binding) {
        IEntitySwappers swappers = Services.CAPABILITIES.swappers(mob).orElse(null);
        if (swappers == null || swappers.getQueue().isEmpty()) {
            return;
        }
        HolderLookup.Provider registries = mob.registryAccess();
        Optional<Tag> encoded = binding.map(b -> FamiliarBinding.MAP_CODEC.codec()
                .encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), b).getOrThrow());
        Queue<EntitySwapper> rewritten = new LinkedBlockingQueue<>();
        boolean changed = false;
        for (EntitySwapper swapper : swappers.getQueue()) {
            CompoundTag swapperTag = swapper.serializeNBT(registries);
            // Swappers share their original data with the swap-backs they queue; never edit it in place
            CompoundTag data = swapperTag.contains(SWAPPER_ORIGINAL_DATA_KEY, Tag.TAG_COMPOUND) ? swapperTag.getCompound(SWAPPER_ORIGINAL_DATA_KEY).copy() : null;
            if (data == null || !setBinding(data, encoded)) {
                rewritten.offer(swapper);
                continue;
            }
            swapperTag.put(SWAPPER_ORIGINAL_DATA_KEY, data);
            rewritten.offer(new EntitySwapper(registries, swapperTag));
            changed = true;
        }
        if (changed) {
            swappers.setQueue(rewritten);
        }
    }

    /**
     * Writes the encoded binding into an entity save's attachments, or removes it there if empty.
     *
     * @return true if the save was changed
     */
    private static boolean setBinding(CompoundTag entityData, Optional<Tag> encodedBinding) {
        // Empty and detached from the save if it has no attachments yet
        CompoundTag attachments = entityData.getCompound(ATTACHMENTS_KEY);
        if (encodedBinding.isPresent()) {
            attachments.put(BINDING_ATTACHMENT_KEY, encodedBinding.get().copy());
            entityData.put(ATTACHMENTS_KEY, attachments);
            return true;
        }
        if (!attachments.contains(BINDING_ATTACHMENT_KEY)) {
            return false;
        }
        attachments.remove(BINDING_ATTACHMENT_KEY);
        if (attachments.isEmpty()) {
            entityData.remove(ATTACHMENTS_KEY);
        }
        return true;
    }

    /**
     * Builds the stored familiar at the player's feet (or the nearest free spot within two blocks) under a
     * fresh UUID, restored to full health without harmful effects, freezing or drowning. The mob is not
     * added to the level; see {@link #addFamiliar}.
     *
     * @return the prepared mob, or empty if the snapshot cannot produce a mob
     */
    private static Optional<Mob> loadFamiliar(ServerPlayer player, StoredFamiliar stored) {
        ServerLevel level = player.serverLevel();
        // The snapshot was saved without its id, which loading needs to pick the entity type
        CompoundTag entityData = stored.entityData().copy();
        entityData.putString(Entity.ID_TAG, EntityType.getKey(stored.type()).toString());
        Entity entity = EntityType.loadEntityRecursive(entityData, level, loaded -> {
            // The snapshot carries the old UUID; a fresh one keeps the level from rejecting a duplicate
            loaded.setUUID(UUID.randomUUID());
            return loaded;
        });
        if (!(entity instanceof Mob mob)) {
            LOGGER.error("Failed to recreate familiar of type {} for {}", stored.type(), player.getName().getString());
            return Optional.empty();
        }
        float yaw = player.getYRot();
        placeNear(level, mob, player.position(), yaw);
        mob.setYHeadRot(yaw);
        mob.setYBodyRot(yaw);
        mob.setDeltaMovement(Vec3.ZERO);
        mob.fallDistance = 0.0F;
        mob.clearFire();
        mob.setTicksFrozen(0);
        mob.setAirSupply(mob.getMaxAirSupply());
        // PM's POLYMORPH marker effect is HARMFUL and is removed too; that is harmless, since the swap-back
        // is driven by PM's serialized entity_swappers attachment, not by the effect
        List<Holder<MobEffect>> harmful = mob.getActiveEffects().stream()
                .map(MobEffectInstance::getEffect)
                .filter(effect -> effect.value().getCategory() == MobEffectCategory.HARMFUL)
                .toList();
        harmful.forEach(mob::removeEffect);
        mob.setHealth(mob.getMaxHealth());
        mob.setPersistenceRequired();
        return Optional.of(mob);
    }

    /**
     * Runs the given preparation on a mob from {@link #loadFamiliar}, adds it to the level and plays the
     * poof effect.
     *
     * @return true if the mob is now in the level; false if the level refused it, for example because
     *         another mod vetoed the join
     */
    private static boolean addFamiliar(ServerLevel level, Mob mob, Consumer<Mob> beforeAdd) {
        beforeAdd.accept(mob);
        if (!addFilter.test(mob) || !level.addFreshEntity(mob)) {
            LOGGER.error("Level refused familiar entity {} of type {}", mob.getUUID(), EntityType.getKey(mob.getType()));
            return false;
        }
        FamiliarFx.poof(level, mob);
        return true;
    }

    /**
     * Test seam: replaces the check consulted before a familiar is added to the level, so tests can make
     * the level refuse it as a vetoed join would. Production code never calls this; the default always
     * accepts.
     *
     * @param filter the new check, or null to restore the default
     */
    public static void setAddFilterForTesting(@Nullable Predicate<Mob> filter) {
        addFilter = filter == null ? mob -> true : filter;
    }

    /**
     * Moves the mob to the first collision-free spot among the given position and the eight horizontal
     * offsets of one and then two blocks around it, falling back to the position itself.
     */
    private static void placeNear(ServerLevel level, Mob mob, Vec3 pos, float yaw) {
        for (int radius = 0; radius <= 2; radius++) {
            for (int dx = -radius; dx <= radius; dx += radius == 0 ? 1 : radius) {
                for (int dz = -radius; dz <= radius; dz += radius == 0 ? 1 : radius) {
                    if (radius > 0 && dx == 0 && dz == 0) {
                        continue;
                    }
                    mob.absMoveTo(pos.x + dx, pos.y, pos.z + dz, yaw, 0.0F);
                    if (level.noCollision(mob)) {
                        return;
                    }
                }
            }
        }
        mob.absMoveTo(pos.x, pos.y, pos.z, yaw, 0.0F);
    }
}
