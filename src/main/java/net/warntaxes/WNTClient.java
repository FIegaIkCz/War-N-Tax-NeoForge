package net.warntaxes;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.warntaxes.gui.WarHudOverlay;

@Mod.EventBusSubscriber(modid = WarNTaxesMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WNTClient {

    @Mod.EventBusSubscriber(modid = WarNTaxesMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @net.neoforged.neoforge.eventbus.api.SubscribeEvent
        public static void onClientRenderRegister(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll(WarNTaxesMod.MODID + ":hud", new WarHudOverlay()::onRenderOverlay);
        }
    }

    @net.neoforged.fml.event.lifecycle.SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Future keybinds or render handlers can go here
    }

    @net.neoforged.fml.event.lifecycle.SubscribeEvent
    public static void onServerSetup(FMLDedicatedServerSetupEvent event) {
        // Empty here since client-only class
    }
}
