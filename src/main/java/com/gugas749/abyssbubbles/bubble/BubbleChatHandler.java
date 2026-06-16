package com.gugas749.abyssbubbles.bubble;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;

@EventBusSubscriber
public class BubbleChatHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        if (!BubbleManager.hasUsagePermission(player.getUUID())) return;

        // Dispatch as bubble and cancel normal chat
        String message = event.getMessage().getString();
        BubbleManager.dispatchBubble(player, message);
        event.setCanceled(true);
    }
}
