package com.sheldera.practicebot.api.event;

import com.sheldera.practicebot.api.bot.PracticeBotHandle;
import java.util.UUID;
import org.bukkit.event.HandlerList;

public final class PracticeBotKillEvent extends PracticeBotEvent {
   private static final HandlerList HANDLERS = new HandlerList();
   private final UUID victimEntityUuid;

   public PracticeBotKillEvent(PracticeBotHandle var1, UUID var2) {
      super(var1);
      this.victimEntityUuid = var2;
   }

   public UUID getVictimEntityUuid() {
      return this.victimEntityUuid;
   }

   public HandlerList getHandlers() {
      return HANDLERS;
   }

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }
}
