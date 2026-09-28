package com.gugas749.abyssbubbles.commands;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.commands.SubRegisters.ABBubbleCommands;
import com.gugas749.abysscore.api.command.AbyssCommand;
import com.gugas749.abysscore.api.command.AbyssCommandRegistrar;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;

public class ABModCommands {

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        Abyssbubbles.LOGGER.info("[AbyssBubbles] Registering commands...");

        ABBubbleCommands.register(event.getDispatcher());
    }
}
