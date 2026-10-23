package com.claviger.primalmagickextended.test;

import com.verdantartifice.primalmagick.test.AbstractBaseTest;
import net.minecraft.gametest.framework.GameTestHelper;

/**
 * Smoke test proving that the mod's test instances are discovered and executed.
 */
public abstract class CanaryTest extends AbstractBaseTest {
    public static void canary(GameTestHelper helper) {
        helper.succeed();
    }
}
