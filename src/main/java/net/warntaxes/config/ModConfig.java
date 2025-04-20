package net.warntaxes.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.warntaxes.WarNTaxesMod;

@Mod.EventBusSubscriber(modid = WarNTaxesMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {

    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec.IntValue TAX_INTERVAL_TICKS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("general");
        TAX_INTERVAL_TICKS = builder
            .comment("Interval (in ticks) for automatic tax collection")
            .defineInRange("taxIntervalTicks", 24000, 1, Integer.MAX_VALUE);
        builder.pop();

        COMMON_CONFIG = builder.build();
    }

    public static void register() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(
            (net.minecraftforge.fml.config.ModConfig.ModConfigEvent e) -> {
                if (e.getConfig().getType() == Type.COMMON) {
                    // Any runtime config sync logic here
                }
            }
        );
    }
}
