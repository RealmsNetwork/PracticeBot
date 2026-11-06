package com.sheldera.practicebot;

/**
 * Backwards-compatible facade for code that used the original Mace tuning
 * helper. Template parsing and the shared resolver now live in
 * TemplateDifficultyTuning so every kit can use the same tier system.
 */
public final class MaceDifficultyTuning {
    private MaceDifficultyTuning() {
    }

    public static String difficultyFromTemplateKey(String templateKey) {
        return TemplateDifficultyTuning.difficultyFromTemplateKey(templateKey);
    }

    public static String normalizeDifficulty(String difficulty) {
        return TemplateDifficultyTuning.normalizeDifficulty(difficulty);
    }

    public static String difficultyFromAggression(int aggression) {
        return TemplateDifficultyTuning.difficultyFromAggression(aggression);
    }

    public static String difficultyFor(BotTrait trait) {
        return TemplateDifficultyTuning.difficultyFor(trait);
    }

    public static double getDouble(
        PracticeBotPlugin plugin,
        BotTrait trait,
        String setting,
        double fallback
    ) {
        String tierPath = "modern-weapons.mace.difficulty-levels." +
            difficultyFor(trait) + "." + setting;

        return TemplateDifficultyTuning.getDouble(
            plugin,
            trait,
            setting,
            fallback,
            tierPath,
            "modern-weapons.mace." + setting
        );
    }

    public static boolean getBoolean(
        PracticeBotPlugin plugin,
        BotTrait trait,
        String setting,
        boolean fallback,
        String... legacyPaths
    ) {
        if (plugin == null || setting == null || setting.isBlank()) {
            return fallback;
        }

        String tier = difficultyFor(trait);
        String kit = TemplateDifficultyTuning.kitFor(trait);
        org.bukkit.configuration.file.FileConfiguration config = plugin.getConfig();

        // Preserve the standard resolver's kit > profile > defaults precedence.
        if (!kit.isEmpty()) {
            String kitPath = "template-difficulty.kits." +
                kit + "." + tier + "." + setting;
            if (config.contains(kitPath)) return config.getBoolean(kitPath, fallback);
        }

        // Hybrid spear_mace profiles are classified as SPEAR by the shared
        // profile resolver, but Stun Slam is specifically a Mace capability.
        String maceProfilePath = "template-difficulty.profiles.MACE." +
            tier + "." + setting;
        if (config.contains(maceProfilePath)) {
            return config.getBoolean(maceProfilePath, fallback);
        }

        String defaultPath = "template-difficulty.defaults." +
            tier + "." + setting;
        if (config.contains(defaultPath)) return config.getBoolean(defaultPath, fallback);

        String tierPath = "modern-weapons.mace.difficulty-levels." +
            tier + "." + setting;
        if (config.contains(tierPath)) return config.getBoolean(tierPath, fallback);

        if (legacyPaths != null) {
            for (String path : legacyPaths) {
                if (path != null && !path.isBlank() && config.contains(path)) {
                    return config.getBoolean(path, fallback);
                }
            }
        }
        return fallback;
    }
}
