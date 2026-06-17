package com.gugas749.abyssbubbles.client;

import com.gugas749.abyssbubbles.AbyssBubblesConfig;
import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.data.Bubble;
import com.gugas749.abyssbubbles.data.BubbleConfigAttachment;
import com.gugas749.abyssbubbles.data.BubblesAttachment;
import com.gugas749.abyssbubbles.data.ModAttachments;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.util.TriState;

@EventBusSubscriber(modid = Abyssbubbles.MODID)
public class NametagRenderer {
   @SubscribeEvent
   public static void onNametagRender(RenderNameTagEvent event) {
      if (event.getEntity() instanceof Player player) {
         BubbleConfigAttachment config = (BubbleConfigAttachment)player.getData(ModAttachments.BUBBLE_CONFIG.get());
         boolean hideNametag = config.isHideNametag();
         if (hideNametag) {
            List<Bubble> bubbles = ((BubblesAttachment)player.getData(ModAttachments.BUBBLES.get())).bubbles();
            if (bubbles.size() > 0) {
               boolean hasBubbles = false;
               int LIFETIME = (Integer)AbyssBubblesConfig.BUBBLE_LIFETIME.get();
               long gameTime = player.level().getGameTime();

               for (Bubble bubble : bubbles) {
                  long age = gameTime - bubble.startTime();
                  if (age < LIFETIME) {
                     hasBubbles = true;
                     break;
                  }
               }

               if (hasBubbles) {
                  event.setCanRender(TriState.FALSE);
               }
            }
         }
      }
   }
}
