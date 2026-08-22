package com.sheldera.practicebot.api.event;

import com.sheldera.practicebot.api.bot.PracticeBotHandle;
import java.util.Objects;
import org.bukkit.event.Event;

public abstract class PracticeBotEvent extends Event {
   private final PracticeBotHandle bot;

   protected PracticeBotEvent(PracticeBotHandle var1) {
      super(false);
      this.bot = Objects.requireNonNull(var1, "bot");
   }

   public PracticeBotHandle getBot() {
      return this.bot;
   }
}
