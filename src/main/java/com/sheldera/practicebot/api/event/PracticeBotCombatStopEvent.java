package com.sheldera.practicebot.api.event;

import com.sheldera.practicebot.api.bot.PracticeBotHandle;
import com.sheldera.practicebot.api.bot.PracticeBotTargetSnapshot;
import org.bukkit.event.HandlerList;

public final class PracticeBotCombatStopEvent extends PracticeBotEvent {
   private static final HandlerList HANDLERS = new HandlerList();
   private final PracticeBotTargetSnapshot previousTarget;

   public PracticeBotCombatStopEvent(PracticeBotHandle var1, PracticeBotTargetSnapshot var2) {
      super(var1);
      this.previousTarget = var2;
   }

   public PracticeBotTargetSnapshot getPreviousTarget() {
      return this.previousTarget;
   }

   public HandlerList getHandlers() {
      return HANDLERS;
   }

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }
}
