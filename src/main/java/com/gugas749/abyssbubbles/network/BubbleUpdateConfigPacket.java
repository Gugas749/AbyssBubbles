package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.bubble.BubbleManager;
import com.gugas749.abyssbubbles.config.BubbleConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BubbleUpdateConfigPacket(BubbleConfig config) implements CustomPacketPayload {

    public static final Type<BubbleUpdateConfigPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Abyssbubbles.MODID, "update_config"));

    public static final StreamCodec<ByteBuf, BubbleUpdateConfigPacket> STREAM_CODEC = StreamCodec.composite(
            BubbleConfigCodec.STREAM_CODEC, BubbleUpdateConfigPacket::config,
            BubbleUpdateConfigPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer sp) {
                BubbleManager.updateConfig(sp, config);
            }
        });
    }
}
