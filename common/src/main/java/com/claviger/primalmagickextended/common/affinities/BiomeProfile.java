package com.claviger.primalmagickextended.common.affinities;

import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.Sources;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Where a block generates, summarized from the biomes it generates in: which dimension holds most of them, and what
 * share of its overworld biomes belong to each climate group, judged by biome tags. Good biome mods tag their biomes
 * (a redwood forest as a forest, a salt flat as dry), so the tags carry the hint even for biomes nobody here knows.
 */
public record BiomeProfile(int biomeCount, Dimension dimension, Map<Dimension, Integer> dimensionCounts, Map<Group, Double> groupShares) {
    /** A group applies when at least this share of a block's overworld biomes belong to it. */
    public static final double GROUP_THRESHOLD = 0.5D;

    public enum Dimension { OVERWORLD, NETHER, END, OTHER }

    /** Climate groups, each with the source it suggests and the biome tags that mark it. */
    public enum Group {
        AQUATIC(Sources.SEA, "minecraft:is_ocean", "minecraft:is_deep_ocean", "minecraft:is_river", "minecraft:is_beach",
                "c:is_ocean", "c:is_deep_ocean", "c:is_shallow_ocean", "c:is_river", "c:is_beach", "c:is_aquatic", "c:is_swamp"),
        ARID(Sources.SUN, "c:is_desert", "c:is_badlands", "minecraft:is_badlands", "minecraft:is_savanna", "c:is_savanna", "c:is_dry"),
        HIGH_OR_FROZEN(Sources.SKY, "c:is_snowy", "c:is_icy", "c:is_mountain", "minecraft:is_mountain", "c:is_windswept"),
        DARK_OR_MAGICAL(Sources.MOON, "c:is_spooky", "c:is_dead", "c:is_mushroom", "c:is_magical"),
        /** Underground biomes: no sunlight, so it moves the block underground rather than adding a source. */
        CAVE(null, "c:is_cave", "c:is_underground");

        @Nullable
        private final Source source;
        private final List<TagKey<Biome>> tags;

        Group(@Nullable Source source, String... tags) {
            this.source = source;
            this.tags = java.util.Arrays.stream(tags).map(tag -> TagKey.create(Registries.BIOME, ResourceLocation.parse(tag))).toList();
        }

        @Nullable
        public Source source() {
            return this.source;
        }

        public List<TagKey<Biome>> tags() {
            return this.tags;
        }

        boolean contains(Holder<Biome> biome) {
            return this.tags.stream().anyMatch(biome::is);
        }
    }

    private static final TagKey<Biome> IS_NETHER = TagKey.create(Registries.BIOME, ResourceLocation.withDefaultNamespace("is_nether"));
    private static final TagKey<Biome> IS_END = TagKey.create(Registries.BIOME, ResourceLocation.withDefaultNamespace("is_end"));
    private static final TagKey<Biome> IS_OVERWORLD = TagKey.create(Registries.BIOME, ResourceLocation.withDefaultNamespace("is_overworld"));

    public static BiomeProfile of(Collection<ResourceKey<Biome>> biomes, WorldgenSurvey survey) {
        Registry<Biome> registry = survey.registryAccess().registryOrThrow(Registries.BIOME);
        Map<Dimension, Integer> dimensionCounts = new EnumMap<>(Dimension.class);
        Map<Group, Integer> groupCounts = new EnumMap<>(Group.class);
        int overworld = 0;
        for (ResourceKey<Biome> key : biomes) {
            Optional<Holder.Reference<Biome>> holder = registry.getHolder(key);
            Dimension dimension = dimensionOf(survey.dimensionOf(key), holder.orElse(null));
            dimensionCounts.merge(dimension, 1, Integer::sum);
            if (dimension == Dimension.OVERWORLD && holder.isPresent()) {
                overworld++;
                for (Group group : Group.values()) {
                    if (group.contains(holder.get())) {
                        groupCounts.merge(group, 1, Integer::sum);
                    }
                }
            }
        }
        int overworldCount = overworld;
        Map<Group, Double> shares = new EnumMap<>(Group.class);
        groupCounts.forEach((group, count) -> shares.put(group, (double) count / overworldCount));
        Dimension main = dimensionCounts.entrySet().stream()
                .filter(e -> e.getValue() * 2 > biomes.size())
                .map(Map.Entry::getKey).findFirst().orElse(biomes.isEmpty() ? Dimension.OVERWORLD : Dimension.OTHER);
        return new BiomeProfile(biomes.size(), main, dimensionCounts, shares);
    }

    private static Dimension dimensionOf(@Nullable ResourceKey<Level> level, @Nullable Holder<Biome> biome) {
        if (Level.OVERWORLD.equals(level)) {
            return Dimension.OVERWORLD;
        } else if (Level.NETHER.equals(level)) {
            return Dimension.NETHER;
        } else if (Level.END.equals(level)) {
            return Dimension.END;
        } else if (biome != null) {
            // A biome no loaded dimension uses, or one of a modded dimension: fall back on its tags
            if (biome.is(IS_NETHER)) {
                return Dimension.NETHER;
            } else if (biome.is(IS_END)) {
                return Dimension.END;
            } else if (biome.is(IS_OVERWORLD)) {
                return Dimension.OVERWORLD;
            }
        }
        return Dimension.OTHER;
    }

    public boolean has(Group group) {
        return this.groupShares.getOrDefault(group, 0D) >= GROUP_THRESHOLD;
    }

    public double share(Group group) {
        return this.groupShares.getOrDefault(group, 0D);
    }

    /** A short human description, e.g. "12 biomes, overworld; aquatic 75%". */
    public String describe() {
        if (this.biomeCount == 0) {
            return "no biomes";
        }
        String dims = this.dimensionCounts.entrySet().stream()
                .map(e -> e.getKey().name().toLowerCase() + (this.dimensionCounts.size() > 1 ? " " + e.getValue() : ""))
                .collect(Collectors.joining(", "));
        String groups = this.groupShares.entrySet().stream()
                .filter(e -> e.getValue() > 0D)
                .sorted(Map.Entry.<Group, Double>comparingByValue().reversed())
                .map(e -> e.getKey().name().toLowerCase() + " " + Math.round(e.getValue() * 100) + "%")
                .collect(Collectors.joining(", "));
        return this.biomeCount + (this.biomeCount == 1 ? " biome" : " biomes") + " (" + dims + ")" + (groups.isEmpty() ? "" : "; " + groups);
    }
}
