package com.claviger.primalmagickextended.common.util;

import com.claviger.primalmagickextended.ConstantsPMX;
import net.minecraft.resources.ResourceLocation;

/**
 * Helper for building resource locations in this mod's namespace.
 */
public class ResourceUtilsPMX {
    public static ResourceLocation loc(String name) {
        return ResourceLocation.fromNamespaceAndPath(ConstantsPMX.MOD_ID, name);
    }
}
