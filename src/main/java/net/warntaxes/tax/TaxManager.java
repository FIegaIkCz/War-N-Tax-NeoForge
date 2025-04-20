package net.warntaxes.tax;

import net.minecraft.server.level.ServerPlayer;
import net.warntaxes.currency.CurrencyHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaxManager {

    // Map to hold tax balances (simplified model)
    private static final Map<UUID, Integer> taxAccounts = new HashMap<>();

    public static void applyTax(ServerPlayer player, int amount) {
        taxAccounts.put(player.getUUID(),
            taxAccounts.getOrDefault(player.getUUID(), 0) + amount);
    }

    public static void collectTax(ServerPlayer player) {
        UUID id = player.getUUID();
        int amount = taxAccounts.getOrDefault(id, 0);

        if (amount > 0) {
            CurrencyHelper.deposit(player, amount);
            taxAccounts.put(id, 0);
        }
    }

    public static int getOwedTax(ServerPlayer player) {
        return taxAccounts.getOrDefault(player.getUUID(), 0);
    }

    public static void resetTax(ServerPlayer player) {
        taxAccounts.remove(player.getUUID());
    }
}
