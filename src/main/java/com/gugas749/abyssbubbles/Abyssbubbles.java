package com.gugas749.abyssbubbles;

import com.gugas749.abyssbubbles.commands.ABModCommands;
import com.gugas749.abyssbubbles.data.ModAttachments;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(Abyssbubbles.MODID)
public class Abyssbubbles {

    public static final String MODID = "abyssbubbles";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Abyssbubbles(IEventBus modEventBus, ModContainer modContainer) {
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.SERVER, AbyssBubblesConfig.SPEC);

        NeoForge.EVENT_BUS.register(new ABModCommands());
    }
}
