package com.claviger.primalmagickextended.common.capabilities;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

/**
 * NeoForge attachment form of the player familiar data. Persistence uses the common codec; the sync
 * codec sends only the two booleans the client needs.
 */
public class PlayerFamiliarNeoforge extends PlayerFamiliar implements ValueIOSerializable {
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerFamiliarNeoforge> SYNC_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PlayerFamiliarNeoforge::hasFamiliar,
            ByteBufCodecs.BOOL, PlayerFamiliarNeoforge::isFamiliarNearby,
            PlayerFamiliarNeoforge::fromClientView);

    private static PlayerFamiliarNeoforge fromClientView(boolean hasFamiliar, boolean nearby) {
        PlayerFamiliarNeoforge data = new PlayerFamiliarNeoforge();
        data.applyClientView(hasFamiliar, nearby);
        return data;
    }
}
