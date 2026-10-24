package com.claviger.primalmagickextended.common.blocks.portals;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Portal Enhancer Rune: extends its portal's link range to the enhancer range. Adding or removing it
 * relinks the portal, since it changes which portals can link. Ported from Custom Portals.
 */
public class EnhancerRuneBlock extends AbstractRuneBlock {
    public static final MapCodec<EnhancerRuneBlock> CODEC = simpleCodec(EnhancerRuneBlock::new);

    @Override
    public MapCodec<EnhancerRuneBlock> codec() {
        return CODEC;
    }

    public EnhancerRuneBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }
}
