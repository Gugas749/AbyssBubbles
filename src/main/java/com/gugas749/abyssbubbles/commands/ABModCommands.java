package com.gugas749.abyssbubbles.commands;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.commands.SubRegisters.ABBubbleCommands;
import com.gugas749.abysscore.api.command.AbyssCommand;
import com.gugas749.abysscore.api.command.AbyssCommandRegistrar;

import java.util.List;

public class ABModCommands extends AbyssCommandRegistrar {

    @Override
    protected List<AbyssCommand> commands() {
        return List.of(ABBubbleCommands::register);
    }
}
