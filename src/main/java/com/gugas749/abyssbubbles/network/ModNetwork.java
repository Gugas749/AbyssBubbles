package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.Abyssbubbles;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Abyssbubbles.MODID)
public class ModNetwork {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(BubblePacket.TYPE, BubblePacket.STREAM_CODEC, ClientPayloadHandler::handleNewBubble);
        registrar.playToClient(OpenBubbleScreenPacket.TYPE, OpenBubbleScreenPacket.STREAM_CODEC, OpenBubbleScreenPacket::handle);
        registrar.playToServer(BubbleConfigUpdatePacket.TYPE, BubbleConfigUpdatePacket.STREAM_CODEC, ServerPayloadHandler::handleUpdateBubbleConfig);
    }
}
