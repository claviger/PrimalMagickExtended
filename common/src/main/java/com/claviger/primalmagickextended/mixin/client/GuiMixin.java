package com.claviger.primalmagickextended.mixin.client;

import com.claviger.primalmagickextended.common.portals.IPortalTraveler;
import com.claviger.primalmagickextended.common.portals.PortalHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Draws the portal overlay with the texture of the custom portal the player stands in, instead of the nether
 * portal's. Ported from Custom Portals.
 */
@Mixin(Gui.class)
public abstract class GuiMixin {
    @Final
    @Shadow
    private Minecraft minecraft;

    /*
     * Mirrors the vanilla overlay drawing, with the custom portal's sprite in place of the nether portal's.
     */
    @Inject(method = "renderPortalOverlay(Lnet/minecraft/client/gui/GuiGraphics;F)V", at = @At("HEAD"), cancellable = true)
    private void primalmagickextended$renderPortalOverlay(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        int color = ((IPortalTraveler) this.minecraft.player).primalmagickextended$getPortalColor();
        if (color != 0) {
            Block spriteModel = PortalHelper.getPortalBlockFromColorId(color);
            if (f < 1.0F) {
                f *= f;
                f *= f;
                f = f * 0.8F + 0.2F;
            }

            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(spriteModel);
            ResourceLocation textureId = ResourceLocation.fromNamespaceAndPath(blockId.getNamespace(), "block/" + blockId.getPath());
            TextureAtlasSprite sprite = this.minecraft.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(textureId);
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, f);
            guiGraphics.blit(0, 0, -90, guiGraphics.guiWidth(), guiGraphics.guiHeight(), sprite);
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            ci.cancel();
        }
    }
}
