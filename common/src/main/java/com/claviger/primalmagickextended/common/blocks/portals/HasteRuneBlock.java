package com.claviger.primalmagickextended.common.blocks.portals;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Haste Rune: makes travel through its portal, in either direction, take one tick. Ported from Custom Portals.
 */
public class HasteRuneBlock extends AbstractRuneBlock {
    public static final MapCodec<HasteRuneBlock> CODEC = simpleCodec(HasteRuneBlock::new);

    @Override
    public MapCodec<HasteRuneBlock> codec() {
        return CODEC;
    }

    public HasteRuneBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }
}
