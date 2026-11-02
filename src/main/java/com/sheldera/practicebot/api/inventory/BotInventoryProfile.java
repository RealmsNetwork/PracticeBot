package com.sheldera.practicebot.api.inventory;

import org.bukkit.inventory.ItemStack;

/** Immutable snapshot of a bot/player inventory layout. ItemStacks are cloned at the API boundary. */
public final class BotInventoryProfile {
    private final ItemStack[] storage;
    private final ItemStack[] armor;
    private final ItemStack offhand;
    private final int heldSlot;

    public BotInventoryProfile(ItemStack[] storage, ItemStack[] armor, ItemStack offhand, int heldSlot) {
        this.storage = cloneItems(storage, 36);
        this.armor = cloneItems(armor, 4);
        this.offhand = offhand == null ? null : offhand.clone();
        this.heldSlot = Math.max(0, Math.min(8, heldSlot));
    }

    public ItemStack[] getStorage() {
        return cloneItems(this.storage, 36);
    }

    public ItemStack[] getArmor() {
        return cloneItems(this.armor, 4);
    }

    public ItemStack getOffhand() {
        return this.offhand == null ? null : this.offhand.clone();
    }

    public int getHeldSlot() {
        return this.heldSlot;
    }

    private static ItemStack[] cloneItems(ItemStack[] source, int size) {
        ItemStack[] copy = new ItemStack[size];
        if (source == null) return copy;
        for (int i = 0; i < Math.min(source.length, size); i++) {
            copy[i] = source[i] == null ? null : source[i].clone();
        }
        return copy;
    }

    @Override
    public String toString() {
        return "BotInventoryProfile{storage=" + this.storage.length + ", armor=" + this.armor.length + ", offhand=" +
            (this.offhand == null ? "empty" : this.offhand.getType()) + ", heldSlot=" + this.heldSlot + '}';
    }
}
