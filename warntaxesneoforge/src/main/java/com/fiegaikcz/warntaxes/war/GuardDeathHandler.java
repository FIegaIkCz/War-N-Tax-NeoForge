package com.fiegaikcz.warntaxes.war;

import com.minecolonies.api.colony.ColonyManager;
import com.minecolonies.api.colony.ICitizenColony;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizenGuard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.UUID;

@Mod.EventBusSubscriber
public class GuardDeathHandler {

    @SubscribeEvent
    public static void onGuardDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof AbstractEntityCitizenGuard guard)) return;
        if (!(event.getEntity().level() instanceof ServerLevel level)) return;

        ICitizenColony colony = ColonyManager.getInstance().getColonyByCitizen(guard);
        if (colony == null) return;

        UUID colonyId = colony.getID();

        // Only track if colony is currently in war
        boolean active = ColonyWarManager.getActiveWars().stream().anyMatch(pair -> pair.a().equals(colonyId) || pair.b().equals(colonyId));
        if (!active) return;

        WarStatsManager.addGuardKill(colonyId, 1);
    }
}
