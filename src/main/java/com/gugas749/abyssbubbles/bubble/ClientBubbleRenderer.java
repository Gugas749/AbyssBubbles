package com.gugas749.abyssbubbles.bubble;

import com.gugas749.abyssbubbles.config.BubbleConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.*;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientBubbleRenderer {

    private static final Map<UUID, Deque<BubbleEntry>> bubbleQueues = new HashMap<>();
    private static final Map<UUID, BubbleEntry> activeBubbles = new HashMap<>();
    private static final Set<UUID> activeEntities = new HashSet<>();
    private static final Map<UUID, BubbleConfig> localConfigs = new HashMap<>();

    private static final double FULL_TEXT_RANGE = 16.0;
    private static final double MAX_RENDER_RANGE = 32.0;

    // -------------------------------------------------------------------------
    // API called by packets
    // -------------------------------------------------------------------------

    public static void enqueueBubble(UUID entityUUID, BubbleEntry entry) {
        bubbleQueues.computeIfAbsent(entityUUID, k -> new ArrayDeque<>()).addLast(entry);
    }

    public static void setActive(UUID uuid, boolean active) {
        if (active) activeEntities.add(uuid);
        else {
            activeEntities.remove(uuid);
            bubbleQueues.remove(uuid);
            activeBubbles.remove(uuid);
        }
    }

    public static void updateLocalConfig(UUID uuid, BubbleConfig config) {
        localConfigs.put(uuid, config);
    }

    // -------------------------------------------------------------------------
    // Tick — advance active bubbles and pop queue
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onClientTick(LevelTickEvent.Post event) {
        if (Minecraft.getInstance().level == null) return;

        for (UUID uuid : new HashSet<>(bubbleQueues.keySet())) {
            BubbleEntry current = activeBubbles.get(uuid);

            if (current == null || current.isExpired()) {
                // Pop next from queue
                Deque<BubbleEntry> queue = bubbleQueues.get(uuid);
                if (queue != null && !queue.isEmpty()) {
                    activeBubbles.put(uuid, queue.pollFirst());
                } else {
                    activeBubbles.remove(uuid);
                }
            } else {
                current.tick();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Render — hooked via RenderNameTagEvent
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onRenderNameTag(RenderNameTagEvent event) {
        Entity entity = event.getEntity();
        UUID uuid = entity.getUUID();

        BubbleEntry entry = activeBubbles.get(uuid);
        if (entry == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.gameRenderer == null) return;

        double dist = mc.player.distanceTo(entity);
        if (dist > MAX_RENDER_RANGE) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();
        Font font = mc.font;

        // Determine display text
        String displayText;
        if (dist > FULL_TEXT_RANGE) {
            displayText = truncate(entry.getText());
        } else {
            displayText = entry.getText();
        }

        float alpha = entry.getAlpha();
        BubbleConfig config = entry.getConfig();

        renderBubble(poseStack, bufferSource, font, displayText, config, alpha, event.getPackedLight());
    }

    // -------------------------------------------------------------------------
    // Core bubble render
    // -------------------------------------------------------------------------

    private static void renderBubble(PoseStack poseStack, MultiBufferSource bufferSource,
                                     Font font, String text, BubbleConfig config,
                                     float alpha, int packedLight) {
        poseStack.pushPose();

        // Position above the entity's head (name tag sits around y=0.5 above bounding box)
        poseStack.translate(0, 0.5, 0);

        // Billboard — face the camera
        poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        poseStack.scale(-0.025f, -0.025f, 0.025f);

        int maxWidth = config.getMaxWidth();

        // Word-wrap the text
        List<String> lines = wordWrap(text, font, maxWidth);

        int lineHeight = font.lineHeight + 2;
        int totalHeight = lines.size() * lineHeight + 6;
        int boxWidth = 0;
        for (String line : lines) boxWidth = Math.max(boxWidth, font.width(line));
        boxWidth += 10;

        int bgColor = applyAlpha(config.getBackgroundColor(), alpha);
        int borderColor = applyAlpha(config.getBorderColor(), alpha);
        int textColor = applyAlpha(config.getTextColor(), alpha);

        int left = -boxWidth / 2;
        int top = -totalHeight;

        // Draw background box via fill
        // NeoForge 1.21.1: use GuiGraphics equivalent — draw via RenderType quads
        drawFilledRect(poseStack, bufferSource, left - 2, top - 2, left + boxWidth + 2, top + totalHeight + 2, borderColor, packedLight);
        drawFilledRect(poseStack, bufferSource, left, top, left + boxWidth, top + totalHeight, bgColor, packedLight);

        // Draw tail (simple downward triangle)
        drawTail(poseStack, bufferSource, bgColor, borderColor, packedLight);

        // Draw text lines
        int y = top + 3;
        for (String line : lines) {
            int x = left + (boxWidth - font.width(line)) / 2;
            font.drawInBatch(line, x, y, textColor, false, poseStack.last().pose(),
                    bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
            y += lineHeight;
        }

        poseStack.popPose();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static void drawFilledRect(PoseStack poseStack, MultiBufferSource bufferSource,
                                       int x0, int y0, int x1, int y1, int color, int packedLight) {
        var buffer = bufferSource.getBuffer(net.minecraft.client.renderer.RenderType.gui());
        var matrix = poseStack.last().pose();
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        buffer.addVertex(matrix, x0, y0, 0).setColor(r, g, b, a).setLight(packedLight);
        buffer.addVertex(matrix, x0, y1, 0).setColor(r, g, b, a).setLight(packedLight);
        buffer.addVertex(matrix, x1, y1, 0).setColor(r, g, b, a).setLight(packedLight);
        buffer.addVertex(matrix, x1, y0, 0).setColor(r, g, b, a).setLight(packedLight);
    }

    private static void drawTail(PoseStack poseStack, MultiBufferSource bufferSource,
                                 int bgColor, int borderColor, int packedLight) {
        // Small downward-pointing triangle beneath the box (pointing toward the entity's head)
        int tailW = 5;
        int tailH = 5;
        // Border triangle
        drawTriangle(poseStack, bufferSource, -tailW - 1, 1, tailW + 1, 1, 0, tailH + 1, borderColor, packedLight);
        // Fill triangle
        drawTriangle(poseStack, bufferSource, -tailW, 1, tailW, 1, 0, tailH, bgColor, packedLight);
    }

    private static void drawTriangle(PoseStack poseStack, MultiBufferSource bufferSource,
                                     float x0, float y0, float x1, float y1, float x2, float y2,
                                     int color, int packedLight) {
        var buffer = bufferSource.getBuffer(net.minecraft.client.renderer.RenderType.gui());
        var matrix = poseStack.last().pose();
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        buffer.addVertex(matrix, x0, y0, 0).setColor(r, g, b, a).setLight(packedLight);
        buffer.addVertex(matrix, x2, y2, 0).setColor(r, g, b, a).setLight(packedLight);
        buffer.addVertex(matrix, x1, y1, 0).setColor(r, g, b, a).setLight(packedLight);
        buffer.addVertex(matrix, x0, y0, 0).setColor(r, g, b, a).setLight(packedLight);
    }

    private static List<String> wordWrap(String text, Font font, int maxWidth) {
        List<String> result = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            String test = current.isEmpty() ? word : current + " " + word;
            if (font.width(test) > maxWidth && !current.isEmpty()) {
                result.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(test);
            }
        }
        if (!current.isEmpty()) result.add(current.toString());
        return result;
    }

    private static String truncate(String text) {
        if (text.length() <= 6) return text + "...";
        return text.substring(0, 6) + "...";
    }

    private static int applyAlpha(int argb, float alpha) {
        int a = (int) (((argb >> 24) & 0xFF) * alpha);
        return (argb & 0x00FFFFFF) | (a << 24);
    }
}
