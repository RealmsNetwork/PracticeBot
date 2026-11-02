package com.sheldera.practicebot.combat;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class NmsCombatBridgeTest {
    @Test
    void nativePacketBridgeRejectsMissingEntitiesSafely() {
        assertFalse(NmsCombatBridge.attack(null, null));
        assertFalse(NmsCombatBridge.stabAttack(null));
        assertFalse(NmsCombatBridge.startUsingMainHand(null));
        assertFalse(NmsCombatBridge.stopUsingItem(null));
        assertFalse(NmsCombatBridge.isUsingItem(null));
    }
}
