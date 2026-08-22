package com.sheldera.practicebot.api.template;

public record BotTemplateView$InventoryView(
   String helmetMaterial,
   String chestplateMaterial,
   String leggingsMaterial,
   String bootsMaterial,
   String helmetEnchant,
   String chestplateEnchant,
   String leggingsEnchant,
   String bootsEnchant,
   String offhandType,
   int totemCount,
   String customMainHandMaterial,
   BotTemplateView$ArmorTrimView helmetTrim,
   BotTemplateView$ArmorTrimView chestTrim,
   BotTemplateView$ArmorTrimView leggingsTrim,
   BotTemplateView$ArmorTrimView bootsTrim
) {
}
