package com.gugas749.abyssbubbles.network;

import com.gugas749.abysscore.api.network.AbyssPacketCodec;
import com.gugas749.abyssbubbles.util.Color;

/** Client → Server: the player saved new bubble settings in the config screen. */
public record BubbleConfigUpdatePacket(Color bgColor, Color borderColor, int textColor, double offset, double spacing, boolean hideNametag) {

   public static final AbyssPacketCodec<BubbleConfigUpdatePacket> CODEC = AbyssPacketCodec.of(
      (buf, pkt) -> {
         Color.write(buf, pkt.bgColor());
         Color.write(buf, pkt.borderColor());
         buf.writeInt(pkt.textColor());
         buf.writeDouble(pkt.offset());
         buf.writeDouble(pkt.spacing());
         buf.writeBoolean(pkt.hideNametag());
      },
      // Read in EXACTLY the same order as written
      buf -> new BubbleConfigUpdatePacket(
         Color.read(buf),
         Color.read(buf),
         buf.readInt(),
         buf.readDouble(),
         buf.readDouble(),
         buf.readBoolean()
      )
   );
}
