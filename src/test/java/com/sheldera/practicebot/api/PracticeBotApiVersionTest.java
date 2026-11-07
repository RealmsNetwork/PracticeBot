package com.sheldera.practicebot.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PracticeBotApiVersionTest {
    @Test
    void exposesTheVersionRequiredForExtendedCombatProfiles() {
        assertEquals("1.12.0", PracticeBotApi.API_VERSION);
    }
}
