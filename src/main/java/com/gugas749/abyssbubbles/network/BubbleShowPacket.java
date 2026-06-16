package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.bubble.BubbleEntry;
import com.gugas749.abyssbubbles.bubble.ClientBubbleRenderer;
import com.gugas749.abyssbubbles.config.BubbleConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record BubbleShowPacket(UUID entityUUID, String message, BubbleConfig config) implements CustomPacketPayload {

    public static final Type<BubbleShowPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Abyssbubbles.MODID, "bubble_show"));

    public static final StreamCodec<ByteBuf, BubbleShowPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(net.minecraft.core.UUIDUtil.STRING_CODEC), BubbleShowPacket::entityUUID,
            ByteBufCodecs.STRING_UTF8, BubbleShowPacket::message,
            BubbleConfigCodec.STREAM_CODEC, BubbleShowPacket::config,
            BubbleShowPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ClientBubbleRenderer.enqueueBubble(entityUUID, new BubbleEntry(message, config));
        });
    }
}
