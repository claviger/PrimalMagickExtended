package com.claviger.primalmagickextended.datagen.tags;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.tags.EntityTypeTagsPMX;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * Data provider for the mod's entity type tags.
 */
public class EntityTypeTagsProviderPMXNeoforge extends EntityTypeTagsProvider {
    public EntityTypeTagsProviderPMXNeoforge(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, ConstantsPMX.MOD_ID, existingFileHelper);
    }

    @Override
    @NotNull
    public String getName() {
        return ConstantsPMX.MOD_NAME + " Entity Type Tags";
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider lookupProvider) {
        // Minecraft 1.21.1 has no nautiluses, which later versions also list here
        this.tag(EntityTypeTagsPMX.FAMILIAR_CANDIDATES).add(EntityType.WOLF, EntityType.CAT, EntityType.PARROT, EntityType.HORSE, EntityType.DONKEY,
                EntityType.MULE, EntityType.LLAMA, EntityType.TRADER_LLAMA, EntityType.FOX);
    }
}
