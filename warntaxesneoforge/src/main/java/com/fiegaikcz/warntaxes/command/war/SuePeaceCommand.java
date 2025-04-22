package com.fiegaikcz.warntaxes.command.war;

import com.fiegaikcz.warntaxes.war.ColonyWarManager;
import com.minecolonies.api.colony.ColonyManager;
import com.minecolonies.api.colony.ICitizenColony;
import com.minecolonies.api.util.ColonyRoleUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class SuePeaceCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("suepeace")
                .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                        .executes(SuePeaceCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        ServerPlayer sourcePlayer = context.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target");

        ICitizenColony sourceColony = ColonyManager.getInstance().getColonyByOwner(sourcePlayer.getUUID());
        ICitizenColony targetColony = ColonyManager.getInstance().getColonyByOwner(targetPlayer.getUUID());

        if (sourceColony == null || targetColony == null) {
            context.getSource().sendFailure(Component.literal("One of the players does not own a colony."));
            return 0;
        }

        boolean isAuthorized = sourceColony.getOwner().equals(sourcePlayer.getUUID())
                || ColonyRoleUtils.isOfficer(sourceColony, sourcePlayer.getUUID());

        if (!isAuthorized) {
            context.getSource().sendFailure(Component.literal("Only colony owner or officer can offer peace."));
            return 0;
        }

        UUID idA = sourceColony.getID();
        UUID idB = targetColony.getID();

        if (!ColonyWarManager.isAtWar(idA, idB)) {
            context.getSource().sendFailure(Component.literal("You are not currently at war with that colony."));
            return 0;
        }

        ColonyWarManager.endWar(idA, idB);

        sourcePlayer.sendSystemMessage(Component.literal("You offered peace to " + targetPlayer.getName().getString() + ". War ended."));
        targetPlayer.sendSystemMessage(Component.literal("Peace has been offered by " + sourcePlayer.getName().getString() + ". War ended."));

        return 1;
    }
}
