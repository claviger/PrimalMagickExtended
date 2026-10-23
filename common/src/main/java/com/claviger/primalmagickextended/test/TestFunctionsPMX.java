package com.claviger.primalmagickextended.test;

import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.claviger.primalmagickextended.test.familiars.FamiliarCollarTests;
import com.claviger.primalmagickextended.test.familiars.FamiliarCommandTests;
import com.claviger.primalmagickextended.test.familiars.FamiliarDataTests;
import com.claviger.primalmagickextended.test.familiars.FamiliarManaEfficiencyTests;
import com.claviger.primalmagickextended.test.familiars.FamiliarManagerTests;
import com.claviger.primalmagickextended.test.familiars.FamiliarPolymorphTests;
import com.claviger.primalmagickextended.test.familiars.FamiliarProtectionTests;
import com.claviger.primalmagickextended.test.familiars.FamiliarResearchTests;
import com.claviger.primalmagickextended.test.familiars.FindFamiliarPayloadTests;
import com.verdantartifice.primalmagick.common.registries.IRegistryItem;
import net.minecraft.gametest.framework.GameTestHelper;

import java.util.function.Consumer;

/**
 * Registrations of every game test function in the mod. Each function is paired with a test
 * instance in {@link TestInstancesPMX}.
 */
public class TestFunctionsPMX {
    public static void init() {
        // Pass the service initialization through this class so it gets class loaded and fields registered
        ServicesPMX.TEST_FUNCTIONS_REGISTRY.init();
    }

    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> CANARY = register("canary", CanaryTest::canary);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FAMILIAR_COLLAR_ITEM_IS_REGISTERED = register("familiar_collar_item_is_registered", FamiliarCollarTests::familiar_collar_item_is_registered);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> RESEARCH_ENTRIES_ARE_REGISTERED_WITH_EXPECTED_PARENTS = register("research_entries_are_registered_with_expected_parents", FamiliarResearchTests::research_entries_are_registered_with_expected_parents);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> RITUAL_RECIPE_HAS_EXPECTED_INGREDIENTS_AND_REQUIREMENT = register("ritual_recipe_has_expected_ingredients_and_requirement", FamiliarResearchTests::ritual_recipe_has_expected_ingredients_and_requirement);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> RITUAL_RECIPE_REQUIREMENT_UNMET_WITHOUT_RESEARCH = register("ritual_recipe_requirement_unmet_without_research", FamiliarResearchTests::ritual_recipe_requirement_unmet_without_research);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> RITUAL_ALTAR_FINISH_CRAFT_YIELDS_FAMILIAR_COLLAR = register("ritual_altar_finish_craft_yields_familiar_collar", FamiliarResearchTests::ritual_altar_finish_craft_yields_familiar_collar);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FAMILIAR_COLLAR_AFFINITY_HAS_EXPECTED_SOURCES = register("familiar_collar_affinity_has_expected_sources", FamiliarResearchTests::familiar_collar_affinity_has_expected_sources);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> PLAYER_FAMILIAR_ROUND_TRIPS_THROUGH_CODEC = register("player_familiar_round_trips_through_codec", FamiliarDataTests::player_familiar_round_trips_through_codec);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> PLAYER_FAMILIAR_ATTACHMENT_IS_AVAILABLE_AND_EMPTY_BY_DEFAULT = register("player_familiar_attachment_is_available_and_empty_by_default", FamiliarDataTests::player_familiar_attachment_is_available_and_empty_by_default);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ENTITY_BINDING_CAN_BE_SET_READ_AND_CLEARED = register("entity_binding_can_be_set_read_and_cleared", FamiliarDataTests::entity_binding_can_be_set_read_and_cleared);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ENTITY_BINDING_SURVIVES_ENTITY_SAVE_AND_LOAD = register("entity_binding_survives_entity_save_and_load", FamiliarDataTests::entity_binding_survives_entity_save_and_load);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BIND_MARKS_PET_AND_STORES_SNAPSHOT = register("bind_marks_pet_and_stores_snapshot", FamiliarManagerTests::bind_marks_pet_and_stores_snapshot);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BIND_DOES_NOT_CONSUME_COLLAR_IN_CREATIVE = register("bind_does_not_consume_collar_in_creative", FamiliarManagerTests::bind_does_not_consume_collar_in_creative);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> DISMISS_REMOVES_ENTITY_AND_UPDATES_SNAPSHOT = register("dismiss_removes_entity_and_updates_snapshot", FamiliarManagerTests::dismiss_removes_entity_and_updates_snapshot);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> SUMMON_RESTORES_FAMILIAR_BESIDE_PLAYER_WITH_NAME_AND_NEW_IDENTITY = register("summon_restores_familiar_beside_player_with_name_and_new_identity", FamiliarManagerTests::summon_restores_familiar_beside_player_with_name_and_new_identity);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> TOGGLE_DISMISSES_WHEN_IN_PLAY_AND_SUMMONS_WHEN_ABSENT = register("toggle_dismisses_when_in_play_and_summons_when_absent", FamiliarManagerTests::toggle_dismisses_when_in_play_and_summons_when_absent);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> TOGGLE_TWICE_IN_ONE_TICK_TOGGLES_ONCE = register("toggle_twice_in_one_tick_toggles_once", FamiliarManagerTests::toggle_twice_in_one_tick_toggles_once);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> TOGGLE_WITHIN_DEBOUNCE_WINDOW_IS_IGNORED = register("toggle_within_debounce_window_is_ignored", FamiliarManagerTests::toggle_within_debounce_window_is_ignored);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> SUMMON_WITH_NO_FAMILIAR_SENDS_MESSAGE = register("summon_with_no_familiar_sends_message", FamiliarManagerTests::summon_with_no_familiar_sends_message);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> DISMISS_EJECTS_RIDING_OWNER = register("dismiss_ejects_riding_owner", FamiliarManagerTests::dismiss_ejects_riding_owner);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BIND_SNAPSHOT_STRIPS_PASSENGERS = register("bind_snapshot_strips_passengers", FamiliarManagerTests::bind_snapshot_strips_passengers);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> SUMMON_WHEN_IN_PLAY_DOES_NOT_DUPLICATE = register("summon_when_in_play_does_not_duplicate", FamiliarManagerTests::summon_when_in_play_does_not_duplicate);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> SUMMON_CLEARS_HARMFUL_EFFECTS = register("summon_clears_harmful_effects", FamiliarManagerTests::summon_clears_harmful_effects);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> DISMISS_STORES_LIVE_ENTITY_TYPE = register("dismiss_stores_live_entity_type", FamiliarManagerTests::dismiss_stores_live_entity_type);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COLLAR_BINDS_OWN_TAMED_WOLF = register("collar_binds_own_tamed_wolf", FamiliarCollarTests::collar_binds_own_tamed_wolf);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COLLAR_BINDS_OWN_TAMED_HORSE = register("collar_binds_own_tamed_horse", FamiliarCollarTests::collar_binds_own_tamed_horse);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COLLAR_BINDS_TRUSTING_FOX = register("collar_binds_trusting_fox", FamiliarCollarTests::collar_binds_trusting_fox);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COLLAR_IGNORES_UNTRUSTING_FOX = register("collar_ignores_untrusting_fox", FamiliarCollarTests::collar_ignores_untrusting_fox);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COLLAR_IGNORES_UNTAMED_WOLF = register("collar_ignores_untamed_wolf", FamiliarCollarTests::collar_ignores_untamed_wolf);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COLLAR_IGNORES_WOLF_OWNED_BY_ANOTHER_PLAYER = register("collar_ignores_wolf_owned_by_another_player", FamiliarCollarTests::collar_ignores_wolf_owned_by_another_player);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COLLAR_IGNORES_NON_CANDIDATE_ANIMAL = register("collar_ignores_non_candidate_animal", FamiliarCollarTests::collar_ignores_non_candidate_animal);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COLLAR_ON_OWN_FAMILIAR_DOES_NOTHING = register("collar_on_own_familiar_does_nothing", FamiliarCollarTests::collar_on_own_familiar_does_nothing);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COLLAR_ON_ANOTHER_PLAYERS_FAMILIAR_DOES_NOTHING = register("collar_on_another_players_familiar_does_nothing", FamiliarCollarTests::collar_on_another_players_familiar_does_nothing);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COLLAR_CONVERTS_OCELOT_INTO_OCELOT_CAT_FAMILIAR = register("collar_converts_ocelot_into_ocelot_cat_familiar", FamiliarCollarTests::collar_converts_ocelot_into_ocelot_cat_familiar);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> OCELOT_CAT_VARIANT_IS_REGISTERED = register("ocelot_cat_variant_is_registered", FamiliarCollarTests::ocelot_cat_variant_is_registered);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COLLAR_NAMES_UNNAMED_OCELOT_CAT_OCELOT = register("collar_names_unnamed_ocelot_cat_ocelot", FamiliarCollarTests::collar_names_unnamed_ocelot_cat_ocelot);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COLLAR_KEEPS_NAME_TAG_NAME_WHEN_CONVERTING_OCELOT = register("collar_keeps_name_tag_name_when_converting_ocelot", FamiliarCollarTests::collar_keeps_name_tag_name_when_converting_ocelot);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COLLAR_CONVERTS_LEASHED_OCELOT_KEEPING_LEASH = register("collar_converts_leashed_ocelot_keeping_leash", FamiliarCollarTests::collar_converts_leashed_ocelot_keeping_leash);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> CLIENT_SIDE_COLLAR_USE_IS_PREDICTED_FOR_OWNED_PET = register("client_side_collar_use_is_predicted_for_owned_pet", FamiliarCollarTests::client_side_collar_use_is_predicted_for_owned_pet);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FAMILIAR_DISMISSES_INSTEAD_OF_TAKING_DAMAGE = register("familiar_dismisses_instead_of_taking_damage", FamiliarProtectionTests::familiar_dismisses_instead_of_taking_damage);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FAMILIAR_DISMISSES_ON_GENERIC_KILL = register("familiar_dismisses_on_generic_kill", FamiliarProtectionTests::familiar_dismisses_on_generic_kill);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FAMILIAR_DISMISSES_ON_FIRE_DAMAGE = register("familiar_dismisses_on_fire_damage", FamiliarProtectionTests::familiar_dismisses_on_fire_damage);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BINDING_NEW_FAMILIAR_RELEASES_OLD_ONE_UNHARMED = register("binding_new_familiar_releases_old_one_unharmed", FamiliarProtectionTests::binding_new_familiar_releases_old_one_unharmed);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BINDING_NEW_FAMILIAR_SUMMONS_DISMISSED_OLD_ONE_FIRST = register("binding_new_familiar_summons_dismissed_old_one_first", FamiliarProtectionTests::binding_new_familiar_summons_dismissed_old_one_first);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> STALE_GENERATION_FAMILIAR_IS_REMOVED_ON_JOIN = register("stale_generation_familiar_is_removed_on_join", FamiliarProtectionTests::stale_generation_familiar_is_removed_on_join);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> CURRENT_GENERATION_FAMILIAR_IS_ADOPTED_ON_JOIN = register("current_generation_familiar_is_adopted_on_join", FamiliarProtectionTests::current_generation_familiar_is_adopted_on_join);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> DUPLICATE_FAMILIAR_IS_REMOVED_ON_JOIN = register("duplicate_familiar_is_removed_on_join", FamiliarProtectionTests::duplicate_familiar_is_removed_on_join);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FAMILIAR_WITH_OFFLINE_OWNER_IS_REMOVED_ON_JOIN = register("familiar_with_offline_owner_is_removed_on_join", FamiliarProtectionTests::familiar_with_offline_owner_is_removed_on_join);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> LOGOUT_DISMISSES_FAMILIAR_IN_PLAY = register("logout_dismisses_familiar_in_play", FamiliarProtectionTests::logout_dismisses_familiar_in_play);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> CHUNK_UNLOAD_STORES_FAMILIAR_AND_ROTATES_GENERATION = register("chunk_unload_stores_familiar_and_rotates_generation", FamiliarProtectionTests::chunk_unload_stores_familiar_and_rotates_generation);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> CHUNK_HIDDEN_LEAVE_STORES_FAMILIAR_WITHOUT_REMOVING_IT = register("chunk_hidden_leave_stores_familiar_without_removing_it", FamiliarProtectionTests::chunk_hidden_leave_stores_familiar_without_removing_it);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> LEAVE_LEVEL_IGNORES_DISCARDED_FAMILIAR = register("leave_level_ignores_discarded_familiar", FamiliarProtectionTests::leave_level_ignores_discarded_familiar);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> STALE_GENERATION_FAMILIAR_IS_REJECTED_BY_REAL_JOIN_EVENT = register("stale_generation_familiar_is_rejected_by_real_join_event", FamiliarProtectionTests::stale_generation_familiar_is_rejected_by_real_join_event);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> STALE_BOUND_MOB_IS_DISCARDED_BY_PERIODIC_CHECK = register("stale_bound_mob_is_discarded_by_periodic_check", FamiliarProtectionTests::stale_bound_mob_is_discarded_by_periodic_check);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> DEATH_SAFETY_NET_DISMISSES_FAMILIAR = register("death_safety_net_dismisses_familiar", FamiliarProtectionTests::death_safety_net_dismisses_familiar);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ZERO_DAMAGE_HIT_DOES_NOT_DISMISS = register("zero_damage_hit_does_not_dismiss", FamiliarProtectionTests::zero_damage_hit_does_not_dismiss);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> STALE_FAMILIAR_IN_BOAT_IS_SWEPT = register("stale_familiar_in_boat_is_swept", FamiliarProtectionTests::stale_familiar_in_boat_is_swept);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FIND_FAMILIAR_PAYLOAD_IS_REGISTERED = register("find_familiar_payload_is_registered", FindFamiliarPayloadTests::find_familiar_payload_is_registered);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FIND_FAMILIAR_PAYLOAD_TOGGLES_FOR_CASTER = register("find_familiar_payload_toggles_for_caster", FindFamiliarPayloadTests::find_familiar_payload_toggles_for_caster);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FIND_FAMILIAR_PAYLOAD_IGNORES_BURST_POINTS = register("find_familiar_payload_ignores_burst_points", FindFamiliarPayloadTests::find_familiar_payload_ignores_burst_points);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FIND_FAMILIAR_PAYLOAD_WITH_NO_FAMILIAR_DOES_NOTHING = register("find_familiar_payload_with_no_familiar_does_nothing", FindFamiliarPayloadTests::find_familiar_payload_with_no_familiar_does_nothing);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FIND_FAMILIAR_PAYLOAD_HIDDEN_WITHOUT_RESEARCH_AND_SHOWN_WITH_IT = register("find_familiar_payload_hidden_without_research_and_shown_with_it", FindFamiliarPayloadTests::find_familiar_payload_hidden_without_research_and_shown_with_it);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FIND_FAMILIAR_WAND_CAST_DEDUCTS_MANA_AND_DISMISSES = register("find_familiar_wand_cast_deducts_mana_and_dismisses", FindFamiliarPayloadTests::find_familiar_wand_cast_deducts_mana_and_dismisses);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FIND_FAMILIAR_MINE_DOES_NOT_TOGGLE = register("find_familiar_mine_does_not_toggle", FindFamiliarPayloadTests::find_familiar_mine_does_not_toggle);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FIND_FAMILIAR_SPELL_PACKAGE_ROUND_TRIPS_THROUGH_CODECS = register("find_familiar_spell_package_round_trips_through_codecs", FindFamiliarPayloadTests::find_familiar_spell_package_round_trips_through_codecs);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FAMILIAR_NEARBY_ADDS_FIVE_TO_WAND_COST_MODIFIER = register("familiar_nearby_adds_five_to_wand_cost_modifier", FamiliarManaEfficiencyTests::familiar_nearby_adds_five_to_wand_cost_modifier);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FAMILIAR_FAR_AWAY_ADDS_NOTHING = register("familiar_far_away_adds_nothing", FamiliarManaEfficiencyTests::familiar_far_away_adds_nothing);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> DISMISSED_FAMILIAR_ADDS_NOTHING = register("dismissed_familiar_adds_nothing", FamiliarManaEfficiencyTests::dismissed_familiar_adds_nothing);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FAMILIAR_BONUS_REDUCES_SPELL_MANA_CONSUMED = register("familiar_bonus_reduces_spell_mana_consumed", FamiliarManaEfficiencyTests::familiar_bonus_reduces_spell_mana_consumed);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> POLYMORPH_SWAP_BACK_KEEPS_CURRENT_BINDING = register("polymorph_swap_back_keeps_current_binding", FamiliarPolymorphTests::polymorph_swap_back_keeps_current_binding);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> RELEASE_REWRITES_QUEUED_SWAPPER_BINDING = register("release_rewrites_queued_swapper_binding", FamiliarPolymorphTests::release_rewrites_queued_swapper_binding);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> RELEASE_WITH_UNRESTORABLE_SNAPSHOT_CLEARS_FAMILIAR_AND_ALLOWS_BIND = register("release_with_unrestorable_snapshot_clears_familiar_and_allows_bind", FamiliarProtectionTests::release_with_unrestorable_snapshot_clears_familiar_and_allows_bind);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> RELEASE_WITH_REFUSED_JOIN_ABORTS_BIND_AND_KEEPS_FAMILIAR = register("release_with_refused_join_aborts_bind_and_keeps_familiar", FamiliarProtectionTests::release_with_refused_join_aborts_bind_and_keeps_familiar);

    /**
     * Registers a game test function under the given name in this mod's namespace. Keep argument evaluation
     * inside the lambda body (e.g. {@code helper -> Tests.foo(helper, Sources.EARTH)}); never resolve registry
     * values when the function is registered, because this runs during mod construction.
     */
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> SUMMON_COMMAND_SUMMONS_DISMISSED_FAMILIAR = register("summon_command_summons_dismissed_familiar", FamiliarCommandTests::summon_command_summons_dismissed_familiar);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> SUMMON_COMMAND_REFUSES_WHEN_FAMILIAR_IS_ALREADY_IN_PLAY = register("summon_command_refuses_when_familiar_is_already_in_play", FamiliarCommandTests::summon_command_refuses_when_familiar_is_already_in_play);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> DISMISS_COMMAND_DISMISSES_FAMILIAR_IN_PLAY = register("dismiss_command_dismisses_familiar_in_play", FamiliarCommandTests::dismiss_command_dismisses_familiar_in_play);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> CLEAR_COMMAND_REVERTS_FAMILIAR_IN_PLAY_TO_ORDINARY_PET = register("clear_command_reverts_familiar_in_play_to_ordinary_pet", FamiliarCommandTests::clear_command_reverts_familiar_in_play_to_ordinary_pet);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> CLEAR_COMMAND_RESTORES_DISMISSED_FAMILIAR_AS_ORDINARY_PET = register("clear_command_restores_dismissed_familiar_as_ordinary_pet", FamiliarCommandTests::clear_command_restores_dismissed_familiar_as_ordinary_pet);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COMMANDS_FAIL_WHEN_THERE_IS_NO_FAMILIAR = register("commands_fail_when_there_is_no_familiar", FamiliarCommandTests::commands_fail_when_there_is_no_familiar);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COMMANDS_REQUIRE_GAME_MASTER_PERMISSION_AND_ACCEPT_TARGETS_AND_ALIAS = register("commands_require_game_master_permission_and_accept_targets_and_alias", FamiliarCommandTests::commands_require_game_master_permission_and_accept_targets_and_alias);

    private static IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> register(String name, Consumer<GameTestHelper> function) {
        return ServicesPMX.TEST_FUNCTIONS_REGISTRY.register(name, () -> function);
    }
}
