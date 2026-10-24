package com.claviger.primalmagickextended.test;

import java.util.List;

/**
 * Game test table for the mod's test infrastructure.
 */
public class CoreTestsPMX {
    public static List<TestEntryPMX> entries() {
        return List.of(
                TestEntryPMX.of("canary", CanaryTest::canary),
                TestEntryPMX.of("default_template_is_empty_3x3x3", TestTemplateTests::default_template_is_empty_3x3x3),
                TestEntryPMX.of("floor_template_is_floored_5x5x5", TestUtilsPMX.FLOOR_TEMPLATE, TestTemplateTests::floor_template_is_floored_5x5x5),
                TestEntryPMX.of("large_floor_template_is_floored_7x10x7", TestUtilsPMX.LARGE_FLOOR_TEMPLATE, TestTemplateTests::large_floor_template_is_floored_7x10x7)
        );
    }
}
