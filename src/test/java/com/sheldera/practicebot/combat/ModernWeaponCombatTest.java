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
    void spearChargeUsesRelativeVelocityModel() {
        assertEquals(
            6.0D,
            ModernWeaponCombat.spearChargeDamage(5.0D, 6.2D, 1.0D, 1.0D),
            1.0E-9D
        );
    }

    @Test
    void spearChargeCannotDropBelowBaseDamage() {
        assertEquals(
            5.0D,
            ModernWeaponCombat.spearChargeDamage(5.0D, 1.0D, 1.0D, 0.0D),
            1.0E-9D
        );
    }
}
