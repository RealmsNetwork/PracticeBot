package com.sheldera.practicebot.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.sheldera.practicebot.api.inventory.BotInventoryProfile;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

class BotInventoryProfileTest {
    @Test
    void profileDefensivelyCopiesInventoryArrays() {
        ItemStack[] storage = new ItemStack[36];
        ItemStack[] armor = new ItemStack[4];
        BotInventoryProfile profile = new BotInventoryProfile(storage, armor, null, 4);

        assertNotSame(storage, profile.getStorage());
        assertNotSame(armor, profile.getArmor());
        assertNotSame(profile.getStorage(), profile.getStorage());
        assertEquals(4, profile.getHeldSlot());
    }

    @Test
    void profileNormalizesArraySizesAndHeldSlot() {
        BotInventoryProfile profile = new BotInventoryProfile(null, null, null, 20);

        assertEquals(36, profile.getStorage().length);
        assertEquals(4, profile.getArmor().length);
        assertEquals(8, profile.getHeldSlot());
        assertNull(profile.getStorage()[0]);
        assertNull(profile.getOffhand());
    }
}
