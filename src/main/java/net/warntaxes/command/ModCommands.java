package net.warntaxes.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.warntaxes.command.sub.WageWarCommand;
import net.warntaxes.command.sub.SuePeaceCommand;
import net.warntaxes.command.sub.PvpArenaCommand;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, net.minecraft.commands.CommandBuildContext ctx) {
        dispatcher.register(
            Commands.literal("warntaxes")
                .requires(cs -> cs.hasPermission(2))
                .then(WageWarCommand.register())
                .then(SuePeaceCommand.register())
                .then(PvpArenaCommand.register())
        );
    }
}
