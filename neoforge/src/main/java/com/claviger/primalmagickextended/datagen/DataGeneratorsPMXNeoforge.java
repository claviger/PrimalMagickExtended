package com.claviger.primalmagickextended.datagen;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.datagen.affinities.AffinityProviderPMX;
import com.claviger.primalmagickextended.datagen.lang.LanguageProviderEnUsPMX;
import com.claviger.primalmagickextended.datagen.models.ModelProviderPMXNeoforge;
import com.claviger.primalmagickextended.datagen.recipes.RecipesPMX;
import com.claviger.primalmagickextended.datagen.tags.EntityTypeTagsProviderPMXNeoforge;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

/**
 * Handlers for events related to data file generation.
 */
@EventBusSubscriber(modid = ConstantsPMX.MOD_ID)
public class DataGeneratorsPMXNeoforge {
    @SubscribeEvent
    public static void onGatherClientData(GatherDataEvent.Client event) {
        // A client data run generates both client and server data
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> registryLookupFuture = addRegistryProviders(event, generator);
        addClientProviders(event, generator, registryLookupFuture);
        addServerProviders(event, generator, registryLookupFuture);
    }

    @SubscribeEvent
    public static void onGatherServerData(GatherDataEvent.Server event) {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> registryLookupFuture = addRegistryProviders(event, generator);
        addServerProviders(event, generator, registryLookupFuture);
    }

    private static CompletableFuture<HolderLookup.Provider> addRegistryProviders(GatherDataEvent event, DataGenerator generator) {
        return RegistryDataGeneratorPMXNeoforge.addProviders(true, generator, generator.getPackOutput(), event.getLookupProvider());
    }

    private static void addClientProviders(GatherDataEvent event, DataGenerator generator, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        event.addProvider(new ModelProviderPMXNeoforge(generator.getPackOutput()));
        event.addProvider(new LanguageProviderEnUsPMX(generator.getPackOutput()));
    }

    private static void addServerProviders(GatherDataEvent event, DataGenerator generator, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        event.addProvider(new RecipesPMX.Runner(generator.getPackOutput(), registryLookupFuture));
        event.addProvider(new AffinityProviderPMX(generator.getPackOutput(), registryLookupFuture));
        event.addProvider(new EntityTypeTagsProviderPMXNeoforge(generator.getPackOutput(), registryLookupFuture));
    }
}
