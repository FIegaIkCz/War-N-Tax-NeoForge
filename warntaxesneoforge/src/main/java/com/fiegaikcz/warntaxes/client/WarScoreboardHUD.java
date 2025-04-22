package com.fiegaikcz.warntaxes.client;

import com.fiegaikcz.warntaxes.war.WarStatsManager;
import com.fiegaikcz.warntaxes.war.WarStatsManager.WarStats;
import com.minecolonies.api.util.LanguageHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import java.util.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class WarScoreboardHUD {

    private static final Minecraft MC = Minecraft.getInstance();
    private static boolean visible = true;

    public static void setVisible(boolean show) {
        visible = show;
    }

    public static boolean isVisible() {
        return visible;
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!visible || MC.level == null || MC.player == null || MC.options.hideGui) return;

        List<Map.Entry<UUID, WarStats>> sorted = WarStatsManager.getAll().entrySet().stream()
                .sorted(Comparator.comparingDouble(e -> -e.getValue().getWinLossRatio()))
                .limit(5)
                .collect(Collectors.toList());

        GuiGraphics graphics = event.getGuiGraphics();
        PoseStack poseStack = graphics.pose();
        int x = 10;
        int y = 10;
        int lineSpacing = 10;

        graphics.drawString(MC.font, "== War Scoreboard ==", x, y, 0xFF5555, false);
        y += lineSpacing;

        for (Map.Entry<UUID, WarStats> entry : sorted) {
            WarStats stats = entry.getValue();
            String line = String.format("Colony %s   %.2f   %d wins   %d kills",
                    entry.getKey().toString().substring(0, 6),
                    stats.getWinLossRatio(),
                    stats.wins,
                    stats.guardsKilled);

            graphics.drawString(MC.font, line, x, y, 0xFFFFFF, false);
            y += lineSpacing;
        }

        y += lineSpacing;
        graphics.drawString(MC.font, "Hide with /warstats hide", x, y, 0xAAAAAA, false);
    }
}
