package com.claviger.primalmagickextended.mixin.client;

import com.claviger.primalmagickextended.client.portals.PortalClientState;
import com.claviger.primalmagickextended.common.network.packets.portals.ScreenTransitionPacket;
import com.verdantartifice.primalmagick.common.network.PacketHandler;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 * Fills the loading screen shown during portal travel with the texture of the portal travelled through, and tells
 * the server when that screen opens and closes. Ported from Custom Portals.
 */
@Mixin(ReceivingLevelScreen.class)
public abstract class ReceivingLevelScreenMixin extends Screen {
    @Unique
    private boolean primalmagickextended$packetSent = false;

    public ReceivingLevelScreenMixin(BooleanSupplier levelReceived, ReceivingLevelScreen.Reason reason) {
        super(GameNarrator.NO_TITLE);
    }

    /*
     * Draws the sprite as vanilla draws the nether portal's on this screen.
     */
    @Inject(method = "renderBackground(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("HEAD"), cancellable = true)
    private void primalmagickextended$renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (PortalClientState.transitionBackgroundSpriteModel != null) {
            if (!this.primalmagickextended$packetSent) {
                PacketHandler.sendToServer(new ScreenTransitionPacket(true));
                this.primalmagickextended$packetSent = true;
            }
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(PortalClientState.transitionBackgroundSpriteModel);
            ResourceLocation textureId = ResourceLocation.fromNamespaceAndPath(blockId.getNamespace(), "block/" + blockId.getPath());
            TextureAtlasSprite sprite = this.minecraft.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(textureId);
            guiGraphics.blit(0, 0, -90, guiGraphics.guiWidth(), guiGraphics.guiHeight(), sprite);
            ci.cancel();
        }
    }

    @Inject(method = "onClose()V", at = @At("HEAD"))
    private void primalmagickextended$onClose(CallbackInfo ci) {
        PacketHandler.sendToServer(new ScreenTransitionPacket(false));
        PortalClientState.transitionBackgroundSpriteModel = null;
        this.primalmagickextended$packetSent = false;
    }
}
