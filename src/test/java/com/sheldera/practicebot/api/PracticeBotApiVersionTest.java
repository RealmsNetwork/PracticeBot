package com.sheldera.practicebot.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PracticeBotApiVersionTest {
    @Test
    void exposesTheVersionRequiredForTemporaryTemplateAndDirectInventoryApi() {
        assertEquals("1.10.0", PracticeBotApi.API_VERSION);
    }
}
