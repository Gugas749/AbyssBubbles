package com.gugas749.abyssbubbles.client.screen.config;

import com.gugas749.abysscore.api.gui.layout.AbyssLayout;
import com.gugas749.abysscore.api.gui.render.AbyssDraw;
import com.gugas749.abysscore.api.gui.screen.AbyssPanelScreen;
import com.gugas749.abysscore.api.gui.screen.AbyssTab;
import com.gugas749.abysscore.api.gui.screen.TabContext;
import com.gugas749.abysscore.api.gui.theme.AbyssTheme;
import com.gugas749.abysscore.api.gui.widget.AbyssButton;
import com.gugas749.abysscore.api.gui.widget.AbyssSlider;
import com.gugas749.abysscore.api.gui.widget.AbyssTextField;
import com.gugas749.abysscore.api.gui.widget.AbyssToggle;
import com.gugas749.abysscore.api.permission.AbyssPermissionLevel;
import com.gugas749.abyssbubbles.network.BubbleConfigUpdatePacket;
import com.gugas749.abyssbubbles.network.OpenBubbleScreenPacket;
import com.gugas749.abyssbubbles.util.Color;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class BubbleConfigScreen extends AbyssPanelScreen {

    private static final double OFFSET_MIN = -2.0, OFFSET_MAX = 50.0;
    private static final double SPACING_MIN = 1.0, SPACING_MAX = 10.0;

    // Working state (the three colors as the text the player typed)
    private String bgHex, borderHex, textHex;
    private double offset, spacing;
    private boolean hideNametag;
    private String importText = "";
    @Nullable private String errorKey;

    public BubbleConfigScreen(OpenBubbleScreenPacket packet) {
        super(Component.translatable("abyssbubbles.screen.title"));
        this.bgHex       = colorToHex(packet.bgColor());
        this.borderHex   = colorToHex(packet.borderColor());
        this.textHex     = intToHex(packet.textColor());
        this.offset      = packet.offset();
        this.spacing     = packet.spacing();
        this.hideNametag = packet.hideNametag();
    }

    /** Every player with bubble permission uses this — not only staff (the server still checks on save). */
    @Override protected AbyssPermissionLevel requiredLevel() { return AbyssPermissionLevel.PLAYER; }

    @Override protected int panelWidth()  { return 340; }
    @Override protected int panelHeight() { return 260; }

    @Override
    protected void addTabs(List<AbyssTab> tabs) {
        tabs.add(new SettingsTab());
    }

    //-----------------------------------------------------------------------------------
    //                                     THE FORM
    //-----------------------------------------------------------------------------------

    private class SettingsTab implements AbyssTab {

        // Layout (relative to the content area)
        private static final int HEX_W = 64, SWATCH = 14, ROW = 24, MIN_LEFT_W = 170;
        private static final String[] COLOR_LABELS = {
                "abyssbubbles.screen.bg_color", "abyssbubbles.screen.border_color", "abyssbubbles.screen.text_color"};

        /**
         * Label column = the widest color label + a gap, measured with the real font. So the
         * fields never cover their labels — in English, Portuguese or any future language.
         */
        private int labelW(Font font) {
            int widest = 0;
            for (String key : COLOR_LABELS) widest = Math.max(widest, font.width(Component.translatable(key)));
            return widest + 8;
        }

        /** Left column (colors + sliders): wide enough for label + field + swatch. */
        private int leftW(Font font) {
            return Math.max(MIN_LEFT_W, labelW(font) + HEX_W + 6 + SWATCH);
        }
        private static final int COLORS_Y = 0, LAYOUT_Y = 88, IO_Y = 176;

        @Override public Component title() { return Component.translatable("abyssbubbles.screen.title"); }

        @Override
        public void init(TabContext ctx) {
            int fieldX = ctx.x + labelW(ctx.font());
            int leftW = leftW(ctx.font());

            // ── Colors: hex fields (the swatches next to them are drawn in render) ──
            hexField(ctx, fieldX, ctx.y + COLORS_Y + 12, bgHex, v -> bgHex = v);
            hexField(ctx, fieldX, ctx.y + COLORS_Y + 12 + ROW, borderHex, v -> borderHex = v);
            hexField(ctx, fieldX, ctx.y + COLORS_Y + 12 + 2 * ROW, textHex, v -> textHex = v);

            // ── Layout: sliders instead of typing numbers (always in range, 0.5 steps) ──
            var col = AbyssLayout.column(ctx.x, ctx.y + LAYOUT_Y + 12, 4);
            col.add(ctx.add(new AbyssSlider(0, 0, leftW, toSlider(offset, OFFSET_MIN, OFFSET_MAX),
                    v -> Component.translatable("abyssbubbles.screen.vertical_offset")
                            .append(": " + format(fromSlider(v, OFFSET_MIN, OFFSET_MAX))),
                    v -> { offset = fromSlider(v, OFFSET_MIN, OFFSET_MAX); errorKey = null; })));
            col.add(ctx.add(new AbyssSlider(0, 0, leftW, toSlider(spacing, SPACING_MIN, SPACING_MAX),
                    v -> Component.translatable("abyssbubbles.screen.spacing")
                            .append(": " + format(fromSlider(v, SPACING_MIN, SPACING_MAX))),
                    v -> { spacing = fromSlider(v, SPACING_MIN, SPACING_MAX); errorKey = null; })));
            // The old screen had no control for this at all — only the export string carried it
            col.add(ctx.add(new AbyssToggle(0, 0,
                    Component.translatableWithFallback("abyssbubbles.screen.hide_nametag", "Hide nametag while talking"),
                    hideNametag, on -> hideNametag = on)));

            // ── Import / export ──
            int ioY = ctx.y + IO_Y;
            int buttonW = 60;
            int ioFieldW = ctx.width - 2 * (buttonW + 4);
            var io = ctx.add(new AbyssTextField(ctx.x, ioY, ioFieldW,
                    Component.translatableWithFallback("abyssbubbles.screen.import_hint", "Paste export string here...")));
            io.setMaxLength(512);
            io.setValue(importText);
            io.setResponder(v -> { importText = v; errorKey = null; });
            ctx.add(new AbyssButton(ctx.x + ioFieldW + 4, ioY, buttonW,
                    Component.translatable("abyssbubbles.screen.export"), b -> export(ctx)));
            ctx.add(new AbyssButton(ctx.x + ioFieldW + 8 + buttonW, ioY, buttonW,
                    Component.translatable("abyssbubbles.screen.import"), b -> importConfig(ctx)));

            // ── Save / Cancel ──
            var row = AbyssLayout.row(ctx.x, ctx.y + ctx.height - AbyssButton.HEIGHT, 4);
            row.add(ctx.add(new AbyssButton(0, 0, 70, Component.translatable("abyssbubbles.screen.save"), b -> save())));
            row.add(ctx.add(new AbyssButton(0, 0, 70, Component.translatable("abyssbubbles.screen.cancel"), b -> onClose())));
        }

        private void hexField(TabContext ctx, int x, int y, String value, java.util.function.Consumer<String> onChange) {
            var field = ctx.add(new AbyssTextField(x, y, HEX_W, Component.literal("RRGGBB")));
            field.setMaxLength(7);
            field.setFilter(s -> s.matches("#?[0-9a-fA-F]{0,6}"));   // only hex digits can be typed
            field.setValue(value);
            field.setResponder(v -> { onChange.accept(v); errorKey = null; });
        }

        @Override
        public void render(GuiGraphics g, TabContext ctx, int mouseX, int mouseY, float partialTick) {
            Font font = ctx.font();

            // Colors: labels + swatches (a swatch shows the typed color, or an empty frame while it's invalid)
            AbyssDraw.sectionTitle(g, font, Component.literal("COLORS"), ctx.x, ctx.y + COLORS_Y);
            colorRow(g, font, ctx, 0, "abyssbubbles.screen.bg_color", bgHex);
            colorRow(g, font, ctx, 1, "abyssbubbles.screen.border_color", borderHex);
            colorRow(g, font, ctx, 2, "abyssbubbles.screen.text_color", textHex);

            AbyssDraw.sectionTitle(g, font, Component.literal("LAYOUT"), ctx.x, ctx.y + LAYOUT_Y);
            g.drawString(font, Component.translatable("abyssbubbles.screen.import_export"),
                    ctx.x, ctx.y + IO_Y - 11, AbyssTheme.TEXT_DIM, false);

            int leftW = leftW(font);
            renderPreview(g, font, ctx.x + leftW + 14, ctx.y, ctx.width - leftW - 14);

            if (errorKey != null) {
                int x = ctx.x + 150;
                g.drawString(font, AbyssDraw.trimmed(font, Component.translatable(errorKey), ctx.width - 150),
                        x, ctx.y + ctx.height - 14, AbyssTheme.DANGER_TEXT, false);
            }
        }

        private void colorRow(GuiGraphics g, Font font, TabContext ctx, int row, String labelKey, String hex) {
            int y = ctx.y + COLORS_Y + 12 + row * ROW;
            g.drawString(font, Component.translatable(labelKey), ctx.x, y + 6, AbyssTheme.TEXT, false);
            int sx = ctx.x + labelW(font) + HEX_W + 6, sy = y + 3;
            Integer rgb = parseRgb(hex);
            g.fill(sx, sy, sx + 14, sy + 14, 0xFF000000);
            if (rgb != null) g.fill(sx + 1, sy + 1, sx + 13, sy + 13, 0xFF000000 | rgb);
            AbyssDraw.outline(g, sx, sy, 14, 14, rgb == null ? AbyssTheme.DANGER_TEXT : AbyssTheme.DIVIDER);
        }

        /** A small live bubble with the current colors, so players see the result before saving. */
        private void renderPreview(GuiGraphics g, Font font, int x, int y, int w) {
            AbyssDraw.sectionTitle(g, font, Component.literal("PREVIEW"), x, y);
            int bg = orDefault(parseRgb(bgHex), 0x000000);
            int border = orDefault(parseRgb(borderHex), 0xFFFFFF);
            int text = orDefault(parseRgb(textHex), 0xFFFFFF);

            String sample = "Hello!";
            int bw = font.width(sample) + 16, bh = 18;
            int bx = x + (w - bw) / 2, by = y + 22;
            g.fill(bx, by, bx + bw, by + bh, 0xFF000000 | bg);
            AbyssDraw.outline(g, bx, by, bw, bh, 0xFF000000 | border);
            // little tail pointing down at the (imaginary) player
            int tx = bx + bw / 2;
            g.fill(tx - 3, by + bh, tx + 4, by + bh + 1, 0xFF000000 | border);
            g.fill(tx - 2, by + bh + 1, tx + 3, by + bh + 2, 0xFF000000 | border);
            g.fill(tx - 1, by + bh + 2, tx + 2, by + bh + 3, 0xFF000000 | border);
            g.drawString(font, sample, bx + 8, by + 5, 0xFF000000 | text, false);
        }
    }

    //-----------------------------------------------------------------------------------
    //                                     ACTIONS
    //-----------------------------------------------------------------------------------

    private void save() {
        Integer bg = parseRgb(bgHex), border = parseRgb(borderHex), text = parseRgb(textHex);
        if (bg == null)     { errorKey = "abyssbubbles.screen.error.invalid_bg"; return; }
        if (border == null) { errorKey = "abyssbubbles.screen.error.invalid_border"; return; }
        if (text == null)   { errorKey = "abyssbubbles.screen.error.invalid_text"; return; }

        PacketDistributor.sendToServer(new BubbleConfigUpdatePacket(
                rgbToColor(bg), rgbToColor(border), text, offset, spacing, hideNametag));
        onClose();
    }

    /** "BG,BORDER,TEXT,offset,spacing,hideNametag" → the import field + the clipboard. */
    private void export(TabContext ctx) {
        if (parseRgb(bgHex) == null || parseRgb(borderHex) == null || parseRgb(textHex) == null) {
            errorKey = "abyssbubbles.screen.error.invalid_import";
            return;
        }
        importText = clean(bgHex) + "," + clean(borderHex) + "," + clean(textHex) + ","
                + format(offset) + "," + format(spacing) + "," + hideNametag;
        this.minecraft.keyboardHandler.setClipboard(importText);
        ctx.rebuild();   // show it in the field
    }

    private void importConfig(TabContext ctx) {
        String raw = importText.trim();
        if (raw.isEmpty()) { errorKey = "abyssbubbles.screen.error.empty_import"; return; }
        String[] p = raw.split(",");
        try {
            if (p.length != 6 || parseRgb(p[0]) == null || parseRgb(p[1]) == null || parseRgb(p[2]) == null) {
                throw new IllegalArgumentException();
            }
            bgHex = clean(p[0]);
            borderHex = clean(p[1]);
            textHex = clean(p[2]);
            offset = clamp(Double.parseDouble(p[3].trim()), OFFSET_MIN, OFFSET_MAX);
            spacing = clamp(Double.parseDouble(p[4].trim()), SPACING_MIN, SPACING_MAX);
            hideNametag = Boolean.parseBoolean(p[5].trim());   // the old screen ignored this part
            errorKey = null;
            ctx.rebuild();   // fields, sliders and toggle pick up the new values
        } catch (Exception e) {
            errorKey = "abyssbubbles.screen.error.invalid_import";
        }
    }

    //-----------------------------------------------------------------------------------
    //                                     HELPERS
    //-----------------------------------------------------------------------------------

    /** "#a1b2c3" / "A1B2C3" → 0xA1B2C3, or null if it isn't exactly 6 hex digits. */
    @Nullable
    private static Integer parseRgb(String hex) {
        String h = clean(hex);
        if (!h.matches("[0-9A-F]{6}")) return null;
        return Integer.parseInt(h, 16);
    }

    private static String clean(String hex) {
        return hex.trim().replace("#", "").toUpperCase(Locale.ROOT);
    }

    private static int orDefault(@Nullable Integer value, int fallback) {
        return value != null ? value : fallback;
    }

    /** Slider 0..1 ↔ a value in [min, max], rounded to steps of 0.5. */
    private static double fromSlider(double v, double min, double max) {
        return Math.round((min + v * (max - min)) * 2) / 2.0;
    }

    private static double toSlider(double value, double min, double max) {
        return (clamp(value, min, max) - min) / (max - min);
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private static String format(double v) {
        return String.format(Locale.ROOT, "%.1f", v);
    }

    private static Color rgbToColor(int rgb) {
        return new Color(((rgb >> 16) & 0xFF) / 255f, ((rgb >> 8) & 0xFF) / 255f, (rgb & 0xFF) / 255f);
    }

    private static String colorToHex(Color c) {
        return String.format("%02X%02X%02X",
                Math.round(c.r() * 255), Math.round(c.g() * 255), Math.round(c.b() * 255));
    }

    private static String intToHex(int color) {
        return String.format("%06X", color & 0xFFFFFF);
    }
}