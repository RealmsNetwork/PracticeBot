package com.sheldera.practicebot.api.event;

import com.sheldera.practicebot.api.bot.PracticeBotHandle;
import org.bukkit.event.HandlerList;

public final class PracticeBotSpawnEvent extends PracticeBotEvent {
   private static final HandlerList HANDLERS = new HandlerList();

   public PracticeBotSpawnEvent(PracticeBotHandle var1) {
      super(var1);
   }

   public HandlerList getHandlers() {
      return HANDLERS;
   }

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }
}
