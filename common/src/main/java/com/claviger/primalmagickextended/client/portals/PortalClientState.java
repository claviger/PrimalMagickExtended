package com.claviger.primalmagickextended.client.portals;

import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

/**
 * Client state shared between the portal packet handlers, the loading screen and the travel sound filter. Has no
 * client-only imports, so it is safe to reference from packets. Ported from Custom Portals.
 */
public class PortalClientState {
    /**
     * The portal block whose sprite fills the loading screen background, or null for the default background. Set only
     * while the player is in or travelling through a custom portal.
     */
    @Nullable
    public static Block transitionBackgroundSpriteModel;
}
