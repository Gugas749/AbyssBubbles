package com.gugas749.abyssbubbles;

import com.gugas749.abyssbubbles.commands.ABModCommands;
import com.gugas749.abyssbubbles.network.ModNetwork;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(Abyssbubbles.MODID)
public class Abyssbubbles {

    public static final String MODID = "abyssbubbles";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Forge 1.20.1: no-arg constructor (no IEventBus / ModContainer parameters)
    public Abyssbubbles() {
        // NeoForge: modContainer.registerConfig(...)  →  Forge: ModLoadingContext
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, AbyssBubblesConfig.SPEC);

        // Replaces ModAttachments.ATTACHMENT_TYPES.register(...) and the RegisterPayloadHandlersEvent
        ModNetwork.register();

        MinecraftForge.EVENT_BUS.register(new ABModCommands());

        // ChatEvents, BubbleConfigStorage, BubbleRenderer and NametagRenderer register themselves
        // through @Mod.EventBusSubscriber
    }
}
