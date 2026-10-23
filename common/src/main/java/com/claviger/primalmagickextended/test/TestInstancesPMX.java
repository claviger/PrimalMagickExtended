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
    }

    private static ResourceKey<GameTestInstance> createInstanceKey(String name) {
        return ResourceKey.create(Registries.TEST_INSTANCE, ResourceUtilsPMX.loc(name));
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
