package com.claviger.primalmagickextended.platform.registries;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.platform.services.registries.IBlockRegistryServicePMX;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * NeoForge implementation of the block registry service.
 */
public class BlockRegistryServicePMXNeoforge extends AbstractRegistryServicePMXNeoforge<Block> implements IBlockRegistryServicePMX {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, ConstantsPMX.MOD_ID);

    @Override
    protected Supplier<DeferredRegister<Block>> getDeferredRegisterSupplier() {
        return () -> BLOCKS;
    }

    @Override
    protected Registry<Block> getRegistry() {
        return BuiltInRegistries.BLOCK;
    }
}
