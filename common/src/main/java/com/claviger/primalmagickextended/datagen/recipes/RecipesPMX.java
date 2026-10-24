package com.claviger.primalmagickextended.datagen.recipes;

import com.claviger.primalmagickextended.common.features.FeaturePMX;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import com.claviger.primalmagickextended.common.research.ResearchEntriesPMX;
import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.verdantartifice.primalmagick.common.blocks.BlocksPM;
import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.common.sources.Sources;
import com.verdantartifice.primalmagick.common.tags.CommonTags;
import com.verdantartifice.primalmagick.datagen.recipes.ArcaneShapedRecipeBuilder;
import com.verdantartifice.primalmagick.datagen.recipes.RitualRecipeBuilder;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

/**
 * Recipe data provider for the mod. Uses Primal Magick's recipe builders; recipe ids take this mod's namespace
 * because they are derived from the result item. Every recipe belongs to a feature and is written through
 * {@link #featureOutput(RecipeOutput, FeaturePMX)}, so that the platform can make it load only while that feature is
 * turned on.
 */
public class RecipesPMX extends RecipeProvider {
    public RecipesPMX(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    /**
     * The output for the recipes of a feature, and their unlock advancements. Load conditions are a platform
     * feature, so this returns the plain output and the NeoForge subclass attaches the feature's condition.
     */
    protected RecipeOutput featureOutput(RecipeOutput output, FeaturePMX feature) {
        return output;
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        this.registerFamiliarRecipes(output);
        this.registerPortalRecipes(output);
    }

    protected void registerFamiliarRecipes(RecipeOutput output) {
        RitualRecipeBuilder.ritualRecipe(ItemsPMX.FAMILIAR_COLLAR.get())
                .addIngredient(ItemsPM.RUNE_SUMMON.get())
                .addIngredient(ItemsPM.RUNE_CREATURE.get())
                .addIngredient(ItemsPM.RUNE_INFERNAL.get())
                .addIngredient(Items.BONE)
                .addIngredient(Items.FEATHER)
                .addIngredient(CommonTags.Items.FOODS_RAW_FISH)
                .addProp(BlocksPM.RITUAL_BELL.get())
                .addProp(BlocksPM.INCENSE_BRAZIER.get())
                .requiredResearch(ResearchEntriesPMX.FAMILIAR_COLLAR)
                .manaCost(SourceList.EMPTY.add(Sources.BLOOD, 20).add(Sources.INFERNAL, 20))
                .instability(3)
                .build(this.featureOutput(output, FeaturePMX.FAMILIARS));
    }

    /**
     * Registers the portal catalyst and rune recipes, ported from the datapack with mana in whole mana. Catalysts and
     * runes are crafted only at the arcane workbench; the one vanilla recipe per colour recolours an existing catalyst.
     */
    protected void registerPortalRecipes(RecipeOutput output) {
        RecipeOutput portalOutput = this.featureOutput(output, FeaturePMX.PORTALS);
        for (PortalColor color : PortalColor.values()) {
            Item catalyst = ItemsPMX.catalyst(color);
            Item dye = color.dyeItem();
            // One shared expertise group, so crafting all sixteen colours grants expertise once rather than sixteen times
            ArcaneShapedRecipeBuilder.arcaneShapedRecipe(catalyst, 2)
                    .patternLine("#E#")
                    .patternLine("PBP")
                    .patternLine("#E#")
                    .key('#', ItemsPM.ESSENCE_SHARD_INFERNAL.get())
                    .key('E', Items.ENDER_EYE)
                    .key('P', Items.ENDER_PEARL)
                    .key('B', dye)
                    .requiredResearch(ResearchEntriesPMX.PORTAL_TRAVEL)
                    .manaCost(SourceList.EMPTY.add(Sources.INFERNAL, 100))
                    .expertiseGroup(ResourceUtilsPMX.loc("portal_catalysts"))
                    .build(portalOutput);

            Item[] otherCatalysts = ItemsPMX.catalysts().stream().filter(other -> other != catalyst).toArray(Item[]::new);
            // Unlocks on owning any catalyst that this recipe can recolour. A single predicate over all fifteen items
            // matches any of them; one predicate per item would require all of them.
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, catalyst)
                    .requires(Ingredient.of(otherCatalysts))
                    .requires(dye)
                    .group("portal_catalyst")
                    .unlockedBy("has_portal_catalyst", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(otherCatalysts)))
                    .save(portalOutput, ResourceUtilsPMX.loc(color.getSerializedName() + "_portal_catalyst_2"));
        }

        ArcaneShapedRecipeBuilder.arcaneShapedRecipe(ItemsPMX.WEAK_ENHANCER_RUNE.get())
                .patternLine("###")
                .patternLine("#A ")
                .key('#', Items.LAPIS_LAZULI)
                .key('A', Items.AMETHYST_SHARD)
                .requiredResearch(ResearchEntriesPMX.WEAK_ENHANCER_RUNE)
                .manaCost(SourceList.EMPTY.add(Sources.INFERNAL, 250))
                .build(portalOutput);
        ArcaneShapedRecipeBuilder.arcaneShapedRecipe(ItemsPMX.STRONG_ENHANCER_RUNE.get())
                .patternLine(" # ")
                .patternLine("PW#")
                .patternLine("#P ")
                .key('#', Items.PRISMARINE_SHARD)
                .key('P', Items.PRISMARINE_CRYSTALS)
                .key('W', ItemsPMX.WEAK_ENHANCER_RUNE.get())
                .requiredResearch(ResearchEntriesPMX.STRONG_ENHANCER_RUNE)
                .manaCost(SourceList.EMPTY.add(Sources.INFERNAL, 750))
                .build(portalOutput);
        ArcaneShapedRecipeBuilder.arcaneShapedRecipe(ItemsPMX.INFINITY_RUNE.get())
                .patternLine("#PP")
                .patternLine("PnP")
                .patternLine("PP#")
                .key('#', ItemsPMX.STRONG_ENHANCER_RUNE.get())
                .key('P', Items.POPPED_CHORUS_FRUIT)
                .key('n', Items.NETHERITE_INGOT)
                .requiredResearch(ResearchEntriesPMX.INFINITY_RUNE)
                .manaCost(SourceList.EMPTY.add(Sources.VOID, 750).add(Sources.INFERNAL, 750))
                .build(portalOutput);
        ArcaneShapedRecipeBuilder.arcaneShapedRecipe(ItemsPMX.GATE_RUNE.get())
                .patternLine("##T")
                .patternLine("#A#")
                .patternLine("T##")
                .key('#', Items.WEEPING_VINES)
                .key('T', Items.POPPED_CHORUS_FRUIT)
                .key('A', Items.AMETHYST_SHARD)
                .requiredResearch(ResearchEntriesPMX.GATE_RUNE)
                .manaCost(SourceList.EMPTY.add(Sources.VOID, 750))
                .build(portalOutput);
        ArcaneShapedRecipeBuilder.arcaneShapedRecipe(ItemsPMX.HASTE_RUNE.get())
                .patternLine("#  ")
                .patternLine("#R#")
                .patternLine(" A#")
                .key('#', Items.FEATHER)
                .key('R', Items.RABBIT_FOOT)
                .key('A', Items.AMETHYST_SHARD)
                .requiredResearch(ResearchEntriesPMX.HASTE_RUNE)
                .manaCost(SourceList.EMPTY.add(Sources.VOID, 750).add(Sources.INFERNAL, 750))
                .build(portalOutput);
    }
}
