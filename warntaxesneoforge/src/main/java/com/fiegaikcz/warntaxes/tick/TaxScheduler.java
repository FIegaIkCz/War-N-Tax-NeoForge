package com.fiegaikcz.warntaxes.tick;

import com.fiegaikcz.warntaxes.Config;
import com.fiegaikcz.warntaxes.currency.CurrencyHelper;
import com.fiegaikcz.warntaxes.util.TextUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;

@Mod.EventBusSubscriber
public class TaxScheduler {

    private static long tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        tickCounter++;

        if (tickCounter % Config.TAX_INTERVAL_TICKS.get() == 0) {
            collectTaxes(event.getServer());
        }
    }

    private static void collectTaxes(MinecraftServer server) {
        int taxAmount = Config.TAX_AMOUNT.get();
        int totalCollected = 0;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            boolean success = CurrencyHelper.withdraw(player, taxAmount);
            if (success) {
                CurrencyHelper.depositToServer(taxAmount);
                player.sendSystemMessage(TextUtil.info("You paid " + taxAmount + " coins in taxes."));
                totalCollected += taxAmount;
            } else {
                player.sendSystemMessage(TextUtil.error("Tax collection failed. Not enough coins?"));
            }
        }

        if (totalCollected > 0) {
            System.out.println("[TAX] Total collected this round: " + totalCollected + " coins");
        }
    }
}
