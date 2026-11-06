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
        String tierPath = "modern-weapons.mace.difficulty-levels." +
            difficultyFor(trait) + "." + setting;
        int legacyCount = legacyPaths == null ? 0 : legacyPaths.length;
        String[] paths = new String[legacyCount + 1];
        paths[0] = tierPath;
        if (legacyCount > 0) {
            System.arraycopy(legacyPaths, 0, paths, 1, legacyCount);
        }

        return TemplateDifficultyTuning.getBoolean(
            plugin, trait, setting, fallback, paths
        );
    }
}
