package com.sheldera.practicebot.combat;

import com.sheldera.practicebot.PracticeBotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Locale;

public final class PvpEnvironment {
    private final PracticeBotPlugin plugin;
    private final String serverName;
    private final String bukkitVersion;
    private final boolean paperFamily;
    private final boolean equipmentUpdatesOnActions;
    private final boolean windChargeReset;
    private final boolean fastCrystals;
    private final boolean attributeSwapAvailable;

    public PvpEnvironment(PracticeBotPlugin plugin) {
        this.plugin = plugin;
        this.serverName = Bukkit.getName();
        this.bukkitVersion = Bukkit.getBukkitVersion();
        this.paperFamily = isPaperFamily();
        this.equipmentUpdatesOnActions = readEquipmentUpdateSetting();
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
        this.attributeSwapAvailable = paperFamily && !equipmentUpdatesOnActions;
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

    public String summary() {
        return "server=" + serverName +
            ", version=" + bukkitVersion +
            ", paperFamily=" + paperFamily +
            ", updateEquipmentOnPlayerActions=" + equipmentUpdatesOnActions +
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

    private boolean readEquipmentUpdateSetting() {
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
                if (config.contains("unsupported-settings.update-equipment-on-player-actions")) {
                    return config.getBoolean(
                        "unsupported-settings.update-equipment-on-player-actions", true
                    );
                }
            } catch (Exception ex) {
                plugin.getLogger().warning(
                    "Could not inspect " + file.getPath() +
                    " for Paper equipment-update settings: " + ex.getMessage()
                );
            }
        }

        return true;
    }
}
