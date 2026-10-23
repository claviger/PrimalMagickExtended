package com.claviger.primalmagickextended;

import com.claviger.primalmagickextended.common.init.InitRegistriesPMXNeoforge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

/**
 * Main class of the Primal Magick Extended mod. Most initialization happens in response to
 * NeoForge events, as in Primal Magick.
 */
@Mod(ConstantsPMX.MOD_ID)
public class PrimalMagickExtended {
    private static IEventBus eventBus;

    public PrimalMagickExtended(@NotNull IEventBus eventBus, @NotNull ModContainer container) {
        PrimalMagickExtended.eventBus = eventBus;
        InitRegistriesPMXNeoforge.initDeferredRegistries();
    }

    @NotNull
    public static IEventBus getEventBus() {
        return PrimalMagickExtended.eventBus;
    }
}
