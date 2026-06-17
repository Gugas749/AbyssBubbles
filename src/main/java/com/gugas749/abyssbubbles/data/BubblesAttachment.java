package com.gugas749.abyssbubbles.data;

import com.mojang.serialization.Codec;
import com.gugas749.abyssbubbles.AbyssBubblesConfig;
import java.util.ArrayList;
import java.util.List;

public class BubblesAttachment {
   public static final Codec<BubblesAttachment> CODEC = Bubble.CODEC.listOf().xmap(BubblesAttachment::new, BubblesAttachment::bubbles);
   private final List<Bubble> bubbles;

   public BubblesAttachment(List<Bubble> bubbles) {
      this.bubbles = new ArrayList<>(bubbles);
   }

   public BubblesAttachment() {
      this.bubbles = new ArrayList<>();
   }

   public List<Bubble> bubbles() {
      return this.bubbles;
   }

   public void addBubble(Bubble bubble) {
      this.bubbles.add(bubble);
      if (this.bubbles.size() > (Integer)AbyssBubblesConfig.MAX_BUBBLES.get()) {
         this.bubbles.removeFirst();
      }
   }
}
