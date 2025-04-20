package net.warntaxes.init;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.warntaxes.command.ModCommands;

@Mod.EventBusSubscriber
public class ModEvents {

    public static void register(net.neoforged.fml.event.lifecycle.FMLConstructModEvent event) {
        // Add lifecycle event hooks here if needed
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    public static void onServerStart(ServerStartingEvent event) {
        // Optional: initialize war state or load config
    }

    @SubscribeEvent
    public static void onServerStop(ServerStoppingEvent event) {
        // Optional: persist war state or perform cleanup
    }
}
