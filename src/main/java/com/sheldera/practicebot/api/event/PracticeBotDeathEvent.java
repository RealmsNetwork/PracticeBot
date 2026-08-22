package com.sheldera.practicebot.api.event;

import com.sheldera.practicebot.api.bot.PracticeBotHandle;
import java.util.UUID;
import org.bukkit.event.HandlerList;

public final class PracticeBotDeathEvent extends PracticeBotEvent {
   private static final HandlerList HANDLERS = new HandlerList();
   private final UUID killerEntityUuid;

   public PracticeBotDeathEvent(PracticeBotHandle var1, UUID var2) {
      super(var1);
      this.killerEntityUuid = var2;
   }

   public UUID getKillerEntityUuid() {
      return this.killerEntityUuid;
   }

   public HandlerList getHandlers() {
      return HANDLERS;
   }

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }
}
