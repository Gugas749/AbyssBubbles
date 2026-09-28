package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.client.ClientBubbleData;
import com.gugas749.abyssbubbles.client.ClientScreenOpener;
import com.gugas749.abyssbubbles.data.Bubble;
import com.gugas749.abysscore.api.network.AbyssPacketContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPayloadHandler {

   public static void handleNewBubble(BubblePacket packet, AbyssPacketContext ctx) {
      // Stored by UUID: no need to look up the player entity (it may not even be loaded yet)
      ctx.enqueueWork(() -> ClientBubbleData.bubbles(packet.playerUUID())
              .addBubble(new Bubble(packet.message(), packet.startTime())));
   }

   public static void handleOpenScreen(OpenBubbleScreenPacket packet, AbyssPacketContext ctx) {
      ctx.enqueueWork(() -> ClientScreenOpener.openBubbleScreen(packet));
   }

   public static void handleConfigSync(BubbleConfigSyncPacket packet, AbyssPacketContext ctx) {
      ctx.enqueueWork(() -> ClientBubbleData.setConfig(packet.playerUUID(), packet.config()));
   }
}
