package com.claviger.primalmagickextended.client.fx.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * Vanilla portal particle with a fixed tint, used by every portal colour except purple. Ported from Custom
 * Portals.
 */
public class ColoredPortalParticle extends PortalParticle {
    protected ColoredPortalParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz,
                                    SpriteSet sprites, float red, float green, float blue, boolean randomizeBrightness) {
        super(level, x, y, z, dx, dy, dz);
        this.pickSprite(sprites);
        float brightness = randomizeBrightness ? this.random.nextFloat() * 0.6F + 0.4F : 1.0F;
        this.rCol = red * brightness;
        this.gCol = green * brightness;
        this.bCol = blue * brightness;
    }

    /** Creates tinted portal particles; with randomized brightness, each particle is dimmed by a random amount. */
    public record Provider(SpriteSet sprites, float red, float green, float blue, boolean randomizeBrightness)
            implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new ColoredPortalParticle(level, x, y, z, dx, dy, dz, sprites, red, green, blue, randomizeBrightness);
        }
    }
}
