package com.gugas749.abyssbubbles.commands;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.commands.SubRegisters.ABBubbleCommands;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;

public class ABModCommands {

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        Abyssbubbles.LOGGER.info("[AbyssBubbles] Registering commands...");

        ABBubbleCommands.register(event.getDispatcher());
    }
}
