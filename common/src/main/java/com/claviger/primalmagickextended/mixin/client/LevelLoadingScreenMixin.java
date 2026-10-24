package com.claviger.primalmagickextended.mixin.client;

import com.claviger.primalmagickextended.client.portals.PortalClientState;
import com.claviger.primalmagickextended.common.network.packets.portals.ScreenTransitionPacket;
import com.verdantartifice.primalmagick.common.network.PacketHandler;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fills the loading screen shown during portal travel with the texture of the portal travelled through, and tells
 * the server when that screen opens and closes. Ported from Custom Portals.
 */
@Mixin(LevelLoadingScreen.class)
public abstract class LevelLoadingScreenMixin extends Screen {
    @Shadow
    private LevelLoadingScreen.Reason reason;
    @Shadow
    private LevelLoadTracker loadTracker;

    @Unique
    private boolean primalmagickextended$packetSent = false;

    public LevelLoadingScreenMixin(LevelLoadTracker clientChunkLoadProgress, LevelLoadingScreen.Reason worldEntryReason) {
        super(GameNarrator.NO_TITLE);
        this.loadTracker = clientChunkLoadProgress;
        this.reason = worldEntryReason;
    }

    @Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
    private void primalmagickextended$extractBackground(GuiGraphicsExtractor drawContext, int i, int j, float f, CallbackInfo ci) {
        if (PortalClientState.transitionBackgroundSpriteModel != null) {
            if (!this.primalmagickextended$packetSent) {
                PacketHandler.sendToServer(new ScreenTransitionPacket(true));
                this.primalmagickextended$packetSent = true;
            }
            Identifier blockId = BuiltInRegistries.BLOCK.getKey(PortalClientState.transitionBackgroundSpriteModel);
            Identifier textureId = Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/" + blockId.getPath());
            TextureAtlasSprite sprite = this.minecraft.getAtlasManager().get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, textureId));
            drawContext.blitSprite(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND, sprite, 0, 0, drawContext.guiWidth(), drawContext.guiHeight());
            ci.cancel();
        }
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void primalmagickextended$onClose(CallbackInfo ci) {
        PacketHandler.sendToServer(new ScreenTransitionPacket(false));
        PortalClientState.transitionBackgroundSpriteModel = null;
        this.primalmagickextended$packetSent = false;
    }
}
