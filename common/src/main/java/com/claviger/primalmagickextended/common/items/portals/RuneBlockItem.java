package com.claviger.primalmagickextended.common.items.portals;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.IntSupplier;

/**
 * Block item for the portal runes. Adds a gray description line to the tooltip, optionally formatted with a range
 * read from the config when the tooltip is shown. Ported from Custom Portals.
 */
public class RuneBlockItem extends BlockItem {
    private final String tooltipKey;
    @Nullable
    private final IntSupplier rangeArgument;

    public RuneBlockItem(Block block, Item.Properties properties, String tooltipKey, @Nullable IntSupplier rangeArgument) {
        super(block, properties);
        this.tooltipKey = tooltipKey;
        this.rangeArgument = rangeArgument;
    }

    /**
     * The item's own {@code item.primalmagickextended.<id>} key. A block item would otherwise use its block's key,
     * and the rune items are named apart from their blocks.
     */
    @Override
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        MutableComponent line = rangeArgument == null
                ? Component.translatable(tooltipKey)
                : Component.translatable(tooltipKey, rangeArgument.getAsInt());
        tooltip.add(line.withStyle(ChatFormatting.GRAY));
    }
}
