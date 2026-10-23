package com.claviger.primalmagickextended.common.init;

import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.spells.payloads.SpellPayloadsPMX;
import com.claviger.primalmagickextended.test.TestFunctionsPMX;

/**
 * Point of initialization for the mod's cross-platform deferred registries. Each holder's init()
 * call class-loads it so that its static registration fields are populated.
 */
public class InitRegistriesPMX {
    public static void initDeferredRegistries() {
        // Holders are added here by later tasks, in dependency order
        ItemsPMX.init();
        SpellPayloadsPMX.init();
        TestFunctionsPMX.init();
    }
}
