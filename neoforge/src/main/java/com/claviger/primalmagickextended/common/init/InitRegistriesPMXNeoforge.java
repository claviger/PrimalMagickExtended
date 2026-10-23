package com.claviger.primalmagickextended.common.init;

import com.claviger.primalmagickextended.common.capabilities.CapabilitiesPMXNeoforge;

/**
 * Point of initialization for mod deferred registries on NeoForge.
 */
public class InitRegistriesPMXNeoforge {
    public static void initDeferredRegistries() {
        // Platform implementations of cross-platform registries
        InitRegistriesPMX.initDeferredRegistries();

        // Platform specific registries
        CapabilitiesPMXNeoforge.init();
    }
}
