package com.gugas749.abyssbubbles.client.screen.config;

import com.gugas749.abyssbubbles.network.BubbleConfigUpdatePacket;
import com.gugas749.abyssbubbles.network.OpenBubbleScreenPacket;
import com.gugas749.abyssbubbles.util.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class BubbleConfigScreen extends Screen {

    private static final int PANEL_PAD  = 8;
    private static final int ROW_H      = 30;
    private static final int FIELD_W    = 130;
    private static final int LABEL_COL  = 0xFFDDDDDD;
    private static final int ERROR_COL  = 0xFFFF5555;
    private static final int HINT_COL   = 0xFF888888;

    // Panel geometry — computed once, shared between init() and render()
    private static final int PANEL_X    = 0; // resolved at runtime via width
    private static final int PANEL_W    = 240;
    private static final int PANEL_Y    = PANEL_PAD;
    private static final int TITLE_H    = 20; // title text + divider + gap
    private static final int FIRST_ROW_Y = PANEL_Y + TITLE_H + PANEL_PAD; // 36

    // Working state
    private Color   bgColor;
    private Color   borderColor;
    private int     textColor;
    private double  offset;
    private double  spacing;
    private boolean hideNametag;

    // Fields
    private EditBox bgColorField;
    private EditBox borderColorField;
    private EditBox textColorField;
    private EditBox offsetField;
    private EditBox spacingField;
    private EditBox importExportField;

    private String errorMessage = null;

    public BubbleConfigScreen(OpenBubbleScreenPacket packet) {
        super(Component.translatable("abyssbubbles.screen.title"));
        this.bgColor     = packet.bgColor();
        this.borderColor = packet.borderColor();
        this.textColor   = packet.textColor();
        this.offset      = packet.offset();
        this.spacing     = packet.spacing();
        this.hideNametag = packet.hideNametag();
    }

    // Convenience: panel left edge
    private int panelX() { return width / 2 - PANEL_W / 2; }
    private int labelX() { return panelX() + PANEL_PAD; }
    private int fieldX() { return labelX() + 80; }

    @Override
    protected void init() {
        int fx = fieldX();
        int y  = FIRST_ROW_Y;

        bgColorField = field(fx, y, colorToHex(bgColor));
        addRenderableWidget(bgColorField);
        y += ROW_H;
        borderColorField = field(fx, y, colorToHex(borderColor));
        addRenderableWidget(borderColorField);
        y += ROW_H;
        textColorField = field(fx, y, intToHex(textColor));
        addRenderableWidget(textColorField);
        y += ROW_H;

        offsetField = field(fx, y, String.valueOf(offset));
        offsetField.setMaxLength(8);
        addRenderableWidget(offsetField);
        y += ROW_H;

        spacingField = field(fx, y, String.valueOf(spacing));
        spacingField.setMaxLength(6);
        addRenderableWidget(spacingField);
        y += ROW_H + PANEL_PAD;

        importExportField = new EditBox(font, panelX() + PANEL_PAD, y, PANEL_W - PANEL_PAD * 2, 16, Component.literal(""));
        importExportField.setMaxLength(512);
        importExportField.setHint(Component.literal("Paste export string here...").withStyle(s -> s.withColor(0x888888)));
        addRenderableWidget(importExportField);
        y += 24;

        int bx = panelX() + PANEL_PAD;
        addRenderableWidget(Button.builder(Component.translatable("abyssbubbles.screen.save"),   btn -> save()).bounds(bx,       y, 54, 18).build());
        addRenderableWidget(Button.builder(Component.translatable("abyssbubbles.screen.export"), btn -> export()).bounds(bx + 58, y, 54, 18).build());
        addRenderableWidget(Button.builder(Component.translatable("abyssbubbles.screen.import"), btn -> importConfig()).bounds(bx + 116, y, 54, 18).build());
        addRenderableWidget(Button.builder(Component.translatable("abyssbubbles.screen.cancel"), btn -> onClose()).bounds(bx + 174, y, 54, 18).build());
    }

    private EditBox field(int x, int y, String value) {
        EditBox box = new EditBox(font, x, y + 10, FIELD_W, 16, Component.literal(""));
        box.setMaxLength(16);
        box.setValue(value);
        box.setResponder(v -> errorMessage = null);
        return box;
    }

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    private void save() {
        if (!applyFields()) return;
        PacketDistributor.sendToServer(new BubbleConfigUpdatePacket(
                bgColor, borderColor, textColor, offset, spacing, hideNametag));
        onClose();
    }

    private void export() {
        if (!applyFields()) return;
        String encoded = colorToHex(bgColor) + "," + colorToHex(borderColor) + ","
                + intToHex(textColor) + "," + offset + "," + spacing + "," + hideNametag;
        importExportField.setValue(encoded);
        assert minecraft != null;
        minecraft.keyboardHandler.setClipboard(encoded);
    }

    private void importConfig() {
        String raw = importExportField.getValue().trim();
        if (raw.isEmpty()) { errorMessage = "abyssbubbles.screen.error.empty_import"; return; }
        try {
            String[] p = raw.split(",");
            if (p.length != 6) throw new IllegalArgumentException();
            bgColorField.setValue(p[0]);
            borderColorField.setValue(p[1]);
            textColorField.setValue(p[2]);
            offsetField.setValue(p[3]);
            spacingField.setValue(p[4]);
            errorMessage = null;
        } catch (Exception e) {
            errorMessage = "abyssbubbles.screen.error.invalid_import";
        }
    }

    private boolean applyFields() {
        try { bgColor     = hexToColor(bgColorField.getValue());
        } catch (Exception e) { errorMessage = "abyssbubbles.screen.error.invalid_bg";      return false; }
        try { borderColor = hexToColor(borderColorField.getValue());
        } catch (Exception e) { errorMessage = "abyssbubbles.screen.error.invalid_border";  return false; }
        try { textColor   = parseHexInt(textColorField.getValue());
        } catch (Exception e) { errorMessage = "abyssbubbles.screen.error.invalid_text";    return false; }
        try { offset  = Math.max(-2.0, Math.min(50.0, Double.parseDouble(offsetField.getValue())));
        } catch (Exception e) { errorMessage = "abyssbubbles.screen.error.invalid_offset";  return false; }
        try { spacing = Math.max(1.0,  Math.min(10.0, Double.parseDouble(spacingField.getValue())));
        } catch (Exception e) { errorMessage = "abyssbubbles.screen.error.invalid_spacing"; return false; }
        return true;
    }

    // -------------------------------------------------------------------------
    // Render
    // -------------------------------------------------------------------------

    @Override
    public void render(GuiGraphics g, int mx, int my, float delta) {
        renderBackground(g, mx, my, delta);

        int px = panelX();
        int lx = labelX();

        // Panel height = title + rows + import label + import field + buttons + padding
        int panelH = TITLE_H + PANEL_PAD + 5 * ROW_H + PANEL_PAD + 10 + 24 + 22 + PANEL_PAD;

        g.fill(px, PANEL_Y, px + PANEL_W, PANEL_Y + panelH, 0xCC111111);
        g.renderOutline(px, PANEL_Y, PANEL_W, panelH, 0xFF555555);

        // Title + divider
        g.drawCenteredString(font, title, width / 2, PANEL_Y + 5, 0xFFFFFF);
        g.fill(px, PANEL_Y + 14, px + PANEL_W, PANEL_Y + 15, 0xFF555555);

        // Labels — same y as fields
        int rowY = FIRST_ROW_Y;
        rowY = renderRow(g, lx, rowY, "abyssbubbles.screen.bg_color",        colorToArgb(bgColor));
        rowY = renderRow(g, lx, rowY, "abyssbubbles.screen.border_color",    colorToArgb(borderColor));
        rowY = renderRow(g, lx, rowY, "abyssbubbles.screen.text_color",      textColor | 0xFF000000);
        rowY = renderRowNoPreview(g, lx, rowY, "abyssbubbles.screen.vertical_offset");
        rowY = renderRowNoPreview(g, lx, rowY, "abyssbubbles.screen.spacing");

        rowY += PANEL_PAD;
        g.drawString(font, Component.translatable("abyssbubbles.screen.import_export"), lx, rowY, HINT_COL, false);

        if (errorMessage != null) {
            g.drawCenteredString(font, Component.translatable(errorMessage),
                    width / 2, PANEL_Y + panelH - 6, ERROR_COL);
        }

        super.render(g, mx, my, delta);
    }

    private int renderRow(GuiGraphics g, int lx, int rowY, String key, int previewArgb) {
        g.drawString(font, Component.translatable(key), lx, rowY, LABEL_COL, false);
        int px = fieldX() + FIELD_W + 4;
        g.fill(px - 1, rowY - 1, px + 13, rowY + 13, 0xFF000000);
        g.fill(px,     rowY,     px + 12, rowY + 12, previewArgb);
        return rowY + ROW_H;
    }

    private int renderRowNoPreview(GuiGraphics g, int lx, int rowY, String key) {
        g.drawString(font, Component.translatable(key), lx, rowY, LABEL_COL, false);
        return rowY + ROW_H;
    }

    @Override public boolean isPauseScreen() { return false; }
    @Override public void renderBackground(GuiGraphics g, int mx, int my, float delta) {}

    // -------------------------------------------------------------------------
    // Color helpers
    // -------------------------------------------------------------------------

    private static String colorToHex(Color c) {
        return String.format("%02X%02X%02X",
                Math.round(c.r() * 255), Math.round(c.g() * 255), Math.round(c.b() * 255));
    }

    private static int colorToArgb(Color c) {
        return 0xFF000000 | (Math.round(c.r() * 255) << 16) | (Math.round(c.g() * 255) << 8) | Math.round(c.b() * 255);
    }

    private static Color hexToColor(String hex) {
        hex = hex.trim().replace("#", "");
        if (hex.length() == 6) {
            int rgb = (int) Long.parseLong(hex, 16);
            return new Color(((rgb >> 16) & 0xFF) / 255f, ((rgb >> 8) & 0xFF) / 255f, (rgb & 0xFF) / 255f);
        }
        throw new IllegalArgumentException("Invalid hex: " + hex);
    }

    private static String intToHex(int color) { return String.format("%06X", color & 0xFFFFFF); }

    private static int parseHexInt(String hex) { return (int) Long.parseLong(hex.trim().replace("#", ""), 16); }
}
