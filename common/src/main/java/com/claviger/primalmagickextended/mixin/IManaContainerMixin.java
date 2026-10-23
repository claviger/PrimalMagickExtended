package com.claviger.primalmagickextended.mixin;

import com.claviger.primalmagickextended.common.familiars.FamiliarManager;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.wands.IManaContainer;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Grants the owner of a nearby, in-play familiar a mana discount worth a little over two levels of Mana Efficiency. Primal Magick
 * reads that enchantment in exactly one place, IManaContainer#getTotalCostModifier, as +2 percentage
 * points per level; this injection adds FamiliarManager#MANA_EFFICIENCY_BONUS (+5) to its result. The modifier feeds
 * every mana cost in the mod: spell casts, rituals, tooltips and the wand HUD.
 */
@Mixin(IManaContainer.class)
public interface IManaContainerMixin {
    @ModifyReturnValue(method = "getTotalCostModifier", at = @At("RETURN"), remap = false)
    private static int primalmagickextended$addFamiliarBonus(int original, ItemStack stack, Player player, Source source, HolderLookup.Provider registries) {
        return (player != null && FamiliarManager.isFamiliarNearby(player)) ? original + FamiliarManager.MANA_EFFICIENCY_BONUS : original;
    }
}
