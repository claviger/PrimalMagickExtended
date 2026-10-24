package com.claviger.primalmagickextended.common.portals;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

import java.util.List;

/**
 * Server-wide portal registry. Stored once, in the overworld's data storage, because portals link across
 * dimensions. Links are not persisted: loading re-registers every portal in its saved order, which recomputes the
 * links. Ported from Custom Portals.
 */
public class PortalSavedData extends SavedData {
    private static final Logger LOGGER = LogUtils.getLogger();

    /** Persistence codec: the list of portals, in registration order. */
    public static final Codec<PortalSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CustomPortal.CODEC.listOf().fieldOf("portals").forGetter(data -> data.registry.getPortals())
    ).apply(instance, PortalSavedData::new));

    /**
     * The name of the saved data file, {@code data/primalmagickextended_portals.dat} in the overworld's folder. The
     * name becomes a file name, so it cannot be a namespaced id.
     */
    public static final String FILE_NAME = ConstantsPMX.MOD_ID + "_portals";

    /**
     * The saved data factory. It reads and writes the data through {@link #CODEC}, and has no data fixer, which
     * NeoForge allows for modded saved data.
     */
    public static final SavedData.Factory<PortalSavedData> FACTORY = new SavedData.Factory<>(
            PortalSavedData::new,
            PortalSavedData::load,
            null
    );

    private final PortalRegistry registry = new PortalRegistry();

    public PortalSavedData() {}

    PortalSavedData(List<CustomPortal> portals) {
        // Registering in order recomputes links
        for (CustomPortal portal : portals) {
            this.registry.register(portal);
        }
    }

    /** Decodes saved data written by {@link #save}; data that fails to decode is logged and replaced by an empty registry. */
    public static PortalSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        return CODEC.parse(NbtOps.INSTANCE, tag)
                .resultOrPartial(error -> LOGGER.error("Failed to load saved portals: {}", error))
                .orElseGet(PortalSavedData::new);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        Tag encoded = CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
        if (encoded instanceof CompoundTag compound) {
            tag.merge(compound);
        }
        return tag;
    }

    public PortalRegistry getRegistry() {
        return this.registry;
    }

    public static PortalSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(FACTORY, FILE_NAME);
    }
}
