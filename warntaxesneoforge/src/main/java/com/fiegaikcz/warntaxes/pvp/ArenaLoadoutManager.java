package com.fiegaikcz.warntaxes.pvp;

import com.fiegaikcz.warntaxes.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;

public class ArenaLoadoutManager {

    public static void spawnLoadoutChest(ServerPlayer player, int arenaIndex) {
        ServerLevel level = player.serverLevel();
        var spawn = PvpArenaManager.getArenaSpawn(arenaIndex, 1); // spawn1 is where chest goes
        if (spawn == null) return;

        BlockPos chestPos = BlockPos.containing(spawn).above();

        level.setBlockAndUpdate(chestPos, Blocks.CHEST.defaultBlockState());

        var blockEntity = level.getBlockEntity(chestPos);
        if (blockEntity instanceof ChestBlockEntity chest) {
            fillLoadoutInventory(chest, arenaIndex);
        }
    }

    public static void refillChest(ServerLevel level, BlockPos pos, int arenaIndex) {
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ChestBlockEntity chest) {
            fillLoadoutInventory(chest, arenaIndex);
        }
    }

    private static void fillLoadoutInventory(SimpleContainer inventory, int arenaIndex) {
        inventory.clearContent();

        List<? extends String> itemIds = arenaIndex == 0
                ? Config.ARENA0_LOADOUT.get()
                : Config.ARENA1_LOADOUT.get();

        int index = 0;
        for (String id : itemIds) {
            Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(id));
            if (item != Items.AIR) {
                inventory.setItem(index++, new ItemStack(item));
            }
        }
    }
}
