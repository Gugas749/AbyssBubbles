package com.gugas749.abyssbubbles.bubble;

import com.gugas749.abyssbubbles.config.BubbleConfig;

public class BubbleEntry {

    private final String text;
    private final BubbleConfig config;
    private final int durationTicks;
    private int ticksAlive = 0;

    public BubbleEntry(String text, BubbleConfig config) {
        this.text = text;
        this.config = config;
        this.durationTicks = computeDuration(text);
    }

    /**
     * Duration scales with message length.
     * Base: 60 ticks (3s). +2 ticks per character beyond 10 chars. Cap at 200 ticks (10s).
     */
    private static int computeDuration(String text) {
        int base = 60;
        int extra = Math.max(0, text.length() - 10) * 2;
        return Math.min(base + extra, 200);
    }

    public void tick() {
        ticksAlive++;
    }

    public boolean isExpired() {
        return ticksAlive >= durationTicks;
    }

    public float getLifeProgress() {
        return (float) ticksAlive / durationTicks;
    }

    public float getAlpha() {
        int ticksLeft = durationTicks - ticksAlive;
        if (ticksLeft < 10) return ticksLeft / 10f;
        return 1f;
    }

    public String getText() { return text; }
    public BubbleConfig getConfig() { return config; }
    public int getDurationTicks() { return durationTicks; }
}
