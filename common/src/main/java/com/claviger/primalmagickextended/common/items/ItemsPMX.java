package com.claviger.primalmagickextended.common.items;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.blocks.BlocksPMX;
import com.claviger.primalmagickextended.common.blocks.portals.AbstractRuneBlock;
import com.claviger.primalmagickextended.common.items.familiars.FamiliarCollarItem;
import com.claviger.primalmagickextended.common.items.portals.PortalCatalystItem;
import com.claviger.primalmagickextended.common.items.portals.RuneBlockItem;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.verdantartifice.primalmagick.common.creative.CreativeModeTabsPM;
import com.verdantartifice.primalmagick.common.registries.IRegistryItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Deferred registry for the mod's items. Items are added to Primal Magick's creative tab, in registration order.
 * Must be initialized after {@link BlocksPMX}, since block items look up their blocks while being constructed.
 */
public class ItemsPMX {
    public static void init() {
        // Pass the service initialization through this class so it gets class loaded and fields registered
        ServicesPMX.ITEMS_REGISTRY.init();
    }

    // Familiars
    public static final IRegistryItem<Item, FamiliarCollarItem> FAMILIAR_COLLAR = registerSupplier("familiar_collar",
            () -> new FamiliarCollarItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));

    // Portals
    // Portal catalysts, registered in Custom Portals' creative tab order
    private static final Map<PortalColor, IRegistryItem<Item, PortalCatalystItem>> CATALYSTS = registerCatalysts();

    private static Map<PortalColor, IRegistryItem<Item, PortalCatalystItem>> registerCatalysts() {
        List<PortalColor> creativeOrder = List.of(PortalColor.WHITE, PortalColor.LIGHT_GRAY, PortalColor.GRAY,
                PortalColor.BLACK, PortalColor.BROWN, PortalColor.RED, PortalColor.ORANGE, PortalColor.YELLOW,
                PortalColor.LIME, PortalColor.GREEN, PortalColor.CYAN, PortalColor.LIGHT_BLUE, PortalColor.BLUE,
                PortalColor.PURPLE, PortalColor.MAGENTA, PortalColor.PINK);
        Map<PortalColor, IRegistryItem<Item, PortalCatalystItem>> catalysts = new EnumMap<>(PortalColor.class);
        for (PortalColor color : creativeOrder) {
            catalysts.put(color, registerSupplier(color.getSerializedName() + "_portal_catalyst",
                    () -> new PortalCatalystItem(new Item.Properties().stacksTo(16), () -> BlocksPMX.portal(color))));
        }
        return Collections.unmodifiableMap(catalysts);
    }

    // Rune block items (item ids differ from block ids, as in Custom Portals)
    public static final IRegistryItem<Item, RuneBlockItem> HASTE_RUNE = registerRune("haste_rune", BlocksPMX.HASTE_RUNE_BLOCK, null);
    public static final IRegistryItem<Item, RuneBlockItem> GATE_RUNE = registerRune("gate_rune", BlocksPMX.GATE_RUNE_BLOCK, null);
    public static final IRegistryItem<Item, RuneBlockItem> WEAK_ENHANCER_RUNE = registerRune("weak_enhancer_rune", BlocksPMX.WEAK_ENHANCER_RUNE_BLOCK, ServicesPMX.CONFIG::portalsRangeWithEnhancer);
    public static final IRegistryItem<Item, RuneBlockItem> STRONG_ENHANCER_RUNE = registerRune("strong_enhancer_rune", BlocksPMX.STRONG_ENHANCER_RUNE_BLOCK, ServicesPMX.CONFIG::portalsRangeWithStrongEnhancer);
    public static final IRegistryItem<Item, RuneBlockItem> INFINITY_RUNE = registerRune("infinity_rune", BlocksPMX.INFINITY_RUNE_BLOCK, null);

    /** The portal catalyst of the given colour. */
    public static Item catalyst(PortalColor color) {
        return CATALYSTS.get(color).get();
    }

    /** All sixteen portal catalysts, in {@link PortalColor} order. */
    public static List<Item> catalysts() {
        return Arrays.stream(PortalColor.values()).map(ItemsPMX::catalyst).toList();
    }

    /** The five rune items, in registration order. */
    public static List<Item> runes() {
        return List.of(HASTE_RUNE.get(), GATE_RUNE.get(), WEAK_ENHANCER_RUNE.get(), STRONG_ENHANCER_RUNE.get(), INFINITY_RUNE.get());
    }

    private static IRegistryItem<Item, RuneBlockItem> registerRune(String name, IRegistryItem<Block, ? extends AbstractRuneBlock> block,
                                                                   @Nullable IntSupplier rangeArgument) {
        String tooltipKey = "item." + ConstantsPMX.MOD_ID + "." + name + ".tooltip";
        return registerSupplier(name, () -> new RuneBlockItem(block.get(), new Item.Properties().stacksTo(1), tooltipKey, rangeArgument));
    }

    protected static <I extends Item> IRegistryItem<Item, I> registerSupplier(String name, Supplier<I> itemSupplier) {
        IRegistryItem<Item, I> obj = ServicesPMX.ITEMS_REGISTRY.register(name, itemSupplier);
        CreativeModeTabsPM.registerSupplier(obj);
        return obj;
    }
}
