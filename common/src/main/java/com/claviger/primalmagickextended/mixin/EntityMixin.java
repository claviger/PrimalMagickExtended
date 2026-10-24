package com.claviger.primalmagickextended.mixin;

import com.claviger.primalmagickextended.common.network.packets.portals.DrawSpritePacket;
import com.claviger.primalmagickextended.common.portals.CustomPortal;
import com.claviger.primalmagickextended.common.portals.IPortalTraveler;
import com.claviger.primalmagickextended.common.portals.PortalData;
import com.verdantartifice.primalmagick.common.network.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PortalProcessor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes every entity an {@link IPortalTraveler}. Tracks the custom portal an entity stands in and the portal it
 * will arrive at, and tells a player's client which portal colour to draw on the loading screen while travelling.
 * Ported from Custom Portals.
 */
@Mixin(Entity.class)
public abstract class EntityMixin implements IPortalTraveler {
    @Unique
    private int primalmagickextended$portalColor;
    @Unique
    private boolean primalmagickextended$inCustomPortal;
    @Unique
    @Nullable
    private CustomPortal primalmagickextended$destPortal;
    @Unique
    private boolean primalmagickextended$packetSent = false;
    @Unique
    private boolean primalmagickextended$inTransition;

    @Shadow
    @Nullable
    public PortalProcessor portalProcess;
    @Shadow
    private Level level;
    @Shadow
    private BlockPos blockPosition;

    /** Clears a player's loading screen background once they have left the portal and finished the transition. */
    @Inject(method = "baseTick", at = @At("TAIL"))
    private void primalmagickextended$baseTick(CallbackInfo ci) {
        if (!this.primalmagickextended$inCustomPortal && this.primalmagickextended$packetSent && ((Entity) (Object) this) instanceof ServerPlayer && !this.primalmagickextended$inTransition) {
            PacketHandler.sendToPlayer(new DrawSpritePacket(0), (ServerPlayer) (Object) this);
            this.primalmagickextended$packetSent = false;
        }
    }

    /**
     * Sends a player in a custom portal the portal's colour for the loading screen, and forgets the portal once the
     * entity is no longer in any portal.
     */
    @Inject(method = "handlePortal", at = @At("TAIL"))
    private void primalmagickextended$handlePortal(CallbackInfo ci) {
        if (this.level instanceof ServerLevel) {
            if (this.primalmagickextended$inCustomPortal) {
                if (((Entity) (Object) this) instanceof ServerPlayer && !this.primalmagickextended$packetSent) {
                    PacketHandler.sendToPlayer(new DrawSpritePacket(this.primalmagickextended$portalColor), (ServerPlayer) (Object) this);
                    this.primalmagickextended$packetSent = true;
                }
                if (this.portalProcess == null) {
                    this.primalmagickextended$destPortal = null;
                    this.primalmagickextended$inCustomPortal = false;
                    this.primalmagickextended$portalColor = 0;
                }
            }
        }
    }

    @Override
    public void primalmagickextended$setInCustomPortal(CustomPortal customPortal) {
        if (this.portalProcess != null) {
            this.primalmagickextended$destPortal = customPortal.getLinked();
            this.primalmagickextended$inCustomPortal = true;
            this.primalmagickextended$portalColor = customPortal.getColor().id;
        }
    }

    @Override
    @Nullable
    public CustomPortal primalmagickextended$getDestPortal() {
        return this.primalmagickextended$destPortal;
    }

    @Override
    public boolean primalmagickextended$isInCustomPortal() {
        return this.primalmagickextended$inCustomPortal;
    }

    @Override
    public void primalmagickextended$notInCustomPortal() {
        this.primalmagickextended$inCustomPortal = false;
    }

    /** The colour of the custom portal the entity is in, looked up from its position if not yet known; 0 if none. */
    @Override
    public int primalmagickextended$getPortalColor() {
        if (this.primalmagickextended$portalColor == 0) {
            CustomPortal portal = PortalData.getPortalFromPos(this.level, this.blockPosition);
            this.primalmagickextended$portalColor = portal == null ? 0 : portal.getColor().id;
        }
        return this.primalmagickextended$portalColor;
    }

    @Override
    public void primalmagickextended$setPortalColor(int color) {
        this.primalmagickextended$portalColor = color;
    }

    @Override
    public void primalmagickextended$setInTransition(boolean inTransition) {
        this.primalmagickextended$inTransition = inTransition;
    }
}
