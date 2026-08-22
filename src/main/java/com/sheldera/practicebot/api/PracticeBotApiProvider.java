package com.sheldera.practicebot.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class PracticeBotApiProvider {
   private PracticeBotApiProvider() {
   }

   public static PracticeBotApi get() {
      RegisteredServiceProvider var0 = Bukkit.getServicesManager().getRegistration(PracticeBotApi.class);
      if (var0 != null && var0.getProvider() != null) {
         return (PracticeBotApi)var0.getProvider();
      } else {
         throw new IllegalStateException("PracticeBot API service is not available.");
      }
   }

   public static PracticeBotApi resolve() {
      return get();
   }
}
