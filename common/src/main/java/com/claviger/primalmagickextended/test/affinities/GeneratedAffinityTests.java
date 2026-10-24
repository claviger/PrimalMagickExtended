package com.claviger.primalmagickextended.test.affinities;

import com.claviger.primalmagickextended.common.affinities.GeneratedAffinities;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.verdantartifice.primalmagick.common.affinities.AffinityManager;
import com.verdantartifice.primalmagick.common.affinities.IAffinity;
import com.verdantartifice.primalmagick.common.commands.PrimalMagickCommand;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.test.AbstractBaseTest;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
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

/**
 * Tests for generated affinities, which run when the test server starts.
 */
public class GeneratedAffinityTests extends AbstractBaseTest {
    public static void generated_affinities_are_registered_and_yield_to_data(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        GeneratedAffinities.Run run = GeneratedAffinities.lastRun();
        assertTrue(helper, run != null, "Affinities were not generated when the server started");

        // A data pack entry always wins: nothing it defines is generated
        Set<Identifier> dataTargets = dataPackItemTargets(server);
        for (Identifier id : run.generated().keySet()) {
            assertFalse(helper, dataTargets.contains(id), "Generated an affinity for " + id + ", which a data pack defines");
        }

        // Every generated affinity is what Primal Magick now reports
        run.generated().forEach((id, generated) ->
                assertValueEqual(helper, generated.total(), total(server, id), "Primal Magick's affinity of generated " + id));

        // The pale oak log is a tagged log without an affinity in Primal Magick's data, so it is based on the oak log
        Identifier paleOakLog = Identifier.withDefaultNamespace("pale_oak_log");
        GeneratedAffinities.Generated paleOak = run.generated().get(paleOakLog);
        assertTrue(helper, paleOak != null, "No affinity generated for " + paleOakLog);
        assertValueEqual(helper, Identifier.withDefaultNamespace("oak_log"), paleOak.base(), "Base of " + paleOakLog);

        // A data pack entry is unchanged: oak log is earth 10, sun 10 in Primal Magick's data
        assertValueEqual(helper, SourceList.builder().withEarth(10).withSun(10).build(), total(server, Identifier.withDefaultNamespace("oak_log")), "Oak log affinity");
        helper.succeed();
    }

    public static void rounded_down_crafts_of_generated_items_get_one_of_each_source(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        // Pale oak planks get their affinity from the generated pale oak log through Primal Magick's recipe scan. A slab
        // (6 from 3 planks) and a button (1 from 1 plank) would round down to nothing; each gets 1 of every plank source.
        SourceList planks = total(server, Identifier.withDefaultNamespace("pale_oak_planks"));
        assertFalse(helper, planks.isEmpty(), "Pale oak planks have no affinity");
        SourceList.Builder expected = SourceList.builder();
        planks.getSources().forEach(source -> expected.with(source, 1));
        assertValueEqual(helper, expected.build(), total(server, Identifier.withDefaultNamespace("pale_oak_slab")), "Pale oak slab affinity");
        assertValueEqual(helper, expected.build(), total(server, Identifier.withDefaultNamespace("pale_oak_button")), "Pale oak button affinity");
        helper.succeed();
    }

    @SuppressWarnings("unchecked")
    public static void lint_still_lists_items_with_only_a_generated_affinity(GameTestHelper helper) {
        // The pale oak log has no recipe and no data pack affinity, so Primal Magick's lint must keep listing it for
        // data pack authors even though it now has a generated affinity; excluding its namespace still drops it
        Item paleOakLog = BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace("pale_oak_log"));
        try {
            Method lint = PrimalMagickCommand.class.getDeclaredMethod("listSourcelessItems", RecipeManager.class, RegistryAccess.class, ServerLevel.class, Collection.class);
            lint.setAccessible(true);
            ServerLevel level = helper.getLevel();
            List<Item> all = (List<Item>) lint.invoke(null, level.recipeAccess(), level.registryAccess(), level, null);
            List<Item> notVanilla = (List<Item>) lint.invoke(null, level.recipeAccess(), level.registryAccess(), level, List.of(Identifier.DEFAULT_NAMESPACE));
            assertTrue(helper, all.contains(paleOakLog), "Lint does not list the pale oak log");
            assertFalse(helper, notVanilla.contains(paleOakLog), "Lint lists the pale oak log although its namespace is excluded");
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not run Primal Magick's lint", e);
        }
        helper.succeed();
    }

    private static SourceList total(MinecraftServer server, Identifier id) {
        Collection<RecipeHolder<?>> recipes = server.getRecipeManager().getRecipes();
        RegistryAccess registryAccess = server.registryAccess();
        IAffinity affinity = AffinityManager.getInstance().getOrGenerateItemAffinityAsync(id, recipes, registryAccess, new ArrayList<>()).join();
        return affinity == null ? SourceList.EMPTY : affinity.getTotalAsync(recipes, registryAccess, new ArrayList<>()).join();
    }

    /** The targets of every item affinity file in the loaded data packs. */
    private static Set<Identifier> dataPackItemTargets(MinecraftServer server) {
        Set<Identifier> retVal = new HashSet<>();
        server.getResourceManager().listResources("affinities", id -> id.getPath().endsWith(".json")).values().forEach(resource -> {
            JsonElement json = readJson(resource);
            if (json.isJsonObject()) {
                JsonObject object = json.getAsJsonObject();
                if ("primalmagick:item".equals(object.has("type") ? object.get("type").getAsString() : null) && object.has("target")) {
                    retVal.add(Identifier.parse(object.get("target").getAsString()));
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
