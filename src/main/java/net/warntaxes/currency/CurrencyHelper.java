package net.warntaxes.currency;

import com.lightsmanscurrency.api.money.IMoneyHolder;
import com.lightsmanscurrency.api.money.MoneyAPI;
import com.lightsmanscurrency.common.money.MoneyAmount;
import net.minecraft.server.level.ServerPlayer;
import net.warntaxes.util.TextUtil;

public class CurrencyHelper {

    public static boolean deposit(ServerPlayer player, int coins) {
        IMoneyHolder wallet = MoneyAPI.getWallet(player);
        if (wallet != null) {
            wallet.deposit(new MoneyAmount(coins));
            player.sendSystemMessage(TextUtil.success("Deposited " + coins + " coins."));
            return true;
        }
        player.sendSystemMessage(TextUtil.error("No wallet found!"));
        return false;
    }

    public static boolean withdraw(ServerPlayer player, int coins) {
        IMoneyHolder wallet = MoneyAPI.getWallet(player);
        if (wallet != null && wallet.hasAtLeast(new MoneyAmount(coins))) {
            wallet.withdraw(new MoneyAmount(coins));
            player.sendSystemMessage(TextUtil.success("Withdrew " + coins + " coins."));
            return true;
        }
        player.sendSystemMessage(TextUtil.error("Insufficient funds."));
        return false;
    }

    public static int getBalance(ServerPlayer player) {
        IMoneyHolder wallet = MoneyAPI.getWallet(player);
        return wallet != null ? wallet.getBalance().getCoinValue() : 0;
    }
}