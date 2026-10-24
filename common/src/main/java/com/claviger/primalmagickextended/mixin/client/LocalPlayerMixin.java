package com.claviger.primalmagickextended.mixin.client;

import com.claviger.primalmagickextended.common.portals.CustomPortal;
import com.claviger.primalmagickextended.common.portals.IPortalTraveler;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces the nether portal's nausea effect and trigger sound while the player stands in a custom portal, and
 * clears the portal colour once the effect has faded. Ported from Custom Portals.
 */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    @Final
    @Shadow
    protected Minecraft minecraft;
    @Shadow
    public float oSpinningEffectIntensity;
    @Shadow
    public float spinningEffectIntensity;

    public LocalPlayerMixin(Minecraft minecraftClient, ClientLevel clientWorld, ClientPacketListener clientPlayNetworkHandler, StatsCounter statHandler, ClientRecipeBook clientRecipeBook, boolean bl, boolean bl2) {
        super(clientWorld, clientPlayNetworkHandler.getLocalGameProfile());
    }

    @Inject(method = "handleConfusionTransitionEffect(Z)V", at = @At("HEAD"), cancellable = true)
    private void primalmagickextended$handleConfusionTransitionEffect(boolean useConfusion, CallbackInfo ci) {
        IPortalTraveler traveler = (IPortalTraveler) this;
        CustomPortal portal = traveler.primalmagickextended$getDestPortal();
        float f = 0.0F;
        if (traveler.primalmagickextended$isInCustomPortal() && portal != null && this.portalProcess != null) {
            this.oSpinningEffectIntensity = this.spinningEffectIntensity;
            if (this.minecraft.screen != null && !this.minecraft.screen.isPauseScreen() && !(this.minecraft.screen instanceof DeathScreen)) {
                if (this.minecraft.screen instanceof AbstractContainerScreen) {
                    this.closeContainer();
                }

                this.minecraft.setScreen((Screen) null);
            }

            if (!ServicesPMX.CONFIG.portalsMuteTeleportSounds() && this.spinningEffectIntensity == 0.0F && portal.getPlayerTeleportDelay() > 1) {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.PORTAL_TRIGGER, this.random.nextFloat() * 0.4F + 0.8F, 0.25F));
            }

            f = 0.0125F;
            this.portalProcess.setAsInsidePortalThisTick(false);
            traveler.primalmagickextended$notInCustomPortal();
            this.spinningEffectIntensity = Mth.clamp(this.spinningEffectIntensity + f, 0.0F, 1.0F);
            this.processPortalCooldown();
            ci.cancel();
        }
        if (this.oSpinningEffectIntensity == 0.0F && traveler.primalmagickextended$getPortalColor() != 0) {
            traveler.primalmagickextended$setPortalColor(0);
        }
    }
}
