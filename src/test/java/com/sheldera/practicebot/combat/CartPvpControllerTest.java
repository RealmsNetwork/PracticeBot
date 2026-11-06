package com.sheldera.practicebot.combat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartPvpControllerTest {
    @Test
    void cartDetonationRequiresAValidSelfSafetyDistance() {
        assertTrue(CartPvpController.isOutsideSelfSafetyRadius(6.0D, 6.0D));
        assertFalse(CartPvpController.isOutsideSelfSafetyRadius(5.99D, 6.0D));
        assertFalse(CartPvpController.isOutsideSelfSafetyRadius(Double.NaN, 6.0D));
        assertFalse(CartPvpController.isOutsideSelfSafetyRadius(8.0D, Double.NaN));
    }

    @Test
    void cartingIsLimitedToTheConfiguredCloseRangeWindow() {
        assertTrue(CartPvpController.shouldAttemptCart(5.0D, 2.5D, 7.5D, true, true, true));
        assertFalse(CartPvpController.shouldAttemptCart(18.0D, 2.5D, 7.5D, true, true, true));
        assertFalse(CartPvpController.shouldAttemptCart(1.0D, 2.5D, 7.5D, true, true, true));
        assertFalse(CartPvpController.shouldAttemptCart(5.0D, 2.5D, 7.5D, false, true, true));
        assertFalse(CartPvpController.shouldAttemptCart(5.0D, 2.5D, 7.5D, true, false, true));
        assertFalse(CartPvpController.shouldAttemptCart(5.0D, 2.5D, 7.5D, true, true, false));
    }
}
