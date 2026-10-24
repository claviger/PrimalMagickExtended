package com.claviger.primalmagickextended.platform.registries;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.platform.services.registries.IItemRegistryServicePMX;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * NeoForge implementation of the item registry service.
 */
public class ItemRegistryServicePMXNeoforge extends AbstractRegistryServicePMXNeoforge<Item> implements IItemRegistryServicePMX {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ConstantsPMX.MOD_ID);

    @Override
    protected Supplier<DeferredRegister<Item>> getDeferredRegisterSupplier() {
        return () -> ITEMS;
    }

    @Override
    protected Registry<Item> getRegistry() {
        return BuiltInRegistries.ITEM;
    }
}
