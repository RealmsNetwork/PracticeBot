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
   void maceSmashStartsAtVanillaOnePointFiveBlockThreshold() {
      assertEquals(true,
         ModernWeaponCombat.shouldPerformMaceSmash(true, 1.5, false));
      assertEquals(true,
         ModernWeaponCombat.shouldPerformMaceSmash(true, 1.5001, false));
      assertEquals(false,
         ModernWeaponCombat.shouldPerformMaceSmash(true, 1.4999, false));
      assertEquals(false,
         ModernWeaponCombat.shouldPerformMaceSmash(false, 2.0, false));
      assertEquals(false,
         ModernWeaponCombat.shouldPerformMaceSmash(true, 2.0, true));
      assertEquals(false,
         ModernWeaponCombat.shouldPerformMaceSmash(true, Double.NaN, false));
   }

   @Test
   void stunSlamIsOptInAndRequiresAnAxe() {
      assertEquals(false, ModernWeaponCombat.shouldAttemptStunSlam(false, true, true, true));
      assertEquals(false, ModernWeaponCombat.shouldAttemptStunSlam(true, true, true, false));
      assertEquals(false, ModernWeaponCombat.shouldAttemptStunSlam(true, true, false, true));
      assertEquals(true, ModernWeaponCombat.shouldAttemptStunSlam(true, true, true, true));
      assertEquals(true, ModernWeaponCombat.shouldAttemptStunSlam(true, false, false, true));
   }

   @Test
   void lungeJabIsOnlyUsedWithLungeOutsideNormalJabRange() {
      assertEquals(true, ModernWeaponCombat.shouldUseLungeJab(4.0, 4.5, 3, false, false));
      assertEquals(false, ModernWeaponCombat.shouldUseLungeJab(3.0, 4.5, 3, false, false));
      assertEquals(false, ModernWeaponCombat.shouldUseLungeJab(4.0, 4.5, 0, false, false));
      assertEquals(false, ModernWeaponCombat.shouldUseLungeJab(4.0, 4.5, 3, true, false));
      assertEquals(false, ModernWeaponCombat.shouldUseLungeJab(4.0, 4.5, 3, false, true));
      assertEquals(false, ModernWeaponCombat.shouldUseLungeJab(5.0, 4.5, 3, false, false));
   }

   @Test
   void stunSlamFollowupStaysInsideTheFiveTickWindow() {
      assertEquals(2, ModernWeaponCombat.stunSlamDelayTicks(2, 5));
      assertEquals(5, ModernWeaponCombat.stunSlamDelayTicks(8, 5));
      assertEquals(1, ModernWeaponCombat.stunSlamDelayTicks(0, 5));
      assertEquals(1, ModernWeaponCombat.stunSlamDelayTicks(2, 0));
   }

   @Test
   void bareMaceAtTwoBlocksMatchesPublishedExample() {
      assertEquals(14.0,
         ModernWeaponCombat.maceSmashDamage(6.0, 2.0, 0), 1.0E-9);
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

   @Test
   void densityAddsPerFallenBlock() {
      assertEquals(17.0,
         ModernWeaponCombat.maceSmashDamage(6.0, 2.0, 3), 1.0E-9);
   }

   @Test
   void baseDamageOverloadNeverDropsBelowBase() {
      assertEquals(5.0,
         ModernWeaponCombat.spearChargeDamage(5.0, 2.0, 1.20), 1.0E-9);
   }
}
