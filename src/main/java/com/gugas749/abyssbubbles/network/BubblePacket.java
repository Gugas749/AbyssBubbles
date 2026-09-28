package com.gugas749.abyssbubbles.network;

import com.gugas749.abysscore.api.network.AbyssPacketCodec;

import java.util.UUID;

/** Server → Client: `playerUUID` said `message` at game time `startTime`. */
public record BubblePacket(UUID playerUUID, String message, long startTime) {

   public static final AbyssPacketCodec<BubblePacket> CODEC = AbyssPacketCodec.of(
      (buf, pkt) -> {
         buf.writeUUID(pkt.playerUUID());
         buf.writeUtf(pkt.message());
         buf.writeVarLong(pkt.startTime());
      },
      buf -> new BubblePacket(buf.readUUID(), buf.readUtf(), buf.readVarLong())
   );
}
