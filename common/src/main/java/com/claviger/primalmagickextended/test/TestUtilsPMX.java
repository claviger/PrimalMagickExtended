package com.claviger.primalmagickextended.test;

import com.verdantartifice.primalmagick.common.util.ResourceUtils;
import net.minecraft.resources.Identifier;

/**
 * Shared constants for this mod's game tests. Templates are reused from Primal Magick's jar.
 */
public class TestUtilsPMX {
    /** 3x3x3 enclosed template from Primal Magick; the default for small tests. */
    public static final Identifier DEFAULT_TEMPLATE = ResourceUtils.loc("test/empty3x3x3");
    /** 5x5x5 floored template from Primal Magick; use for tests that spawn mobs. */
    public static final Identifier FLOOR_TEMPLATE = ResourceUtils.loc("test/floor5x5x5");
    /** 7x10x7 floored template from Primal Magick; room for two portal frames side by side. */
    public static final Identifier LARGE_FLOOR_TEMPLATE = ResourceUtils.loc("test/floor7x10x7");
}
