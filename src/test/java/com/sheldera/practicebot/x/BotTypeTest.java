package com.sheldera.practicebot.x;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BotTypeTest {
    @Test
    void legacyCpvpNameLoadsAsCanonicalCrystalType() {
        assertEquals(a.CRYSTAL, a.fromName("CPVP"));
        assertEquals(a.CRYSTAL, a.fromName("crystal"));
    }

    @Test
    void allFirstClassTypesResolveFromTemplateNames() {
        assertEquals(a.NORMAL, a.fromName("NORMAL"));
        assertEquals(a.CART, a.fromName("CART"));
        assertEquals(a.MACE, a.fromName("MACE"));
        assertEquals(a.UHC, a.fromName("UHC"));
        assertEquals(a.WATER, a.fromName("WATER"));
    }

    @Test
    void crystalAndDummyAreNotStandardMeleeCombatTypes() {
        assertFalse(a.CRYSTAL.isStandardCombat());
        assertFalse(a.DUMMY.isStandardCombat());
        assertTrue(a.NORMAL.isStandardCombat());
        assertTrue(a.MACE.isStandardCombat());
        assertTrue(a.CART.isStandardCombat());
        assertTrue(a.UHC.isStandardCombat());
        assertTrue(a.WATER.isStandardCombat());
    }

    @Test
    void gameTypesProvideExpectedDefaultWeaponProfiles() {
        assertEquals("sword", a.NORMAL.defaultWeaponProfile());
        assertEquals("cart", a.CART.defaultWeaponProfile());
        assertEquals("mace", a.MACE.defaultWeaponProfile());
        assertEquals("uhc", a.UHC.defaultWeaponProfile());
        assertEquals("water", a.WATER.defaultWeaponProfile());
    }
}
