package com.gugas749.abyssbubbles.commands;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.gugas749.abyssbubbles.client.screen.config.BubbleConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(modid = Abyssbubbles.MODID, value = Dist.CLIENT)
public class ClientBubbleCommand {
   @SubscribeEvent
   private static void register(RegisterClientCommandsEvent event) {
      event.getDispatcher().register((LiteralArgumentBuilder)Commands.literal(Abyssbubbles.MODID).then(Commands.literal("settings").executes(ctx -> {
         Minecraft mc = Minecraft.getInstance();
         mc.tell(() -> mc.setScreen(new BubbleConfigScreen(null)));
         return 1;
      })));
   }
}
