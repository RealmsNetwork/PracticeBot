package com.sheldera.practicebot.combat;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.api.PracticeBotWeaponProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PvpTacticsEngineTest {
    @Test
    void maceRocketIsARecognizedWeaponProfileAndUsesElytraCombat() {
        BotTrait trait = new BotTrait();
        trait.setWeaponProfile("mace_rocket");

        assertEquals("mace_rocket", trait.getWeaponProfile());
        assertEquals(PracticeBotWeaponProfile.MACE_ROCKET,
            PracticeBotWeaponProfile.fromName("mace_rocket"));
        assertTrue(PvpTacticsEngine.usesElytraProfile(trait.getWeaponProfile()));
    }

    @Test
    void ordinaryMaceDoesNotAccidentallyEnterElytraMode() {
        assertFalse(PvpTacticsEngine.usesElytraProfile("mace"));
        assertTrue(PvpTacticsEngine.usesElytraProfile("mace_elytra"));
    }
}
