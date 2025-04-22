package com.fiegaikcz.warntaxes.war;

import com.fiegaikcz.warntaxes.Config;
import com.fiegaikcz.warntaxes.tax.TaxModifierManager;
import com.minecolonies.api.colony.ColonyManager;
import com.minecolonies.api.colony.ICitizenColony;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import com.fiegaikcz.warntaxes.warntaxesneoforge;

import java.util.*;

@Mod.EventBusSubscriber(modid = warntaxesneoforge.MODID)
public class WarTickHandler {

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        MinecraftServer server = event.getServer();
        long now = System.currentTimeMillis();
        long warDurationMs = Config.COMMON.WAR_DURATION_MINUTES.get() * 60L * 1000L;

        Set<ColonyWarManager.WarPair> toEnd = new HashSet<>();

        for (ColonyWarManager.WarPair pair : ColonyWarManager.getActiveWars()) {
            Long startTime = ColonyWarManager.getStartTime(pair.a(), pair.b());
            if (startTime == null) continue;

            if ((now - startTime) >= warDurationMs) {
                toEnd.add(pair);
                continue;
            }

            ICitizenColony colonyA = ColonyManager.getInstance().getColonyByID(pair.a());
            ICitizenColony colonyB = ColonyManager.getInstance().getColonyByID(pair.b());
            if (colonyA == null || colonyB == null) continue;

            int guardsA = colonyA.getBuildingManager().getGuards().size();
            int guardsB = colonyB.getBuildingManager().getGuards().size();
            int totalGuardsA = colonyA.getBuildingManager().getAllBuildings().stream()
                    .filter(b -> b.getBuildingName().toLowerCase().contains("guard")).mapToInt(b -> b.getBuildingLevel() + 1).sum();
            int totalGuardsB = colonyB.getBuildingManager().getAllBuildings().stream()
                    .filter(b -> b.getBuildingName().toLowerCase().contains("guard")).mapToInt(b -> b.getBuildingLevel() + 1).sum();

            double threshold = Config.COMMON.GUARD_LOSS_DEFEAT_THRESHOLD.get();

            boolean aDefeated = totalGuardsA > 0 && (guardsA / (double) totalGuardsA) < (1.0 - threshold);
            boolean bDefeated = totalGuardsB > 0 && (guardsB / (double) totalGuardsB) < (1.0 - threshold);

            if (aDefeated || bDefeated) {
                UUID winner = aDefeated ? pair.b() : pair.a();
                UUID loser = aDefeated ? pair.a() : pair.b();

                applyVictory(server, winner, now);
                applyDefeat(server, loser, now);

                WarStatsManager.addWin(winner);
                WarStatsManager.addLoss(loser);

                // 🕓 Record cooldown
                WarCooldownManager.markWarEnded(pair.a(), now);
                WarCooldownManager.markWarEnded(pair.b(), now);

                // 🔔 Broadcast
                String colonyNameA = colonyA.getName();
                String colonyNameB = colonyB.getName();
                String winnerName = aDefeated ? getPlayerName(server, colonyB.getOwner()) : getPlayerName(server, colonyA.getOwner());
                String loserName = aDefeated ? getPlayerName(server, colonyA.getOwner()) : getPlayerName(server, colonyB.getOwner());

                int committed = ColonyWarManager.getProposedGuards(pair.a(), pair.b()) != null
                        ? ColonyWarManager.getProposedGuards(pair.a(), pair.b()) : 0;

                String broadcast = String.format(
                        "%s (%s) won against %s (%s) in a war with %d vs %d guards!",
                        aDefeated ? colonyNameB : colonyNameA,
                        winnerName,
                        aDefeated ? colonyNameA : colonyNameB,
                        loserName,
                        committed,
                        committed
                );
                server.getPlayerList().broadcastSystemMessage(Component.literal(broadcast), false);

                toEnd.add(pair);
            }
        }

        for (ColonyWarManager.WarPair pair : toEnd) {
            ColonyWarManager.endWar(pair.a(), pair.b());
        }
    }

    private static String getPlayerName(MinecraftServer server, UUID uuid) {
        ServerPlayer p = server.getPlayerList().getPlayer(uuid);
        return p != null ? p.getName().getString() : "Unknown";
    }

    private static void applyVictory(MinecraftServer server, UUID colonyId, long now) {
        double boost = Config.COMMON.VICTORY_TAX_BOOST_PERCENT.get();
        long duration = Config.COMMON.VICTORY_TAX_BOOST_HOURS.get() * 3600000L;
        TaxModifierManager.setBonus(colonyId, boost, now + duration);

        notifyOwner(server, colonyId, "Your colony won the war! +" +
                (int)(boost * 100) + "% tax income for " + Config.COMMON.VICTORY_TAX_BOOST_HOURS.get() + " hours.");
    }

    private static void applyDefeat(MinecraftServer server, UUID colonyId, long now) {
        double penalty = Config.COMMON.DEFEAT_TAX_PENALTY_PERCENT.get();
        long duration = Config.COMMON.DEFEAT_TAX_PENALTY_HOURS.get() * 3600000L;
        TaxModifierManager.setPenalty(colonyId, penalty, now + duration);

        notifyOwner(server, colonyId, "Your colony lost the war! -" +
                (int)(penalty * 100) + "% tax income for " + Config.COMMON.DEFEAT_TAX_PENALTY_HOURS.get() + " hours.");
    }

    private static void notifyOwner(MinecraftServer server, UUID colonyId, String message) {
        ICitizenColony colony = ColonyManager.getInstance().getColonyByID(colonyId);
        if (colony == null) return;
        ServerPlayer player = server.getPlayerList().getPlayer(colony.getOwner());
        if (player != null) {
            player.sendSystemMessage(Component.literal(message));
        }
    }
}
