package com.fiegaikcz.warntaxes.currency;

import io.github.lightman314.lightmanscurrency.api.capability.money.IMoneyHandler;
import io.github.lightman314.lightmanscurrency.api.money.value.MoneyView;
import io.github.lightman314.lightmanscurrency.api.money.value.holder.IMoneyHolder;
import net.minecraft.server.level.ServerPlayer;

public class CurrencyHelper {

    public static boolean deposit(ServerPlayer player, int coins) {
        IMoneyHolder holder = (IMoneyHolder) player.getCapability(
            io.github.lightman314.lightmanscurrency.api.MoneyCapability.INSTANCE
        ).orElse(null);

        if (holder == null) {
            System.out.println("Money capability not found for " + player.getName().getString());
            return false;
        }

        MoneyView moneyToDeposit = MoneyView.of(coins);
        holder.deposit(moneyToDeposit);
        return true;
    }

    public static boolean withdraw(ServerPlayer player, int coins) {
        IMoneyHolder holder = (IMoneyHolder) player.getCapability(
            io.github.lightman314.lightmanscurrency.api.MoneyCapability.INSTANCE
        ).orElse(null);

        if (holder == null) {
            System.out.println("Money capability not found for " + player.getName().getString());
            return false;
        }

        MoneyView moneyToWithdraw = MoneyView.of(coins);
        return holder.withdraw(moneyToWithdraw).isSuccess();
    }

    public static int getBalance(ServerPlayer player) {
        IMoneyHolder holder = (IMoneyHolder) player.getCapability(
            io.github.lightman314.lightmanscurrency.api.MoneyCapability.INSTANCE
        ).orElse(null);

        if (holder == null) {
            return 0;
        }

        return holder.getStoredMoney().getTotalValue();
    }

    public static void depositToServer(int coins) {
        IMoneyHolder serverHolder = io.github.lightman314.lightmanscurrency.api.MoneyHelper.getServerBank();
        if (serverHolder != null) {
            serverHolder.deposit(MoneyView.of(coins));
        } else {
            System.out.println("Server bank not available.");
        }
    }
}
