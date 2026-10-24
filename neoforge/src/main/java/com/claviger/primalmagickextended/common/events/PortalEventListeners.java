package com.claviger.primalmagickextended.common.events;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.portals.PortalData;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * NeoForge listeners for portal-related events.
 */
@EventBusSubscriber(modid = ConstantsPMX.MOD_ID)
public class PortalEventListeners {
    /** New players need the full registry before they can see portal overlays or predict portal behaviour. */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PortalData.syncTo(player);
        }
    }
}
