package com.claviger.primalmagickextended.datagen.particles;

import com.claviger.primalmagickextended.client.fx.particles.ParticleTypesPMX;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.data.ParticleDescriptionProvider;

/**
 * Generates the particle description files for the mod. Each tinted portal particle uses the vanilla
 * {@code generic_0} to {@code generic_7} sprites, as in Custom Portals.
 */
public class ParticleDescriptionProviderPMXNeoforge extends ParticleDescriptionProvider {
    public ParticleDescriptionProviderPMXNeoforge(PackOutput output) {
        super(output);
    }

    @Override
    protected void addDescriptions() {
        for (PortalColor color : PortalColor.values()) {
            // Purple portals use the vanilla portal particle, which already has a description
            if (!color.usesVanillaParticle()) {
                this.spriteSet(ParticleTypesPMX.particleType(color), Identifier.withDefaultNamespace("generic"), 8, false);
            }
        }
    }
}
