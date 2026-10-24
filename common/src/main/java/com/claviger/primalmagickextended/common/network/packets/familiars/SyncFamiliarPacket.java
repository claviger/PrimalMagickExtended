package com.claviger.primalmagickextended.common.network.packets.familiars;

import com.claviger.primalmagickextended.common.capabilities.PlayerFamiliar;
import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.verdantartifice.primalmagick.client.util.ClientUtils;
import com.verdantartifice.primalmagick.common.network.packets.IMessageToClient;
import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Packet sent from the server to a player's client with the client's view of that player's familiar data: whether
 * they have a familiar and whether it is nearby. Only the owning player receives it, so a client only ever holds
 * the local player's data.
 */
public class SyncFamiliarPacket implements IMessageToClient {
    public static final ResourceLocation CHANNEL = ResourceUtilsPMX.loc("sync_familiar");
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncFamiliarPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SyncFamiliarPacket::hasFamiliar,
            ByteBufCodecs.BOOL, SyncFamiliarPacket::isFamiliarNearby,
            SyncFamiliarPacket::new);

    protected final boolean hasFamiliar;
    protected final boolean familiarNearby;

    public SyncFamiliarPacket(boolean hasFamiliar, boolean familiarNearby) {
        this.hasFamiliar = hasFamiliar;
        this.familiarNearby = familiarNearby;
    }

    public boolean hasFamiliar() {
        return this.hasFamiliar;
    }

    public boolean isFamiliarNearby() {
        return this.familiarNearby;
    }

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(CHANNEL);
    }

    public static void onMessage(PacketContext<SyncFamiliarPacket> ctx) {
        // Common Networking registers packets in both directions; only a client may act on this one
        Player player = Side.CLIENT.equals(ctx.side()) ? ClientUtils.getCurrentPlayer() : null;
        if (player != null) {
            ServicesPMX.CAPABILITIES.familiar(player).ifPresent(familiar -> {
                if (familiar instanceof PlayerFamiliar playerFamiliar) {
                    playerFamiliar.applyClientView(ctx.message().hasFamiliar(), ctx.message().isFamiliarNearby());
                }
            });
        }
    }
}
