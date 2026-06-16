package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.config.BubbleConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record BubbleSyncConfigPacket(UUID playerUUID, BubbleConfig config) implements CustomPacketPayload {

    public static final Type<BubbleSyncConfigPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Abyssbubbles.MODID, "sync_config"));

    public static final StreamCodec<ByteBuf, BubbleSyncConfigPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(UUIDUtil.STRING_CODEC), BubbleSyncConfigPacket::playerUUID,
            BubbleConfigCodec.STREAM_CODEC, BubbleSyncConfigPacket::config,
            BubbleSyncConfigPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            com.gugas749.abyssbubbles.bubble.ClientBubbleRenderer.updateLocalConfig(playerUUID, config);
        });
    }
}
