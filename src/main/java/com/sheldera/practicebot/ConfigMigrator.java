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
    private static final int CURRENT_VERSION = 2;

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

        backup(plugin, file);

        Map<String, Object> defaults = new LinkedHashMap<>();

        defaults.put("config-version", CURRENT_VERSION);

        defaults.put("modern-weapons.mace.wind-charge-enabled", true);
        defaults.put("modern-weapons.mace.wind-charge-launch-height", 7.0D);
        defaults.put("modern-weapons.mace.assisted-jump-without-wind-charge", true);
        defaults.put("modern-weapons.mace.launch-min-distance", 2.8D);
        defaults.put("modern-weapons.mace.launch-max-distance", 4.5D);

        defaults.put("modern-weapons.spear.charge-enabled", true);
        defaults.put("modern-weapons.spear.charge-delay-ms", 400L);
        defaults.put("modern-weapons.spear.contact-cooldown-ms", 500L);
        defaults.put("modern-weapons.spear.min-relative-speed-bps", 4.6D);
        defaults.put("modern-weapons.spear.charge-velocity-multiplier", 1.2D);
        defaults.put("modern-weapons.spear.base-damage", 5.0D);

        defaults.put("tactics.humanization.movement-enabled", true);
        defaults.put("tactics.humanization.movement-jitter", 0.015D);
        defaults.put("tactics.humanization.strafe-switch-ms", 380L);

        defaults.put("tactics.humanization.ping.min-ms", 35);
        defaults.put("tactics.humanization.ping.max-ms", 180);
        defaults.put("tactics.humanization.ping.jitter-ms", 8);
        defaults.put("tactics.humanization.ping.action-latency-fraction", 0.35D);

        defaults.put("tactics.humanization.reaction.min-ms", 85.0D);
        defaults.put("tactics.humanization.reaction.max-ms", 210.0D);

        defaults.put("tactics.humanization.aim.max-degrees-per-update", 24.0D);
        defaults.put("tactics.humanization.aim.jitter-degrees", 0.6D);

        defaults.put("tactics.humanization.attack.min-delay-ms", 8.0D);
        defaults.put("tactics.humanization.attack.max-delay-ms", 42.0D);

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

        defaults.put("tactics.attribute-swap.enabled", false);
        defaults.put("tactics.attribute-swap.chance-percent", 10.0D);

        defaults.put("tactics.fast-crystals.mode", "auto");
        defaults.put("tactics.fast-crystals.remove-artificial-delay", true);

        defaults.put("tactics.cart-pvp.enabled", true);
        defaults.put("tactics.cart-pvp.cooldown-ms", 3500L);
        defaults.put("tactics.cart-pvp.min-distance", 5.0D);
        defaults.put("tactics.cart-pvp.max-distance", 18.0D);
        defaults.put("tactics.cart-pvp.launch-speed", 0.8D);
        defaults.put("tactics.cart-pvp.fuse-ticks", 45);

        defaults.put("weapon.type", "sword");
        defaults.put("weapon.mobility.firework-rockets", 24);
        defaults.put("weapon.mace.density", 5);
        defaults.put("weapon.mace.breach", 0);
        defaults.put("weapon.mace.wind-burst", 3);
        defaults.put("weapon.mace.wind-charges", 16);
        defaults.put("weapon.spear.material", "netherite");
        defaults.put("weapon.spear.sharpness", 5);
        defaults.put("weapon.spear.lunge", 3);
        defaults.put("weapon.cart.tnt-minecarts", 16);
        defaults.put("weapon.cart.powered-rails", 16);
        defaults.put("weapon.cart.rails", 32);
        defaults.put("weapon.cart.arrows", 32);
        defaults.put("weapon.cart.power", 5);
        defaults.put("weapon.cart.punch", 2);

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
