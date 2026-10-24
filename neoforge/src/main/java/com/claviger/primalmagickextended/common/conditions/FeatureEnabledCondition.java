package com.claviger.primalmagickextended.common.conditions;

import com.claviger.primalmagickextended.common.features.FeaturePMX;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Load condition, {@code primalmagickextended:feature_enabled}, that holds while a feature is turned on in the common
 * config. Attached at datagen to every research entry, recipe and recipe advancement of a feature, so that NeoForge
 * skips the whole file when the feature is off. In JSON: {@code {"type": "primalmagickextended:feature_enabled",
 * "feature": "portals"}}.
 */
public final class FeatureEnabledCondition implements ICondition {
    public static final MapCodec<FeatureEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FeaturePMX.CODEC.fieldOf("feature").forGetter(FeatureEnabledCondition::feature)
    ).apply(instance, FeatureEnabledCondition::new));

    private final FeaturePMX feature;
    private final Predicate<FeaturePMX> isEnabled;

    public FeatureEnabledCondition(FeaturePMX feature) {
        this(feature, ServicesPMX.CONFIG::isFeatureEnabled);
    }

    /**
     * Creates a condition that asks the given predicate, rather than the config, whether its feature is turned on.
     */
    @VisibleForTesting
    public FeatureEnabledCondition(FeaturePMX feature, Predicate<FeaturePMX> isEnabled) {
        this.feature = feature;
        this.isEnabled = isEnabled;
    }

    public FeaturePMX feature() {
        return this.feature;
    }

    /**
     * Holds if the feature is turned on. The exception is a condition read without any condition context. That only
     * happens in {@code RegistryDataLoader.loadContentsFromNetwork}, when a client decodes, from its own copy of the
     * mod's data, the research entries a server listed for it: it parses with plain {@code RegistryOps}, for which
     * {@link net.neoforged.neoforge.common.conditions.ConditionalOps#retrieveContext()} yields
     * {@link IContext#EMPTY}, whereas server-side data pack loading always supplies a context. The server already
     * left out the entries of its disabled features, so the client keeps every listed entry whatever its own config
     * says; dropping one would leave its registry ids out of step with the server's. The game test
     * {@code plain_registry_ops_have_no_condition_context} fails if NeoForge changes that fallback.
     */
    @Override
    public boolean test(@NotNull IContext context) {
        return context == IContext.EMPTY || this.isEnabled.test(this.feature);
    }

    @Override
    @NotNull
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public String toString() {
        return "feature_enabled(\"" + this.feature.getSerializedName() + "\")";
    }
}
