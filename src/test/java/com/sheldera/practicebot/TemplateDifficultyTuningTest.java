package com.sheldera.practicebot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemplateDifficultyTuningTest {
    @Test
    void parsesCurrentAndPlannedKitPrefixesAtEveryTier() {
        String[][] cases = {
            {"tank_easy_1Wenzy", "tank", "EASY"},
            {"axeandshield_normal_Kinngg", "axeandshield", "NORMAL"},
            {"nethpot_hard_Prusso", "nethpot", "HARD"},
            {"cartpvp_expert_Freekee_Fang", "cartpvp", "EXPERT"},
            {"smpkit_hacker_Marlowww", "smpkit", "HACKER"},
            {"builduhc_master_Example", "builduhc", "MASTER"},
            {"diamondpot_nightmare_janekv", "diamondpot", "NIGHTMARE"},
            {"bowpvp_easy_1Wenzy", "bowpvp", "EASY"},
            {"macepvp_easy_1Wenzy", "macepvp", "EASY"},
            {"cpvpffa_hacker_Marlowww", "cpvpffa", "HACKER"},
            {"diasmp_master_coldified", "diasmp", "MASTER"},
            {"macerocket_normal_Example", "macerocket", "NORMAL"},
            {"spearelytra_expert_Example", "spearelytra", "EXPERT"},
            {"spearmace_nightmare_Example", "spearmace", "NIGHTMARE"},
            {"beast_hard_TheRandomizer", "beast", "HARD"}
        };

        for (String[] testCase : cases) {
            assertEquals(testCase[1],
                TemplateDifficultyTuning.kitFromTemplateKey(testCase[0]),
                testCase[0] + " kit");
            assertEquals(testCase[2],
                TemplateDifficultyTuning.difficultyFromTemplateKey(testCase[0]),
                testCase[0] + " tier");
        }
    }

    @Test
    void invalidKeysDoNotAccidentallyBecomeTemplateOverrides() {
        assertEquals("", TemplateDifficultyTuning.kitFromTemplateKey("arena_normal"));
        assertEquals("", TemplateDifficultyTuning.difficultyFromTemplateKey("arena_normal"));
        assertEquals("", TemplateDifficultyTuning.difficultyFromTemplateKey("tank_legend_Example"));
        assertEquals("", TemplateDifficultyTuning.kitFromTemplateKey("tank__Example"));
    }

    @Test
    void directlySpawnedBotsStillUseTheirCombatAggressionAsFallback() {
        assertEquals("EASY", TemplateDifficultyTuning.difficultyFromAggression(0));
        assertEquals("NORMAL", TemplateDifficultyTuning.difficultyFromAggression(1));
        assertEquals("HARD", TemplateDifficultyTuning.difficultyFromAggression(2));
    }
}
