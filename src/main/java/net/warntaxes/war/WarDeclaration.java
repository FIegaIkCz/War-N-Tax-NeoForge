package net.warntaxes.war;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.UUID;

public class WarDeclaration {
    private final UUID player1;
    private final UUID player2;

    public WarDeclaration(UUID player1, UUID player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public boolean involves(UUID uuid) {
        return player1.equals(uuid) || player2.equals(uuid);
    }

    public boolean involves(UUID uuid1, UUID uuid2) {
        return (player1.equals(uuid1) && player2.equals(uuid2)) ||
               (player1.equals(uuid2) && player2.equals(uuid1));
    }

    public ServerPlayer getOtherPlayer(ServerPlayer current) {
        MinecraftServer server = current.server;
        UUID otherUUID = player1.equals(current.getUUID()) ? player2 : player1;
        return server.getPlayerList().getPlayer(otherUUID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WarDeclaration that)) return false;
        return (Objects.equals(player1, that.player1) && Objects.equals(player2, that.player2)) ||
               (Objects.equals(player1, that.player2) && Objects.equals(player2, that.player1));
    }

    @Override
    public int hashCode() {
        return player1.hashCode() + player2.hashCode(); // order-independent
    }
}
