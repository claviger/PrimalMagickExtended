package com.claviger.primalmagickextended.common.research;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.verdantartifice.primalmagick.common.registries.RegistryKeysPM;
import com.verdantartifice.primalmagick.common.research.ResearchDisciplines;
import com.verdantartifice.primalmagick.common.research.ResearchEntries;
import com.verdantartifice.primalmagick.common.research.ResearchEntry;
import com.verdantartifice.primalmagick.common.research.ResearchTiers;
import com.verdantartifice.primalmagick.common.sources.Sources;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import java.util.function.Function;

/**
 * Research entry keys and datapack bootstrap for the mod. Entries are built with this mod's id so
 * that their stage text keys land in this mod's namespace, and are flagged finale-exempt so that
 * they never block Primal Magick's discipline finales.
 */
public class ResearchEntriesPMX {
    public static final ResourceKey<ResearchEntry> FAMILIAR_COLLAR = create("familiar_collar");
    public static final ResourceKey<ResearchEntry> FIND_FAMILIAR = create("find_familiar");

    public static ResourceKey<ResearchEntry> create(String name) {
        return ResourceKey.create(RegistryKeysPM.RESEARCH_ENTRIES, ResourceUtilsPMX.loc(name));
    }

    public static void bootstrap(BootstrapContext<ResearchEntry> context) {
        register(context, FAMILIAR_COLLAR, key -> new ResearchEntry.Builder(ConstantsPMX.MOD_ID, key)
                .discipline(ResearchDisciplines.RITUAL).tier(ResearchTiers.EXPERT).icon(ItemsPMX.FAMILIAR_COLLAR.get())
                .parent(ResearchEntries.EXPERT_RITUAL).parent(ResearchEntries.RUNE_SUMMON).parent(ResearchEntries.RUNE_CREATURE)
                .parent(ResearchEntries.RUNE_INFERNAL).parent(ResearchEntries.SPELL_PAYLOAD_POLYMORPH).parent(ResearchEntries.SPELL_PAYLOAD_HEALING)
                .parent(ResearchEntries.INCENSE_BRAZIER).parent(ResearchEntries.RITUAL_BELL)
                .flags(ResearchEntry.Flags.builder().finaleExempt())
                .stage().requiredTheories(1).end()
                .stage().recipe(ItemsPMX.FAMILIAR_COLLAR.get()).end()
                .build());
        register(context, FIND_FAMILIAR, key -> new ResearchEntry.Builder(ConstantsPMX.MOD_ID, key)
                .discipline(ResearchDisciplines.SORCERY).icon(Sources.BLOOD.getImageTexture())
                .parent(FAMILIAR_COLLAR)
                .flags(ResearchEntry.Flags.builder().finaleExempt())
                .stage().requiredTheories(2).end()
                .stage().end()
                .build());
    }

    private static Holder.Reference<ResearchEntry> register(BootstrapContext<ResearchEntry> context, ResourceKey<ResearchEntry> key, Function<ResourceKey<ResearchEntry>, ResearchEntry> supplier) {
        return context.register(key, supplier.apply(key));
    }
}
