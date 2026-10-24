package com.claviger.primalmagickextended.mixin;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.affinities.GeneratedAffinities;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.verdantartifice.primalmagick.common.commands.PrimalMagickCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Keeps Primal Magick's affinity commands useful to data pack authors once affinities are generated. The lint and
 * data pack template commands still list items that only have a generated affinity, as they would without this mod,
 * since those are the items a data pack still needs to cover. The explain command says that an affinity is generated
 * rather than calling it explicitly set.
 */
@Mixin(PrimalMagickCommand.class)
public abstract class PrimalMagickCommandMixin {
    private static final String EXPLAIN_GENERATED = "message." + ConstantsPMX.MOD_ID + ".command.affinities.explain.generated";

    @ModifyReturnValue(method = "listSourcelessItems", at = @At("RETURN"), remap = false)
    private static List<Item> primalmagickextended$listGeneratedAsSourceless(List<Item> original, RecipeManager recipeManager, RegistryAccess registryAccess,
                                                                             ServerLevel level, Collection<String> excludeNamespaces) {
        GeneratedAffinities.Run run = GeneratedAffinities.lastRun();
        if (run == null) {
            return original;
        }
        // Rebuild the list in registry order, as Primal Magick builds it
        Set<Item> listed = new HashSet<>(original);
        return BuiltInRegistries.ITEM.stream().filter(item -> listed.contains(item) || (run.isGeneratedWithoutRecipe(item)
                && (excludeNamespaces == null || !excludeNamespaces.contains(BuiltInRegistries.ITEM.getKey(item).getNamespace())))).toList();
    }

    @Inject(method = "explainItemAffinity", at = @At("HEAD"), cancellable = true, remap = false)
    private static void primalmagickextended$explainGenerated(CommandSourceStack source, ItemInput item, CallbackInfoReturnable<Integer> cir) {
        GeneratedAffinities.Run run = GeneratedAffinities.lastRun();
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item.item().value());
        GeneratedAffinities.Generated generated = run == null ? null : run.generated().get(itemId);
        if (generated != null) {
            source.sendSuccess(() -> Component.translatable(EXPLAIN_GENERATED, itemId.toString(), generated.role().name().toLowerCase(Locale.ROOT)), true);
            cir.setReturnValue(0);
        }
    }
}
