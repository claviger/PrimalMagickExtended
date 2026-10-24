package com.claviger.primalmagickextended.test;

import com.claviger.primalmagickextended.test.features.FeatureConditionTests;
import com.claviger.primalmagickextended.test.features.FeatureToggleNeoforgeTests;

import java.util.ArrayList;
import java.util.List;

/**
 * Game test table for the feature toggles' NeoForge-only code: load conditions and config specs.
 */
public class FeatureTestsPMXNeoforge {
    public static List<TestEntryPMX> entries() {
        List<TestEntryPMX> entries = new ArrayList<>();
        entries.add(TestEntryPMX.of("feature_condition_follows_config", FeatureConditionTests::feature_condition_follows_config));
        entries.add(TestEntryPMX.of("feature_condition_keeps_entries_listed_by_server", FeatureConditionTests::feature_condition_keeps_entries_listed_by_server));
        entries.add(TestEntryPMX.of("plain_registry_ops_have_no_condition_context", FeatureConditionTests::plain_registry_ops_have_no_condition_context));
        entries.add(TestEntryPMX.of("feature_condition_round_trips_through_codec", FeatureConditionTests::feature_condition_round_trips_through_codec));
        entries.add(TestEntryPMX.of("conditioned_research_entry_loads_while_enabled", FeatureConditionTests::conditioned_research_entry_loads_while_enabled));
        entries.add(TestEntryPMX.of("feature_switches_default_to_enabled", FeatureToggleNeoforgeTests::feature_switches_default_to_enabled));
        entries.add(TestEntryPMX.of("stale_research_key_survives_sync_packet", FeatureToggleNeoforgeTests::stale_research_key_survives_sync_packet));

        return entries;
    }
}
