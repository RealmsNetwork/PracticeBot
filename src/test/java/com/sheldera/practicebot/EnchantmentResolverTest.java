package com.sheldera.practicebot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.bukkit.configuration.file.YamlConfiguration;

class EnchantmentResolverTest {
    @Test
    void serializesNamespacedEnchantmentsAndUnsafeLevels() {
        Map<String, Integer> expected = new LinkedHashMap<>();
        expected.put("minecraft:sharpness", 12);
        expected.put("example:custom_enchant", 3);

        String serialized = EnchantmentResolver.serializeLevels(expected);

        assertEquals(expected, EnchantmentResolver.deserializeLevels(serialized));
    }

    @Test
    void readsNamespacedEnchantmentKeysAndUnsafeLevelsFromConfiguration() {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("armor-enchantments.helmet.minecraft:protection", 7);
        configuration.set("armor-enchantments.helmet.exampleplugin:custom_enchant", 125);

        Map<String, Integer> levels = EnchantmentResolver.readLevels(
            configuration.getConfigurationSection("armor-enchantments.helmet"));

        assertEquals(Map.of("minecraft:protection", 7, "exampleplugin:custom_enchant", 125), levels);
    }

    @Test
    void ignoresInvalidLevelsWhenDeserializingTraitData() {
        Map<String, Integer> actual = EnchantmentResolver.deserializeLevels("sharpness=5;bad=nope;negative=-2;=4");

        assertEquals(Map.of("sharpness", 5), actual);
        assertTrue(EnchantmentResolver.deserializeLevels("").isEmpty());
    }
}
