package com.claviger.primalmagickextended.common.config;

import com.claviger.primalmagickextended.common.features.FeaturePMX;
import com.claviger.primalmagickextended.common.portals.PortalHasteMode;
import com.claviger.primalmagickextended.common.portals.PortalRedstoneMode;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Definition of the mod's common, server and client configuration files. The feature switches live in the
 * install-wide common config, because they decide which research entries and recipes load, and data packs load
 * before any per-world server config exists. The portal gameplay options keep the names, comments, ranges and
 * defaults of Custom Portals; they live in the per-world server config, which is synchronized to clients, while the
 * portal sound options live in the client config. Read values through
 * {@link com.claviger.primalmagickextended.platform.ServicesPMX#CONFIG} rather than from here.
 */
public final class ConfigPMXNeoforge {
    private static final String CATEGORY_FEATURES = "features";
    private static final String CATEGORY_PORTALS = "portals";
    private static final String LANG = "primalmagickextended.configuration.";

    public static ModConfigSpec COMMON_SPEC;
    public static ModConfigSpec SERVER_SPEC;
    public static ModConfigSpec CLIENT_SPEC;

    /** One on/off switch per feature, keyed by the feature. */
    public static final Map<FeaturePMX, ModConfigSpec.BooleanValue> FEATURES = new EnumMap<>(FeaturePMX.class);

    public static ModConfigSpec.BooleanValue UNLIMITED_RANGE;
    public static ModConfigSpec.BooleanValue ALWAYS_INTERDIM;
    public static ModConfigSpec.EnumValue<PortalHasteMode> ALWAYS_HASTE;
    public static ModConfigSpec.BooleanValue PRIVATE_PORTALS;
    public static ModConfigSpec.EnumValue<PortalRedstoneMode> REDSTONE;
    public static ModConfigSpec.IntValue DEFAULT_RANGE;
    public static ModConfigSpec.IntValue RANGE_WITH_ENHANCER;
    public static ModConfigSpec.IntValue RANGE_WITH_STRONG_ENHANCER;
    public static ModConfigSpec.BooleanValue IS_WHITELIST;
    public static ModConfigSpec.ConfigValue<List<? extends String>> FILTERED_BLOCKS;

    public static ModConfigSpec.BooleanValue MUTE_AMBIENT_SOUNDS;
    public static ModConfigSpec.BooleanValue MUTE_TELEPORT_SOUNDS;

    static {
        buildCommonConfigSpec();
        buildServerConfigSpec();
        buildClientConfigSpec();
    }

    private ConfigPMXNeoforge() {}

    private static void buildCommonConfigSpec() {
        // Define the install-wide common config file spec
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Turn whole features of Primal Magick Extended on or off. A disabled feature's Grimoire entries and recipes are not loaded.",
                        "Changes take effect after restarting the game or server.")
                .translation(LANG + CATEGORY_FEATURES)
                .push(CATEGORY_FEATURES);
        defineFeature(builder, FeaturePMX.FAMILIARS, "Familiar Collar ritual and Find Familiar spell.");
        defineFeature(builder, FeaturePMX.PORTALS, "Portal catalysts and portal runes.");
        defineFeature(builder, FeaturePMX.GENERATED_AFFINITIES, "Affinities worked out for natural blocks (such as those of biome mods) that have none.");
        builder.pop();

        COMMON_SPEC = builder.build();
    }

    /**
     * Defines a feature's switch, on by default. It needs a game restart, so the value read when data packs first
     * load stays in force for the whole session and research entries and recipes always agree.
     */
    private static void defineFeature(ModConfigSpec.Builder builder, FeaturePMX feature, String comment) {
        String name = feature.getSerializedName();
        // Keyed below the section, as the bare "portals" key already labels the server config's portal section
        FEATURES.put(feature, builder.comment(comment).translation(LANG + CATEGORY_FEATURES + "." + name).gameRestart().define(name, true));
    }

    private static void buildServerConfigSpec() {
        // Define the per-world server config file spec, which is synchronized to clients
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Portal settings").push(CATEGORY_PORTALS);
        UNLIMITED_RANGE = builder
                .comment("Portals always link regardless of distance.")
                .translation(LANG + "unlimitedRange")
                .define("unlimitedRange", false);
        ALWAYS_INTERDIM = builder
                .comment("Portals always link across dimensions, even without a Gate Rune.")
                .translation(LANG + "alwaysInterdim")
                .define("alwaysInterdim", false);
        ALWAYS_HASTE = builder
                .comment("Whether portals always teleport instantly: YES, NO, or CREATIVE (creative mode only).")
                .translation(LANG + "alwaysHaste")
                .defineEnum("alwaysHaste", PortalHasteMode.CREATIVE);
        PRIVATE_PORTALS = builder
                .comment("Only allows portals to link if they were each constructed by the same player. Useful for servers.")
                .translation(LANG + "privatePortals")
                .define("privatePortals", false);
        REDSTONE = builder
                .comment("What a redstone signal does to a portal: OFF turns portals off, ON turns portals on, NONE has no effect.",
                        "Note: Setting this to ON means your portals will be off without redstone power!")
                .translation(LANG + "redstone")
                .defineEnum("redstone", PortalRedstoneMode.OFF);
        DEFAULT_RANGE = builder
                .comment("Default portal range, in blocks.")
                .translation(LANG + "defaultRange")
                .defineInRange("defaultRange", 100, 0, Integer.MAX_VALUE);
        RANGE_WITH_ENHANCER = builder
                .comment("Portal range with an Enhancer Rune, in blocks.")
                .translation(LANG + "rangeWithEnhancer")
                .defineInRange("rangeWithEnhancer", 1000, 0, Integer.MAX_VALUE);
        RANGE_WITH_STRONG_ENHANCER = builder
                .comment("Portal range with a Strong Enhancer Rune, in blocks.")
                .translation(LANG + "rangeWithStrongEnhancer")
                .defineInRange("rangeWithStrongEnhancer", 10000, 0, Integer.MAX_VALUE);
        IS_WHITELIST = builder
                .comment("Whether filteredBlocks is a whitelist (true) or a blacklist (false).")
                .translation(LANG + "isWhitelist")
                .define("isWhitelist", false);
        FILTERED_BLOCKS = builder
                .comment("Allows customizing which blocks may be used as portal frames. Takes the block's id, e.g. \"minecraft:stone\" for stone block.")
                .translation(LANG + "filteredBlocks")
                .defineListAllowEmpty("filteredBlocks", List.of(), () -> "", obj -> obj instanceof String);
        builder.pop();

        SERVER_SPEC = builder.build();
    }

    private static void buildClientConfigSpec() {
        // Define the client-only config file spec
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Portal settings").push(CATEGORY_PORTALS);
        MUTE_AMBIENT_SOUNDS = builder
                .comment("Mute the ambient portal sound effects.")
                .translation(LANG + "muteAmbientSounds")
                .define("muteAmbientSounds", false);
        MUTE_TELEPORT_SOUNDS = builder
                .comment("Mute the player teleport sound effects.")
                .translation(LANG + "muteTeleportSounds")
                .define("muteTeleportSounds", false);
        builder.pop();

        CLIENT_SPEC = builder.build();
    }
}
