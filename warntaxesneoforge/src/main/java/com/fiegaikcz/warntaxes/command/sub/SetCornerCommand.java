package com.fiegaikcz.warntaxes.command.sub;

import com.fiegaikcz.warntaxes.pvp.PvpArenaManager;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SetCornerCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("setcorner")
            .then(Commands.argument("arena", IntegerArgumentType.integer(0, 1))
            .then(Commands.argument("corner", IntegerArgumentType.integer(1, 2))
                .executes(context -> {
                    int arena = IntegerArgumentType.getInteger(context, "arena");
                    int corner = IntegerArgumentType.getInteger(context, "corner");
                    ServerPlayer player = context.getSource().getPlayer();
                    BlockPos pos = player.blockPosition();

                    PvpArenaManager.setCorner(arena, corner, pos);
                    context.getSource().sendSuccess(() ->
                        Component.literal("Corner " + corner + " set for arena " + arena), false);
                    return 1;
                })));
    }
}
