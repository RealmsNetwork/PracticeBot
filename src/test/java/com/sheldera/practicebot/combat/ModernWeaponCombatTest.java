package com.sheldera.practicebot.combat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModernWeaponCombatTest {
   @Test
   void maceSmashUsesVanillaTierBreakpoints() {
      assertEquals(0.0, ModernWeaponCombat.maceSmashBonus(1.5), 1.0E-9);
      assertEquals(6.0004, ModernWeaponCombat.maceSmashBonus(1.5001), 1.0E-6);
      assertEquals(12.0, ModernWeaponCombat.maceSmashBonus(3.0), 1.0E-9);
      assertEquals(22.0, ModernWeaponCombat.maceSmashBonus(8.0), 1.0E-9);
      assertEquals(24.0, ModernWeaponCombat.maceSmashBonus(10.0), 1.0E-9);
   }

   @Test
   void maceWindChargeLaunchRangeIncludesNormalMeleeNavigationDistance() {
      assertEquals(true, ModernWeaponCombat.shouldLaunchMaceWindCharge(
         3.0, 2.0, 10.0, true, true, true, false));
      assertEquals(false, ModernWeaponCombat.shouldLaunchMaceWindCharge(
         1.9, 2.0, 10.0, true, true, true, false));
      assertEquals(false, ModernWeaponCombat.shouldLaunchMaceWindCharge(
         10.1, 2.0, 10.0, true, true, true, false));
      assertEquals(false, ModernWeaponCombat.shouldLaunchMaceWindCharge(
         3.0, 2.0, 10.0, true, false, true, false));
      assertEquals(false, ModernWeaponCombat.shouldLaunchMaceWindCharge(
         3.0, 2.0, 10.0, true, true, false, false));
      assertEquals(false, ModernWeaponCombat.shouldLaunchMaceWindCharge(
         3.0, 2.0, 10.0, true, true, true, true));
   }

   @Test
   void maceFallDistanceUsesTheGreaterOfNativeAndTrackedApexDistance() {
      assertEquals(2.5, ModernWeaponCombat.resolveMaceFallDistance(1.0, 80.0, 77.5), 1.0E-9);
      assertEquals(3.0, ModernWeaponCombat.resolveMaceFallDistance(3.0, 80.0, 79.0), 1.0E-9);
      assertEquals(0.0, ModernWeaponCombat.resolveMaceFallDistance(Double.NaN, 80.0, 82.0), 1.0E-9);
   }

   @Test
   void maceSmashRequiresFallDistanceGreaterThanOnePointFiveBlocksForBonusDamage() {
      assertEquals(false,
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
      assertEquals(true, ModernWeaponCombat.shouldUseLungeJab(4.0, 4.5, 3, false, false, 20));
      assertEquals(false, ModernWeaponCombat.shouldUseLungeJab(3.0, 4.5, 3, false, false, 20));
      assertEquals(false, ModernWeaponCombat.shouldUseLungeJab(4.0, 4.5, 0, false, false, 20));
      assertEquals(false, ModernWeaponCombat.shouldUseLungeJab(4.0, 4.5, 3, true, false, 20));
      assertEquals(false, ModernWeaponCombat.shouldUseLungeJab(4.0, 4.5, 3, false, true, 20));
      assertEquals(false, ModernWeaponCombat.shouldUseLungeJab(5.0, 4.5, 3, false, false, 20));
      assertEquals(false, ModernWeaponCombat.shouldUseLungeJab(4.0, 4.5, 3, false, false, 5));
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
   @Test
   void reachSwapIsBoundedByBothDonorAndConfiguredReach() {
      assertEquals(4.625,
         ModernWeaponCombat.resolveReachSwapDistance(3.125, 4.625, 5.0, true), 1.0E-9);
      assertEquals(4.0,
         ModernWeaponCombat.resolveReachSwapDistance(3.125, 4.625, 4.0, true), 1.0E-9);
      assertEquals(3.125,
         ModernWeaponCombat.resolveReachSwapDistance(3.125, 4.625, 4.0, false), 1.0E-9);
      assertEquals(3.125,
         ModernWeaponCombat.resolveReachSwapDistance(3.125, 4.625, Double.NaN, true), 1.0E-9);
      assertEquals(3.125,
         ModernWeaponCombat.resolveReachSwapDistance(3.125, 3.0, 4.0, true), 1.0E-9);
   }

}
