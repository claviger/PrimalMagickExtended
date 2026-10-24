package com.claviger.primalmagickextended.mixin.client;

import com.claviger.primalmagickextended.client.portals.PortalClientState;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.LevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Silences the portal travel sound on arrival through a custom portal when this player has muted teleport sounds.
 * The server always sends the sound, so each player's client setting only affects their own sounds.
 */
@Mixin(LevelEventHandler.class)
public abstract class LevelEventHandlerMixin {
    /*
     * The loading screen background is set while the player stands in a custom portal and is only cleared once the
     * loading screen for the new level closes, which needs chunks the server sends after the travel sound. It is never
     * set for a nether portal, so nether portal travel keeps its sound.
     */
    @Inject(method = "levelEvent(ILnet/minecraft/core/BlockPos;I)V", at = @At("HEAD"), cancellable = true)
    private void primalmagickextended$levelEvent(int eventType, BlockPos pos, int data, CallbackInfo ci) {
        if (eventType == LevelEvent.SOUND_PORTAL_TRAVEL && PortalClientState.transitionBackgroundSpriteModel != null
                && ServicesPMX.CONFIG.portalsMuteTeleportSounds()) {
            ci.cancel();
        }
    }
}
