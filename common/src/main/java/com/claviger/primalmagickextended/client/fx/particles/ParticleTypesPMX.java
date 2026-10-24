package com.claviger.primalmagickextended.client.fx.particles;

import com.claviger.primalmagickextended.common.portals.PortalColor;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.verdantartifice.primalmagick.common.registries.IRegistryItem;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Deferred registry for the mod's particle types: one tinted portal particle per {@link PortalColor}, except
 * purple, whose portals use the vanilla portal particle as in Custom Portals.
 */
public class ParticleTypesPMX {
    public static void init() {
        // Pass the service initialization through this class so it gets class loaded and fields registered
        ServicesPMX.PARTICLE_TYPES_REGISTRY.init();
    }

    private static final Map<PortalColor, IRegistryItem<ParticleType<?>, SimpleParticleType>> PORTAL_PARTICLES = registerPortalParticles();

    private static Map<PortalColor, IRegistryItem<ParticleType<?>, SimpleParticleType>> registerPortalParticles() {
        Map<PortalColor, IRegistryItem<ParticleType<?>, SimpleParticleType>> particles = new EnumMap<>(PortalColor.class);
        for (PortalColor color : PortalColor.values()) {
            if (!color.usesVanillaParticle()) {
                particles.put(color, ServicesPMX.PARTICLE_TYPES_REGISTRY.register(color.getSerializedName() + "_portal_particle",
                        () -> new SimpleParticleType(false)));
            }
        }
        return Collections.unmodifiableMap(particles);
    }

    /** The particle shown by portals of the given colour: the vanilla portal particle for purple. */
    public static ParticleOptions particle(PortalColor color) {
        IRegistryItem<ParticleType<?>, SimpleParticleType> type = PORTAL_PARTICLES.get(color);
        return type == null ? ParticleTypes.PORTAL : type.get();
    }

    /** The registered particle type of the given colour, or null for purple, which has none of its own. */
    @Nullable
    public static SimpleParticleType particleType(PortalColor color) {
        IRegistryItem<ParticleType<?>, SimpleParticleType> type = PORTAL_PARTICLES.get(color);
        return type == null ? null : type.get();
    }
}
