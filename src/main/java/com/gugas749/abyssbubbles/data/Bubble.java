package com.gugas749.abyssbubbles.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class Bubble {
   private final String message;
   private final long startTime;
   private transient List<FormattedCharSequence> lines;
   private transient int width;
   private transient int height;
   public static final Codec<Bubble> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(Codec.STRING.fieldOf("message").forGetter(Bubble::message), Codec.LONG.fieldOf("startTime").forGetter(Bubble::startTime))
         .apply(instance, Bubble::new)
   );

   public Bubble(String message, long startTime) {
      this.message = message;
      this.startTime = startTime;
   }

   public String message() {
      return this.message;
   }

   public long startTime() {
      return this.startTime;
   }

   public List<FormattedCharSequence> getLayout(Font font, int maxWidth) {
      if (this.lines == null) {
         this.lines = font.split(Component.literal(this.message), maxWidth);
         this.width = 0;

         for (FormattedCharSequence line : this.lines) {
            this.width = Math.max(this.width, font.width(line));
         }

         this.height = 9 * this.lines.size();
      }

      return this.lines;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }
}
