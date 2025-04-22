package com.fiegaikcz.warntaxes.command.sub;

import com.fiegaikcz.warntaxes.pvp.*;
import com.fiegaikcz.warntaxes.util.TextUtil;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class LeavePvpCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("leavepvp")
            .executes(context -> {
                ServerPlayer player = context.getSource().getPlayer();
                UUID playerId = player.getUUID();

                if (!PvpArenaManager.isInAnyArena(player)) {
                    player.sendSystemMessage(TextUtil.error("You are not in a PvP arena."));
                    return 0;
                }

                if (!DuelStateManager.canLeave(player)) {
                    player.sendSystemMessage(TextUtil.error("You can't leave the PvP arena yet. Wait for the duel to progress."));
                    return 0;
                }

                // End duel if 2 players were fighting
                for (ServerPlayer other : player.server.getPlayerList().getPlayers()) {
                    if (!other.getUUID().equals(playerId) && PvpArenaManager.isInAnyArena(other)) {
                        DuelStateManager.endDuel(player, other);
                        InventoryManager.restoreInventory(other);
                        other.sendSystemMessage(TextUtil.info(player.getName().getString() + " left the duel. You win by forfeit."));
                    }
                }

                InventoryManager.restoreInventory(player);
                player.teleportTo(player.getLevel().getSharedSpawnPos().getX() + 0.5,
                                  player.getLevel().getSharedSpawnPos().getY(),
                                  player.getLevel().getSharedSpawnPos().getZ() + 0.5);
                player.sendSystemMessage(TextUtil.success("You have left the PvP duel."));

                return 1;
            });
    }
}
