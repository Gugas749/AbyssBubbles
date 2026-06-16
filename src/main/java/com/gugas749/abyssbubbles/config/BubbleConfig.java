package com.gugas749.abyssbubbles.config;

import net.minecraft.nbt.CompoundTag;

public class BubbleConfig {

    // Background color of the bubble box (ARGB)
    private int backgroundColor = 0xF0FFFFFF;
    // Border color (ARGB)
    private int borderColor = 0xFF000000;
    // Text color (ARGB)
    private int textColor = 0xFF000000;
    // Vertical offset above the entity's head (in blocks, added on top of base position)
    private float verticalOffset = 0.0f;
    // Max width in pixels before text wraps
    private int maxWidth = 120;

    public BubbleConfig() {}

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("backgroundColor", backgroundColor);
        tag.putInt("borderColor", borderColor);
        tag.putInt("textColor", textColor);
        tag.putFloat("verticalOffset", verticalOffset);
        tag.putInt("maxWidth", maxWidth);
        return tag;
    }

    public static BubbleConfig load(CompoundTag tag) {
        BubbleConfig cfg = new BubbleConfig();
        cfg.backgroundColor = tag.getInt("backgroundColor");
        cfg.borderColor = tag.getInt("borderColor");
        cfg.textColor = tag.getInt("textColor");
        cfg.verticalOffset = tag.getFloat("verticalOffset");
        cfg.maxWidth = tag.getInt("maxWidth");
        return cfg;
    }

    public BubbleConfig copy() {
        BubbleConfig copy = new BubbleConfig();
        copy.backgroundColor = this.backgroundColor;
        copy.borderColor = this.borderColor;
        copy.textColor = this.textColor;
        copy.verticalOffset = this.verticalOffset;
        copy.maxWidth = this.maxWidth;
        return copy;
    }

    public String toExportString() {
        CompoundTag tag = save();
        byte[] bytes = tag.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }

    public static BubbleConfig fromExportString(String encoded) {
        try {
            byte[] bytes = java.util.Base64.getDecoder().decode(encoded);
            String raw = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            CompoundTag tag = net.minecraft.nbt.TagParser.parseTag(raw);
            return load(tag);
        } catch (Exception e) {
            return null;
        }
    }

    // Getters
    public int getBackgroundColor() { return backgroundColor; }
    public int getBorderColor() { return borderColor; }
    public int getTextColor() { return textColor; }
    public float getVerticalOffset() { return verticalOffset; }
    public int getMaxWidth() { return maxWidth; }

    // Setters
    public void setBackgroundColor(int backgroundColor) { this.backgroundColor = backgroundColor; }
    public void setBorderColor(int borderColor) { this.borderColor = borderColor; }
    public void setTextColor(int textColor) { this.textColor = textColor; }
    public void setVerticalOffset(float verticalOffset) { this.verticalOffset = verticalOffset; }
    public void setMaxWidth(int maxWidth) { this.maxWidth = maxWidth; }
}
