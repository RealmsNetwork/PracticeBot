package com.sheldera.practicebot.api.event;

import com.sheldera.practicebot.api.bot.PracticeBotHandle;
import com.sheldera.practicebot.api.bot.PracticeBotTargetSnapshot;
import org.bukkit.event.HandlerList;

public final class PracticeBotTargetChangeEvent extends PracticeBotEvent {
   private static final HandlerList HANDLERS = new HandlerList();
   private final PracticeBotTargetSnapshot previousTarget;
   private final PracticeBotTargetSnapshot newTarget;
   private final PracticeBotTargetChangeCause cause;

   public PracticeBotTargetChangeEvent(
      PracticeBotHandle var1, PracticeBotTargetSnapshot var2, PracticeBotTargetSnapshot var3, PracticeBotTargetChangeCause var4
   ) {
      super(var1);
      this.previousTarget = var2;
      this.newTarget = var3;
      this.cause = var4 == null ? PracticeBotTargetChangeCause.UNKNOWN : var4;
   }

   public PracticeBotTargetSnapshot getPreviousTarget() {
      return this.previousTarget;
   }

   public PracticeBotTargetSnapshot getNewTarget() {
      return this.newTarget;
   }

   public PracticeBotTargetChangeCause getCause() {
      return this.cause;
   }

   public HandlerList getHandlers() {
      return HANDLERS;
   }

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }
}
