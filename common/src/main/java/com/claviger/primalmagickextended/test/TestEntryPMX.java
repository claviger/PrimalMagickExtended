package com.claviger.primalmagickextended.test;

import com.claviger.primalmagickextended.ConstantsPMX;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;

import java.util.function.Consumer;

/**
 * One row of the mod's game test tables: a test function, its name and its structure template. The NeoForge game
 * test holder turns every row into a {@link TestFunction}.
 *
 * @param name     the test's name, unique within the mod; the registered test name is prefixed with the mod id
 * @param template the full id of the test's structure template, one of the {@link TestUtilsPMX} templates
 * @param function the test function
 */
public record TestEntryPMX(String name, String template, Consumer<GameTestHelper> function) {
    /**
     * Rejects a template outside this mod's namespace: NeoForge would silently drop such a test rather than run it.
     */
    public TestEntryPMX {
        String namespace = ResourceLocation.parse(template).getNamespace();
        if (!ConstantsPMX.MOD_ID.equals(namespace)) {
            throw new IllegalArgumentException("Game test " + name + " uses template " + template + " outside the " + ConstantsPMX.MOD_ID + " namespace");
        }
    }

    /** A test in the default 3x3x3 template. */
    public static TestEntryPMX of(String name, Consumer<GameTestHelper> function) {
        return new TestEntryPMX(name, TestUtilsPMX.DEFAULT_TEMPLATE, function);
    }

    /** A test in the given template. */
    public static TestEntryPMX of(String name, String template, Consumer<GameTestHelper> function) {
        return new TestEntryPMX(name, template, function);
    }

    /**
     * The game test for this row. All of the mod's tests share one batch and are required, and they get the timing
     * set in {@link TestUtilsPMX}.
     */
    public TestFunction toTestFunction() {
        return new TestFunction(ConstantsPMX.MOD_ID, ConstantsPMX.MOD_ID + "." + this.name, this.template, Rotation.NONE,
                TestUtilsPMX.MAX_TICKS, TestUtilsPMX.SETUP_TICKS, true, false, TestUtilsPMX.MAX_ATTEMPTS, 1, false, this.function);
    }
}
