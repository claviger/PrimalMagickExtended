package com.claviger.primalmagickextended.common.portals;

import org.jetbrains.annotations.Nullable;

/**
 * Duck interface implemented on every entity by EntityMixin. Tracks whether the entity stands in a custom portal,
 * that portal's colour, the portal it will arrive at, and whether its client is showing the transition screen.
 * Ported from Custom Portals.
 */
public interface IPortalTraveler {
    void primalmagickextended$setInCustomPortal(CustomPortal portal);
    boolean primalmagickextended$isInCustomPortal();
    void primalmagickextended$notInCustomPortal();
    int primalmagickextended$getPortalColor();
    void primalmagickextended$setPortalColor(int color);
    void primalmagickextended$setInTransition(boolean inTransition);
    @Nullable
    CustomPortal primalmagickextended$getDestPortal();
}
