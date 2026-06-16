package com.gugas749.abyssbubbles.bubble;

import com.gugas749.abyssbubbles.config.BubbleConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Map;
import java.util.UUID;

public class BubbleSavedData extends SavedData {

    private static final String DATA_NAME = "abyssbubbles";

    private static final Factory<BubbleSavedData> FACTORY = new Factory<>(
            BubbleSavedData::new,
            BubbleSavedData::load,
            null
    );

    // -------------------------------------------------------------------------
    // Load / Save
    // -------------------------------------------------------------------------

    public static BubbleSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    private static BubbleSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        BubbleSavedData data = new BubbleSavedData();
        ListTag players = tag.getList("players", Tag.TAG_COMPOUND);
        for (int i = 0; i < players.size(); i++) {
            CompoundTag entry = players.getCompound(i);
            UUID uuid = UUID.fromString(entry.getString("uuid"));
            BubbleManager.loadPlayerData(uuid, entry);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag players = new ListTag();
        // Collect all UUIDs that have any state
        for (UUID uuid : BubbleManager.getAllTrackedUUIDs()) {
            CompoundTag entry = BubbleManager.savePlayerData(uuid);
            entry.putString("uuid", uuid.toString());
            players.add(entry);
        }
        tag.put("players", players);
        return tag;
    }

    // -------------------------------------------------------------------------
    // Dirty marking helpers — call these whenever BubbleManager state changes
    // -------------------------------------------------------------------------

    public static void markDirty(MinecraftServer server) {
        get(server).setDirty();
    }
}
