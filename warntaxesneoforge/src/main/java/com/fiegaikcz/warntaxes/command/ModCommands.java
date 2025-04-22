package com.fiegaikcz.warntaxes.command;

import com.fiegaikcz.warntaxes.Config;
import com.fiegaikcz.warntaxes.warntaxesneoforge;
import com.fiegaikcz.warntaxes.command.sub.WageWarCommand;
import com.fiegaikcz.warntaxes.command.sub.SuePeaceCommand;
import com.fiegaikcz.warntaxes.command.sub.PvpArenaCommand;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod.EventBusSubscriber(modid = warntaxesneoforge.MODID)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal("warntaxes")
                .requires(source -> {
                    if (Config.ONLY_OP_CAN_USE_COMMANDS.get()) {
                        return source.hasPermission(2);
                    }
                    return true;
                })
                .executes(context -> {
                    context.getSource().sendSuccess(() ->
                        Component.literal("War 'N Taxes mod command root."), false);
                    return 1;
                })
                .then(WageWarCommand.register())
                .then(SuePeaceCommand.register())
                .then(PvpArenaCommand.register())
                .then(SetSpawnCommand.register())
                .then(SetCornerCommand.register())
                .then(PvpInviteCommand.register())
                .then(PvpDeclineCommand.register())
                .then(LeavePvpCommand.register())
                .then(PvpStatsCommand.register())

        );
    }
}
