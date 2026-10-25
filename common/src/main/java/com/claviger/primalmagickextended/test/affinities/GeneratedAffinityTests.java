package com.claviger.primalmagickextended.test.affinities;

import com.claviger.primalmagickextended.common.affinities.GeneratedAffinities;
import com.claviger.primalmagickextended.test.AbstractBaseTestPMX;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.verdantartifice.primalmagick.common.affinities.AffinityManager;
import com.verdantartifice.primalmagick.common.affinities.IAffinity;
import com.verdantartifice.primalmagick.common.commands.PrimalMagickCommand;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Tests for generated affinities. Primal Magick 6.0.7's data covers every natural block of Minecraft 1.21.1, so these
 * tests regenerate as if its data left out the mangrove log family, then put the real data back.
 */
public class GeneratedAffinityTests extends AbstractBaseTestPMX {
    private static final ResourceLocation MANGROVE_LOG = ResourceLocation.withDefaultNamespace("mangrove_log");
    /** The mangrove items whose data pack affinities the tests leave out. */
    private static final Set<ResourceLocation> LEFT_OUT = Set.of(MANGROVE_LOG, ResourceLocation.withDefaultNamespace("mangrove_wood"),
            ResourceLocation.withDefaultNamespace("stripped_mangrove_log"), ResourceLocation.withDefaultNamespace("stripped_mangrove_wood"),
            ResourceLocation.withDefaultNamespace("mangrove_planks"));

    public static void generated_affinities_are_registered_and_yield_to_data(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        assertTrue(helper, GeneratedAffinities.lastRun() != null, "Affinities were not generated when the server started");
        withoutMangroveData(server, run -> {
            // A data pack entry always wins: nothing it defines is generated
            Set<ResourceLocation> dataTargets = dataPackItemTargets(server);
            dataTargets.removeAll(LEFT_OUT);
            for (ResourceLocation id : run.generated().keySet()) {
                assertFalse(helper, dataTargets.contains(id), "Generated an affinity for " + id + ", which a data pack defines");
            }

            // Every generated affinity is what Primal Magick now reports
            run.generated().forEach((id, generated) ->
                    assertValueEqual(helper, generated.total(), total(server, id), "Primal Magick's affinity of generated " + id));

            // The mangrove log is a tagged log without an affinity in the data, so it is based on the oak log
            GeneratedAffinities.Generated mangrove = run.generated().get(MANGROVE_LOG);
            assertTrue(helper, mangrove != null, "No affinity generated for " + MANGROVE_LOG);
            assertValueEqual(helper, ResourceLocation.withDefaultNamespace("oak_log"), mangrove.base(), "Base of " + MANGROVE_LOG);

            // A data pack entry is unchanged: oak log is earth 10, sun 10 in Primal Magick's data
            assertValueEqual(helper, SourceList.builder().withEarth(10).withSun(10).build(), total(server, ResourceLocation.withDefaultNamespace("oak_log")), "Oak log affinity");
        });
        helper.succeed();
    }

    public static void rounded_down_crafts_of_generated_items_get_one_of_each_source(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        withoutMangroveData(server, run -> {
            // Mangrove planks get their affinity from the generated mangrove log through Primal Magick's recipe scan. A
            // slab (6 from 3 planks) and a button (1 from 1 plank) would round down to nothing; each gets 1 of every
            // plank source.
            SourceList planks = total(server, ResourceLocation.withDefaultNamespace("mangrove_planks"));
            assertFalse(helper, planks.isEmpty(), "Mangrove planks have no affinity");
            SourceList.Builder expected = SourceList.builder();
            planks.getSources().forEach(source -> expected.with(source, 1));
            assertValueEqual(helper, expected.build(), total(server, ResourceLocation.withDefaultNamespace("mangrove_slab")), "Mangrove slab affinity");
            assertValueEqual(helper, expected.build(), total(server, ResourceLocation.withDefaultNamespace("mangrove_button")), "Mangrove button affinity");
        });
        helper.succeed();
    }

    @SuppressWarnings("unchecked")
    public static void lint_still_lists_items_with_only_a_generated_affinity(GameTestHelper helper) {
        // The mangrove log has no recipe, so with no data pack affinity Primal Magick's lint must keep listing it for
        // data pack authors even though it now has a generated affinity; excluding its namespace still drops it
        MinecraftServer server = helper.getLevel().getServer();
        Item mangroveLog = BuiltInRegistries.ITEM.get(MANGROVE_LOG);
        withoutMangroveData(server, run -> {
            try {
                Method lint = PrimalMagickCommand.class.getDeclaredMethod("listSourcelessItems", RecipeManager.class, RegistryAccess.class, ServerLevel.class, Collection.class);
                lint.setAccessible(true);
                ServerLevel level = helper.getLevel();
                List<Item> all = (List<Item>) lint.invoke(null, level.getRecipeManager(), level.registryAccess(), level, null);
                List<Item> notVanilla = (List<Item>) lint.invoke(null, level.getRecipeManager(), level.registryAccess(), level, List.of(ResourceLocation.DEFAULT_NAMESPACE));
                assertTrue(helper, all.contains(mangroveLog), "Lint does not list the mangrove log");
                assertFalse(helper, notVanilla.contains(mangroveLog), "Lint lists the mangrove log although its namespace is excluded");
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Could not run Primal Magick's lint", e);
            }
        });
        helper.succeed();
    }

    /**
     * Regenerates affinities as if Primal Magick's data left out the mangrove log family, runs the checks, and then
     * regenerates from the real data. The whole test runs within one server tick, so no other test sees the change.
     */
    private static void withoutMangroveData(MinecraftServer server, Consumer<GeneratedAffinities.Run> checks) {
        List<IAffinity> original = GeneratedAffinities.dataPackAffinities();
        if (original == null) {
            throw new IllegalStateException("Primal Magick's data pack affinities were not recorded");
        }
        try {
            GeneratedAffinities.onDataPackAffinitiesLoaded(original.stream().filter(affinity -> !LEFT_OUT.contains(affinity.getTarget())).toList());
            GeneratedAffinities.regenerate(server);
            GeneratedAffinities.Run run = GeneratedAffinities.lastRun();
            if (run == null) {
                throw new IllegalStateException("Affinities could not be generated");
            }
            checks.accept(run);
        } finally {
            GeneratedAffinities.onDataPackAffinitiesLoaded(original);
            GeneratedAffinities.regenerate(server);
        }
    }

    private static SourceList total(MinecraftServer server, ResourceLocation id) {
        RecipeManager recipes = server.getRecipeManager();
        RegistryAccess registryAccess = server.registryAccess();
        IAffinity affinity = AffinityManager.getInstance().getOrGenerateItemAffinityAsync(id, recipes, registryAccess, new ArrayList<>()).join();
        return affinity == null ? SourceList.EMPTY : affinity.getTotalAsync(recipes, registryAccess, new ArrayList<>()).join();
    }

    /** The targets of every item affinity file in the loaded data packs. */
    private static Set<ResourceLocation> dataPackItemTargets(MinecraftServer server) {
        Set<ResourceLocation> retVal = new HashSet<>();
        server.getResourceManager().listResources("affinities", id -> id.getPath().endsWith(".json")).values().forEach(resource -> {
            JsonElement json = readJson(resource);
            if (json.isJsonObject()) {
                JsonObject object = json.getAsJsonObject();
                if ("item".equals(object.has("type") ? object.get("type").getAsString() : null) && object.has("target")) {
                    retVal.add(ResourceLocation.parse(object.get("target").getAsString()));
                }
            }
        });
        return retVal;
    }

    private static JsonElement readJson(Resource resource) {
        try (Reader reader = resource.openAsReader()) {
            return JsonParser.parseReader(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
