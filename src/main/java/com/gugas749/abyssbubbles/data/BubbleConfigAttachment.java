package com.gugas749.abyssbubbles.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.gugas749.abyssbubbles.AbyssBubblesConfig;
import com.gugas749.abyssbubbles.util.Color;

public class BubbleConfigAttachment {
   private Color bgColor;
   private Color borderColor;
   private int textColor;
   private boolean hideNametag = false;
   private double offset = 2.0;
   private double spacing = 4.0;
   private boolean isBgColorChanged = false;
   private boolean isBorderColorChanged = false;
   private boolean isTextColorChanged = false;
   private boolean isHideNametagChanged = false;
   private boolean isOffsetChanged = false;
   private boolean isSpacingChanged = false;
   public static final Codec<BubbleConfigAttachment> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Color.CODEC.fieldOf("bg_color").forGetter(BubbleConfigAttachment::getBgColor),
            Color.CODEC.fieldOf("border_color").forGetter(BubbleConfigAttachment::getBorderColor),
            Codec.INT.fieldOf("text_color").forGetter(BubbleConfigAttachment::getTextColor),
            Codec.BOOL.fieldOf("hide_nametag").forGetter(BubbleConfigAttachment::isHideNametag),
            Codec.DOUBLE.fieldOf("offset").forGetter(BubbleConfigAttachment::getOffset),
            Codec.DOUBLE.fieldOf("spacing").forGetter(BubbleConfigAttachment::getSpacing)
         )
         .apply(instance, BubbleConfigAttachment::new)
   );

   public BubbleConfigAttachment(Color bgColor, Color borderColor, int textColor) {
      this.bgColor = bgColor;
      this.borderColor = borderColor;
      this.textColor = textColor;
   }

   public BubbleConfigAttachment(Color bgColor, Color borderColor, int textColor, boolean hideNametag, double offset, double spacing) {
      this.bgColor = bgColor;
      this.borderColor = borderColor;
      this.textColor = textColor;
      this.offset = offset;
      this.spacing = spacing;
      this.hideNametag = hideNametag;
   }

   public void resetToDefaults() {
      this.setBgColor(new Color(1.0F, 1.0F, 1.0F));
      this.setBorderColor(new Color(0.0F, 0.0F, 0.0F));
      this.setTextColor(0);
      this.setOffset((Double)AbyssBubblesConfig.BUBBLE_OFFSET.get());
      this.setSpacing((Double)AbyssBubblesConfig.BUBBLE_SPACING.get());
      this.setHideNametag((Boolean)AbyssBubblesConfig.HIDE_NAMETAG.get());
   }

   public void resetFlags() {
      this.isBgColorChanged = false;
      this.isBorderColorChanged = false;
      this.isTextColorChanged = false;
      this.isHideNametagChanged = false;
      this.isOffsetChanged = false;
      this.isSpacingChanged = false;
   }

   public boolean isSpacingChanged() {
      return this.isSpacingChanged;
   }

   public boolean isOffsetChanged() {
      return this.isOffsetChanged;
   }

   public boolean isHideNametagChanged() {
      return this.isHideNametagChanged;
   }

   public double getSpacing() {
      return this.spacing;
   }

   public void setSpacing(double spacing) {
      if (spacing != this.spacing) {
         this.isSpacingChanged = true;
         this.spacing = spacing;
      }
   }

   public double getOffset() {
      return this.offset;
   }

   public void setOffset(double offset) {
      if (offset != this.offset) {
         this.isOffsetChanged = true;
         this.offset = offset;
      }
   }

   public boolean isHideNametag() {
      return this.hideNametag;
   }

   public void setHideNametag(boolean hideNametag) {
      if (hideNametag != this.hideNametag) {
         this.isHideNametagChanged = true;
         this.hideNametag = hideNametag;
      }
   }

   public Color getBgColor() {
      return this.bgColor;
   }

   public void setBgColor(Color bgColor) {
      if (!bgColor.equals(this.bgColor)) {
         this.isBgColorChanged = true;
         this.bgColor = bgColor;
      }
   }

   public Color getBorderColor() {
      return this.borderColor;
   }

   public void setBorderColor(Color borderColor) {
      if (!borderColor.equals(this.borderColor)) {
         this.isBorderColorChanged = true;
         this.borderColor = borderColor;
      }
   }

   public int getTextColor() {
      return this.textColor;
   }

   public void setTextColor(int textColor) {
      if (textColor != this.textColor) {
         this.isTextColorChanged = true;
         this.textColor = textColor;
      }
   }

   public boolean isBgColorChanged() {
      return this.isBgColorChanged;
   }

   public boolean isBorderColorChanged() {
      return this.isBorderColorChanged;
   }

   public boolean isTextColorChanged() {
      return this.isTextColorChanged;
   }
}
