package com.claviger.primalmagickextended.test;

import net.minecraft.core.Vec3i;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

/**
 * Tests that the mod's game test templates load and contain what the tests built on them expect. Each test runs in
 * the template it checks.
 */
public class TestTemplateTests extends AbstractBaseTestPMX {
    public static void default_template_is_empty_3x3x3(GameTestHelper helper) {
        assertTemplate(helper, TestUtilsPMX.DEFAULT_TEMPLATE, new Vec3i(3, 3, 3), false);
    }

    public static void floor_template_is_floored_5x5x5(GameTestHelper helper) {
        assertTemplate(helper, TestUtilsPMX.FLOOR_TEMPLATE, new Vec3i(5, 5, 5), true);
    }

    public static void large_floor_template_is_floored_7x10x7(GameTestHelper helper) {
        assertTemplate(helper, TestUtilsPMX.LARGE_FLOOR_TEMPLATE, new Vec3i(7, 10, 7), true);
    }

    /**
     * Asserts that a template has the given size and that the test area holds only air, above a full layer of stone
     * if the template is floored.
     */
    private static void assertTemplate(GameTestHelper helper, String template, Vec3i size, boolean floored) {
        Optional<StructureTemplate> loaded = helper.getLevel().getStructureManager().get(ResourceLocation.parse(template));
        assertTrue(helper, loaded.isPresent(), template + " did not load");
        assertValueEqual(helper, size, loaded.get().getSize(), template + " size");
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    Block expected = floored && y == 0 ? Blocks.STONE : Blocks.AIR;
                    helper.assertBlockPresent(expected, TestUtilsPMX.TEMPLATE_ORIGIN.offset(x, y, z));
                }
            }
        }
        helper.succeed();
    }
}
