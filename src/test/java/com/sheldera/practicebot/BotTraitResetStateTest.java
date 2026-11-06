package com.sheldera.practicebot;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class BotTraitResetStateTest {
    @Test
    void clearsTransientModernCombatStateOnRespawn() {
        BotTrait trait = new BotTrait();

        trait.modernWeaponCooldownUntil = 9000L;
        trait.maceLaunchStartedAt = 8000L;
        trait.maceLaunchActive = true;
        trait.maceWindChargePending = true;
        trait.maceRestoreAt = 7000L;
        trait.maceAttemptedThisFall = true;
        trait.maceMissThisAttack = true;
        trait.maceMissOffsetDegrees = 7.5D;
        trait.maceLaunchOriginY = 90.0D;
        trait.macePeakY = 100.0D;
        trait.spearChargeStartedAt = 6000L;
        trait.spearLastHitAt = 5000L;
        trait.modernWeaponTarget = UUID.randomUUID();
        trait.simulatedPingMs = 145;
        trait.tacticNextDecisionAt = 4000L;
        trait.tacticCooldownUntil = 3000L;
        trait.windChargeResetCooldownUntil = 2000L;
        trait.spearWindChargeLaunchedAt = 1500L;
        trait.elytraFlightUntil = 1000L;
        trait.lastCartActionAt = 900L;
        trait.lastPingDriftAt = 800L;
        trait.lastRocketBoostAt = 700L;
        trait.strafeDirection = -1;
        trait.lastTactic = "stale-tactic";

        trait.resetAllStates();

        assertEquals(0L, trait.modernWeaponCooldownUntil);
        assertEquals(0L, trait.maceLaunchStartedAt);
        assertFalse(trait.maceLaunchActive);
        assertFalse(trait.maceWindChargePending);
        assertEquals(0L, trait.maceRestoreAt);
        assertFalse(trait.maceAttemptedThisFall);
        assertFalse(trait.maceMissThisAttack);
        assertEquals(0.0D, trait.maceMissOffsetDegrees);
        assertEquals(0.0D, trait.maceLaunchOriginY);
        assertEquals(0.0D, trait.macePeakY);
        assertEquals(0L, trait.spearChargeStartedAt);
        assertEquals(0L, trait.spearLastHitAt);
        assertNull(trait.modernWeaponTarget);
        assertEquals(0, trait.simulatedPingMs);
        assertEquals(0L, trait.tacticNextDecisionAt);
        assertEquals(0L, trait.tacticCooldownUntil);
        assertEquals(0L, trait.windChargeResetCooldownUntil);
        assertEquals(0L, trait.spearWindChargeLaunchedAt);
        assertEquals(0L, trait.elytraFlightUntil);
        assertEquals(0L, trait.lastCartActionAt);
        assertEquals(0L, trait.lastPingDriftAt);
        assertEquals(0L, trait.lastRocketBoostAt);
        assertEquals(1, trait.strafeDirection);
        assertEquals("none", trait.lastTactic);
    }
}
