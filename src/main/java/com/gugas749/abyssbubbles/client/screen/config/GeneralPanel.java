package com.gugas749.abyssbubbles.client.screen.config;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.LinearLayout.Orientation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

public class GeneralPanel extends LinearLayout {
   private BubbleConfigState state;
   private final ExtendedButton nametagButton;

   public GeneralPanel(final BubbleConfigState state, int width) {
      super(0, 0, Orientation.VERTICAL);
      this.state = state;
      this.spacing(5);
      ExtendedSlider offsetSlider = new ExtendedSlider(
         0, 0, width, 20, Component.translatable("gui.abyssbubbles.config.offset"), Component.empty(), -20.0, 50.0, this.state.offsetValue, 0.5, 1, true
      ) {
         protected void applyValue() {
            super.applyValue();
            state.offsetValue = this.getValue();
         }
      };
      ExtendedSlider spacingSlider = new ExtendedSlider(
         0, 0, width, 20, Component.translatable("gui.abyssbubbles.config.spacing"), Component.empty(), 0.0, 10.0, this.state.spacingValue, 1.0, 1, true
      ) {
         protected void applyValue() {
            super.applyValue();
            state.spacingValue = this.getValue();
         }
      };
      this.nametagButton = new ExtendedButton(0, 0, width, 20, this.getNametagButtonText(), b -> {
         this.state.hideNametagValue = !this.state.hideNametagValue;
         b.setMessage(this.getNametagButtonText());
      });
      this.addChild(offsetSlider);
      this.addChild(spacingSlider);
      this.addChild(this.nametagButton);
   }

   private Component getNametagButtonText() {
      MutableComponent statusComponent = this.state.hideNametagValue
         ? Component.translatable("gui.abyssbubbles.config.hidenametag.true").withStyle(ChatFormatting.GREEN)
         : Component.translatable("gui.abyssbubbles.config.hidenametag.false").withStyle(ChatFormatting.RED);
      return Component.translatable("gui.abyssbubbles.config.hidenametag", new Object[]{statusComponent});
   }
}
