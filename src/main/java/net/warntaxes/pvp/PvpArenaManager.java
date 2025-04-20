package net.warntaxes.pvp;

import net.minecraft.core.BlockPos;

public class PvpArenaManager {

    private static BlockPos corner1 = null;
    private static BlockPos corner2 = null;

    public static void setArenaCorner(int corner, BlockPos pos) {
        if (corner == 1) {
            corner1 = pos;
        } else if (corner == 2) {
            corner2 = pos;
        }
    }

    public static boolean isArenaSet() {
        return corner1 != null && corner2 != null;
    }

    public static BlockPos getCorner1() {
        return corner1;
    }

    public static BlockPos getCorner2() {
        return corner2;
    }

    public static void resetArena() {
        corner1 = null;
        corner2 = null;
    }
}
