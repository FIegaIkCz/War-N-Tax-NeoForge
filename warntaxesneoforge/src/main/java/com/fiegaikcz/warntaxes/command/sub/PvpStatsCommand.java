package com.fiegaikcz.warntaxes.command.sub;

import com.fiegaikcz.warntaxes.pvp.PvpStatsManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.text.DecimalFormat;
import java.util.UUID;

public class PvpStatsCommand {

    private static final DecimalFormat df = new DecimalFormat("#.##");

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("pvpstats")
            .then(Commands.argument("target", StringArgumentType.word())
                .executes(ctx -> {
                    String targetName = StringArgumentType.getString(ctx, "target");
                    ServerPlayer target = ctx.getSource().getServer().getPlayerList().getPlayerByName(targetName);

                    if (target == null) {
                        ctx.getSource().sendFailure(Component.literal("Player not found: " + targetName));
                        return 0;
                    }

                    UUID id = target.getUUID();
                    PvpStatsManager.Stats stats = PvpStatsManager.get(id);

                    ctx.getSource().sendSuccess(() -> Component.literal("§ePvP Stats for §f" + targetName), false);
                    ctx.getSource().sendSuccess(() -> Component.literal("§7Kills: §f" + stats.kills), false);
                    ctx.getSource().sendSuccess(() -> Component.literal("§7Deaths: §f" + stats.deaths), false);
                    ctx.getSource().sendSuccess(() -> Component.literal("§7K/D: §f" + df.format(stats.getKD())), false);
                    ctx.getSource().sendSuccess(() -> Component.literal("§7Duels: §f" + stats.fights), false);

                    return 1;
                }));
    }
}
