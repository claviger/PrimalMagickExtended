package com.claviger.primalmagickextended.common.network.packets.portals;

import com.claviger.primalmagickextended.common.portals.CustomPortal;
import com.claviger.primalmagickextended.common.portals.PortalData;
import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.verdantartifice.primalmagick.common.network.packets.IMessageToClient;
import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Packet sent from the server to replace a client's copy of the portal registry with the full server registry.
 * Ported from Custom Portals.
 */
public class SyncPortalsPacket implements IMessageToClient {
    public static final ResourceLocation CHANNEL = ResourceUtilsPMX.loc("sync_portals");
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPortalsPacket> STREAM_CODEC = StreamCodec.ofMember(SyncPortalsPacket::encode, SyncPortalsPacket::decode);

    protected final List<CustomPortal> portals;

    public SyncPortalsPacket(List<CustomPortal> portals) {
        this.portals = portals;
    }

    public List<CustomPortal> getPortals() {
        return this.portals;
    }

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(CHANNEL);
    }

    public static void encode(SyncPortalsPacket message, RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(message.portals.size());
        for (CustomPortal portal : message.portals) {
            CustomPortal.STREAM_CODEC.encode(buf, portal);
        }
    }

    public static SyncPortalsPacket decode(RegistryFriendlyByteBuf buf) {
        int count = CustomPortal.readBoundedCount(buf, CustomPortal.MIN_STREAM_BYTES);
        List<CustomPortal> portals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            portals.add(CustomPortal.STREAM_CODEC.decode(buf));
        }
        return new SyncPortalsPacket(portals);
    }

    public static void onMessage(PacketContext<SyncPortalsPacket> ctx) {
        // Common Networking registers packets in both directions; only a client may act on this one
        if (!Side.CLIENT.equals(ctx.side())) {
            return;
        }
        PortalData.applySync(ctx.message().portals);
    }
}
