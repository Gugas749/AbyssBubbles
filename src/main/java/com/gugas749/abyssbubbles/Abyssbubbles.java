package com.gugas749.abyssbubbles;

import com.gugas749.abyssbubbles.bubble.BubbleManager;
import com.gugas749.abyssbubbles.bubble.BubbleSavedData;
import com.gugas749.abyssbubbles.command.BubbleCommand;
import com.gugas749.abyssbubbles.network.BubbleNetwork;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;

@Mod(Abyssbubbles.MODID)
public class Abyssbubbles {
    public static final String MODID = "abyssbubbles";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Abyssbubbles(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        BubbleNetwork.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
        NeoForge.EVENT_BUS.addListener(this::onServerStopping);
        NeoForge.EVENT_BUS.register(BubbleCommand.class);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        BubbleManager.init();
    }

    private void onServerStarted(ServerStartedEvent event) {
        BubbleManager.setServer(event.getServer());
        BubbleSavedData.get(event.getServer());
    }

    private void onServerStopping(ServerStoppingEvent event) {
        BubbleSavedData.markDirty(event.getServer());
    }
}
