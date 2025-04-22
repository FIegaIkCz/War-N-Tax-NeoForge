package com.fiegaikcz.warntaxes.command.sub;

import com.fiegaikcz.warntaxes.pvp.*;
import com.fiegaikcz.warntaxes.util.TextUtil;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class PvpAcceptCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("accept")
            .executes(context -> {
                ServerPlayer player = context.getSource().getPlayer();
                ServerPlayer challenger = context.getSource().getServer().getPlayerList().getPlayer(PvpInviteManager.getChallenger(player));

                if (challenger == null || !PvpInviteManager.hasInvite(player, challenger)) {
                    player.sendSystemMessage(TextUtil.error("Nobody wants to fight with you (yet)."));
                    return 0;
                }

                // Accept the duel
                PvpInviteManager.clearInvite(player);

                // Assign arena (use arena 0 for now)
                int arena = 0;

                // Save + clear inventories
                InventoryManager.saveInventory(player);
                InventoryManager.clearAll(player);
                InventoryManager.saveInventory(challenger);
                InventoryManager.clearAll(challenger);

                // Teleport both
                player.teleportTo(player.serverLevel(), PvpArenaManager.getArenaSpawn(arena, 1).x, PvpArenaManager.getArenaSpawn(arena, 1).y, PvpArenaManager.getArenaSpawn(arena, 1).z, 0, 0);
                challenger.teleportTo(challenger.serverLevel(), PvpArenaManager.getArenaSpawn(arena, 2).x, PvpArenaManager.getArenaSpawn(arena, 2).y, PvpArenaManager.getArenaSpawn(arena, 2).z, 0, 0);

                // Spawn loadout chest
                ArenaLoadoutManager.spawnLoadoutChest(player, arena);
                ArenaLoadoutManager.spawnLoadoutChest(challenger, arena);
