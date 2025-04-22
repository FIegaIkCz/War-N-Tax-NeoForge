package com.fiegaikcz.warntaxes.pvp;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.UUID;

// Curios
import top.theillusivec4.curios.api.CuriosApi;

// Skills
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;

public class InventoryManager {

    private static final HashMap<UUID, CompoundTag> savedInventories = new HashMap<>();

    public static void saveInventory(ServerPlayer player) {
        CompoundTag tag = new CompoundTag();

        // Save inventory
        ListTag inventory = new ListTag();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (!item.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                item.save(itemTag);
                itemTag.putInt("Slot", i);
                inventory.add(itemTag);
            }
        }
        tag.put("Inventory", inventory);

        // Save Curios
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            CompoundTag curiosTag = handler.serializeNBT();
            tag.put("Curios", curiosTag);
        });

        // Save Improvable Skills
        PlayerData skillData = PlayerDataHandler.get(player);
        if (skillData != null) {
            CompoundTag skillTag = skillData.serializeNBT();
            tag.put("Skills", skillTag);
        }

        savedInventories.put(player.getUUID(), tag);
    }

    public static void restoreInventory(ServerPlayer player) {
        CompoundTag tag = savedInventories.remove(player.getUUID());
        if (tag == null) return;

        player.getInventory().clearContent();

        ListTag inventory = tag.getList("Inventory", 10);
        for (int i = 0; i < inventory.size(); i++) {
            CompoundTag itemTag = inventory.getCompound(i);
            int slot = itemTag.getInt("Slot");
            ItemStack item = ItemStack.of(itemTag);
            player.getInventory().setItem(slot, item);
        }

        player.inventoryMenu.broadcastChanges();

        // Restore Curios
        if (tag.contains("Curios")) {
            CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
                handler.deserializeNBT(tag.getCompound("Curios"));
            });
        }

        // Restore Improvable Skills
        if (tag.contains("Skills")) {
            PlayerData skillData = PlayerDataHandler.get(player);
            if (skillData != null) {
                skillData.deserializeNBT(tag.getCompound("Skills"));
            }
        }
    }

    public static void clearAll(ServerPlayer player) {
        player.getInventory().clearContent();
        player.inventoryMenu.broadcastChanges();
    }
}
