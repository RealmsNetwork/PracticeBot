package com.sheldera.practicebot.x;

import com.sheldera.practicebot.PracticeBotPlugin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class ad implements CommandExecutor, TabCompleter {
   private final PracticeBotPlugin C;
   private static final String D = "practicebot.spawn.normal";
   private static final String E = "practicebot.spawn.cpvp";
   private static final String F = "practicebot.spawn.mace";
   private static final String G = "practicebot.spawn.spear";

   public ad(PracticeBotPlugin var1) {
      this.C = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         if (!this.C.isLicenseActive()) {
            var5.sendMessage(this.C.licenseLockMessage());
            return true;
         } else if (!this.C.isCitizensReady()) {
            var5.sendMessage(h.ac("citizens-not-loaded"));
            return true;
         } else {
            boolean var6 = this.l(var5);
            boolean var7 = this.m(var5);
            boolean var8 = this.n(var5);
            boolean var9 = this.o(var5);
            if (!var6 && !var7 && !var8 && !var9) {
               var5.sendMessage(h.ac("no-permission"));
               return true;
            } else if (var4.length == 0) {
               this.C.getGuiManager().bf(var5);
               return true;
            } else {
               String var10 = var4[0].toLowerCase();
               if (var10.equals("normal")) {
                  if (!var6) {
                     var5.sendMessage(h.ac("no-permission"));
                     return true;
                  } else {
                     return this.C.getBotManager().a(var5, a.NORMAL);
                  }
               } else if (var10.equals("crystal")) {
                  if (!var7) {
                     var5.sendMessage(h.ac("no-permission"));
                     return true;
                  } else {
                     return this.C.getBotManager().a(var5, a.CPVP);
                  }
               } else if (var10.equals("mace")) {
                  if (!var8) {
                     var5.sendMessage(h.ac("no-permission"));
                     return true;
                  }
                  return this.C.getBotManager().a(var5, "mace");
               } else if (var10.equals("spear")) {
                  if (!var9) {
                     var5.sendMessage(h.ac("no-permission"));
                     return true;
                  }
                  return this.C.getBotManager().a(var5, "spear");
               } else {
                  var5.sendMessage(h.ac("invalid-usage.spawnbot"));
                  return true;
               }
            }
         }
      } else {
         var1.sendMessage(h.ad("player-only"));
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length != 1) {
         return Collections.emptyList();
      } else if (!this.C.isLicenseActive()) {
         return Collections.emptyList();
      } else {
         String var5 = var4[0].toLowerCase();
         ArrayList var6 = new ArrayList();
         if (this.l(var1)) {
            this.a(var6, "normal", var5);
         }

         if (this.m(var1)) {
            this.a(var6, "crystal", var5);
         }

         if (this.n(var1)) {
            this.a(var6, "mace", var5);
         }

         if (this.o(var1)) {
            this.a(var6, "spear", var5);
         }

         return var6;
      }
   }

   private boolean l(CommandSender var1) {
      return var1.hasPermission("practicebot.spawn.normal") || var1.hasPermission(this.C.getConfigManager().bm());
   }

   private boolean m(CommandSender var1) {
      return var1.hasPermission("practicebot.spawn.cpvp") || var1.hasPermission(this.C.getConfigManager().bm());
   }

   private boolean n(CommandSender var1) {
      return var1.hasPermission(F) || var1.hasPermission(this.C.getConfigManager().bm());
   }

   private boolean o(CommandSender var1) {
      return var1.hasPermission(G) || var1.hasPermission(this.C.getConfigManager().bm());
   }

   private void a(List<String> var1, String var2, String var3) {
      if (var2.startsWith(var3)) {
         var1.add(var2);
      }
   }
}
