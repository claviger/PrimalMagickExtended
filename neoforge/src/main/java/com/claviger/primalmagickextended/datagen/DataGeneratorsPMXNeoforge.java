package com.claviger.primalmagickextended.datagen;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.datagen.affinities.AffinityProviderPMX;
import com.claviger.primalmagickextended.datagen.blocks.BlockStateProviderPMXNeoforge;
import com.claviger.primalmagickextended.datagen.items.ItemModelProviderPMXNeoforge;
import com.claviger.primalmagickextended.datagen.lang.LanguageProviderEnUsPMX;
import com.claviger.primalmagickextended.datagen.loot_tables.BlockLootTablesPMX;
import com.claviger.primalmagickextended.datagen.particles.ParticleDescriptionProviderPMXNeoforge;
import com.claviger.primalmagickextended.datagen.recipes.RecipesPMXNeoforge;
import com.claviger.primalmagickextended.datagen.tags.EntityTypeTagsProviderPMXNeoforge;
import com.claviger.primalmagickextended.datagen.tags.ResearchEntryTagsProviderPMXNeoforge;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Handlers for events related to data file generation.
 */
@EventBusSubscriber(modid = ConstantsPMX.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGeneratorsPMXNeoforge {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        // Add all of the mod's data providers to the generator for processing
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> registryLookupFuture = RegistryDataGeneratorPMXNeoforge.addProviders(event.includeServer(), generator, output, event.getLookupProvider());

        // Shared by all features
        generator.addProvider(event.includeClient(), new BlockStateProviderPMXNeoforge(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ItemModelProviderPMXNeoforge(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new LanguageProviderEnUsPMX(output));
        generator.addProvider(event.includeServer(), new RecipesPMXNeoforge(output, registryLookupFuture));

        // Familiars
        generator.addProvider(event.includeServer(), new AffinityProviderPMX(output, registryLookupFuture));
        generator.addProvider(event.includeServer(), new EntityTypeTagsProviderPMXNeoforge(output, registryLookupFuture, existingFileHelper));

        // Portals
        generator.addProvider(event.includeClient(), new ParticleDescriptionProviderPMXNeoforge(output, existingFileHelper));
        generator.addProvider(event.includeServer(), new ResearchEntryTagsProviderPMXNeoforge(output, registryLookupFuture, existingFileHelper));
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Collections.emptySet(), List.of(
                BlockLootTablesPMX.getSubProviderEntry()), registryLookupFuture));
    }
}
