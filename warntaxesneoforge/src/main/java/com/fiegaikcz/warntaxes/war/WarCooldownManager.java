package com.fiegaikcz.warntaxes.war;

import com.fiegaikcz.warntaxes.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarCooldownManager {

    private static final Map<UUID, Long> lastWarEnd = new HashMap<>();

    public static void markWarEnded(UUID colonyId, long now) {
        lastWarEnd.put(colonyId, now);
    }

    public static boolean isInCooldown(UUID colonyId) {
        long gracePeriodMs = Config.COMMON.ATTACKER_GRACE_PERIOD_MINUTES.get() * 60L * 1000L;
        Long last = lastWarEnd.get(colonyId);
        if (last == null) return false;
        return System.currentTimeMillis() - last < gracePeriodMs;
    }

    public static long getRemainingCooldown(UUID colonyId) {
        long gracePeriodMs = Config.COMMON.ATTACKER_GRACE_PERIOD_MINUTES.get() * 60L * 1000L;
        Long last = lastWarEnd.get(colonyId);
        if (last == null) return 0;
        return Math.max(0, gracePeriodMs - (System.currentTimeMillis() - last));
    }
}
