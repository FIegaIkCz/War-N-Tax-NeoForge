package com.fiegaikcz.warntaxes.init;

import com.fiegaikcz.warntaxes.Config;
import com.fiegaikcz.warntaxes.pvp.*;
import com.fiegaikcz.warntaxes.warntaxesneoforge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;

import java.util.HashMap;
import java.util.UUID;

@EventBusSubscriber(modid = warntaxesneoforge.MODID)
public class ModEvents {

    private static final HashMap<UUID, BlockPos> lastValidArenaPos = new HashMap<>();
    private static final HashMap<UUID, Integer> arenaEscapeCounts = new HashMap<>();

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (!Config.ENABLE_PVP_IN_COLONIES.get()) return;
        if (PvpArenaManager.isInAnyArena(player)) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal("You can't break blocks inside the PvP arena."));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (!Config.ENABLE_PVP_IN_COLONIES.get()) return;

        UUID id = player.getUUID();

        if (PvpArenaManager.isInAnyArena(player)) {
            lastValidArenaPos.put(id, player.blockPosition().immutable());
        } else if (lastValidArenaPos.containsKey(id)) {
            int count = arenaEscapeCounts.getOrDefault(id, 0) + 1;
            arenaEscapeCounts.put(id, count);

            if (count == 1 || count == 2) {
                BlockPos spawn = player.getRespawnPosition() != null
                        ? player.getRespawnPosition()
                        : player.getLevel().getSharedSpawnPos();
                player.teleportTo(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5);
                player.sendSystemMessage(Component.literal("You left the PvP arena. Returned to spawn. (" + count + "/3)"));
            } else {
                player.kill();
                player.sendSystemMessage(Component.literal("You left the PvP arena 3 times. You have been eliminated."));
                arenaEscapeCounts.remove(id);
            }
        }

        PvpInviteManager.tick();
        DuelStateManager.tick();
    }

    @SubscribeEvent
    public static void onPlayerDeath(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer dead)) return;
        if (!Config.ENABLE_PVP_IN_COLONIES.get()) return;

        if (PvpArenaManager.isInAnyArena(dead)) {
            int arena = PvpArenaManager.getArenaIndex(dead);
            if (arena < 0) return;

            for (ServerPlayer other : dead.getServer().getPlayerList().getPlayers()) {
                if (!other.getUUID().equals(dead.getUUID()) && PvpArenaManager.isInAnyArena(other)) {
                    InventoryManager.restoreInventory(other);
                    other.sendSystemMessage(Component.literal("Duel won. Inventory restored."));

                    BlockPos chestPos = BlockPos.containing(PvpArenaManager.getArenaSpawn(arena, 1)).above();
                    ArenaLoadoutManager.refillChest(other.serverLevel(), chestPos, arena);

                    DuelStateManager.endDuel(dead, other);

                    // Track stats + persist
                    PvpStatsManager.recordWin(other.getUUID(), other.serverLevel());
                    PvpStatsManager.recordLoss(dead.getUUID(), dead.serverLevel());
                }
            }

            InventoryManager.restoreInventory(dead);
            dead.sendSystemMessage(Component.literal("Duel lost. Inventory restored."));

            lastValidArenaPos.remove(dead.getUUID());
            arenaEscapeCounts.remove(dead.getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerDamage(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer target)) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer attacker)) return;
        if (!Config.ENABLE_PVP_IN_COLONIES.get()) return;

        if (DuelStateManager.isInDuel(attacker) && !DuelStateManager.canLeave(attacker)) {
            attacker.sendSystemMessage(Component.literal("Wait for the duel to start!"));
            event.setCanceled(true);
        }
    }
}
