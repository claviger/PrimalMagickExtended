package com.claviger.primalmagickextended.test;

import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.verdantartifice.primalmagick.test.TestDataBuilder;
import com.verdantartifice.primalmagick.test.TestEnvironmentsPM;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Consumer;

/**
 * Data-driven game test instances for the mod, generated into data/primalmagickextended/test_instance.
 */
public class TestInstancesPMX {
    public static final ResourceKey<GameTestInstance> CANARY = createInstanceKey("canary");
    public static final ResourceKey<GameTestInstance> FAMILIAR_COLLAR_ITEM_IS_REGISTERED = createInstanceKey("familiar_collar_item_is_registered");
    public static final ResourceKey<GameTestInstance> RESEARCH_ENTRIES_ARE_REGISTERED_WITH_EXPECTED_PARENTS = createInstanceKey("research_entries_are_registered_with_expected_parents");
    public static final ResourceKey<GameTestInstance> RITUAL_RECIPE_HAS_EXPECTED_INGREDIENTS_AND_REQUIREMENT = createInstanceKey("ritual_recipe_has_expected_ingredients_and_requirement");
    public static final ResourceKey<GameTestInstance> RITUAL_RECIPE_REQUIREMENT_UNMET_WITHOUT_RESEARCH = createInstanceKey("ritual_recipe_requirement_unmet_without_research");
    public static final ResourceKey<GameTestInstance> RITUAL_ALTAR_FINISH_CRAFT_YIELDS_FAMILIAR_COLLAR = createInstanceKey("ritual_altar_finish_craft_yields_familiar_collar");
    public static final ResourceKey<GameTestInstance> FAMILIAR_COLLAR_AFFINITY_HAS_EXPECTED_SOURCES = createInstanceKey("familiar_collar_affinity_has_expected_sources");
    public static final ResourceKey<GameTestInstance> PLAYER_FAMILIAR_ROUND_TRIPS_THROUGH_CODEC = createInstanceKey("player_familiar_round_trips_through_codec");
    public static final ResourceKey<GameTestInstance> PLAYER_FAMILIAR_ATTACHMENT_IS_AVAILABLE_AND_EMPTY_BY_DEFAULT = createInstanceKey("player_familiar_attachment_is_available_and_empty_by_default");
    public static final ResourceKey<GameTestInstance> ENTITY_BINDING_CAN_BE_SET_READ_AND_CLEARED = createInstanceKey("entity_binding_can_be_set_read_and_cleared");
    public static final ResourceKey<GameTestInstance> ENTITY_BINDING_SURVIVES_ENTITY_SAVE_AND_LOAD = createInstanceKey("entity_binding_survives_entity_save_and_load");
    public static final ResourceKey<GameTestInstance> BIND_MARKS_PET_AND_STORES_SNAPSHOT = createInstanceKey("bind_marks_pet_and_stores_snapshot");
    public static final ResourceKey<GameTestInstance> BIND_DOES_NOT_CONSUME_COLLAR_IN_CREATIVE = createInstanceKey("bind_does_not_consume_collar_in_creative");
    public static final ResourceKey<GameTestInstance> DISMISS_REMOVES_ENTITY_AND_UPDATES_SNAPSHOT = createInstanceKey("dismiss_removes_entity_and_updates_snapshot");
    public static final ResourceKey<GameTestInstance> SUMMON_RESTORES_FAMILIAR_BESIDE_PLAYER_WITH_NAME_AND_NEW_IDENTITY = createInstanceKey("summon_restores_familiar_beside_player_with_name_and_new_identity");
    public static final ResourceKey<GameTestInstance> TOGGLE_DISMISSES_WHEN_IN_PLAY_AND_SUMMONS_WHEN_ABSENT = createInstanceKey("toggle_dismisses_when_in_play_and_summons_when_absent");
    public static final ResourceKey<GameTestInstance> TOGGLE_TWICE_IN_ONE_TICK_TOGGLES_ONCE = createInstanceKey("toggle_twice_in_one_tick_toggles_once");
    public static final ResourceKey<GameTestInstance> TOGGLE_WITHIN_DEBOUNCE_WINDOW_IS_IGNORED = createInstanceKey("toggle_within_debounce_window_is_ignored");
    public static final ResourceKey<GameTestInstance> SUMMON_WITH_NO_FAMILIAR_SENDS_MESSAGE = createInstanceKey("summon_with_no_familiar_sends_message");
    public static final ResourceKey<GameTestInstance> DISMISS_EJECTS_RIDING_OWNER = createInstanceKey("dismiss_ejects_riding_owner");
    public static final ResourceKey<GameTestInstance> BIND_SNAPSHOT_STRIPS_PASSENGERS = createInstanceKey("bind_snapshot_strips_passengers");
    public static final ResourceKey<GameTestInstance> SUMMON_WHEN_IN_PLAY_DOES_NOT_DUPLICATE = createInstanceKey("summon_when_in_play_does_not_duplicate");
    public static final ResourceKey<GameTestInstance> SUMMON_CLEARS_HARMFUL_EFFECTS = createInstanceKey("summon_clears_harmful_effects");
    public static final ResourceKey<GameTestInstance> DISMISS_STORES_LIVE_ENTITY_TYPE = createInstanceKey("dismiss_stores_live_entity_type");
    public static final ResourceKey<GameTestInstance> COLLAR_BINDS_OWN_TAMED_WOLF = createInstanceKey("collar_binds_own_tamed_wolf");
    public static final ResourceKey<GameTestInstance> COLLAR_BINDS_OWN_TAMED_HORSE = createInstanceKey("collar_binds_own_tamed_horse");
    public static final ResourceKey<GameTestInstance> COLLAR_BINDS_TRUSTING_FOX = createInstanceKey("collar_binds_trusting_fox");
    public static final ResourceKey<GameTestInstance> COLLAR_IGNORES_UNTRUSTING_FOX = createInstanceKey("collar_ignores_untrusting_fox");
    public static final ResourceKey<GameTestInstance> COLLAR_IGNORES_UNTAMED_WOLF = createInstanceKey("collar_ignores_untamed_wolf");
    public static final ResourceKey<GameTestInstance> COLLAR_IGNORES_WOLF_OWNED_BY_ANOTHER_PLAYER = createInstanceKey("collar_ignores_wolf_owned_by_another_player");
    public static final ResourceKey<GameTestInstance> COLLAR_IGNORES_NON_CANDIDATE_ANIMAL = createInstanceKey("collar_ignores_non_candidate_animal");
    public static final ResourceKey<GameTestInstance> COLLAR_ON_OWN_FAMILIAR_DOES_NOTHING = createInstanceKey("collar_on_own_familiar_does_nothing");
    public static final ResourceKey<GameTestInstance> COLLAR_ON_ANOTHER_PLAYERS_FAMILIAR_DOES_NOTHING = createInstanceKey("collar_on_another_players_familiar_does_nothing");
    public static final ResourceKey<GameTestInstance> COLLAR_CONVERTS_OCELOT_INTO_OCELOT_CAT_FAMILIAR = createInstanceKey("collar_converts_ocelot_into_ocelot_cat_familiar");
    public static final ResourceKey<GameTestInstance> OCELOT_CAT_VARIANT_IS_REGISTERED = createInstanceKey("ocelot_cat_variant_is_registered");
    public static final ResourceKey<GameTestInstance> COLLAR_NAMES_UNNAMED_OCELOT_CAT_OCELOT = createInstanceKey("collar_names_unnamed_ocelot_cat_ocelot");
    public static final ResourceKey<GameTestInstance> COLLAR_KEEPS_NAME_TAG_NAME_WHEN_CONVERTING_OCELOT = createInstanceKey("collar_keeps_name_tag_name_when_converting_ocelot");
    public static final ResourceKey<GameTestInstance> COLLAR_CONVERTS_LEASHED_OCELOT_KEEPING_LEASH = createInstanceKey("collar_converts_leashed_ocelot_keeping_leash");
    public static final ResourceKey<GameTestInstance> CLIENT_SIDE_COLLAR_USE_IS_PREDICTED_FOR_OWNED_PET = createInstanceKey("client_side_collar_use_is_predicted_for_owned_pet");
    public static final ResourceKey<GameTestInstance> FAMILIAR_DISMISSES_INSTEAD_OF_TAKING_DAMAGE = createInstanceKey("familiar_dismisses_instead_of_taking_damage");
    public static final ResourceKey<GameTestInstance> FAMILIAR_DISMISSES_ON_GENERIC_KILL = createInstanceKey("familiar_dismisses_on_generic_kill");
    public static final ResourceKey<GameTestInstance> FAMILIAR_DISMISSES_ON_FIRE_DAMAGE = createInstanceKey("familiar_dismisses_on_fire_damage");
    public static final ResourceKey<GameTestInstance> BINDING_NEW_FAMILIAR_RELEASES_OLD_ONE_UNHARMED = createInstanceKey("binding_new_familiar_releases_old_one_unharmed");
    public static final ResourceKey<GameTestInstance> BINDING_NEW_FAMILIAR_SUMMONS_DISMISSED_OLD_ONE_FIRST = createInstanceKey("binding_new_familiar_summons_dismissed_old_one_first");
    public static final ResourceKey<GameTestInstance> STALE_GENERATION_FAMILIAR_IS_REMOVED_ON_JOIN = createInstanceKey("stale_generation_familiar_is_removed_on_join");
    public static final ResourceKey<GameTestInstance> CURRENT_GENERATION_FAMILIAR_IS_ADOPTED_ON_JOIN = createInstanceKey("current_generation_familiar_is_adopted_on_join");
    public static final ResourceKey<GameTestInstance> DUPLICATE_FAMILIAR_IS_REMOVED_ON_JOIN = createInstanceKey("duplicate_familiar_is_removed_on_join");
    public static final ResourceKey<GameTestInstance> FAMILIAR_WITH_OFFLINE_OWNER_IS_REMOVED_ON_JOIN = createInstanceKey("familiar_with_offline_owner_is_removed_on_join");
    public static final ResourceKey<GameTestInstance> LOGOUT_DISMISSES_FAMILIAR_IN_PLAY = createInstanceKey("logout_dismisses_familiar_in_play");
    public static final ResourceKey<GameTestInstance> CHUNK_UNLOAD_STORES_FAMILIAR_AND_ROTATES_GENERATION = createInstanceKey("chunk_unload_stores_familiar_and_rotates_generation");
    public static final ResourceKey<GameTestInstance> CHUNK_HIDDEN_LEAVE_STORES_FAMILIAR_WITHOUT_REMOVING_IT = createInstanceKey("chunk_hidden_leave_stores_familiar_without_removing_it");
    public static final ResourceKey<GameTestInstance> LEAVE_LEVEL_IGNORES_DISCARDED_FAMILIAR = createInstanceKey("leave_level_ignores_discarded_familiar");
    public static final ResourceKey<GameTestInstance> STALE_GENERATION_FAMILIAR_IS_REJECTED_BY_REAL_JOIN_EVENT = createInstanceKey("stale_generation_familiar_is_rejected_by_real_join_event");
    public static final ResourceKey<GameTestInstance> STALE_BOUND_MOB_IS_DISCARDED_BY_PERIODIC_CHECK = createInstanceKey("stale_bound_mob_is_discarded_by_periodic_check");
    public static final ResourceKey<GameTestInstance> DEATH_SAFETY_NET_DISMISSES_FAMILIAR = createInstanceKey("death_safety_net_dismisses_familiar");
    public static final ResourceKey<GameTestInstance> ZERO_DAMAGE_HIT_DOES_NOT_DISMISS = createInstanceKey("zero_damage_hit_does_not_dismiss");
    public static final ResourceKey<GameTestInstance> STALE_FAMILIAR_IN_BOAT_IS_SWEPT = createInstanceKey("stale_familiar_in_boat_is_swept");
    public static final ResourceKey<GameTestInstance> FIND_FAMILIAR_PAYLOAD_IS_REGISTERED = createInstanceKey("find_familiar_payload_is_registered");
    public static final ResourceKey<GameTestInstance> FIND_FAMILIAR_PAYLOAD_TOGGLES_FOR_CASTER = createInstanceKey("find_familiar_payload_toggles_for_caster");
    public static final ResourceKey<GameTestInstance> FIND_FAMILIAR_PAYLOAD_IGNORES_BURST_POINTS = createInstanceKey("find_familiar_payload_ignores_burst_points");
    public static final ResourceKey<GameTestInstance> FIND_FAMILIAR_PAYLOAD_WITH_NO_FAMILIAR_DOES_NOTHING = createInstanceKey("find_familiar_payload_with_no_familiar_does_nothing");
    public static final ResourceKey<GameTestInstance> FIND_FAMILIAR_PAYLOAD_HIDDEN_WITHOUT_RESEARCH_AND_SHOWN_WITH_IT = createInstanceKey("find_familiar_payload_hidden_without_research_and_shown_with_it");
    public static final ResourceKey<GameTestInstance> FIND_FAMILIAR_WAND_CAST_DEDUCTS_MANA_AND_DISMISSES = createInstanceKey("find_familiar_wand_cast_deducts_mana_and_dismisses");
    public static final ResourceKey<GameTestInstance> FIND_FAMILIAR_MINE_DOES_NOT_TOGGLE = createInstanceKey("find_familiar_mine_does_not_toggle");
    public static final ResourceKey<GameTestInstance> FIND_FAMILIAR_SPELL_PACKAGE_ROUND_TRIPS_THROUGH_CODECS = createInstanceKey("find_familiar_spell_package_round_trips_through_codecs");
    public static final ResourceKey<GameTestInstance> FAMILIAR_NEARBY_ADDS_FIVE_TO_WAND_COST_MODIFIER = createInstanceKey("familiar_nearby_adds_five_to_wand_cost_modifier");
    public static final ResourceKey<GameTestInstance> FAMILIAR_FAR_AWAY_ADDS_NOTHING = createInstanceKey("familiar_far_away_adds_nothing");
    public static final ResourceKey<GameTestInstance> DISMISSED_FAMILIAR_ADDS_NOTHING = createInstanceKey("dismissed_familiar_adds_nothing");
    public static final ResourceKey<GameTestInstance> FAMILIAR_BONUS_REDUCES_SPELL_MANA_CONSUMED = createInstanceKey("familiar_bonus_reduces_spell_mana_consumed");
    public static final ResourceKey<GameTestInstance> POLYMORPH_SWAP_BACK_KEEPS_CURRENT_BINDING = createInstanceKey("polymorph_swap_back_keeps_current_binding");
    public static final ResourceKey<GameTestInstance> RELEASE_REWRITES_QUEUED_SWAPPER_BINDING = createInstanceKey("release_rewrites_queued_swapper_binding");
    public static final ResourceKey<GameTestInstance> RELEASE_WITH_UNRESTORABLE_SNAPSHOT_CLEARS_FAMILIAR_AND_ALLOWS_BIND = createInstanceKey("release_with_unrestorable_snapshot_clears_familiar_and_allows_bind");
    public static final ResourceKey<GameTestInstance> RELEASE_WITH_REFUSED_JOIN_ABORTS_BIND_AND_KEEPS_FAMILIAR = createInstanceKey("release_with_refused_join_aborts_bind_and_keeps_familiar");

    public static final ResourceKey<GameTestInstance> SUMMON_COMMAND_SUMMONS_DISMISSED_FAMILIAR = createInstanceKey("summon_command_summons_dismissed_familiar");
    public static final ResourceKey<GameTestInstance> SUMMON_COMMAND_REFUSES_WHEN_FAMILIAR_IS_ALREADY_IN_PLAY = createInstanceKey("summon_command_refuses_when_familiar_is_already_in_play");
    public static final ResourceKey<GameTestInstance> DISMISS_COMMAND_DISMISSES_FAMILIAR_IN_PLAY = createInstanceKey("dismiss_command_dismisses_familiar_in_play");
    public static final ResourceKey<GameTestInstance> CLEAR_COMMAND_REVERTS_FAMILIAR_IN_PLAY_TO_ORDINARY_PET = createInstanceKey("clear_command_reverts_familiar_in_play_to_ordinary_pet");
    public static final ResourceKey<GameTestInstance> CLEAR_COMMAND_RESTORES_DISMISSED_FAMILIAR_AS_ORDINARY_PET = createInstanceKey("clear_command_restores_dismissed_familiar_as_ordinary_pet");
    public static final ResourceKey<GameTestInstance> COMMANDS_FAIL_WHEN_THERE_IS_NO_FAMILIAR = createInstanceKey("commands_fail_when_there_is_no_familiar");
    public static final ResourceKey<GameTestInstance> COMMANDS_REQUIRE_GAME_MASTER_PERMISSION_AND_ACCEPT_TARGETS_AND_ALIAS = createInstanceKey("commands_require_game_master_permission_and_accept_targets_and_alias");

    public static final ResourceKey<GameTestInstance> CONFIG_SERVICE_REPORTS_PORTAL_DEFAULTS = createInstanceKey("config_service_reports_portal_defaults");
    public static final ResourceKey<GameTestInstance> SAME_COLOUR_AND_FRAME_WITHIN_DEFAULT_RANGE_LINK = createInstanceKey("same_colour_and_frame_within_default_range_link");
    public static final ResourceKey<GameTestInstance> BEYOND_DEFAULT_RANGE_DOES_NOT_LINK = createInstanceKey("beyond_default_range_does_not_link");
    public static final ResourceKey<GameTestInstance> WEAK_ENHANCER_EXTENDS_RANGE_TO_ONE_THOUSAND = createInstanceKey("weak_enhancer_extends_range_to_one_thousand");
    public static final ResourceKey<GameTestInstance> STRONG_ENHANCER_EXTENDS_RANGE_TO_TEN_THOUSAND = createInstanceKey("strong_enhancer_extends_range_to_ten_thousand");
    public static final ResourceKey<GameTestInstance> INFINITY_RUNE_HAS_UNLIMITED_RANGE = createInstanceKey("infinity_rune_has_unlimited_range");
    public static final ResourceKey<GameTestInstance> DIFFERENT_COLOUR_DOES_NOT_LINK = createInstanceKey("different_colour_does_not_link");
    public static final ResourceKey<GameTestInstance> DIFFERENT_FRAME_DOES_NOT_LINK = createInstanceKey("different_frame_does_not_link");
    public static final ResourceKey<GameTestInstance> CROSS_DIMENSION_NEEDS_GATE_RUNE = createInstanceKey("cross_dimension_needs_gate_rune");
    public static final ResourceKey<GameTestInstance> NETHER_COORDINATES_SCALE_BY_EIGHT = createInstanceKey("nether_coordinates_scale_by_eight");
    public static final ResourceKey<GameTestInstance> PORTAL_LINKED_ELSEWHERE_IS_NOT_STOLEN = createInstanceKey("portal_linked_elsewhere_is_not_stolen");
    public static final ResourceKey<GameTestInstance> HASTE_RUNE_SETS_ONE_TICK_DELAY = createInstanceKey("haste_rune_sets_one_tick_delay");
    public static final ResourceKey<GameTestInstance> REGISTRY_UNREGISTER_RELINKS_PARTNER = createInstanceKey("registry_unregister_relinks_partner");
    public static final ResourceKey<GameTestInstance> CUSTOM_PORTAL_ROUND_TRIPS_THROUGH_CODEC = createInstanceKey("custom_portal_round_trips_through_codec");
    public static final ResourceKey<GameTestInstance> CUSTOM_PORTAL_ROUND_TRIPS_THROUGH_STREAM_CODEC = createInstanceKey("custom_portal_round_trips_through_stream_codec");
    public static final ResourceKey<GameTestInstance> STREAM_DECODING_REJECTS_IMPOSSIBLE_COUNTS = createInstanceKey("stream_decoding_rejects_impossible_counts");
    public static final ResourceKey<GameTestInstance> SAVED_DATA_RELINKS_PORTALS_ON_LOAD = createInstanceKey("saved_data_relinks_portals_on_load");
    public static final ResourceKey<GameTestInstance> SYNC_PACKET_ROUND_TRIPS_PORTALS = createInstanceKey("sync_packet_round_trips_portals");
    public static final ResourceKey<GameTestInstance> PORTAL_DATA_RESOLVES_SERVER_REGISTRY = createInstanceKey("portal_data_resolves_server_registry");
    public static final ResourceKey<GameTestInstance> PORTAL_BLOCKS_AND_ITEMS_ARE_REGISTERED = createInstanceKey("portal_blocks_and_items_are_registered");
    public static final ResourceKey<GameTestInstance> RUNE_BLOCKS_AND_ITEMS_ARE_REGISTERED = createInstanceKey("rune_blocks_and_items_are_registered");
    public static final ResourceKey<GameTestInstance> PORTAL_BLOCK_ENTITY_AND_PARTICLES_ARE_REGISTERED = createInstanceKey("portal_block_entity_and_particles_are_registered");
    public static final ResourceKey<GameTestInstance> CATALYST_BUILDS_PORTAL_IN_CLOSED_FRAME = createInstanceKey("catalyst_builds_portal_in_closed_frame");
    public static final ResourceKey<GameTestInstance> CATALYST_FAILS_ON_MIXED_FRAME = createInstanceKey("catalyst_fails_on_mixed_frame");
    public static final ResourceKey<GameTestInstance> CATALYST_FAILS_ON_OPEN_FRAME = createInstanceKey("catalyst_fails_on_open_frame");
    public static final ResourceKey<GameTestInstance> TWO_MATCHING_PORTALS_LINK_AND_LIGHT = createInstanceKey("two_matching_portals_link_and_light");
    public static final ResourceKey<GameTestInstance> PORTALS_OF_DIFFERENT_COLOURS_DO_NOT_LINK = createInstanceKey("portals_of_different_colours_do_not_link");
    public static final ResourceKey<GameTestInstance> PORTALS_OF_DIFFERENT_FRAMES_DO_NOT_LINK = createInstanceKey("portals_of_different_frames_do_not_link");
    public static final ResourceKey<GameTestInstance> BREAKING_FRAME_COLLAPSES_PORTAL_AND_DROPS_CATALYST = createInstanceKey("breaking_frame_collapses_portal_and_drops_catalyst");
    public static final ResourceKey<GameTestInstance> PLACING_AND_BREAKING_RUNE_UPDATES_PORTAL = createInstanceKey("placing_and_breaking_rune_updates_portal");
    public static final ResourceKey<GameTestInstance> SNEAK_USE_SETS_ARRIVAL_POINT = createInstanceKey("sneak_use_sets_arrival_point");
    public static final ResourceKey<GameTestInstance> ENTITY_TELEPORTS_THROUGH_LINKED_PORTAL = createInstanceKey("entity_teleports_through_linked_portal");
    public static final ResourceKey<GameTestInstance> PORTAL_RESEARCH_ENTRIES_HAVE_EXPECTED_STRUCTURE = createInstanceKey("portal_research_entries_have_expected_structure");
    public static final ResourceKey<GameTestInstance> PORTAL_TRAVEL_IS_OPAQUE = createInstanceKey("portal_travel_is_opaque");
    public static final ResourceKey<GameTestInstance> CATALYST_RECIPES_ARE_ARCANE_AND_GATED = createInstanceKey("catalyst_recipes_are_arcane_and_gated");
    public static final ResourceKey<GameTestInstance> RUNE_RECIPES_ARE_ARCANE_AND_GATED = createInstanceKey("rune_recipes_are_arcane_and_gated");
    public static final ResourceKey<GameTestInstance> RECOLOUR_RECIPES_EXIST_FOR_EVERY_COLOUR = createInstanceKey("recolour_recipes_exist_for_every_colour");
    public static final ResourceKey<GameTestInstance> NO_VANILLA_RECIPE_CRAFTS_PORTAL_ITEMS_EXCEPT_RECOLOURING = createInstanceKey("no_vanilla_recipe_crafts_portal_items_except_recolouring");
    public static final ResourceKey<GameTestInstance> RUNE_BLOCK_LOOT_DROPS_RUNE = createInstanceKey("rune_block_loot_drops_rune");
    public static final ResourceKey<GameTestInstance> RUNE_REMOVED_WITHOUT_PLAYER_UPDATES_COUNT = createInstanceKey("rune_removed_without_player_updates_count");
    public static final ResourceKey<GameTestInstance> RUNE_SET_BY_COMMAND_IS_COUNTED = createInstanceKey("rune_set_by_command_is_counted");
    public static final ResourceKey<GameTestInstance> RUNE_RECOUNT_RELINKS_PORTAL = createInstanceKey("rune_recount_relinks_portal");
    public static final ResourceKey<GameTestInstance> POWERED_PORTAL_STAYS_DARK_AFTER_RELOAD = createInstanceKey("powered_portal_stays_dark_after_reload");
    public static final ResourceKey<GameTestInstance> FEATURES_ARE_ENABLED_BY_DEFAULT = createInstanceKey("features_are_enabled_by_default");
    public static final ResourceKey<GameTestInstance> EVERY_FEATURE_RESEARCH_ENTRY_AND_RECIPE_IS_CONDITIONED = createInstanceKey("every_feature_research_entry_and_recipe_is_conditioned");
    public static final ResourceKey<GameTestInstance> OPAQUE_TAG_REFERENCE_IS_OPTIONAL = createInstanceKey("opaque_tag_reference_is_optional");
    public static final ResourceKey<GameTestInstance> STALE_RESEARCH_AND_RECIPE_KEYS_ARE_HARMLESS = createInstanceKey("stale_research_and_recipe_keys_are_harmless");
    public static final ResourceKey<GameTestInstance> STALE_GRIMOIRE_TOPICS_ARE_CLEARED = createInstanceKey("stale_grimoire_topics_are_cleared");
    public static final ResourceKey<GameTestInstance> FIND_FAMILIAR_PAYLOAD_NEEDS_ITS_ENTRY_LOADED = createInstanceKey("find_familiar_payload_needs_its_entry_loaded");
    public static final ResourceKey<GameTestInstance> GENERATED_AFFINITIES_ARE_REGISTERED_AND_YIELD_TO_DATA = createInstanceKey("generated_affinities_are_registered_and_yield_to_data");
    public static final ResourceKey<GameTestInstance> ROUNDED_DOWN_CRAFTS_OF_GENERATED_ITEMS_GET_ONE_OF_EACH_SOURCE = createInstanceKey("rounded_down_crafts_of_generated_items_get_one_of_each_source");
    public static final ResourceKey<GameTestInstance> LINT_STILL_LISTS_ITEMS_WITH_ONLY_A_GENERATED_AFFINITY = createInstanceKey("lint_still_lists_items_with_only_a_generated_affinity");

    // Test functions registered by the NeoForge module (TestFunctionsPMXNeoforge), which this module refers to by name
    public static final String FEATURE_CONDITION_FOLLOWS_CONFIG_FUNCTION = "feature_condition_follows_config";
    public static final String FEATURE_CONDITION_KEEPS_ENTRIES_LISTED_BY_SERVER_FUNCTION = "feature_condition_keeps_entries_listed_by_server";
    public static final String FEATURE_CONDITION_ROUND_TRIPS_THROUGH_CODEC_FUNCTION = "feature_condition_round_trips_through_codec";
    public static final String CONDITIONED_RESEARCH_ENTRY_LOADS_WHILE_ENABLED_FUNCTION = "conditioned_research_entry_loads_while_enabled";
    public static final String PLAIN_REGISTRY_OPS_HAVE_NO_CONDITION_CONTEXT_FUNCTION = "plain_registry_ops_have_no_condition_context";
    public static final String FEATURE_SWITCHES_DEFAULT_TO_ENABLED_FUNCTION = "feature_switches_default_to_enabled";
    public static final String STALE_RESEARCH_KEY_SURVIVES_SYNC_PACKET_FUNCTION = "stale_research_key_survives_sync_packet";
    public static final ResourceKey<GameTestInstance> FEATURE_CONDITION_FOLLOWS_CONFIG = createInstanceKey(FEATURE_CONDITION_FOLLOWS_CONFIG_FUNCTION);
    public static final ResourceKey<GameTestInstance> FEATURE_CONDITION_KEEPS_ENTRIES_LISTED_BY_SERVER = createInstanceKey(FEATURE_CONDITION_KEEPS_ENTRIES_LISTED_BY_SERVER_FUNCTION);
    public static final ResourceKey<GameTestInstance> FEATURE_CONDITION_ROUND_TRIPS_THROUGH_CODEC = createInstanceKey(FEATURE_CONDITION_ROUND_TRIPS_THROUGH_CODEC_FUNCTION);
    public static final ResourceKey<GameTestInstance> CONDITIONED_RESEARCH_ENTRY_LOADS_WHILE_ENABLED = createInstanceKey(CONDITIONED_RESEARCH_ENTRY_LOADS_WHILE_ENABLED_FUNCTION);
    public static final ResourceKey<GameTestInstance> PLAIN_REGISTRY_OPS_HAVE_NO_CONDITION_CONTEXT = createInstanceKey(PLAIN_REGISTRY_OPS_HAVE_NO_CONDITION_CONTEXT_FUNCTION);
    public static final ResourceKey<GameTestInstance> FEATURE_SWITCHES_DEFAULT_TO_ENABLED = createInstanceKey(FEATURE_SWITCHES_DEFAULT_TO_ENABLED_FUNCTION);
    public static final ResourceKey<GameTestInstance> STALE_RESEARCH_KEY_SURVIVES_SYNC_PACKET = createInstanceKey(STALE_RESEARCH_KEY_SURVIVES_SYNC_PACKET_FUNCTION);

    public static void bootstrap(BootstrapContext<GameTestInstance> context) {
        registerFunction(context, SUMMON_COMMAND_SUMMONS_DISMISSED_FAMILIAR, TestFunctionsPMX.SUMMON_COMMAND_SUMMONS_DISMISSED_FAMILIAR.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, SUMMON_COMMAND_REFUSES_WHEN_FAMILIAR_IS_ALREADY_IN_PLAY, TestFunctionsPMX.SUMMON_COMMAND_REFUSES_WHEN_FAMILIAR_IS_ALREADY_IN_PLAY.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, DISMISS_COMMAND_DISMISSES_FAMILIAR_IN_PLAY, TestFunctionsPMX.DISMISS_COMMAND_DISMISSES_FAMILIAR_IN_PLAY.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, CLEAR_COMMAND_REVERTS_FAMILIAR_IN_PLAY_TO_ORDINARY_PET, TestFunctionsPMX.CLEAR_COMMAND_REVERTS_FAMILIAR_IN_PLAY_TO_ORDINARY_PET.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, CLEAR_COMMAND_RESTORES_DISMISSED_FAMILIAR_AS_ORDINARY_PET, TestFunctionsPMX.CLEAR_COMMAND_RESTORES_DISMISSED_FAMILIAR_AS_ORDINARY_PET.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, COMMANDS_FAIL_WHEN_THERE_IS_NO_FAMILIAR, TestFunctionsPMX.COMMANDS_FAIL_WHEN_THERE_IS_NO_FAMILIAR.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, COMMANDS_REQUIRE_GAME_MASTER_PERMISSION_AND_ACCEPT_TARGETS_AND_ALIAS, TestFunctionsPMX.COMMANDS_REQUIRE_GAME_MASTER_PERMISSION_AND_ACCEPT_TARGETS_AND_ALIAS.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, CANARY, TestFunctionsPMX.CANARY.getKey());
        registerFunction(context, FAMILIAR_COLLAR_ITEM_IS_REGISTERED, TestFunctionsPMX.FAMILIAR_COLLAR_ITEM_IS_REGISTERED.getKey());
        registerFunction(context, RESEARCH_ENTRIES_ARE_REGISTERED_WITH_EXPECTED_PARENTS, TestFunctionsPMX.RESEARCH_ENTRIES_ARE_REGISTERED_WITH_EXPECTED_PARENTS.getKey());
        registerFunction(context, RITUAL_RECIPE_HAS_EXPECTED_INGREDIENTS_AND_REQUIREMENT, TestFunctionsPMX.RITUAL_RECIPE_HAS_EXPECTED_INGREDIENTS_AND_REQUIREMENT.getKey());
        registerFunction(context, RITUAL_RECIPE_REQUIREMENT_UNMET_WITHOUT_RESEARCH, TestFunctionsPMX.RITUAL_RECIPE_REQUIREMENT_UNMET_WITHOUT_RESEARCH.getKey());
        registerFunction(context, RITUAL_ALTAR_FINISH_CRAFT_YIELDS_FAMILIAR_COLLAR, TestFunctionsPMX.RITUAL_ALTAR_FINISH_CRAFT_YIELDS_FAMILIAR_COLLAR.getKey());
        registerFunction(context, FAMILIAR_COLLAR_AFFINITY_HAS_EXPECTED_SOURCES, TestFunctionsPMX.FAMILIAR_COLLAR_AFFINITY_HAS_EXPECTED_SOURCES.getKey());
        registerFunction(context, PLAYER_FAMILIAR_ROUND_TRIPS_THROUGH_CODEC, TestFunctionsPMX.PLAYER_FAMILIAR_ROUND_TRIPS_THROUGH_CODEC.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, PLAYER_FAMILIAR_ATTACHMENT_IS_AVAILABLE_AND_EMPTY_BY_DEFAULT, TestFunctionsPMX.PLAYER_FAMILIAR_ATTACHMENT_IS_AVAILABLE_AND_EMPTY_BY_DEFAULT.getKey());
        registerFunction(context, ENTITY_BINDING_CAN_BE_SET_READ_AND_CLEARED, TestFunctionsPMX.ENTITY_BINDING_CAN_BE_SET_READ_AND_CLEARED.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, ENTITY_BINDING_SURVIVES_ENTITY_SAVE_AND_LOAD, TestFunctionsPMX.ENTITY_BINDING_SURVIVES_ENTITY_SAVE_AND_LOAD.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, BIND_MARKS_PET_AND_STORES_SNAPSHOT, TestFunctionsPMX.BIND_MARKS_PET_AND_STORES_SNAPSHOT.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, BIND_DOES_NOT_CONSUME_COLLAR_IN_CREATIVE, TestFunctionsPMX.BIND_DOES_NOT_CONSUME_COLLAR_IN_CREATIVE.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, DISMISS_REMOVES_ENTITY_AND_UPDATES_SNAPSHOT, TestFunctionsPMX.DISMISS_REMOVES_ENTITY_AND_UPDATES_SNAPSHOT.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, SUMMON_RESTORES_FAMILIAR_BESIDE_PLAYER_WITH_NAME_AND_NEW_IDENTITY, TestFunctionsPMX.SUMMON_RESTORES_FAMILIAR_BESIDE_PLAYER_WITH_NAME_AND_NEW_IDENTITY.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, TOGGLE_DISMISSES_WHEN_IN_PLAY_AND_SUMMONS_WHEN_ABSENT, TestFunctionsPMX.TOGGLE_DISMISSES_WHEN_IN_PLAY_AND_SUMMONS_WHEN_ABSENT.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, TOGGLE_TWICE_IN_ONE_TICK_TOGGLES_ONCE, TestFunctionsPMX.TOGGLE_TWICE_IN_ONE_TICK_TOGGLES_ONCE.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, TOGGLE_WITHIN_DEBOUNCE_WINDOW_IS_IGNORED, TestFunctionsPMX.TOGGLE_WITHIN_DEBOUNCE_WINDOW_IS_IGNORED.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, SUMMON_WITH_NO_FAMILIAR_SENDS_MESSAGE, TestFunctionsPMX.SUMMON_WITH_NO_FAMILIAR_SENDS_MESSAGE.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, DISMISS_EJECTS_RIDING_OWNER, TestFunctionsPMX.DISMISS_EJECTS_RIDING_OWNER.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, BIND_SNAPSHOT_STRIPS_PASSENGERS, TestFunctionsPMX.BIND_SNAPSHOT_STRIPS_PASSENGERS.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, SUMMON_WHEN_IN_PLAY_DOES_NOT_DUPLICATE, TestFunctionsPMX.SUMMON_WHEN_IN_PLAY_DOES_NOT_DUPLICATE.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, SUMMON_CLEARS_HARMFUL_EFFECTS, TestFunctionsPMX.SUMMON_CLEARS_HARMFUL_EFFECTS.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, DISMISS_STORES_LIVE_ENTITY_TYPE, TestFunctionsPMX.DISMISS_STORES_LIVE_ENTITY_TYPE.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, COLLAR_BINDS_OWN_TAMED_WOLF, TestFunctionsPMX.COLLAR_BINDS_OWN_TAMED_WOLF.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, COLLAR_BINDS_OWN_TAMED_HORSE, TestFunctionsPMX.COLLAR_BINDS_OWN_TAMED_HORSE.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, COLLAR_BINDS_TRUSTING_FOX, TestFunctionsPMX.COLLAR_BINDS_TRUSTING_FOX.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, COLLAR_IGNORES_UNTRUSTING_FOX, TestFunctionsPMX.COLLAR_IGNORES_UNTRUSTING_FOX.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, COLLAR_IGNORES_UNTAMED_WOLF, TestFunctionsPMX.COLLAR_IGNORES_UNTAMED_WOLF.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, COLLAR_IGNORES_WOLF_OWNED_BY_ANOTHER_PLAYER, TestFunctionsPMX.COLLAR_IGNORES_WOLF_OWNED_BY_ANOTHER_PLAYER.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, COLLAR_IGNORES_NON_CANDIDATE_ANIMAL, TestFunctionsPMX.COLLAR_IGNORES_NON_CANDIDATE_ANIMAL.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, COLLAR_ON_OWN_FAMILIAR_DOES_NOTHING, TestFunctionsPMX.COLLAR_ON_OWN_FAMILIAR_DOES_NOTHING.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, COLLAR_ON_ANOTHER_PLAYERS_FAMILIAR_DOES_NOTHING, TestFunctionsPMX.COLLAR_ON_ANOTHER_PLAYERS_FAMILIAR_DOES_NOTHING.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, COLLAR_CONVERTS_OCELOT_INTO_OCELOT_CAT_FAMILIAR, TestFunctionsPMX.COLLAR_CONVERTS_OCELOT_INTO_OCELOT_CAT_FAMILIAR.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, OCELOT_CAT_VARIANT_IS_REGISTERED, TestFunctionsPMX.OCELOT_CAT_VARIANT_IS_REGISTERED.getKey());
        registerFunction(context, COLLAR_NAMES_UNNAMED_OCELOT_CAT_OCELOT, TestFunctionsPMX.COLLAR_NAMES_UNNAMED_OCELOT_CAT_OCELOT.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, COLLAR_KEEPS_NAME_TAG_NAME_WHEN_CONVERTING_OCELOT, TestFunctionsPMX.COLLAR_KEEPS_NAME_TAG_NAME_WHEN_CONVERTING_OCELOT.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, COLLAR_CONVERTS_LEASHED_OCELOT_KEEPING_LEASH, TestFunctionsPMX.COLLAR_CONVERTS_LEASHED_OCELOT_KEEPING_LEASH.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, CLIENT_SIDE_COLLAR_USE_IS_PREDICTED_FOR_OWNED_PET, TestFunctionsPMX.CLIENT_SIDE_COLLAR_USE_IS_PREDICTED_FOR_OWNED_PET.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, FAMILIAR_DISMISSES_INSTEAD_OF_TAKING_DAMAGE, TestFunctionsPMX.FAMILIAR_DISMISSES_INSTEAD_OF_TAKING_DAMAGE.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, FAMILIAR_DISMISSES_ON_GENERIC_KILL, TestFunctionsPMX.FAMILIAR_DISMISSES_ON_GENERIC_KILL.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, FAMILIAR_DISMISSES_ON_FIRE_DAMAGE, TestFunctionsPMX.FAMILIAR_DISMISSES_ON_FIRE_DAMAGE.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, BINDING_NEW_FAMILIAR_RELEASES_OLD_ONE_UNHARMED, TestFunctionsPMX.BINDING_NEW_FAMILIAR_RELEASES_OLD_ONE_UNHARMED.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, BINDING_NEW_FAMILIAR_SUMMONS_DISMISSED_OLD_ONE_FIRST, TestFunctionsPMX.BINDING_NEW_FAMILIAR_SUMMONS_DISMISSED_OLD_ONE_FIRST.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, STALE_GENERATION_FAMILIAR_IS_REMOVED_ON_JOIN, TestFunctionsPMX.STALE_GENERATION_FAMILIAR_IS_REMOVED_ON_JOIN.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, CURRENT_GENERATION_FAMILIAR_IS_ADOPTED_ON_JOIN, TestFunctionsPMX.CURRENT_GENERATION_FAMILIAR_IS_ADOPTED_ON_JOIN.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, DUPLICATE_FAMILIAR_IS_REMOVED_ON_JOIN, TestFunctionsPMX.DUPLICATE_FAMILIAR_IS_REMOVED_ON_JOIN.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, FAMILIAR_WITH_OFFLINE_OWNER_IS_REMOVED_ON_JOIN, TestFunctionsPMX.FAMILIAR_WITH_OFFLINE_OWNER_IS_REMOVED_ON_JOIN.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, LOGOUT_DISMISSES_FAMILIAR_IN_PLAY, TestFunctionsPMX.LOGOUT_DISMISSES_FAMILIAR_IN_PLAY.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, CHUNK_UNLOAD_STORES_FAMILIAR_AND_ROTATES_GENERATION, TestFunctionsPMX.CHUNK_UNLOAD_STORES_FAMILIAR_AND_ROTATES_GENERATION.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, CHUNK_HIDDEN_LEAVE_STORES_FAMILIAR_WITHOUT_REMOVING_IT, TestFunctionsPMX.CHUNK_HIDDEN_LEAVE_STORES_FAMILIAR_WITHOUT_REMOVING_IT.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, LEAVE_LEVEL_IGNORES_DISCARDED_FAMILIAR, TestFunctionsPMX.LEAVE_LEVEL_IGNORES_DISCARDED_FAMILIAR.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, STALE_GENERATION_FAMILIAR_IS_REJECTED_BY_REAL_JOIN_EVENT, TestFunctionsPMX.STALE_GENERATION_FAMILIAR_IS_REJECTED_BY_REAL_JOIN_EVENT.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, STALE_BOUND_MOB_IS_DISCARDED_BY_PERIODIC_CHECK, TestFunctionsPMX.STALE_BOUND_MOB_IS_DISCARDED_BY_PERIODIC_CHECK.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, DEATH_SAFETY_NET_DISMISSES_FAMILIAR, TestFunctionsPMX.DEATH_SAFETY_NET_DISMISSES_FAMILIAR.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, ZERO_DAMAGE_HIT_DOES_NOT_DISMISS, TestFunctionsPMX.ZERO_DAMAGE_HIT_DOES_NOT_DISMISS.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, STALE_FAMILIAR_IN_BOAT_IS_SWEPT, TestFunctionsPMX.STALE_FAMILIAR_IN_BOAT_IS_SWEPT.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, FIND_FAMILIAR_PAYLOAD_IS_REGISTERED, TestFunctionsPMX.FIND_FAMILIAR_PAYLOAD_IS_REGISTERED.getKey());
        registerFunction(context, FIND_FAMILIAR_PAYLOAD_TOGGLES_FOR_CASTER, TestFunctionsPMX.FIND_FAMILIAR_PAYLOAD_TOGGLES_FOR_CASTER.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, FIND_FAMILIAR_PAYLOAD_IGNORES_BURST_POINTS, TestFunctionsPMX.FIND_FAMILIAR_PAYLOAD_IGNORES_BURST_POINTS.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, FIND_FAMILIAR_PAYLOAD_WITH_NO_FAMILIAR_DOES_NOTHING, TestFunctionsPMX.FIND_FAMILIAR_PAYLOAD_WITH_NO_FAMILIAR_DOES_NOTHING.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, FIND_FAMILIAR_PAYLOAD_HIDDEN_WITHOUT_RESEARCH_AND_SHOWN_WITH_IT, TestFunctionsPMX.FIND_FAMILIAR_PAYLOAD_HIDDEN_WITHOUT_RESEARCH_AND_SHOWN_WITH_IT.getKey());
        registerFunction(context, FIND_FAMILIAR_WAND_CAST_DEDUCTS_MANA_AND_DISMISSES, TestFunctionsPMX.FIND_FAMILIAR_WAND_CAST_DEDUCTS_MANA_AND_DISMISSES.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, FIND_FAMILIAR_MINE_DOES_NOT_TOGGLE, TestFunctionsPMX.FIND_FAMILIAR_MINE_DOES_NOT_TOGGLE.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, FIND_FAMILIAR_SPELL_PACKAGE_ROUND_TRIPS_THROUGH_CODECS, TestFunctionsPMX.FIND_FAMILIAR_SPELL_PACKAGE_ROUND_TRIPS_THROUGH_CODECS.getKey());
        registerFunction(context, FAMILIAR_NEARBY_ADDS_FIVE_TO_WAND_COST_MODIFIER, TestFunctionsPMX.FAMILIAR_NEARBY_ADDS_FIVE_TO_WAND_COST_MODIFIER.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, FAMILIAR_FAR_AWAY_ADDS_NOTHING, TestFunctionsPMX.FAMILIAR_FAR_AWAY_ADDS_NOTHING.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, DISMISSED_FAMILIAR_ADDS_NOTHING, TestFunctionsPMX.DISMISSED_FAMILIAR_ADDS_NOTHING.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, FAMILIAR_BONUS_REDUCES_SPELL_MANA_CONSUMED, TestFunctionsPMX.FAMILIAR_BONUS_REDUCES_SPELL_MANA_CONSUMED.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, POLYMORPH_SWAP_BACK_KEEPS_CURRENT_BINDING, TestFunctionsPMX.POLYMORPH_SWAP_BACK_KEEPS_CURRENT_BINDING.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, RELEASE_REWRITES_QUEUED_SWAPPER_BINDING, TestFunctionsPMX.RELEASE_REWRITES_QUEUED_SWAPPER_BINDING.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, RELEASE_WITH_UNRESTORABLE_SNAPSHOT_CLEARS_FAMILIAR_AND_ALLOWS_BIND, TestFunctionsPMX.RELEASE_WITH_UNRESTORABLE_SNAPSHOT_CLEARS_FAMILIAR_AND_ALLOWS_BIND.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, RELEASE_WITH_REFUSED_JOIN_ABORTS_BIND_AND_KEEPS_FAMILIAR, TestFunctionsPMX.RELEASE_WITH_REFUSED_JOIN_ABORTS_BIND_AND_KEEPS_FAMILIAR.getKey(), TestUtilsPMX.FLOOR_TEMPLATE);
        registerFunction(context, CONFIG_SERVICE_REPORTS_PORTAL_DEFAULTS, TestFunctionsPMX.CONFIG_SERVICE_REPORTS_PORTAL_DEFAULTS.getKey());
        registerFunction(context, SAME_COLOUR_AND_FRAME_WITHIN_DEFAULT_RANGE_LINK, TestFunctionsPMX.SAME_COLOUR_AND_FRAME_WITHIN_DEFAULT_RANGE_LINK.getKey());
        registerFunction(context, BEYOND_DEFAULT_RANGE_DOES_NOT_LINK, TestFunctionsPMX.BEYOND_DEFAULT_RANGE_DOES_NOT_LINK.getKey());
        registerFunction(context, WEAK_ENHANCER_EXTENDS_RANGE_TO_ONE_THOUSAND, TestFunctionsPMX.WEAK_ENHANCER_EXTENDS_RANGE_TO_ONE_THOUSAND.getKey());
        registerFunction(context, STRONG_ENHANCER_EXTENDS_RANGE_TO_TEN_THOUSAND, TestFunctionsPMX.STRONG_ENHANCER_EXTENDS_RANGE_TO_TEN_THOUSAND.getKey());
        registerFunction(context, INFINITY_RUNE_HAS_UNLIMITED_RANGE, TestFunctionsPMX.INFINITY_RUNE_HAS_UNLIMITED_RANGE.getKey());
        registerFunction(context, DIFFERENT_COLOUR_DOES_NOT_LINK, TestFunctionsPMX.DIFFERENT_COLOUR_DOES_NOT_LINK.getKey());
        registerFunction(context, DIFFERENT_FRAME_DOES_NOT_LINK, TestFunctionsPMX.DIFFERENT_FRAME_DOES_NOT_LINK.getKey());
        registerFunction(context, CROSS_DIMENSION_NEEDS_GATE_RUNE, TestFunctionsPMX.CROSS_DIMENSION_NEEDS_GATE_RUNE.getKey());
        registerFunction(context, NETHER_COORDINATES_SCALE_BY_EIGHT, TestFunctionsPMX.NETHER_COORDINATES_SCALE_BY_EIGHT.getKey());
        registerFunction(context, PORTAL_LINKED_ELSEWHERE_IS_NOT_STOLEN, TestFunctionsPMX.PORTAL_LINKED_ELSEWHERE_IS_NOT_STOLEN.getKey());
        registerFunction(context, HASTE_RUNE_SETS_ONE_TICK_DELAY, TestFunctionsPMX.HASTE_RUNE_SETS_ONE_TICK_DELAY.getKey());
        registerFunction(context, REGISTRY_UNREGISTER_RELINKS_PARTNER, TestFunctionsPMX.REGISTRY_UNREGISTER_RELINKS_PARTNER.getKey());
        registerFunction(context, CUSTOM_PORTAL_ROUND_TRIPS_THROUGH_CODEC, TestFunctionsPMX.CUSTOM_PORTAL_ROUND_TRIPS_THROUGH_CODEC.getKey());
        registerFunction(context, CUSTOM_PORTAL_ROUND_TRIPS_THROUGH_STREAM_CODEC, TestFunctionsPMX.CUSTOM_PORTAL_ROUND_TRIPS_THROUGH_STREAM_CODEC.getKey());
        registerFunction(context, STREAM_DECODING_REJECTS_IMPOSSIBLE_COUNTS, TestFunctionsPMX.STREAM_DECODING_REJECTS_IMPOSSIBLE_COUNTS.getKey());
        registerFunction(context, SAVED_DATA_RELINKS_PORTALS_ON_LOAD, TestFunctionsPMX.SAVED_DATA_RELINKS_PORTALS_ON_LOAD.getKey());
        registerFunction(context, SYNC_PACKET_ROUND_TRIPS_PORTALS, TestFunctionsPMX.SYNC_PACKET_ROUND_TRIPS_PORTALS.getKey());
        registerFunction(context, PORTAL_DATA_RESOLVES_SERVER_REGISTRY, TestFunctionsPMX.PORTAL_DATA_RESOLVES_SERVER_REGISTRY.getKey());
        registerFunction(context, PORTAL_BLOCKS_AND_ITEMS_ARE_REGISTERED, TestFunctionsPMX.PORTAL_BLOCKS_AND_ITEMS_ARE_REGISTERED.getKey());
        registerFunction(context, RUNE_BLOCKS_AND_ITEMS_ARE_REGISTERED, TestFunctionsPMX.RUNE_BLOCKS_AND_ITEMS_ARE_REGISTERED.getKey());
        registerFunction(context, PORTAL_BLOCK_ENTITY_AND_PARTICLES_ARE_REGISTERED, TestFunctionsPMX.PORTAL_BLOCK_ENTITY_AND_PARTICLES_ARE_REGISTERED.getKey());
        registerFunction(context, CATALYST_BUILDS_PORTAL_IN_CLOSED_FRAME, TestFunctionsPMX.CATALYST_BUILDS_PORTAL_IN_CLOSED_FRAME.getKey(), TestUtilsPMX.LARGE_FLOOR_TEMPLATE);
        registerFunction(context, CATALYST_FAILS_ON_MIXED_FRAME, TestFunctionsPMX.CATALYST_FAILS_ON_MIXED_FRAME.getKey(), TestUtilsPMX.LARGE_FLOOR_TEMPLATE);
        registerFunction(context, CATALYST_FAILS_ON_OPEN_FRAME, TestFunctionsPMX.CATALYST_FAILS_ON_OPEN_FRAME.getKey(), TestUtilsPMX.LARGE_FLOOR_TEMPLATE);
        registerFunction(context, TWO_MATCHING_PORTALS_LINK_AND_LIGHT, TestFunctionsPMX.TWO_MATCHING_PORTALS_LINK_AND_LIGHT.getKey(), TestUtilsPMX.LARGE_FLOOR_TEMPLATE);
        registerFunction(context, PORTALS_OF_DIFFERENT_COLOURS_DO_NOT_LINK, TestFunctionsPMX.PORTALS_OF_DIFFERENT_COLOURS_DO_NOT_LINK.getKey(), TestUtilsPMX.LARGE_FLOOR_TEMPLATE);
        registerFunction(context, PORTALS_OF_DIFFERENT_FRAMES_DO_NOT_LINK, TestFunctionsPMX.PORTALS_OF_DIFFERENT_FRAMES_DO_NOT_LINK.getKey(), TestUtilsPMX.LARGE_FLOOR_TEMPLATE);
        registerFunction(context, BREAKING_FRAME_COLLAPSES_PORTAL_AND_DROPS_CATALYST, TestFunctionsPMX.BREAKING_FRAME_COLLAPSES_PORTAL_AND_DROPS_CATALYST.getKey(), TestUtilsPMX.LARGE_FLOOR_TEMPLATE);
        registerFunction(context, PLACING_AND_BREAKING_RUNE_UPDATES_PORTAL, TestFunctionsPMX.PLACING_AND_BREAKING_RUNE_UPDATES_PORTAL.getKey(), TestUtilsPMX.LARGE_FLOOR_TEMPLATE);
        registerFunction(context, SNEAK_USE_SETS_ARRIVAL_POINT, TestFunctionsPMX.SNEAK_USE_SETS_ARRIVAL_POINT.getKey(), TestUtilsPMX.LARGE_FLOOR_TEMPLATE);
        registerFunction(context, ENTITY_TELEPORTS_THROUGH_LINKED_PORTAL, TestFunctionsPMX.ENTITY_TELEPORTS_THROUGH_LINKED_PORTAL.getKey(), TestUtilsPMX.LARGE_FLOOR_TEMPLATE);
        registerFunction(context, PORTAL_RESEARCH_ENTRIES_HAVE_EXPECTED_STRUCTURE, TestFunctionsPMX.PORTAL_RESEARCH_ENTRIES_HAVE_EXPECTED_STRUCTURE.getKey());
        registerFunction(context, PORTAL_TRAVEL_IS_OPAQUE, TestFunctionsPMX.PORTAL_TRAVEL_IS_OPAQUE.getKey());
        registerFunction(context, CATALYST_RECIPES_ARE_ARCANE_AND_GATED, TestFunctionsPMX.CATALYST_RECIPES_ARE_ARCANE_AND_GATED.getKey());
        registerFunction(context, RUNE_RECIPES_ARE_ARCANE_AND_GATED, TestFunctionsPMX.RUNE_RECIPES_ARE_ARCANE_AND_GATED.getKey());
        registerFunction(context, RECOLOUR_RECIPES_EXIST_FOR_EVERY_COLOUR, TestFunctionsPMX.RECOLOUR_RECIPES_EXIST_FOR_EVERY_COLOUR.getKey());
        registerFunction(context, NO_VANILLA_RECIPE_CRAFTS_PORTAL_ITEMS_EXCEPT_RECOLOURING, TestFunctionsPMX.NO_VANILLA_RECIPE_CRAFTS_PORTAL_ITEMS_EXCEPT_RECOLOURING.getKey());
        registerFunction(context, RUNE_BLOCK_LOOT_DROPS_RUNE, TestFunctionsPMX.RUNE_BLOCK_LOOT_DROPS_RUNE.getKey());
        registerFunction(context, RUNE_REMOVED_WITHOUT_PLAYER_UPDATES_COUNT, TestFunctionsPMX.RUNE_REMOVED_WITHOUT_PLAYER_UPDATES_COUNT.getKey(), TestUtilsPMX.LARGE_FLOOR_TEMPLATE);
        registerFunction(context, RUNE_SET_BY_COMMAND_IS_COUNTED, TestFunctionsPMX.RUNE_SET_BY_COMMAND_IS_COUNTED.getKey(), TestUtilsPMX.LARGE_FLOOR_TEMPLATE);
        registerFunction(context, RUNE_RECOUNT_RELINKS_PORTAL, TestFunctionsPMX.RUNE_RECOUNT_RELINKS_PORTAL.getKey(), TestUtilsPMX.LARGE_FLOOR_TEMPLATE);
        registerFunction(context, POWERED_PORTAL_STAYS_DARK_AFTER_RELOAD, TestFunctionsPMX.POWERED_PORTAL_STAYS_DARK_AFTER_RELOAD.getKey(), TestUtilsPMX.LARGE_FLOOR_TEMPLATE);
        registerFunction(context, FEATURES_ARE_ENABLED_BY_DEFAULT, TestFunctionsPMX.FEATURES_ARE_ENABLED_BY_DEFAULT.getKey());
        registerFunction(context, EVERY_FEATURE_RESEARCH_ENTRY_AND_RECIPE_IS_CONDITIONED, TestFunctionsPMX.EVERY_FEATURE_RESEARCH_ENTRY_AND_RECIPE_IS_CONDITIONED.getKey());
        registerFunction(context, OPAQUE_TAG_REFERENCE_IS_OPTIONAL, TestFunctionsPMX.OPAQUE_TAG_REFERENCE_IS_OPTIONAL.getKey());
        registerFunction(context, STALE_RESEARCH_AND_RECIPE_KEYS_ARE_HARMLESS, TestFunctionsPMX.STALE_RESEARCH_AND_RECIPE_KEYS_ARE_HARMLESS.getKey());
        registerFunction(context, STALE_GRIMOIRE_TOPICS_ARE_CLEARED, TestFunctionsPMX.STALE_GRIMOIRE_TOPICS_ARE_CLEARED.getKey());
        registerFunction(context, FIND_FAMILIAR_PAYLOAD_NEEDS_ITS_ENTRY_LOADED, TestFunctionsPMX.FIND_FAMILIAR_PAYLOAD_NEEDS_ITS_ENTRY_LOADED.getKey());
        registerFunction(context, GENERATED_AFFINITIES_ARE_REGISTERED_AND_YIELD_TO_DATA, TestFunctionsPMX.GENERATED_AFFINITIES_ARE_REGISTERED_AND_YIELD_TO_DATA.getKey());
        registerFunction(context, ROUNDED_DOWN_CRAFTS_OF_GENERATED_ITEMS_GET_ONE_OF_EACH_SOURCE, TestFunctionsPMX.ROUNDED_DOWN_CRAFTS_OF_GENERATED_ITEMS_GET_ONE_OF_EACH_SOURCE.getKey());
        registerFunction(context, LINT_STILL_LISTS_ITEMS_WITH_ONLY_A_GENERATED_AFFINITY, TestFunctionsPMX.LINT_STILL_LISTS_ITEMS_WITH_ONLY_A_GENERATED_AFFINITY.getKey());
        registerFunction(context, FEATURE_CONDITION_FOLLOWS_CONFIG, functionKey(FEATURE_CONDITION_FOLLOWS_CONFIG_FUNCTION));
        registerFunction(context, FEATURE_CONDITION_KEEPS_ENTRIES_LISTED_BY_SERVER, functionKey(FEATURE_CONDITION_KEEPS_ENTRIES_LISTED_BY_SERVER_FUNCTION));
        registerFunction(context, FEATURE_CONDITION_ROUND_TRIPS_THROUGH_CODEC, functionKey(FEATURE_CONDITION_ROUND_TRIPS_THROUGH_CODEC_FUNCTION));
        registerFunction(context, CONDITIONED_RESEARCH_ENTRY_LOADS_WHILE_ENABLED, functionKey(CONDITIONED_RESEARCH_ENTRY_LOADS_WHILE_ENABLED_FUNCTION));
        registerFunction(context, PLAIN_REGISTRY_OPS_HAVE_NO_CONDITION_CONTEXT, functionKey(PLAIN_REGISTRY_OPS_HAVE_NO_CONDITION_CONTEXT_FUNCTION));
        registerFunction(context, FEATURE_SWITCHES_DEFAULT_TO_ENABLED, functionKey(FEATURE_SWITCHES_DEFAULT_TO_ENABLED_FUNCTION));
        registerFunction(context, STALE_RESEARCH_KEY_SURVIVES_SYNC_PACKET, functionKey(STALE_RESEARCH_KEY_SURVIVES_SYNC_PACKET_FUNCTION));
    }

    private static ResourceKey<GameTestInstance> createInstanceKey(String name) {
        return ResourceKey.create(Registries.TEST_INSTANCE, ResourceUtilsPMX.loc(name));
    }

    /** The key of a test function registered under this mod's namespace by another module. */
    private static ResourceKey<Consumer<GameTestHelper>> functionKey(String name) {
        return ResourceKey.create(Registries.TEST_FUNCTION, ResourceUtilsPMX.loc(name));
    }

    private static Holder.Reference<GameTestInstance> registerFunction(BootstrapContext<GameTestInstance> context,
                                                                       ResourceKey<GameTestInstance> instanceKey,
                                                                       ResourceKey<Consumer<GameTestHelper>> funcKey) {
        return registerFunction(context, instanceKey, funcKey, TestUtilsPMX.DEFAULT_TEMPLATE);
    }

    private static Holder.Reference<GameTestInstance> registerFunction(BootstrapContext<GameTestInstance> context,
                                                                       ResourceKey<GameTestInstance> instanceKey,
                                                                       ResourceKey<Consumer<GameTestHelper>> funcKey,
                                                                       Identifier templateLoc) {
        HolderGetter<TestEnvironmentDefinition<?>> envs = context.lookup(Registries.TEST_ENVIRONMENT);
        return registerFunction(context, instanceKey, funcKey, TestDataBuilder.withEnvironment(TestEnvironmentsPM.DEFAULT, envs).template(templateLoc).build());
    }

    private static Holder.Reference<GameTestInstance> registerFunction(BootstrapContext<GameTestInstance> context,
                                                                       ResourceKey<GameTestInstance> instanceKey,
                                                                       ResourceKey<Consumer<GameTestHelper>> funcKey,
                                                                       TestData<Holder<TestEnvironmentDefinition<?>>> testData) {
        return context.register(instanceKey, new FunctionGameTestInstance(funcKey, testData));
    }
}
