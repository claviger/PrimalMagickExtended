package com.claviger.primalmagickextended.platform.registries;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.platform.services.registries.ITestFunctionRegistryServicePMX;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * NeoForge implementation of the test function registry service, registering in this mod's namespace.
 */
public class TestFunctionRegistryServicePMXNeoforge extends AbstractRegistryServicePMXNeoforge<Consumer<GameTestHelper>> implements ITestFunctionRegistryServicePMX {
    private static final DeferredRegister<Consumer<GameTestHelper>> TYPES = DeferredRegister.create(Registries.TEST_FUNCTION, ConstantsPMX.MOD_ID);

    @Override
    protected Supplier<DeferredRegister<Consumer<GameTestHelper>>> getDeferredRegisterSupplier() {
        return () -> TYPES;
    }

    @Override
    protected Registry<Consumer<GameTestHelper>> getRegistry() {
        return BuiltInRegistries.TEST_FUNCTION;
    }
}
