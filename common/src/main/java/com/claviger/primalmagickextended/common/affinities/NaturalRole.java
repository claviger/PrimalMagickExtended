package com.claviger.primalmagickextended.common.affinities;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * What a natural block is, as far as its affinity goes. Each role borrows the affinity of a vanilla block that Primal
 * Magick already defines (its analog), as Primal Magick's own data does for its variants: every log is based on the oak
 * log, every flower on the dandelion.
 */
public enum NaturalRole {
    LOG("oak_log", Kind.LIVING),
    LEAVES("oak_leaves", Kind.LIVING),
    SAPLING("oak_sapling", Kind.LIVING),
    FLOWER("dandelion", Kind.LIVING),
    TALL_FLOWER("sunflower", Kind.LIVING),
    TWIG("stick", Kind.LIVING),
    GRASS("short_grass", Kind.LIVING),
    TALL_GRASS("tall_grass", Kind.LIVING),
    GROUND_COVER("pink_petals", Kind.LIVING),
    VINE("vine", Kind.LIVING),
    HANGING_ROOTS("hanging_roots", Kind.LIVING),
    MOSS("moss_block", Kind.LIVING),
    MUSHROOM("brown_mushroom", Kind.LIVING),
    MUSHROOM_BLOCK("mushroom_stem", Kind.LIVING),
    CACTUS("cactus", Kind.LIVING),
    BAMBOO("bamboo", Kind.LIVING),
    REED("sugar_cane", Kind.LIVING),
    FRUIT("pumpkin", Kind.LIVING),
    SEAGRASS("seagrass", Kind.LIVING),
    KELP("kelp", Kind.LIVING),
    LILY_PAD("lily_pad", Kind.LIVING),
    CORAL("tube_coral", Kind.LIVING),
    CORAL_BLOCK("tube_coral_block", Kind.LIVING),
    DEAD_CORAL("dead_tube_coral", Kind.MINERAL),
    DEAD_CORAL_BLOCK("dead_tube_coral_block", Kind.MINERAL),
    FLESH("rotten_flesh", Kind.CREATURE),
    COBWEB("cobweb", Kind.CREATURE),
    EGG("sniffer_egg", Kind.CREATURE),
    DIRT("dirt", Kind.GROUND),
    GRASS_BLOCK("grass_block", Kind.GROUND),
    MUD("mud", Kind.GROUND),
    SAND("sand", Kind.GROUND),
    GRAVEL("gravel", Kind.GROUND),
    STONE("stone", Kind.GROUND),
    ORE("iron_ore", Kind.GROUND, 5),
    ICE("ice", Kind.GROUND),
    SNOW("snow_block", Kind.GROUND),
    CRYSTAL("amethyst_cluster", Kind.MINERAL),
    LARGE_BUD("large_amethyst_bud", Kind.MINERAL),
    MEDIUM_BUD("medium_amethyst_bud", Kind.MINERAL),
    SMALL_BUD("small_amethyst_bud", Kind.MINERAL),
    /** A bucket of a fluid that generates in the world: the empty bucket plus the fluid's source. */
    BUCKET("bucket", Kind.MINERAL),
    /** A stripped log: exactly the log it was stripped from. */
    STRIPPED(null, Kind.LIVING),
    /** An item only known as a worldgen block's drop: part of that block's affinity. */
    DROP(null, Kind.LIVING),
    /**
     * A crafted item made, directly or not, from generated ones, whose recipe gives it so small a share of its
     * ingredients that it rounds down to nothing: one of each of their sources, as Primal Magick's own data gives the
     * oak slab.
     */
    CRAFTED_MINIMUM(null, Kind.MINERAL);

    /** How context changes a role's affinity. */
    public enum Kind {
        /** Plants and fungi: their sun gives way to infernal, void or moon away from sunlight. */
        LIVING,
        /** Stone, soil, sand and ice: context only adds. */
        GROUND,
        /** Crystals and dead coral: context only adds. */
        MINERAL,
        /** Animal matter: context only adds. */
        CREATURE
    }

    @Nullable
    private final Identifier analog;
    private final Kind kind;
    /** Size of a context modifier, or 0 for the analog's largest amount. */
    private final int unit;

    NaturalRole(@Nullable String analog, Kind kind) {
        this(analog, kind, 0);
    }

    NaturalRole(@Nullable String analog, Kind kind, int unit) {
        this.analog = analog == null ? null : Identifier.withDefaultNamespace(analog);
        this.kind = kind;
        this.unit = unit;
    }

    /** The vanilla item whose affinity this role borrows, or null for roles derived from another item. */
    @Nullable
    public Identifier analog() {
        return this.analog;
    }

    public Kind kind() {
        return this.kind;
    }

    /** The size of a context modifier, given the largest amount in the analog's affinity. */
    public int unit(int largestAmount) {
        return this.unit > 0 ? this.unit : largestAmount;
    }
}
