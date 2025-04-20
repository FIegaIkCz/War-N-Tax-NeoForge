package net.warntaxes.command.sub;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.warntaxes.util.TextUtil;
import net.warntaxes.war.WarManager;

public class WageWarCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("wagewar")
            .then(Commands.argument("targetPlayer", net.minecraft.commands.arguments.EntityArgument.player())
                .executes(WageWarCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        ServerPlayer initiator = context.getSource().getPlayerOrException();
        ServerPlayer target = net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "targetPlayer");

        boolean success = WarManager.tryStartWar(initiator, target);

        if (success) {
            context.getSource().sendSuccess(() ->
                TextUtil.success("War declared between " + initiator.getName().getString()
                + " and " + target.getName().getString()), true);
        } else {
            context.getSource().sendFailure(TextUtil.error("Failed to declare war."));
        }

        return 1;
    }
}