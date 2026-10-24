package com.claviger.primalmagickextended.datagen.recipes;

import com.claviger.primalmagickextended.common.conditions.FeatureEnabledCondition;
import com.claviger.primalmagickextended.common.features.FeaturePMX;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;

import java.util.concurrent.CompletableFuture;

/**
 * NeoForge recipe data provider for the mod. Writes each feature's recipes, and their unlock advancements, with
 * that feature's load condition, so they do not load while the feature is turned off.
 */
public class RecipesPMXNeoforge extends RecipesPMX {
    public RecipesPMXNeoforge(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected RecipeOutput featureOutput(RecipeOutput output, FeaturePMX feature) {
        return output.withConditions(new FeatureEnabledCondition(feature));
    }
}
