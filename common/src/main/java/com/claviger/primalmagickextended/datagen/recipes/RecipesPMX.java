package com.claviger.primalmagickextended.datagen.recipes;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.research.ResearchEntriesPMX;
import com.verdantartifice.primalmagick.common.blocks.BlocksPM;
import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.common.sources.Sources;
import com.verdantartifice.primalmagick.common.tags.CommonTags;
import com.verdantartifice.primalmagick.datagen.recipes.RitualRecipeBuilder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Recipe data provider for the mod. Uses Primal Magick's ritual recipe builder; recipe ids take this
 * mod's namespace because they are derived from the result item.
 */
public class RecipesPMX extends RecipeProvider {
    // Vanilla RecipeProvider.items is private outside Primal Magick's access transformer, so the common module keeps its own lookup
    protected final HolderGetter<Item> itemGetter;
    protected final HolderGetter<Block> blocks;

    public RecipesPMX(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
        this.itemGetter = registries.lookupOrThrow(Registries.ITEM);
        this.blocks = registries.lookupOrThrow(Registries.BLOCK);
    }

    @Override
    protected void buildRecipes() {
        this.registerFamiliarRecipes();
    }

    protected void registerFamiliarRecipes() {
        RitualRecipeBuilder.ritualRecipe(this.itemGetter, this.blocks, ItemsPMX.FAMILIAR_COLLAR.get())
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
                .build(this.output);
    }

    /**
     * Datagen runner for the recipe provider.
     */
    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        @NotNull
        protected RecipeProvider createRecipeProvider(@NotNull HolderLookup.Provider provider, @NotNull RecipeOutput recipeOutput) {
            return new RecipesPMX(provider, recipeOutput);
        }

        @Override
        @NotNull
        public String getName() {
            return ConstantsPMX.MOD_NAME + " Recipes";
        }
    }
}
