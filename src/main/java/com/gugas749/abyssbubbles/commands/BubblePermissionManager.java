package com.gugas749.abyssbubbles.commands;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Tracks two permission levels per player:
 *  - USAGE: chat goes through bubble
 *  - CONFIG: can open config screen and edit bubble style
 *
 * Persisted to world/data/abyssbubbles_perms.dat
 */
public class BubblePermissionManager extends SavedData {

    private static final String DATA_NAME = "abyssbubbles_perms";

    private static final Factory<BubblePermissionManager> FACTORY = new Factory<>(
            BubblePermissionManager::new,
            BubblePermissionManager::load,
            null
    );

    private final Set<UUID> usagePermitted = new HashSet<>();
    private final Set<UUID> configPermitted = new HashSet<>();

    // -------------------------------------------------------------------------
    // Singleton access
    // -------------------------------------------------------------------------

    public static BubblePermissionManager get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    // -------------------------------------------------------------------------
    // Permission management
    // -------------------------------------------------------------------------

    public void grantUsage(ServerPlayer player) {
        usagePermitted.add(player.getUUID());
        setDirty();
    }

    public void revokeUsage(ServerPlayer player) {
        usagePermitted.remove(player.getUUID());
        setDirty();
    }

    public void grantConfig(ServerPlayer player) {
        configPermitted.add(player.getUUID());
        // Config implies usage
        usagePermitted.add(player.getUUID());
        setDirty();
    }

    public void revokeConfig(ServerPlayer player) {
        configPermitted.remove(player.getUUID());
        setDirty();
    }

    public boolean hasUsage(UUID uuid) {
        return usagePermitted.contains(uuid);
    }

    public boolean hasConfig(UUID uuid) {
        return configPermitted.contains(uuid);
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    private static BubblePermissionManager load(CompoundTag tag, HolderLookup.Provider registries) {
        BubblePermissionManager mgr = new BubblePermissionManager();
        ListTag usage = tag.getList("usage", Tag.TAG_STRING);
        for (int i = 0; i < usage.size(); i++) mgr.usagePermitted.add(UUID.fromString(usage.getString(i)));
        ListTag config = tag.getList("config", Tag.TAG_STRING);
        for (int i = 0; i < config.size(); i++) mgr.configPermitted.add(UUID.fromString(config.getString(i)));
        return mgr;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag usage = new ListTag();
        usagePermitted.forEach(uuid -> usage.add(StringTag.valueOf(uuid.toString())));
        tag.put("usage", usage);
        ListTag config = new ListTag();
        configPermitted.forEach(uuid -> config.add(StringTag.valueOf(uuid.toString())));
        tag.put("config", config);
        return tag;
    }
}
