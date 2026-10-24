package com.claviger.primalmagickextended.common.affinities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A survey of where world generation puts each block, read from the server's live registries so that it covers
 * every mod and data pack. Placed features and surface rules are encoded to JSON through their own codecs and
 * walked, which works for modded feature types without knowing their classes: a block state ({@code {"Name": ...}})
 * is a placement unless it sits inside a block predicate or rule test, and the nearest role key on its JSON path
 * ({@code trunk_provider}, {@code foliage_provider}, {@code ground_state}, ...) says what it is used as.
 */
public final class WorldgenSurvey {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_DEPTH = 256;

    /** What a block is used as by the feature or rule that places it. */
    public enum Use {
        WOOD, FOLIAGE, HANGING, FRUIT, GROUND, ORE, SURFACE, PLANT, DECORATION, FLUID, OTHER;

        public boolean isGround() {
            return this == GROUND || this == ORE || this == SURFACE;
        }
    }

    /** Role keys of feature configurations, mapped to what the blocks below them are used as. */
    private static final Map<String, Use> ROLE_KEYS = new HashMap<>();
    static {
        for (String key : List.of("trunk_provider", "stem_provider", "root_provider", "roots_provider", "above_root_provider",
                "muddy_roots_provider", "log_decorators", "stump_decorators", "branch_provider", "log_provider")) {
            ROLE_KEYS.put(key, Use.WOOD);
        }
        for (String key : List.of("foliage_provider", "alt_foliage_provider", "cap_provider", "leaves_provider")) {
            ROLE_KEYS.put(key, Use.FOLIAGE);
        }
        for (String key : List.of("hanging_provider", "vine_provider")) {
            ROLE_KEYS.put(key, Use.HANGING);
        }
        ROLE_KEYS.put("trunk_fruit_provider", Use.FRUIT);
        for (String key : List.of("below_trunk_provider", "dirt_provider", "ground_state", "barrier", "state_provider",
                "filling_provider", "inner_layer_provider", "alternate_inner_layer_provider", "middle_layer_provider",
                "outer_layer_provider", "inner_placements", "default_block")) {
            ROLE_KEYS.put(key, Use.GROUND);
        }
        ROLE_KEYS.put("targets", Use.ORE);
        ROLE_KEYS.put("result_state", Use.SURFACE);
        for (String key : List.of("to_place", "layers", "vegetation_feature")) {
            ROLE_KEYS.put(key, Use.PLANT);
        }
        ROLE_KEYS.put("decorators", Use.DECORATION);
        for (String key : List.of("fluid", "default_fluid")) {
            ROLE_KEYS.put(key, Use.FLUID);
        }
    }

    /** Keys whose values are matched against the world rather than placed in it. */
    private static final Set<String> MATCH_KEYS = Set.of("target", "predicate", "predicates", "if_true", "blocks", "fluids",
            "can_place_on", "can_replace", "replaceable", "location_predicate", "input_predicate", "position_predicate",
            "target_condition", "allowed_search_condition", "block_state");

    /** Keys whose string values may name another placed or configured feature. */
    private static final Set<String> FEATURE_KEYS = Set.of("feature", "features", "default", "vegetation_feature",
            "placed_feature", "configured_feature");

    /** Feature types that only place blocks in or under water. */
    private static final Set<String> WATER_FEATURE_TYPES = Set.of("minecraft:seagrass", "minecraft:kelp", "minecraft:coral_tree",
            "minecraft:coral_mushroom", "minecraft:coral_claw", "minecraft:sea_pickle", "minecraft:waterlogged_vegetation_patch",
            "minecraft:underwater_magma");

    private static final Set<GenerationStep.Decoration> UNDERGROUND_STEPS = EnumSet.of(GenerationStep.Decoration.UNDERGROUND_ORES,
            GenerationStep.Decoration.UNDERGROUND_DECORATION, GenerationStep.Decoration.UNDERGROUND_STRUCTURES);

    /** One block state placement found in a feature or rule. */
    private record Placement(Block block, Use use, boolean waterlogged) {}

    /**
     * A top level placed feature, or one dimension's surface rule, with everything nested inside it and the biomes and
     * generation steps it runs in.
     */
    public static final class Source {
        public final String id;
        final Set<ResourceKey<Biome>> biomes = new HashSet<>();
        final Set<GenerationStep.Decoration> steps = EnumSet.noneOf(GenerationStep.Decoration.class);
        final List<Placement> placements = new ArrayList<>();
        /** Feature types met inside, and those of them configured with nothing (their blocks are hard-coded). */
        final Set<String> featureTypes = new LinkedHashSet<>();
        final Set<String> opaqueTypes = new LinkedHashSet<>();
        /** Water appears in a placement predicate, or a feature type only works under water. */
        boolean water;
        /** Placement scans for a floor or ceiling, as cave decorations do. */
        boolean scan;

        Source(String id) {
            this.id = id;
        }

        boolean isUnderground() {
            return this.scan || (!this.steps.isEmpty() && UNDERGROUND_STEPS.containsAll(this.steps));
        }
    }

    /** Everything the survey knows about where one block generates. */
    public static final class BlockEvidence {
        public final Set<ResourceKey<Biome>> biomes = new HashSet<>();
        public final Map<Use, Integer> uses = new EnumMap<>(Use.class);
        public final Set<GenerationStep.Decoration> steps = EnumSet.noneOf(GenerationStep.Decoration.class);
        public final Set<String> sources = new LinkedHashSet<>();
        public final Set<String> featureTypes = new LinkedHashSet<>();
        public int placements;
        public int water;
        public int underground;

        void add(Source source, Placement placement) {
            this.biomes.addAll(source.biomes);
            this.uses.merge(placement.use(), 1, Integer::sum);
            this.steps.addAll(source.steps);
            this.sources.add(source.id);
            this.featureTypes.addAll(source.featureTypes);
            this.placements++;
            if (source.water || placement.waterlogged()) {
                this.water++;
            }
            if (source.isUnderground()) {
                this.underground++;
            }
        }

        /** Adds every source that places nothing visible, as matched by name. */
        void addMatched(Source source) {
            this.biomes.addAll(source.biomes);
            this.uses.merge(Use.OTHER, 1, Integer::sum);
            this.steps.addAll(source.steps);
            this.sources.add(source.id);
            this.featureTypes.addAll(source.featureTypes);
            this.placements++;
            if (source.water) {
                this.water++;
            }
            if (source.isUnderground()) {
                this.underground++;
            }
        }

        public void merge(BlockEvidence other) {
            this.biomes.addAll(other.biomes);
            other.uses.forEach((use, count) -> this.uses.merge(use, count, Integer::sum));
            this.steps.addAll(other.steps);
            this.sources.addAll(other.sources);
            this.featureTypes.addAll(other.featureTypes);
            this.placements += other.placements;
            this.water += other.water;
            this.underground += other.underground;
        }

        /** The most common use, or null if there is none. */
        @Nullable
        public Use mainUse() {
            return this.uses.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        }

        public boolean hasUse(Use use) {
            return this.uses.containsKey(use);
        }

        public boolean isMostlyWater() {
            return this.placements > 0 && this.water * 2 >= this.placements;
        }

        public boolean isMostlyUnderground() {
            return this.placements > 0 && this.underground * 2 >= this.placements;
        }
    }

    private final RegistryAccess registryAccess;
    private final DynamicOps<JsonElement> ops;
    private final Registry<PlacedFeature> placedFeatures;
    private final Registry<ConfiguredFeature<?, ?>> configuredFeatures;
    private final Set<String> predicateTypes = new HashSet<>();

    /** Every biome that some loaded dimension can generate, with its dimension. */
    private final Map<ResourceKey<Biome>, ResourceKey<Level>> biomeDimensions = new HashMap<>();
    private final Map<String, Source> sources = new LinkedHashMap<>();
    private final Map<Block, BlockEvidence> evidence = new HashMap<>();
    private final Map<Item, Set<Block>> droppedBy = new HashMap<>();

    private WorldgenSurvey(MinecraftServer server) {
        this.registryAccess = server.registryAccess();
        this.ops = RegistryOps.create(JsonOps.INSTANCE, this.registryAccess);
        this.placedFeatures = this.registryAccess.lookupOrThrow(Registries.PLACED_FEATURE);
        this.configuredFeatures = this.registryAccess.lookupOrThrow(Registries.CONFIGURED_FEATURE);
        BuiltInRegistries.BLOCK_PREDICATE_TYPE.keySet().forEach(id -> this.predicateTypes.add(id.toString()));
    }

    /** Surveys every loaded dimension of the server. */
    public static WorldgenSurvey run(MinecraftServer server) {
        WorldgenSurvey survey = new WorldgenSurvey(server);
        for (ServerLevel level : server.getAllLevels()) {
            ChunkGenerator generator = level.getChunkSource().getGenerator();
            generator.getBiomeSource().possibleBiomes().forEach(biome ->
                    biome.unwrapKey().ifPresent(key -> survey.biomeDimensions.putIfAbsent(key, level.dimension())));
        }
        survey.surveyFeatures();
        for (ServerLevel level : server.getAllLevels()) {
            if (level.getChunkSource().getGenerator() instanceof NoiseBasedChunkGenerator noise) {
                survey.surveySurface(level.dimension(), noise.generatorSettings().value());
            }
        }
        survey.sources.values().forEach(source -> source.placements.forEach(placement ->
                survey.evidence.computeIfAbsent(placement.block(), $ -> new BlockEvidence()).add(source, placement)));
        survey.surveyDrops(server);
        return survey;
    }

    /** The dimension that generates the biome, or null if no loaded dimension does. */
    @Nullable
    public ResourceKey<Level> dimensionOf(ResourceKey<Biome> biome) {
        return this.biomeDimensions.get(biome);
    }

    @Nullable
    public BlockEvidence evidence(Block block) {
        return this.evidence.get(block);
    }

    public Map<Block, BlockEvidence> allEvidence() {
        return this.evidence;
    }

    /** The blocks whose loot tables can drop the item. */
    public Set<Block> droppedBy(Item item) {
        return this.droppedBy.getOrDefault(item, Set.of());
    }

    /**
     * Sources whose blocks are hard-coded in Java: those containing a feature type configured with nothing, and
     * those that place no block the survey can see at all.
     */
    public Collection<Source> opaqueSources() {
        return this.sources.values().stream().filter(source -> !source.opaqueTypes.isEmpty() || (source.placements.isEmpty() && !source.featureTypes.isEmpty())).toList();
    }

    /** Evidence for a block matched by name to an opaque source. */
    public static BlockEvidence matchedEvidence(Collection<Source> sources) {
        BlockEvidence retVal = new BlockEvidence();
        sources.forEach(retVal::addMatched);
        return retVal;
    }

    /** The names an opaque source goes by: its id and its hard-coded feature types. */
    public static Set<String> opaqueNames(Source source) {
        Set<String> retVal = new LinkedHashSet<>(source.placements.isEmpty() ? source.featureTypes : source.opaqueTypes);
        retVal.add(source.id);
        return retVal;
    }

    public RegistryAccess registryAccess() {
        return this.registryAccess;
    }

    private void surveyFeatures() {
        Registry<Biome> biomes = this.registryAccess.lookupOrThrow(Registries.BIOME);
        GenerationStep.Decoration[] steps = GenerationStep.Decoration.values();
        biomes.listElements().forEach(biome -> {
            if (!this.biomeDimensions.containsKey(biome.key())) {
                return;
            }
            List<HolderSet<PlacedFeature>> features = biome.value().getGenerationSettings().features();
            for (int index = 0; index < features.size(); index++) {
                GenerationStep.Decoration step = index < steps.length ? steps[index] : null;
                for (Holder<PlacedFeature> feature : features.get(index)) {
                    String id = feature.unwrapKey().map(key -> key.identifier().toString()).orElse("inline:" + System.identityHashCode(feature.value()));
                    Source source = this.sources.computeIfAbsent(id, $ -> this.walkPlacedFeature(id, feature.value()));
                    source.biomes.add(biome.key());
                    if (step != null) {
                        source.steps.add(step);
                    }
                }
            }
        });
    }

    private Source walkPlacedFeature(String id, PlacedFeature feature) {
        Source source = new Source(id);
        Set<String> visited = new HashSet<>();
        visited.add("placed:" + id);
        this.encode(PlacedFeature.DIRECT_CODEC, feature, id).ifPresent(json -> this.walk(json, "", new ArrayDeque<>(), source, visited, false, false, 0));
        return source;
    }

    private void surveySurface(ResourceKey<Level> dimension, NoiseGeneratorSettings settings) {
        String id = "surface_rule:" + dimension.identifier();
        Set<ResourceKey<Biome>> dimensionBiomes = new HashSet<>();
        this.biomeDimensions.forEach((biome, dim) -> {
            if (dim.equals(dimension)) {
                dimensionBiomes.add(biome);
            }
        });
        // Each surface rule placement is recorded in its own source, restricted to the biomes its conditions allow
        this.encode(SurfaceRules.RuleSource.CODEC, settings.surfaceRule(), id).ifPresent(json ->
                this.walkSurfaceRule(json, dimensionBiomes, dimension, 0));
        this.addSurfacePlacement(dimension, settings.defaultBlock().getBlock(), Use.GROUND, dimensionBiomes);
        this.addSurfacePlacement(dimension, settings.defaultFluid().getBlock(), Use.FLUID, dimensionBiomes);
    }

    private void walkSurfaceRule(JsonElement json, Set<ResourceKey<Biome>> biomes, ResourceKey<Level> dimension, int depth) {
        if (depth > MAX_DEPTH || biomes.isEmpty()) {
            return;
        }
        if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(element -> this.walkSurfaceRule(element, biomes, dimension, depth + 1));
        } else if (json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();
            String type = stringOf(object, "type");
            if ("minecraft:condition".equals(type) && object.has("if_true") && object.has("then_run")) {
                JsonElement condition = object.get("if_true");
                Set<ResourceKey<Biome>> allowed = biomes;
                if (condition.isJsonObject() && "minecraft:biome".equals(stringOf(condition.getAsJsonObject(), "type"))) {
                    Set<ResourceKey<Biome>> named = new HashSet<>();
                    JsonElement list = condition.getAsJsonObject().get("biome_is");
                    if (list != null && list.isJsonArray()) {
                        list.getAsJsonArray().forEach(element -> named.add(ResourceKey.create(Registries.BIOME, Identifier.parse(element.getAsString()))));
                    }
                    allowed = new HashSet<>(biomes);
                    allowed.retainAll(named);
                }
                this.walkSurfaceRule(object.get("then_run"), allowed, dimension, depth + 1);
            } else if (object.has("base") && object.has("sources") && object.get("sources").isJsonObject()) {
                // TerraBlender's namespaced rule: each namespace's rule runs only in that namespace's biomes
                this.walkSurfaceRule(object.get("base"), biomes, dimension, depth + 1);
                object.getAsJsonObject("sources").entrySet().forEach(entry -> {
                    Set<ResourceKey<Biome>> allowed = new HashSet<>();
                    biomes.forEach(biome -> {
                        if (biome.identifier().getNamespace().equals(entry.getKey())) {
                            allowed.add(biome);
                        }
                    });
                    this.walkSurfaceRule(entry.getValue(), allowed, dimension, depth + 1);
                });
            } else {
                JsonElement result = object.get("result_state");
                if (result != null && result.isJsonObject()) {
                    this.blockOf(result.getAsJsonObject()).ifPresent(block -> this.addSurfacePlacement(dimension, block, Use.SURFACE, biomes));
                }
                object.entrySet().forEach(entry -> {
                    if (!entry.getKey().equals("result_state")) {
                        this.walkSurfaceRule(entry.getValue(), biomes, dimension, depth + 1);
                    }
                });
            }
        }
    }

    private void addSurfacePlacement(ResourceKey<Level> dimension, Block block, Use use, Set<ResourceKey<Biome>> biomes) {
        if (block == Blocks.AIR) {
            return;
        }
        String id = "surface_rule:" + dimension.identifier() + "/" + BuiltInRegistries.BLOCK.getKey(block);
        Source source = this.sources.computeIfAbsent(id, Source::new);
        source.biomes.addAll(biomes);
        if (source.placements.isEmpty()) {
            source.placements.add(new Placement(block, use, false));
        }
    }

    /**
     * Walks a feature's JSON, recording placements, feature types and placement hints in the source, and following
     * features that are referenced by id.
     */
    private void walk(JsonElement json, String key, Deque<String> path, Source source, Set<String> visited, boolean match, boolean negated, int depth) {
        if (depth > MAX_DEPTH) {
            return;
        }
        if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(element -> this.walk(element, key, path, source, visited, match, negated, depth + 1));
        } else if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            String value = json.getAsString();
            if (match && !negated && (value.equals("minecraft:water") || value.equals("#minecraft:water"))) {
                source.water = true;
            } else if (!match && FEATURE_KEYS.contains(key)) {
                this.follow(value, FeatureKind.PLACED, key, path, source, visited, depth);
            }
        } else if (json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();
            String type = stringOf(object, "type");
            boolean childMatch = match || object.has("predicate_type") || (type != null && this.predicateTypes.contains(type));
            boolean childNegated = negated || "minecraft:not".equals(type);
            if (type != null && object.has("config")) {
                source.featureTypes.add(type);
                JsonElement config = object.get("config");
                if (config.isJsonObject() && config.getAsJsonObject().isEmpty()) {
                    source.opaqueTypes.add(type);
                }
                if (WATER_FEATURE_TYPES.contains(type)) {
                    source.water = true;
                }
            }
            if ("minecraft:environment_scan".equals(type)) {
                source.scan = true;
            }
            if (!match && object.has("Name") && object.get("Name").isJsonPrimitive()) {
                this.blockOf(object).ifPresent(block -> source.placements.add(new Placement(block, useOf(path), isWaterlogged(object))));
                return;
            }
            // A placed feature object: its "feature" names a configured feature
            boolean placedObject = object.has("placement") && object.has("feature");
            object.entrySet().forEach(entry -> {
                String childKey = entry.getKey();
                JsonElement child = entry.getValue();
                if (placedObject && childKey.equals("feature") && child.isJsonPrimitive()) {
                    this.follow(child.getAsString(), FeatureKind.CONFIGURED, childKey, path, source, visited, depth);
                    return;
                }
                path.addLast(childKey);
                this.walk(child, childKey, path, source, visited, childMatch || MATCH_KEYS.contains(childKey), childNegated, depth + 1);
                path.removeLast();
            });
        }
    }

    private enum FeatureKind { PLACED, CONFIGURED }

    /** Follows a feature referenced by id (or a tag of them), walking it as part of the same source. */
    private void follow(String reference, FeatureKind kind, String key, Deque<String> path, Source source, Set<String> visited, int depth) {
        if (reference.startsWith("#")) {
            Identifier tagId = Identifier.tryParse(reference.substring(1));
            if (tagId != null) {
                this.placedFeatures.getTagOrEmpty(net.minecraft.tags.TagKey.create(Registries.PLACED_FEATURE, tagId)).forEach(holder ->
                        holder.unwrapKey().ifPresent(k -> this.follow(k.identifier().toString(), FeatureKind.PLACED, key, path, source, visited, depth)));
            }
            return;
        }
        Identifier id = Identifier.tryParse(reference);
        if (id == null || !visited.add(kind.name() + ":" + id)) {
            return;
        }
        path.addLast(key);
        PlacedFeature placed = kind == FeatureKind.PLACED ? this.placedFeatures.getValue(id) : null;
        if (placed != null) {
            this.encode(PlacedFeature.DIRECT_CODEC, placed, id.toString()).ifPresent(json -> this.walk(json, key, path, source, visited, false, false, depth + 1));
        } else {
            // A configured feature, or a reference that is not a placed feature after all
            ConfiguredFeature<?, ?> configured = this.configuredFeatures.getValue(id);
            if (configured != null && (kind == FeatureKind.CONFIGURED || visited.add(FeatureKind.CONFIGURED.name() + ":" + id))) {
                this.encode(ConfiguredFeature.DIRECT_CODEC, configured, id.toString()).ifPresent(json -> this.walk(json, key, path, source, visited, false, false, depth + 1));
            }
        }
        path.removeLast();
    }

    /** Reads the loot table of every block, to link the items they drop. */
    private void surveyDrops(MinecraftServer server) {
        BuiltInRegistries.BLOCK.forEach(block -> block.getLootTable().ifPresent(key -> {
            LootTable table = server.reloadableRegistries().getLootTable(key);
            this.encode(LootTable.DIRECT_CODEC, table, key.identifier().toString()).ifPresent(json -> this.collectDrops(json, block, 0));
        }));
    }

    private void collectDrops(JsonElement json, Block block, int depth) {
        if (depth > MAX_DEPTH) {
            return;
        }
        if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(element -> this.collectDrops(element, block, depth + 1));
        } else if (json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();
            if ("minecraft:item".equals(stringOf(object, "type"))) {
                String name = stringOf(object, "name");
                Identifier id = name == null ? null : Identifier.tryParse(name);
                if (id != null) {
                    BuiltInRegistries.ITEM.getOptional(id).ifPresent(item -> this.droppedBy.computeIfAbsent(item, $ -> new LinkedHashSet<>()).add(block));
                }
            }
            object.entrySet().forEach(entry -> this.collectDrops(entry.getValue(), block, depth + 1));
        }
    }

    private <T> Optional<JsonElement> encode(Codec<T> codec, T value, String what) {
        return codec.encodeStart(this.ops, value).resultOrPartial(error -> LOGGER.debug("Could not encode {} for affinity generation: {}", what, error));
    }

    private Optional<Block> blockOf(JsonObject state) {
        Identifier id = Identifier.tryParse(state.get("Name").getAsString());
        return id == null ? Optional.empty() : BuiltInRegistries.BLOCK.getOptional(id).filter(block -> block != Blocks.AIR && block != Blocks.CAVE_AIR && block != Blocks.VOID_AIR);
    }

    private static boolean isWaterlogged(JsonObject state) {
        JsonElement properties = state.get("Properties");
        return properties != null && properties.isJsonObject() && "true".equals(stringOf(properties.getAsJsonObject(), "waterlogged"));
    }

    /** The use given by the nearest role key on the path. */
    @NotNull
    private static Use useOf(Deque<String> path) {
        var iterator = path.descendingIterator();
        while (iterator.hasNext()) {
            Use use = ROLE_KEYS.get(iterator.next());
            if (use != null) {
                return use;
            }
        }
        return Use.OTHER;
    }

    @Nullable
    private static String stringOf(JsonObject object, String key) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString() ? element.getAsString() : null;
    }
}
