package com.claviger.primalmagickextended.mixin;

import com.claviger.primalmagickextended.common.affinities.GeneratedAffinities;
import com.google.gson.JsonElement;
import com.verdantartifice.primalmagick.common.affinities.AffinityManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Records the affinities Primal Magick loads from data packs. Primal Magick 6.0.7 adds the affinities its recipe scan
 * works out to the same table, so Generated Affinities takes the data pack ones from here rather than from the table.
 */
@Mixin(AffinityManager.class)
public abstract class AffinityManagerMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("TAIL"), remap = false)
    private void primalmagickextended$recordDataPackAffinities(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager,
                                                              ProfilerFiller profiler, CallbackInfo ci) {
        GeneratedAffinities.onDataPackAffinitiesLoaded(((AffinityManager) (Object) this).getAllAffinities());
    }
}
