package com.claviger.primalmagickextended.test.features;

import com.claviger.primalmagickextended.common.conditions.ConditionsPMXNeoforge;
import com.claviger.primalmagickextended.common.conditions.FeatureEnabledCondition;
import com.claviger.primalmagickextended.common.features.FeaturePMX;
import com.claviger.primalmagickextended.common.research.ResearchEntriesPMX;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.verdantartifice.primalmagick.common.research.ResearchEntry;
import com.verdantartifice.primalmagick.test.AbstractBaseTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Optional;

/**
 * Tests for the feature load condition, which lives in the NeoForge module. The config itself is never changed:
 * the condition's false path is driven through a stand-in for the config.
 */
public class FeatureConditionTests extends AbstractBaseTest {
    public static void feature_condition_follows_config(GameTestHelper helper) {
        for (FeaturePMX feature : FeaturePMX.values()) {
            String name = feature.getSerializedName();
            assertFalse(helper, new FeatureEnabledCondition(feature, f -> false).test(ICondition.IContext.TAGS_INVALID), name + " condition holds while turned off");
            assertTrue(helper, new FeatureEnabledCondition(feature, f -> true).test(ICondition.IContext.TAGS_INVALID), name + " condition fails while turned on");
            assertFalse(helper, new FeatureEnabledCondition(feature, f -> f != feature).test(ICondition.IContext.TAGS_INVALID), name + " condition asks about another feature");
            // The default config turns every feature on
            assertTrue(helper, new FeatureEnabledCondition(feature).test(ICondition.IContext.TAGS_INVALID), name + " condition fails under the default config");
        }
        helper.succeed();
    }

    public static void feature_condition_keeps_entries_listed_by_server(GameTestHelper helper) {
        // A client decoding entries a server listed has no condition context and keeps them, whatever its own config
        for (FeaturePMX feature : FeaturePMX.values()) {
            assertTrue(helper, new FeatureEnabledCondition(feature, f -> false).test(ICondition.IContext.EMPTY), feature.getSerializedName() + " entry dropped on the client");
        }
        helper.succeed();
    }

    public static void plain_registry_ops_have_no_condition_context(GameTestHelper helper) {
        // FeatureEnabledCondition keeps entries decoded without a condition context. NetworkRegistryLoadTask decodes
        // with plain RegistryOps, so those must keep yielding IContext.EMPTY, while data pack loading has a context
        RegistryOps<JsonElement> plain = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().registryAccess());
        ICondition.IContext plainContext = ConditionalOps.retrieveContext().codec().decode(plain, plain.emptyMap()).getOrThrow().getFirst();
        assertTrue(helper, plainContext == ICondition.IContext.EMPTY, "Plain registry ops no longer yield the empty condition context");
        RegistryOps<JsonElement> conditional = new ConditionalOps<>(plain, ICondition.IContext.TAGS_INVALID);
        ICondition.IContext conditionalContext = ConditionalOps.retrieveContext().codec().decode(conditional, conditional.emptyMap()).getOrThrow().getFirst();
        assertTrue(helper, conditionalContext == ICondition.IContext.TAGS_INVALID, "Conditional ops lost their condition context");
        helper.succeed();
    }

    public static void feature_condition_round_trips_through_codec(GameTestHelper helper) {
        assertTrue(helper, ConditionsPMXNeoforge.FEATURE_ENABLED.isBound(), "Condition codec is not registered");
        for (FeaturePMX feature : FeaturePMX.values()) {
            JsonElement json = ICondition.CODEC.encodeStart(JsonOps.INSTANCE, new FeatureEnabledCondition(feature)).getOrThrow();
            JsonObject expected = new JsonObject();
            expected.addProperty("type", "primalmagickextended:feature_enabled");
            expected.addProperty("feature", feature.getSerializedName());
            assertValueEqual(helper, expected, json, feature.getSerializedName() + " condition JSON");
            ICondition decoded = ICondition.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
            FeatureEnabledCondition condition = assertInstanceOf(helper, decoded, FeatureEnabledCondition.class, feature.getSerializedName() + " decoded condition type");
            assertValueEqual(helper, feature, condition.feature(), "Decoded condition feature");
        }
        helper.succeed();
    }

    public static void conditioned_research_entry_loads_while_enabled(GameTestHelper helper) {
        // Decode a generated research entry the way NeoForge loads data pack registries; with the feature on it loads
        Identifier file = Identifier.fromNamespaceAndPath(ResearchEntriesPMX.PORTAL_TRAVEL.identifier().getNamespace(),
                "primalmagick/research_entries/" + ResearchEntriesPMX.PORTAL_TRAVEL.identifier().getPath() + ".json");
        Resource resource = helper.getLevel().getServer().getResourceManager().getResource(file).orElseThrow(() -> new IllegalStateException("Missing " + file));
        JsonElement json;
        try (Reader reader = resource.openAsReader()) {
            json = JsonParser.parseReader(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        RegistryOps<JsonElement> ops = new ConditionalOps<>(RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().registryAccess()), ICondition.IContext.TAGS_INVALID);
        Optional<ResearchEntry> entry = ConditionalOps.createConditionalCodec(ResearchEntry.codec()).parse(ops, json).getOrThrow();
        assertTrue(helper, entry.isPresent(), "Portal travel skipped with portals on");
        assertValueEqual(helper, ResearchEntriesPMX.PORTAL_TRAVEL, entry.get().key().getRootKey(), "Decoded entry key");
        helper.succeed();
    }
}
