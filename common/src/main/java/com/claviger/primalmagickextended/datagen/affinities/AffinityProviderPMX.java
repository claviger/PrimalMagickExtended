package com.claviger.primalmagickextended.datagen.affinities;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.verdantartifice.primalmagick.common.sources.Sources;
import com.verdantartifice.primalmagick.datagen.affinities.AffinityProvider;
import com.verdantartifice.primalmagick.datagen.affinities.IFinishedAffinity;
import com.verdantartifice.primalmagick.datagen.affinities.ItemAffinityBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Affinity data provider for the mod's items. Extends Primal Magick's provider, which writes each
 * affinity under its target's namespace, but registers only this mod's affinities.
 */
public class AffinityProviderPMX extends AffinityProvider {
    public AffinityProviderPMX(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProviderFuture) {
        super(packOutput, lookupProviderFuture);
    }

    @Override
    protected void registerAffinities(HolderLookup.Provider lookupProvider, Consumer<IFinishedAffinity> consumer) {
        ItemAffinityBuilder.itemAffinity(ItemsPMX.FAMILIAR_COLLAR.get())
                .set(Sources.BLOOD, 4).set(Sources.INFERNAL, 4).set(Sources.EARTH, 1).set(Sources.SKY, 1).set(Sources.SEA, 1)
                .build(consumer);
    }

    @Override
    @NotNull
    public String getName() {
        return ConstantsPMX.MOD_NAME + " Affinities";
    }
}
