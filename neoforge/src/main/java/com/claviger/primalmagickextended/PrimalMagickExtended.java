package com.claviger.primalmagickextended;

import com.claviger.primalmagickextended.common.config.ConfigPMXNeoforge;
import com.claviger.primalmagickextended.common.init.InitRegistriesPMXNeoforge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
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
        container.registerConfig(ModConfig.Type.COMMON, ConfigPMXNeoforge.COMMON_SPEC);
        container.registerConfig(ModConfig.Type.SERVER, ConfigPMXNeoforge.SERVER_SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, ConfigPMXNeoforge.CLIENT_SPEC);
    }

    @NotNull
    public static IEventBus getEventBus() {
        return PrimalMagickExtended.eventBus;
    }
}
