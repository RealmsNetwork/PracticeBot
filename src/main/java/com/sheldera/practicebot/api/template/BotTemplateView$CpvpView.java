package com.sheldera.practicebot.api.template;

public record BotTemplateView$CpvpView(
   boolean enabled,
   String skillLevel,
   double aggressionWeight,
   boolean usePearls,
   boolean useMace,
   boolean useGoldenApples,
   boolean placeObsidian,
   boolean breakBlocks,
   boolean strafingEnabled,
   boolean anchoringMode,
   double healThreshold,
   double lowHpCrystalLethalReserve,
   long pearlCooldownMs
) {
}
