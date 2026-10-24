package com.claviger.primalmagickextended.platform.registries;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.platform.services.registries.ICatVariantRegistryServicePMX;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.animal.CatVariant;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * NeoForge implementation of the cat variant registry service.
 */
public class CatVariantRegistryServicePMXNeoforge extends AbstractRegistryServicePMXNeoforge<CatVariant> implements ICatVariantRegistryServicePMX {
    private static final DeferredRegister<CatVariant> CAT_VARIANTS = DeferredRegister.create(BuiltInRegistries.CAT_VARIANT, ConstantsPMX.MOD_ID);

    @Override
    protected Supplier<DeferredRegister<CatVariant>> getDeferredRegisterSupplier() {
        return () -> CAT_VARIANTS;
    }

    @Override
    protected Registry<CatVariant> getRegistry() {
        return BuiltInRegistries.CAT_VARIANT;
    }
}
