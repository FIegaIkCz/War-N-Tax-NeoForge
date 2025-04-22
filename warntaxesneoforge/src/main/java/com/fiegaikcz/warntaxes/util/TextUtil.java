package com.fiegaikcz.warntaxes.util;

import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class TextUtil {

    public static Component success(String msg) {
        return Component.literal(msg).withStyle(ChatFormatting.GREEN);
    }

    public static Component warning(String msg) {
        return Component.literal(msg).withStyle(ChatFormatting.YELLOW);
    }

    public static Component error(String msg) {
        return Component.literal(msg).withStyle(ChatFormatting.RED);
    }

    public static Component info(String msg) {
        return Component.literal(msg).withStyle(ChatFormatting.GRAY);
    }
}
