package com.fiegaikcz.warntaxes.tax;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TickEvent;
import com.fiegaikcz.warntaxes.warntaxesneoforge;

@EventBusSubscriber(modid = warntaxesneoforge.MODID)
public class TaxEventHandler {

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        MinecraftServer server = event.getServer();
        ServerLevel overworld = server.getLevel(ServerLevel.OVERWORLD);

        if (overworld != null) {
            TaxManager.tick(overworld);
        }
    }
}
