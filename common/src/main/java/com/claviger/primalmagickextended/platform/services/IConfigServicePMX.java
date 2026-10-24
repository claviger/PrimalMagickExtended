package com.claviger.primalmagickextended.platform.services;

import com.claviger.primalmagickextended.common.features.FeaturePMX;
import com.claviger.primalmagickextended.common.portals.PortalHasteMode;
import com.claviger.primalmagickextended.common.portals.PortalRedstoneMode;

import java.util.List;

/**
 * Access to this mod's configuration. Feature switches are install-wide common settings; gameplay options are
 * per-world server settings synchronized to clients; sound options are client settings. Every accessor returns the
 * option's default until its config is loaded, so it is safe to call during datagen and at the main menu.
 */
public interface IConfigServicePMX {
    /**
     * Whether a whole feature is turned on. Install-wide common setting, read when data packs load, so a change
     * takes effect after a restart. Defaults to true.
     */
    boolean isFeatureEnabled(FeaturePMX feature);

    /** Whether portals link regardless of distance. Per-world server setting, synced to clients. */
    boolean portalsUnlimitedRange();

    /** Whether portals link across dimensions even without a Gate Rune. Per-world server setting, synced to clients. */
    boolean portalsAlwaysInterdimensional();

    /** The mode deciding when portals teleport instantly without a Haste Rune. Per-world server setting, synced to clients. */
    PortalHasteMode portalsAlwaysHaste();

    /** Whether portals only link to portals built by the same player. Per-world server setting, synced to clients. */
    boolean portalsPrivate();

    /** The mode deciding what a redstone signal does to a portal. Per-world server setting, synced to clients. */
    PortalRedstoneMode portalsRedstone();

    /** Portal link range without enhancer runes, in blocks. Per-world server setting, synced to clients. */
    int portalsDefaultRange();

    /** Portal link range with a Portal Enhancer Rune, in blocks. Per-world server setting, synced to clients. */
    int portalsRangeWithEnhancer();

    /** Portal link range with a Strong Enhancer Rune, in blocks. Per-world server setting, synced to clients. */
    int portalsRangeWithStrongEnhancer();

    /** True if the filtered frame block list is a whitelist, false if a blacklist. Per-world server setting, synced to clients. */
    boolean portalsFrameFilterIsWhitelist();

    /** Block id strings (e.g. {@code "minecraft:stone"}) filtered as portal frames. Per-world server setting, synced to clients. */
    List<? extends String> portalsFilteredFrameBlocks();

    /** Whether ambient portal sounds are muted. Per-client setting. */
    boolean portalsMuteAmbientSounds();

    /** Whether player teleport sounds are muted. Per-client setting. */
    boolean portalsMuteTeleportSounds();
}
