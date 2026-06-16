package com.gugas749.abyssbubbles.bubble;

import com.gugas749.abyssbubbles.config.BubbleConfig;
import com.gugas749.abyssbubbles.network.BubbleNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.*;

@EventBusSubscriber
public class BubbleManager {

    private static final Set<UUID> usagePermitted = new HashSet<>();
    private static final Set<UUID> configPermitted = new HashSet<>();
    private static final Map<UUID, BubbleConfig> playerConfigs = new HashMap<>();

    private static MinecraftServer server;

    public static void init() {}

    public static void setServer(MinecraftServer srv) {
        server = srv;
    }

    // -------------------------------------------------------------------------
    // Permission management
    // -------------------------------------------------------------------------

    public static void grantUsage(ServerPlayer player) {
        usagePermitted.add(player.getUUID());
        playerConfigs.putIfAbsent(player.getUUID(), new BubbleConfig());
        markDirty();
    }

    public static void revokeUsage(ServerPlayer player) {
        usagePermitted.remove(player.getUUID());
        markDirty();
    }

    public static void grantConfig(ServerPlayer player) {
        configPermitted.add(player.getUUID());
        playerConfigs.putIfAbsent(player.getUUID(), new BubbleConfig());
        grantUsage(player); // implies usage
        // markDirty called inside grantUsage
    }

    public static void revokeConfig(ServerPlayer player) {
        configPermitted.remove(player.getUUID());
        markDirty();
    }

    public static boolean hasUsagePermission(UUID uuid) {
        return usagePermitted.contains(uuid);
    }

    public static boolean hasConfigPermission(UUID uuid) {
        return configPermitted.contains(uuid);
    }

    // -------------------------------------------------------------------------
    // Config management
    // -------------------------------------------------------------------------

    public static BubbleConfig getConfig(UUID uuid) {
        return playerConfigs.getOrDefault(uuid, new BubbleConfig());
    }

    public static boolean updateConfig(ServerPlayer player, BubbleConfig newConfig) {
        if (!hasConfigPermission(player.getUUID())) return false;
        playerConfigs.put(player.getUUID(), newConfig);
        BubbleNetwork.syncConfigToClient(player);
        markDirty();
        return true;
    }

    // -------------------------------------------------------------------------
    // Chat dispatch
    // -------------------------------------------------------------------------

    public static boolean dispatchBubble(ServerPlayer player, String message) {
        if (!hasUsagePermission(player.getUUID())) return false;
        BubbleConfig config = getConfig(player.getUUID());
        BubbleNetwork.sendBubbleToNearby(player, message, config);
        return true;
    }

    // -------------------------------------------------------------------------
    // Persistence — called by BubbleSavedData
    // -------------------------------------------------------------------------

    public static Set<UUID> getAllTrackedUUIDs() {
        Set<UUID> all = new HashSet<>();
        all.addAll(usagePermitted);
        all.addAll(configPermitted);
        all.addAll(playerConfigs.keySet());
        return all;
    }

    public static CompoundTag savePlayerData(UUID uuid) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("usagePermitted", usagePermitted.contains(uuid));
        tag.putBoolean("configPermitted", configPermitted.contains(uuid));
        if (playerConfigs.containsKey(uuid)) {
            tag.put("config", playerConfigs.get(uuid).save());
        }
        return tag;
    }

    public static void loadPlayerData(UUID uuid, CompoundTag tag) {
        if (tag.getBoolean("usagePermitted")) usagePermitted.add(uuid);
        if (tag.getBoolean("configPermitted")) configPermitted.add(uuid);
        if (tag.contains("config")) playerConfigs.put(uuid, BubbleConfig.load(tag.getCompound("config")));
    }

    private static void markDirty() {
        if (server != null) BubbleSavedData.markDirty(server);
    }

    // -------------------------------------------------------------------------
    // Events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        // State stays in memory; SavedData handles disk persistence
    }
}
