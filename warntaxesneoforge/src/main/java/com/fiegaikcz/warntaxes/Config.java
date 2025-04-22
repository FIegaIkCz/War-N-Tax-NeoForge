package com.fiegaikcz.warntaxes;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class Config {

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        final var builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }

    public static class Common {

        public final ForgeConfigSpec.IntValue DEBT_LIMIT;
        public final ForgeConfigSpec.IntValue TAX_INTERVAL_MINUTES;
        public final ForgeConfigSpec.IntValue MAX_TAX_REVENUE;
        public final ForgeConfigSpec.BooleanValue ENABLE_SDM_CONVERSION;
        public final ForgeConfigSpec.ConfigValue<String> CURRENCY_ITEM_NAME;
        public final ForgeConfigSpec.BooleanValue ENABLE_TAX_OFFLINE;

        // War settings
        public final ForgeConfigSpec.BooleanValue WAR_ACCEPTANCE_REQUIRED;
        public final ForgeConfigSpec.IntValue WAR_GRACE_PERIOD_MIN;
        public final ForgeConfigSpec.IntValue RAID_GRACE_PERIOD_MIN;
        public final ForgeConfigSpec.IntValue MAX_RAID_DURATION_MIN;
        public final ForgeConfigSpec.BooleanValue ALLOW_OFFLINE_RAIDS;
        public final ForgeConfigSpec.DoubleValue RAID_PENALTY_PERCENT;
        public final ForgeConfigSpec.IntValue RAID_MIN_GUARDS;
        public final ForgeConfigSpec.IntValue RAID_TAX_INTERVAL_SECONDS;
        public final ForgeConfigSpec.ConfigValue<List<? extends Double>> RAID_TAX_PERCENTAGES;
        public final ForgeConfigSpec.IntValue WAR_DURATION_MINUTES;
        public final ForgeConfigSpec.IntValue WAR_MIN_GUARDS;
        public final ForgeConfigSpec.IntValue WAR_JOIN_PHASE_MINUTES;
        public final ForgeConfigSpec.IntValue REQUIRED_TOWERS_FOR_BOOST;
        public final ForgeConfigSpec.DoubleValue TOWER_TAX_BOOST;

        // New: War defeat/boost logic
        public final ForgeConfigSpec.DoubleValue GUARD_LOSS_DEFEAT_THRESHOLD;
        public final ForgeConfigSpec.IntValue DEFEAT_TAX_PENALTY_HOURS;
        public final ForgeConfigSpec.DoubleValue DEFEAT_TAX_PENALTY_PERCENT;
        public final ForgeConfigSpec.DoubleValue VICTORY_TAX_BOOST_PERCENT;
        public final ForgeConfigSpec.IntValue VICTORY_TAX_BOOST_HOURS;

        // PvP
        public final ForgeConfigSpec.BooleanValue ALLOW_PVP_COMMANDS;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("General Tax Settings").push("General");
            DEBT_LIMIT = builder.defineInRange("DebtLimit", 0, 0, Integer.MAX_VALUE);
            TAX_INTERVAL_MINUTES = builder.defineInRange("TaxIntervalMinutes", 60, 1, 1440);
            MAX_TAX_REVENUE = builder.defineInRange("MaxTaxRevenue", 5000, 1, Integer.MAX_VALUE);
            ENABLE_SDM_CONVERSION = builder.define("EnableSDMShopConversion", true);
            CURRENCY_ITEM_NAME = builder.define("CurrencyItemName", "minecraft:emerald");
            ENABLE_TAX_OFFLINE = builder.define("EnableTaxForOfflineColonies", false);
            builder.pop();

            builder.comment("War Settings").push("War Settings");
            WAR_ACCEPTANCE_REQUIRED = builder.define("WarAcceptanceRequired", true);
            WAR_GRACE_PERIOD_MIN = builder.defineInRange("AttackerGracePeriodMinutes", 120, 1, 1440);
            RAID_GRACE_PERIOD_MIN = builder.defineInRange("RaidGracePeriodMinutes", 1, 1, 1440);
            MAX_RAID_DURATION_MIN = builder.defineInRange("MaxRaidDurationMinutes", 20, 1, 1440);
            ALLOW_OFFLINE_RAIDS = builder.define("AllowOfflineRaids", true);
            RAID_PENALTY_PERCENT = builder.defineInRange("RaidPenaltyPercentage", 0.25, 0.0, 1.0);
            RAID_MIN_GUARDS = builder.defineInRange("MinGuardsToRaid", 5, 1, 100);
            RAID_TAX_INTERVAL_SECONDS = builder.defineInRange("RaidTaxIntervalSeconds", 10, 5, 3600);
            RAID_TAX_PERCENTAGES = builder.defineListAllowEmpty("RaidTaxPercentages", List.of(0.1, 0.3, 0.5, 0.7), val -> val instanceof Double);
            WAR_DURATION_MINUTES = builder.defineInRange("WarDurationMinutes", 30, 1, 1440);
            WAR_MIN_GUARDS = builder.defineInRange("MinGuardsToWageWar", 5, 1, 100);
            WAR_JOIN_PHASE_MINUTES = builder.defineInRange("JoinPhaseDurationMinutes", 5, 1, 30);
            REQUIRED_TOWERS_FOR_BOOST = builder.defineInRange("RequiredGuardTowersForBoost", 5, 1, 100);
            TOWER_TAX_BOOST = builder.defineInRange("GuardTowerTaxBoostPercentage", 0.1, 0.0, 1.0);

            GUARD_LOSS_DEFEAT_THRESHOLD = builder.defineInRange("GuardLossDefeatThreshold", 0.8, 0.01, 1.0);
            DEFEAT_TAX_PENALTY_HOURS = builder.defineInRange("DefeatTaxPenaltyHours", 4, 1, 24);
            DEFEAT_TAX_PENALTY_PERCENT = builder.defineInRange("DefeatTaxPenaltyPercentage", 0.25, 0.0, 1.0);
            VICTORY_TAX_BOOST_PERCENT = builder.defineInRange("VictoryTaxBoostPercentage", 0.25, 0.0, 1.0);
            VICTORY_TAX_BOOST_HOURS = builder.defineInRange("VictoryTaxBoostHours", 4, 1, 24);
            builder.pop();

            builder.comment("PvP Arena Settings").push("PvP Arena Settings");
            ALLOW_PVP_COMMANDS = builder.define("AllowPvPArenaCommands", false);
            builder.pop();
        }
    }
}
