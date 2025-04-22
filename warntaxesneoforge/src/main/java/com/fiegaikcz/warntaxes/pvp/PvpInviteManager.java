package com.fiegaikcz.warntaxes.pvp;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.UUID;

public class PvpInviteManager {

    private static final class Invite {
        UUID sender;
        long expireTick;

        Invite(UUID sender, long expireTick) {
            this.sender = sender;
            this.expireTick = expireTick;
        }
    }

    private static final HashMap<UUID, Invite> invitations = new HashMap<>();
    private static long currentTick = 0;

    public static void sendInvite(ServerPlayer from, ServerPlayer to, MinecraftServer server) {
        long expire = currentTick + 6000; // 5 minutes = 6000 ticks
        invitations.put(to.getUUID(), new Invite(from.getUUID(), expire));
    }

    public static boolean hasInvite(ServerPlayer target, ServerPlayer from) {
        Invite invite = invitations.get(target.getUUID());
        return invite != null && invite.sender.equals(from.getUUID());
    }

    public static UUID getChallenger(ServerPlayer target) {
        Invite invite = invitations.get(target.getUUID());
        return (invite != null) ? invite.sender : null;
    }

    public static void clearInvite(ServerPlayer player) {
        invitations.remove(player.getUUID());
    }

    public static void tick() {
        currentTick++;
        invitations.entrySet().removeIf(entry -> entry.getValue().expireTick <= currentTick);
    }
}
