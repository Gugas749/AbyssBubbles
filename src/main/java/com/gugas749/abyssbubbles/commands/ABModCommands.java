package com.gugas749.abyssbubbles.commands;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.commands.SubRegisters.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Abyssbubbles.MODID)
public class ABModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        Abyssbubbles.LOGGER.info("[AbyssBubbles] Registering commands...");

        ABBubbleCommands.register(event.getDispatcher());
    }
}
