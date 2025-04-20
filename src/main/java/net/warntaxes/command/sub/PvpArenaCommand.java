package net.warntaxes.command.sub;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.warntaxes.pvp.PvpArenaManager;
import net.warntaxes.util.TextUtil;

public class PvpArenaCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("pvparena")
            .then(Commands.literal("p1").executes(PvpArenaCommand::setP1))
            .then(Commands.literal("p2").executes(PvpArenaCommand::setP2));
    }

    private static int setP1(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Vec3 pos = player.position();
        PvpArenaManager.setArenaCorner(1, BlockPos.containing(pos));
        context.getSource().sendSuccess(() -> TextUtil.info("Arena point 1 set."), true);
        return 1;
    }

    private static int setP2(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Vec3 pos = player.position();
        PvpArenaManager.setArenaCorner(2, BlockPos.containing(pos));
        context.getSource().sendSuccess(() -> TextUtil.info("Arena point 2 set."), true);
        return 1;
    }
}