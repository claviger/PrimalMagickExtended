package com.claviger.primalmagickextended.common.items.familiars;

import com.claviger.primalmagickextended.ConstantsPMX;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * Collar which binds a tamed pet to the player as a familiar. Applying it is handled by
 * {@code FamiliarEvents.onEntityInteract}, because owned pets consume right-clicks before
 * {@link Item#interactLivingEntity} runs.
 */
public class FamiliarCollarItem extends Item {
    public static final String TOOLTIP_KEY = "tooltip." + ConstantsPMX.MOD_ID + ".familiar_collar";

    public FamiliarCollarItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable(TOOLTIP_KEY).withStyle(ChatFormatting.GRAY));
    }
}
