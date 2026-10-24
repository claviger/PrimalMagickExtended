package com.claviger.primalmagickextended.common.events;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * NeoForge listeners for familiar-related events. The damage, death and join listeners run at the highest
 * priority so other listeners never observe a hit, death or join that is cancelled here. The login, respawn and
 * dimension change listeners resend the player's familiar data, since the client then has a new player entity
 * whose attached data starts empty.
 */
@EventBusSubscriber(modid = ConstantsPMX.MOD_ID)
public class FamiliarEventListeners {
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (FamiliarEvents.onEntityInteract(event.getEntity(), event.getHand(), event.getTarget())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (FamiliarEvents.onIncomingDamage(event.getEntity(), event.getSource(), event.getAmount())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDeath(LivingDeathEvent event) {
        if (FamiliarEvents.onDeath(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (FamiliarEvents.onEntityJoin(event.getEntity(), event.getLevel())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServicesPMX.CAPABILITIES.syncFamiliar(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServicesPMX.CAPABILITIES.syncFamiliar(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServicesPMX.CAPABILITIES.syncFamiliar(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            FamiliarEvents.onPlayerLogout(player);
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        FamiliarEvents.onServerTick(event.getServer());
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player) && !entity.level().isClientSide() && !entity.isPassenger()) {
            FamiliarEvents.onEntityTick(entity);
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        FamiliarEvents.onEntityLeaveLevel(event.getEntity(), event.getLevel());
    }
}
