package com.claviger.primalmagickextended.common.blocks.portals;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Gate Rune: lets its portal link across dimensions. Adding or removing it relinks the portal, since it changes which
 * portals can link. Ported from Custom Portals.
 */
public class GateRuneBlock extends AbstractRuneBlock {
    public static final MapCodec<GateRuneBlock> CODEC = simpleCodec(GateRuneBlock::new);

    @Override
    public MapCodec<GateRuneBlock> codec() {
        return CODEC;
    }

    public GateRuneBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }
}
