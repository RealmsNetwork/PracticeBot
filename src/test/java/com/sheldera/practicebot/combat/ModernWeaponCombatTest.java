package com.sheldera.practicebot.combat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModernWeaponCombatTest {

    @Test
    void maceFalloffUsesVanillaBreakpoints() {
        assertEquals(0.0D, ModernWeaponCombat.maceSmashBonus(1.5D), 1.0E-9D);
        assertEquals(12.0D, ModernWeaponCombat.maceSmashBonus(3.0D), 1.0E-9D);
        assertEquals(22.0D, ModernWeaponCombat.maceSmashBonus(8.0D), 1.0E-9D);
        assertEquals(24.0D, ModernWeaponCombat.maceSmashBonus(10.0D), 1.0E-9D);
    }

    @Test
    void bareMaceAtTwoBlocksMatchesPublishedExample() {
        // Player base + a bare Mace's attack contribution is 6 damage.
        // 2 blocks of fall adds 8, giving the documented 14 damage result.
        assertEquals(
            14.0D,
            ModernWeaponCombat.maceSmashDamage(6.0D, 2.0D, 0),
            1.0E-9D
        );
    }

    @Test
    void densityAddsPerFallenBlock() {
        assertEquals(
            17.0D,
            ModernWeaponCombat.maceSmashDamage(6.0D, 2.0D, 3),
            1.0E-9D
        );
    }

    @Test
    void spearChargeUsesPublishedKineticMultiplier() {
        assertEquals(
            6.0D,
            ModernWeaponCombat.spearChargeDamage(5.0D, 5.1D, 1.2D),
            1.0E-9D
        );
    }

    @Test
    void spearChargeMinimumStillUsesBaseDamage() {
        assertEquals(
            5.0D,
            ModernWeaponCombat.spearChargeDamage(5.0D, 4.6D, 1.2D),
            1.0E-9D
        );
    }
}
