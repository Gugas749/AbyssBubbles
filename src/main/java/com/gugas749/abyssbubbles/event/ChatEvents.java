package com.gugas749.abyssbubbles.event;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.commands.BubblePermissionManager;
import com.gugas749.abyssbubbles.network.BubblePacket;
import com.gugas749.abyssbubbles.network.ModNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = Abyssbubbles.MODID)
public class ChatEvents {

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public static void onMessage(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();

        if (!BubblePermissionManager.get(player.server).hasUsage(player.getUUID())) return;

        String message = event.getRawText();
        long curTime = player.level().getGameTime();

        // NeoForge: PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, packet)
        // Forge:    send through our channel with the TRACKING_ENTITY_AND_SELF target
        ModNetwork.CHANNEL.raw().send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                new BubblePacket(player.getUUID(), message, curTime));
    }
}
