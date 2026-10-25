package com.claviger.primalmagickextended.test;

import com.claviger.primalmagickextended.test.affinities.GeneratedAffinityTests;

import java.util.ArrayList;
import java.util.List;

public class AffinityTestsPMX {
    public static List<TestEntryPMX> entries() {
        List<TestEntryPMX> entries = new ArrayList<>();
        entries.add(TestEntryPMX.of("generated_affinities_are_registered_and_yield_to_data", GeneratedAffinityTests::generated_affinities_are_registered_and_yield_to_data));
        entries.add(TestEntryPMX.of("rounded_down_crafts_of_generated_items_get_one_of_each_source", GeneratedAffinityTests::rounded_down_crafts_of_generated_items_get_one_of_each_source));
        entries.add(TestEntryPMX.of("lint_still_lists_items_with_only_a_generated_affinity", GeneratedAffinityTests::lint_still_lists_items_with_only_a_generated_affinity));

        return entries;
    }
}
