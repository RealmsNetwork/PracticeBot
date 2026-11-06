package com.sheldera.practicebot.combat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PvpEnvironmentTest {
    @Test
    void attributeSwapRequiresPaperAndAnExplicitFalseSetting() {
        assertTrue(PvpEnvironment.supportsAttributeSwapping(true, true, false));
        assertFalse(PvpEnvironment.supportsAttributeSwapping(true, true, true));
        assertFalse(PvpEnvironment.supportsAttributeSwapping(true, false, false));
        assertFalse(PvpEnvironment.supportsAttributeSwapping(false, true, false));
    }
}
