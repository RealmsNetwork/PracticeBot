package com.sheldera.practicebot.combat;

/** Pure decision helpers for consumable policy, kept independent of Bukkit registries. */
final class PvpConsumablePolicy {
    private PvpConsumablePolicy() { }

    static boolean shouldEat(int foodLevel, float saturation, int foodThreshold, double saturationThreshold) {
        return foodLevel < 20 &&
            (foodLevel <= foodThreshold || saturation <= saturationThreshold);
    }
}
