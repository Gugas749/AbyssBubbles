package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.BubbleConfigScreen;
import com.gugas749.abyssbubbles.config.BubbleConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BubbleOpenScreenPacket(BubbleConfig config) implements CustomPacketPayload {

    public static final Type<BubbleOpenScreenPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Abyssbubbles.MODID, "open_screen"));

    public static final StreamCodec<ByteBuf, BubbleOpenScreenPacket> STREAM_CODEC = StreamCodec.composite(
            BubbleConfigCodec.STREAM_CODEC, BubbleOpenScreenPacket::config,
            BubbleOpenScreenPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> Minecraft.getInstance().setScreen(new BubbleConfigScreen(config)));
    }
}
