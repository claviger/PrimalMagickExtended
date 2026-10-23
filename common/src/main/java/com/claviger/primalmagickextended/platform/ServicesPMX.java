package com.claviger.primalmagickextended.platform;

import com.claviger.primalmagickextended.platform.services.ICapabilityServicePMX;
import com.claviger.primalmagickextended.platform.services.registries.IItemRegistryServicePMX;
import com.claviger.primalmagickextended.platform.services.registries.ISpellPayloadTypeRegistryServicePMX;
import com.claviger.primalmagickextended.platform.services.registries.ITestFunctionRegistryServicePMX;

import java.util.ServiceLoader;

/**
 * Definition point for cross-platform services of Primal Magick Extended. Mirrors Primal Magick's
 * {@code Services} class, but with this mod's own interfaces so that registrations land in this
 * mod's namespace and on this mod's event bus.
 */
public class ServicesPMX {
    public static final IItemRegistryServicePMX ITEMS_REGISTRY = load(IItemRegistryServicePMX.class);
    public static final ISpellPayloadTypeRegistryServicePMX SPELL_PAYLOAD_TYPES_REGISTRY = load(ISpellPayloadTypeRegistryServicePMX.class);
    public static final ITestFunctionRegistryServicePMX TEST_FUNCTIONS_REGISTRY = load(ITestFunctionRegistryServicePMX.class);
    public static final ICapabilityServicePMX CAPABILITIES = load(ICapabilityServicePMX.class);

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}
