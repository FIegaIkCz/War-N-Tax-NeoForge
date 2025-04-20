package net.warntaxes.war;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.warntaxes.util.TextUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WarManager {

    private static final Set<WarDeclaration> activeWars = new HashSet<>();

    public static boolean tryStartWar(ServerPlayer initiator, ServerPlayer target) {
        if (isAtWar(initiator.getUUID(), target.getUUID())) {
            return false; // already at war
        }

        WarDeclaration war = new WarDeclaration(initiator.getUUID(), target.getUUID());
        activeWars.add(war);

        initiator.sendSystemMessage(TextUtil.success("You have declared war on " + target.getName().getString()));
        target.sendSystemMessage(TextUtil.info("You are now at war with " + initiator.getName().getString()));

        return true;
    }

    public static boolean tryRequestPeace(ServerPlayer requester) {
        for (WarDeclaration war : activeWars) {
            if (war.involves(requester.getUUID())) {
                activeWars.remove(war);

                ServerPlayer opponent = war.getOtherPlayer(requester);
                if (opponent != null) {
                    opponent.sendSystemMessage(TextUtil.info(requester.getName().getString() + " has requested peace."));
                }

                requester.sendSystemMessage(TextUtil.success("You are no longer at war."));
                return true;
            }
        }
        return false;
    }

    public static boolean isAtWar(UUID player1, UUID player2) {
        return activeWars.stream().anyMatch(w -> w.involves(player1, player2));
    }
}