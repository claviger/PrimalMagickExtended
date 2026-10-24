package com.claviger.primalmagickextended.test.features;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.features.FeaturePMX;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import com.claviger.primalmagickextended.common.research.ResearchEntriesPMX;
import com.claviger.primalmagickextended.common.research.StaleResearchTopicsPMX;
import com.claviger.primalmagickextended.common.spells.payloads.FindFamiliarSpellPayload;
import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.claviger.primalmagickextended.test.AbstractBaseTestPMX;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.verdantartifice.primalmagick.common.capabilities.IPlayerKnowledge;
import com.verdantartifice.primalmagick.common.capabilities.PlayerKnowledge;
import com.verdantartifice.primalmagick.common.registries.RegistryKeysPM;
import com.verdantartifice.primalmagick.common.research.ResearchEntry;
import com.verdantartifice.primalmagick.common.research.ResearchManager;
import com.verdantartifice.primalmagick.common.research.keys.ResearchEntryKey;
import com.verdantartifice.primalmagick.common.research.topics.AbstractResearchTopic;
import com.verdantartifice.primalmagick.common.research.topics.EntryResearchTopic;
import com.verdantartifice.primalmagick.common.research.topics.MainIndexResearchTopic;
import com.verdantartifice.primalmagick.platform.Services;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.stats.ServerRecipeBook;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tests for the config switches that turn whole features off: their defaults, the load conditions generated onto
 * each feature's research entries, recipes and recipe advancements, and that research and recipe keys left behind
 * by a disabled feature are harmless.
 */
public class FeatureToggleTests extends AbstractBaseTestPMX {
    private static final String CONDITIONS_KEY = "neoforge:conditions";
    private static final String CONDITION_TYPE = ConstantsPMX.MOD_ID + ":feature_enabled";
    private static final List<String> RUNES = List.of("weak_enhancer_rune", "strong_enhancer_rune", "infinity_rune", "gate_rune", "haste_rune");

    /** The expected feature of each of the mod's research entry files, by file name. */
    private static Map<String, FeaturePMX> expectedResearchEntries() {
        Map<String, FeaturePMX> expected = new HashMap<>();
        expected.put("familiar_collar", FeaturePMX.FAMILIARS);
        expected.put("find_familiar", FeaturePMX.FAMILIARS);
        expected.put("portal_travel", FeaturePMX.PORTALS);
        RUNES.forEach(rune -> expected.put(rune, FeaturePMX.PORTALS));
        return expected;
    }

    /** The expected feature of each of the mod's recipe files, by file name. */
    private static Map<String, FeaturePMX> expectedRecipes() {
        Map<String, FeaturePMX> expected = new HashMap<>();
        expected.put("familiar_collar", FeaturePMX.FAMILIARS);
        for (PortalColor color : PortalColor.values()) {
            expected.put(color.getSerializedName() + "_portal_catalyst", FeaturePMX.PORTALS);
            expected.put(color.getSerializedName() + "_portal_catalyst_2", FeaturePMX.PORTALS);
        }
        RUNES.forEach(rune -> expected.put(rune, FeaturePMX.PORTALS));
        return expected;
    }

    /** The expected feature of each of the mod's advancement files, by path below the advancement folder. */
    private static Map<String, FeaturePMX> expectedAdvancements() {
        Map<String, FeaturePMX> expected = new HashMap<>();
        for (PortalColor color : PortalColor.values()) {
            expected.put("recipes/misc/" + color.getSerializedName() + "_portal_catalyst_2", FeaturePMX.PORTALS);
        }
        return expected;
    }

    private static JsonElement readJson(Resource resource) {
        try (Reader reader = resource.openAsReader()) {
            return JsonParser.parseReader(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Reads every JSON file of this mod under the given data folder, keyed by its path below that folder without the
     * extension.
     */
    private static Map<String, JsonObject> readModFiles(GameTestHelper helper, String folder) {
        ResourceManager manager = helper.getLevel().getServer().getResourceManager();
        return manager.listResources(folder, id -> id.getNamespace().equals(ConstantsPMX.MOD_ID) && id.getPath().endsWith(".json"))
                .entrySet().stream().collect(Collectors.toMap(
                        e -> e.getKey().getPath().substring(folder.length() + 1, e.getKey().getPath().length() - ".json".length()),
                        e -> readJson(e.getValue()).getAsJsonObject()));
    }

    /** Asserts that every file in a folder is exactly the expected set, each carrying the condition of its feature. */
    private static void assertConditioned(GameTestHelper helper, String folder, Map<String, FeaturePMX> expected) {
        Map<String, JsonObject> files = readModFiles(helper, folder);
        assertValueEqual(helper, expected.keySet(), files.keySet(), folder + " files");
        files.forEach((name, json) -> {
            String what = folder + "/" + name;
            assertTrue(helper, json.has(CONDITIONS_KEY), what + " has no load conditions");
            JsonArray conditions = json.getAsJsonArray(CONDITIONS_KEY);
            assertValueEqual(helper, 1, conditions.size(), what + " condition count");
            JsonObject condition = conditions.get(0).getAsJsonObject();
            assertValueEqual(helper, CONDITION_TYPE, condition.get("type").getAsString(), what + " condition type");
            assertValueEqual(helper, expected.get(name).getSerializedName(), condition.get("feature").getAsString(), what + " condition feature");
        });
    }

    public static void features_are_enabled_by_default(GameTestHelper helper) {
        for (FeaturePMX feature : FeaturePMX.values()) {
            assertTrue(helper, ServicesPMX.CONFIG.isFeatureEnabled(feature), feature.getSerializedName() + " is not enabled by default");
        }
        helper.succeed();
    }

    public static void every_feature_research_entry_and_recipe_is_conditioned(GameTestHelper helper) {
        assertConditioned(helper, "primalmagick/research_entries", expectedResearchEntries());
        assertConditioned(helper, "recipe", expectedRecipes());
        assertConditioned(helper, "advancement", expectedAdvancements());

        // The feature map used at datagen agrees with the expected membership
        Map<String, FeaturePMX> mapped = ResearchEntriesPMX.FEATURES.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().location().getPath(), Map.Entry::getValue));
        assertValueEqual(helper, expectedResearchEntries(), mapped, "Research entry feature map");
        helper.succeed();
    }

    public static void opaque_tag_reference_is_optional(GameTestHelper helper) {
        ResourceManager manager = helper.getLevel().getServer().getResourceManager();
        ResourceLocation tagFile = ResourceLocation.fromNamespaceAndPath("primalmagick", "tags/primalmagick/research_entries/opaque.json");
        String portalTravel = ResearchEntriesPMX.PORTAL_TRAVEL.location().toString();
        int references = 0;
        for (Resource resource : manager.getResourceStack(tagFile)) {
            for (JsonElement value : readJson(resource).getAsJsonObject().getAsJsonArray("values")) {
                if (value.isJsonPrimitive()) {
                    assertFalse(helper, value.getAsString().equals(portalTravel), "Opaque tag requires " + portalTravel);
                } else if (value.getAsJsonObject().get("id").getAsString().equals(portalTravel)) {
                    assertFalse(helper, value.getAsJsonObject().get("required").getAsBoolean(), "Opaque tag reference to " + portalTravel + " is required");
                    references++;
                }
            }
        }
        assertValueEqual(helper, 1, references, "Optional opaque tag references to " + portalTravel);
        helper.succeed();
    }

    public static void stale_research_and_recipe_keys_are_harmless(GameTestHelper helper) {
        ServerPlayer player = makeMockServerPlayer(helper);
        RegistryAccess registryAccess = helper.getLevel().registryAccess();
        ResourceKey<ResearchEntry> missing = ResourceKey.create(RegistryKeysPM.RESEARCH_ENTRIES, ResourceUtilsPMX.loc("no_such_entry"));
        ResearchEntryKey staleKey = new ResearchEntryKey(missing);
        assertFalse(helper, registryAccess.registryOrThrow(RegistryKeysPM.RESEARCH_ENTRIES).containsKey(missing), "The missing entry exists");

        // Knowledge left behind by a disabled feature: a known, flagged entry the Grimoire was last open at
        IPlayerKnowledge knowledge = Services.CAPABILITIES.knowledge(player).orElseThrow();
        assertTrue(helper, knowledge.addResearch(staleKey), "Could not add the stale research key");
        knowledge.setResearchStage(staleKey, 2);
        for (IPlayerKnowledge.ResearchFlag flag : IPlayerKnowledge.ResearchFlag.values()) {
            knowledge.addResearchFlag(staleKey, flag);
        }
        AbstractResearchTopic<?> staleTopic = new EntryResearchTopic(staleKey, 1);
        knowledge.setLastResearchTopic(staleTopic);
        knowledge.setResearchTopicHistory(List.of(MainIndexResearchTopic.INSTANCE, staleTopic));

        // Queries the Grimoire and the research table make about known research
        assertTrue(helper, knowledge.isResearchComplete(registryAccess, staleKey), "A known key without an entry is not complete");
        assertFalse(helper, staleTopic.isUnread(player), "A topic without an entry is unread");

        // Saving and loading the player, and syncing knowledge to the client (the sync packet's stream codec is
        // round-tripped in the NeoForge module, which has the non-deprecated byte buffer constructor)
        if (knowledge instanceof PlayerKnowledge playerKnowledge) {
            PlayerKnowledge loaded = new PlayerKnowledge();
            loaded.deserializeNBT(registryAccess, playerKnowledge.serializeNBT(registryAccess));
            assertTrue(helper, loaded.isResearchKnown(staleKey), "Stale key lost on save and load");
            assertValueEqual(helper, staleTopic, loaded.getLastResearchTopic(), "Last topic after save and load");
        } else {
            throw new IllegalStateException("Unexpected knowledge implementation " + knowledge.getClass().getName());
        }
        knowledge.sync(player);
        ResearchManager.progressResearch(player, staleKey);
        assertTrue(helper, ResearchManager.revokeResearch(player, staleKey), "Could not revoke the stale research key");

        // The recipe book: a stale recipe is dropped when the player loads, and sending a book that still holds a
        // stale id to the client does not fail
        ResourceLocation missingRecipe = ResourceUtilsPMX.loc("no_such_recipe");
        ListTag staleIds = new ListTag();
        staleIds.add(StringTag.valueOf(missingRecipe.toString()));
        CompoundTag savedBook = new CompoundTag();
        savedBook.put("recipes", staleIds);
        savedBook.put("toBeDisplayed", staleIds.copy());
        ServerRecipeBook book = player.getRecipeBook();
        book.fromNbt(savedBook, helper.getLevel().getServer().getRecipeManager());
        assertFalse(helper, book.contains(missingRecipe), "Stale recipe kept on load");
        ServerRecipeBook staleBook = new ServerRecipeBook() {
            {
                this.add(missingRecipe);
                this.addHighlight(missingRecipe);
            }
        };
        assertTrue(helper, staleBook.contains(missingRecipe), "Could not add the stale recipe id");
        // Only proves the server sends the book without failing; the client drops unknown ids
        // (ClientPacketListener.handleAddOrRemoveRecipes looks each up with recipeManager.byKey(...).ifPresent),
        // which a game test cannot exercise
        staleBook.sendInitialRecipeBook(player);
        helper.succeed();
    }

    public static void stale_grimoire_topics_are_cleared(GameTestHelper helper) {
        ServerPlayer player = makeMockServerPlayer(helper);
        RegistryAccess registryAccess = helper.getLevel().registryAccess();
        IPlayerKnowledge knowledge = Services.CAPABILITIES.knowledge(player).orElseThrow();
        AbstractResearchTopic<?> staleTopic = new EntryResearchTopic(ResourceKey.create(RegistryKeysPM.RESEARCH_ENTRIES, ResourceUtilsPMX.loc("no_such_entry")), 1);
        AbstractResearchTopic<?> liveTopic = new EntryResearchTopic(ResearchEntriesPMX.PORTAL_TRAVEL, 0);

        // A Grimoire last open at a live entry is left alone
        knowledge.setLastResearchTopic(liveTopic);
        knowledge.setResearchTopicHistory(List.of(MainIndexResearchTopic.INSTANCE, liveTopic));
        assertFalse(helper, StaleResearchTopicsPMX.clear(knowledge, registryAccess), "Cleared live topics");
        assertValueEqual(helper, liveTopic, knowledge.getLastResearchTopic(), "Live last topic");

        // A Grimoire last open at a missing entry goes back to the index, and the Back history skips the missing entry
        knowledge.setLastResearchTopic(staleTopic);
        knowledge.setResearchTopicHistory(List.of(MainIndexResearchTopic.INSTANCE, staleTopic, liveTopic, staleTopic));
        StaleResearchTopicsPMX.clearFor(player);
        assertValueEqual(helper, MainIndexResearchTopic.INSTANCE, knowledge.getLastResearchTopic(), "Last topic after clearing");
        assertValueEqual(helper, List.of(MainIndexResearchTopic.INSTANCE, liveTopic), List.copyOf(knowledge.getResearchTopicHistory()), "History after clearing");
        assertTrue(helper, ResearchManager.isSyncScheduled(player), "No knowledge sync scheduled after clearing");
        assertFalse(helper, StaleResearchTopicsPMX.clear(knowledge, registryAccess), "Cleared twice");
        helper.succeed();
    }

    public static void find_familiar_payload_needs_its_entry_loaded(GameTestHelper helper) {
        ServerPlayer player = makeMockServerPlayer(helper);
        ResearchManager.forceGrantWithAllParents(player, ResearchEntriesPMX.FIND_FAMILIAR);
        assertTrue(helper, FindFamiliarSpellPayload.getRequirement().isMetBy(player), "Payload unavailable with its research known");
        assertTrue(helper, FindFamiliarSpellPayload.getRequirement().contains(new ResearchEntryKey(ResearchEntriesPMX.FIND_FAMILIAR)), "Payload not gated by Find Familiar");

        // Research known from before its feature was turned off, whose entry is no longer loaded, does not count
        ResourceKey<ResearchEntry> missing = ResourceKey.create(RegistryKeysPM.RESEARCH_ENTRIES, ResourceUtilsPMX.loc("no_such_entry"));
        IPlayerKnowledge knowledge = Services.CAPABILITIES.knowledge(player).orElseThrow();
        knowledge.addResearch(new ResearchEntryKey(missing));
        assertFalse(helper, FindFamiliarSpellPayload.loadedResearch(missing).isMetBy(player), "Requirement met by research whose entry is missing");
        assertFalse(helper, FindFamiliarSpellPayload.loadedResearch(ResearchEntriesPMX.FAMILIAR_COLLAR).isMetBy(makeMockServerPlayer(helper)), "Requirement met without the research");
        helper.succeed();
    }
}
