package com.gugas749.abyssbubbles.client;

import com.gugas749.abyssbubbles.data.BubbleConfigAttachment;
import com.gugas749.abyssbubbles.data.BubblesAttachment;
import com.gugas749.abyssbubbles.Abyssbubbles;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * CLIENT side: what this client knows about every player's bubbles and bubble config.
 *
 * Keyed by player UUID instead of being stored ON the player entity (like the NeoForge
 * attachment was). Advantage: a packet for a player whose entity isn't loaded yet
 * is not lost — it's waiting here when the entity appears.
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Abyssbubbles.MODID, value = Dist.CLIENT)
public final class ClientBubbleData {

    private static final Map<UUID, BubblesAttachment> BUBBLES = new HashMap<>();
    private static final Map<UUID, BubbleConfigAttachment> CONFIGS = new HashMap<>();

    private ClientBubbleData() {}

    public static BubblesAttachment bubbles(UUID player) {
        return BUBBLES.computeIfAbsent(player, id -> new BubblesAttachment());
    }

    public static BubbleConfigAttachment config(UUID player) {
        // No sync received yet → server defaults (server config is synced to the client on join)
        return CONFIGS.computeIfAbsent(player, id -> BubbleConfigAttachment.createDefault());
    }

    public static void setConfig(UUID player, BubbleConfigAttachment config) {
        CONFIGS.put(player, config);
    }

    /** Leaving a world/server: forget everything so nothing leaks into the next one. */
    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        BUBBLES.clear();
        CONFIGS.clear();
    }
}
