package com.claviger.primalmagickextended.common.blocks.portals;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Strong Enhancer Rune: extends its portal's link range to the strong enhancer range. Adding or removing it
 * relinks the portal, since it changes which portals can link. Ported from Custom Portals.
 */
public class StrongEnhancerRuneBlock extends AbstractRuneBlock {
    public static final MapCodec<StrongEnhancerRuneBlock> CODEC = simpleCodec(StrongEnhancerRuneBlock::new);

    @Override
    public MapCodec<StrongEnhancerRuneBlock> codec() {
        return CODEC;
    }

    public StrongEnhancerRuneBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }
}
