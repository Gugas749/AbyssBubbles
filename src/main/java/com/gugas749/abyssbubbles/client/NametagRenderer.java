package com.gugas749.abyssbubbles.client;

import com.gugas749.abyssbubbles.AbyssBubblesConfig;
import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.data.Bubble;
import com.gugas749.abyssbubbles.data.BubbleConfigAttachment;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// value = Dist.CLIENT: RenderNameTagEvent is a client class. Without it, a dedicated server
// would try to load this subscriber and crash (the NeoForge version was missing this too).
@Mod.EventBusSubscriber(modid = Abyssbubbles.MODID, value = Dist.CLIENT)
public class NametagRenderer {
   @SubscribeEvent
   public static void onNametagRender(RenderNameTagEvent event) {
      if (event.getEntity() instanceof Player player) {
         BubbleConfigAttachment config = ClientBubbleData.config(player.getUUID());
         boolean hideNametag = config.isHideNametag();
         if (hideNametag) {
            List<Bubble> bubbles = ClientBubbleData.bubbles(player.getUUID()).bubbles();
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
                  event.setResult(Event.Result.DENY);  // Forge 1.20.1: DENY = never render the nametag
               }
            }
         }
      }
   }
}
