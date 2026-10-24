package com.claviger.primalmagickextended.common.blocks.portals;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Infinite Enhancer Rune: removes the link range limit within a dimension. Adding or removing it retries linking,
 * since it changes which portals can link. Ported from Custom Portals.
 */
public class InfinityRuneBlock extends AbstractRuneBlock {
    public static final MapCodec<InfinityRuneBlock> CODEC = simpleCodec(InfinityRuneBlock::new);

    @Override
    public MapCodec<InfinityRuneBlock> codec() {
        return CODEC;
    }

    public InfinityRuneBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }
}
