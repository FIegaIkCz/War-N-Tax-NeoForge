package com.fiegaikcz.warntaxes.command.sub;

import com.fiegaikcz.warntaxes.pvp.PvpInviteManager;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class PvpInviteCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("invite")
            .then(Commands.argument("target", StringArgumentType.word())
                .executes(context -> {
                    ServerPlayer sender = context.getSource().getPlayer();
                    String targetName = StringArgumentType.getString(context, "target");

                    ServerPlayer target = context.getSource().getServer().getPlayerList().getPlayerByName(targetName);
                    if (target == null) {
                        sender.sendSystemMessage(Component.literal("Target player not found."));
                        return 0;
                    }

                    if (target.getUUID().equals(sender.getUUID())) {
                        sender.sendSystemMessage(Component.literal("You cannot duel yourself."));
                        return 0;
                    }

                    PvpInviteManager.sendInvite(sender, target, context.getSource().getServer());
                    sender.sendSystemMessage(Component.literal("PvP invite sent to " + target.getName().getString()));
                    target.sendSystemMessage(Component.literal(sender.getName().getString() + " has challenged you to a PvP duel. Use /warntaxes pvp accept or /warntaxes pvp decline."));

                    return 1;
                }));
    }
}
