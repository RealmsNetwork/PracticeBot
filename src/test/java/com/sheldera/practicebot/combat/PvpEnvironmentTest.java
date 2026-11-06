package com.sheldera.practicebot.combat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PvpEnvironmentTest {
    @Test
    void attributeSwapUsesExplicitPaperConfigValueRegardlessOfServerBrand() {
        // The server implementation name is deliberately not part of this gate.
        assertTrue(PvpEnvironment.supportsAttributeSwapping(true, false));
    }

    @Test
    void attributeSwapRequiresExplicitFalseSetting() {
        assertFalse(PvpEnvironment.supportsAttributeSwapping(true, true));
        assertFalse(PvpEnvironment.supportsAttributeSwapping(false, false));
    }
}
