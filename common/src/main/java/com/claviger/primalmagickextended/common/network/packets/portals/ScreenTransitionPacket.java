package com.claviger.primalmagickextended.common.network.packets.portals;

import com.claviger.primalmagickextended.common.portals.IPortalTraveler;
import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.verdantartifice.primalmagick.common.network.packets.IMessageToServer;
import commonnetwork.networking.data.PacketContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Packet sent from a client to tell the server whether the loading screen with the portal background is showing.
 * Ported from Custom Portals.
 */
public class ScreenTransitionPacket implements IMessageToServer {
    public static final ResourceLocation CHANNEL = ResourceUtilsPMX.loc("is_in_transition");
    public static final StreamCodec<RegistryFriendlyByteBuf, ScreenTransitionPacket> STREAM_CODEC = StreamCodec.ofMember(ScreenTransitionPacket::encode, ScreenTransitionPacket::decode);

    protected final boolean isTransitioning;

    public ScreenTransitionPacket(boolean isTransitioning) {
        this.isTransitioning = isTransitioning;
    }

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(CHANNEL);
    }

    public static void encode(ScreenTransitionPacket message, RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(message.isTransitioning);
    }

    public static ScreenTransitionPacket decode(RegistryFriendlyByteBuf buf) {
        return new ScreenTransitionPacket(buf.readBoolean());
    }

    public static void onMessage(PacketContext<ScreenTransitionPacket> ctx) {
        ServerPlayer player = ctx.sender();
        if (player != null) {
            ((IPortalTraveler) player).primalmagickextended$setInTransition(ctx.message().isTransitioning);
        }
    }
}
