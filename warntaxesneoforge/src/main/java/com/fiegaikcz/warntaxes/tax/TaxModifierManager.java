package com.fiegaikcz.warntaxes.tax;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaxModifierManager {

    private static final Map<UUID, Modifier> penalties = new HashMap<>();
    private static final Map<UUID, Modifier> bonuses = new HashMap<>();

    public static void setPenalty(UUID colonyId, double percent, long expiresAtMillis) {
        penalties.put(colonyId, new Modifier(percent, expiresAtMillis));
    }

    public static void setBonus(UUID colonyId, double percent, long expiresAtMillis) {
        bonuses.put(colonyId, new Modifier(percent, expiresAtMillis));
    }

    public static double getModifier(UUID colonyId) {
        long now = System.currentTimeMillis();
        double modifier = 1.0;

        Modifier bonus = bonuses.get(colonyId);
        if (bonus != null && bonus.expiresAt > now) {
            modifier += bonus.percent;
        }

        Modifier penalty = penalties.get(colonyId);
        if (penalty != null && penalty.expiresAt > now) {
            modifier -= penalty.percent;
        }

        return Math.max(0, modifier); // Ensure no negative income
    }

    public static void cleanupExpired() {
        long now = System.currentTimeMillis();
        penalties.entrySet().removeIf(entry -> entry.getValue().expiresAt <= now);
        bonuses.entrySet().removeIf(entry -> entry.getValue().expiresAt <= now);
    }

    private static class Modifier {
        final double percent;
        final long expiresAt;

        Modifier(double percent, long expiresAt) {
            this.percent = percent;
            this.expiresAt = expiresAt;
        }
    }
}
