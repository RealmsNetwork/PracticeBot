package com.sheldera.practicebot;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        String[] allKits = {
            "tank", "axeandshield", "nethpot", "smpkit", "diamondpot",
            "bowpvp", "beast", "diasmp", "cpvpffa", "cartpvp",
            "builduhc", "macepvp", "macerocket", "spearelytra", "spearmace"
        };
        String[] allTiers = {
            "EASY", "NORMAL", "HARD", "EXPERT", "HACKER", "MASTER", "NIGHTMARE"
        };

        for (String kit : allKits) {
            for (String tier : allTiers) {
                String key = kit + "_" + tier.toLowerCase(Locale.ROOT) + "_Example";
                assertEquals(kit, TemplateDifficultyTuning.kitFromTemplateKey(key), key + " kit");
                assertEquals(tier, TemplateDifficultyTuning.difficultyFromTemplateKey(key), key + " tier");
            }
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
    void bundledConfigContainsAllTemplateDifficultyProfilesAndParsesAsYaml() throws Exception {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            assertNotNull(stream, "bundled config.yml must be present");

            FileConfiguration config;
            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                config = YamlConfiguration.loadConfiguration(reader);
            }

            assertEquals(9, config.getInt("config-version"));
            assertTrue(config.getBoolean("modern-weapons.spear.wind-charge-approach.enabled"));
            assertFalse(config.getBoolean("modern-weapons.mace.stun-slam.enabled"));
            assertTrue(config.getBoolean("tactics.attribute-swap.enabled"));
            assertTrue(config.getDouble("tactics.cart-pvp.max-distance") <= 8.0D);

            String[] tiers = {
                "EASY", "NORMAL", "HARD", "EXPERT", "HACKER", "MASTER", "NIGHTMARE"
            };

            for (String tier : tiers) {
                assertNotNull(config.getConfigurationSection(
                    "template-difficulty.defaults." + tier), tier + " shared defaults");
                assertNotNull(config.getConfigurationSection(
                    "template-difficulty.profiles.CART." + tier), tier + " cart profile");
                assertTrue(config.getDouble("template-difficulty.profiles.CART." + tier + ".cart-max-distance") <= 8.0D,
                    tier + " cart range must remain close-range");
                assertNotNull(config.getConfigurationSection(
                    "template-difficulty.profiles.MACE." + tier), tier + " mace profile");
                assertNotNull(config.getConfigurationSection(
                    "template-difficulty.profiles.SPEAR." + tier), tier + " spear profile");
                assertNotNull(config.getConfigurationSection(
                    "modern-weapons.mace.difficulty-levels." + tier), tier + " legacy mace profile");
            }
        }
    }

    @Test
    void applyingANewTemplateClearsStaleTacticalState() {
        BotTrait trait = new BotTrait();
        trait.simulatedPingMs = 150;
        trait.lastPingDriftAt = 900L;
        trait.tacticNextDecisionAt = 800L;
        trait.tacticCooldownUntil = 700L;
        trait.windChargeResetCooldownUntil = 600L;
        trait.elytraFlightUntil = 500L;
        trait.lastCartActionAt = 400L;
        trait.lastStrafeSwitchAt = 300L;
        trait.lastRocketBoostAt = 200L;
        trait.strafeDirection = -1;
        trait.lastTactic = "stale-tactic";

        trait.setTemplateFromKey("macepvp_easy_1Wenzy");

        assertEquals("macepvp", trait.getTemplateKit());
        assertEquals("EASY", trait.getTemplateDifficulty());
        assertEquals(0, trait.simulatedPingMs);
        assertEquals(0L, trait.lastPingDriftAt);
        assertEquals(0L, trait.tacticNextDecisionAt);
        assertEquals(0L, trait.tacticCooldownUntil);
        assertEquals(0L, trait.windChargeResetCooldownUntil);
        assertEquals(0L, trait.elytraFlightUntil);
        assertEquals(0L, trait.lastCartActionAt);
        assertEquals(0L, trait.lastStrafeSwitchAt);
        assertEquals(0L, trait.lastRocketBoostAt);
        assertEquals(1, trait.strafeDirection);
        assertEquals("none", trait.lastTactic);
    }

    @Test
    void directlySpawnedBotsStillUseTheirCombatAggressionAsFallback() {
        assertEquals("EASY", TemplateDifficultyTuning.difficultyFromAggression(0));
        assertEquals("NORMAL", TemplateDifficultyTuning.difficultyFromAggression(1));
        assertEquals("HARD", TemplateDifficultyTuning.difficultyFromAggression(2));
    }

    @Test
    void v8CartDefaultsMigrateToCloseRangeButKeepCustomRanges() {
        YamlConfiguration legacy = new YamlConfiguration();
        legacy.set("tactics.cart-pvp.min-distance", 5.0D);
        legacy.set("tactics.cart-pvp.max-distance", 18.0D);
        legacy.set("template-difficulty.profiles.CART.NIGHTMARE.cart-min-distance", 4.0D);
        legacy.set("template-difficulty.profiles.CART.NIGHTMARE.cart-max-distance", 22.0D);
        legacy.set("template-difficulty.profiles.CART.HARD.cart-max-distance", 12.0D);

        ConfigMigrator.migrateCartCombatDefaults(legacy);

        assertEquals(2.5D, legacy.getDouble("tactics.cart-pvp.min-distance"));
        assertEquals(7.5D, legacy.getDouble("tactics.cart-pvp.max-distance"));
        assertEquals(2.5D, legacy.getDouble("template-difficulty.profiles.CART.NIGHTMARE.cart-min-distance"));
        assertEquals(8.0D, legacy.getDouble("template-difficulty.profiles.CART.NIGHTMARE.cart-max-distance"));
        assertEquals(12.0D, legacy.getDouble("template-difficulty.profiles.CART.HARD.cart-max-distance"));
    }

    @Test
    void spearDefaultInventoryIncludesWindChargesForNonElytraApproach() throws Exception {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("default_inv.yml")) {
            assertNotNull(stream);
            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                YamlConfiguration inventory = YamlConfiguration.loadConfiguration(reader);
                assertEquals(4, inventory.getInt("inventory-config-version"));
                assertEquals(12, inventory.getInt("weapon.spear.wind-charges"));
            }
        }
    }

}
