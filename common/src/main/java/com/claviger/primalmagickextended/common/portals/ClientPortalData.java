package com.claviger.primalmagickextended.common.portals;

/**
 * Client-side copy of the portal registry, replaced wholesale by each sync packet. Has no client-only imports, so
 * it is safe to reference from common code. Ported from Custom Portals.
 */
public final class ClientPortalData {
    private static PortalRegistry registry = new PortalRegistry();

    private ClientPortalData() {}

    public static PortalRegistry registry() {
        return registry;
    }

    public static void set(PortalRegistry newRegistry) {
        registry = newRegistry;
    }
}
