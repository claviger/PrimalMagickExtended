package com.claviger.primalmagickextended.platform;

import com.claviger.primalmagickextended.common.features.FeaturePMX;
import com.claviger.primalmagickextended.common.portals.PortalHasteMode;
import com.claviger.primalmagickextended.common.portals.PortalRedstoneMode;
import com.claviger.primalmagickextended.common.config.ConfigPMXNeoforge;
import com.claviger.primalmagickextended.platform.services.IConfigServicePMX;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * NeoForge implementation of the config service, backed by the specs in {@link ConfigPMXNeoforge}. Values read
 * before their config file is loaded (during datagen, or the server config at the main menu) fall back to their
 * defaults.
 */
public class ConfigServicePMXNeoforge implements IConfigServicePMX {
    @Override
    public boolean isFeatureEnabled(FeaturePMX feature) {
        return read(ConfigPMXNeoforge.COMMON_SPEC, ConfigPMXNeoforge.FEATURES.get(feature));
    }

    @Override
    public boolean portalsUnlimitedRange() {
        return read(ConfigPMXNeoforge.SERVER_SPEC, ConfigPMXNeoforge.UNLIMITED_RANGE);
    }

    @Override
    public boolean portalsAlwaysInterdimensional() {
        return read(ConfigPMXNeoforge.SERVER_SPEC, ConfigPMXNeoforge.ALWAYS_INTERDIM);
    }

    @Override
    public PortalHasteMode portalsAlwaysHaste() {
        return read(ConfigPMXNeoforge.SERVER_SPEC, ConfigPMXNeoforge.ALWAYS_HASTE);
    }

    @Override
    public boolean portalsPrivate() {
        return read(ConfigPMXNeoforge.SERVER_SPEC, ConfigPMXNeoforge.PRIVATE_PORTALS);
    }

    @Override
    public PortalRedstoneMode portalsRedstone() {
        return read(ConfigPMXNeoforge.SERVER_SPEC, ConfigPMXNeoforge.REDSTONE);
    }

    @Override
    public int portalsDefaultRange() {
        return read(ConfigPMXNeoforge.SERVER_SPEC, ConfigPMXNeoforge.DEFAULT_RANGE);
    }

    @Override
    public int portalsRangeWithEnhancer() {
        return read(ConfigPMXNeoforge.SERVER_SPEC, ConfigPMXNeoforge.RANGE_WITH_ENHANCER);
    }

    @Override
    public int portalsRangeWithStrongEnhancer() {
        return read(ConfigPMXNeoforge.SERVER_SPEC, ConfigPMXNeoforge.RANGE_WITH_STRONG_ENHANCER);
    }

    @Override
    public boolean portalsFrameFilterIsWhitelist() {
        return read(ConfigPMXNeoforge.SERVER_SPEC, ConfigPMXNeoforge.IS_WHITELIST);
    }

    @Override
    public List<? extends String> portalsFilteredFrameBlocks() {
        return read(ConfigPMXNeoforge.SERVER_SPEC, ConfigPMXNeoforge.FILTERED_BLOCKS);
    }

    @Override
    public boolean portalsMuteAmbientSounds() {
        return read(ConfigPMXNeoforge.CLIENT_SPEC, ConfigPMXNeoforge.MUTE_AMBIENT_SOUNDS);
    }

    @Override
    public boolean portalsMuteTeleportSounds() {
        return read(ConfigPMXNeoforge.CLIENT_SPEC, ConfigPMXNeoforge.MUTE_TELEPORT_SOUNDS);
    }

    /**
     * Reads a config value, or its default if the spec that owns it is not loaded.
     */
    private static <T> T read(ModConfigSpec spec, ModConfigSpec.ConfigValue<T> value) {
        if (!spec.isLoaded()) {
            return value.getDefault();
        }
        try {
            return value.get();
        } catch (IllegalStateException e) {
            // The server config can be unloaded on another thread between the check above and the read, e.g. while
            // a world is shutting down, in which case get() throws
            return value.getDefault();
        }
    }
}
