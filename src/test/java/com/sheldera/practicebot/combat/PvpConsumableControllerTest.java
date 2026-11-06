package com.sheldera.practicebot.combat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PvpConsumableControllerTest {
    @Test
    void foodIsUsedBeforeHungerAndSaturationBecomeCritical() {
        assertTrue(PvpConsumablePolicy.shouldEat(14, 10.0F, 14, 1.0D));
        assertTrue(PvpConsumablePolicy.shouldEat(17, 0.5F, 14, 1.0D));
        assertTrue(PvpConsumablePolicy.shouldEat(18, 0.5F, 14, 1.0D));
        assertFalse(PvpConsumablePolicy.shouldEat(18, 10.0F, 14, 1.0D));
        assertFalse(PvpConsumablePolicy.shouldEat(20, 0.0F, 14, 1.0D));
    }
}
