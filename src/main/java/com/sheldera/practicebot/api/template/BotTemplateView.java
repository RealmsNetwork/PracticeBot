package com.sheldera.practicebot.api.template;

import com.sheldera.practicebot.api.PracticeBotType;

public record BotTemplateView(
   String key,
   String name,
   String displayName,
   PracticeBotType botType,
   BotTemplateView$TargetBindingMode targetBindingMode,
   BotTemplateView$DeathMode deathMode,
   BotTemplateView$KillMode killMode,
   long respawnDelayTicks,
   long warmupTicks,
   BotTemplateView$BehaviorView behavior,
   BotTemplateView$CombatView combat,
   BotTemplateView$InventoryView inventory,
   BotTemplateView$CpvpView cpvp,
   BotTemplateView$SkinView skin
) {
}
