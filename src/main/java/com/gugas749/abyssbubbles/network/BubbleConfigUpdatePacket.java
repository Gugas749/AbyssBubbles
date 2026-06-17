package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.util.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record BubbleConfigUpdatePacket(Color bgColor, Color borderColor, int textColor, double offset, double spacing, boolean hideNametag)
   implements CustomPacketPayload {
   public static final Type<BubbleConfigUpdatePacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath(Abyssbubbles.MODID, "update_bubble_config"));
   public static final StreamCodec<ByteBuf, BubbleConfigUpdatePacket> STREAM_CODEC = StreamCodec.composite(
      Color.STREAM_CODEC,
      BubbleConfigUpdatePacket::bgColor,
      Color.STREAM_CODEC,
      BubbleConfigUpdatePacket::borderColor,
      ByteBufCodecs.INT,
      BubbleConfigUpdatePacket::textColor,
      ByteBufCodecs.DOUBLE,
      BubbleConfigUpdatePacket::offset,
      ByteBufCodecs.DOUBLE,
      BubbleConfigUpdatePacket::spacing,
      ByteBufCodecs.BOOL,
      BubbleConfigUpdatePacket::hideNametag,
      BubbleConfigUpdatePacket::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
