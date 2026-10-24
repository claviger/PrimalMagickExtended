package com.claviger.primalmagickextended.datagen.particles;

import com.claviger.primalmagickextended.client.fx.particles.ParticleTypesPMX;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;

/**
 * Generates the particle description files for the mod. Each tinted portal particle uses the vanilla
 * {@code generic_0} to {@code generic_7} sprites, as in Custom Portals.
 */
public class ParticleDescriptionProviderPMXNeoforge extends ParticleDescriptionProvider {
    public ParticleDescriptionProviderPMXNeoforge(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper);
    }

    @Override
    protected void addDescriptions() {
        for (PortalColor color : PortalColor.values()) {
            // Purple portals use the vanilla portal particle, which already has a description
            if (!color.usesVanillaParticle()) {
                this.spriteSet(ParticleTypesPMX.particleType(color), ResourceLocation.withDefaultNamespace("generic"), 8, false);
            }
        }
    }
}
