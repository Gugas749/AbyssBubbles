package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abysscore.api.network.AbyssNetworkChannel;
import com.gugas749.abysscore.api.network.AbyssPacketHandler;
import net.minecraft.resources.ResourceLocation;

public class ModNetwork {

    /** AbyssBubbles' own channel. Bump "1" whenever a packet's fields change. */
    public static final AbyssNetworkChannel CHANNEL =
            AbyssNetworkChannel.create(new ResourceLocation(Abyssbubbles.MODID, "main"), "1");

    /**
     * Called once from the mod constructor.
     * ORDER MATTERS: each packet gets the next index; add new packets at the END.
     */
    public static void register() {
        // ── S2C ── (client handlers wrapped in a Supplier: only resolved on the CLIENT)
        AbyssPacketHandler.registerS2C(CHANNEL,
                BubblePacket.class, BubblePacket.CODEC,
                () -> ClientPayloadHandler::handleNewBubble);

        AbyssPacketHandler.registerS2C(CHANNEL,
                OpenBubbleScreenPacket.class, OpenBubbleScreenPacket.CODEC,
                () -> ClientPayloadHandler::handleOpenScreen);

        AbyssPacketHandler.registerS2C(CHANNEL,
                BubbleConfigSyncPacket.class, BubbleConfigSyncPacket.CODEC,
                () -> ClientPayloadHandler::handleConfigSync);

        // ── C2S ──
        AbyssPacketHandler.registerC2S(CHANNEL,
                BubbleConfigUpdatePacket.class, BubbleConfigUpdatePacket.CODEC,
                ServerPayloadHandler::handleUpdateBubbleConfig);
    }
}
