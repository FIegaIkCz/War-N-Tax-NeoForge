package net.warntaxes.command.sub;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.warntaxes.util.TextUtil;
import net.warntaxes.war.WarManager;

public class SuePeaceCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("suepeace")
            .executes(SuePeaceCommand::execute);
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        ServerPlayer sender = context.getSource().getPlayerOrException();

        boolean success = WarManager.tryRequestPeace(sender);

        if (success) {
            context.getSource().sendSuccess(() ->
                TextUtil.success("Peace request sent."), true);
        } else {
            context.getSource().sendFailure(TextUtil.error("Failed to request peace."));
        }

        return 1;
    }
}