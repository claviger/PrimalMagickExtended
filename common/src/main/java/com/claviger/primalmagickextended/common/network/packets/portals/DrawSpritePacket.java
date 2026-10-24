package com.claviger.primalmagickextended.common.network.packets.portals;

import com.claviger.primalmagickextended.client.portals.PortalClientState;
import com.claviger.primalmagickextended.common.portals.PortalHelper;
import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.verdantartifice.primalmagick.common.network.packets.IMessageToClient;
import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Packet sent from the server to tell a client which portal colour, as a map colour id, to use for the loading
 * screen background during portal travel. An id of zero clears the background. Ported from Custom Portals.
 */
public class DrawSpritePacket implements IMessageToClient {
    public static final ResourceLocation CHANNEL = ResourceUtilsPMX.loc("draw_sprite");
    public static final StreamCodec<RegistryFriendlyByteBuf, DrawSpritePacket> STREAM_CODEC = StreamCodec.ofMember(DrawSpritePacket::encode, DrawSpritePacket::decode);

    protected final int colorId;

    public DrawSpritePacket(int colorId) {
        this.colorId = colorId;
    }

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(CHANNEL);
    }

    public static void encode(DrawSpritePacket message, RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(message.colorId);
    }

    public static DrawSpritePacket decode(RegistryFriendlyByteBuf buf) {
        return new DrawSpritePacket(buf.readVarInt());
    }

    public static void onMessage(PacketContext<DrawSpritePacket> ctx) {
        // Common Networking registers packets in both directions; only a client may act on this one
        if (!Side.CLIENT.equals(ctx.side())) {
            return;
        }
        int colorId = ctx.message().colorId;
        PortalClientState.transitionBackgroundSpriteModel = colorId == 0 ? null : PortalHelper.getPortalBlockFromColorId(colorId);
    }
}
