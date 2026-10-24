package com.claviger.primalmagickextended.test;

import com.claviger.primalmagickextended.test.features.FeatureToggleTests;

import java.util.ArrayList;
import java.util.List;

/**
 * Game test table for the feature toggles. Tests of NeoForge-only code are in the NeoForge module's table.
 */
public class FeatureTestsPMX {
    public static List<TestEntryPMX> entries() {
        List<TestEntryPMX> entries = new ArrayList<>();
        entries.add(TestEntryPMX.of("features_are_enabled_by_default", FeatureToggleTests::features_are_enabled_by_default));
        entries.add(TestEntryPMX.of("every_feature_research_entry_and_recipe_is_conditioned", FeatureToggleTests::every_feature_research_entry_and_recipe_is_conditioned));
        entries.add(TestEntryPMX.of("opaque_tag_reference_is_optional", FeatureToggleTests::opaque_tag_reference_is_optional));
        entries.add(TestEntryPMX.of("stale_research_and_recipe_keys_are_harmless", FeatureToggleTests::stale_research_and_recipe_keys_are_harmless));
        entries.add(TestEntryPMX.of("stale_grimoire_topics_are_cleared", FeatureToggleTests::stale_grimoire_topics_are_cleared));
        entries.add(TestEntryPMX.of("find_familiar_payload_needs_its_entry_loaded", FeatureToggleTests::find_familiar_payload_needs_its_entry_loaded));

        return entries;
    }
}
