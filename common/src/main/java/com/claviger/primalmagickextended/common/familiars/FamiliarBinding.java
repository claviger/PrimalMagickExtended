package com.claviger.primalmagickextended.common.familiars;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.Util;

import java.util.UUID;

/**
 * Marker attached to an in-world familiar entity.
 *
 * @param ownerId the UUID of the player the familiar is bound to
 * @param generation the generation of the owner's stored familiar this entity belongs to
 */
public record FamiliarBinding(UUID ownerId, UUID generation) {
    /** Sentinel used as the attachment default; never persisted. */
    public static final FamiliarBinding NONE = new FamiliarBinding(Util.NIL_UUID, Util.NIL_UUID);

    public static final MapCodec<FamiliarBinding> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("ownerId").forGetter(FamiliarBinding::ownerId),
            UUIDUtil.CODEC.fieldOf("generation").forGetter(FamiliarBinding::generation)
        ).apply(instance, FamiliarBinding::new));

    public boolean isPresent() {
        return !Util.NIL_UUID.equals(this.ownerId);
    }

    public boolean isOwnedBy(UUID playerId) {
        return this.isPresent() && this.ownerId.equals(playerId);
    }
}
