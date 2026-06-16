package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.bubble.ClientBubbleRenderer;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record BubbleActivePacket(UUID playerUUID, boolean active) implements CustomPacketPayload {

    public static final Type<BubbleActivePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Abyssbubbles.MODID, "bubble_active"));

    public static final StreamCodec<ByteBuf, BubbleActivePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(UUIDUtil.STRING_CODEC), BubbleActivePacket::playerUUID,
            ByteBufCodecs.BOOL, BubbleActivePacket::active,
            BubbleActivePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientBubbleRenderer.setActive(playerUUID, active));
    }
}
