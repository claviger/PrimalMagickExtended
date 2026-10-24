package com.claviger.primalmagickextended.common.portals;

import com.claviger.primalmagickextended.common.network.packets.portals.SyncPortalsPacket;
import com.verdantartifice.primalmagick.common.network.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Single access point for the portal registry on either side: the saved registry on the server, the last synced
 * copy on the client. Ported from Custom Portals.
 */
public final class PortalData {
    private PortalData() {}

    /** Server: the saved registry. Client: the last synced copy. */
    public static PortalRegistry get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return PortalSavedData.get(serverLevel.getServer()).getRegistry();
        }
        return ClientPortalData.registry();
    }

    @Nullable
    public static CustomPortal getPortalFromPos(Level level, BlockPos pos) {
        return get(level).getPortalFromPos(level.dimension().location().toString(), pos);
    }

    /**
     * Call after mutating the registry on the server: persists it and pushes it to every client. No-op on the
     * client.
     */
    public static void markChanged(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            MinecraftServer server = serverLevel.getServer();
            PortalSavedData data = PortalSavedData.get(server);
            data.setDirty();
            SyncPortalsPacket packet = snapshotPacket(data.getRegistry());
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                PacketHandler.sendToPlayer(packet, player);
            }
        }
    }

    /** Sends the full registry to one player, on login. */
    public static void syncTo(ServerPlayer player) {
        PortalSavedData data = PortalSavedData.get(player.level().getServer());
        PacketHandler.sendToPlayer(snapshotPacket(data.getRegistry()), player);
    }

    /** Snapshots every portal so the packet never shares mutable state with the server registry. */
    private static SyncPortalsPacket snapshotPacket(PortalRegistry registry) {
        return new SyncPortalsPacket(registry.getPortals().stream().map(CustomPortal::snapshot).toList());
    }

    /** Client: replaces the synced registry with the packet contents, relinking them in order. */
    public static void applySync(List<CustomPortal> portals) {
        PortalRegistry registry = new PortalRegistry();
        for (CustomPortal portal : portals) {
            registry.register(portal);
        }
        ClientPortalData.set(registry);
    }
}
