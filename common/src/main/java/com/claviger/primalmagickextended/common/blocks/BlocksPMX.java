package com.claviger.primalmagickextended.common.blocks;

import com.claviger.primalmagickextended.common.blocks.portals.EnhancerRuneBlock;
import com.claviger.primalmagickextended.common.blocks.portals.GateRuneBlock;
import com.claviger.primalmagickextended.common.blocks.portals.HasteRuneBlock;
import com.claviger.primalmagickextended.common.blocks.portals.InfinityRuneBlock;
import com.claviger.primalmagickextended.common.blocks.portals.PortalBlock;
import com.claviger.primalmagickextended.common.blocks.portals.StrongEnhancerRuneBlock;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.verdantartifice.primalmagick.common.registries.IRegistryItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

/**
 * Deferred registry for the mod's blocks: one portal block per {@link PortalColor} and the five portal rune blocks.
 * The property values are Custom Portals'.
 */
public class BlocksPMX {
    public static void init() {
        // Pass the service initialization through this class so it gets class loaded and fields registered
        ServicesPMX.BLOCKS_REGISTRY.init();
    }

    private static final ToIntFunction<BlockState> STATE_TO_LUMINANCE = state -> state.getValue(BlockStateProperties.LIT) ? 11 : 0;
    private static final ToIntFunction<BlockState> RUNE_LUMINANCE = state -> 2;

    /** Fresh instance per block: block properties must not be shared. */
    private static BlockBehaviour.Properties runeProperties() {
        return BlockBehaviour.Properties.of().sound(SoundType.AMETHYST_CLUSTER)
                .noOcclusion().lightLevel(RUNE_LUMINANCE).noCollission().strength(0.3F);
    }

    private static BlockBehaviour.Properties portalProperties(MapColor color, boolean emitsLight) {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of().mapColor(color)
                .noOcclusion().noCollission().randomTicks().strength(-1.0F).sound(SoundType.GLASS);
        return emitsLight ? properties.lightLevel(STATE_TO_LUMINANCE) : properties;
    }

    // Runes
    public static final IRegistryItem<Block, HasteRuneBlock> HASTE_RUNE_BLOCK = register("haste_rune_block", () -> new HasteRuneBlock(runeProperties()));
    public static final IRegistryItem<Block, GateRuneBlock> GATE_RUNE_BLOCK = register("gate_rune_block", () -> new GateRuneBlock(runeProperties()));
    public static final IRegistryItem<Block, EnhancerRuneBlock> WEAK_ENHANCER_RUNE_BLOCK = register("weak_enhancer_rune_block", () -> new EnhancerRuneBlock(runeProperties()));
    public static final IRegistryItem<Block, StrongEnhancerRuneBlock> STRONG_ENHANCER_RUNE_BLOCK = register("strong_enhancer_rune_block", () -> new StrongEnhancerRuneBlock(runeProperties()));
    public static final IRegistryItem<Block, InfinityRuneBlock> INFINITY_RUNE_BLOCK = register("infinity_rune_block", () -> new InfinityRuneBlock(runeProperties()));

    // Portal blocks, one per colour (black portals do not emit light, as in Custom Portals)
    private static final Map<PortalColor, IRegistryItem<Block, PortalBlock>> PORTALS = registerPortals();

    private static Map<PortalColor, IRegistryItem<Block, PortalBlock>> registerPortals() {
        Map<PortalColor, IRegistryItem<Block, PortalBlock>> portals = new EnumMap<>(PortalColor.class);
        for (PortalColor color : PortalColor.values()) {
            portals.put(color, register(color.getSerializedName() + "_portal",
                    () -> new PortalBlock(portalProperties(color.mapColor(), color.emitsLight()))));
        }
        return Collections.unmodifiableMap(portals);
    }

    /** The portal block of the given colour. */
    public static PortalBlock portal(PortalColor color) {
        return PORTALS.get(color).get();
    }

    /** The registry entries of all sixteen portal blocks, in {@link PortalColor} order. */
    public static Collection<IRegistryItem<Block, PortalBlock>> portals() {
        return PORTALS.values();
    }

    private static <T extends Block> IRegistryItem<Block, T> register(String name, Supplier<T> blockSupplier) {
        return ServicesPMX.BLOCKS_REGISTRY.register(name, blockSupplier);
    }
}
