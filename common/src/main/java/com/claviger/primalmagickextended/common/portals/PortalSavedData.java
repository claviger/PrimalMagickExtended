package com.claviger.primalmagickextended.common.portals;

import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.List;

/**
 * Server-wide portal registry. Stored once, in the overworld's data storage, because portals link across
 * dimensions. Links are not persisted: loading re-registers every portal in its saved order, which recomputes the
 * links. Ported from Custom Portals.
 */
public class PortalSavedData extends SavedData {
    /** Persistence codec: the list of portals, in registration order. */
    public static final Codec<PortalSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CustomPortal.CODEC.listOf().fieldOf("portals").forGetter(data -> data.registry.getPortals())
    ).apply(instance, PortalSavedData::new));

    /** The saved data type. It has no data fixer, which NeoForge allows for modded saved data. */
    public static final SavedDataType<PortalSavedData> TYPE = new SavedDataType<>(
            ResourceUtilsPMX.loc("portals"),
            PortalSavedData::new,
            CODEC,
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

    public PortalRegistry getRegistry() {
        return this.registry;
    }

    public static PortalSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }
}
