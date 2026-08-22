package com.sheldera.practicebot.x;

import com.sheldera.practicebot.PracticeBotPlugin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class ab implements CommandExecutor, TabCompleter {
   private static final String z1 = "practicebot.spawn.normal";
   private static final String A = "practicebot.spawn.cpvp";
   private final PracticeBotPlugin B;

   public ab(PracticeBotPlugin var1) {
      this.B = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!this.B.isLicenseActive()) {
         var1.sendMessage(this.B.licenseLockMessage());
         return true;
      } else if (var4.length == 0) {
         return this.g(var1);
      } else {
         String var5 = var4[0].toLowerCase(Locale.ROOT);

         return switch (var5) {
            case "spawn" -> this.a(var1, this::p);
            case "info" -> this.k(var1);
            case "help" -> this.g(var1);
            default -> {
               var1.sendMessage(h.ac("invalid-usage.practicebot"));
               yield true;
            }
         };
      }
   }

   private boolean p(Player var1) {
      if (!this.B.isCitizensReady()) {
         var1.sendMessage(h.ac("citizens-not-loaded"));
         return true;
      } else if (!this.n(var1)) {
         var1.sendMessage(h.ac("no-permission"));
         return true;
      } else {
         this.B.getGuiManager().bf(var1);
         return true;
      }
   }

   private boolean k(CommandSender var1) {
      List var2 = h.c(
         "info.lines",
         "{version}",
         this.B.getDescription().getVersion(),
         "{author}",
         String.join(", ", this.B.getDescription().getAuthors()),
         "{citizens_status}",
         this.B.isCitizensReady() ? h.ad("info.citizens-ready") : h.ad("info.citizens-not-ready")
      );
      var1.sendMessage(h.ad("info.header"));

      for (String var4 : (java.util.Collection<String>)(java.util.Collection<?>) var2) {
         var1.sendMessage(var4);
      }

      var1.sendMessage(h.ad("info.footer"));
      return true;
   }

   private boolean g(CommandSender var1) {
      var1.sendMessage(h.ad("help.header"));

      for (String var3 : h.ae("help.spawn-commands")) {
         var1.sendMessage(var3);
      }

      var1.sendMessage(h.ad("help.footer"));
      return true;
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!this.B.isLicenseActive()) {
         return Collections.emptyList();
      } else if (var4.length != 1) {
         return Collections.emptyList();
      } else {
         ArrayList var5 = new ArrayList();
         if (this.n(var1)) {
            this.a(var5, "spawn", var4[0]);
         }

         this.a(var5, "info", var4[0]);
         this.a(var5, "help", var4[0]);
         return var5;
      }
   }

   private boolean a(CommandSender var1, ac var2) {
      if (var1 instanceof Player var3) {
         return var2.handle(var3);
      } else {
         var1.sendMessage(h.ad("player-only"));
         return true;
      }
   }

   private boolean l(CommandSender var1) {
      return var1.hasPermission("practicebot.spawn.normal") || var1.hasPermission(this.B.getConfigManager().bm());
   }

   private boolean m(CommandSender var1) {
      return var1.hasPermission("practicebot.spawn.cpvp") || var1.hasPermission(this.B.getConfigManager().bm());
   }

   private boolean n(CommandSender var1) {
      return this.l(var1) || this.m(var1);
   }

   private void a(List<String> var1, String var2, String var3) {
      if (var2.toLowerCase(Locale.ROOT).startsWith(var3.toLowerCase(Locale.ROOT))) {
         var1.add(var2);
      }
   }
}
