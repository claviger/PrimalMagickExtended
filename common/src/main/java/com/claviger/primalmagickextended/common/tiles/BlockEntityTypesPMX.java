package com.claviger.primalmagickextended.common.tiles;

import com.claviger.primalmagickextended.common.blocks.BlocksPMX;
import com.claviger.primalmagickextended.common.blocks.portals.PortalBlockEntity;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.verdantartifice.primalmagick.common.registries.IRegistryItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Deferred registry for the mod's block entity types.
 */
public class BlockEntityTypesPMX {
    public static void init() {
        // Pass the service initialization through this class so it gets class loaded and fields registered
        ServicesPMX.BLOCK_ENTITY_TYPES_REGISTRY.init();
    }

    /** Shared by all sixteen portal blocks; ticks the portal's lit state. */
    public static final IRegistryItem<BlockEntityType<?>, BlockEntityType<PortalBlockEntity>> PORTAL = register("portal_block_entity",
            () -> new BlockEntityType<>(PortalBlockEntity::new, BlocksPMX.portals().stream().map(item -> (Block) item.get()).collect(Collectors.toUnmodifiableSet())));

    private static <T extends BlockEntity> IRegistryItem<BlockEntityType<?>, BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> supplier) {
        return ServicesPMX.BLOCK_ENTITY_TYPES_REGISTRY.register(name, supplier);
    }
}
