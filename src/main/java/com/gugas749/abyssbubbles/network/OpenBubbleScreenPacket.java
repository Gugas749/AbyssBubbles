package com.gugas749.abyssbubbles.network;

import com.gugas749.abysscore.api.network.AbyssPacketCodec;
import com.gugas749.abyssbubbles.util.Color;

/** Server → Client: open the bubble config screen, pre-filled with the player's current settings. */
public record OpenBubbleScreenPacket(
        Color bgColor,
        Color borderColor,
        int textColor,
        double offset,
        double spacing,
        boolean hideNametag
) {

    public static final AbyssPacketCodec<OpenBubbleScreenPacket> CODEC = AbyssPacketCodec.of(
            (buf, pkt) -> {
                Color.write(buf, pkt.bgColor());
                Color.write(buf, pkt.borderColor());
                buf.writeInt(pkt.textColor());
                buf.writeDouble(pkt.offset());
                buf.writeDouble(pkt.spacing());
                buf.writeBoolean(pkt.hideNametag());
            },
            buf -> new OpenBubbleScreenPacket(
                    Color.read(buf),
                    Color.read(buf),
                    buf.readInt(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readBoolean()
            )
    );
    // The handler moved to ClientPayloadHandler.handleOpenScreen (a client-only class),
    // so this record no longer references client code at all.
}
