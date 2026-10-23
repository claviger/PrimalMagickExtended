package com.claviger.primalmagickextended.test.familiars;

import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.research.ResearchEntriesPMX;
import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.verdantartifice.primalmagick.common.affinities.AffinityManager;
import com.verdantartifice.primalmagick.common.blocks.BlocksPM;
import com.verdantartifice.primalmagick.common.crafting.RitualRecipe;
import com.verdantartifice.primalmagick.common.registries.RegistryKeysPM;
import com.verdantartifice.primalmagick.common.research.ResearchDisciplines;
import com.verdantartifice.primalmagick.common.research.ResearchEntries;
import com.verdantartifice.primalmagick.common.research.ResearchEntry;
import com.verdantartifice.primalmagick.common.research.ResearchManager;
import com.verdantartifice.primalmagick.common.research.ResearchTiers;
import com.verdantartifice.primalmagick.common.research.keys.ResearchEntryKey;
import com.verdantartifice.primalmagick.common.research.requirements.ResearchRequirement;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.common.tiles.rituals.RitualAltarTileEntity;
import com.verdantartifice.primalmagick.test.AbstractBaseTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Tests that the familiar research entries and ritual recipe are registered and gated as designed.
 */
public class FamiliarResearchTests extends AbstractBaseTest {
    private static final ResourceKey<Recipe<?>> COLLAR_RECIPE = ResourceKey.create(Registries.RECIPE, ResourceUtilsPMX.loc("familiar_collar"));

    public static void research_entries_are_registered_with_expected_parents(GameTestHelper helper) {
        var registry = helper.getLevel().registryAccess().lookupOrThrow(RegistryKeysPM.RESEARCH_ENTRIES);

        ResearchEntry collar = registry.getOrThrow(ResearchEntriesPMX.FAMILIAR_COLLAR).value();
        assertValueEqual(helper, ResearchDisciplines.RITUAL, collar.disciplineKeyOpt().orElseThrow().getRootKey(), "Collar entry discipline");
        assertValueEqual(helper, Optional.of(ResearchTiers.EXPERT), collar.tierOpt(), "Collar entry tier");
        Set<ResourceKey<ResearchEntry>> collarParents = collar.parents().stream().map(ResearchEntryKey::getRootKey).collect(Collectors.toSet());
        assertValueEqual(helper, Set.of(ResearchEntries.EXPERT_RITUAL, ResearchEntries.RUNE_SUMMON, ResearchEntries.RUNE_CREATURE, ResearchEntries.RUNE_INFERNAL, ResearchEntries.SPELL_PAYLOAD_POLYMORPH, ResearchEntries.SPELL_PAYLOAD_HEALING, ResearchEntries.INCENSE_BRAZIER, ResearchEntries.RITUAL_BELL), collarParents, "Collar entry parents");
        assertTrue(helper, collar.flags().finaleExempt(), "Collar entry is not finale exempt");
        assertValueEqual(helper, 2, collar.stages().size(), "Collar entry stage count");
        assertValueEqual(helper, List.of(COLLAR_RECIPE), collar.stages().get(1).recipes(), "Collar entry stage 2 recipes");
        assertValueEqual(helper, "research.primalmagickextended.familiar_collar.text.stage.1", collar.stages().get(0).textTranslationKey(), "Collar entry stage 1 text key");

        ResearchEntry spell = registry.getOrThrow(ResearchEntriesPMX.FIND_FAMILIAR).value();
        assertValueEqual(helper, ResearchDisciplines.SORCERY, spell.disciplineKeyOpt().orElseThrow().getRootKey(), "Spell entry discipline");
        Set<ResourceKey<ResearchEntry>> spellParents = spell.parents().stream().map(ResearchEntryKey::getRootKey).collect(Collectors.toSet());
        assertValueEqual(helper, Set.of(ResearchEntriesPMX.FAMILIAR_COLLAR), spellParents, "Spell entry parents");
        assertTrue(helper, spell.flags().finaleExempt(), "Spell entry is not finale exempt");
        assertValueEqual(helper, 2, spell.stages().size(), "Spell entry stage count");
        helper.succeed();
    }

    public static void ritual_recipe_has_expected_ingredients_and_requirement(GameTestHelper helper) {
        RecipeHolder<?> holder = helper.getLevel().getServer().getRecipeManager().byKey(COLLAR_RECIPE).orElseThrow(() -> new IllegalStateException("Collar ritual recipe missing"));
        RitualRecipe recipe = assertInstanceOf(helper, holder.value(), RitualRecipe.class, "Collar recipe is not a ritual recipe");
        assertValueEqual(helper, 6, recipe.getIngredients().size(), "Collar ritual ingredient count");
        assertValueEqual(helper, 2, recipe.getProps().size(), "Collar ritual prop count");
        assertTrue(helper, recipe.getProps().stream().anyMatch(prop -> prop.test(BlocksPM.RITUAL_BELL.get().defaultBlockState())), "Collar ritual lacks the ritual bell prop");
        assertTrue(helper, recipe.getProps().stream().anyMatch(prop -> prop.test(BlocksPM.INCENSE_BRAZIER.get().defaultBlockState())), "Collar ritual lacks the incense brazier prop");
        assertValueEqual(helper, 3, recipe.getInstability(), "Collar ritual instability");
        ResearchRequirement requirement = assertInstanceOf(helper, recipe.getRequirement().orElseThrow(() -> new IllegalStateException("Collar ritual has no requirement")),
                ResearchRequirement.class, "Collar ritual requirement is not a research requirement");
        assertValueEqual(helper, new ResearchEntryKey(ResearchEntriesPMX.FAMILIAR_COLLAR), requirement.getRootKey(), "Collar ritual required research");
        assertValueEqual(helper, SourceList.builder().withBlood(2000).withInfernal(2000).build(), recipe.getManaCosts(), "Collar ritual mana costs");
        assertTrue(helper, recipe.getIngredients().get(5).test(new ItemStack(Items.COD)), "Collar ritual fish ingredient rejects raw cod");
        assertFalse(helper, recipe.getIngredients().get(5).test(new ItemStack(Items.COOKED_COD)), "Collar ritual fish ingredient accepts cooked cod");
        // RitualRecipe has no result accessor; assembling it creates a stack from its result template
        assertValueEqual(helper, ItemsPMX.FAMILIAR_COLLAR.get(), recipe.assemble(CraftingInput.EMPTY).getItem(), "Collar ritual result");
        helper.succeed();
    }

    public static void ritual_recipe_requirement_unmet_without_research(GameTestHelper helper) {
        var player = makeMockServerPlayer(helper);
        RecipeHolder<?> holder = helper.getLevel().getServer().getRecipeManager().byKey(COLLAR_RECIPE).orElseThrow(() -> new IllegalStateException("Collar ritual recipe missing"));
        RitualRecipe recipe = assertInstanceOf(helper, holder.value(), RitualRecipe.class, "Collar recipe is not a ritual recipe");
        assertFalse(helper, recipe.getRequirement().orElseThrow().isMetBy(player), "Requirement met without research");
        ResearchManager.forceGrantWithAllParents(player, ResearchEntriesPMX.FAMILIAR_COLLAR);
        assertTrue(helper, recipe.getRequirement().orElseThrow().isMetBy(player), "Requirement unmet after research");
        helper.succeed();
    }

    public static void ritual_altar_finish_craft_yields_familiar_collar(GameTestHelper helper) {
        BlockPos pos = BlockPos.ZERO;
        helper.setBlock(pos, BlocksPM.RITUAL_ALTAR.get());
        RitualAltarTileEntity tile = helper.getBlockEntity(pos, RitualAltarTileEntity.class);
        tile.setActiveRecipeId(COLLAR_RECIPE);
        tile.finishCraftForTesting();
        assertValueEqual(helper, ItemsPMX.FAMILIAR_COLLAR.get(), tile.getItem().getItem(), "Output slot item after finishing the ritual");
        helper.succeed();
    }

    public static void familiar_collar_affinity_has_expected_sources(GameTestHelper helper) {
        // Computes the runtime affinity through Primal Magick's manager, bypassing its per-stack cache; a fixed
        // item affinity needs no recipe scan, so waiting on it here is quick
        SourceList affinity = AffinityManager.getInstance().getAffinityValuesAsync(new ItemStack(ItemsPMX.FAMILIAR_COLLAR.get()), helper.getLevel())
                .orTimeout(30, TimeUnit.SECONDS).join();
        assertValueEqual(helper, SourceList.builder().withBlood(4).withInfernal(4).withEarth(1).withSky(1).withSea(1).build(), affinity, "Familiar collar affinity");
        helper.succeed();
    }
}
