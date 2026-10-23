package com.claviger.primalmagickextended.common.items;

import com.claviger.primalmagickextended.common.items.familiars.FamiliarCollarItem;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.verdantartifice.primalmagick.common.creative.CreativeModeTabsPM;
import com.verdantartifice.primalmagick.common.registries.IRegistryItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

/**
 * Deferred registry for the mod's items. Items are added to Primal Magick's creative tab.
 */
public class ItemsPMX {
    public static void init() {
        // Pass the service initialization through this class so it gets class loaded and fields registered
        ServicesPMX.ITEMS_REGISTRY.init();
    }

    public static final IRegistryItem<Item, FamiliarCollarItem> FAMILIAR_COLLAR = registerSupplier("familiar_collar",
            () -> new FamiliarCollarItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));

    protected static <I extends Item> IRegistryItem<Item, I> registerSupplier(String name, Supplier<I> itemSupplier) {
        IRegistryItem<Item, I> obj = ServicesPMX.ITEMS_REGISTRY.register(name, itemSupplier);
        CreativeModeTabsPM.registerSupplier(obj);
        return obj;
    }
}
