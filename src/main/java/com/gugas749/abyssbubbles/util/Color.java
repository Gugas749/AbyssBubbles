package com.gugas749.abyssbubbles.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Color(float r, float g, float b) {
   public static final StreamCodec<ByteBuf, Color> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.FLOAT, Color::r, ByteBufCodecs.FLOAT, Color::g, ByteBufCodecs.FLOAT, Color::b, Color::new
   );
   public static final Codec<Color> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.FLOAT.fieldOf("r").forGetter(Color::r), Codec.FLOAT.fieldOf("g").forGetter(Color::g), Codec.FLOAT.fieldOf("b").forGetter(Color::b)
         )
         .apply(instance, Color::new)
   );

   public static boolean isHex(String hex) {
      return hex != null && hex.matches("^#?[0-9-a-fA-F]+$");
   }

   public static int hexStrToInt(String hex) {
      if (hex.startsWith("#")) {
         hex = hex.substring(1);
      }

      return Integer.parseInt(hex, 16);
   }

   public static int rgbToHex(Color rgb) {
      return (int)(rgb.r() * 255.0F) << 16 | (int)(rgb.g() * 255.0F) << 8 | (int)(rgb.b() * 255.0F);
   }

   public static Color hexToRGB(String hex) {
      if (hex.startsWith("#")) {
         hex = hex.substring(1);
      }

      int colorInt = Integer.parseInt(hex, 16);
      float r = ((colorInt & 0xFF0000) >> 16) / 255.0F;
      float g = ((colorInt & 0xFF00) >> 8) / 255.0F;
      float b = (colorInt & 0xFF) / 255.0F;
      return new Color(r, g, b);
   }

   public static Color hexToRGB(int hex) {
      float r = ((hex & 0xFF0000) >> 16) / 255.0F;
      float g = ((hex & 0xFF00) >> 8) / 255.0F;
      float b = (hex & 0xFF) / 255.0F;
      return new Color(r, g, b);
   }
}
