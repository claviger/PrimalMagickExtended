package com.claviger.primalmagickextended.common.familiars;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;
import java.util.UUID;

/**
 * A player's bound familiar as stored with the player.
 *
 * @param type the familiar's entity type
 * @param entityData the last snapshot of the familiar, as saved by {@code Entity.saveWithoutId}
 * @param generation identifier regenerated at every bind and summon; in-world entities whose binding
 *                   carries a different generation are stale copies
 * @param activeEntityId the UUID of the in-world instance while the familiar is in play, or empty while dismissed
 */
public record StoredFamiliar(EntityType<?> type, CompoundTag entityData, UUID generation, Optional<UUID> activeEntityId) {
    public static final Codec<StoredFamiliar> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(StoredFamiliar::type),
            CompoundTag.CODEC.fieldOf("entityData").forGetter(StoredFamiliar::entityData),
            UUIDUtil.CODEC.fieldOf("generation").forGetter(StoredFamiliar::generation),
            UUIDUtil.CODEC.optionalFieldOf("activeEntityId").forGetter(StoredFamiliar::activeEntityId)
        ).apply(instance, StoredFamiliar::new));

    public StoredFamiliar withGeneration(UUID newGeneration) {
        return new StoredFamiliar(this.type, this.entityData, newGeneration, this.activeEntityId);
    }

    public StoredFamiliar withActiveEntityId(Optional<UUID> newActiveEntityId) {
        return new StoredFamiliar(this.type, this.entityData, this.generation, newActiveEntityId);
    }
}
