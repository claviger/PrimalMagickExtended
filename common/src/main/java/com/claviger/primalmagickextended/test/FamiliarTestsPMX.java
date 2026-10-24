package com.claviger.primalmagickextended.test;

import com.claviger.primalmagickextended.test.familiars.FamiliarCollarTests;
import com.claviger.primalmagickextended.test.familiars.FamiliarCommandTests;
import com.claviger.primalmagickextended.test.familiars.FamiliarDataTests;
import com.claviger.primalmagickextended.test.familiars.FamiliarManaEfficiencyTests;
import com.claviger.primalmagickextended.test.familiars.FamiliarManagerTests;
import com.claviger.primalmagickextended.test.familiars.FamiliarPolymorphTests;
import com.claviger.primalmagickextended.test.familiars.FamiliarProtectionTests;
import com.claviger.primalmagickextended.test.familiars.FamiliarResearchTests;
import com.claviger.primalmagickextended.test.familiars.FindFamiliarPayloadTests;

import java.util.ArrayList;
import java.util.List;

/**
 * Game test table for the familiars feature.
 */
public class FamiliarTestsPMX {
    public static List<TestEntryPMX> entries() {
        List<TestEntryPMX> entries = new ArrayList<>();
        entries.add(TestEntryPMX.of("familiar_collar_item_is_registered", FamiliarCollarTests::familiar_collar_item_is_registered));
        entries.add(TestEntryPMX.of("research_entries_are_registered_with_expected_parents", FamiliarResearchTests::research_entries_are_registered_with_expected_parents));
        entries.add(TestEntryPMX.of("ritual_recipe_has_expected_ingredients_and_requirement", FamiliarResearchTests::ritual_recipe_has_expected_ingredients_and_requirement));
        entries.add(TestEntryPMX.of("ritual_recipe_requirement_unmet_without_research", FamiliarResearchTests::ritual_recipe_requirement_unmet_without_research));
        entries.add(TestEntryPMX.of("ritual_altar_finish_craft_yields_familiar_collar", FamiliarResearchTests::ritual_altar_finish_craft_yields_familiar_collar));
        entries.add(TestEntryPMX.of("familiar_collar_affinity_has_expected_sources", FamiliarResearchTests::familiar_collar_affinity_has_expected_sources));
        entries.add(TestEntryPMX.of("player_familiar_round_trips_through_codec", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarDataTests::player_familiar_round_trips_through_codec));
        entries.add(TestEntryPMX.of("player_familiar_attachment_is_available_and_empty_by_default", FamiliarDataTests::player_familiar_attachment_is_available_and_empty_by_default));
        entries.add(TestEntryPMX.of("entity_binding_can_be_set_read_and_cleared", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarDataTests::entity_binding_can_be_set_read_and_cleared));
        entries.add(TestEntryPMX.of("entity_binding_survives_entity_save_and_load", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarDataTests::entity_binding_survives_entity_save_and_load));
        entries.add(TestEntryPMX.of("sync_familiar_packet_round_trips_through_stream_codec", FamiliarDataTests::sync_familiar_packet_round_trips_through_stream_codec));
        entries.add(TestEntryPMX.of("bind_marks_pet_and_stores_snapshot", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManagerTests::bind_marks_pet_and_stores_snapshot));
        entries.add(TestEntryPMX.of("bind_does_not_consume_collar_in_creative", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManagerTests::bind_does_not_consume_collar_in_creative));
        entries.add(TestEntryPMX.of("dismiss_removes_entity_and_updates_snapshot", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManagerTests::dismiss_removes_entity_and_updates_snapshot));
        entries.add(TestEntryPMX.of("summon_restores_familiar_beside_player_with_name_and_new_identity", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManagerTests::summon_restores_familiar_beside_player_with_name_and_new_identity));
        entries.add(TestEntryPMX.of("toggle_dismisses_when_in_play_and_summons_when_absent", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManagerTests::toggle_dismisses_when_in_play_and_summons_when_absent));
        entries.add(TestEntryPMX.of("toggle_twice_in_one_tick_toggles_once", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManagerTests::toggle_twice_in_one_tick_toggles_once));
        entries.add(TestEntryPMX.of("toggle_within_debounce_window_is_ignored", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManagerTests::toggle_within_debounce_window_is_ignored));
        entries.add(TestEntryPMX.of("summon_with_no_familiar_sends_message", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManagerTests::summon_with_no_familiar_sends_message));
        entries.add(TestEntryPMX.of("dismiss_ejects_riding_owner", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManagerTests::dismiss_ejects_riding_owner));
        entries.add(TestEntryPMX.of("bind_snapshot_strips_passengers", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManagerTests::bind_snapshot_strips_passengers));
        entries.add(TestEntryPMX.of("summon_when_in_play_does_not_duplicate", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManagerTests::summon_when_in_play_does_not_duplicate));
        entries.add(TestEntryPMX.of("summon_clears_harmful_effects", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManagerTests::summon_clears_harmful_effects));
        entries.add(TestEntryPMX.of("dismiss_stores_live_entity_type", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManagerTests::dismiss_stores_live_entity_type));
        entries.add(TestEntryPMX.of("collar_binds_own_tamed_wolf", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCollarTests::collar_binds_own_tamed_wolf));
        entries.add(TestEntryPMX.of("collar_binds_own_tamed_horse", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCollarTests::collar_binds_own_tamed_horse));
        entries.add(TestEntryPMX.of("collar_binds_trusting_fox", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCollarTests::collar_binds_trusting_fox));
        entries.add(TestEntryPMX.of("collar_ignores_untrusting_fox", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCollarTests::collar_ignores_untrusting_fox));
        entries.add(TestEntryPMX.of("collar_ignores_untamed_wolf", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCollarTests::collar_ignores_untamed_wolf));
        entries.add(TestEntryPMX.of("collar_ignores_wolf_owned_by_another_player", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCollarTests::collar_ignores_wolf_owned_by_another_player));
        entries.add(TestEntryPMX.of("collar_ignores_non_candidate_animal", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCollarTests::collar_ignores_non_candidate_animal));
        entries.add(TestEntryPMX.of("collar_on_own_familiar_does_nothing", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCollarTests::collar_on_own_familiar_does_nothing));
        entries.add(TestEntryPMX.of("collar_on_another_players_familiar_does_nothing", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCollarTests::collar_on_another_players_familiar_does_nothing));
        entries.add(TestEntryPMX.of("collar_converts_ocelot_into_ocelot_cat_familiar", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCollarTests::collar_converts_ocelot_into_ocelot_cat_familiar));
        entries.add(TestEntryPMX.of("ocelot_cat_variant_is_registered", FamiliarCollarTests::ocelot_cat_variant_is_registered));
        entries.add(TestEntryPMX.of("collar_names_unnamed_ocelot_cat_ocelot", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCollarTests::collar_names_unnamed_ocelot_cat_ocelot));
        entries.add(TestEntryPMX.of("collar_keeps_name_tag_name_when_converting_ocelot", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCollarTests::collar_keeps_name_tag_name_when_converting_ocelot));
        entries.add(TestEntryPMX.of("collar_converts_leashed_ocelot_keeping_leash", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCollarTests::collar_converts_leashed_ocelot_keeping_leash));
        entries.add(TestEntryPMX.of("collar_converts_ridden_ocelot_keeping_rider_and_movement_state", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCollarTests::collar_converts_ridden_ocelot_keeping_rider_and_movement_state));
        entries.add(TestEntryPMX.of("failed_ocelot_conversion_keeps_leash", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCollarTests::failed_ocelot_conversion_keeps_leash));
        entries.add(TestEntryPMX.of("client_side_collar_use_is_predicted_for_owned_pet", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCollarTests::client_side_collar_use_is_predicted_for_owned_pet));
        entries.add(TestEntryPMX.of("familiar_dismisses_instead_of_taking_damage", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::familiar_dismisses_instead_of_taking_damage));
        entries.add(TestEntryPMX.of("familiar_dismisses_on_generic_kill", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::familiar_dismisses_on_generic_kill));
        entries.add(TestEntryPMX.of("familiar_dismisses_on_fire_damage", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::familiar_dismisses_on_fire_damage));
        entries.add(TestEntryPMX.of("binding_new_familiar_releases_old_one_unharmed", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::binding_new_familiar_releases_old_one_unharmed));
        entries.add(TestEntryPMX.of("binding_new_familiar_summons_dismissed_old_one_first", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::binding_new_familiar_summons_dismissed_old_one_first));
        entries.add(TestEntryPMX.of("stale_generation_familiar_is_removed_on_join", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::stale_generation_familiar_is_removed_on_join));
        entries.add(TestEntryPMX.of("current_generation_familiar_is_adopted_on_join", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::current_generation_familiar_is_adopted_on_join));
        entries.add(TestEntryPMX.of("duplicate_familiar_is_removed_on_join", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::duplicate_familiar_is_removed_on_join));
        entries.add(TestEntryPMX.of("familiar_with_offline_owner_is_removed_on_join", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::familiar_with_offline_owner_is_removed_on_join));
        entries.add(TestEntryPMX.of("logout_dismisses_familiar_in_play", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::logout_dismisses_familiar_in_play));
        entries.add(TestEntryPMX.of("chunk_unload_stores_familiar_and_rotates_generation", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::chunk_unload_stores_familiar_and_rotates_generation));
        entries.add(TestEntryPMX.of("chunk_hidden_leave_stores_familiar_without_removing_it", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::chunk_hidden_leave_stores_familiar_without_removing_it));
        entries.add(TestEntryPMX.of("leave_level_ignores_discarded_familiar", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::leave_level_ignores_discarded_familiar));
        entries.add(TestEntryPMX.of("stale_generation_familiar_is_rejected_by_real_join_event", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::stale_generation_familiar_is_rejected_by_real_join_event));
        entries.add(TestEntryPMX.of("stale_bound_mob_is_discarded_by_periodic_check", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::stale_bound_mob_is_discarded_by_periodic_check));
        entries.add(TestEntryPMX.of("death_safety_net_dismisses_familiar", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::death_safety_net_dismisses_familiar));
        entries.add(TestEntryPMX.of("zero_damage_hit_does_not_dismiss", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::zero_damage_hit_does_not_dismiss));
        entries.add(TestEntryPMX.of("stale_familiar_in_boat_is_swept", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::stale_familiar_in_boat_is_swept));
        entries.add(TestEntryPMX.of("find_familiar_payload_is_registered", FindFamiliarPayloadTests::find_familiar_payload_is_registered));
        entries.add(TestEntryPMX.of("find_familiar_payload_toggles_for_caster", TestUtilsPMX.FLOOR_TEMPLATE, FindFamiliarPayloadTests::find_familiar_payload_toggles_for_caster));
        entries.add(TestEntryPMX.of("find_familiar_payload_ignores_burst_points", TestUtilsPMX.FLOOR_TEMPLATE, FindFamiliarPayloadTests::find_familiar_payload_ignores_burst_points));
        entries.add(TestEntryPMX.of("find_familiar_payload_with_no_familiar_does_nothing", TestUtilsPMX.FLOOR_TEMPLATE, FindFamiliarPayloadTests::find_familiar_payload_with_no_familiar_does_nothing));
        entries.add(TestEntryPMX.of("find_familiar_payload_hidden_without_research_and_shown_with_it", FindFamiliarPayloadTests::find_familiar_payload_hidden_without_research_and_shown_with_it));
        entries.add(TestEntryPMX.of("find_familiar_wand_cast_deducts_mana_and_dismisses", TestUtilsPMX.FLOOR_TEMPLATE, FindFamiliarPayloadTests::find_familiar_wand_cast_deducts_mana_and_dismisses));
        entries.add(TestEntryPMX.of("find_familiar_mine_does_not_toggle", TestUtilsPMX.FLOOR_TEMPLATE, FindFamiliarPayloadTests::find_familiar_mine_does_not_toggle));
        entries.add(TestEntryPMX.of("find_familiar_spell_package_round_trips_through_codecs", FindFamiliarPayloadTests::find_familiar_spell_package_round_trips_through_codecs));
        entries.add(TestEntryPMX.of("familiar_nearby_adds_five_to_wand_cost_modifier", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManaEfficiencyTests::familiar_nearby_adds_five_to_wand_cost_modifier));
        entries.add(TestEntryPMX.of("familiar_far_away_adds_nothing", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManaEfficiencyTests::familiar_far_away_adds_nothing));
        entries.add(TestEntryPMX.of("dismissed_familiar_adds_nothing", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManaEfficiencyTests::dismissed_familiar_adds_nothing));
        entries.add(TestEntryPMX.of("familiar_bonus_reduces_spell_mana_consumed", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarManaEfficiencyTests::familiar_bonus_reduces_spell_mana_consumed));
        entries.add(TestEntryPMX.of("polymorph_swap_back_keeps_current_binding", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarPolymorphTests::polymorph_swap_back_keeps_current_binding));
        entries.add(TestEntryPMX.of("release_rewrites_queued_swapper_binding", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarPolymorphTests::release_rewrites_queued_swapper_binding));
        entries.add(TestEntryPMX.of("release_with_unrestorable_snapshot_clears_familiar_and_allows_bind", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::release_with_unrestorable_snapshot_clears_familiar_and_allows_bind));
        entries.add(TestEntryPMX.of("release_with_refused_join_aborts_bind_and_keeps_familiar", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarProtectionTests::release_with_refused_join_aborts_bind_and_keeps_familiar));
        entries.add(TestEntryPMX.of("summon_command_summons_dismissed_familiar", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCommandTests::summon_command_summons_dismissed_familiar));
        entries.add(TestEntryPMX.of("summon_command_refuses_when_familiar_is_already_in_play", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCommandTests::summon_command_refuses_when_familiar_is_already_in_play));
        entries.add(TestEntryPMX.of("dismiss_command_dismisses_familiar_in_play", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCommandTests::dismiss_command_dismisses_familiar_in_play));
        entries.add(TestEntryPMX.of("clear_command_reverts_familiar_in_play_to_ordinary_pet", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCommandTests::clear_command_reverts_familiar_in_play_to_ordinary_pet));
        entries.add(TestEntryPMX.of("clear_command_restores_dismissed_familiar_as_ordinary_pet", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCommandTests::clear_command_restores_dismissed_familiar_as_ordinary_pet));
        entries.add(TestEntryPMX.of("commands_fail_when_there_is_no_familiar", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCommandTests::commands_fail_when_there_is_no_familiar));
        entries.add(TestEntryPMX.of("commands_require_game_master_permission_and_accept_targets_and_alias", TestUtilsPMX.FLOOR_TEMPLATE, FamiliarCommandTests::commands_require_game_master_permission_and_accept_targets_and_alias));

        return entries;
    }
}
