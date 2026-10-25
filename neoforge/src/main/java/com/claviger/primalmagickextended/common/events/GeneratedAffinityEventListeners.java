package com.claviger.primalmagickextended.common.events;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.affinities.GeneratedAffinities;
import com.claviger.primalmagickextended.common.features.FeaturePMX;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

/**
 * NeoForge listeners that generate affinities whenever the server's data loads: once the server has started, and
 * after each {@code /reload}. Primal Magick 6.0.7 sends affinity data to each player as they join, so players who join
 * after a run get its affinities; as with Primal Magick's own affinities, players already online keep theirs until
 * they rejoin.
 */
@EventBusSubscriber(modid = ConstantsPMX.MOD_ID)
public class GeneratedAffinityEventListeners {
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        generate(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        // A player joining syncs the data already loaded; only a reload, which syncs every player, brings new data
        if (event.getPlayer() == null) {
            generate(event.getPlayerList().getServer());
        }
    }

    private static void generate(MinecraftServer server) {
        if (ServicesPMX.CONFIG.isFeatureEnabled(FeaturePMX.GENERATED_AFFINITIES)) {
            GeneratedAffinities.regenerate(server);
        }
    }
}
