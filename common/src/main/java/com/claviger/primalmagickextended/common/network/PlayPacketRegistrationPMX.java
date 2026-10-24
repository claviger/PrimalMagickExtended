package com.claviger.primalmagickextended.common.network;

import com.claviger.primalmagickextended.common.network.packets.familiars.SyncFamiliarPacket;
import com.claviger.primalmagickextended.common.network.packets.portals.DrawSpritePacket;
import com.claviger.primalmagickextended.common.network.packets.portals.ScreenTransitionPacket;
import com.claviger.primalmagickextended.common.network.packets.portals.SyncPortalsPacket;
import commonnetwork.api.Network;

/**
 * Registration of the mod's play channel packets with Common Networking, as in Primal Magick.
 */
public class PlayPacketRegistrationPMX {
    public static void registerMessages() {
        // Familiars
        Network.registerPacket(SyncFamiliarPacket.type(), SyncFamiliarPacket.class, SyncFamiliarPacket.STREAM_CODEC, SyncFamiliarPacket::onMessage);

        // Portals
        // Client-bound play channel packets
        Network.registerPacket(SyncPortalsPacket.type(), SyncPortalsPacket.class, SyncPortalsPacket.STREAM_CODEC, SyncPortalsPacket::onMessage);
        Network.registerPacket(DrawSpritePacket.type(), DrawSpritePacket.class, DrawSpritePacket.STREAM_CODEC, DrawSpritePacket::onMessage);

        // Server-bound play channel packets
        Network.registerPacket(ScreenTransitionPacket.type(), ScreenTransitionPacket.class, ScreenTransitionPacket.STREAM_CODEC, ScreenTransitionPacket::onMessage);
    }
}
