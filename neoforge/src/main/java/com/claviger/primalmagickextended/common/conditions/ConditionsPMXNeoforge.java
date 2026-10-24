package com.claviger.primalmagickextended.common.conditions;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.PrimalMagickExtended;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Registrations of the mod's data load condition codecs.
 */
public class ConditionsPMXNeoforge {
    private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, ConstantsPMX.MOD_ID);

    public static void init() {
        CONDITION_CODECS.register(PrimalMagickExtended.getEventBus());
    }

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<FeatureEnabledCondition>> FEATURE_ENABLED = CONDITION_CODECS.register("feature_enabled", () -> FeatureEnabledCondition.CODEC);
}
