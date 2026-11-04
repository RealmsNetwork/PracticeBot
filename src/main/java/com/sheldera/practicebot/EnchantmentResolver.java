package com.sheldera.practicebot;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

public final class EnchantmentResolver {
    private EnchantmentResolver() {
    }

    public static Enchantment resolve(String value) {
        if (value == null || value.isBlank()) return null;
        String key = value.trim().toLowerCase(Locale.ROOT);
        try {
            NamespacedKey namespacedKey = key.indexOf(':') >= 0
                ? NamespacedKey.fromString(key)
                : NamespacedKey.minecraft(key);
            if (namespacedKey != null) {
                Enchantment enchantment = Registry.ENCHANTMENT.get(namespacedKey);
                if (enchantment != null) return enchantment;
            }
        } catch (IllegalArgumentException ignored) {
            return null;
        }
        String legacy = switch (key) {
            case "sharpness" -> "DAMAGE_ALL";
            case "knockback" -> "KNOCKBACK";
            case "fire_aspect" -> "FIRE_ASPECT";
            case "unbreaking" -> "DURABILITY";
            default -> value.trim().toUpperCase(Locale.ROOT);
        };
        try {
            return Enchantment.getByName(legacy);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static Map<String, Integer> readLevels(ConfigurationSection section) {
        Map<String, Integer> levels = new LinkedHashMap<>();
        if (section == null) return levels;
        for (String key : section.getKeys(false)) {
            int level = section.getInt(key, 1);
            if (level > 0) levels.put(key.toLowerCase(Locale.ROOT), level);
        }
        return levels;
    }

    public static Map<String, Integer> copyLevels(Map<String, Integer> source) {
        Map<String, Integer> copy = new LinkedHashMap<>();
        if (source == null) return copy;
        source.forEach((key, level) -> {
            if (key != null && !key.isBlank() && level != null && level > 0) {
                copy.put(key.trim().toLowerCase(Locale.ROOT), level);
            }
        });
        return copy;
    }

    public static String serializeLevels(Map<String, Integer> levels) {
        StringBuilder serialized = new StringBuilder();
        copyLevels(levels).forEach((key, level) -> {
            if (!serialized.isEmpty()) serialized.append(';');
            serialized.append(key).append('=').append(level);
        });
        return serialized.toString();
    }

    public static Map<String, Integer> deserializeLevels(String serialized) {
        Map<String, Integer> levels = new LinkedHashMap<>();
        if (serialized == null || serialized.isBlank()) return levels;
        for (String entry : serialized.split(";")) {
            int separator = entry.lastIndexOf('=');
            if (separator <= 0 || separator == entry.length() - 1) continue;
            try {
                int level = Integer.parseInt(entry.substring(separator + 1).trim());
                if (level > 0) levels.put(entry.substring(0, separator).trim().toLowerCase(Locale.ROOT), level);
            } catch (NumberFormatException ignored) {
            }
        }
        return levels;
    }
}
