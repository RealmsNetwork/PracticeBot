package com.sheldera.practicebot.api.template;

import com.sheldera.practicebot.api.PracticeBotType;
import com.sheldera.practicebot.api.PracticeBotWeaponProfile;

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
   BotTemplateView$SkinView skin,
   PracticeBotWeaponProfile weaponProfile
) {
   public BotTemplateView(
      String key, String name, String displayName, PracticeBotType botType,
      BotTemplateView$TargetBindingMode targetBindingMode,
      BotTemplateView$DeathMode deathMode, BotTemplateView$KillMode killMode,
      long respawnDelayTicks, long warmupTicks, BotTemplateView$BehaviorView behavior,
      BotTemplateView$CombatView combat, BotTemplateView$InventoryView inventory,
      BotTemplateView$CpvpView cpvp, BotTemplateView$SkinView skin
   ) {
      this(key, name, displayName, botType, targetBindingMode, deathMode, killMode,
         respawnDelayTicks, warmupTicks, behavior, combat, inventory, cpvp, skin, PracticeBotWeaponProfile.SWORD);
   }
}
