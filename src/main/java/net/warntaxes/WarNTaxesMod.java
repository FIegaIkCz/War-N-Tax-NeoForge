package net.warntaxes;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.warntaxes.init.ModEvents;
import net.warntaxes.init.ModRegistry;

@Mod(WarNTaxesMod.MODID)
public class WarNTaxesMod {
    public static final String MODID = "warntaxes";

    public WarNTaxesMod() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModRegistry.register(modBus);
        ModEvents.register(modBus);
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }
}
