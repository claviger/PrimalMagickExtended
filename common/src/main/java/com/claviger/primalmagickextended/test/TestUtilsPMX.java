package com.claviger.primalmagickextended.test;

import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import net.minecraft.core.BlockPos;

/**
 * Shared constants for this mod's game tests. The structure templates ship in this mod's namespace, because the
 * game test namespace filter only keeps tests whose template is in an enabled namespace; the first two are copies of
 * Primal Magick's.
 */
public class TestUtilsPMX {
    /** 3x3x3 enclosed template, copied from Primal Magick; the default for small tests. */
    public static final String DEFAULT_TEMPLATE = ResourceUtilsPMX.loc("test/empty3x3x3").toString();
    /** 5x5x5 floored template, copied from Primal Magick; use for tests that spawn mobs. */
    public static final String FLOOR_TEMPLATE = ResourceUtilsPMX.loc("test/floor5x5x5").toString();
    /** 7x10x7 floored template; room for two portal frames side by side. */
    public static final String LARGE_FLOOR_TEMPLATE = ResourceUtilsPMX.loc("test/floor7x10x7").toString();

    /**
     * Where a template's own origin is, in the test-relative coordinates that {@code GameTestHelper} takes. On 1.21.1
     * those are relative to the test's structure block, which sits one block below the template, so a floored
     * template's floor is at relative y = 1 and the air above it starts at y = 2. (On 26.1 they were relative to the
     * template itself.)
     */
    public static final BlockPos TEMPLATE_ORIGIN = new BlockPos(0, 1, 0);

    /** Ticks a test may run before it times out. */
    public static final int MAX_TICKS = 400;
    /** Ticks between placing a test's template and starting the test. */
    public static final long SETUP_TICKS = 50L;
    /**
     * Attempts a test gets. One, unlike the three on 26.1: the 1.21.1 game test server only waits for a test's first
     * attempt, so retries would never count, and a failed test with retries is not logged.
     */
    public static final int MAX_ATTEMPTS = 1;
}
