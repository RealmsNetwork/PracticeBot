package com.sheldera.practicebot.combat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModernWeaponCombatTest {
   @Test
   void maceSmashUsesVanillaTierBreakpoints() {
      assertEquals(0.0, ModernWeaponCombat.maceSmashBonus(1.5), 1.0E-9);
      assertEquals(12.0, ModernWeaponCombat.maceSmashBonus(3.0), 1.0E-9);
      assertEquals(22.0, ModernWeaponCombat.maceSmashBonus(8.0), 1.0E-9);
      assertEquals(24.0, ModernWeaponCombat.maceSmashBonus(10.0), 1.0E-9);
   }

   @Test
   void netheriteSpearChargeUsesVelocityMultiplier() {
      assertEquals(6.0,
         ModernWeaponCombat.spearChargeDamage(5.612, 1.20), 0.0001);
   }

   @Test
   void spearChargeFloorsTheKineticDamage() {
      assertEquals(6.0,
         ModernWeaponCombat.spearChargeDamage(5.612, 1.20), 1.0E-9);
      assertEquals(0.0,
         ModernWeaponCombat.spearChargeDamage(2.0, 1.20), 1.0E-9);
   }
}
