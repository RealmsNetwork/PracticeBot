package com.sheldera.practicebot.api.event;

import com.sheldera.practicebot.api.bot.PracticeBotHandle;
import com.sheldera.practicebot.api.template.BotTemplateView;
import org.bukkit.event.HandlerList;

public final class PracticeBotTemplateApplyEvent extends PracticeBotEvent {
   private static final HandlerList HANDLERS = new HandlerList();
   private final BotTemplateView previousTemplate;
   private final BotTemplateView newTemplate;
   private final PracticeBotTemplateApplyCause cause;

   public PracticeBotTemplateApplyEvent(PracticeBotHandle var1, BotTemplateView var2, BotTemplateView var3, PracticeBotTemplateApplyCause var4) {
      super(var1);
      this.previousTemplate = var2;
      this.newTemplate = var3;
      this.cause = var4;
   }

   public BotTemplateView getPreviousTemplate() {
      return this.previousTemplate;
   }

   public BotTemplateView getNewTemplate() {
      return this.newTemplate;
   }

   public PracticeBotTemplateApplyCause getCause() {
      return this.cause;
   }

   public HandlerList getHandlers() {
      return HANDLERS;
   }

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }
}
