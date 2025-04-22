package com.fiegaikcz.warntaxes.tax;

import com.fiegaikcz.warntaxes.Config;
import com.fiegaikcz.warntaxes.currency.CurrencyHelper;
import com.minecolonies.api.colony.ColonyManager;
import com.minecolonies.api.colony.ICitizenColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;
import java.util.UUID;

public class TaxManager {

    private static int tickCounter = 0;

    public static void tick(ServerLevel level) {
        tickCounter++;

        int intervalTicks = Config.COMMON.TAX_INTERVAL_MINUTES.get() * 1200;
        if (tickCounter >= intervalTicks) {
            tickCounter = 0;

            collectTaxes(level);
            collectMaintenance(level);
        }
    }

    private static void collectTaxes(ServerLevel level) {
        double total = 0.0;

        for (ICitizenColony colony : ColonyManager.getInstance().getColonies()) {
            if (!colony.getWorld().dimension().equals(level.dimension())) continue;

            UUID ownerId = colony.getOwner();
            ServerPlayer owner = level.getPlayerByUUID(ownerId);

            if (owner == null && !Config.COMMON.ENABLE_TAX_OFFLINE.get()) {
                continue;
            }

            for (IBuilding building : colony.getBuildings()) {
                String name = building.getBuildingName().toLowerCase(Locale.ROOT);
                int lvl = building.getBuildingLevel();

                double tax = BuildingTaxCalculator.calculateTax(name, lvl);
                double modifier = TaxModifierManager.getModifier(colony.getID());
                double tax = base * modifier;
                total += tax;
            }
        }

        if (total > 0.0) {
            CurrencyHelper.depositToServer(level, (int) total);
        }
    }

    private static void collectMaintenance(ServerLevel level) {
        double total = 0.0;

        for (ICitizenColony colony : ColonyManager.getInstance().getColonies()) {
            if (!colony.getWorld().dimension().equals(level.dimension())) continue;

            UUID ownerId = colony.getOwner();
            ServerPlayer owner = level.getPlayerByUUID(ownerId);

            if (owner == null && !Config.COMMON.ENABLE_TAX_OFFLINE.get()) {
                continue;
            }

            for (IBuilding building : colony.getBuildings()) {
                String name = building.getBuildingName().toLowerCase(Locale.ROOT);
                int lvl = building.getBuildingLevel();

                double base = getMaintenanceValue(name);
                double upgrade = getMaintenanceValue(name + "Upgrade");
                total += base + (lvl * upgrade);
            }
        }

        if (total > 0.0) {
            CurrencyHelper.withdrawFromServer(level, (int) total);
        }
    }

    private static double getMaintenanceValue(String key) {
        try {
            var field = Config.COMMON.getClass().getDeclaredField(key);
            field.setAccessible(true);
            Object val = field.get(Config.COMMON);
            if (val instanceof net.minecraftforge.common.ForgeConfigSpec.DoubleValue d) {
                return d.get();
            }
        } catch (Exception ignored) {}
        return 0.0;
    }
}
