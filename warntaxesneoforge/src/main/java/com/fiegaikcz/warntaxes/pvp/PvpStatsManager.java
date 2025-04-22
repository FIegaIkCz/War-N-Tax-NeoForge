package com.fiegaikcz.warntaxes.pvp;

import java.util.*;

public class PvpStatsManager {

    public static class Stats {
        public int kills = 0;
        public int deaths = 0;
        public int fights = 0;

        public double getKD() {
            return deaths == 0 ? kills : (double) kills / deaths;
        }
    }

    private static final Map<UUID, Stats> statsMap = new HashMap<>();

    public static Stats get(UUID id) {
        return statsMap.computeIfAbsent(id, uuid -> new Stats());
    }

    public static void recordWin(UUID winner) {
        Stats stats = get(winner);
        stats.kills++;
        stats.fights++;
    }

    public static void recordLoss(UUID loser) {
        Stats stats = get(loser);
        stats.deaths++;
        stats.fights++;
    }

    public static Map<UUID, Stats> getAll() {
        return statsMap;
    }
}
