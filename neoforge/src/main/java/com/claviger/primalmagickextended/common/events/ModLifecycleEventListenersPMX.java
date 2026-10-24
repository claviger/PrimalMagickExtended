package com.claviger.primalmagickextended.common.events;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.network.PlayPacketRegistrationPMX;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * NeoForge listeners for mod lifecycle related events.
 */
@EventBusSubscriber(modid = ConstantsPMX.MOD_ID)
public class ModLifecycleEventListenersPMX {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(PlayPacketRegistrationPMX::registerMessages);
    }
}
