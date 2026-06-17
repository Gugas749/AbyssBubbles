package com.gugas749.abyssbubbles.event;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.commands.BubblePermissionManager;
import com.gugas749.abyssbubbles.network.BubblePacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Abyssbubbles.MODID)
public class ChatEvents {

    @SubscribeEvent
    public static void onMessage(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();

        // Only route chat through bubble if player has usage permission
        if (!BubblePermissionManager.get(player.server).hasUsage(player.getUUID())) return;

        String message = event.getRawText();
        long curTime = player.level().getGameTime();
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new BubblePacket(player.getUUID(), message, curTime), new CustomPacketPayload[0]);
    }
}
