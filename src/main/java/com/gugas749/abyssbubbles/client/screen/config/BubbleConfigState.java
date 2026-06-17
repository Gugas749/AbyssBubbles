package com.gugas749.abyssbubbles.client.screen.config;

import com.gugas749.abyssbubbles.AbyssBubblesConfig;
import com.gugas749.abyssbubbles.data.BubbleConfigAttachment;
import com.gugas749.abyssbubbles.data.ModAttachments;
import com.gugas749.abyssbubbles.network.BubbleConfigUpdatePacket;
import com.gugas749.abyssbubbles.util.Color;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class BubbleConfigState {
   private Player player;
   protected Color bgColor;
   protected Color borderColor;
   protected int textColor;
   protected Color textRgbColor;
   protected double offsetValue;
   protected double spacingValue;
   protected boolean hideNametagValue;

   public BubbleConfigState(Player player) {
      this.player = player;
      this.load();
   }

   public void load() {
      BubbleConfigAttachment config = (BubbleConfigAttachment)this.player.getData(ModAttachments.BUBBLE_CONFIG.get());
      this.bgColor = config.getBgColor();
      this.borderColor = config.getBorderColor();
      this.textColor = config.getTextColor();
      this.textRgbColor = Color.hexToRGB(this.textColor);
      this.offsetValue = config.getOffset();
      this.spacingValue = config.getSpacing();
      this.hideNametagValue = config.isHideNametag();
   }

   public void reset() {
      this.bgColor = new Color(1.0F, 1.0F, 1.0F);
      this.borderColor = new Color(0.0F, 0.0F, 0.0F);
      this.textColor = 0;
      this.offsetValue = (Double)AbyssBubblesConfig.BUBBLE_OFFSET.get();
      this.spacingValue = (Double)AbyssBubblesConfig.BUBBLE_SPACING.get();
      this.textRgbColor = Color.hexToRGB(this.textColor);
      this.hideNametagValue = (Boolean)AbyssBubblesConfig.HIDE_NAMETAG.get();
   }

   private void save() {
      PacketDistributor.sendToServer(
         new BubbleConfigUpdatePacket(this.bgColor, this.borderColor, this.textColor, this.offsetValue, this.spacingValue, this.hideNametagValue),
         new CustomPacketPayload[0]
      );
   }
}
