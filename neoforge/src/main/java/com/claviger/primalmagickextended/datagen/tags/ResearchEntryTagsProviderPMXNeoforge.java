package com.claviger.primalmagickextended.datagen.tags;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.research.ResearchEntriesPMX;
import com.verdantartifice.primalmagick.common.registries.RegistryKeysPM;
import com.verdantartifice.primalmagick.common.research.ResearchEntry;
import com.verdantartifice.primalmagick.common.tags.ResearchEntryTagsPM;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Data provider for the mod's research entry tags. Adds to Primal Magick's own tags without replacing them, so the
 * generated files merge with Primal Magick's entries.
 */
public class ResearchEntryTagsProviderPMXNeoforge extends KeyTagProvider<ResearchEntry> {
    public ResearchEntryTagsProviderPMXNeoforge(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, RegistryKeysPM.RESEARCH_ENTRIES, lookupProvider, ConstantsPMX.MOD_ID);
    }

    @Override
    @NotNull
    public String getName() {
        return ConstantsPMX.MOD_NAME + " Research Entry Tags";
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider lookupProvider) {
        // Opaque entries hide their children from the upcoming research list until they are known. The reference is
        // optional because the entry is not loaded while portals are turned off, and a required reference to a
        // missing entry would fail Primal Magick's whole opaque tag.
        this.tag(ResearchEntryTagsPM.OPAQUE).addOptional(ResearchEntriesPMX.PORTAL_TRAVEL);
    }
}
