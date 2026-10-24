package com.claviger.primalmagickextended.datagen.loot_tables;

import com.claviger.primalmagickextended.common.blocks.BlocksPMX;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Collections;
import java.util.List;

/**
 * Data provider for the mod's block loot tables. Each rune block drops its rune item unless destroyed by an
 * explosion that destroys its drops. Portal blocks are unbreakable and have no table, as in Custom Portals.
 */
public class BlockLootTablesPMX extends BlockLootSubProvider {
    public BlockLootTablesPMX(HolderLookup.Provider registries) {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.dropOther(BlocksPMX.HASTE_RUNE_BLOCK.get(), ItemsPMX.HASTE_RUNE.get());
        this.dropOther(BlocksPMX.GATE_RUNE_BLOCK.get(), ItemsPMX.GATE_RUNE.get());
        this.dropOther(BlocksPMX.WEAK_ENHANCER_RUNE_BLOCK.get(), ItemsPMX.WEAK_ENHANCER_RUNE.get());
        this.dropOther(BlocksPMX.STRONG_ENHANCER_RUNE_BLOCK.get(), ItemsPMX.STRONG_ENHANCER_RUNE.get());
        this.dropOther(BlocksPMX.INFINITY_RUNE_BLOCK.get(), ItemsPMX.INFINITY_RUNE.get());
    }

    // This method overrides the getKnownBlocks base method patched into BlockLootSubProvider by the loaders. It has no
    // override annotation because vanilla's class lacks it, but it is still called through polymorphism, as in
    // Primal Magick's AbstractBlockLootTableProvider.
    protected Iterable<Block> getKnownBlocks() {
        // Only the rune blocks have tables
        return List.of(BlocksPMX.HASTE_RUNE_BLOCK.get(), BlocksPMX.GATE_RUNE_BLOCK.get(), BlocksPMX.WEAK_ENHANCER_RUNE_BLOCK.get(),
                BlocksPMX.STRONG_ENHANCER_RUNE_BLOCK.get(), BlocksPMX.INFINITY_RUNE_BLOCK.get());
    }

    public static LootTableProvider.SubProviderEntry getSubProviderEntry() {
        return new LootTableProvider.SubProviderEntry(BlockLootTablesPMX::new, LootContextParamSets.BLOCK);
    }
}
