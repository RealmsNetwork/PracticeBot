package com.sheldera.practicebot.api.template;

public record BotTemplateView$CombatView(
   boolean strafe,
   boolean wTap,
   boolean sTap,
   boolean crits,
   boolean shieldBreaker,
   boolean retreat,
   int reachMode,
   int aggression,
   int critChance,
   int critSpeed
) {
}
