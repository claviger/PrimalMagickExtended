package com.claviger.primalmagickextended.client.events;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.client.fx.particles.ColoredPortalParticle;
import com.claviger.primalmagickextended.client.fx.particles.ParticleTypesPMX;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/**
 * NeoForge listeners for client-only registration events.
 */
@EventBusSubscriber(modid = ConstantsPMX.MOD_ID, value = Dist.CLIENT)
public class ClientRegistrationEventListenersPMX {
    /** Registers a tinted portal particle provider for every portal colour except purple, which has no particle of its own. */
    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        for (PortalColor color : PortalColor.values()) {
            if (color.usesVanillaParticle()) {
                continue;
            }
            event.registerSpriteSet(ParticleTypesPMX.particleType(color),
                    sprites -> new ColoredPortalParticle.Provider(sprites, color.red(), color.green(), color.blue(), color.randomizeBrightness()));
        }
    }
}
