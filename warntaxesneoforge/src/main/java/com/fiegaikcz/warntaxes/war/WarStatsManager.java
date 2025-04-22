package com.fiegaikcz.warntaxes.war;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarStatsManager extends SavedData {

    private static final String FILE_ID = "war_stats_data";
    private static final Map<UUID, WarStats> stats = new HashMap<>();

    public static class WarStats {
        public int wins = 0;
        public int losses = 0;
        public int guardsKilled = 0;

        public double getWinLossRatio() {
            if (losses == 0) return wins;
            return Math.round((wins / (double) losses) * 100.0) / 100.0;
        }
    }

    public static WarStats get(UUID colonyId) {
        return stats.computeIfAbsent(colonyId, id -> new WarStats());
    }

    public static void addWin(UUID colonyId) {
        get(colonyId).wins++;
        markDirty();
    }

    public static void addLoss(UUID colonyId) {
        get(colonyId).losses++;
        markDirty();
    }

    public static void addGuardKill(UUID colonyId, int count) {
        get(colonyId).guardsKilled += count;
        markDirty();
    }

    public static Map<UUID, WarStats> getAll() {
        return stats;
    }

    public static void resetAll() {
        stats.clear();
        markDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Map.Entry<UUID, WarStats> entry : stats.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("id", entry.getKey().toString());
            entryTag.putInt("wins", entry.getValue().wins);
            entryTag.putInt("losses", entry.getValue().losses);
            entryTag.putInt("kills", entry.getValue().guardsKilled);
            list.add(entryTag);
        }
        tag.put("war_stats", list);
        return tag;
    }

    public static WarStatsManager load(CompoundTag tag) {
        WarStatsManager manager = new WarStatsManager();
        ListTag list = tag.getList("war_stats", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag entry = (CompoundTag) t;
            UUID id = UUID.fromString(entry.getString("id"));
            WarStats stats = new WarStats();
            stats.wins = entry.getInt("wins");
            stats.losses = entry.getInt("losses");
            stats.guardsKilled = entry.getInt("kills");
            manager.stats.put(id, stats);
        }
        return manager;
    }

    // Load/save from world
    public static void loadFromWorld(MinecraftServer server) {
        Level level = server.overworld();
        level.getDataStorage().computeIfAbsent(WarStatsManager::load, WarStatsManager::new, FILE_ID);
    }

    private static void markDirty() {
        // Called after stat change
        MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            Level level = server.overworld();
            WarStatsManager manager = level.getDataStorage().get(WarStatsManager::load, FILE_ID);
            if (manager != null) manager.setDirty();
        }
    }
}

@SubscribeEvent
public static void onServerStarting(ServerStartingEvent event) {
    WarStatsManager.loadFromWorld(event.getServer());
}