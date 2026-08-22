package com.sheldera.practicebot.x;

import com.sheldera.practicebot.PracticeBotPlugin;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class h {
   private static JavaPlugin ar;
   private static FileConfiguration as;
   private static String at = "";
   private static final String au = "&cMessage not found: ";

   private static void a(JavaPlugin var0, String var1) {
      if (var0 instanceof PracticeBotPlugin var2) {
         var2.debugLog(var1);
      }
   }

   public static void a(JavaPlugin var0) {
      ar = var0;
      File var1 = new File(var0.getDataFolder(), "messages.yml");
      as = Z.a(var0, var1, "messages.yml");
      at = ag(as.getString("prefix", "&7[&bPracticeBot&7] "));
      a(var0, "Messages loaded successfully.");
   }

   public static void C() {
      if (ar == null) {
         throw new IllegalStateException("MessageManager not initialized. Call load() first.");
      } else {
         a(ar);
      }
   }

   public static String ac(String var0) {
      String var1 = as.getString(var0);
      return var1 == null ? at + ag("&cMessage not found: " + var0) : at + ag(var1);
   }

   public static String a(String var0, String... var1) {
      String var2 = as.getString(var0);
      if (var2 == null) {
         return at + ag("&cMessage not found: " + var0);
      } else {
         var2 = d(var2, var1);
         return at + ag(var2);
      }
   }

   public static String ad(String var0) {
      String var1 = as.getString(var0);
      return var1 == null ? ag("&cMessage not found: " + var0) : ag(var1);
   }

   public static String b(String var0, String... var1) {
      String var2 = as.getString(var0);
      if (var2 == null) {
         return ag("&cMessage not found: " + var0);
      } else {
         var2 = d(var2, var1);
         return ag(var2);
      }
   }

   public static List<String> ae(String var0) {
      return !as.isList(var0) ? Collections.emptyList() : as.getStringList(var0).stream().map(h::ag).collect(Collectors.toList());
   }

   public static List<String> c(String var0, String... var1) {
      return !as.isList(var0) ? Collections.emptyList() : as.getStringList(var0).stream().map(var1x -> ag(d(var1x, var1))).collect(Collectors.toList());
   }

   public static String D() {
      return at;
   }

   public static boolean af(String var0) {
      return as.contains(var0);
   }

   public static String f(String var0, String var1) {
      return as.getString(var0, var1);
   }

   private static String d(String var0, String... var1) {
      if (var1 != null && var1.length >= 2) {
         for (byte var2 = 0; var2 < var1.length - 1; var2 += 2) {
            String var3 = var1[var2];
            String var4 = var1[var2 + 1];
            if (var3 != null && var4 != null) {
               var0 = var0.replace(var3, var4);
            }
         }

         return var0;
      } else {
         return var0;
      }
   }

   private static String ag(String var0) {
      return Y.ag(var0);
   }
}
