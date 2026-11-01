package com.sheldera.practicebot;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

/**
 * Resolves difficulty for every bot template using the established
 * <kit>_<difficulty>_<player> key format.
 *
 * Overrides are resolved in this order:
 * 1. template-difficulty.kits.<kit>.<tier>
 * 2. template-difficulty.profiles.<profile>.<tier>
 * 3. template-difficulty.defaults.<tier>
 * 4. Explicit legacy config paths supplied by the caller
 */
public final class TemplateDifficultyTuning {
    private static final String[] DIFFICULTIES = {
        "EASY", "NORMAL", "HARD", "EXPERT", "HACKER", "MASTER", "NIGHTMARE"
    };

    private TemplateDifficultyTuning() {
    }

    public static String kitFromTemplateKey(String templateKey) {
        String[] parts = splitTemplateKey(templateKey);
        return parts == null ? "" : normalizeKit(parts[0]);
    }

    public static String difficultyFromTemplateKey(String templateKey) {
        String[] parts = splitTemplateKey(templateKey);
        return parts == null ? "" : normalizeDifficulty(parts[1]);
    }

    private static String[] splitTemplateKey(String templateKey) {
        if (templateKey == null || templateKey.isBlank()) {
            return null;
        }

        String[] parts = templateKey.trim().split("_", 3);
        return parts.length == 3 ? parts : null;
    }

    public static String normalizeKit(String kit) {
        if (kit == null) {
            return "";
        }

        String normalized = kit.trim().toLowerCase(Locale.ROOT);
        return normalized.matches("[a-z0-9-]+") ? normalized : "";
    }

    public static String normalizeDifficulty(String difficulty) {
        if (difficulty == null) {
            return "";
        }

        String normalized = difficulty.trim().toUpperCase(Locale.ROOT);
        for (String supported : DIFFICULTIES) {
            if (supported.equals(normalized)) {
                return normalized;
            }
        }
        return "";
    }

    public static String difficultyFromAggression(int aggression) {
        return switch (aggression) {
            case 0 -> "EASY";
            case 1 -> "NORMAL";
            default -> "HARD";
        };
    }

    public static String difficultyFor(BotTrait trait) {
        if (trait == null) {
            return "NORMAL";
        }

        String templateDifficulty = normalizeDifficulty(trait.getTemplateDifficulty());
        return templateDifficulty.isEmpty()
            ? difficultyFromAggression(trait.getPvpAggression())
            : templateDifficulty;
    }

    public static String kitFor(BotTrait trait) {
        return trait == null ? "" : normalizeKit(trait.getTemplateKit());
    }

    public static String profileFor(BotTrait trait) {
        if (trait == null) {
            return "NORMAL";
        }

        if (trait.isCpvpEnabled()) {
            return "CRYSTAL";
        }

        String profile = trait.getWeaponProfile();
        if (profile == null) {
            return "NORMAL";
        }

        String normalized = profile.trim().toLowerCase(Locale.ROOT);
        if (normalized.contains("mace")) {
            return "MACE";
        }
        if (normalized.contains("spear")) {
            return "SPEAR";
        }
        if (normalized.startsWith("cart")) {
            return "CART";
        }
        if ("uhc".equals(normalized)) {
            return "UHC";
        }
        if ("water".equals(normalized)) {
            return "WATER";
        }
        return "NORMAL";
    }

    private static Object configuredValue(
        PracticeBotPlugin plugin,
        BotTrait trait,
        String setting,
        String... legacyPaths
    ) {
        if (plugin == null || setting == null || setting.isBlank()) {
            return null;
        }

        FileConfiguration config = plugin.getConfig();
        String tier = difficultyFor(trait);
        String kit = kitFor(trait);
        String profile = profileFor(trait);

        if (!kit.isEmpty()) {
            String kitPath = "template-difficulty.kits." +
                kit + "." + tier + "." + setting;
            if (config.contains(kitPath)) {
                return config.get(kitPath);
            }
        }

        String profilePath = "template-difficulty.profiles." +
            profile + "." + tier + "." + setting;
        if (config.contains(profilePath)) {
            return config.get(profilePath);
        }

        String defaultPath = "template-difficulty.defaults." +
            tier + "." + setting;
        if (config.contains(defaultPath)) {
            return config.get(defaultPath);
        }

        if (legacyPaths != null) {
            for (String legacyPath : legacyPaths) {
                if (legacyPath != null && !legacyPath.isBlank() &&
                    config.contains(legacyPath)) {
                    return config.get(legacyPath);
                }
            }
        }

        return null;
    }

    public static double getDouble(
        PracticeBotPlugin plugin,
        BotTrait trait,
        String setting,
        double fallback,
        String... legacyPaths
    ) {
        Object value = configuredValue(plugin, trait, setting, legacyPaths);
        return value instanceof Number number ? number.doubleValue() : fallback;
    }

    public static long getLong(
        PracticeBotPlugin plugin,
        BotTrait trait,
        String setting,
        long fallback,
        String... legacyPaths
    ) {
        Object value = configuredValue(plugin, trait, setting, legacyPaths);
        return value instanceof Number number ? number.longValue() : fallback;
    }

    public static int getInt(
        PracticeBotPlugin plugin,
        BotTrait trait,
        String setting,
        int fallback,
        String... legacyPaths
    ) {
        Object value = configuredValue(plugin, trait, setting, legacyPaths);
        return value instanceof Number number ? number.intValue() : fallback;
    }

    public static boolean getBoolean(
        PracticeBotPlugin plugin,
        BotTrait trait,
        String setting,
        boolean fallback,
        String... legacyPaths
    ) {
        Object value = configuredValue(plugin, trait, setting, legacyPaths);
        return value instanceof Boolean bool ? bool : fallback;
    }
}
