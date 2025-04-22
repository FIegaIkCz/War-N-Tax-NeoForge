package com.fiegaikcz.warntaxes.command.sub;

import com.fiegaikcz.warntaxes.pvp.PvpInviteManager;
import com.fiegaikcz.warntaxes.util.TextUtil;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;

public class PvpDeclineCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("decline")
            .executes(context -> {
                ServerPlayer player = context.getSource().getPlayer();
                UUID challengerId = PvpInviteManager.getChallenger(player);
                MinecraftServer server = context.getSource().getServer();

                if (challengerId == null) {
                    player.sendSystemMessage(TextUtil.error("You cannot decline a fight, if nobody wants to fight you, right?"));
                    return 0;
                }

                ServerPlayer challenger = server.getPlayerList().getPlayer(challengerId);
                if (challenger != null) {
                    challenger.sendSystemMessage(TextUtil.info(player.getName().getString() + " is scared of you and doesn't want to fight."));
                }

                PvpInviteManager.clearInvite(player);
                player.sendSystemMessage(TextUtil.success("Invite declined."));
                return 1;
            });
    }
}
