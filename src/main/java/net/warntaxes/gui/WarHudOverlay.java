package net.warntaxes.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.eventbus.api.SubscribeEvent;

public class WarHudOverlay {

    private final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (mc.player == null || mc.level == null) return;

        int screenWidth = event.getWindow().getGuiScaledWidth();
        int screenHeight = event.getWindow().getGuiScaledHeight();

        int x = screenWidth / 2 - 80;
        int y = 10;

        if (isAtWar()) {
            GuiGraphics graphics = event.getGuiGraphics();
            graphics.drawString(mc.font, Component.literal("? War In Progress"), x, y, 0xFF5555, true);
        }

        if (isTaxDue()) {
            GuiGraphics graphics = event.getGuiGraphics();
            graphics.drawString(mc.font, Component.literal("?? Tax Due Soon"), x, y + 12, 0xFFFF55, true);
        }
    }

    private boolean isAtWar() {
        // TODO: Read actual client war state, maybe sync via packet
        return false;
    }

    private boolean isTaxDue() {
        // TODO: Optional logic; could track last tax tick
        return false;
    }
}
