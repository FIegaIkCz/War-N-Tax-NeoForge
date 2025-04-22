package com.fiegaikcz.warntaxes.command;

import com.fiegaikcz.warntaxes.client.WarScoreboardHUD;
import com.fiegaikcz.warntaxes.war.WarStatsManager;
import com.minecolonies.api.colony.ColonyManager;
import com.minecolonies.api.colony.ICitizenColony;
import com.minecolonies.api.util.LanguageHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.;
import java.util.stream.Collectors;

public class WarStatsCommand {

    public static void register(CommandDispatcherCommandSourceStack dispatcher) {
        dispatcher.register(Commands.literal(warstats)
                .executes(ctx - showStats(ctx.getSource()))
                .then(Commands.literal(hide).executes(ctx - toggle(ctx.getSource(), false)))
                .then(Commands.literal(show).executes(ctx - toggle(ctx.getSource(), true)))
                .then(Commands.literal(toggle).executes(ctx - toggle(ctx.getSource(), !WarScoreboardHUD.isVisible()))));
    }

    private static int showStats(CommandSourceStack source) {
        ListMap.EntryUUID, WarStatsManager.WarStats top = WarStatsManager.getAll().entrySet().stream()
                .sorted(Comparator.comparingDouble(e - -e.getValue().getWinLossRatio()))
                .limit(5)
                .collect(Collectors.toList());

        source.sendSuccess(() - Component.literal(== War Scoreboard ==), false);
        for (Map.EntryUUID, WarStatsManager.WarStats entry  top) {
            String name = entry.getKey().toString().substring(0, 6);
            ICitizenColony colony = ColonyManager.getInstance().getColonyByID(entry.getKey());
            if (colony != null) name = colony.getName();

            WarStatsManager.WarStats stats = entry.getValue();
            source.sendSuccess(() - Component.literal(
                    String.format(%s   %.2f WL   %d Wins   %d Kills,
                            name,
                            stats.getWinLossRatio(),
                            stats.wins,
                            stats.guardsKilled)
            ), false);
        }

        return 1;
    }

    private static int toggle(CommandSourceStack source, boolean state) {
        WarScoreboardHUD.setVisible(state);
        source.sendSuccess(() - Component.literal(War scoreboard is now  + (state  visible.  hidden.)), false);
        return 1;
    }
}
