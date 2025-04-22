package com.fiegaikcz.warntaxes.command.sub;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class PvpArenaCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("pvp")
            .then(Commands.argument("arenaName", StringArgumentType.word())
                .executes(context -> {
                    String arena = StringArgumentType.getString(context, "arenaName");
                    context.getSource().sendSuccess(() ->
                        Component.literal("PvP Arena created: " + arena), false);
                    return 1;
                }));
    }
}
