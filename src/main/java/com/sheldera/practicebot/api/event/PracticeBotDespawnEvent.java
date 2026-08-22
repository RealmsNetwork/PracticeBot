package com.sheldera.practicebot.api.event;

import com.sheldera.practicebot.api.bot.PracticeBotHandle;
import org.bukkit.event.HandlerList;

public final class PracticeBotDespawnEvent extends PracticeBotEvent {
   private static final HandlerList HANDLERS = new HandlerList();
   private final PracticeBotRemovalReason reason;

   public PracticeBotDespawnEvent(PracticeBotHandle var1, PracticeBotRemovalReason var2) {
      super(var1);
      this.reason = var2 == null ? PracticeBotRemovalReason.UNKNOWN : var2;
   }

   public PracticeBotRemovalReason getReason() {
      return this.reason;
   }

   public HandlerList getHandlers() {
      return HANDLERS;
   }

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }
}
