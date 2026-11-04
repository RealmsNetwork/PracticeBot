package com.sheldera.practicebot.api.inventory;

import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class TotemInventory {
    private TotemInventory() {
    }

    public static int count(ItemStack[] storage, ItemStack[] armor, ItemStack offhand) {
        int storageCount = count(storage);
        int armorCount = count(armor);
        return countAmounts(storageCount, armorCount, amount(offhand));
    }

    public static int countAmounts(int... amounts) {
        int total = 0;
        if (amounts == null) return 0;
        for (int amount : amounts) total += Math.max(0, amount);
        return total;
    }

    public static int remainingAfterTakingOne(int stackAmount) {
        return Math.max(0, stackAmount - 1);
    }

    public static ItemStack moveOneToOffhand(ItemStack[] storage, ItemStack offhand) {
        if (isTotem(offhand)) return offhand.clone();
        if (storage == null) return offhand == null ? null : offhand.clone();
        for (int slot = 0; slot < storage.length; slot++) {
            ItemStack item = storage[slot];
            if (!isTotem(item)) continue;
            ItemStack equipped = new ItemStack(Material.TOTEM_OF_UNDYING);
            int remaining = remainingAfterTakingOne(item.getAmount());
            if (remaining == 0) storage[slot] = null;
            else item.setAmount(remaining);
            return equipped;
        }
        return offhand == null ? null : offhand.clone();
    }

    public static ItemStack takeNext(PlayerInventory inventory) {
        if (inventory == null) return null;
        for (int slot = 0; slot < inventory.getStorageContents().length; slot++) {
            ItemStack item = inventory.getItem(slot);
            if (!isTotem(item)) continue;
            ItemStack next = new ItemStack(Material.TOTEM_OF_UNDYING);
            int remainingAmount = remainingAfterTakingOne(item.getAmount());
            if (remainingAmount == 0) inventory.setItem(slot, null);
            else {
                ItemStack remaining = item.clone();
                remaining.setAmount(remainingAmount);
                inventory.setItem(slot, remaining);
            }
            return next;
        }
        return null;
    }

    private static int count(ItemStack[] items) {
        if (items == null) return 0;
        return Arrays.stream(items).mapToInt(TotemInventory::amount).sum();
    }

    private static int amount(ItemStack item) {
        return isTotem(item) ? Math.max(0, item.getAmount()) : 0;
    }

    private static boolean isTotem(ItemStack item) {
        return item != null && item.getType() == Material.TOTEM_OF_UNDYING;
    }
}
