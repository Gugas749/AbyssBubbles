package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.data.Bubble;
import com.gugas749.abyssbubbles.data.BubblesAttachment;
import com.gugas749.abyssbubbles.data.ModAttachments;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
   public static void handleNewBubble(BubblePacket bubblePacket, IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         Player player = ctx.player().level().getPlayerByUUID(bubblePacket.playerUUID());
         if (player != null) {
            ((BubblesAttachment)player.getData(ModAttachments.BUBBLES.get())).addBubble(new Bubble(bubblePacket.message(), bubblePacket.startTime()));
         }
      });
   }
}
