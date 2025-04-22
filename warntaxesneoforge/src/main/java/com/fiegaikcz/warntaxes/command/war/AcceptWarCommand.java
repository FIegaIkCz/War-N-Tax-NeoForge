package com.fiegaikcz.warntaxes.command.war;

import com.fiegaikcz.warntaxes.war.ColonyWarManager;
import com.fiegaikcz.warntaxes.war.WarStatsManager;
import com.minecolonies.api.colony.ColonyManager;
import com.minecolonies.api.colony.ICitizenColony;
import com.minecolonies.api.util.ColonyRoleUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class AcceptWarCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("acceptwar")
                .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                        .executes(AcceptWarCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        ServerPlayer sourcePlayer = context.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target");
        MinecraftServer server = sourcePlayer.getServer();

        ICitizenColony sourceColony = ColonyManager.getInstance().getColonyByOwner(sourcePlayer.getUUID());
        ICitizenColony targetColony = ColonyManager.getInstance().getColonyByOwner(targetPlayer.getUUID());

        if (sourceColony == null || targetColony == null) {
            context.getSource().sendFailure(Component.literal("One of the players does not own a colony."));
            return 0;
        }

        boolean isAuthorized = sourceColony.getOwner().equals(sourcePlayer.getUUID())
                || ColonyRoleUtils.isOfficer(sourceColony, sourcePlayer.getUUID());

        if (!isAuthorized) {
            context.getSource().sendFailure(Component.literal("Only the colony owner or officer can accept war."));
            return 0;
        }

        UUID idA = sourceColony.getID();
        UUID idB = targetColony.getID();

        if (!ColonyWarManager.hasProposal(idA, idB)) {
            context.getSource().sendFailure(Component.literal("No war proposal exists between these colonies."));
            return 0;
        }

        Integer proposedGuards = ColonyWarManager.getProposedGuards(idA, idB);
        if (proposedGuards != null) {
            sourcePlayer.sendSystemMessage(Component.literal("They are committing " + proposedGuards + " guards to this war."));
        }

        ColonyWarManager.acceptWar(idA, idB, System.currentTimeMillis());

        sourcePlayer.sendSystemMessage(Component.literal("You have accepted the war! It begins now."));
        targetPlayer.sendSystemMessage(Component.literal(sourcePlayer.getName().getString() + " has accepted your war proposal!"));

        // 🔔 Global broadcast
        String colonyA = sourceColony.getName();
        String colonyB = targetColony.getName();
        String ownerA = sourcePlayer.getName().getString();
        String ownerB = targetPlayer.getName().getString();
        int guards = proposedGuards != null ? proposedGuards : 0;

        String broadcast = String.format(
                "%s (%s) has started a war with %s (%s)! Committed guards: %d vs %d",
                colonyA, ownerA, colonyB, ownerB, guards, guards
        );

        server.getPlayerList().broadcastSystemMessage(Component.literal(broadcast), false);

        return 1;
    }
}
