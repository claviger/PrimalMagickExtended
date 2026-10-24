package com.claviger.primalmagickextended.test.portals;

import com.claviger.primalmagickextended.common.blocks.BlocksPMX;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import com.claviger.primalmagickextended.common.research.ResearchEntriesPMX;
import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.mojang.serialization.JsonOps;
import com.verdantartifice.primalmagick.common.crafting.IArcaneRecipe;
import com.verdantartifice.primalmagick.common.crafting.ShapedArcaneRecipe;
import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.registries.RegistryKeysPM;
import com.verdantartifice.primalmagick.common.research.KnowledgeType;
import com.verdantartifice.primalmagick.common.research.ResearchDiscipline;
import com.verdantartifice.primalmagick.common.research.ResearchDisciplines;
import com.verdantartifice.primalmagick.common.research.ResearchEntries;
import com.verdantartifice.primalmagick.common.research.ResearchEntry;
import com.verdantartifice.primalmagick.common.research.ResearchStage;
import com.verdantartifice.primalmagick.common.research.ResearchTier;
import com.verdantartifice.primalmagick.common.research.ResearchTiers;
import com.verdantartifice.primalmagick.common.research.keys.ResearchEntryKey;
import com.verdantartifice.primalmagick.common.research.requirements.AbstractRequirement;
import com.verdantartifice.primalmagick.common.research.requirements.ItemStackRequirement;
import com.verdantartifice.primalmagick.common.research.requirements.KnowledgeRequirement;
import com.verdantartifice.primalmagick.common.research.requirements.RequirementCategory;
import com.verdantartifice.primalmagick.common.research.requirements.ResearchRequirement;
import com.verdantartifice.primalmagick.common.rewards.AbstractReward;
import com.verdantartifice.primalmagick.common.rewards.AttunementReward;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.common.sources.Sources;
import com.verdantartifice.primalmagick.common.tags.ResearchEntryTagsPM;
import com.verdantartifice.primalmagick.test.AbstractBaseTest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tests that the portal research entries, their arcane and recolouring recipes, the opaque research tag and the rune
 * block loot tables are generated as designed.
 */
public class PortalResearchTests extends AbstractBaseTest {
    private static final Identifier CATALYST_EXPERTISE_GROUP = ResourceUtilsPMX.loc("portal_catalysts");

    /** Expected shape of one research entry, including its stage-1 theory cost and required items. */
    private record ExpectedEntry(ResourceKey<ResearchEntry> key, ResourceKey<ResearchDiscipline> discipline, ResearchTier tier, Set<ResourceKey<ResearchEntry>> parents,
                                 int theories, Map<Item, Integer> requiredItems, List<Item> recipeItems, Set<AttunementReward> attunements) {}

    /** Expected cost and gating of one rune recipe. */
    private record ExpectedRune(Item rune, ResourceKey<ResearchEntry> research, SourceList mana) {}

    private static List<ExpectedEntry> expectedEntries() {
        return List.of(
                new ExpectedEntry(ResearchEntriesPMX.PORTAL_TRAVEL, ResearchDisciplines.MANAWEAVING, ResearchTiers.EXPERT,
                        Set.of(ResearchEntries.EXPERT_MANAWEAVING, ResearchEntries.DISCOVER_INFERNAL),
                        2, Map.of(Items.ENDER_PEARL, 1, Items.ENDER_EYE, 1),
                        ItemsPMX.catalysts(), Set.of(new AttunementReward(Sources.INFERNAL, 2))),
                new ExpectedEntry(ResearchEntriesPMX.WEAK_ENHANCER_RUNE, ResearchDisciplines.RUNEWORKING, ResearchTiers.EXPERT,
                        Set.of(ResearchEntriesPMX.PORTAL_TRAVEL, ResearchEntries.EXPERT_RUNEWORKING, ResearchEntries.DISCOVER_INFERNAL),
                        2, Map.of(Items.AMETHYST_SHARD, 1, Items.LAPIS_LAZULI, 4),
                        List.of(ItemsPMX.WEAK_ENHANCER_RUNE.get()), Set.of(new AttunementReward(Sources.INFERNAL, 2))),
                new ExpectedEntry(ResearchEntriesPMX.STRONG_ENHANCER_RUNE, ResearchDisciplines.RUNEWORKING, ResearchTiers.MASTER,
                        Set.of(ResearchEntries.MASTER_RUNEWORKING, ResearchEntriesPMX.WEAK_ENHANCER_RUNE, ResearchEntries.WAND_GEM_WIZARD),
                        2, Map.of(Items.PRISMARINE_SHARD, 2, Items.PRISMARINE_CRYSTALS, 2),
                        List.of(ItemsPMX.STRONG_ENHANCER_RUNE.get()), Set.of(new AttunementReward(Sources.INFERNAL, 2))),
                new ExpectedEntry(ResearchEntriesPMX.INFINITY_RUNE, ResearchDisciplines.RUNEWORKING, ResearchTiers.MASTER,
                        Set.of(ResearchEntries.SUPREME_RUNEWORKING, ResearchEntriesPMX.STRONG_ENHANCER_RUNE, ResearchEntries.WAND_GEM_WIZARD, ResearchEntries.DISCOVER_VOID),
                        2, Map.of(Items.CHORUS_FRUIT, 6),
                        List.of(ItemsPMX.INFINITY_RUNE.get()), Set.of(new AttunementReward(Sources.INFERNAL, 2), new AttunementReward(Sources.VOID, 2))),
                new ExpectedEntry(ResearchEntriesPMX.GATE_RUNE, ResearchDisciplines.RUNEWORKING, ResearchTiers.SUPREME,
                        Set.of(ResearchEntries.MASTER_RUNEWORKING, ResearchEntriesPMX.STRONG_ENHANCER_RUNE, ResearchEntries.WAND_GEM_WIZARD, ResearchEntries.DISCOVER_VOID, ResearchEntries.DISCOVER_INFERNAL),
                        2, Map.of(Items.WEEPING_VINES, 2, Items.POPPED_CHORUS_FRUIT, 2),
                        List.of(ItemsPMX.GATE_RUNE.get()), Set.of(new AttunementReward(Sources.VOID, 2))),
                new ExpectedEntry(ResearchEntriesPMX.HASTE_RUNE, ResearchDisciplines.RUNEWORKING, ResearchTiers.SUPREME,
                        Set.of(ResearchEntries.SUPREME_RUNEWORKING, ResearchEntriesPMX.INFINITY_RUNE, ResearchEntries.WAND_GEM_WIZARD, ResearchEntries.DISCOVER_VOID),
                        2, Map.of(Items.FEATHER, 1, Items.RABBIT_FOOT, 1),
                        List.of(ItemsPMX.HASTE_RUNE.get()), Set.of(new AttunementReward(Sources.VOID, 2))));
    }

    private static ResourceKey<Recipe<?>> recipeKey(String name) {
        return ResourceKey.create(Registries.RECIPE, ResourceUtilsPMX.loc(name));
    }

    private static ResourceKey<Recipe<?>> recipeKey(Item item) {
        return ResourceKey.create(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(item));
    }

    private static RecipeHolder<?> recipe(GameTestHelper helper, ResourceKey<Recipe<?>> key) {
        return helper.getLevel().getServer().getRecipeManager().byKey(key).orElseThrow(() -> new IllegalStateException("Recipe " + key.identifier() + " missing"));
    }

    public static void portal_research_entries_have_expected_structure(GameTestHelper helper) {
        var registry = helper.getLevel().registryAccess().lookupOrThrow(RegistryKeysPM.RESEARCH_ENTRIES);
        for (ExpectedEntry expected : expectedEntries()) {
            String name = expected.key().identifier().getPath();
            ResearchEntry entry = registry.getOrThrow(expected.key()).value();
            assertValueEqual(helper, expected.discipline(), entry.disciplineKeyOpt().orElseThrow().getRootKey(), name + " discipline");
            assertValueEqual(helper, Optional.of(expected.tier()), entry.tierOpt(), name + " tier");
            Set<ResourceKey<ResearchEntry>> parents = entry.parents().stream().map(ResearchEntryKey::getRootKey).collect(Collectors.toSet());
            assertValueEqual(helper, expected.parents(), parents, name + " parents");
            assertTrue(helper, entry.flags().finaleExempt(), name + " is not finale exempt");
            assertValueEqual(helper, 2, entry.stages().size(), name + " stage count");

            ResearchStage first = entry.stages().get(0);
            assertValueEqual(helper, "research.primalmagickextended." + name + ".text.stage.1", first.textTranslationKey(), name + " stage 1 text key");
            List<AbstractRequirement<?>> knowledge = first.getRequirementsByCategory(RequirementCategory.KNOWLEDGE);
            assertValueEqual(helper, 1, knowledge.size(), name + " stage 1 knowledge requirement count");
            KnowledgeRequirement theories = assertInstanceOf(helper, knowledge.getFirst(), KnowledgeRequirement.class, name + " stage 1 knowledge requirement type");
            assertValueEqual(helper, KnowledgeType.THEORY, theories.getKnowledgeType(), name + " stage 1 knowledge type");
            assertValueEqual(helper, expected.theories(), theories.getAmount(), name + " stage 1 theory count");
            Map<Item, Integer> requiredItems = new HashMap<>();
            for (AbstractRequirement<?> requirement : first.getRequirementsByCategory(RequirementCategory.MUST_OBTAIN)) {
                ItemStack stack = assertInstanceOf(helper, requirement, ItemStackRequirement.class, name + " stage 1 item requirement type").getStack();
                requiredItems.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
            assertValueEqual(helper, expected.requiredItems(), requiredItems, name + " stage 1 required items");
            for (RequirementCategory other : List.of(RequirementCategory.MUST_CRAFT, RequirementCategory.RESEARCH, RequirementCategory.STAT)) {
                assertTrue(helper, first.getRequirementsByCategory(other).isEmpty(), name + " stage 1 has unexpected " + other.getSerializedName() + " requirements");
            }

            ResearchStage second = entry.stages().get(1);
            assertValueEqual(helper, "research.primalmagickextended." + name + ".text.stage.2", second.textTranslationKey(), name + " stage 2 text key");
            assertValueEqual(helper, expected.recipeItems().stream().map(PortalResearchTests::recipeKey).toList(), second.recipes(), name + " stage 2 recipes");
            Set<AbstractReward<?>> rewards = new HashSet<>(second.rewards());
            assertValueEqual(helper, Set.copyOf(expected.attunements()), rewards, name + " stage 2 attunements");
        }
        helper.succeed();
    }

    public static void portal_travel_is_opaque(GameTestHelper helper) {
        boolean opaque = helper.getLevel().registryAccess().lookupOrThrow(RegistryKeysPM.RESEARCH_ENTRIES)
                .getOrThrow(ResearchEntriesPMX.PORTAL_TRAVEL).is(ResearchEntryTagsPM.OPAQUE);
        assertTrue(helper, opaque, "Portal travel is not tagged opaque");
        // The tag must merge with Primal Magick's own opaque entries rather than replace them
        boolean discoverInfernalOpaque = helper.getLevel().registryAccess().lookupOrThrow(RegistryKeysPM.RESEARCH_ENTRIES)
                .getOrThrow(ResearchEntries.DISCOVER_INFERNAL).is(ResearchEntryTagsPM.OPAQUE);
        assertTrue(helper, discoverInfernalOpaque, "Primal Magick's discover_infernal lost its opaque tag");
        helper.succeed();
    }

    public static void catalyst_recipes_are_arcane_and_gated(GameTestHelper helper) {
        for (PortalColor color : PortalColor.values()) {
            String name = color.getSerializedName() + "_portal_catalyst";
            ShapedArcaneRecipe recipe = assertInstanceOf(helper, recipe(helper, recipeKey(name)).value(), ShapedArcaneRecipe.class, name + " is not a shaped arcane recipe");
            ItemStack result = recipe.assemble(CraftingInput.EMPTY);
            assertValueEqual(helper, ItemsPMX.catalyst(color), result.getItem(), name + " result");
            assertValueEqual(helper, 2, result.getCount(), name + " result count");
            ResearchRequirement requirement = assertInstanceOf(helper, recipe.getRequirement().orElseThrow(() -> new IllegalStateException(name + " has no requirement")),
                    ResearchRequirement.class, name + " requirement is not a research requirement");
            assertValueEqual(helper, new ResearchEntryKey(ResearchEntriesPMX.PORTAL_TRAVEL), requirement.getRootKey(), name + " required research");
            assertValueEqual(helper, SourceList.builder().withInfernal(10000).build(), recipe.getManaCosts(), name + " mana costs");
            assertValueEqual(helper, Optional.of(CATALYST_EXPERTISE_GROUP), recipe.getExpertiseGroup(), name + " expertise group");

            List<Ingredient> ingredients = recipe.placementInfo().ingredients();
            assertValueEqual(helper, 9, ingredients.size(), name + " ingredient count");
            assertTrue(helper, ingredients.get(0).test(new ItemStack(ItemsPM.ESSENCE_SHARD_INFERNAL.get())), name + " corner is not an infernal shard");
            assertTrue(helper, ingredients.get(1).test(new ItemStack(Items.ENDER_EYE)), name + " top centre is not an ender eye");
            assertTrue(helper, ingredients.get(3).test(new ItemStack(Items.ENDER_PEARL)), name + " middle left is not an ender pearl");
            assertTrue(helper, ingredients.get(4).test(new ItemStack(color.dyeItem())), name + " centre is not the matching dye");
        }
        helper.succeed();
    }

    public static void rune_recipes_are_arcane_and_gated(GameTestHelper helper) {
        List<ExpectedRune> runes = List.of(
                new ExpectedRune(ItemsPMX.WEAK_ENHANCER_RUNE.get(), ResearchEntriesPMX.WEAK_ENHANCER_RUNE, SourceList.builder().withInfernal(25000).build()),
                new ExpectedRune(ItemsPMX.STRONG_ENHANCER_RUNE.get(), ResearchEntriesPMX.STRONG_ENHANCER_RUNE, SourceList.builder().withInfernal(75000).build()),
                new ExpectedRune(ItemsPMX.INFINITY_RUNE.get(), ResearchEntriesPMX.INFINITY_RUNE, SourceList.builder().withVoid(75000).withInfernal(75000).build()),
                new ExpectedRune(ItemsPMX.GATE_RUNE.get(), ResearchEntriesPMX.GATE_RUNE, SourceList.builder().withVoid(75000).build()),
                new ExpectedRune(ItemsPMX.HASTE_RUNE.get(), ResearchEntriesPMX.HASTE_RUNE, SourceList.builder().withVoid(75000).withInfernal(75000).build()));

        for (ExpectedRune expected : runes) {
            Item rune = expected.rune();
            String name = BuiltInRegistries.ITEM.getKey(rune).getPath();
            ShapedArcaneRecipe recipe = assertInstanceOf(helper, recipe(helper, recipeKey(rune)).value(), ShapedArcaneRecipe.class, name + " is not a shaped arcane recipe");
            ItemStack result = recipe.assemble(CraftingInput.EMPTY);
            assertValueEqual(helper, rune, result.getItem(), name + " result");
            assertValueEqual(helper, 1, result.getCount(), name + " result count");
            ResearchRequirement requirement = assertInstanceOf(helper, recipe.getRequirement().orElseThrow(() -> new IllegalStateException(name + " has no requirement")),
                    ResearchRequirement.class, name + " requirement is not a research requirement");
            assertValueEqual(helper, new ResearchEntryKey(expected.research()), requirement.getRootKey(), name + " required research");
            assertValueEqual(helper, expected.mana(), recipe.getManaCosts(), name + " mana costs");
            assertValueEqual(helper, Optional.empty(), recipe.getExpertiseGroup(), name + " expertise group");
        }
        helper.succeed();
    }

    public static void recolour_recipes_exist_for_every_colour(GameTestHelper helper) {
        for (PortalColor color : PortalColor.values()) {
            String name = color.getSerializedName() + "_portal_catalyst_2";
            ShapelessRecipe recipe = assertInstanceOf(helper, recipe(helper, recipeKey(name)).value(), ShapelessRecipe.class, name + " is not a shapeless crafting recipe");
            assertValueEqual(helper, "portal_catalyst", recipe.group(), name + " group");
            ItemStack dye = new ItemStack(color.dyeItem());
            ItemStack same = new ItemStack(ItemsPMX.catalyst(color));

            List<Ingredient> ingredients = recipe.placementInfo().ingredients();
            assertValueEqual(helper, 2, ingredients.size(), name + " ingredient count");
            Ingredient catalystIngredient = ingredients.get(0);
            assertTrue(helper, ingredients.get(1).test(dye), name + " does not take the matching dye");
            assertFalse(helper, catalystIngredient.test(same), name + " accepts a catalyst of the same colour");
            for (PortalColor other : PortalColor.values()) {
                if (other == color) {
                    continue;
                }
                ItemStack otherCatalyst = new ItemStack(ItemsPMX.catalyst(other));
                assertTrue(helper, catalystIngredient.test(otherCatalyst), name + " rejects a " + other.getSerializedName() + " catalyst");
                ItemStack result = recipe.assemble(CraftingInput.of(2, 1, List.of(otherCatalyst, dye)));
                assertValueEqual(helper, ItemsPMX.catalyst(color), result.getItem(), name + " result from " + other.getSerializedName());
                assertValueEqual(helper, 1, result.getCount(), name + " result count");
            }
            assertFalse(helper, recipe.matches(CraftingInput.of(2, 1, List.of(same, dye)), helper.getLevel()), name + " matches a catalyst of the same colour");
        }
        helper.succeed();
    }

    public static void no_vanilla_recipe_crafts_portal_items_except_recolouring(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        var context = SlotDisplayContext.fromLevel(helper.getLevel());
        Set<Item> catalysts = Set.copyOf(ItemsPMX.catalysts());
        Set<Item> portalItems = new HashSet<>(catalysts);
        portalItems.addAll(ItemsPMX.runes());
        int arcaneCount = 0;
        int recolourCount = 0;
        for (RecipeHolder<?> holder : server.getRecipeManager().getRecipes()) {
            Set<Item> results = new HashSet<>();
            for (RecipeDisplay display : holder.value().display()) {
                display.result().resolveForStacks(context).forEach(stack -> results.add(stack.getItem()));
            }
            results.retainAll(portalItems);
            if (results.isEmpty()) {
                continue;
            }
            Identifier id = holder.id().identifier();
            assertValueEqual(helper, ResourceUtilsPMX.loc(id.getPath()), id, "Recipe " + id + " crafting a portal item is outside this mod's namespace");
            if (holder.value() instanceof IArcaneRecipe) {
                arcaneCount++;
            } else {
                assertTrue(helper, id.getPath().endsWith("_2"), "Non-arcane recipe " + id + " crafts a portal item");
                assertTrue(helper, catalysts.containsAll(results), "Non-arcane recipe " + id + " crafts a rune");
                recolourCount++;
            }
        }
        assertValueEqual(helper, catalysts.size() + ItemsPMX.runes().size(), arcaneCount, "Arcane portal recipe count");
        assertValueEqual(helper, catalysts.size(), recolourCount, "Recolouring recipe count");
        helper.succeed();
    }

    public static void rune_block_loot_drops_rune(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        Map<Block, Item> drops = Map.of(
                BlocksPMX.HASTE_RUNE_BLOCK.get(), ItemsPMX.HASTE_RUNE.get(),
                BlocksPMX.GATE_RUNE_BLOCK.get(), ItemsPMX.GATE_RUNE.get(),
                BlocksPMX.WEAK_ENHANCER_RUNE_BLOCK.get(), ItemsPMX.WEAK_ENHANCER_RUNE.get(),
                BlocksPMX.STRONG_ENHANCER_RUNE_BLOCK.get(), ItemsPMX.STRONG_ENHANCER_RUNE.get(),
                BlocksPMX.INFINITY_RUNE_BLOCK.get(), ItemsPMX.INFINITY_RUNE.get());
        for (Map.Entry<Block, Item> entry : drops.entrySet()) {
            Block block = entry.getKey();
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
            ResourceKey<LootTable> tableKey = block.getLootTable().orElseThrow(() -> new IllegalStateException(name + " has no loot table"));
            LootTable table = server.reloadableRegistries().getLootTable(tableKey);
            assertTrue(helper, table != LootTable.EMPTY, name + " loot table is empty or missing");

            LootParams params = new LootParams.Builder(helper.getLevel())
                    .withParameter(LootContextParams.ORIGIN, helper.absoluteVec(Vec3.ZERO))
                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                    .withParameter(LootContextParams.BLOCK_STATE, block.defaultBlockState())
                    .create(LootContextParamSets.BLOCK);
            List<ItemStack> rolled = table.getRandomItems(params);
            assertValueEqual(helper, 1, rolled.size(), name + " drop stack count");
            assertValueEqual(helper, entry.getValue(), rolled.getFirst().getItem(), name + " drop item");
            assertValueEqual(helper, 1, rolled.getFirst().getCount(), name + " drop count");

            String json = LootTable.DIRECT_CODEC.encodeStart(helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE), table)
                    .getOrThrow().toString();
            assertTrue(helper, json.contains("minecraft:survives_explosion"), name + " loot table lacks a survives_explosion condition");
        }
        helper.succeed();
    }
}
