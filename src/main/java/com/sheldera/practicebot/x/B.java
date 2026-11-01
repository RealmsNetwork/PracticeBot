package com.sheldera.practicebot.x;

import com.sheldera.practicebot.PracticeBotPlugin;
import org.bukkit.entity.Player;

public class B {
   private final PracticeBotPlugin ru;

   public B(PracticeBotPlugin var1) {
      this.ru = var1;
   }

   public boolean bq(Player var1) {
      if (!this.ru.isCitizensReady()) {
         var1.sendMessage(h.ac("citizens-not-loaded"));
         return false;
      } else {
         return true;
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public boolean e(Player var1, a var2) {
      String var3 = this.ru.getConfigManager().bm();
      if (var1.hasPermission(var3)) {
         return true;
      } else {
         return switch (var2) {
            case NORMAL -> var1.hasPermission("practicebot.spawn.normal");
            case CRYSTAL, CPVP -> var1.hasPermission("practicebot.spawn.cpvp") || var1.hasPermission("practicebot.spawn.crystal");
            case CART -> var1.hasPermission("practicebot.spawn.cart");
            case MACE -> var1.hasPermission("practicebot.spawn.mace");
            case UHC -> var1.hasPermission("practicebot.spawn.uhc");
            case WATER -> var1.hasPermission("practicebot.spawn.water");
            case DUMMY -> false;
         };
      }
   }

   public boolean br(Player var1) {
      String var2 = this.ru.getConfigManager().bm();
      return var1.hasPermission(var2) || var1.hasPermission("practicebot.spawn.normal") || var1.hasPermission("practicebot.spawn.cpvp") || var1.hasPermission("practicebot.spawn.crystal") || var1.hasPermission("practicebot.spawn.cart") || var1.hasPermission("practicebot.spawn.mace") || var1.hasPermission("practicebot.spawn.uhc") || var1.hasPermission("practicebot.spawn.water");
   }

   public D f(Player var1, a var2) {
      if (!this.ru.isCitizensReady()) {
         var1.sendMessage(h.ac("citizens-not-loaded"));
         return D.NO_CITIZENS;
      } else if (!this.e(var1, var2)) {
         var1.sendMessage(h.ac("no-permission"));
         return D.NO_PERMISSION;
      } else {
         boolean var3 = this.ru.getBotManager().a(var1, var2);
         return var3 ? D.SUCCESS : D.COOLDOWN;
      }
   }

   public boolean bs(Player var1) {
      if (!this.ru.isCitizensReady()) {
         var1.sendMessage(h.ac("citizens-not-loaded"));
         return false;
      } else if (!this.br(var1)) {
         var1.sendMessage(h.ac("no-permission"));
         return false;
      } else {
         this.ru.getGuiManager().bf(var1);
         return true;
      }
   }

   public a h1(String var1) {
      if (var1 == null || var1.isBlank()) return null;
      String normalized = var1.trim().toUpperCase(java.util.Locale.ROOT);
      return switch (normalized) {
         case "NORMAL", "CART", "MACE", "UHC", "WATER", "DUMMY" -> a.valueOf(normalized);
         case "CRYSTAL", "CPVP" -> a.CRYSTAL;
         default -> null;
      };
   }
}
