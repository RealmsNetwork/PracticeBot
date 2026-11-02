package com.sheldera.practicebot.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PracticeBotApiVersionTest {
    @Test
    void exposesTheVersionRequiredForTemplateCloningAndDeletion() {
        assertEquals("1.8.4", PracticeBotApi.API_VERSION);
    }
}
