package com.sheldera.practicebot;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ConfigMigrator {
    private static final int CURRENT_VERSION = 11;
    private static final int CURRENT_INVENTORY_VERSION = 5;

    private ConfigMigrator() {
    }

    public static boolean migrate(PracticeBotPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.isFile()) {
            return false;
        }

        FileConfiguration config =
            YamlConfiguration.loadConfiguration(file);

        int current = config.getInt("config-version", 1);

        if (current >= CURRENT_VERSION) {
            return false;
        }

        // The version guard above guarantees current < CURRENT_VERSION, so
        // apply the v11 migration without a redundant always-true comparison.
        migrateCartCombatDefaults(config);
        // Attribute swapping was previously opt-in. The new spear/mace
        // approach uses it when the Paper runtime explicitly supports it.
        replaceIfOldDefault(config, "tactics.attribute-swap.enabled", false, true);
        // Mace's normal melee reach is about 3.1 blocks. The old 3.2 launch
        // floor could be outside the bot's navigation stop distance, causing
        // it to skip the Wind Charge and repeatedly use grounded melee.
        replaceIfOldDefault(config, "modern-weapons.mace.launch-min-distance", 3.2D, 2.0D);

        backup(plugin, file);

        Map<String, Object> defaults = new LinkedHashMap<>();

        defaults.put("config-version", CURRENT_VERSION);

        // Shared defaults for every <kit>_<difficulty>_<player> template.
        // These additions are non-destructive: user-defined values remain intact.
        String[] templateDifficultyLevels = {
            "EASY", "NORMAL", "HARD", "EXPERT", "HACKER", "MASTER", "NIGHTMARE"
        };
        String[] commonDifficultySettings = {
            "ping-min-ms",
            "ping-max-ms",
            "ping-jitter-ms",
            "ping-drift-ms",
            "ping-drift-interval-ms",
            "ping-action-latency-fraction",
            "reaction-min-ms",
            "reaction-max-ms",
            "aim-min-lead-seconds",
            "aim-lead-ping-factor",
            "aim-target-offset",
            "aim-max-degrees-per-update",
            "aim-jitter-degrees",
            "movement-enabled",
            "movement-jitter",
            "strafe-switch-ms",
            "strafe-direction-change-chance",
            "mistake-chance-percent",
            "mistake-delay-min-ms",
            "mistake-delay-max-ms",
            "attack-delay-min-ms",
            "attack-delay-max-ms",
            "pearl-enabled",
            "pearl-low-health-threshold",
            "pearl-min-distance",
            "pearl-max-distance",
            "pearl-chance-percent",
            "pearl-cooldown-ms"
        };
        Object[][] commonDifficultyDefaults = {
            {80, 160, 16, 24, 5000, 0.6D, 220, 360, 0, 0, 0.34D, 12, 1.4D, true, 0.008D, 700, 0.14D, 18, 70, 160, 70, 160, true, 0.28D, 4, 10, 15, 10000},
            {60, 130, 12, 20, 6000, 0.52D, 180, 300, 0.02D, 0.25D, 0.24D, 16, 1, true, 0.011D, 560, 0.17D, 13, 50, 120, 50, 120, true, 0.3D, 3.5D, 11, 28, 8500},
            {45, 100, 10, 15, 6500, 0.45D, 140, 230, 0.03D, 0.35D, 0.18D, 20, 0.75D, true, 0.014D, 460, 0.2D, 9, 35, 90, 35, 90, true, 0.33D, 3, 12, 40, 7600},
            {35, 85, 8, 12, 7000, 0.4D, 110, 185, 0.03D, 0.45D, 0.13D, 26, 0.55D, true, 0.017D, 390, 0.23D, 6, 25, 70, 25, 70, true, 0.35D, 2.5D, 13, 48, 7000},
            {25, 70, 7, 10, 7500, 0.35D, 85, 155, 0.03D, 0.55D, 0.09D, 32, 0.4D, true, 0.02D, 320, 0.25D, 3, 15, 55, 15, 55, true, 0.38D, 2.5D, 14, 55, 6500},
            {20, 60, 6, 8, 8000, 0.3D, 65, 125, 0.03D, 0.6D, 0.065D, 38, 0.28D, true, 0.022D, 270, 0.28D, 1, 8, 35, 8, 35, true, 0.4D, 2.5D, 15, 65, 6000},
            {15, 50, 5, 7, 9000, 0.25D, 45, 95, 0.03D, 0.65D, 0.04D, 45, 0.18D, true, 0.025D, 230, 0.3D, 0.5D, 4, 20, 4, 20, true, 0.42D, 2.5D, 16, 75, 5500}
        };

        for (int tierIndex = 0; tierIndex < templateDifficultyLevels.length; tierIndex++) {
            for (int settingIndex = 0; settingIndex < commonDifficultySettings.length; settingIndex++) {
                defaults.put(
                    "template-difficulty.defaults." +
                        templateDifficultyLevels[tierIndex] + "." +
                        commonDifficultySettings[settingIndex],
                    commonDifficultyDefaults[tierIndex][settingIndex]
                );
            }
        }


        String[] maceElytraDifficultySettings = {
            "elytra-start-min-distance",
            "elytra-start-max-distance",
            "elytra-launch-y",
            "elytra-flight-window-ms",
            "elytra-rocket-interval-ms",
            "elytra-dive-distance",
            "elytra-mace-dive-y"
        };
        Object[][] maceElytraDifficultyDefaults = {
            {12L, 22L, 0.42D, 2800L, 950L, 3.6D, -1.1D},
            {11L, 24L, 0.46D, 3100L, 850L, 3.8D, -1.22D},
            {10L, 25L, 0.5D, 3400L, 780L, 4L, -1.32D},
            {9.5D, 26L, 0.54D, 3700L, 720L, 4.2D, -1.4D},
            {9L, 27L, 0.57D, 3900L, 680L, 4.3D, -1.45D},
            {8.5D, 28L, 0.6D, 4100L, 640L, 4.4D, -1.5D},
            {8L, 30L, 0.63D, 4300L, 600L, 4.5D, -1.55D}
        };

        String[] spearElytraDifficultySettings = {
            "elytra-start-min-distance",
            "elytra-start-max-distance",
            "elytra-launch-y",
            "elytra-flight-window-ms",
            "elytra-rocket-interval-ms",
            "elytra-dive-distance",
            "elytra-spear-dive-y",
            "spear-charge-delay-ms"
        };
        Object[][] spearElytraDifficultyDefaults = {
            {11L, 21L, 0.4D, 2600L, 1050L, 3.5D, -0.55D, 180L},
            {10.5D, 23L, 0.44D, 2900L, 900L, 3.8D, -0.62D, 230L},
            {10L, 24L, 0.48D, 3200L, 820L, 4L, -0.7D, 280L},
            {9.5D, 25L, 0.52D, 3500L, 760L, 4.2D, -0.78D, 330L},
            {9L, 26L, 0.56D, 3800L, 700L, 4.3D, -0.83D, 380L},
            {8.5D, 28L, 0.6D, 4000L, 650L, 4.4D, -0.88D, 400L},
            {8L, 30L, 0.63D, 4200L, 600L, 4.5D, -0.93D, 400L}
        };

        for (int tierIndex = 0; tierIndex < templateDifficultyLevels.length; tierIndex++) {
            for (int settingIndex = 0; settingIndex < maceElytraDifficultySettings.length; settingIndex++) {
                defaults.put(
                    "template-difficulty.profiles.MACE." +
                        templateDifficultyLevels[tierIndex] + "." +
                        maceElytraDifficultySettings[settingIndex],
                    maceElytraDifficultyDefaults[tierIndex][settingIndex]
                );
            }
            for (int settingIndex = 0; settingIndex < spearElytraDifficultySettings.length; settingIndex++) {
                defaults.put(
                    "template-difficulty.profiles.SPEAR." +
                        templateDifficultyLevels[tierIndex] + "." +
                        spearElytraDifficultySettings[settingIndex],
                    spearElytraDifficultyDefaults[tierIndex][settingIndex]
                );
            }
        }
        String[] cartDifficultySettings = {
            "cart-cooldown-ms",
            "cart-min-distance",
            "cart-max-distance",
            "cart-fuse-ticks",
            "cart-rail-search-radius",
            "cart-followup-delay-ms",
            "elytra-start-min-distance",
            "elytra-start-max-distance",
            "elytra-launch-y",
            "elytra-flight-window-ms",
            "elytra-rocket-interval-ms",
            "elytra-dive-distance",
            "elytra-cart-dive-y"
        };
        Object[][] cartDifficultyDefaults = {
            {7500L, 3.5D, 5.0D, 40L, 1L, 1300L, 12L, 22L, 0.42D, 2800L, 950L, 3.6D, -0.7D},
            {6500L, 3.0D, 5.5D, 38L, 1L, 1150L, 11L, 24L, 0.46D, 3100L, 850L, 3.8D, -0.76D},
            {5500L, 2.8D, 6.0D, 36L, 2L, 1000L, 10L, 25L, 0.5D, 3400L, 780L, 4L, -0.8D},
            {4800L, 2.5D, 6.5D, 35L, 2L, 900L, 9.5D, 26L, 0.54D, 3700L, 720L, 4.2D, -0.84D},
            {4200L, 2.5D, 7.0D, 34L, 2L, 850L, 9L, 27L, 0.57D, 3900L, 680L, 4.3D, -0.88D},
            {3800L, 2.5D, 7.5D, 32L, 2L, 800L, 8.5D, 28L, 0.6D, 4100L, 640L, 4.4D, -0.92D},
            {3500L, 2.5D, 8.0D, 30L, 3L, 750L, 8L, 30L, 0.63D, 4300L, 600L, 4.5D, -0.96D}
        };

        for (int tierIndex = 0; tierIndex < templateDifficultyLevels.length; tierIndex++) {
            for (int settingIndex = 0; settingIndex < cartDifficultySettings.length; settingIndex++) {
                defaults.put(
                    "template-difficulty.profiles.CART." +
                        templateDifficultyLevels[tierIndex] + "." +
                        cartDifficultySettings[settingIndex],
                    cartDifficultyDefaults[tierIndex][settingIndex]
                );
            }
        }

        defaults.put("modern-weapons.mace.stun-slam.enabled", false);
        defaults.put("modern-weapons.mace.stun-slam.require-shield", true);
        defaults.put("modern-weapons.mace.stun-slam.followup-delay-ticks", 2);
        defaults.put("modern-weapons.mace.stun-slam.window-ticks", 5);
        defaults.put("modern-weapons.spear.wind-charge-approach.enabled", true);
        defaults.put("modern-weapons.spear.wind-charge-approach.min-distance", 5.0D);
        defaults.put("modern-weapons.spear.wind-charge-approach.max-distance", 10.0D);
        defaults.put("modern-weapons.spear.wind-charge-approach.cooldown-ms", 1800L);
        defaults.put("modern-weapons.mace.wind-charge-enabled", true);
        defaults.put("modern-weapons.mace.wind-charge-launch-height", 7.0D);
        defaults.put("modern-weapons.mace.assisted-jump-without-wind-charge", false);
        defaults.put("modern-weapons.mace.launch-min-distance", 2.0D);
        defaults.put("modern-weapons.mace.launch-max-distance", 10.0D);
        defaults.put("modern-weapons.mace.miss-chance-percent", 10.0D);
        defaults.put("modern-weapons.mace.miss-offset-degrees", 6.0D);
        defaults.put("modern-weapons.mace.target-lead-seconds", 0.08D);
        defaults.put("modern-weapons.mace.aim-max-degrees-per-tick", 32.0D);
        defaults.put("modern-weapons.mace.aim-jitter-degrees", 0.15D);

        String[] maceDifficultyLevels = {
            "EASY", "NORMAL", "HARD", "EXPERT", "HACKER", "MASTER", "NIGHTMARE"
        };
        double[][] maceDifficultyDefaults = {
            {32.0D, 10.0D, 0.0D, 8.0D, 1.2D},
            {25.0D, 9.0D, 0.02D, 11.0D, 0.95D},
            {18.0D, 7.5D, 0.04D, 15.0D, 0.7D},
            {13.0D, 6.5D, 0.05D, 20.0D, 0.5D},
            {9.0D, 5.5D, 0.06D, 24.0D, 0.35D},
            {5.0D, 4.0D, 0.07D, 29.0D, 0.2D},
            {2.0D, 3.0D, 0.08D, 32.0D, 0.15D}
        };
        String[] maceDifficultySettings = {
            "miss-chance-percent",
            "miss-offset-degrees",
            "target-lead-seconds",
            "aim-max-degrees-per-tick",
            "aim-jitter-degrees"
        };

        for (int i = 0; i < maceDifficultyLevels.length; i++) {
            for (int j = 0; j < maceDifficultySettings.length; j++) {
                defaults.put(
                    "modern-weapons.mace.difficulty-levels." +
                        maceDifficultyLevels[i] + "." + maceDifficultySettings[j],
                    maceDifficultyDefaults[i][j]
                );
            }
        }

        defaults.put("modern-weapons.spear.charge-enabled", true);
        defaults.put("modern-weapons.spear.charge-delay-ms", 400L);
        defaults.put("modern-weapons.spear.contact-cooldown-ms", 500L);
        defaults.put("modern-weapons.spear.min-relative-speed-bps", 4.6D);
        defaults.put("modern-weapons.spear.charge-velocity-multiplier", 1.2D);
        defaults.put("modern-weapons.spear.base-damage", 5.0D);
        defaults.put("modern-weapons.spear.damage-max-duration-ticks", 175L);
        defaults.put("modern-weapons.spear.knockback-max-duration-ticks", 110L);
        defaults.put("modern-weapons.spear.dismount-max-duration-ticks", 50L);
        defaults.put("modern-weapons.spear.knockback-min-speed-bps", 5.1D);
        defaults.put("modern-weapons.spear.dismount-min-speed-bps", 9.0D);

        defaults.put("tactics.humanization.movement-enabled", true);
        defaults.put("tactics.humanization.movement-jitter", 0.015D);
        defaults.put("tactics.humanization.strafe-switch-ms", 380L);

        defaults.put("tactics.humanization.ping.min-ms", 35);
        defaults.put("tactics.humanization.ping.max-ms", 180);
        defaults.put("tactics.humanization.ping.jitter-ms", 8);
        defaults.put("tactics.humanization.ping.drift-ms", 18);
        defaults.put("tactics.humanization.ping.drift-interval-ms", 7000L);
        defaults.put("tactics.humanization.ping.action-latency-fraction", 0.35D);

        defaults.put("tactics.humanization.reaction.min-ms", 85.0D);
        defaults.put("tactics.humanization.reaction.max-ms", 210.0D);

        defaults.put("tactics.humanization.aim.max-degrees-per-update", 24.0D);
        defaults.put("tactics.humanization.aim.jitter-degrees", 0.6D);
        defaults.put("tactics.humanization.aim.lead-ping-factor", 0.65D);
        defaults.put("tactics.humanization.aim.target-offset", 0.10D);

        defaults.put("tactics.humanization.attack.min-delay-ms", 8.0D);
        defaults.put("tactics.humanization.attack.max-delay-ms", 42.0D);
        defaults.put("tactics.humanization.mistake-chance-percent", 3.0D);
        defaults.put("tactics.humanization.mistake-delay-min-ms", 55L);
        defaults.put("tactics.humanization.mistake-delay-max-ms", 180L);

        defaults.put("tactics.wind-charge-reset.enabled", true);
        defaults.put("tactics.wind-charge-reset.local-fallback", true);
        defaults.put("tactics.wind-charge-reset.min-fall-distance", 2.25D);
        defaults.put("tactics.wind-charge-reset.max-upward-velocity", -0.55D);
        defaults.put("tactics.wind-charge-reset.max-target-distance", 18.0D);
        defaults.put("tactics.wind-charge-reset.cooldown-ms", 650L);
        defaults.put("tactics.wind-charge-reset.plugin-pitch", 82.0D);

        defaults.put("tactics.elytra.enabled", true);
        defaults.put("tactics.elytra.start-min-distance", 9.0D);
        defaults.put("tactics.elytra.start-max-distance", 28.0D);
        defaults.put("tactics.elytra.launch-y", 0.58D);
        defaults.put("tactics.elytra.flight-window-ms", 4200L);
        defaults.put("tactics.elytra.rocket-interval-ms", 650L);
        defaults.put("tactics.elytra.dive-distance", 4.25D);
        defaults.put("tactics.elytra.mace-dive-y", -1.45D);
        defaults.put("tactics.elytra.spear-dive-y", -0.85D);
        defaults.put("tactics.elytra.mace-min-smash-fall", 2.0D);

        defaults.put("tactics.pearl.enabled", true);
        defaults.put("tactics.pearl.low-health-threshold", 0.35D);
        defaults.put("tactics.pearl.min-distance", 2.5D);
        defaults.put("tactics.pearl.max-distance", 12.0D);
        defaults.put("tactics.pearl.cooldown-ms", 7000L);
        defaults.put("tactics.pearl.chance-percent", 45.0D);

        defaults.put("tactics.attribute-swap.enabled", true);
        defaults.put("tactics.attribute-swap.chance-percent", 10.0D);

        defaults.put("tactics.fast-crystals.mode", "auto");
        defaults.put("tactics.fast-crystals.remove-artificial-delay", true);
        defaults.put("tactics.fast-crystals.timing-scale", 0.80D);

        defaults.put("tactics.cart-pvp.enabled", true);
        defaults.put("tactics.cart-pvp.cooldown-ms", 4500L);
        defaults.put("tactics.cart-pvp.min-distance", 2.5D);
        defaults.put("tactics.cart-pvp.max-distance", 7.5D);
        defaults.put("tactics.cart-pvp.fuse-ticks", 35);
        defaults.put("tactics.cart-pvp.rail-search-radius", 2);
        defaults.put("tactics.cart-pvp.flame-bow-chance-percent", 35.0D);
        defaults.put("tactics.cart-pvp.cart-followup-delay-ms", 900L);

        defaults.put("tactics.consumables.enabled", true);
        defaults.put("tactics.consumables.food-level-threshold", 14);
        defaults.put("tactics.consumables.saturation-threshold", 1.0D);
        defaults.put("tactics.consumables.heal-health-ratio", 0.62D);
        defaults.put("tactics.consumables.golden-apple-health-ratio", 0.72D);
        defaults.put("tactics.consumables.buff-safe-distance", 8.0D);
        defaults.put("tactics.consumables.debuff-max-distance", 8.0D);
        defaults.put("tactics.consumables.debuff-chance-percent", 35.0D);

        defaults.put("tactics.bow.min-distance", 5.0D);
        defaults.put("tactics.bow.melee-switch-distance", 4.25D);
        defaults.put("tactics.bow.melee-return-distance", 7.0D);
        defaults.put("tactics.bow.max-distance", 32.0D);
        defaults.put("tactics.bow.preferred-distance", 16.0D);
        defaults.put("tactics.bow.draw-min-ticks", 18);
        defaults.put("tactics.bow.draw-max-ticks", 24);
        defaults.put("tactics.bow.shot-cooldown-min-ms", 650L);
        defaults.put("tactics.bow.shot-cooldown-max-ms", 1050L);
        defaults.put("tactics.bow.miss-chance-percent", 14.0D);
        defaults.put("tactics.bow.miss-offset-degrees", 2.6D);
        defaults.put("tactics.bow.max-turn-degrees-per-tick", 12.0D);
        defaults.put("tactics.bow.aim-jitter-degrees", 0.22D);
        defaults.put("tactics.bow.strafe-switch-ms", 1100L);
        defaults.put("tactics.bow.strafe-speed", 0.12D);

        defaults.put("weapon.type", "sword");
        defaults.put("weapon.mobility.firework-rockets", 24);
        defaults.put("weapon.mace.density", 5);
        defaults.put("weapon.mace.breach", 0);
        defaults.put("weapon.mace.wind-burst", 3);
        defaults.put("weapon.mace.wind-charges", 16);
        defaults.put("weapon.spear.material", "netherite");
        defaults.put("weapon.spear.wind-charges", 12);
        defaults.put("weapon.spear.sharpness", 5);
        defaults.put("weapon.spear.lunge", 3);
        defaults.put("weapon.cart.tnt-minecarts", 16);
        defaults.put("weapon.cart.powered-rails", 16);
        defaults.put("weapon.cart.rails", 32);
        defaults.put("weapon.cart.arrows", 32);
        defaults.put("weapon.cart.power", 5);
        defaults.put("weapon.cart.punch", 2);
        defaults.put("weapon.bow.power", 5);
        defaults.put("weapon.bow.arrows", 32);
        defaults.put("weapon.uhc.golden-apples", 6);
        defaults.put("weapon.uhc.cobwebs", 8);
        defaults.put("weapon.uhc.arrows", 24);
        defaults.put("weapon.uhc.food", 16);
        defaults.put("weapon.water.buckets", 2);
        defaults.put("weapon.water.sponges", 4);

        int changed = 0;

        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            if (!config.contains(entry.getKey())) {
                config.set(entry.getKey(), entry.getValue());
                changed++;
            }
        }

        // Preserve every user value. Migration only fills missing paths.
        config.set("config-version", CURRENT_VERSION);

        if (changed == 0 && current != CURRENT_VERSION) {
            changed = 1;
        }

        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().severe(
                "Could not save migrated config.yml: " +
                    exception.getMessage()
            );
            return false;
        }

        plugin.getLogger().info(
            "Migrated config.yml from version " + current +
                " to version " + CURRENT_VERSION +
                " (" + changed + " paths updated)."
        );

        return true;
    }

    public static boolean migrateDefaultInventory(
        PracticeBotPlugin plugin,
        FileConfiguration inventory
    ) {
        if (inventory == null) {
            return false;
        }

        int current = inventory.getInt(
            "inventory-config-version", 1
        );

        if (current >= CURRENT_INVENTORY_VERSION) {
            return false;
        }

        int changed = 0;

        Map<String, Object> defaults = new LinkedHashMap<>();
        defaults.put("inventory-config-version", CURRENT_INVENTORY_VERSION);
        defaults.put("weapon.type", "sword");
        defaults.put("weapon.mobility.firework-rockets", 24);
        defaults.put("weapon.mace.density", 5);
        defaults.put("weapon.mace.breach", 0);
        defaults.put("weapon.mace.wind-burst", 3);
        defaults.put("weapon.mace.wind-charges", 16);
        defaults.put("weapon.spear.material", "netherite");
        defaults.put("weapon.spear.sharpness", 5);
        defaults.put("weapon.spear.lunge", 3);
        defaults.put("weapon.spear.wind-charges", 12);
        defaults.put("weapon.cart.tnt-minecarts", 16);
        defaults.put("weapon.cart.powered-rails", 16);
        defaults.put("weapon.cart.rails", 32);
        defaults.put("weapon.cart.arrows", 32);
        defaults.put("weapon.cart.power", 5);
        defaults.put("weapon.cart.punch", 2);
        defaults.put("weapon.bow.power", 5);
        defaults.put("weapon.bow.arrows", 32);
        defaults.put("weapon.uhc.golden-apples", 6);
        defaults.put("weapon.uhc.cobwebs", 8);
        defaults.put("weapon.uhc.arrows", 24);
        defaults.put("weapon.uhc.food", 16);
        defaults.put("weapon.water.buckets", 2);
        defaults.put("weapon.water.sponges", 4);

        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            if (!inventory.contains(entry.getKey())) {
                inventory.set(entry.getKey(), entry.getValue());
                changed++;
            }
        }

        inventory.set(
            "inventory-config-version",
            CURRENT_INVENTORY_VERSION
        );

        if (changed == 0 && current != CURRENT_INVENTORY_VERSION) {
            changed = 1;
        }

        plugin.saveDefaultInventoryConfig();

        plugin.getLogger().info(
            "Migrated default_inv.yml from version " +
                current + " to version " +
                CURRENT_INVENTORY_VERSION +
                " (" + changed + " paths updated)."
        );

        return true;
    }

    static void migrateCartCombatDefaults(FileConfiguration config) {
        replaceIfOldDefault(config, "tactics.cart-pvp.cooldown-ms", 3500L, 4500L);
        replaceIfOldDefault(config, "tactics.cart-pvp.min-distance", 5.0D, 2.5D);
        replaceIfOldDefault(config, "tactics.cart-pvp.max-distance", 18.0D, 7.5D);
        replaceIfOldDefault(config, "tactics.cart-pvp.fuse-ticks", 45, 35);
        replaceIfOldDefault(config, "tactics.cart-pvp.rail-search-radius", 6, 2);

        String[] tiers = {"EASY", "NORMAL", "HARD", "EXPERT", "HACKER", "MASTER", "NIGHTMARE"};
        double[] oldMin = {6.5D, 6.0D, 5.5D, 5.0D, 5.0D, 4.5D, 4.0D};
        double[] oldMax = {14.0D, 15.0D, 16.0D, 17.0D, 18.0D, 20.0D, 22.0D};
        double[] newMin = {3.5D, 3.0D, 2.8D, 2.5D, 2.5D, 2.5D, 2.5D};
        double[] newMax = {5.0D, 5.5D, 6.0D, 6.5D, 7.0D, 7.5D, 8.0D};
        long[] oldCooldown = {6500L, 5500L, 4500L, 3800L, 3200L, 2800L, 2400L};
        long[] newCooldown = {7500L, 6500L, 5500L, 4800L, 4200L, 3800L, 3500L};
        long[] oldFuse = {55L, 52L, 50L, 48L, 46L, 44L, 42L};
        long[] newFuse = {40L, 38L, 36L, 35L, 34L, 32L, 30L};
        long[] oldRadius = {2L, 3L, 4L, 5L, 6L, 6L, 8L};
        long[] newRadius = {1L, 1L, 2L, 2L, 2L, 2L, 3L};
        long[] oldFollowup = {1100L, 950L, 850L, 750L, 700L, 650L, 600L};
        long[] newFollowup = {1300L, 1150L, 1000L, 900L, 850L, 800L, 750L};
        for (int i = 0; i < tiers.length; i++) {
            String base = "template-difficulty.profiles.CART." + tiers[i] + ".";
            replaceIfOldDefault(config, base + "cart-min-distance", oldMin[i], newMin[i]);
            replaceIfOldDefault(config, base + "cart-max-distance", oldMax[i], newMax[i]);
            replaceIfOldDefault(config, base + "cart-cooldown-ms", oldCooldown[i], newCooldown[i]);
            replaceIfOldDefault(config, base + "cart-fuse-ticks", oldFuse[i], newFuse[i]);
            replaceIfOldDefault(config, base + "cart-rail-search-radius", oldRadius[i], newRadius[i]);
            replaceIfOldDefault(config, base + "cart-followup-delay-ms", oldFollowup[i], newFollowup[i]);
        }
    }

    private static void replaceIfOldDefault(
        FileConfiguration config, String path, Object oldDefault, Object newDefault
    ) {
        if (!config.contains(path)) return;
        Object current = config.get(path);
        boolean matches;
        if (current instanceof Number currentNumber && oldDefault instanceof Number oldNumber) {
            matches = Double.compare(currentNumber.doubleValue(), oldNumber.doubleValue()) == 0;
        } else {
            matches = java.util.Objects.equals(current, oldDefault);
        }
        if (matches) config.set(path, newDefault);
    }

    private static void backup(PracticeBotPlugin plugin, File file) {
        File backup = new File(
            plugin.getDataFolder(),
            "config.yml.v" + CURRENT_VERSION + ".pre-migration.bak"
        );

        if (backup.exists()) {
            return;
        }

        try {
            Files.copy(
                file.toPath(),
                backup.toPath(),
                StandardCopyOption.COPY_ATTRIBUTES
            );
        } catch (IOException exception) {
            plugin.getLogger().warning(
                "Could not create config migration backup: " +
                    exception.getMessage()
            );
        }
    }
}
