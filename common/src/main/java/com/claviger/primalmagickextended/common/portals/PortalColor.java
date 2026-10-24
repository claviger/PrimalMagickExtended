package com.claviger.primalmagickextended.common.portals;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.MapColor;

import java.util.Arrays;
import java.util.Optional;

/**
 * The sixteen portal colours. A portal's colour is identified by its block's map colour, which is also what is
 * persisted and synchronized, so every lookup from a stored colour goes through {@link #byMapColorId(int)}.
 * Particle tints are Custom Portals' values; purple portals use the vanilla portal particle instead.
 */
public enum PortalColor implements StringRepresentable {
    WHITE(DyeColor.WHITE, MapColor.SNOW, 1.0F, 1.0F, 1.0F, true),
    ORANGE(DyeColor.ORANGE, MapColor.COLOR_ORANGE, 1.0F, 0.7F, 0.2F, true),
    MAGENTA(DyeColor.MAGENTA, MapColor.COLOR_MAGENTA, 1.0F, 0.4F, 1.0F, true),
    LIGHT_BLUE(DyeColor.LIGHT_BLUE, MapColor.COLOR_LIGHT_BLUE, 0.6F, 0.7F, 1.0F, true),
    YELLOW(DyeColor.YELLOW, MapColor.COLOR_YELLOW, 1.0F, 1.0F, 0.2F, true),
    LIME(DyeColor.LIME, MapColor.COLOR_LIGHT_GREEN, 0.5F, 1.0F, 0.3F, true),
    PINK(DyeColor.PINK, MapColor.COLOR_PINK, 1.0F, 0.6F, 0.9F, true),
    GRAY(DyeColor.GRAY, MapColor.COLOR_GRAY, 0.5F, 0.5F, 0.5F, true),
    LIGHT_GRAY(DyeColor.LIGHT_GRAY, MapColor.COLOR_LIGHT_GRAY, 0.7F, 0.7F, 0.7F, true),
    CYAN(DyeColor.CYAN, MapColor.COLOR_CYAN, 0.2F, 0.65F, 0.8F, true),
    PURPLE(DyeColor.PURPLE, MapColor.COLOR_PURPLE, 0.0F, 0.0F, 0.0F, false),
    BLUE(DyeColor.BLUE, MapColor.COLOR_BLUE, 0.2F, 0.2F, 1.0F, true),
    BROWN(DyeColor.BROWN, MapColor.COLOR_BROWN, 0.575F, 0.45F, 0.325F, true),
    GREEN(DyeColor.GREEN, MapColor.COLOR_GREEN, 0.2F, 0.5F, 0.2F, true),
    RED(DyeColor.RED, MapColor.COLOR_RED, 1.0F, 0.2F, 0.2F, true),
    BLACK(DyeColor.BLACK, MapColor.COLOR_BLACK, 0.0F, 0.0F, 0.0F, false);

    private final DyeColor dye;
    private final MapColor mapColor;
    private final float red;
    private final float green;
    private final float blue;
    private final boolean randomizeBrightness;

    PortalColor(DyeColor dye, MapColor mapColor, float red, float green, float blue, boolean randomizeBrightness) {
        this.dye = dye;
        this.mapColor = mapColor;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.randomizeBrightness = randomizeBrightness;
    }

    /**
     * The vanilla dye item of this colour, such as {@code minecraft:white_dye}. Minecraft 26.1 has no direct lookup
     * from a dye colour to its item, so this resolves the item by id and throws if it is not registered.
     */
    public Item dyeItem() {
        return BuiltInRegistries.ITEM.getValueOrThrow(ResourceKey.create(Registries.ITEM, Identifier.withDefaultNamespace(this.dye.getSerializedName() + "_dye")));
    }

    public MapColor mapColor() { return this.mapColor; }
    public float red() { return this.red; }
    public float green() { return this.green; }
    public float blue() { return this.blue; }
    public boolean randomizeBrightness() { return this.randomizeBrightness; }

    /** Purple portals use the vanilla portal particle rather than a tinted one of their own. */
    public boolean usesVanillaParticle() { return this == PURPLE; }

    /** Black portals do not emit light, matching Custom Portals. */
    public boolean emitsLight() { return this != BLACK; }

    @Override
    public String getSerializedName() { return this.dye.getSerializedName(); }

    public static Optional<PortalColor> byMapColorId(int mapColorId) {
        return Arrays.stream(values()).filter(c -> c.mapColor.id == mapColorId).findFirst();
    }
}
