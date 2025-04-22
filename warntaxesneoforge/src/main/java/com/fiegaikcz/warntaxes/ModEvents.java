package com.fiegaikcz.warntaxes;

import com.fiegaikcz.warntaxes.war.WarStatsManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod.EventBusSubscriber(modid = warntaxesneoforge.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        WarStatsManager.loadFromWorld(event.getServer());
    }
}
