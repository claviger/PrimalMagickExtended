package com.claviger.primalmagickextended.common.util;

import com.claviger.primalmagickextended.ConstantsPMX;
import net.minecraft.resources.Identifier;

/**
 * Helper for building identifiers in this mod's namespace.
 */
public class ResourceUtilsPMX {
    public static Identifier loc(String name) {
        return Identifier.fromNamespaceAndPath(ConstantsPMX.MOD_ID, name);
    }
}
