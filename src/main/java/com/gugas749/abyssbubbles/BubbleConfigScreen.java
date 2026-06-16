package com.gugas749.abyssbubbles;

import com.gugas749.abyssbubbles.config.BubbleConfig;
import com.gugas749.abyssbubbles.network.BubbleUpdateConfigPacket;
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
    private static final int PREVIEW_SIZE = 80;

    private final BubbleConfig workingCopy;

    // Input fields
    private EditBox bgColorField;
    private EditBox borderColorField;
    private EditBox textColorField;
    private EditBox verticalOffsetField;
    private EditBox importExportField;

    private String errorMessage = null;

    public BubbleConfigScreen(BubbleConfig current) {
        super(Component.translatable("abyssbubbles.screen.title"));
        this.workingCopy = current.copy();
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int startY = 40;
        int rowH = 28;

        // --- Background color ---
        bgColorField = new EditBox(font, centerX - FIELD_W / 2, startY + rowH, FIELD_W, 18,
                Component.translatable("abyssbubbles.screen.bg_color"));
        bgColorField.setMaxLength(8);
        bgColorField.setValue(toHex(workingCopy.getBackgroundColor()));
        bgColorField.setResponder(v -> errorMessage = null);
        addRenderableWidget(bgColorField);

        // --- Border color ---
        borderColorField = new EditBox(font, centerX - FIELD_W / 2, startY + rowH * 3, FIELD_W, 18,
                Component.translatable("abyssbubbles.screen.border_color"));
        borderColorField.setMaxLength(8);
        borderColorField.setValue(toHex(workingCopy.getBorderColor()));
        borderColorField.setResponder(v -> errorMessage = null);
        addRenderableWidget(borderColorField);

        // --- Text color ---
        textColorField = new EditBox(font, centerX - FIELD_W / 2, startY + rowH * 5, FIELD_W, 18,
                Component.translatable("abyssbubbles.screen.text_color"));
        textColorField.setMaxLength(8);
        textColorField.setValue(toHex(workingCopy.getTextColor()));
        textColorField.setResponder(v -> errorMessage = null);
        addRenderableWidget(textColorField);

        // --- Vertical offset ---
        EditBox offsetField = new EditBox(font, centerX - FIELD_W / 2, startY + rowH * 7, FIELD_W, 18,
                Component.translatable("abyssbubbles.screen.vertical_offset"));
        offsetField.setMaxLength(6);
        offsetField.setValue(String.valueOf(workingCopy.getVerticalOffset()));
        offsetField.setResponder(v -> errorMessage = null);
        verticalOffsetField = offsetField;
        addRenderableWidget(verticalOffsetField);

        // --- Import / Export field ---
        importExportField = new EditBox(font, centerX - 100, startY + rowH * 10, 200, 18,
                Component.literal("Import/Export"));
        importExportField.setMaxLength(512);
        importExportField.setValue("");
        addRenderableWidget(importExportField);

        // --- Buttons ---
        int btnY = startY + rowH * 12;

        // Save
        addRenderableWidget(Button.builder(
                Component.translatable("abyssbubbles.screen.save"), btn -> save())
                .bounds(centerX - 110, btnY, 50, 20).build());

        // Export
        addRenderableWidget(Button.builder(
                Component.translatable("abyssbubbles.screen.export"), btn -> export())
                .bounds(centerX - 55, btnY, 50, 20).build());

        // Import
        addRenderableWidget(Button.builder(
                Component.translatable("abyssbubbles.screen.import"), btn -> importConfig())
                .bounds(centerX, btnY, 50, 20).build());

        // Cancel
        addRenderableWidget(Button.builder(
                Component.translatable("abyssbubbles.screen.cancel"), btn -> onClose())
                .bounds(centerX + 55, btnY, 50, 20).build());
    }

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    private void save() {
        if (!applyFields()) return;
        PacketDistributor.sendToServer(new BubbleUpdateConfigPacket(workingCopy.copy()));
        onClose();
    }

    private void export() {
        if (!applyFields()) return;
        String encoded = workingCopy.toExportString();
        importExportField.setValue(encoded);
        // Also copy to clipboard
        assert minecraft != null;
        minecraft.keyboardHandler.setClipboard(encoded);
    }

    private void importConfig() {
        String raw = importExportField.getValue().trim();
        if (raw.isEmpty()) {
            errorMessage = "abyssbubbles.screen.error.empty_import";
            return;
        }
        BubbleConfig imported = BubbleConfig.fromExportString(raw);
        if (imported == null) {
            errorMessage = "abyssbubbles.screen.error.invalid_import";
            return;
        }
        // Apply imported values to fields
        bgColorField.setValue(toHex(imported.getBackgroundColor()));
        borderColorField.setValue(toHex(imported.getBorderColor()));
        textColorField.setValue(toHex(imported.getTextColor()));
        verticalOffsetField.setValue(String.valueOf(imported.getVerticalOffset()));
        errorMessage = null;
    }

    private boolean applyFields() {
        try {
            int bg = parseHex(bgColorField.getValue());
            workingCopy.setBackgroundColor(bg);
        } catch (Exception e) {
            errorMessage = "abyssbubbles.screen.error.invalid_bg";
            return false;
        }

        try {
            int border = parseHex(borderColorField.getValue());
            workingCopy.setBorderColor(border);
        } catch (Exception e) {
            errorMessage = "abyssbubbles.screen.error.invalid_border";
            return false;
        }

        try {
            int text = parseHex(textColorField.getValue());
            workingCopy.setTextColor(text);
        } catch (Exception e) {
            errorMessage = "abyssbubbles.screen.error.invalid_text";
            return false;
        }

        try {
            float offset = Float.parseFloat(verticalOffsetField.getValue());
            offset = Math.max(-2.0f, Math.min(5.0f, offset));
            workingCopy.setVerticalOffset(offset);
        } catch (Exception e) {
            errorMessage = "abyssbubbles.screen.error.invalid_offset";
            return false;
        }

        return true;
    }

    // -------------------------------------------------------------------------
    // Render
    // -------------------------------------------------------------------------

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx, mouseX, mouseY, partialTick);

        int centerX = width / 2;
        int startY = 40;
        int rowH = 28;

        // Title
        gfx.drawCenteredString(font, title, centerX, 12, 0xFFFFFFFF);

        // Field labels
        gfx.drawString(font, Component.translatable("abyssbubbles.screen.bg_color"),
                centerX - FIELD_W / 2, startY + rowH - 10, LABEL_COLOR, false);
        gfx.drawString(font, Component.translatable("abyssbubbles.screen.border_color"),
                centerX - FIELD_W / 2, startY + rowH * 3 - 10, LABEL_COLOR, false);
        gfx.drawString(font, Component.translatable("abyssbubbles.screen.text_color"),
                centerX - FIELD_W / 2, startY + rowH * 5 - 10, LABEL_COLOR, false);
        gfx.drawString(font, Component.translatable("abyssbubbles.screen.vertical_offset"),
                centerX - FIELD_W / 2, startY + rowH * 7 - 10, LABEL_COLOR, false);
        gfx.drawString(font, Component.translatable("abyssbubbles.screen.import_export"),
                centerX - 100, startY + rowH * 10 - 10, LABEL_COLOR, false);

        // Color preview squares
        drawColorPreview(gfx, parseHexSafe(bgColorField.getValue(), workingCopy.getBackgroundColor()),
                centerX + FIELD_W / 2 + 6, startY + rowH);
        drawColorPreview(gfx, parseHexSafe(borderColorField.getValue(), workingCopy.getBorderColor()),
                centerX + FIELD_W / 2 + 6, startY + rowH * 3);
        drawColorPreview(gfx, parseHexSafe(textColorField.getValue(), workingCopy.getTextColor()),
                centerX + FIELD_W / 2 + 6, startY + rowH * 5);

        // Error message
        if (errorMessage != null) {
            gfx.drawCenteredString(font, Component.translatable(errorMessage),
                    centerX, startY + rowH * 12 + 26, ERROR_COLOR);
        }

        super.render(gfx, mouseX, mouseY, partialTick);
    }

    private void drawColorPreview(GuiGraphics gfx, int color, int x, int y) {
        // Border
        gfx.fill(x - 1, y - 1, x + 19, y + 19, 0xFF000000);
        // Color fill (ignore alpha for preview, force full opacity)
        gfx.fill(x, y, x + 18, y + 18, color | 0xFF000000);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static String toHex(int argb) {
        return String.format("%08X", argb);
    }

    private static int parseHex(String hex) {
        hex = hex.trim().replace("#", "");
        if (hex.length() == 6) hex = "FF" + hex; // Add full alpha if not provided
        if (hex.length() != 8) throw new IllegalArgumentException("Invalid hex");
        return (int) Long.parseLong(hex, 16);
    }

    private static int parseHexSafe(String hex, int fallback) {
        try { return parseHex(hex); } catch (Exception e) { return fallback; }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
