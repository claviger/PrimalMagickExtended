package com.claviger.primalmagickextended.common.capabilities;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.familiars.StoredFamiliar;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

import java.util.Optional;

/**
 * Default implementation of the per-player familiar data.
 */
public class PlayerFamiliar implements IPlayerFamiliar {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String SERIALIZATION_KEY = ConstantsPMX.MOD_ID + "familiar";
    public static final Codec<PlayerFamiliar> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StoredFamiliar.CODEC.optionalFieldOf("stored").forGetter(PlayerFamiliar::getStored)
        ).apply(instance, PlayerFamiliar::new));

    private Optional<StoredFamiliar> stored;
    private boolean clientHasFamiliar = false;
    private boolean familiarNearby = false;
    private long lastToggleGameTime = -1L;

    public PlayerFamiliar() {
        this(Optional.empty());
    }

    public PlayerFamiliar(Optional<StoredFamiliar> stored) {
        this.stored = stored;
    }

    @Override
    public Optional<StoredFamiliar> getStored() {
        return this.stored;
    }

    @Override
    public void setStored(Optional<StoredFamiliar> stored) {
        this.stored = stored;
    }

    @Override
    public boolean hasFamiliar() {
        // The stored familiar only exists on the server; clientHasFamiliar is only ever set on the
        // client, through applyClientView
        return this.stored.isPresent() || this.clientHasFamiliar;
    }

    @Override
    public boolean isFamiliarNearby() {
        return this.familiarNearby;
    }

    @Override
    public void setFamiliarNearby(boolean nearby) {
        this.familiarNearby = nearby;
    }

    @Override
    public long getLastToggleGameTime() {
        return this.lastToggleGameTime;
    }

    @Override
    public void setLastToggleGameTime(long gameTime) {
        this.lastToggleGameTime = gameTime;
    }

    /** Applies the view synced from the server; client side only. */
    public void applyClientView(boolean hasFamiliar, boolean nearby) {
        this.clientHasFamiliar = hasFamiliar;
        this.familiarNearby = nearby;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), this)
                .resultOrPartial(error -> LOGGER.error("Could not encode the player familiar data: {}", error))
                .ifPresent(encoded -> tag.put(SERIALIZATION_KEY, encoded));
        return tag;
    }

    /**
     * Reads the stored familiar. Data that is present but cannot be decoded (for example a familiar whose
     * entity type no longer exists) is logged and leaves the player without a familiar.
     */
    @Override
    public void deserializeNBT(HolderLookup.Provider registries, CompoundTag tag) {
        if (!tag.contains(SERIALIZATION_KEY)) {
            return;
        }
        Tag encoded = tag.get(SERIALIZATION_KEY);
        Optional<PlayerFamiliar> read = CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), encoded).result();
        if (read.isPresent()) {
            this.stored = read.get().stored;
        } else {
            LOGGER.warn("Could not decode the player familiar data under key {}; the stored familiar is lost", SERIALIZATION_KEY);
        }
    }
}
