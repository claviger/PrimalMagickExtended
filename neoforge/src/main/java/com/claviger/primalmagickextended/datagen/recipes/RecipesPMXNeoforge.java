package com.claviger.primalmagickextended.datagen.recipes;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.conditions.FeatureEnabledCondition;
import com.claviger.primalmagickextended.common.features.FeaturePMX;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * NeoForge recipe data provider for the mod. Writes each feature's recipes, and their unlock advancements, with
 * that feature's load condition, so they do not load while the feature is turned off.
 */
public class RecipesPMXNeoforge extends RecipesPMX {
    public RecipesPMXNeoforge(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected RecipeOutput featureOutput(FeaturePMX feature) {
        return this.output.withConditions(new FeatureEnabledCondition(feature));
    }

    /**
     * Datagen runner for the NeoForge recipe provider.
     */
    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        @NotNull
        protected RecipeProvider createRecipeProvider(@NotNull HolderLookup.Provider provider, @NotNull RecipeOutput recipeOutput) {
            return new RecipesPMXNeoforge(provider, recipeOutput);
        }

        @Override
        @NotNull
        public String getName() {
            return ConstantsPMX.MOD_NAME + " Recipes (NeoForge)";
        }
    }
}
