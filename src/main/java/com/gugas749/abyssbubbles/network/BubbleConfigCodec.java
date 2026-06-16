package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.config.BubbleConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class BubbleConfigCodec {

    public static final StreamCodec<ByteBuf, BubbleConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,   BubbleConfig::getBackgroundColor,
            ByteBufCodecs.INT,   BubbleConfig::getBorderColor,
            ByteBufCodecs.INT,   BubbleConfig::getTextColor,
            ByteBufCodecs.FLOAT, BubbleConfig::getVerticalOffset,
            ByteBufCodecs.INT,   BubbleConfig::getMaxWidth,
            (bg, border, text, offset, maxW) -> {
                BubbleConfig cfg = new BubbleConfig();
                cfg.setBackgroundColor(bg);
                cfg.setBorderColor(border);
                cfg.setTextColor(text);
                cfg.setVerticalOffset(offset);
                cfg.setMaxWidth(maxW);
                return cfg;
            }
    );
}
