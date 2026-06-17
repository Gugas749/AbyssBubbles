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

    private static final int FIELD_W = 120;
    private static final int LABEL_COLOR = 0xFFDDDDDD;
    private static final int ERROR_COLOR = 0xFFFF5555;

    // Working values
    private Color bgColor;
    private Color borderColor;
    private int textColor;
    private double offset;
    private double spacing;
    private boolean hideNametag;

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

    @Override
    protected void init() {
        int cx = width / 2;
        int y = 40;
        int rh = 28;

        // Background color
        bgColorField = new EditBox(font, cx - FIELD_W / 2, y + rh, FIELD_W, 18,
                Component.translatable("abyssbubbles.screen.bg_color"));
        bgColorField.setMaxLength(8);
        bgColorField.setValue(colorToHex(bgColor));
        bgColorField.setResponder(v -> errorMessage = null);
        addRenderableWidget(bgColorField);

        // Border color
        borderColorField = new EditBox(font, cx - FIELD_W / 2, y + rh * 3, FIELD_W, 18,
                Component.translatable("abyssbubbles.screen.border_color"));
        borderColorField.setMaxLength(8);
        borderColorField.setValue(colorToHex(borderColor));
        borderColorField.setResponder(v -> errorMessage = null);
        addRenderableWidget(borderColorField);

        // Text color
        textColorField = new EditBox(font, cx - FIELD_W / 2, y + rh * 5, FIELD_W, 18,
                Component.translatable("abyssbubbles.screen.text_color"));
        textColorField.setMaxLength(8);
        textColorField.setValue(intToHex(textColor));
        textColorField.setResponder(v -> errorMessage = null);
        addRenderableWidget(textColorField);

        // Offset
        offsetField = new EditBox(font, cx - FIELD_W / 2, y + rh * 7, FIELD_W, 18,
                Component.translatable("abyssbubbles.screen.vertical_offset"));
        offsetField.setMaxLength(8);
        offsetField.setValue(String.valueOf(offset));
        offsetField.setResponder(v -> errorMessage = null);
        addRenderableWidget(offsetField);

        // Spacing
        spacingField = new EditBox(font, cx - FIELD_W / 2, y + rh * 9, FIELD_W, 18,
                Component.translatable("abyssbubbles.screen.spacing"));
        spacingField.setMaxLength(6);
        spacingField.setValue(String.valueOf(spacing));
        spacingField.setResponder(v -> errorMessage = null);
        addRenderableWidget(spacingField);

        // Import/Export
        importExportField = new EditBox(font, cx - 100, y + rh * 11, 200, 18,
                Component.literal("Import/Export"));
        importExportField.setMaxLength(512);
        addRenderableWidget(importExportField);

        // Buttons
        int btnY = y + rh * 13;
        addRenderableWidget(Button.builder(Component.translatable("abyssbubbles.screen.save"),
                btn -> save()).bounds(cx - 110, btnY, 50, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("abyssbubbles.screen.export"),
                btn -> export()).bounds(cx - 55, btnY, 50, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("abyssbubbles.screen.import"),
                btn -> importConfig()).bounds(cx, btnY, 50, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("abyssbubbles.screen.cancel"),
                btn -> onClose()).bounds(cx + 55, btnY, 50, 20).build());
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
        // Simple CSV export: bgRGB,borderRGB,textInt,offset,spacing,hideNametag
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
            String[] parts = raw.split(",");
            if (parts.length != 6) throw new IllegalArgumentException();
            bgColorField.setValue(parts[0]);
            borderColorField.setValue(parts[1]);
            textColorField.setValue(parts[2]);
            offsetField.setValue(parts[3]);
            spacingField.setValue(parts[4]);
            errorMessage = null;
        } catch (Exception e) {
            errorMessage = "abyssbubbles.screen.error.invalid_import";
        }
    }

    private boolean applyFields() {
        try {
            bgColor = hexToColor(bgColorField.getValue());
        } catch (Exception e) { errorMessage = "abyssbubbles.screen.error.invalid_bg"; return false; }

        try {
            borderColor = hexToColor(borderColorField.getValue());
        } catch (Exception e) { errorMessage = "abyssbubbles.screen.error.invalid_border"; return false; }

        try {
            textColor = parseHexInt(textColorField.getValue());
        } catch (Exception e) { errorMessage = "abyssbubbles.screen.error.invalid_text"; return false; }

        try {
            offset = Math.max(-2.0, Math.min(50.0, Double.parseDouble(offsetField.getValue())));
        } catch (Exception e) { errorMessage = "abyssbubbles.screen.error.invalid_offset"; return false; }

        try {
            spacing = Math.max(1.0, Math.min(10.0, Double.parseDouble(spacingField.getValue())));
        } catch (Exception e) { errorMessage = "abyssbubbles.screen.error.invalid_spacing"; return false; }

        return true;
    }

    // -------------------------------------------------------------------------
    // Render
    // -------------------------------------------------------------------------

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx, mouseX, mouseY, partialTick);
        int cx = width / 2;
        int y = 40;
        int rh = 28;

        gfx.drawCenteredString(font, title, cx, 12, 0xFFFFFFFF);

        gfx.drawString(font, Component.translatable("abyssbubbles.screen.bg_color"),     cx - FIELD_W / 2, y + rh - 10,      LABEL_COLOR, false);
        gfx.drawString(font, Component.translatable("abyssbubbles.screen.border_color"), cx - FIELD_W / 2, y + rh * 3 - 10,  LABEL_COLOR, false);
        gfx.drawString(font, Component.translatable("abyssbubbles.screen.text_color"),   cx - FIELD_W / 2, y + rh * 5 - 10,  LABEL_COLOR, false);
        gfx.drawString(font, Component.translatable("abyssbubbles.screen.vertical_offset"), cx - FIELD_W / 2, y + rh * 7 - 10, LABEL_COLOR, false);
        gfx.drawString(font, Component.translatable("abyssbubbles.screen.spacing"),      cx - FIELD_W / 2, y + rh * 9 - 10,  LABEL_COLOR, false);
        gfx.drawString(font, Component.translatable("abyssbubbles.screen.import_export"), cx - 100,        y + rh * 11 - 10, LABEL_COLOR, false);

        // Color previews
        drawColorPreview(gfx, colorToArgb(bgColor),     cx + FIELD_W / 2 + 6, y + rh);
        drawColorPreview(gfx, colorToArgb(borderColor), cx + FIELD_W / 2 + 6, y + rh * 3);
        drawColorPreview(gfx, textColor | 0xFF000000,   cx + FIELD_W / 2 + 6, y + rh * 5);

        if (errorMessage != null) {
            gfx.drawCenteredString(font, Component.translatable(errorMessage),
                    cx, y + rh * 13 + 26, ERROR_COLOR);
        }

        super.render(gfx, mouseX, mouseY, partialTick);
    }

    private void drawColorPreview(GuiGraphics gfx, int argb, int x, int y) {
        gfx.fill(x - 1, y - 1, x + 19, y + 19, 0xFF000000);
        gfx.fill(x, y, x + 18, y + 18, argb | 0xFF000000);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float delta) {
    }

    // -------------------------------------------------------------------------
    // Color helpers
    // -------------------------------------------------------------------------

    /** Color (RGB floats) -> 6-char hex string */
    private static String colorToHex(Color c) {
        int r = Math.round(c.r() * 255);
        int g = Math.round(c.g() * 255);
        int b = Math.round(c.b() * 255);
        return String.format("%02X%02X%02X", r, g, b);
    }

    private static int colorToArgb(Color c) {
        int r = Math.round(c.r() * 255);
        int g = Math.round(c.g() * 255);
        int b = Math.round(c.b() * 255);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    /** 6-char hex -> Color */
    private static Color hexToColor(String hex) {
        hex = hex.trim().replace("#", "");
        if (hex.length() == 6) {
            int rgb = (int) Long.parseLong(hex, 16);
            float r = ((rgb >> 16) & 0xFF) / 255f;
            float g = ((rgb >> 8)  & 0xFF) / 255f;
            float b = ( rgb        & 0xFF) / 255f;
            return new Color(r, g, b);
        }
        throw new IllegalArgumentException("Invalid color hex: " + hex);
    }

    /** int textColor -> 6 or 8 char hex */
    private static String intToHex(int color) {
        return String.format("%06X", color & 0xFFFFFF);
    }

    private static int parseHexInt(String hex) {
        hex = hex.trim().replace("#", "");
        return (int) Long.parseLong(hex, 16);
    }
}
