package com.sheldera.practicebot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaceDifficultyTuningTest {
    @Test
    void readsDifficultyFromStandardTemplateKey() {
        assertEquals("EASY",
            MaceDifficultyTuning.difficultyFromTemplateKey("macepvp_easy_1Wenzy"));
        assertEquals("HACKER",
            MaceDifficultyTuning.difficultyFromTemplateKey("macerocket_hacker_Marlowww"));
        assertEquals("NIGHTMARE",
            MaceDifficultyTuning.difficultyFromTemplateKey("spearmace_nightmare_janekv"));
    }

    @Test
    void invalidOrNonTieredKeysLeaveDifficultyUnset() {
        assertEquals("",
            MaceDifficultyTuning.difficultyFromTemplateKey("macepvp"));
        assertEquals("",
            MaceDifficultyTuning.difficultyFromTemplateKey("macepvp_legend_Example"));
    }

    @Test
    void directMaceBotsFallBackToCombatAggression() {
        assertEquals("EASY", MaceDifficultyTuning.difficultyFromAggression(0));
        assertEquals("NORMAL", MaceDifficultyTuning.difficultyFromAggression(1));
        assertEquals("HARD", MaceDifficultyTuning.difficultyFromAggression(2));
    }
}
