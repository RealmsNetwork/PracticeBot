package com.sheldera.practicebot;

import java.util.Locale;

/**
 * Resolves Mace aim tuning from the difficulty encoded in a bot template key.
 *
 * Template keys use the established kit_difficulty_player format, for example
 * macepvp_easy_1Wenzy. Non-template Mace bots fall back to combat aggression.
 */
public final class MaceDifficultyTuning {
    private MaceDifficultyTuning() {
    }

    public static String difficultyFromTemplateKey(String templateKey) {
        if (templateKey == null || templateKey.isBlank()) {
            return "";
        }

        String[] parts = templateKey.trim().toLowerCase(Locale.ROOT).split("_", 3);
        if (parts.length < 3) {
            return "";
        }

        return normalizeDifficulty(parts[1]);
    }

    public static String normalizeDifficulty(String difficulty) {
        if (difficulty == null) {
            return "";
        }

        return switch (difficulty.trim().toUpperCase(Locale.ROOT)) {
            case "EASY", "NORMAL", "HARD", "EXPERT", "HACKER", "MASTER", "NIGHTMARE" ->
                difficulty.trim().toUpperCase(Locale.ROOT);
            default -> "";
        };
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

        String templateDifficulty = normalizeDifficulty(trait.getMaceDifficulty());
        return templateDifficulty.isEmpty()
            ? difficultyFromAggression(trait.getPvpAggression())
            : templateDifficulty;
    }

    public static double getDouble(
        PracticeBotPlugin plugin,
        BotTrait trait,
        String setting,
        double fallback
    ) {
        if (plugin == null || setting == null || setting.isBlank()) {
            return fallback;
        }

        String levelPath = "modern-weapons.mace.difficulty-levels." +
            difficultyFor(trait) + "." + setting;
        String legacyPath = "modern-weapons.mace." + setting;

        return plugin.getConfig().getDouble(
            levelPath,
            plugin.getConfig().getDouble(legacyPath, fallback)
        );
    }
}
