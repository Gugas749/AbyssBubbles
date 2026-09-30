package com.gugas749.abyssbubbles.data;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.network.BubbleConfigSyncPacket;
import com.gugas749.abyssbubbles.network.ModNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

/**
 * SERVER side: stores each player's bubble config and keeps clients in sync.
 *
 * Replaces the NeoForge BUBBLE_CONFIG data attachment, which did three things for us:
 *   1. saved the config with the player        → here: Forge persistent NBT + the Codec
 *   2. kept it after death (copyOnDeath)       → here: onClone
 *   3. synced it to the player AND to everyone
 *      who can see them (.sync(handler))       → here: syncToTrackingAndSelf + onStartTracking
 */
@Mod.EventBusSubscriber(modid = Abyssbubbles.MODID) // default bus = FORGE (game events)
public final class BubbleConfigStorage {

    private static final String KEY = "abyssbubbles_config";

    private BubbleConfigStorage() {}

    // ── Read / write ──────────────────────────────────────────────────────────

    public static BubbleConfigAttachment get(ServerPlayer player) {
        CompoundTag persistent = player.getPersistentData();
        if (!persistent.contains(KEY)) return BubbleConfigAttachment.createDefault();

        return BubbleConfigAttachment.CODEC.parse(NbtOps.INSTANCE, persistent.get(KEY))
                .resultOrPartial(err -> Abyssbubbles.LOGGER.error("[AbyssBubbles] Bad bubble config for {}: {}", player.getScoreboardName(), err))
                .orElseGet(BubbleConfigAttachment::createDefault);
    }

    /** Saves the config and pushes it to the player + everyone tracking them. */
    public static void set(ServerPlayer player, BubbleConfigAttachment config) {
        Tag encoded = BubbleConfigAttachment.CODEC.encodeStart(NbtOps.INSTANCE, config)
                // DataFixerUpper on 1.20.1: getOrThrow(allowPartial, onError)
                .getOrThrow(false, err -> Abyssbubbles.LOGGER.error("[AbyssBubbles] Failed to encode bubble config: {}", err));
        player.getPersistentData().put(KEY, encoded);
        syncToTrackingAndSelf(player);
    }

    // ── Syncing ───────────────────────────────────────────────────────────────

    /** Everyone who can currently see this player (and the player) gets their config. */
    public static void syncToTrackingAndSelf(ServerPlayer player) {
        // AbyssNetworkChannel has no helper for this distributor yet, so use the raw SimpleChannel
        ModNetwork.CHANNEL.raw().send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                new BubbleConfigSyncPacket(player.getUUID(), get(player)));
    }

    // ── Events ────────────────────────────────────────────────────────────────

    /**
     * Fires when `receiver` starts seeing `target` (comes into range, joins, respawns nearby...).
     * This is the moment the receiver's client needs the target's colors.
     */
    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getTarget() instanceof ServerPlayer target)) return;
        if (!(event.getEntity() instanceof ServerPlayer receiver)) return;

        ModNetwork.CHANNEL.sendToPlayer(receiver, new BubbleConfigSyncPacket(target.getUUID(), get(target)));
    }

    /** A player never "tracks" themselves, so send their own config on login. */
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ModNetwork.CHANNEL.sendToPlayer(player, new BubbleConfigSyncPacket(player.getUUID(), get(player)));
    }

    /** Respawn / End return create a NEW player object — copy the config across (was copyOnDeath). */
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        CompoundTag oldData = original.getPersistentData();
        if (oldData.contains(KEY)) {
            event.getEntity().getPersistentData().put(KEY, oldData.get(KEY).copy());
        }
    }
}
