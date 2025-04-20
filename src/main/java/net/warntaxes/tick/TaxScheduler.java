package net.warntaxes.tick;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.TickEvent.ServerTickEvent;
import net.neoforged.neoforge.eventbus.api.SubscribeEvent;
import net.warntaxes.tax.TaxManager;

import java.util.List;

public class TaxScheduler {

    private static int tickCounter = 0;

    private static final int TAX_INTERVAL = 24000; // Default: 20 minutes in ticks

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        tickCounter++;

        if (tickCounter >= TAX_INTERVAL) {
            tickCounter = 0;
            runTaxCycle(event.getServer().overworld());
        }
    }

    private static void runTaxCycle(ServerLevel level) {
        List<ServerPlayer> players = level.getServer().getPlayerList().getPlayers();

        for (ServerPlayer player : players) {
            TaxManager.collectTax(player);
        }

        level.getServer().getLogger().info("[WarNTaxes] Tax cycle complete.");
    }
}
