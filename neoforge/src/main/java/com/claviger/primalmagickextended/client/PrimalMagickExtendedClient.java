package com.claviger.primalmagickextended.client;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.client.portals.PortalClientState;
import com.claviger.primalmagickextended.common.portals.ClientPortalData;
import com.claviger.primalmagickextended.common.portals.PortalRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Physical-client entry point of the Primal Magick Extended mod. Registers the config screen and resets client
 * portal state on logout. Nothing here is loaded on a dedicated server.
 */
@Mod(value = ConstantsPMX.MOD_ID, dist = Dist.CLIENT)
public class PrimalMagickExtendedClient {
    public PrimalMagickExtendedClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.addListener(PrimalMagickExtendedClient::onLoggedOut);
    }

    /** Drops the synced portal registry when leaving a server, so a stale copy cannot leak into the next world. */
    private static void onLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientPortalData.set(new PortalRegistry());
        PortalClientState.transitionBackgroundSpriteModel = null;
    }
}
