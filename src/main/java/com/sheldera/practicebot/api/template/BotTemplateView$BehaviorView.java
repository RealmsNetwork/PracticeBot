package com.sheldera.practicebot.api.template;

public record BotTemplateView$BehaviorView(
   boolean guiAllowed,
   boolean pvpEnabled,
   boolean lookAtTarget,
   boolean followTarget,
   boolean randomWalk,
   boolean holdShield,
   boolean useShield,
   boolean resistance,
   boolean frozen,
   boolean shieldInMainHand
) {
}
