package com.claviger.primalmagickextended.test;

import net.minecraft.gametest.framework.GameTestHelper;

/**
 * Smoke test proving that the mod's game tests are discovered and executed.
 */
public abstract class CanaryTest extends AbstractBaseTestPMX {
    public static void canary(GameTestHelper helper) {
        helper.succeed();
    }
}
