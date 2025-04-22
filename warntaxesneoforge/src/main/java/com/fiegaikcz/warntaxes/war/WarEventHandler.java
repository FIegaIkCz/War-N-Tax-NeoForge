package com.fiegaikcz.warntaxes.war;

import com.minecolonies.api.colony.ColonyManager;
import com.minecolonies.api.colony.ICitizenColony;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import com.fiegaikcz.warntaxes.warntaxesneoforge;

import java.util.*;

@Mod.EventBusSubscriber(modid = warntaxesneoforge.MODID)
public class WarEventHandler {

    private static final long EXPIRY_MS = 5 * 60 * 1000;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        MinecraftServer server = event.getServer();
        long now = System.currentTimeMillis();

        Set<ColonyWarManager.WarPair> toRemove = new HashSet<>();

        for (ColonyWarManager.WarPair pair : ColonyWarManager.getActiveWars()) {
            continue; // skip active wars
        }

        for (ColonyWarManager.WarPair pair : new HashSet<>(ColonyWarManager.getActiveWars())) {
            // Defensive (in case proposals leak into war list)
            if (ColonyWarManager.getProposalTime(pair.a(), pair.b()) == null) continue;

            long sent = ColonyWarManager.getProposalTime(pair.a(), pair.b());
            if ((now - sent) > EXPIRY_MS) {
                ColonyWarManager.declineWar(pair.a(), pair.b());
                toRemove.add(pair);

                // Notify players
                notifyColonyPlayer(server, pair.a(), "Your war proposal has expired.");
                notifyColonyPlayer(server, pair.b(), "A war proposal sent to you has expired.");
            }
        }
    }

    private static void notifyColonyPlayer(MinecraftServer server, UUID colonyId, String msg) {
        ICitizenColony colony = ColonyManager.getInstance().getColonyByID(colonyId);
        if (colony == null) return;

        UUID ownerId = colony.getOwner();
        ServerPlayer player = server.getPlayerList().getPlayer(ownerId);
        if (player != null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(msg));
        }
    }
}
