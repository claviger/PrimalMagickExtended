package com.claviger.primalmagickextended.common.entities;

import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;

/**
 * Cat variants added by the mod. The ocelot variant reuses vanilla's ocelot textures (cats and
 * ocelots share a model) and never spawns naturally; it exists for the collar's Easter egg.
 */
public class CatVariantsPMX {
    public static final ResourceKey<CatVariant> OCELOT = ResourceKey.create(Registries.CAT_VARIANT, ResourceUtilsPMX.loc("ocelot"));

    public static void bootstrap(BootstrapContext<CatVariant> context) {
        context.register(OCELOT, new CatVariant(
                new ClientAsset.ResourceTexture(Identifier.withDefaultNamespace("entity/cat/ocelot")),
                new ClientAsset.ResourceTexture(Identifier.withDefaultNamespace("entity/cat/ocelot_baby")),
                SpawnPrioritySelectors.EMPTY));
    }
}
