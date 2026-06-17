package com.gugas749.abyssbubbles;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class AbyssBubblesConfig {
   public static final ModConfigSpec SPEC;
   public static final ConfigValue<Boolean> ONLY_OPS;
   public static final ConfigValue<Integer> MAX_BUBBLES;
   public static final IntValue BUBBLE_PADDING;
   public static final ConfigValue<Integer> BUBBLE_LIFETIME;
   public static final ConfigValue<Double> BUBBLE_SPACING;
   public static final ConfigValue<Double> BUBBLE_OFFSET;
   public static final ConfigValue<Boolean> HIDE_NAMETAG;

   static {
      Builder builder = new Builder();
      builder.push("Permissions");
      ONLY_OPS = builder.comment("If true, only OPs can change their visual settings").define("only_ops_can_change", false);
      builder.pop();
      builder.push("General Settings");
      MAX_BUBBLES = builder.comment("Max number of bubbles above the players head").defineInRange("max_bubbles", 5, 0, 15);
      builder.pop();
      builder.push("Visual Settings");
      HIDE_NAMETAG = builder.comment(
            "Default value for the hide nametag value, if true the nametag will be hidden when bubbles active (WARNING: Players can change this value in game)"
         )
         .define("hide_nametag", true);
      BUBBLE_PADDING = builder.comment("Bubble's inner space").defineInRange("bubble_padding", 6, 0, 15);
      BUBBLE_SPACING = builder.comment("Default value for the spacing between bubbles (WARNING: Players can change their spacing in game)")
         .defineInRange("bubble_spacing", 4.0, 0.0, 10.0);
      BUBBLE_OFFSET = builder.comment("Default value for the offset between the bubbles and player's head (WARNING: Players can change their offset in game)")
         .defineInRange("bubble_offset", 3.0, 0.0, 50.0);
      builder.pop();
      builder.push("Time Settings");
      BUBBLE_LIFETIME = builder.comment("How long will a single bubble last? (In ticks)").defineInRange("bubble_lifetime", 500, 20, 1200);
      builder.pop();
      SPEC = builder.build();
   }
}
