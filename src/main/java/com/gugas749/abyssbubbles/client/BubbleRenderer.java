package com.gugas749.abyssbubbles.client;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.gugas749.abyssbubbles.AbyssBubblesConfig;
import com.gugas749.abyssbubbles.data.Bubble;
import com.gugas749.abyssbubbles.data.BubbleConfigAttachment;
import com.gugas749.abyssbubbles.data.BubblesAttachment;
import com.gugas749.abyssbubbles.data.ModAttachments;
import com.gugas749.abyssbubbles.util.Color;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent.Post;
import org.joml.Matrix4f;

@EventBusSubscriber(modid = Abyssbubbles.MODID, value = Dist.CLIENT)
public class BubbleRenderer {
   private static final ResourceLocation ATLAS = ResourceLocation.fromNamespaceAndPath(Abyssbubbles.MODID, "textures/allparts.png");
   private static final float ATLAS_SIZE = 64.0F;
   private static final int B_TEX_W = 48;
   private static final int B_TEX_H = 32;
   private static final int B_SLICE = 7;
   private static final int A_W = 12;
   private static final int A_H = 7;

   @SubscribeEvent
   public static void onRenderPlayer(Post<Player, ?> event) {
      if (event.getEntity() instanceof Player player) {
         Minecraft var42 = Minecraft.getInstance();
         if (player != var42.player || !var42.options.getCameraType().isFirstPerson()) {
            BubblesAttachment data = (BubblesAttachment)player.getData(ModAttachments.BUBBLES.get());
            List<Bubble> bubbles = data.bubbles();
            if (!bubbles.isEmpty()) {
               ProfilerFiller profiler = player.level().getProfiler();
               profiler.push("abyssbubbles_render");
               BubbleConfigAttachment config = (BubbleConfigAttachment)player.getData(ModAttachments.BUBBLE_CONFIG.get());
               Color bgColor = config.getBgColor();
               Color borderColor = config.getBorderColor();
               int textColor = config.getTextColor();
               PoseStack poseStack = event.getPoseStack();
               MultiBufferSource bufferSource = event.getMultiBufferSource();
               double offset = config.getOffset() / 10.0;
               poseStack.pushPose();
               poseStack.translate(0.0, player.getBbHeight() + offset, 0.0);
               poseStack.mulPose(var42.getEntityRenderDispatcher().cameraOrientation());
               float scale = 0.025F;
               poseStack.scale(scale, -scale, scale);
               float currentY = 0.0F;
               double spacing = config.getSpacing();
               int B_PADDING = (Integer)AbyssBubblesConfig.BUBBLE_PADDING.get();
               long gameTime = player.level().getGameTime();
               int LIFETIME = (Integer)AbyssBubblesConfig.BUBBLE_LIFETIME.get();
               VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(ATLAS));
               Matrix4f matrix4f = poseStack.last().pose();

               for (int i = bubbles.size() - 1; i >= 0; i--) {
                  Bubble bubble = bubbles.get(i);
                  long age = gameTime - bubble.startTime();
                  if (age < LIFETIME) {
                     float alpha = 1.0F;
                     int textHeight = bubble.getHeight();
                     int textWidth = Math.max(bubble.getWidth(), 20);
                     float textX = -textWidth / 2.0F;
                     float bubbleWidth = textWidth + B_PADDING * 2;
                     float bubbleHeight = textHeight + B_PADDING * 2;
                     float bubbleX = textX - B_PADDING;
                     float bubbleY = currentY - bubbleHeight;
                     renderBubble(vertexConsumer, matrix4f, bubbleX, bubbleY, bubbleWidth, bubbleHeight, bgColor, borderColor, alpha);
                     boolean showArrow = i == bubbles.size() - 1;
                     if (showArrow) {
                        float arrowScale = 0.6F;
                        float scaledW = 12.0F * arrowScale;
                        float scaledH = 7.0F * arrowScale;
                        float arrowX = -scaledW / 2.0F;
                        float arrowY = bubbleY + bubbleHeight - 2.0F;
                        renderArrow(vertexConsumer, matrix4f, arrowX, arrowY, scaledW, scaledH, bgColor, borderColor, alpha);
                     }

                     currentY -= (float)(bubbleHeight + spacing);
                  } else {
                     bubbles.remove(i);
                  }
               }

               poseStack.translate(1.0, 0.0, 0.0);
               currentY = 0.0F;

               for (int i = bubbles.size() - 1; i >= 0; i--) {
                  Bubble bubble = bubbles.get(i);
                  long age = gameTime - bubble.startTime();
                  if (age < LIFETIME) {
                     Font font = var42.font;
                     List<FormattedCharSequence> lines = bubble.getLayout(font, 150);
                     int textHeight = bubble.getHeight();
                     float bubbleHeight = textHeight + B_PADDING * 2;
                     float bubbleY = currentY - bubbleHeight;
                     float lineY = bubbleY + B_PADDING;

                     for (FormattedCharSequence line : lines) {
                        float centeredLineX = -bubble.getWidth() / 2.0F;
                        font.drawInBatch(line, centeredLineX, lineY, textColor, false, matrix4f, bufferSource, DisplayMode.NORMAL, 0, 15728880);
                        lineY += 9.0F;
                     }

                     currentY = (float)(currentY - (bubbleHeight + spacing));
                  }
               }

               poseStack.popPose();
               profiler.pop();
            }
         }
      }
   }

   private static void renderBubble(
      VertexConsumer vertexConsumer,
      Matrix4f matrix4f,
      float bubbleX,
      float bubbleY,
      float bubbleWidth,
      float bubbleHeight,
      Color bgColor,
      Color borderColor,
      float alpha
   ) {
      float zBg = -0.04F;
      float zBorder = -0.03F;
      drawNineSlice(vertexConsumer, matrix4f, bubbleX, bubbleY, bubbleWidth, bubbleHeight, 0, 32, alpha, zBg, bgColor.r(), bgColor.g(), bgColor.b());
      drawNineSlice(
         vertexConsumer, matrix4f, bubbleX, bubbleY, bubbleWidth, bubbleHeight, 0, 0, alpha, zBorder, borderColor.r(), borderColor.g(), borderColor.b()
      );
   }

   private static void renderArrow(
      VertexConsumer vertexConsumer, Matrix4f matrix4f, float arrowX, float arrowY, float w, float h, Color bgColor, Color borderColor, float alpha
   ) {
      float zArrowBg = -0.02F;
      float zArrowBorder = -0.01F;
      drawQuad(
         vertexConsumer, matrix4f, arrowX, arrowY, arrowX + w, arrowY + h, 48.0F, 7.0F, 60.0F, 14.0F, alpha, zArrowBg, bgColor.r(), bgColor.g(), bgColor.b()
      );
      drawQuad(
         vertexConsumer,
         matrix4f,
         arrowX,
         arrowY,
         arrowX + w,
         arrowY + h,
         48.0F,
         0.0F,
         60.0F,
         7.0F,
         alpha,
         zArrowBorder,
         borderColor.r(),
         borderColor.g(),
         borderColor.b()
      );
   }

   private static void drawNineSlice(
      VertexConsumer vc, Matrix4f mat, float x, float y, float w, float h, int uOff, int vOff, float alpha, float z, float r, float g, float b
   ) {
      float[] xP = new float[]{x, x + 7.0F, x + w - 7.0F, x + w};
      float[] yP = new float[]{y, y + 7.0F, y + h - 7.0F, y + h};
      float u0 = uOff / 64.0F;
      float u1 = (uOff + 7) / 64.0F;
      float u2 = (uOff + 48 - 7) / 64.0F;
      float u3 = (uOff + 48) / 64.0F;
      float v0 = vOff / 64.0F;
      float v1 = (vOff + 7) / 64.0F;
      float v2 = (vOff + 32 - 7) / 64.0F;
      float v3 = (vOff + 32) / 64.0F;
      float[] uU = new float[]{u0, u1, u2, u3};
      float[] vV = new float[]{v0, v1, v2, v3};

      for (int right = 0; right < 3; right++) {
         for (int c = 0; c < 3; c++) {
            drawQuad(
               vc,
               mat,
               xP[c],
               yP[right],
               xP[c + 1],
               yP[right + 1],
               uU[c] * 64.0F,
               vV[right] * 64.0F,
               uU[c + 1] * 64.0F,
               vV[right + 1] * 64.0F,
               alpha,
               z,
               r,
               g,
               b
            );
         }
      }
   }

   private static void drawQuad(
      VertexConsumer vc,
      Matrix4f mat,
      float x1,
      float y1,
      float x2,
      float y2,
      float u1,
      float v1,
      float u2,
      float v2,
      float alpha,
      float z,
      float r,
      float g,
      float b
   ) {
      vc.addVertex(mat, x1, y1, z)
         .setColor(r, g, b, alpha)
         .setUv(u1 / 64.0F, v1 / 64.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 0.0F, 1.0F);
      vc.addVertex(mat, x1, y2, z)
         .setColor(r, g, b, alpha)
         .setUv(u1 / 64.0F, v2 / 64.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 0.0F, 1.0F);
      vc.addVertex(mat, x2, y2, z)
         .setColor(r, g, b, alpha)
         .setUv(u2 / 64.0F, v2 / 64.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 0.0F, 1.0F);
      vc.addVertex(mat, x2, y1, z)
         .setColor(r, g, b, alpha)
         .setUv(u2 / 64.0F, v1 / 64.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 0.0F, 1.0F);
   }
}
