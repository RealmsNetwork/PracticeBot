package com.sheldera.practicebot.combat;

import com.sheldera.practicebot.PracticeBotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Locale;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class PvpEnvironment {
    private static final Map<PracticeBotPlugin, Boolean> SWAP_WARNING_LOGGED =
        Collections.synchronizedMap(new WeakHashMap<>());

    private final PracticeBotPlugin plugin;
    private final String serverName;
    private final String bukkitVersion;
    private final boolean paperFamily;
    private final boolean equipmentUpdatesOnActions;
    private final boolean equipmentSettingDetected;
    private final String equipmentSettingSource;
    private final boolean windChargeReset;
    private final boolean fastCrystals;
    private final boolean attributeSwapAvailable;

    public PvpEnvironment(PracticeBotPlugin plugin) {
        this.plugin = plugin;
        this.serverName = Bukkit.getName();
        this.bukkitVersion = Bukkit.getBukkitVersion();
        this.paperFamily = isPaperFamily();
        EquipmentUpdateSetting equipmentSetting = readEquipmentUpdateSetting();
        this.equipmentUpdatesOnActions = equipmentSetting.value();
        this.equipmentSettingDetected = equipmentSetting.detected();
        this.equipmentSettingSource = equipmentSetting.source();
        this.windChargeReset = detectPlugin(
            "windchargereset",
            "wind charge reset",
            "wind-charge-reset",
            "windchargeresets"
        );
        this.fastCrystals = detectPlugin(
            "fastcrystals",
            "fast crystal",
            "fastcrystalsreborn",
            "fastercrystals"
        );
        this.attributeSwapAvailable = supportsAttributeSwapping(
            paperFamily, equipmentSettingDetected, equipmentUpdatesOnActions
        );
        logAttributeSwapCapability();
    }

    public String serverName() {
        return serverName;
    }

    public String bukkitVersion() {
        return bukkitVersion;
    }

    public boolean isPaperFamily() {
        String name = serverName == null ? "" : serverName.toLowerCase(Locale.ROOT);
        String version = Bukkit.getVersion() == null ? "" : Bukkit.getVersion().toLowerCase(Locale.ROOT);
        return name.contains("paper") || name.contains("purpur") ||
            version.contains("paper") || version.contains("purpur");
    }

    public boolean equipmentUpdatesOnPlayerActions() {
        return equipmentUpdatesOnActions;
    }

    public boolean isWindChargeResetEnabled() {
        return windChargeReset;
    }

    public boolean isFastCrystalsEnabled() {
        return fastCrystals;
    }

    public boolean isAttributeSwapAvailable() {
        return attributeSwapAvailable;
    }

    public boolean isEquipmentSettingDetected() {
        return equipmentSettingDetected;
    }

    public String equipmentSettingSource() {
        return equipmentSettingSource;
    }

    public String summary() {
        return "server=" + serverName +
            ", version=" + bukkitVersion +
            ", paperFamily=" + paperFamily +
            ", updateEquipmentOnPlayerActions=" + equipmentUpdatesOnActions +
            ", equipmentSettingDetected=" + equipmentSettingDetected +
            ", equipmentSettingSource=" + equipmentSettingSource +
            ", attributeSwapAvailable=" + attributeSwapAvailable +
            ", WindChargeReset=" + windChargeReset +
            ", FastCrystals=" + fastCrystals;
    }

    private boolean detectPlugin(String... aliases) {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            String normalized = normalize(plugin.getName());
            if (!plugin.isEnabled()) {
                continue;
            }

            for (String alias : aliases) {
                if (normalized.equals(normalize(alias))) {
                    return true;
                }
            }
        }

        return false;
    }

    private String normalize(String value) {
        return value == null ? "" :
            value.toLowerCase(Locale.ROOT).replace(" ", "").replace("-", "").replace("_", "");
    }

    static boolean supportsAttributeSwapping(
        boolean paperFamily, boolean settingDetected, boolean equipmentUpdatesOnActions
    ) {
        return paperFamily && settingDetected && !equipmentUpdatesOnActions;
    }

    private record EquipmentUpdateSetting(boolean value, boolean detected, String source) {}

    private EquipmentUpdateSetting readEquipmentUpdateSetting() {
        File serverRoot = Bukkit.getWorldContainer();
        File[] candidates = new File[] {
            new File(serverRoot, "config/paper-global.yml"),
            new File(serverRoot, "paper.yml"),
            new File(serverRoot, "config/paper.yml"),
            new File(serverRoot, "purpur.yml"),
            new File(serverRoot, "config/purpur.yml")
        };

        for (File file : candidates) {
            if (!file.isFile()) {
                continue;
            }

            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                String key = "unsupported-settings.update-equipment-on-player-actions";
                if (config.contains(key)) {
                    return new EquipmentUpdateSetting(config.getBoolean(key, true), true, file.getPath());
                }
            } catch (Exception ex) {
                plugin.getLogger().warning(
                    "Could not inspect " + file.getPath() +
                    " for Paper equipment-update settings: " + ex.getMessage()
                );
            }
        }

        return new EquipmentUpdateSetting(true, false, "not found");
    }

    private void logAttributeSwapCapability() {
        synchronized (SWAP_WARNING_LOGGED) {
            if (SWAP_WARNING_LOGGED.putIfAbsent(plugin, Boolean.TRUE) != null) {
                return;
            }
        }

        if (attributeSwapAvailable) {
            plugin.getLogger().info(
                "Attribute-swap techniques enabled: " + equipmentSettingSource +
                " explicitly sets unsupported-settings.update-equipment-on-player-actions=false. " +
                "These techniques rely on unsupported vanilla behavior and should be tested on this server build."
            );
            return;
        }

        String reason;
        if (!paperFamily) {
            reason = "the server is not detected as Paper/Purpur";
        } else if (!equipmentSettingDetected) {
            reason = "unsupported-settings.update-equipment-on-player-actions was not explicitly found";
        } else {
            reason = "unsupported-settings.update-equipment-on-player-actions is true";
        }

        plugin.getLogger().warning(
            "Attribute-swap techniques are DISABLED because " + reason + ". " +
            "On supported Paper/Purpur builds, set unsupported-settings.update-equipment-on-player-actions: false " +
            "in config/paper-global.yml and restart the server. Normal native weapon attacks remain enabled; " +
            "PracticeBot will not substitute a synthetic damage bonus for unsupported swapping."
        );
    }

}
