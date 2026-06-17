package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.util.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenBubbleScreenPacket(
        Color bgColor,
        Color borderColor,
        int textColor,
        double offset,
        double spacing,
        boolean hideNametag
) implements CustomPacketPayload {

    public static final Type<OpenBubbleScreenPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Abyssbubbles.MODID, "open_bubble_screen"));

    public static final StreamCodec<ByteBuf, OpenBubbleScreenPacket> STREAM_CODEC = StreamCodec.composite(
            Color.STREAM_CODEC,      OpenBubbleScreenPacket::bgColor,
            Color.STREAM_CODEC,      OpenBubbleScreenPacket::borderColor,
            ByteBufCodecs.INT,       OpenBubbleScreenPacket::textColor,
            ByteBufCodecs.DOUBLE,    OpenBubbleScreenPacket::offset,
            ByteBufCodecs.DOUBLE,    OpenBubbleScreenPacket::spacing,
            ByteBufCodecs.BOOL,      OpenBubbleScreenPacket::hideNametag,
            OpenBubbleScreenPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ctx.enqueueWork(() ->
                    com.gugas749.abyssbubbles.client.ClientScreenOpener.openBubbleScreen(this));
        }
    }
}
