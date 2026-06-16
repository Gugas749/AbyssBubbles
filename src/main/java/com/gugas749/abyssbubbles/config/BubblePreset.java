package com.gugas749.abyssbubbles.config;

import net.minecraft.nbt.CompoundTag;

public class BubblePreset {

    private String name;
    private BubbleConfig config;

    public BubblePreset(String name, BubbleConfig config) {
        this.name = name;
        this.config = config;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        tag.put("config", config.save());
        return tag;
    }

    public static BubblePreset load(CompoundTag tag) {
        String name = tag.getString("name");
        BubbleConfig config = BubbleConfig.load(tag.getCompound("config"));
        return new BubblePreset(name, config);
    }

    public String toExportString() {
        CompoundTag tag = save();
        byte[] bytes = tag.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }

    public static BubblePreset fromExportString(String encoded) {
        try {
            byte[] bytes = java.util.Base64.getDecoder().decode(encoded);
            String raw = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            // Parse via Minecraft's SNBT parser
            CompoundTag tag = net.minecraft.nbt.TagParser.parseTag(raw);
            return load(tag);
        } catch (Exception e) {
            return null;
        }
    }

    public String getName() { return name; }
    public BubbleConfig getConfig() { return config; }
    public void setName(String name) { this.name = name; }
    public void setConfig(BubbleConfig config) { this.config = config; }
}
