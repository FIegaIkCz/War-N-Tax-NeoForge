package com.fiegaikcz.warntaxes.tax;

import com.fiegaikcz.warntaxes.Config;

import java.util.Locale;

public class BuildingTaxCalculator {

    public static double calculateTax(String buildingName, int level) {
        String keyBase = buildingName.toLowerCase(Locale.ROOT);
        String keyUpgrade = keyBase + "Upgrade";

        double base = getDoubleConfigValue(keyBase);
        double upgrade = getDoubleConfigValue(keyUpgrade);

        return base + (level * upgrade);
    }

    private static double getDoubleConfigValue(String key) {
        try {
            var field = Config.COMMON.getClass().getDeclaredField(key);
            field.setAccessible(true);
            Object obj = field.get(Config.COMMON);
            if (obj instanceof net.minecraftforge.common.ForgeConfigSpec.DoubleValue val) {
                return val.get();
            }
        } catch (Exception ignored) {}

        return 0.0;
    }
}
