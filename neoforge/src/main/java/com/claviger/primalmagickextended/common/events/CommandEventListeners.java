package com.claviger.primalmagickextended.common.events;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.commands.FamiliarCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * NeoForge listener which registers the mod's commands.
 */
@EventBusSubscriber(modid = ConstantsPMX.MOD_ID)
public class CommandEventListeners {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        FamiliarCommand.register(event.getDispatcher());
    }
}
