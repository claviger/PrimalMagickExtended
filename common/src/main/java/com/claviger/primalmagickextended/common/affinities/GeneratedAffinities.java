package com.claviger.primalmagickextended.common.affinities;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.affinities.AffinityRules.Classification;
import com.claviger.primalmagickextended.common.affinities.AffinityRules.Confidence;
import com.claviger.primalmagickextended.common.affinities.AffinityRules.Modifier;
import com.claviger.primalmagickextended.common.affinities.WorldgenSurvey.BlockEvidence;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.verdantartifice.primalmagick.common.affinities.AffinityManager;
import com.verdantartifice.primalmagick.common.affinities.AffinityType;
import com.verdantartifice.primalmagick.common.affinities.IAffinity;
import com.verdantartifice.primalmagick.common.affinities.ItemAffinity;
import com.verdantartifice.primalmagick.common.crafting.IHasManaCost;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Generated Affinities: gives natural blocks that have no Primal Magick affinity one worked out from what they are
 * and where world generation puts them. Runs whenever the server loads its data; the results are registered in
 * Primal Magick's affinity manager alongside the data pack affinities (which always win), so they reach clients in
 * Primal Magick's own affinity sync, and crafted blocks made from them get affinities through Primal Magick's recipe
 * scan. Every run writes a report and the generated entries to {@code <game dir>/primalmagickextended/}.
 */
public final class GeneratedAffinities {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String REPORT_FILE = "generated_affinities.md";
    public static final String DATA_FILE = "generated_affinities.json";

    /** Name tokens that say nothing about what a block is, ignored when matching blocks to features by name. */
    private static final Set<String> NAME_NOISE = Set.of("huge", "giant", "big", "large", "medium", "small", "tiny", "patch", "patches",
            "cave", "caves", "block", "plant", "strand", "petal", "extra", "sparse", "dense", "splatter", "common", "rare", "tall",
            "short", "leaves", "leaf", "stem", "cluster", "bud", "nether", "end", "white", "orange", "magenta", "light", "blue", "yellow",
            "lime", "pink", "gray", "grey", "cyan", "purple", "brown", "green", "red", "black");

    /** One generated affinity, with how it was worked out. */
    public record Generated(ResourceLocation item, IAffinity affinity, JsonObject json, NaturalRole role, @Nullable ResourceLocation base, SourceList total,
                            String roleReason, List<String> modifiers, String evidence, boolean secondPass) {}

    /** A candidate that got no affinity, and why. */
    public record Skipped(ResourceLocation item, String reason, String evidence) {}

    /** The result of the latest run, or null before the first. */
    @Nullable
    private static volatile Run lastRun;

    /**
     * The affinities Primal Magick loaded from data packs, as of its latest reload. Primal Magick 6.0.7 keeps the
     * affinities its recipe scan works out in the same table, so the table alone cannot tell them apart.
     */
    @Nullable
    private static volatile List<IAffinity> dataPackAffinities;

    private GeneratedAffinities() {}

    @Nullable
    public static Run lastRun() {
        return lastRun;
    }

    /** Records the affinities Primal Magick has just loaded from data packs, before any are worked out from recipes. */
    public static void onDataPackAffinitiesLoaded(Collection<IAffinity> affinities) {
        dataPackAffinities = List.copyOf(affinities);
    }

    /** The affinities Primal Magick loaded from data packs, or null before its first reload. */
    @Nullable
    public static List<IAffinity> dataPackAffinities() {
        return dataPackAffinities;
    }

    /**
     * Generates affinities for the server's current data and registers them. Must run on the server thread after
     * data packs are loaded and before clients are sent affinity data.
     */
    public static void regenerate(MinecraftServer server) {
        long start = System.nanoTime();
        lastRun = null;
        try {
            Run run = new Run(server);
            run.execute();
            lastRun = run;
            long millis = (System.nanoTime() - start) / 1_000_000L;
            LOGGER.info("Generated {} affinities for natural blocks in {} ms ({} candidates skipped)", run.generated.size(), millis, run.skipped.size());
            Path dir = server.getServerDirectory().resolve(ConstantsPMX.MOD_ID);
            GeneratedAffinityReport.write(run, dir, millis);
            LOGGER.info("Wrote the generated affinity report to {}", dir.resolve(REPORT_FILE).toAbsolutePath().normalize());
        } catch (RuntimeException e) {
            LOGGER.error("Failed to generate affinities; Primal Magick's data pack affinities are unchanged", e);
        }
    }

    /** One generation run over the server's data. */
    public static final class Run {
        private final MinecraftServer server;
        private final RegistryAccess registryAccess;
        private final AffinityManager manager;
        private final RecipeManager recipeManager;
        private final Collection<RecipeHolder<?>> recipes;
        private final List<IAffinity> baseline;
        private final Set<ResourceLocation> registered;
        private final Set<Item> crafted = new HashSet<>();
        /** The recipes that make each crafted item, the items each of their ingredients accepts, and how many they make. */
        private final Map<Item, List<RecipeHolder<?>>> recipesByResult = new HashMap<>();
        private final Map<RecipeHolder<?>, List<Set<Item>>> ingredientItems = new HashMap<>();
        private final Map<RecipeHolder<?>, Integer> resultCounts = new HashMap<>();
        private WorldgenSurvey survey;
        private Collection<WorldgenSurvey.Source> opaqueSources;
        final Map<ResourceLocation, Generated> generated = new LinkedHashMap<>();
        final List<Skipped> skipped = new ArrayList<>();
        /** Items without an affinity that are not natural blocks: other items, potted plants, fluids. */
        int notConsidered;

        Run(MinecraftServer server) {
            this.server = server;
            this.registryAccess = server.registryAccess();
            this.manager = AffinityManager.getInstance();
            this.recipeManager = server.getRecipeManager();
            this.recipes = this.recipeManager.getRecipes();
            List<IAffinity> loaded = dataPackAffinities;
            this.baseline = loaded != null ? loaded : List.copyOf(this.manager.getAllAffinities());
            this.registered = this.baseline.stream().filter(affinity -> affinity.getType() == AffinityType.ITEM).map(IAffinity::getTarget).collect(Collectors.toSet());
        }

        public Map<ResourceLocation, Generated> generated() {
            return this.generated;
        }

        public List<Skipped> skipped() {
            return this.skipped;
        }

        public int notConsidered() {
            return this.notConsidered;
        }

        /**
         * Whether the item got a generated affinity and no recipe makes it: an item that Primal Magick's
         * {@code /pm affinities lint} would list as having no affinity if this feature were off.
         */
        public boolean isGeneratedWithoutRecipe(Item item) {
            return this.generated.containsKey(BuiltInRegistries.ITEM.getKey(item)) && !this.crafted.contains(item);
        }

        void execute() {
            for (RecipeHolder<?> holder : this.recipes) {
                try {
                    // The result and ingredients as Primal Magick's recipe scan reads them
                    ItemStack result = holder.value().getResultItem(this.registryAccess);
                    if (result != null && !result.isEmpty()) {
                        this.crafted.add(result.getItem());
                        this.recipesByResult.computeIfAbsent(result.getItem(), $ -> new ArrayList<>()).add(holder);
                        this.resultCounts.put(holder, result.getCount());
                        this.ingredientItems.put(holder, holder.value().getIngredients().stream()
                                .map(Run::itemsOf).toList());
                    }
                } catch (RuntimeException e) {
                    LOGGER.debug("Could not resolve the result of recipe {}", holder.id(), e);
                }
            }
            this.survey = WorldgenSurvey.run(this.server);
            this.opaqueSources = this.survey.opaqueSources();

            // Pass 1: items with no affinity and no recipe. Linked items (stripped logs, drops, buckets) go last, as
            // they build on the others' results.
            List<Item> linked = new ArrayList<>();
            for (Item item : sortedItems()) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
                if (item == Items.AIR || this.registered.contains(id) || this.crafted.contains(item)) {
                    continue;
                }
                if (!this.consider(item, false)) {
                    linked.add(item);
                }
            }
            linked.forEach(this::considerLinked);
            this.register();

            // Pass 2: worldgen blocks that have recipes, but whose recipes give them nothing
            boolean checked = false;
            List<Block> worldgenBlocks = this.survey.allEvidence().keySet().stream()
                    .sorted(Comparator.comparing(block -> BuiltInRegistries.BLOCK.getKey(block).toString())).toList();
            for (Block block : worldgenBlocks) {
                Item item = block.asItem();
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
                if (item == Items.AIR || this.registered.contains(id) || !this.crafted.contains(item) || this.generated.containsKey(id)) {
                    continue;
                }
                checked = true;
                if (this.currentTotal(id).isEmpty()) {
                    this.consider(item, true);
                }
            }
            if (checked) {
                // Also drops the recipe results computed while checking, which may predate this pass's entries
                this.register();
            }

            this.fillRoundedToNothing();
        }

        /**
         * Pass 3: crafted items made from generated ones (directly, or through other such crafted items), whose recipe
         * share of their ingredients rounds down to nothing in Primal Magick's recipe scan (slabs, buttons, and the like
         * when their material is weak). Each gets one of each source of its ingredients, as Primal Magick's own data
         * gives the oak slab. Repeats until nothing changes, since filling one item can leave the next one in a chain
         * still at nothing.
         */
        private void fillRoundedToNothing() {
            // An item counts as made from generated ones if a recipe for it has an ingredient that only generated (or
            // such made) items match. An ingredient that also takes other items, like #planks, leaves the result's value
            // to those other items, which Primal Magick already covers.
            Set<Item> derived = new HashSet<>();
            this.generated.keySet().forEach(id -> derived.add(BuiltInRegistries.ITEM.get(id)));
            Set<Item> made = new HashSet<>();
            boolean changed = true;
            while (changed) {
                changed = false;
                for (Map.Entry<Item, List<RecipeHolder<?>>> entry : this.recipesByResult.entrySet()) {
                    Item result = entry.getKey();
                    if (derived.contains(result) || this.registered.contains(BuiltInRegistries.ITEM.getKey(result))) {
                        continue;
                    }
                    boolean fromDerived = entry.getValue().stream().anyMatch(holder -> this.ingredientItems(holder).stream()
                            .anyMatch(options -> !options.isEmpty() && derived.containsAll(options)));
                    if (fromDerived) {
                        derived.add(result);
                        made.add(result);
                        changed = true;
                    }
                }
            }
            List<Item> pending = made.stream().filter(item -> item != Items.AIR)
                    .sorted(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString())).toList();
            for (int round = 0; round < 16 && !pending.isEmpty(); round++) {
                // Values only grow as items are filled, so an item with an affinity keeps it and is not checked again.
                // Asking Primal Magick costs a scan of every recipe per item, so only items that the estimate puts at
                // nothing are confirmed with it.
                Map<Item, SourceList> estimates = new HashMap<>();
                Set<Item> empty = new HashSet<>();
                for (Item item : pending) {
                    if (this.estimate(item, made, estimates, new HashSet<>()).isEmpty() && this.currentTotal(BuiltInRegistries.ITEM.getKey(item)).isEmpty()) {
                        empty.add(item);
                    }
                }
                // An item with a recipe that needs one of the empty items waits for that item to be filled and for
                // Primal Magick's recipe scan to pass it on
                List<Generated> filled = new ArrayList<>();
                List<Item> waiting = new ArrayList<>();
                for (Item item : pending) {
                    if (!empty.contains(item)) {
                        continue;
                    }
                    ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
                    CheapestRecipe recipe = this.cheapestRecipe(item, empty, made, estimates);
                    if (recipe == null) {
                        waiting.add(item);
                        continue;
                    }
                    SourceList.Builder builder = SourceList.builder();
                    recipe.ingredients().getSources().forEach(source -> builder.with(source, 1));
                    SourceList set = builder.build();
                    JsonObject json = fixed(id, set);
                    filled.add(new Generated(id, decode(id, json), json, NaturalRole.CRAFTED_MINIMUM, null, set,
                            "recipe " + recipe.id() + " gives it less than 1 of anything",
                            List.of("1 of each source of its ingredients (" + GeneratedAffinityReport.sources(recipe.ingredients()) + ")"),
                            "crafted from " + joinIds(recipe.inputs(), 4), false));
                }
                if (filled.isEmpty()) {
                    return;
                }
                filled.forEach(this::store);
                this.register();
                pending = waiting;
            }
        }

        /** A recipe for an item, the combined affinity of its ingredients, and those ingredients. */
        private record CheapestRecipe(String id, SourceList ingredients, List<ResourceLocation> inputs) {}

        /**
         * Of the recipes that make the item, the one whose ingredients have the smallest affinity that is not nothing,
         * as Primal Magick prefers. Each ingredient counts as its cheapest match that has an affinity. Recipes with an
         * ingredient that only the given waiting items match are left out.
         */
        @Nullable
        private CheapestRecipe cheapestRecipe(Item item, Set<Item> waiting, Set<Item> made, Map<Item, SourceList> estimates) {
            CheapestRecipe retVal = null;
            for (RecipeHolder<?> holder : this.recipesByResult.getOrDefault(item, List.of())) {
                if (this.ingredientItems(holder).stream().anyMatch(options -> !options.isEmpty() && waiting.containsAll(options))) {
                    continue;
                }
                SourceList total = SourceList.EMPTY;
                List<ResourceLocation> inputs = new ArrayList<>();
                for (Set<Item> options : this.ingredientItems(holder)) {
                    Item cheapest = this.cheapestOption(options, made, estimates, new HashSet<>());
                    if (cheapest != null) {
                        total = total.add(this.estimate(cheapest, made, estimates, new HashSet<>()));
                        inputs.add(BuiltInRegistries.ITEM.getKey(cheapest));
                    }
                }
                if (!total.isEmpty() && (retVal == null || total.getManaSize() < retVal.ingredients().getManaSize())) {
                    retVal = new CheapestRecipe(holder.id().toString(), total, inputs);
                }
            }
            return retVal;
        }

        /**
         * The affinity Primal Magick's recipe scan gives an item, worked out the same way: for each recipe, each
         * ingredient counts as its cheapest match that has an affinity, less any crafting remainders, times 0.75 per
         * item made (0.75 to 1 rounds up to 1, the rest down), plus a share of any mana cost; the recipe with the
         * smallest result that is not nothing wins. Only made items are worked out; others are asked of Primal Magick.
         */
        private SourceList estimate(Item item, Set<Item> made, Map<Item, SourceList> estimates, Set<Item> visiting) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            Generated generated = this.generated.get(id);
            if (generated != null) {
                return generated.total();
            } else if (!made.contains(item)) {
                return this.currentTotal(id);
            }
            SourceList known = estimates.get(item);
            if (known != null) {
                return known;
            } else if (!visiting.add(item)) {
                return SourceList.EMPTY;
            }
            SourceList best = SourceList.EMPTY;
            for (RecipeHolder<?> holder : this.recipesByResult.getOrDefault(item, List.of())) {
                SourceList sum = SourceList.EMPTY;
                List<Item> chosen = new ArrayList<>();
                for (Set<Item> options : this.ingredientItems(holder)) {
                    Item cheapest = this.cheapestOption(options, made, estimates, visiting);
                    if (cheapest != null) {
                        sum = sum.add(this.estimate(cheapest, made, estimates, visiting));
                        chosen.add(cheapest);
                    }
                }
                if (holder.value() instanceof CraftingRecipe) {
                    for (Item input : chosen) {
                        Item remainder = input.hasCraftingRemainingItem() ? input.getCraftingRemainingItem() : null;
                        if (remainder != null) {
                            sum = sum.remove(this.estimate(remainder, made, estimates, visiting));
                        }
                    }
                }
                int count = this.resultCounts.getOrDefault(holder, 1);
                SourceList.Builder values = SourceList.builder();
                for (Source source : sum.getSources()) {
                    double amount = sum.getAmount(source) * 0.75D / count;
                    int rounded = amount < 1.0D && amount > 0.75D ? 1 : (int) amount;
                    if (rounded > 0) {
                        values.with(source, rounded);
                    }
                }
                SourceList result = values.build();
                if (holder.value() instanceof IHasManaCost manaRecipe) {
                    SourceList costs = manaRecipe.getManaCosts();
                    for (Source source : costs.getSources()) {
                        int amount = (int) (Math.sqrt(1 + costs.getAmount(source) / 200D) / count);
                        if (costs.getAmount(source) > 0 && amount > 0) {
                            result = result.add(source, amount);
                        }
                    }
                }
                if (!result.isEmpty() && (best.isEmpty() || result.getManaSize() < best.getManaSize())) {
                    best = result;
                }
            }
            visiting.remove(item);
            estimates.put(item, best);
            return best;
        }

        /** The ingredient match with the smallest affinity that is not nothing, or null if none has one. */
        @Nullable
        private Item cheapestOption(Set<Item> options, Set<Item> made, Map<Item, SourceList> estimates, Set<Item> visiting) {
            Item retVal = null;
            int smallest = Integer.MAX_VALUE;
            for (Item option : options.stream().sorted(Comparator.comparing(option -> BuiltInRegistries.ITEM.getKey(option).toString())).toList()) {
                int size = this.estimate(option, made, estimates, visiting).getManaSize();
                if (size > 0 && size < smallest) {
                    retVal = option;
                    smallest = size;
                }
            }
            return retVal;
        }

        /** The items each of the recipe's ingredients accepts. */
        private List<Set<Item>> ingredientItems(RecipeHolder<?> holder) {
            return this.ingredientItems.getOrDefault(holder, List.of());
        }

        private static Set<Item> itemsOf(Ingredient ingredient) {
            ItemStack[] stacks = ingredient.getItems();
            return stacks == null ? Set.of() : java.util.Arrays.stream(stacks).filter(Objects::nonNull).map(ItemStack::getItem)
                    .filter(item -> item != Items.AIR).collect(Collectors.toSet());
        }

        private void register() {
            List<IAffinity> all = new ArrayList<>(this.baseline);
            this.generated.values().forEach(generated -> all.add(generated.affinity()));
            this.manager.replaceAffinities(all);
        }

        private static List<Item> sortedItems() {
            return BuiltInRegistries.ITEM.stream().sorted(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString())).toList();
        }

        /**
         * Considers a candidate item. Returns false if the item can only be judged by a link to other results (a
         * stripped log, a drop, a bucket), to be considered once those are known.
         */
        private boolean consider(Item item, boolean secondPass) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (!(item instanceof BlockItem blockItem)) {
                return false;
            }
            Block block = blockItem.getBlock();
            if (block instanceof FlowerPotBlock || block instanceof LiquidBlock) {
                this.notConsidered++;
                return true;
            }
            String via = null;
            BlockEvidence evidence = this.survey.evidence(block);
            if (evidence != null) {
                via = "worldgen";
            } else if (strippedBase(id) != null) {
                return false;
            } else {
                Set<Block> droppers = this.survey.droppedBy(item).stream()
                        .filter(dropper -> dropper != block && this.survey.evidence(dropper) != null).collect(Collectors.toSet());
                if (!droppers.isEmpty()) {
                    evidence = new BlockEvidence();
                    for (Block dropper : droppers) {
                        evidence.merge(this.survey.evidence(dropper));
                    }
                    via = "dropped by " + joinIds(droppers.stream().map(BuiltInRegistries.BLOCK::getKey).toList(), 3);
                } else {
                    List<WorldgenSurvey.Source> matches = this.nameMatches(id);
                    if (!matches.isEmpty()) {
                        evidence = WorldgenSurvey.matchedEvidence(matches);
                        via = "name matches feature " + joinIds(matches.stream().map(source -> source.id).toList(), 3);
                    }
                }
            }

            Classification classification = AffinityRules.classify(block, item, evidence);
            if (classification == null) {
                this.skipped.add(new Skipped(id, "no natural role recognized", this.describe(via, evidence)));
                return true;
            }
            if (evidence == null && (classification.confidence() == Confidence.NAME || classification.confidence() == Confidence.FALLBACK)) {
                this.skipped.add(new Skipped(id, "role " + name(classification.role()) + " is only a guess from its name (" + classification.reason()
                        + "), and it is not found in world generation", "none"));
                return true;
            }
            this.generate(id, classification.role(), classification.reason(), evidence, via, secondPass);
            return true;
        }

        /** Considers a candidate that can only be judged by its link to another item or block. */
        private void considerLinked(Item item) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            ResourceLocation stripped = strippedBase(id);
            if (stripped != null && (this.registered.contains(stripped) || this.generated.containsKey(stripped) || this.crafted.contains(BuiltInRegistries.ITEM.get(stripped)))) {
                this.put(id, NaturalRole.STRIPPED, stripped, SourceList.EMPTY, SourceList.EMPTY, "stripped form of " + stripped, List.of(), "none", false);
                return;
            }
            if (item instanceof BucketItem bucket && bucket.content != Fluids.EMPTY) {
                Block fluidBlock = bucket.content.defaultFluidState().createLegacyBlock().getBlock();
                BlockEvidence evidence = this.survey.evidence(fluidBlock);
                if (evidence != null) {
                    this.generateBucket(id, fluidBlock, evidence);
                    return;
                }
            }
            Set<Block> droppers = this.survey.droppedBy(item);
            if (!droppers.isEmpty() && !(item instanceof BlockItem)) {
                this.generateDrop(id, droppers);
                return;
            }
            if (item instanceof BlockItem) {
                this.skipped.add(new Skipped(id, stripped != null ? "stripped form of " + stripped + ", which has no affinity" : "no link found", "none"));
            } else {
                this.notConsidered++;
            }
        }

        private void generate(ResourceLocation id, NaturalRole role, String roleReason, @Nullable BlockEvidence evidence, @Nullable String via, boolean secondPass) {
            ResourceLocation analog = role.analog();
            SourceList analogTotal = this.currentTotal(analog);
            if (analogTotal.isEmpty()) {
                this.skipped.add(new Skipped(id, "analog " + analog + " of role " + name(role) + " has no affinity", this.describe(via, evidence)));
                return;
            }
            List<Modifier> modifiers = List.of();
            if (evidence != null) {
                BiomeProfile profile = BiomeProfile.of(evidence.biomes, this.survey);
                boolean underground = evidence.isMostlyUnderground() || profile.has(BiomeProfile.Group.CAVE);
                // Soil and stone are often laid down under water (sand and gravel disks), so only count water if always
                boolean water = role.kind() == NaturalRole.Kind.LIVING || role.kind() == NaturalRole.Kind.CREATURE
                        ? evidence.isMostlyWater() : evidence.placements > 0 && evidence.water == evidence.placements;
                modifiers = AffinityRules.modifiers(role, analogTotal, new AffinityRules.Context(profile, water, underground));
            }
            SourceList add = SourceList.EMPTY;
            SourceList remove = SourceList.EMPTY;
            for (Modifier modifier : modifiers) {
                if (modifier.remove()) {
                    remove = remove.add(modifier.source(), modifier.amount());
                } else {
                    add = add.add(modifier.source(), modifier.amount());
                }
            }
            this.put(id, role, analog, add, remove, roleReason, modifiers.stream().map(Modifier::describe).toList(), this.describe(via, evidence), secondPass);
        }

        /** A bucket of a worldgen fluid: the empty bucket plus 20 of the fluid's source, as for water and lava buckets. */
        private void generateBucket(ResourceLocation id, Block fluidBlock, BlockEvidence evidence) {
            BiomeProfile profile = BiomeProfile.of(evidence.biomes, this.survey);
            ResourceLocation fluidId = BuiltInRegistries.BLOCK.getKey(fluidBlock);
            Set<String> tokens = AffinityRules.tokens(fluidId.getPath());
            Source source;
            String why;
            if (tokens.contains("blood")) {
                source = com.verdantartifice.primalmagick.common.sources.Sources.BLOOD;
                why = "fluid named blood";
            } else if (profile.dimension() == BiomeProfile.Dimension.NETHER) {
                source = com.verdantartifice.primalmagick.common.sources.Sources.INFERNAL;
                why = "fluid of the Nether";
            } else if (profile.dimension() == BiomeProfile.Dimension.END) {
                source = com.verdantartifice.primalmagick.common.sources.Sources.VOID;
                why = "fluid of the End";
            } else {
                source = com.verdantartifice.primalmagick.common.sources.Sources.SEA;
                why = "fluid";
            }
            SourceList add = SourceList.builder().with(source, 20).build();
            this.put(id, NaturalRole.BUCKET, NaturalRole.BUCKET.analog(), add, SourceList.EMPTY, "bucket of " + fluidId,
                    List.of("+20 " + source.getId().getPath() + " (" + why + ")"), this.describe("fluid placed by worldgen", evidence), false);
        }

        /**
         * An item only known as a drop of natural blocks (worldgen blocks, or blocks given an affinity in this run): half
         * of its droppers' affinity, rounded down, at least 1 of each source.
         */
        private void generateDrop(ResourceLocation id, Set<Block> droppers) {
            SourceList combined = SourceList.EMPTY;
            List<ResourceLocation> dropperIds = new ArrayList<>();
            for (Block dropper : droppers) {
                ResourceLocation dropperItem = BuiltInRegistries.ITEM.getKey(dropper.asItem());
                if (dropper.asItem() == Items.AIR || (this.survey.evidence(dropper) == null && !this.generated.containsKey(dropperItem))) {
                    continue;
                }
                SourceList total = this.currentTotal(dropperItem);
                if (!total.isEmpty()) {
                    combined = combined.merge(total);
                    dropperIds.add(dropperItem);
                }
            }
            if (combined.isEmpty()) {
                this.notConsidered++;
                return;
            }
            SourceList.Builder builder = SourceList.builder();
            for (Source source : combined.getSources()) {
                builder.with(source, Math.max(1, combined.getAmount(source) / 2));
            }
            SourceList set = builder.build();
            String evidence = "dropped by " + joinIds(dropperIds, 3);
            JsonObject json = fixed(id, set);
            this.store(new Generated(id, decode(id, json), json, NaturalRole.DROP, null, set, "only obtained as a drop of " + joinIds(dropperIds, 3),
                    List.of("half of the largest dropper affinity"), evidence, false));
        }

        private void put(ResourceLocation id, NaturalRole role, ResourceLocation base, SourceList add, SourceList remove, String roleReason, List<String> modifiers,
                         String evidence, boolean secondPass) {
            JsonObject json = new JsonObject();
            json.addProperty("type", AffinityType.ITEM.getSerializedName());
            json.addProperty("target", id.toString());
            json.addProperty("base", base.toString());
            if (!add.isEmpty()) {
                json.add("add", encode(add));
            }
            if (!remove.isEmpty()) {
                json.add("remove", encode(remove));
            }
            SourceList total = this.currentTotal(base).add(add).remove(remove);
            this.store(new Generated(id, decode(id, json), json, role, base, total, roleReason, modifiers, evidence, secondPass));
        }

        private void store(Generated generated) {
            this.generated.put(generated.item(), generated);
        }

        /** The affinity an item has now: generated in this run, or from Primal Magick (data packs and recipes). */
        SourceList currentTotal(ResourceLocation id) {
            Generated generated = this.generated.get(id);
            if (generated != null) {
                return generated.total();
            }
            try {
                IAffinity affinity = this.manager.getOrGenerateItemAffinityAsync(id, this.recipeManager, this.registryAccess, new ArrayList<>()).join();
                return affinity == null ? SourceList.EMPTY : affinity.getTotalAsync(this.recipeManager, this.registryAccess, new ArrayList<>()).join();
            } catch (RuntimeException e) {
                LOGGER.debug("Could not compute the affinity of {}", id, e);
                return SourceList.EMPTY;
            }
        }

        /** The unstripped item that a {@code stripped_} item comes from, if it exists. */
        @Nullable
        private static ResourceLocation strippedBase(ResourceLocation id) {
            if (!id.getPath().startsWith("stripped_")) {
                return null;
            }
            ResourceLocation base = id.withPath(id.getPath().substring("stripped_".length()));
            return BuiltInRegistries.ITEM.containsKey(base) ? base : null;
        }

        /** Opaque worldgen sources whose feature type or id names the same thing as the item. */
        private List<WorldgenSurvey.Source> nameMatches(ResourceLocation id) {
            Set<String> name = normalizedName(id.getPath());
            if (name.isEmpty()) {
                return List.of();
            }
            return this.opaqueSources.stream().filter(source -> WorldgenSurvey.opaqueNames(source).stream()
                    .map(full -> normalizedName(full.substring(full.lastIndexOf(':') + 1)))
                    .anyMatch(name::equals)).toList();
        }

        private String describe(@Nullable String via, @Nullable BlockEvidence evidence) {
            if (evidence == null) {
                return "none";
            }
            BiomeProfile profile = BiomeProfile.of(evidence.biomes, this.survey);
            StringBuilder builder = new StringBuilder();
            builder.append(via == null ? "worldgen" : via).append(": ").append(profile.describe());
            String uses = evidence.uses.entrySet().stream().sorted(Map.Entry.<WorldgenSurvey.Use, Integer>comparingByValue().reversed())
                    .map(e -> e.getKey().name().toLowerCase(Locale.ROOT) + " " + e.getValue()).collect(Collectors.joining(", "));
            builder.append("; used as ").append(uses);
            if (evidence.water > 0) {
                builder.append("; in/by water ").append(evidence.water).append('/').append(evidence.placements);
            }
            if (evidence.underground > 0) {
                builder.append("; underground ").append(evidence.underground).append('/').append(evidence.placements);
            }
            if (!evidence.steps.isEmpty()) {
                builder.append("; steps ").append(evidence.steps.stream().map(step -> step.getSerializedName()).collect(Collectors.joining(", ")));
            }
            builder.append("; from ").append(joinIds(new ArrayList<>(evidence.sources), 4));
            return builder.toString();
        }
    }

    static String name(NaturalRole role) {
        return role.name().toLowerCase(Locale.ROOT);
    }

    /** The tokens of a name that say what it is, singular, without size, color and shape words. */
    static Set<String> normalizedName(String path) {
        Set<String> retVal = new TreeSet<>();
        for (String token : path.toLowerCase(Locale.ROOT).split("[_/]")) {
            if (token.isEmpty() || NAME_NOISE.contains(token)) {
                continue;
            }
            retVal.add(token.length() > 3 && token.endsWith("s") && !token.endsWith("ss") ? token.substring(0, token.length() - 1) : token);
        }
        return retVal;
    }

    static String joinIds(List<?> ids, int limit) {
        String joined = ids.stream().limit(limit).map(Object::toString).collect(Collectors.joining(", "));
        return ids.size() > limit ? joined + " (+" + (ids.size() - limit) + " more)" : joined;
    }

    private static JsonObject encode(SourceList sources) {
        return sources.serializeJson();
    }

    /** Reads an affinity in data pack format, as Primal Magick reads its data packs. */
    private static IAffinity decode(ResourceLocation id, JsonObject json) {
        return ItemAffinity.SERIALIZER.read(id, json);
    }

    /** An item affinity with a fixed set of values, in data pack format. */
    private static JsonObject fixed(ResourceLocation id, SourceList set) {
        JsonObject json = new JsonObject();
        json.addProperty("type", AffinityType.ITEM.getSerializedName());
        json.addProperty("target", id.toString());
        json.add("set", encode(set));
        return json;
    }
}
