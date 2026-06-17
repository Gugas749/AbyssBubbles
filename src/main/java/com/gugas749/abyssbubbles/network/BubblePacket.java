package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.Abyssbubbles;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record BubblePacket(UUID playerUUID, String message, long startTime) implements CustomPacketPayload {
   public static final Type<BubblePacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath(Abyssbubbles.MODID, "bubble"));
   public static final StreamCodec<ByteBuf, BubblePacket> STREAM_CODEC = StreamCodec.composite(
      UUIDUtil.STREAM_CODEC,
      BubblePacket::playerUUID,
      ByteBufCodecs.STRING_UTF8,
      BubblePacket::message,
      ByteBufCodecs.VAR_LONG,
      BubblePacket::startTime,
      BubblePacket::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
