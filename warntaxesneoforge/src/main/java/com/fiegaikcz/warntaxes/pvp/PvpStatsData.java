package com.fiegaikcz.warntaxes.pvp;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Map;
import java.util.UUID;

public class PvpStatsData extends SavedData {

    public static final String ID = "warntaxes_pvp_stats";

    public static PvpStatsData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(PvpStatsData::load, PvpStatsData::new, ID);
    }

    public PvpStatsData() {}

    public static PvpStatsData load(CompoundTag tag) {
        PvpStatsData data = new PvpStatsData();

        CompoundTag players = tag.getCompound("players");
        for (String key : players.getAllKeys()) {
            try {
                UUID id = UUID.fromString(key);
                CompoundTag statsTag = players.getCompound(key);

                PvpStatsManager.Stats stats = new PvpStatsManager.Stats();
                stats.kills = statsTag.getInt("kills");
                stats.deaths = statsTag.getInt("deaths");
                stats.fights = statsTag.getInt("fights");

                PvpStatsManager.set(id, stats);
            } catch (IllegalArgumentException e) {
                // skip invalid UUID
            }
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag players = new CompoundTag();
        for (Map.Entry<UUID, PvpStatsManager.Stats> entry : PvpStatsManager.getAll().entrySet()) {
            CompoundTag statTag = new CompoundTag();
            statTag.putInt("kills", entry.getValue().kills);
            statTag.putInt("deaths", entry.getValue().deaths);
            statTag.putInt("fights", entry.getValue().fights);
            players.put(entry.getKey().toString(), statTag);
        }
        tag.put("players", players);
        return tag;
    }

    public static void save(ServerLevel level) {
        PvpStatsData data = get(level);
        data.setDirty();
    }
}
