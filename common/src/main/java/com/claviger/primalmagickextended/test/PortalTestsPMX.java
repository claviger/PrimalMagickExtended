package com.claviger.primalmagickextended.test;

import com.claviger.primalmagickextended.test.portals.PortalCodecTests;
import com.claviger.primalmagickextended.test.portals.PortalConfigTests;
import com.claviger.primalmagickextended.test.portals.PortalFixTests;
import com.claviger.primalmagickextended.test.portals.PortalLinkTests;
import com.claviger.primalmagickextended.test.portals.PortalPersistenceTests;
import com.claviger.primalmagickextended.test.portals.PortalResearchTests;
import com.claviger.primalmagickextended.test.portals.PortalRegistrationTests;
import com.claviger.primalmagickextended.test.portals.PortalTravelTests;
import com.claviger.primalmagickextended.test.portals.PortalWorldTests;

import java.util.ArrayList;
import java.util.List;

/**
 * Game test table for the portals feature, grouped by area.
 */
public class PortalTestsPMX {
    public static List<TestEntryPMX> entries() {
        List<TestEntryPMX> entries = new ArrayList<>();

        // Configuration
        entries.add(TestEntryPMX.of("config_service_reports_portal_defaults", PortalConfigTests::config_service_reports_portal_defaults));

        // Linking, codecs, persistence, registration, world and fixes
        entries.add(TestEntryPMX.of("same_colour_and_frame_within_default_range_link", PortalLinkTests::same_colour_and_frame_within_default_range_link));
        entries.add(TestEntryPMX.of("beyond_default_range_does_not_link", PortalLinkTests::beyond_default_range_does_not_link));
        entries.add(TestEntryPMX.of("weak_enhancer_extends_range_to_one_thousand", PortalLinkTests::weak_enhancer_extends_range_to_one_thousand));
        entries.add(TestEntryPMX.of("strong_enhancer_extends_range_to_ten_thousand", PortalLinkTests::strong_enhancer_extends_range_to_ten_thousand));
        entries.add(TestEntryPMX.of("infinity_rune_has_unlimited_range", PortalLinkTests::infinity_rune_has_unlimited_range));
        entries.add(TestEntryPMX.of("different_colour_does_not_link", PortalLinkTests::different_colour_does_not_link));
        entries.add(TestEntryPMX.of("different_frame_does_not_link", PortalLinkTests::different_frame_does_not_link));
        entries.add(TestEntryPMX.of("cross_dimension_needs_gate_rune", PortalLinkTests::cross_dimension_needs_gate_rune));
        entries.add(TestEntryPMX.of("nether_coordinates_scale_by_eight", PortalLinkTests::nether_coordinates_scale_by_eight));
        entries.add(TestEntryPMX.of("portal_linked_elsewhere_is_not_stolen", PortalLinkTests::portal_linked_elsewhere_is_not_stolen));
        entries.add(TestEntryPMX.of("haste_rune_sets_one_tick_delay", PortalLinkTests::haste_rune_sets_one_tick_delay));
        entries.add(TestEntryPMX.of("registry_unregister_relinks_partner", PortalLinkTests::registry_unregister_relinks_partner));
        entries.add(TestEntryPMX.of("custom_portal_round_trips_through_codec", PortalCodecTests::custom_portal_round_trips_through_codec));
        entries.add(TestEntryPMX.of("custom_portal_round_trips_through_stream_codec", PortalCodecTests::custom_portal_round_trips_through_stream_codec));
        entries.add(TestEntryPMX.of("stream_decoding_rejects_impossible_counts", PortalCodecTests::stream_decoding_rejects_impossible_counts));
        entries.add(TestEntryPMX.of("saved_data_relinks_portals_on_load", PortalPersistenceTests::saved_data_relinks_portals_on_load));
        entries.add(TestEntryPMX.of("sync_packet_round_trips_portals", PortalPersistenceTests::sync_packet_round_trips_portals));
        entries.add(TestEntryPMX.of("portal_data_resolves_server_registry", PortalPersistenceTests::portal_data_resolves_server_registry));
        entries.add(TestEntryPMX.of("portal_blocks_and_items_are_registered", PortalRegistrationTests::portal_blocks_and_items_are_registered));
        entries.add(TestEntryPMX.of("rune_blocks_and_items_are_registered", PortalRegistrationTests::rune_blocks_and_items_are_registered));
        entries.add(TestEntryPMX.of("portal_block_entity_and_particles_are_registered", PortalRegistrationTests::portal_block_entity_and_particles_are_registered));
        entries.add(TestEntryPMX.of("catalyst_builds_portal_in_closed_frame", TestUtilsPMX.LARGE_FLOOR_TEMPLATE, PortalWorldTests::catalyst_builds_portal_in_closed_frame));
        entries.add(TestEntryPMX.of("catalyst_fails_on_mixed_frame", TestUtilsPMX.LARGE_FLOOR_TEMPLATE, PortalWorldTests::catalyst_fails_on_mixed_frame));
        entries.add(TestEntryPMX.of("catalyst_fails_on_open_frame", TestUtilsPMX.LARGE_FLOOR_TEMPLATE, PortalWorldTests::catalyst_fails_on_open_frame));
        entries.add(TestEntryPMX.of("two_matching_portals_link_and_light", TestUtilsPMX.LARGE_FLOOR_TEMPLATE, PortalWorldTests::two_matching_portals_link_and_light));
        entries.add(TestEntryPMX.of("portals_of_different_colours_do_not_link", TestUtilsPMX.LARGE_FLOOR_TEMPLATE, PortalWorldTests::portals_of_different_colours_do_not_link));
        entries.add(TestEntryPMX.of("portals_of_different_frames_do_not_link", TestUtilsPMX.LARGE_FLOOR_TEMPLATE, PortalWorldTests::portals_of_different_frames_do_not_link));
        entries.add(TestEntryPMX.of("breaking_frame_collapses_portal_and_drops_catalyst", TestUtilsPMX.LARGE_FLOOR_TEMPLATE, PortalWorldTests::breaking_frame_collapses_portal_and_drops_catalyst));
        entries.add(TestEntryPMX.of("placing_and_breaking_rune_updates_portal", TestUtilsPMX.LARGE_FLOOR_TEMPLATE, PortalWorldTests::placing_and_breaking_rune_updates_portal));
        entries.add(TestEntryPMX.of("sneak_use_sets_arrival_point", TestUtilsPMX.LARGE_FLOOR_TEMPLATE, PortalWorldTests::sneak_use_sets_arrival_point));
        entries.add(TestEntryPMX.of("rune_removed_without_player_updates_count", TestUtilsPMX.LARGE_FLOOR_TEMPLATE, PortalFixTests::rune_removed_without_player_updates_count));
        entries.add(TestEntryPMX.of("rune_set_by_command_is_counted", TestUtilsPMX.LARGE_FLOOR_TEMPLATE, PortalFixTests::rune_set_by_command_is_counted));
        entries.add(TestEntryPMX.of("rune_recount_relinks_portal", TestUtilsPMX.LARGE_FLOOR_TEMPLATE, PortalFixTests::rune_recount_relinks_portal));
        entries.add(TestEntryPMX.of("powered_portal_stays_dark_after_reload", TestUtilsPMX.LARGE_FLOOR_TEMPLATE, PortalFixTests::powered_portal_stays_dark_after_reload));


        // Travel
        entries.add(TestEntryPMX.of("entity_teleports_through_linked_portal", TestUtilsPMX.LARGE_FLOOR_TEMPLATE, PortalTravelTests::entity_teleports_through_linked_portal));
        entries.add(TestEntryPMX.of("player_custom_portal_travel_uses_dimension_change_flow", TestUtilsPMX.LARGE_FLOOR_TEMPLATE, PortalTravelTests::player_custom_portal_travel_uses_dimension_change_flow));


        // Research, recipes and loot
        entries.add(TestEntryPMX.of("portal_research_entries_have_expected_structure", PortalResearchTests::portal_research_entries_have_expected_structure));
        entries.add(TestEntryPMX.of("portal_travel_is_opaque", PortalResearchTests::portal_travel_is_opaque));
        entries.add(TestEntryPMX.of("catalyst_recipes_are_arcane_and_gated", PortalResearchTests::catalyst_recipes_are_arcane_and_gated));
        entries.add(TestEntryPMX.of("rune_recipes_are_arcane_and_gated", PortalResearchTests::rune_recipes_are_arcane_and_gated));
        entries.add(TestEntryPMX.of("recolour_recipes_exist_for_every_colour", PortalResearchTests::recolour_recipes_exist_for_every_colour));
        entries.add(TestEntryPMX.of("no_vanilla_recipe_crafts_portal_items_except_recolouring", PortalResearchTests::no_vanilla_recipe_crafts_portal_items_except_recolouring));
        entries.add(TestEntryPMX.of("rune_block_loot_drops_rune", PortalResearchTests::rune_block_loot_drops_rune));

        return entries;
    }
}
