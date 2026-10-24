package com.claviger.primalmagickextended.platform;

import com.claviger.primalmagickextended.platform.services.ICapabilityServicePMX;
import com.claviger.primalmagickextended.platform.services.IConfigServicePMX;
import com.claviger.primalmagickextended.platform.services.registries.IBlockEntityTypeRegistryServicePMX;
import com.claviger.primalmagickextended.platform.services.registries.IBlockRegistryServicePMX;
import com.claviger.primalmagickextended.platform.services.registries.ICatVariantRegistryServicePMX;
import com.claviger.primalmagickextended.platform.services.registries.IItemRegistryServicePMX;
import com.claviger.primalmagickextended.platform.services.registries.IParticleTypeRegistryServicePMX;
import com.claviger.primalmagickextended.platform.services.registries.ISpellPayloadTypeRegistryServicePMX;

import java.util.ServiceLoader;

/**
 * Definition point for cross-platform services of Primal Magick Extended. Mirrors Primal Magick's
 * {@code Services} class, but with this mod's own interfaces so that registrations land in this
 * mod's namespace and on this mod's event bus.
 */
public class ServicesPMX {
    public static final IItemRegistryServicePMX ITEMS_REGISTRY = load(IItemRegistryServicePMX.class);
    public static final IBlockRegistryServicePMX BLOCKS_REGISTRY = load(IBlockRegistryServicePMX.class);
    public static final IBlockEntityTypeRegistryServicePMX BLOCK_ENTITY_TYPES_REGISTRY = load(IBlockEntityTypeRegistryServicePMX.class);
    public static final IParticleTypeRegistryServicePMX PARTICLE_TYPES_REGISTRY = load(IParticleTypeRegistryServicePMX.class);
    public static final ISpellPayloadTypeRegistryServicePMX SPELL_PAYLOAD_TYPES_REGISTRY = load(ISpellPayloadTypeRegistryServicePMX.class);
    public static final ICatVariantRegistryServicePMX CAT_VARIANTS_REGISTRY = load(ICatVariantRegistryServicePMX.class);
    public static final ICapabilityServicePMX CAPABILITIES = load(ICapabilityServicePMX.class);
    public static final IConfigServicePMX CONFIG = load(IConfigServicePMX.class);

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}
