package com.claviger.primalmagickextended.common.affinities;

import com.claviger.primalmagickextended.common.affinities.WorldgenSurvey.BlockEvidence;
import com.claviger.primalmagickextended.common.affinities.WorldgenSurvey.Use;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.common.sources.Sources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.CoralBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.HangingRootsBlock;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.KelpPlantBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.MudBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.FungusBlock;
import net.minecraft.world.level.block.NyliumBlock;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * The rules that turn what is known about a block into an affinity: first what the block is (its role, which picks a
 * vanilla analog), then where it generates (context modifiers on top of the analog).
 */
public final class AffinityRules {
    private AffinityRules() {}

    /** How sure a classification is. Name-only guesses need worldgen evidence to be used. */
    public enum Confidence { TAG, CLASS, WORLDGEN, NAME, FALLBACK }

    public record Classification(NaturalRole role, Confidence confidence, String reason) {}

    /** One change to the analog's affinity, with the reason for it. */
    public record Modifier(Source source, int amount, boolean remove, String reason) {
        public String describe() {
            return (this.remove ? "-" : "+") + this.amount + " " + this.source.getId().getPath() + " (" + this.reason + ")";
        }
    }

    /** Where a block generates, as far as the modifiers go. */
    public record Context(BiomeProfile profile, boolean water, boolean underground) {
        public BiomeProfile.Dimension dimension() {
            return this.profile.dimension();
        }
    }

    // ---------------------------------------------------------------- Classification

    private static TagKey<Block> tag(String id) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.parse(id));
    }

    private static final TagKey<Block> LOGS = tag("minecraft:logs");
    private static final TagKey<Block> LEAVES = tag("minecraft:leaves");
    private static final TagKey<Block> SAPLINGS = tag("minecraft:saplings");
    private static final TagKey<Block> SMALL_FLOWERS = tag("minecraft:small_flowers");
    private static final TagKey<Block> FLOWERS = tag("minecraft:flowers");
    private static final TagKey<Block> CORALS = tag("minecraft:corals");
    private static final TagKey<Block> CORAL_BLOCKS = tag("minecraft:coral_blocks");
    private static final TagKey<Block> SAND = tag("minecraft:sand");
    private static final TagKey<Block> DIRT = tag("minecraft:dirt");
    private static final TagKey<Block> GRASS_BLOCKS = tag("minecraft:grass_blocks");
    private static final TagKey<Block> MOSS_BLOCKS = tag("minecraft:moss_blocks");
    private static final TagKey<Block> NYLIUM = tag("minecraft:nylium");
    private static final TagKey<Block> ICE = tag("minecraft:ice");
    private static final TagKey<Block> SNOW = tag("minecraft:snow");
    private static final TagKey<Block> ORES = tag("c:ores");
    private static final TagKey<Block> CLIMBABLE = tag("minecraft:climbable");
    private static final TagKey<Block> CAVE_VINES = tag("minecraft:cave_vines");
    private static final TagKey<Block> MINEABLE_SHOVEL = tag("minecraft:mineable/shovel");
    private static final TagKey<Block> MINEABLE_PICKAXE = tag("minecraft:mineable/pickaxe");
    private static final List<TagKey<Block>> STONES = List.of(tag("minecraft:base_stone_overworld"), tag("minecraft:base_stone_nether"),
            tag("minecraft:stone_ore_replaceables"), tag("minecraft:deepslate_ore_replaceables"), tag("c:stones"));

    /** Name tokens, checked in order, for blocks that neither tags nor classes explain. */
    private static final List<NameRule> NAME_RULES = List.of(
            new NameRule(NaturalRole.FLESH, "flesh", "gore", "pus", "meat", "hair", "eyeball"),
            new NameRule(NaturalRole.COBWEB, "web", "cobweb", "webbing", "silk"),
            new NameRule(NaturalRole.LEAVES, "leaves", "foliage"),
            new NameRule(NaturalRole.SAPLING, "sapling", "propagule"),
            new NameRule(NaturalRole.EGG, "egg", "eggs"),
            new NameRule(NaturalRole.TWIG, "branch", "branches", "twig", "twigs"),
            new NameRule(NaturalRole.LOG, "log", "wood", "stem", "trunk"),
            new NameRule(NaturalRole.MUSHROOM, "mushroom", "shroom", "toadstool", "fungus", "glowshroom"),
            new NameRule(NaturalRole.MOSS, "moss", "lichen"),
            new NameRule(NaturalRole.VINE, "vine", "vines", "ivy", "tendril", "tendrils", "tendon", "tendons"),
            new NameRule(NaturalRole.CACTUS, "cactus"),
            new NameRule(NaturalRole.BAMBOO, "bamboo"),
            new NameRule(NaturalRole.REED, "reed", "reeds", "cattail", "rush", "papyrus"),
            new NameRule(NaturalRole.KELP, "kelp", "seaweed", "algae"),
            new NameRule(NaturalRole.SEAGRASS, "barnacle", "barnacles", "anemone", "watergrass"),
            new NameRule(NaturalRole.CORAL, "coral"),
            new NameRule(NaturalRole.FRUIT, "pumpkin", "melon", "gourd"),
            new NameRule(NaturalRole.GROUND_COVER, "petal", "petals", "litter", "clover"),
            new NameRule(NaturalRole.FLOWER, "flower", "rose", "lily", "daisy", "tulip", "orchid", "iris", "lavender", "violet",
                    "marigold", "hibiscus", "daffodil", "poppy", "bloom", "blossom", "cosmos", "goldenrod", "bluebell", "aster"),
            new NameRule(NaturalRole.GRASS, "grass", "fern", "sprout", "shrub", "bush", "weed", "sedge", "oats", "barley", "bramble", "scrub"),
            new NameRule(NaturalRole.ORE, "ore"),
            new NameRule(NaturalRole.CRYSTAL, "crystal", "quartz", "gem", "geode"),
            new NameRule(NaturalRole.ICE, "ice", "frost", "permafrost"),
            new NameRule(NaturalRole.SNOW, "snow"),
            new NameRule(NaturalRole.MUD, "mud", "silt", "sludge"),
            new NameRule(NaturalRole.SAND, "sand"),
            new NameRule(NaturalRole.GRAVEL, "gravel"),
            new NameRule(NaturalRole.DIRT, "dirt", "soil", "loam", "peat"),
            new NameRule(NaturalRole.STONE, "stone", "rock", "slate", "basalt", "calcite", "marble", "granite", "brimstone", "shale"));

    private record NameRule(NaturalRole role, Set<String> tokens) {
        NameRule(NaturalRole role, String... tokens) {
            this(role, Set.of(tokens));
        }
    }

    /**
     * Works out what a block is: block tags first (mods tag their blocks for compatibility), then block classes, then
     * what worldgen uses it as, then its name, and last where worldgen puts it.
     */
    @Nullable
    public static Classification classify(Block block, Item item, @Nullable BlockEvidence evidence) {
        BlockState state = block.defaultBlockState();
        String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
        boolean full = state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        boolean waterPlant = state.getFluidState().is(FluidTags.WATER);
        boolean dead = tokens(path).contains("dead");

        // Tags
        if (state.is(LOGS)) {
            return new Classification(NaturalRole.LOG, Confidence.TAG, "tag #minecraft:logs");
        } else if (state.is(LEAVES)) {
            return new Classification(NaturalRole.LEAVES, Confidence.TAG, "tag #minecraft:leaves");
        } else if (state.is(SAPLINGS)) {
            return new Classification(NaturalRole.SAPLING, Confidence.TAG, "tag #minecraft:saplings");
        } else if (state.is(CORAL_BLOCKS)) {
            return new Classification(dead ? NaturalRole.DEAD_CORAL_BLOCK : NaturalRole.CORAL_BLOCK, Confidence.TAG, "tag #minecraft:coral_blocks");
        } else if (state.is(CORALS)) {
            return new Classification(dead ? NaturalRole.DEAD_CORAL : NaturalRole.CORAL, Confidence.TAG, "tag #minecraft:corals");
        } else if (state.is(SMALL_FLOWERS)) {
            return new Classification(NaturalRole.FLOWER, Confidence.TAG, "tag #minecraft:small_flowers");
        } else if (state.is(FLOWERS)) {
            boolean tall = block instanceof DoublePlantBlock;
            NaturalRole role = block instanceof PinkPetalsBlock ? NaturalRole.GROUND_COVER : tall ? NaturalRole.TALL_FLOWER : full ? NaturalRole.LEAVES : NaturalRole.FLOWER;
            return new Classification(role, Confidence.TAG,
                    "tag #minecraft:flowers" + (block instanceof PinkPetalsBlock ? ", flower bed" : tall ? ", double plant" : full ? ", full block" : ""));
        } else if (state.is(GRASS_BLOCKS) || state.is(NYLIUM)) {
            return new Classification(NaturalRole.GRASS_BLOCK, Confidence.TAG, state.is(NYLIUM) ? "tag #minecraft:nylium" : "tag #minecraft:grass_blocks");
        } else if (state.is(SAND)) {
            return new Classification(NaturalRole.SAND, Confidence.TAG, "tag #minecraft:sand");
        } else if (state.is(ORES)) {
            return new Classification(NaturalRole.ORE, Confidence.TAG, "tag #c:ores");
        } else if (state.is(ICE)) {
            return new Classification(NaturalRole.ICE, Confidence.TAG, "tag #minecraft:ice");
        } else if (state.is(SNOW)) {
            return new Classification(NaturalRole.SNOW, Confidence.TAG, "tag #minecraft:snow");
        } else if (state.is(MOSS_BLOCKS) && full) {
            return new Classification(NaturalRole.MOSS, Confidence.TAG, "tag #minecraft:moss_blocks");
        }

        // Classes. A generic plant class says little, so a more specific name wins over it.
        Set<String> tokens = tokens(path);
        boolean tall = block instanceof DoublePlantBlock || block instanceof GrowingPlantBlock;
        Classification byClass = classifyByClass(block, item, state, full, waterPlant, dead);
        if (byClass != null) {
            return GENERIC_PLANT_ROLES.contains(byClass.role()) && !(block instanceof VineBlock) ? refineByName(byClass, tokens, full, waterPlant, tall) : byClass;
        }

        // Tags that only make sense after the classes have had their say
        if (state.is(DIRT)) {
            return new Classification(NaturalRole.DIRT, Confidence.TAG, "tag #minecraft:dirt");
        } else if ((state.is(CLIMBABLE) || state.is(CAVE_VINES)) && !full) {
            return refineByName(new Classification(NaturalRole.VINE, Confidence.TAG, "tag #minecraft:climbable, not a full block"), tokens, full, waterPlant, tall);
        }
        for (TagKey<Block> stone : STONES) {
            if (state.is(stone)) {
                return new Classification(NaturalRole.STONE, Confidence.TAG, "tag #" + stone.location());
            }
        }

        // What worldgen builds with it
        Use use = evidence == null ? null : evidence.mainUse();
        if (use == Use.WOOD) {
            return new Classification(NaturalRole.LOG, Confidence.WORLDGEN, "placed as a tree trunk or roots");
        } else if (use == Use.FOLIAGE) {
            return new Classification(NaturalRole.LEAVES, Confidence.WORLDGEN, "placed as tree foliage");
        }

        // Name
        NameRule byName = nameRule(tokens, null);
        if (byName != null) {
            String token = byName.tokens().stream().filter(tokens::contains).findFirst().orElse("");
            return new Classification(adjustForShape(byName.role(), full, waterPlant, tall), Confidence.NAME, "name contains \"" + token + "\"");
        }
        if (use == Use.HANGING && !full) {
            return new Classification(NaturalRole.VINE, Confidence.WORLDGEN, "placed hanging from trees");
        } else if (use == Use.FRUIT) {
            return new Classification(NaturalRole.FRUIT, Confidence.WORLDGEN, "placed as fruit on tree trunks");
        }

        // Where worldgen puts it
        if (use != null) {
            if (use.isGround() || (use == Use.OTHER && full)) {
                boolean soft = state.is(MINEABLE_SHOVEL) && !state.is(MINEABLE_PICKAXE);
                return new Classification(soft ? NaturalRole.DIRT : NaturalRole.STONE, Confidence.FALLBACK,
                        "placed in the ground (" + use.name().toLowerCase(Locale.ROOT) + "), " + (soft ? "shovel" : "not shovel") + "-mineable");
            } else if (use != Use.FLUID) {
                return new Classification(full ? NaturalRole.LEAVES : NaturalRole.GRASS, Confidence.FALLBACK,
                        "placed on the surface (" + use.name().toLowerCase(Locale.ROOT) + ")" + (full ? ", full block" : ""));
            }
        }
        return null;
    }

    @Nullable
    private static Classification classifyByClass(Block block, Item item, BlockState state, boolean full, boolean waterPlant, boolean dead) {
        if (block instanceof LeavesBlock) {
            return byClass(NaturalRole.LEAVES, block);
        } else if (block instanceof SaplingBlock) {
            return byClass(NaturalRole.SAPLING, block);
        } else if (block instanceof BaseCoralPlantTypeBlock) {
            return byClass(dead ? NaturalRole.DEAD_CORAL : NaturalRole.CORAL, block);
        } else if (block instanceof CoralBlock) {
            return byClass(NaturalRole.CORAL_BLOCK, block);
        } else if (block instanceof KelpBlock || block instanceof KelpPlantBlock) {
            return byClass(NaturalRole.KELP, block);
        } else if (waterPlant && !full) {
            boolean tall = block instanceof DoublePlantBlock || block instanceof GrowingPlantBlock;
            return new Classification(tall ? NaturalRole.KELP : NaturalRole.SEAGRASS, Confidence.CLASS,
                    "always holds water" + (tall ? ", tall" : "") + " (" + block.getClass().getSimpleName() + ")");
        } else if (block instanceof WaterlilyBlock || item instanceof PlaceOnWaterBlockItem) {
            return new Classification(NaturalRole.LILY_PAD, Confidence.CLASS, "placed on water (" + block.getClass().getSimpleName() + ")");
        } else if (block instanceof CactusBlock) {
            return byClass(NaturalRole.CACTUS, block);
        } else if (block instanceof BambooStalkBlock || block instanceof BambooSaplingBlock) {
            return byClass(NaturalRole.BAMBOO, block);
        } else if (block instanceof SugarCaneBlock) {
            return byClass(NaturalRole.REED, block);
        } else if (block instanceof HugeMushroomBlock) {
            return byClass(NaturalRole.MUSHROOM_BLOCK, block);
        } else if (block instanceof MushroomBlock || block instanceof FungusBlock) {
            return byClass(NaturalRole.MUSHROOM, block);
        } else if (block instanceof PinkPetalsBlock) {
            return byClass(NaturalRole.GROUND_COVER, block);
        } else if (block instanceof TallFlowerBlock) {
            return byClass(NaturalRole.TALL_FLOWER, block);
        } else if (block instanceof FlowerBlock) {
            return byClass(NaturalRole.FLOWER, block);
        } else if (block instanceof HangingRootsBlock) {
            return byClass(NaturalRole.HANGING_ROOTS, block);
        } else if (block instanceof VineBlock || block instanceof MultifaceBlock || block instanceof GrowingPlantBlock || block instanceof CaveVines) {
            return byClass(NaturalRole.VINE, block);
        } else if (block instanceof AmethystClusterBlock) {
            Set<String> tokens = tokens(BuiltInRegistries.BLOCK.getKey(block).getPath());
            NaturalRole role = !tokens.contains("bud") ? NaturalRole.CRYSTAL : tokens.contains("small") ? NaturalRole.SMALL_BUD
                    : tokens.contains("medium") ? NaturalRole.MEDIUM_BUD : NaturalRole.LARGE_BUD;
            return byClass(role, block);
        } else if (block instanceof WebBlock) {
            return byClass(NaturalRole.COBWEB, block);
        } else if (block instanceof IceBlock) {
            return byClass(NaturalRole.ICE, block);
        } else if (block instanceof SnowLayerBlock || block instanceof PowderSnowBlock) {
            return byClass(NaturalRole.SNOW, block);
        } else if (block instanceof MudBlock || state.getSoundType() == SoundType.MUD) {
            return byClass(NaturalRole.MUD, block);
        } else if (block instanceof SpreadingSnowyDirtBlock || block instanceof NyliumBlock) {
            return byClass(NaturalRole.GRASS_BLOCK, block);
        } else if (block instanceof FallingBlock && state.getSoundType() == SoundType.SAND) {
            return byClass(NaturalRole.SAND, block);
        } else if (block instanceof FallingBlock && state.getSoundType() == SoundType.GRAVEL) {
            return byClass(NaturalRole.GRAVEL, block);
        } else if (block instanceof DoublePlantBlock) {
            return byClass(NaturalRole.TALL_GRASS, block);
        } else if (block instanceof BushBlock && !full) {
            return byClass(NaturalRole.GRASS, block);
        }
        return null;
    }

    private static Classification byClass(NaturalRole role, Block block) {
        return new Classification(role, Confidence.CLASS, "class " + block.getClass().getSimpleName());
    }

    /** Fits a role guessed from a name to the block's shape, e.g. a full block of moss rather than a moss carpet. */
    private static NaturalRole adjustForShape(NaturalRole role, boolean full, boolean waterPlant, boolean tall) {
        if (waterPlant && (role == NaturalRole.GRASS || role == NaturalRole.FLOWER)) {
            return tall ? NaturalRole.KELP : NaturalRole.SEAGRASS;
        }
        return switch (role) {
            case MUSHROOM -> full ? NaturalRole.MUSHROOM_BLOCK : NaturalRole.MUSHROOM;
            case MOSS -> full ? NaturalRole.MOSS : NaturalRole.GROUND_COVER;
            case FLOWER -> full ? NaturalRole.LEAVES : tall ? NaturalRole.TALL_FLOWER : NaturalRole.FLOWER;
            case GROUND_COVER -> full ? NaturalRole.LEAVES : role;
            case GRASS -> full ? NaturalRole.LEAVES : tall ? NaturalRole.TALL_GRASS : NaturalRole.GRASS;
            default -> role;
        };
    }

    /** Roles given by plant classes and tags so general that the block's name may say more. */
    private static final Set<NaturalRole> GENERIC_PLANT_ROLES = Set.of(NaturalRole.VINE, NaturalRole.GRASS, NaturalRole.TALL_GRASS);

    /** Roles a name may give a block that a generic plant class or tag describes. */
    private static final Set<NaturalRole> REFINING_ROLES = Set.of(NaturalRole.FLESH, NaturalRole.COBWEB, NaturalRole.EGG, NaturalRole.TWIG,
            NaturalRole.MUSHROOM, NaturalRole.CACTUS, NaturalRole.BAMBOO, NaturalRole.REED, NaturalRole.KELP, NaturalRole.SEAGRASS,
            NaturalRole.CORAL, NaturalRole.FRUIT, NaturalRole.GROUND_COVER, NaturalRole.FLOWER, NaturalRole.GRASS, NaturalRole.CRYSTAL);

    /** Replaces a generic plant classification with a more specific one from the block's name, if it has one. */
    private static Classification refineByName(Classification generic, Set<String> tokens, boolean full, boolean waterPlant, boolean tall) {
        NameRule rule = nameRule(tokens, REFINING_ROLES);
        if (rule == null) {
            return generic;
        }
        NaturalRole role = adjustForShape(rule.role(), full, waterPlant, tall);
        if (role == generic.role()) {
            return generic;
        }
        String token = rule.tokens().stream().filter(tokens::contains).findFirst().orElse("");
        return new Classification(role, Confidence.NAME, generic.reason() + ", but name contains \"" + token + "\"");
    }

    /** The first name rule matching one of the tokens, among the allowed roles (or all roles if null). */
    @Nullable
    private static NameRule nameRule(Set<String> tokens, @Nullable Set<NaturalRole> allowed) {
        for (NameRule rule : NAME_RULES) {
            if ((allowed == null || allowed.contains(rule.role())) && tokens.stream().anyMatch(rule.tokens()::contains)) {
                return rule;
            }
        }
        return null;
    }

    /** The parts of an id path, split on underscores and slashes. */
    public static Set<String> tokens(String path) {
        return new HashSet<>(Arrays.asList(path.toLowerCase(Locale.ROOT).split("[_/]")));
    }

    // ---------------------------------------------------------------- Context

    /**
     * The changes that a block's context makes to its analog's affinity. Every addition raises a source to the
     * modifier unit (the analog's largest amount, unless the role says otherwise), so a source the analog already has
     * in full is not added twice. This reproduces Primal Magick's own variants: the crimson stem is the oak log less
     * its sun plus infernal and moon, seagrass is short grass plus sea.
     */
    public static List<Modifier> modifiers(NaturalRole role, SourceList analog, Context context) {
        int largest = analog.getSources().stream().mapToInt(analog::getAmount).max().orElse(0);
        int unit = role.unit(largest);
        List<Modifier> retVal = new ArrayList<>();
        if (unit <= 0) {
            return retVal;
        }
        Working working = new Working(analog, unit, retVal);
        boolean living = role.kind() == NaturalRole.Kind.LIVING;
        BiomeProfile profile = context.profile();
        switch (context.dimension()) {
            case NETHER -> {
                if (living) {
                    working.removeAll(Sources.SUN, "the Nether has no sun");
                    working.raise(Sources.MOON, "the Nether has no sun");
                }
                working.raise(Sources.INFERNAL, "generates in the Nether");
            }
            case END -> {
                if (living) {
                    working.removeAll(Sources.SUN, "the End has no sun");
                    working.raise(Sources.MOON, "the End has no sun");
                }
                working.raise(Sources.VOID, "generates in the End");
            }
            default -> {
                if (living && context.underground()) {
                    working.removeAll(Sources.SUN, "grows underground, out of sunlight");
                    working.raise(Sources.MOON, "grows underground, out of sunlight");
                }
            }
        }
        if (context.water()) {
            working.raise(Sources.SEA, "generates in or by water");
        }
        if (context.dimension() == BiomeProfile.Dimension.OVERWORLD) {
            for (BiomeProfile.Group group : BiomeProfile.Group.values()) {
                Source source = group.source();
                if (source == null || !profile.has(group)) {
                    continue;
                }
                if (source == Sources.SUN && context.underground()) {
                    continue;
                }
                working.raise(source, group.name().toLowerCase(Locale.ROOT) + " biomes " + Math.round(profile.share(group) * 100) + "%");
            }
        }
        return retVal;
    }

    /** The analog's affinity as modifiers are applied, so each source is raised to the unit at most once. */
    private static final class Working {
        private SourceList current;
        private final int unit;
        private final List<Modifier> modifiers;

        Working(SourceList analog, int unit, List<Modifier> modifiers) {
            this.current = analog.copy();
            this.unit = unit;
            this.modifiers = modifiers;
        }

        void raise(Source source, String reason) {
            int amount = this.unit - this.current.getAmount(source);
            if (amount > 0) {
                this.current = this.current.add(source, amount);
                this.modifiers.add(new Modifier(source, amount, false, reason));
            }
        }

        void removeAll(Source source, String reason) {
            int amount = this.current.getAmount(source);
            if (amount > 0) {
                this.current = this.current.remove(source);
                this.modifiers.add(new Modifier(source, amount, true, reason));
            }
        }
    }
}
