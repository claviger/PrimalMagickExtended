package com.claviger.primalmagickextended.common.entities;

import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.verdantartifice.primalmagick.common.registries.IRegistryItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.CatVariant;

/**
 * Deferred registry for the cat variants added by the mod. The ocelot variant reuses vanilla's ocelot texture (cats
 * and ocelots share a model) and never spawns naturally, since it is in none of vanilla's cat spawn tags; it exists
 * for the collar's Easter egg.
 */
public class CatVariantsPMX {
    public static void init() {
        // Pass the service initialization through this class so it gets class loaded and fields registered
        ServicesPMX.CAT_VARIANTS_REGISTRY.init();
    }

    public static final IRegistryItem<CatVariant, CatVariant> OCELOT = ServicesPMX.CAT_VARIANTS_REGISTRY.register("ocelot",
            () -> new CatVariant(ResourceLocation.withDefaultNamespace("textures/entity/cat/ocelot.png")));
}
