package com.claviger.primalmagickextended.common.features;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * A whole feature of the mod that the common config can switch off. A disabled feature's research entries and
 * recipes are not loaded, so they never appear in the Grimoire or in any recipe book; its blocks, items and
 * entities stay registered.
 */
public enum FeaturePMX implements StringRepresentable {
    FAMILIARS("familiars"),
    PORTALS("portals");

    public static final Codec<FeaturePMX> CODEC = StringRepresentable.fromEnum(FeaturePMX::values);

    private final String name;

    FeaturePMX(String name) {
        this.name = name;
    }

    /** The feature's name, used as its config option key and in its load condition. */
    @Override
    @NotNull
    public String getSerializedName() {
        return this.name;
    }
}
