package com.sheldera.practicebot.api.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TotemInventoryTest {
    @Test
    void countsAllRemainingKitStacks() {
        assertEquals(7, TotemInventory.countAmounts(5, 1, 1));
    }

    @Test
    void takingOneTotemPreservesTheRemainingStackAmount() {
        assertEquals(3, TotemInventory.remainingAfterTakingOne(4));
        assertEquals(0, TotemInventory.remainingAfterTakingOne(1));
        assertEquals(0, TotemInventory.remainingAfterTakingOne(0));
    }
}
