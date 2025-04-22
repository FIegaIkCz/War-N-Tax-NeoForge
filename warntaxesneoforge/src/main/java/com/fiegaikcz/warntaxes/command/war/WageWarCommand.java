package com.fiegaikcz.warntaxes.command.war;

import com.fiegaikcz.warntaxes.war.ColonyWarManager;
import com.fiegaikcz.warntaxes.war.WarCooldownManager;
import com.minecolonies.api.colony.ColonyManager;
import com.minecolonies.api.colony.ICitizenColony;
import com.minecolonies.api.util.ColonyRoleUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class WageWarCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("wagewar")
                .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                        .then(Commands.argument("guards", net.minecraft.commands.arguments.IntegerArgumentType.integer(1))
                                .executes(WageWarCommand::execute)));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        ServerPlayer sourcePlayer = context.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target");
        int guards = net.minecraft.commands.arguments.IntegerArgumentType.getInteger(context, "guards");

        ICitizenColony colonyA = ColonyManager.getInstance().getColonyByOwner(sourcePlayer.getUUID());
        ICitizenColony colonyB = ColonyManager.getInstance().getColonyByOwner(targetPlayer.getUUID());

        if (colonyA == null || colonyB == null) {
            context.getSource().sendFailure(Component.literal("Both players must own colonies."));
            return 0;
        }

        if (!colonyA.getOwner().equals(sourcePlayer.getUUID())) {
            context.getSource().sendFailure(Component.literal("Only colony owners can declare war."));
            return 0;
        }

        if (WarCooldownManager.isInCooldown(colonyA.getID())) {
            long seconds = WarCooldownManager.getRemainingCooldown(colonyA.getID()) / 1000;
            context.getSource().sendFailure(Component.literal("Your colony is in cooldown! Wait " + seconds + " seconds."));
            return 0;
        }

        ColonyWarManager.proposeWar(colonyA.getID(), colonyB.getID(), guards);
        sourcePlayer.sendSystemMessage(Component.literal("You proposed war to " + targetPlayer.getName().getString() + " with " + guards + " guards."));
        targetPlayer.sendSystemMessage(Component.literal(sourcePlayer.getName().getString() + " has proposed war with " + guards + " guards."));

        return 1;
    }
}
