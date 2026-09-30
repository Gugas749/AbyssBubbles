package com.gugas749.abyssbubbles.network;

import com.gugas749.abysscore.api.network.AbyssPacketCodec;
import com.gugas749.abyssbubbles.data.BubbleConfigAttachment;
import com.gugas749.abyssbubbles.util.Color;

import java.util.UUID;

/**
 * Server → Client: "player X's bubble config is now Y".
 *
 * Replaces BubbleConfigSyncHandler (NeoForge's automatic attachment sync).
 * Always sends the FULL config — it's 6 small values, so the delta/flags logic the
 * old handler had isn't worth its complexity here.
 */
public record BubbleConfigSyncPacket(UUID playerUUID, BubbleConfigAttachment config) {

    public static final AbyssPacketCodec<BubbleConfigSyncPacket> CODEC = AbyssPacketCodec.of(
            (buf, pkt) -> {
                BubbleConfigAttachment c = pkt.config();
                buf.writeUUID(pkt.playerUUID());
                Color.write(buf, c.getBgColor());
                Color.write(buf, c.getBorderColor());
                buf.writeInt(c.getTextColor());
                buf.writeBoolean(c.isHideNametag());
                buf.writeDouble(c.getOffset());
                buf.writeDouble(c.getSpacing());
            },
            buf -> {
                UUID id = buf.readUUID();
                BubbleConfigAttachment c = new BubbleConfigAttachment(
                        Color.read(buf),     // bg
                        Color.read(buf),     // border
                        buf.readInt(),       // text color
                        buf.readBoolean(),   // hide nametag
                        buf.readDouble(),    // offset
                        buf.readDouble()     // spacing
                );
                return new BubbleConfigSyncPacket(id, c);
            }
    );
}
