package com.sheldera.practicebot.x;

import com.sheldera.practicebot.PracticeBotPlugin;
import org.bukkit.command.CommandSender;

public class c {
   private final PracticeBotPlugin ab;
   private final b config;

   public c(PracticeBotPlugin var1, b var2) {
      this.ab = var1;
      this.config = var2;
   }

   public void k1() {
      this.ab.setLicenseActive(true, "PERMANENTLY UNLOCKED");
   }

   public boolean l0() {
      return true;
   }

   public String l1() {
      return "UNLOCKED";
   }

   public long m0() {
      return System.currentTimeMillis();
   }

   public void a(CommandSender var1) {
      var1.sendMessage("§a[PracticeBot] License is PERMANENTLY UNLOCKED!");
   }

   public void a(CommandSender var1, String var2) {
      var1.sendMessage("§a[PracticeBot] License is PERMANENTLY UNLOCKED!");
   }

   public void m1() {
   }
}
