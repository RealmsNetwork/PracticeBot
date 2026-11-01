package com.sheldera.practicebot.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PracticeBotWeaponProfileTest {
    @Test
    void parsesAerialAndHybridWeaponProfiles() {
        assertEquals(PracticeBotWeaponProfile.MACE_ELYTRA,
            PracticeBotWeaponProfile.fromName("MACE_ELYTRA"));
        assertEquals(PracticeBotWeaponProfile.SPEAR_ELYTRA,
            PracticeBotWeaponProfile.fromName("spear_elytra"));
        assertEquals(PracticeBotWeaponProfile.SPEAR_MACE,
            PracticeBotWeaponProfile.fromName("SPEAR_MACE"));
    }

    @Test
    void invalidProfileFallsBackToSword() {
        assertEquals(PracticeBotWeaponProfile.SWORD,
            PracticeBotWeaponProfile.fromName("not-a-profile"));
    }
}
