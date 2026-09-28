package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.AbyssBubblesConfig;
import com.gugas749.abyssbubbles.data.BubbleConfigAttachment;
import com.gugas749.abyssbubbles.data.BubbleConfigStorage;
import com.gugas749.abysscore.api.network.AbyssPacketContext;
import net.minecraft.server.level.ServerPlayer;

public class ServerPayloadHandler {
   public static void handleUpdateBubbleConfig(BubbleConfigUpdatePacket packet, AbyssPacketContext context) {
      context.enqueueWork(() -> {
         if (context.player() instanceof ServerPlayer player) {
            if (AbyssBubblesConfig.ONLY_OPS.get() && !player.hasPermissions(2)) {
               return;
            }

            BubbleConfigAttachment data = BubbleConfigStorage.get(player);
            data.setBgColor(packet.bgColor());
            data.setBorderColor(packet.borderColor());
            data.setTextColor(packet.textColor());
            data.setOffset(packet.offset());
            data.setHideNametag(packet.hideNametag());
            data.setSpacing(packet.spacing());
            // Saves + sends to this player and everyone who can see them
            // (replaces player.setData(...) + player.syncData(...))
            BubbleConfigStorage.set(player, data);
         }
      });
   }
}
