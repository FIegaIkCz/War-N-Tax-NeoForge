package com.fiegaikcz.warntaxes.init;

import com.fiegaikcz.warntaxes.pvp.PvpStatsManager;
import com.fiegaikcz.warntaxes.warntaxesneoforge;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = warntaxesneoforge.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class ModClientEvents {

    private static long lastFightDisplayTick = -1;
    private static final DecimalFormat KDF = new DecimalFormat("#.##");

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;

        GuiGraphics gui = event.getGuiGraphics();
        PoseStack pose = gui.pose();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // Countdown timer if in Slowness V
        if (player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
            var effect = player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
            if (effect != null && effect.getAmplifier() == 4) {
                int remainingTicks = effect.getDuration();
                int seconds = (int) Math.ceil(remainingTicks / 20.0);
                boolean blink = (remainingTicks / 10) % 2 == 0;

                pose.pushPose();
                pose.translate((screenWidth / 2f), 5, 0);
                pose.scale(1.5f, 1.5f, 1.0f);

                if (seconds > 0 && blink) {
                    Component text = Component.literal("PvP Starts In: " + seconds).withStyle(s -> s.withColor(0xFF5555));
                    int width = mc.font.width(text);
                    gui.drawString(mc.font, text, -width / 2, 0, 0xFF5555, false);
                }

                if (seconds == 0) {
                    long tick = mc.level.getGameTime();
                    if (lastFightDisplayTick == -1) lastFightDisplayTick = tick;
                    if (tick - lastFightDisplayTick <= 40) {
                        Component fightText = Component.literal("FIGHT!").withStyle(s -> s.withColor(0xFF0000));
                        int fightWidth = mc.font.width(fightText);
                        gui.drawString(mc.font, fightText, -fightWidth / 2, 15, 0xFF0000, false);
                    }
                } else {
                    lastFightDisplayTick = -1;
                }

                pose.popPose();
            }
        }

        // PvP Scoreboard (right side)
        var allStats = PvpStatsManager.getAll();
        if (!allStats.isEmpty()) {
            int y = 30;
            int x = screenWidth - 120;

            gui.drawString(mc.font, Component.literal("Top PvP Stats"), x, y, 0xFFFFFF, false);
            y += 10;

            allStats.entrySet().stream()
                .sorted(Comparator.comparingDouble((Map.Entry<UUID, PvpStatsManager.Stats> e) -> e.getValue().getKD()).reversed())
                .limit(5)
                .forEach(entry -> {
                    UUID id = entry.getKey();
                    var stat = entry.getValue();
                    String name = mc.getSocialManager().getPlayerName(id).orElse("Unknown");
                    String row = String.format("%s  %s  %d", name, KDF.format(stat.getKD()), stat.fights);
                    gui.drawString(mc.font, Component.literal(row), x, y, 0xAAAAAA, false);
                    y += 10;
                });
        }
    }
}
