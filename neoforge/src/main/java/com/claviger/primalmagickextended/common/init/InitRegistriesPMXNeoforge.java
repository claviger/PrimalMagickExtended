package com.claviger.primalmagickextended.common.init;

import com.claviger.primalmagickextended.common.capabilities.CapabilitiesPMXNeoforge;
import com.claviger.primalmagickextended.common.conditions.ConditionsPMXNeoforge;
import com.claviger.primalmagickextended.test.TestFunctionsPMXNeoforge;

/**
 * Point of initialization for mod deferred registries on NeoForge.
 */
public class InitRegistriesPMXNeoforge {
    public static void initDeferredRegistries() {
        // Platform implementations of cross-platform registries
        InitRegistriesPMX.initDeferredRegistries();

        // Platform specific registries
        CapabilitiesPMXNeoforge.init();
        ConditionsPMXNeoforge.init();
        TestFunctionsPMXNeoforge.init();
    }
}
