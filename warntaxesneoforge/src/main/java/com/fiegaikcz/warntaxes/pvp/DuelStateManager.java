package com.fiegaikcz.warntaxes.pvp;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.*;

public class DuelStateManager {

    private static final Map<UUID, Long> duelStartTick = new HashMap<>();
    private static final Set<UUID> inDuel = new HashSet<>();
    private static long currentTick = 0;

    public static void registerDuel(ServerPlayer p1, ServerPlayer p2) {
        long start = currentTick;
        inDuel.add(p1.getUUID());
        inDuel.add(p2.getUUID());
        duelStartTick.put(p1.getUUID(), start);
        duelStartTick.put(p2.getUUID(), start);
    }

    public static boolean isInDuel(ServerPlayer player) {
        return inDuel.contains(player.getUUID());
    }

    public static boolean canLeave(ServerPlayer player) {
        if (!duelStartTick.containsKey(player.getUUID())) return false;
        return (currentTick - duelStartTick.get(player.getUUID())) >= 200;
    }

    public static long getDuelTimeLeft(ServerPlayer player) {
        if (!duelStartTick.containsKey(player.getUUID())) return 0;
        long elapsed = currentTick - duelStartTick.get(player.getUUID());
        return Math.max(0, 200 - elapsed);
    }

    public static void endDuel(ServerPlayer p1, ServerPlayer p2) {
        inDuel.remove(p1.getUUID());
        inDuel.remove(p2.getUUID());
        duelStartTick.remove(p1.getUUID());
        duelStartTick.remove(p2.getUUID());
    }

    public static void tick() {
        currentTick++;

        for (UUID id : inDuel) {
            ServerPlayer player = getPlayerById(id);
            if (player == null) continue;

            // Still in delay
            if (!canLeave(player)) {
                MobEffectInstance slowness = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 4, false, false, false);
                player.addEffect(slowness);
            } else {
                player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            }
        }
    }

    private static ServerPlayer getPlayerById(UUID id) {
        for (MinecraftServer server : MinecraftServer.getServer().getAllLevels().stream().map(ServerLevel::getServer).distinct().toList()) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.getUUID().equals(id)) return player;
            }
        }
        return null;
    }
}
