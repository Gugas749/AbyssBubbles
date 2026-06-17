package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.AbyssBubblesConfig;
import com.gugas749.abyssbubbles.data.BubbleConfigAttachment;
import com.gugas749.abyssbubbles.data.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
   public static void handleUpdateBubbleConfig(BubbleConfigUpdatePacket packet, IPayloadContext context) {
      context.enqueueWork(() -> {
         if (context.player() instanceof ServerPlayer player) {
            if ((Boolean)AbyssBubblesConfig.ONLY_OPS.get() && !context.player().hasPermissions(2)) {
               return;
            }

            BubbleConfigAttachment data = (BubbleConfigAttachment)player.getData(ModAttachments.BUBBLE_CONFIG.get());
            data.setBgColor(packet.bgColor());
            data.setBorderColor(packet.borderColor());
            data.setTextColor(packet.textColor());
            data.setOffset(packet.offset());
            data.setHideNametag(packet.hideNametag());
            data.setSpacing(packet.spacing());
            player.setData(ModAttachments.BUBBLE_CONFIG.get(), data);
            player.syncData(ModAttachments.BUBBLE_CONFIG.get());
         }
      });
   }
}
