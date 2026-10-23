package com.claviger.primalmagickextended.common.tags;

import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

/**
 * Entity type tags defined by the mod.
 */
public class EntityTypeTagsPMX {
    /** Pets which can be bound as familiars when tamed by the player. */
    public static final TagKey<EntityType<?>> FAMILIAR_CANDIDATES = tag("familiar_candidates");

    private static TagKey<EntityType<?>> tag(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceUtilsPMX.loc(name));
    }
}
