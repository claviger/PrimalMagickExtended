package com.claviger.primalmagickextended.datagen;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.conditions.FeatureEnabledCondition;
import com.claviger.primalmagickextended.common.entities.CatVariantsPMX;
import com.claviger.primalmagickextended.common.research.ResearchEntriesPMX;
import com.claviger.primalmagickextended.test.TestInstancesPMX;
import com.verdantartifice.primalmagick.common.registries.RegistryKeysPM;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * Generates datapack registry JSON for the mod. Each research entry carries the load condition of the feature it
 * belongs to.
 */
public class RegistryDataGeneratorPMXNeoforge extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(RegistryKeysPM.RESEARCH_ENTRIES, ResearchEntriesPMX::bootstrap)
            .add(Registries.CAT_VARIANT, CatVariantsPMX::bootstrap)
            .add(Registries.TEST_INSTANCE, TestInstancesPMX::bootstrap);

    // Use addProviders() instead
    private RegistryDataGeneratorPMXNeoforge(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, RegistryDataGeneratorPMXNeoforge::addConditions, Set.of(ConstantsPMX.MOD_ID));
    }

    private static void addConditions(BiConsumer<ResourceKey<?>, ICondition> conditions) {
        ResearchEntriesPMX.FEATURES.forEach((key, feature) -> conditions.accept(key, new FeatureEnabledCondition(feature)));
    }

    public static CompletableFuture<HolderLookup.Provider> addProviders(boolean isServer, DataGenerator generator, PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        RegistryDataGeneratorPMXNeoforge registryDataGenerator = generator.addProvider(isServer, new RegistryDataGeneratorPMXNeoforge(output, provider));
        return registryDataGenerator.getRegistryProvider();
    }

    @Override
    @NotNull
    public String getName() {
        return "Primal Magick Extended Datapack Registries";
    }
}
