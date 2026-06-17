package com.gugas749.abyssbubbles.client.screen.config;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.mojang.blaze3d.systems.RenderSystem;
import com.gugas749.abyssbubbles.AbyssBubblesConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.LinearLayout.Orientation;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PreviewPanel extends LinearLayout {
   public PreviewPanel(Player player, BubbleConfigState state, Font font) {
      super(0, 0, Orientation.VERTICAL);
      BubblePreviewWidget preview = new BubblePreviewWidget(150, 200, player, state, font);
      this.addChild(preview);
      this.defaultCellSetting().alignVerticallyMiddle();
   }

   public class BubblePreviewWidget extends AbstractWidget {
      private static final ResourceLocation BUBBLE_TEXTURE = ResourceLocation.fromNamespaceAndPath(Abyssbubbles.MODID, "textures/allparts.png");
      private static final int TEXTURE_SIZE = 64;
      private final Player player;
      private final BubbleConfigState state;
      private final Font font;

      public BubblePreviewWidget(int width, int height, Player player, BubbleConfigState state, Font font) {
         super(0, 0, width, height, Component.empty());
         this.player = player;
         this.state = state;
         this.font = font;
      }

      protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
         if (this.player != null && this.state != null) {
            int widgetCenterX = this.getX() + this.width / 2;
            int playerFeetY = this.getY() + this.height - 20;
            int playerScale = 60;
            InventoryScreen.renderEntityInInventoryFollowsMouse(
               guiGraphics, widgetCenterX - 40, playerFeetY - 120, widgetCenterX + 40, playerFeetY, playerScale, 0.0625F, mouseX, mouseY, this.player
            );
            guiGraphics.pose().pushPose();
            float offsetY = (float)(this.state.offsetValue / 10.0) * playerScale;
            float anchorY = playerScale * this.player.getBbHeight();
            float headTopY = playerFeetY - anchorY - offsetY;
            guiGraphics.pose().translate(widgetCenterX, headTopY, 250.0F);
            int texW = 48;
            int texH = 32;
            int arrowTexW = 12;
            int arrowTexH = 7;
            String previewText = "Preview";
            String helloText = "Hello there!";
            int bubbleW1 = this.font.width(previewText) + 2 * (Integer)AbyssBubblesConfig.BUBBLE_PADDING.get() + 7;
            int bubbleW2 = this.font.width(helloText) + 2 * (Integer)AbyssBubblesConfig.BUBBLE_PADDING.get() + 7;
            int arrowX = -(arrowTexW / 2);
            int arrowY = -arrowTexH;
            int bubbleX1 = -(bubbleW1 / 2);
            int bubbleY1 = -texH - arrowTexH + 2;
            int bubbleX2 = -(bubbleW2 / 2);
            int bubbleY2 = (int)(bubbleY1 - texH - 2 - this.state.spacingValue);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            guiGraphics.setColor(this.state.bgColor.r(), this.state.bgColor.g(), this.state.bgColor.b(), 1.0F);
            guiGraphics.blit(BUBBLE_TEXTURE, bubbleX1, bubbleY1, bubbleW1, texH, 0.0F, 32.0F, texW, texH, 64, 64);
            guiGraphics.blit(BUBBLE_TEXTURE, bubbleX2, bubbleY2, bubbleW2, texH, 0.0F, 32.0F, texW, texH, 64, 64);
            guiGraphics.setColor(this.state.borderColor.r(), this.state.borderColor.g(), this.state.borderColor.b(), 1.0F);
            guiGraphics.blit(BUBBLE_TEXTURE, bubbleX1, bubbleY1, bubbleW1, texH, 0.0F, 0.0F, texW, texH, 64, 64);
            guiGraphics.blit(BUBBLE_TEXTURE, bubbleX2, bubbleY2, bubbleW2, texH, 0.0F, 0.0F, texW, texH, 64, 64);
            guiGraphics.setColor(this.state.bgColor.r(), this.state.bgColor.g(), this.state.bgColor.b(), 1.0F);
            guiGraphics.blit(BUBBLE_TEXTURE, arrowX, arrowY, 48.0F, 7.0F, arrowTexW, arrowTexH, 64, 64);
            guiGraphics.setColor(this.state.borderColor.r(), this.state.borderColor.g(), this.state.borderColor.b(), 1.0F);
            guiGraphics.blit(BUBBLE_TEXTURE, arrowX, arrowY, 48.0F, 0.0F, arrowTexW, arrowTexH, 64, 64);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            int textWidth = this.font.width(previewText);
            int textY1 = bubbleY1 + texH / 2 - 9 / 2;
            guiGraphics.drawString(this.font, previewText, -(textWidth / 2) + 1, textY1, this.state.textColor, false);
            int textWidth2 = this.font.width(helloText);
            int textY2 = bubbleY2 + texH / 2 - 9 / 2;
            guiGraphics.drawString(this.font, helloText, -(textWidth2 / 2) + 1, textY2, this.state.textColor, false);
            guiGraphics.pose().popPose();
         }
      }

      protected void updateWidgetNarration(NarrationElementOutput output) {
      }
   }
}
