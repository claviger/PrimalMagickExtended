package com.claviger.primalmagickextended.common.network;

import com.claviger.primalmagickextended.common.network.packets.portals.DrawSpritePacket;
import com.claviger.primalmagickextended.common.network.packets.portals.ScreenTransitionPacket;
import com.claviger.primalmagickextended.common.network.packets.portals.SyncPortalsPacket;
import commonnetwork.api.Network;

/**
 * Registration of the mod's play channel packets with Common Networking, as in Primal Magick.
 */
public class PlayPacketRegistrationPMX {
    public static void registerMessages() {
        Network
                // Client-bound play channel packets
                .registerPacket(SyncPortalsPacket.type(), SyncPortalsPacket.class, SyncPortalsPacket.STREAM_CODEC, SyncPortalsPacket::onMessage)
                .registerPacket(DrawSpritePacket.type(), DrawSpritePacket.class, DrawSpritePacket.STREAM_CODEC, DrawSpritePacket::onMessage)

                // Server-bound play channel packets
                .registerPacket(ScreenTransitionPacket.type(), ScreenTransitionPacket.class, ScreenTransitionPacket.STREAM_CODEC, ScreenTransitionPacket::onMessage);
    }
}
