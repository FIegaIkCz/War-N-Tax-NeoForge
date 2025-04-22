package com.fiegaikcz.warntaxes.command.sub;

import com.fiegaikcz.warntaxes.pvp.PvpArenaManager;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class SetSpawnCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("setspawn")
            .then(Commands.argument("arena", IntegerArgumentType.integer(0, 1))
            .then(Commands.argument("slot", IntegerArgumentType.integer(1, 2))
                .executes(context -> {
                    int arena = IntegerArgumentType.getInteger(context, "arena");
                    int slot = IntegerArgumentType.getInteger(context, "slot");
                    ServerPlayer player = context.getSource().getPlayer();
                    Vec3 pos = player.position();

                    PvpArenaManager.setSpawn(arena, slot, pos);
                    context.getSource().sendSuccess(() ->
                        Component.literal("Spawn point set for arena " + arena + ", slot " + slot), false);
                    return 1;
                })));
    }
}
