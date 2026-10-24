package com.claviger.primalmagickextended.test;

import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.claviger.primalmagickextended.test.features.FeatureConditionTests;
import com.claviger.primalmagickextended.test.features.FeatureToggleNeoforgeTests;
import com.verdantartifice.primalmagick.common.registries.IRegistryItem;
import net.minecraft.gametest.framework.GameTestHelper;

import java.util.function.Consumer;

/**
 * Registrations of the game test functions that exercise NeoForge-only code. They share the registry of
 * {@link TestFunctionsPMX}, and each is paired with a test instance in {@link TestInstancesPMX}, which refers to it by
 * name because the common module cannot see this class.
 */
public class TestFunctionsPMXNeoforge {
    public static void init() {
        // Referencing this method loads the class, so its fields are registered before the registry event fires
    }

    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FEATURE_CONDITION_FOLLOWS_CONFIG = register(TestInstancesPMX.FEATURE_CONDITION_FOLLOWS_CONFIG_FUNCTION, FeatureConditionTests::feature_condition_follows_config);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FEATURE_CONDITION_KEEPS_ENTRIES_LISTED_BY_SERVER = register(TestInstancesPMX.FEATURE_CONDITION_KEEPS_ENTRIES_LISTED_BY_SERVER_FUNCTION, FeatureConditionTests::feature_condition_keeps_entries_listed_by_server);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FEATURE_CONDITION_ROUND_TRIPS_THROUGH_CODEC = register(TestInstancesPMX.FEATURE_CONDITION_ROUND_TRIPS_THROUGH_CODEC_FUNCTION, FeatureConditionTests::feature_condition_round_trips_through_codec);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> CONDITIONED_RESEARCH_ENTRY_LOADS_WHILE_ENABLED = register(TestInstancesPMX.CONDITIONED_RESEARCH_ENTRY_LOADS_WHILE_ENABLED_FUNCTION, FeatureConditionTests::conditioned_research_entry_loads_while_enabled);

    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> PLAIN_REGISTRY_OPS_HAVE_NO_CONDITION_CONTEXT = register(TestInstancesPMX.PLAIN_REGISTRY_OPS_HAVE_NO_CONDITION_CONTEXT_FUNCTION, FeatureConditionTests::plain_registry_ops_have_no_condition_context);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> FEATURE_SWITCHES_DEFAULT_TO_ENABLED = register(TestInstancesPMX.FEATURE_SWITCHES_DEFAULT_TO_ENABLED_FUNCTION, FeatureToggleNeoforgeTests::feature_switches_default_to_enabled);
    public static final IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> STALE_RESEARCH_KEY_SURVIVES_SYNC_PACKET = register(TestInstancesPMX.STALE_RESEARCH_KEY_SURVIVES_SYNC_PACKET_FUNCTION, FeatureToggleNeoforgeTests::stale_research_key_survives_sync_packet);

    private static IRegistryItem<Consumer<GameTestHelper>, Consumer<GameTestHelper>> register(String name, Consumer<GameTestHelper> testFunction) {
        return ServicesPMX.TEST_FUNCTIONS_REGISTRY.register(name, () -> testFunction);
    }
}
