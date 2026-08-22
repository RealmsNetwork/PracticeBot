package com.sheldera.practicebot.api.event;

import com.sheldera.practicebot.api.bot.PracticeBotHandle;
import com.sheldera.practicebot.api.bot.PracticeBotTargetSnapshot;
import org.bukkit.event.HandlerList;

public final class PracticeBotCombatStartEvent extends PracticeBotEvent {
   private static final HandlerList HANDLERS = new HandlerList();
   private final PracticeBotTargetSnapshot target;

   public PracticeBotCombatStartEvent(PracticeBotHandle var1, PracticeBotTargetSnapshot var2) {
      super(var1);
      this.target = var2;
   }

   public PracticeBotTargetSnapshot getTarget() {
      return this.target;
   }

   public HandlerList getHandlers() {
      return HANDLERS;
   }

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }
}
