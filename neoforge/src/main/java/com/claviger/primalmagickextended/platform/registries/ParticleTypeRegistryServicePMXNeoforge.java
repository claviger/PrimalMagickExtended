package com.claviger.primalmagickextended.platform.registries;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.platform.services.registries.IParticleTypeRegistryServicePMX;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * NeoForge implementation of the particle type registry service.
 */
public class ParticleTypeRegistryServicePMXNeoforge extends AbstractRegistryServicePMXNeoforge<ParticleType<?>> implements IParticleTypeRegistryServicePMX {
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, ConstantsPMX.MOD_ID);

    @Override
    protected Supplier<DeferredRegister<ParticleType<?>>> getDeferredRegisterSupplier() {
        return () -> PARTICLE_TYPES;
    }

    @Override
    protected Registry<ParticleType<?>> getRegistry() {
        return BuiltInRegistries.PARTICLE_TYPE;
    }
}
