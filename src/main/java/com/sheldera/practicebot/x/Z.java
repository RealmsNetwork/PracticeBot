package com.sheldera.practicebot.x;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Locale;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Z {
   private Z() {
   }

   public static YamlConfiguration a(JavaPlugin var0, File var1, String var2) {
      b(var1);
      if (!var1.exists()) {
         a(var0, var2, var1);
      }

      YamlConfiguration var3 = YamlConfiguration.loadConfiguration(var1);
      YamlConfiguration var4 = b(var0, var2);
      boolean var5 = false;
      if (var4 != null && a(var3, var4)) {
         var5 = true;
      }

      if (c(var3, var2)) {
         var5 = true;
      }

      if (var5) {
         a(var0, var1, var3, var2);
      }

      return var3;
   }

   public static boolean b(JavaPlugin var0) {
      File var1 = new File(var0.getDataFolder(), "config.yml");
      b(var1);
      if (!var1.exists()) {
         var0.saveDefaultConfig();
      }

      YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var1);
      YamlConfiguration var3 = b(var0, "config.yml");
      if (var3 == null) {
         return false;
      } else {
         boolean var4 = a(var2, var3);
         if (c(var2, "config.yml")) {
            var4 = true;
         }

         if (var4) {
            a(var0, var1, var2, "config.yml");
         }

         return var4;
      }
   }

   private static void b(File var0) {
      File var1 = var0.getParentFile();
      if (var1 != null && !var1.exists()) {
         var1.mkdirs();
      }
   }

   private static void a(JavaPlugin var0, String var1, File var2) {
      try {
         try (InputStream var3 = var0.getResource(var1)) {
            if (var3 != null) {
               Files.copy(var3, var2.toPath(), StandardCopyOption.REPLACE_EXISTING);
               return;
            }

            var0.getLogger().warning("Missing bundled resource: " + var1);
         }
      } catch (IOException var8) {
         var0.getLogger().warning("Failed to copy default resource " + var1 + ": " + var8.getMessage());
      }
   }

   private static YamlConfiguration b(JavaPlugin var0, String var1) {
      try {
         YamlConfiguration var3;
         try (InputStream var2 = var0.getResource(var1)) {
            if (var2 == null) {
               var0.getLogger().warning("Missing bundled defaults for " + var1);
               return null;
            }

            var3 = YamlConfiguration.loadConfiguration(new InputStreamReader(var2, StandardCharsets.UTF_8));
         }

         return var3;
      } catch (IOException var7) {
         var0.getLogger().warning("Failed to read bundled defaults for " + var1 + ": " + var7.getMessage());
         return null;
      }
   }

   private static boolean a(ConfigurationSection var0, ConfigurationSection var1) {
      boolean var2 = false;

      for (String var4 : var1.getKeys(false)) {
         Object var5 = var1.get(var4);
         if (var5 instanceof ConfigurationSection var6) {
            ConfigurationSection var7 = var0.getConfigurationSection(var4);
            if (var7 == null) {
               var7 = var0.createSection(var4);
               var2 = true;
            }

            if (a(var7, var6)) {
               var2 = true;
            }
         } else if (!var0.contains(var4)) {
            var0.set(var4, var5);
            var2 = true;
         }
      }

      return var2;
   }

   private static boolean c(YamlConfiguration var0, String var1) {
      String var2 = var1 == null ? "" : var1.replace('\\', '/');
      boolean var3 = false;
      switch (var2) {
         case "config.yml":
            var3 |= a(var0, "cpvp.defaults.risk-weight");
            var3 |= b(var0, "cpvp.difficulty-levels", "risk-weight");
            break;
         case "bot_templates.yml":
            var3 |= b(var0, "risk-weight");
            var3 |= b(var0);
            break;
         case "gui/cpvp_settings.yml":
            var3 |= a(var0, "items.risk");
            var3 |= a(var0, "items.aggression");
            var3 |= a(var0, "items.anchoring.slot", 34, 16);
            var3 |= a(var0, "items.reset.slot", 36, 34);
            var3 |= c(var0, "items.heal_threshold.material", "ENCHANTED_GOLDEN_APPLE", "GOLDEN_APPLE");
            break;
         case "gui/template_editor_cpvp.yml":
            var3 |= a(var0, "items.risk");
            var3 |= a(var0, "items.aggression");
            var3 |= a(var0, "items.anchoring.slot", 34, 16);
            var3 |= c(var0, "items.heal_threshold.material", "ENCHANTED_GOLDEN_APPLE", "GOLDEN_APPLE");
            break;
         case "gui/editor_menu.yml":
            var3 |= c(var0);
      }

      return var3;
   }

   private static boolean a(ConfigurationSection var0, String var1) {
      if (var0 != null && var1 != null && var0.contains(var1)) {
         var0.set(var1, null);
         return true;
      } else {
         return false;
      }
   }

   private static boolean b(ConfigurationSection var0, String var1, String var2) {
      if (var0 != null && var1 != null && var2 != null) {
         ConfigurationSection var3 = var0.getConfigurationSection(var1);
         if (var3 == null) {
            return false;
         } else {
            boolean var4 = false;

            for (String var6 : var3.getKeys(false)) {
               ConfigurationSection var7 = var3.getConfigurationSection(var6);
               if (var7 != null && var7.contains(var2)) {
                  var7.set(var2, null);
                  var4 = true;
               }
            }

            return var4;
         }
      } else {
         return false;
      }
   }

   private static boolean b(ConfigurationSection var0, String var1) {
      ConfigurationSection var2 = var0 == null ? null : var0.getConfigurationSection("templates");
      if (var2 == null) {
         return false;
      } else {
         boolean var3 = false;

         for (String var5 : var2.getKeys(false)) {
            ConfigurationSection var6 = var2.getConfigurationSection(var5);
            ConfigurationSection var7 = var6 == null ? null : var6.getConfigurationSection("cpvp");
            if (var7 != null && var7.contains(var1)) {
               var7.set(var1, null);
               var3 = true;
            }
         }

         return var3;
      }
   }

   private static boolean b(ConfigurationSection var0) {
      ConfigurationSection var1 = var0 == null ? null : var0.getConfigurationSection("templates");
      if (var1 == null) {
         return false;
      } else {
         boolean var2 = false;

         for (String var4 : var1.getKeys(false)) {
            ConfigurationSection var5 = var1.getConfigurationSection(var4);
            if (var5 != null) {
               String var6 = var5.getString("bot-type", "NORMAL").trim().toUpperCase(Locale.ROOT);
               switch (var6) {
                  case "CPVP":
                     var2 |= a(var5, "settings");
                     var2 |= a(var5, "combat");
                     break;
                  case "DUMMY":
                     var2 |= a(var5, "combat");
                     var2 |= a(var5, "cpvp");
                     var2 |= a(var5, "settings.pvp-enabled");
                     break;
                  default:
                     var2 |= a(var5, "cpvp");
               }
            }
         }

         return var2;
      }
   }

   private static boolean c(ConfigurationSection var0) {
      boolean var1 = false;
      var1 |= a(var0, "items.info.lore", "&7Customize all plugin GUIs!", "&7Customize GUI item positions only!");
      var1 |= c(var0, "items.info.lore", "&7• Change item materials");
      var1 |= c(var0, "items.info.lore", "&7• Edit item names & lores");
      var1 |= c(var0, "items.info.lore", "&7• Add glow effects");
      var1 |= a(var0, "items.info.lore", "&7• Reset to defaults", "&7• Reset layouts to defaults");
      var1 |= b(var0, "items.info.lore", "&7• Move items to different slots", "&7• Swap items by clicking two slots");
      var1 |= b(var0, "items.info.lore", "&7• Reset layouts to defaults", "&8Item material/name/lore are config-only");
      var1 |= a(var0, "items.help.lore", "&71. Select a GUI to edit", "&71. Select a GUI layout to edit");
      var1 |= a(var0, "items.help.lore", "&72. &eLeft-click &7an item to move it", "&72. &eLeft-click &7an item to select it");
      var1 |= a(var0, "items.help.lore", "&73. Click an empty slot to place", "&73. Click another slot to move/swap");
      var1 |= c(var0, "items.help.lore", "&74. &eShift+Right-click &7to edit item details");
      var1 |= a(var0, "items.help.lore", "&75. Click &aSave &7to apply changes", "&74. Click &aSave &7to apply layout changes");
      ConfigurationSection var2 = var0.getConfigurationSection("items");
      if (var2 != null) {
         for (String var4 : var2.getKeys(false)) {
            var1 |= a(var2, var4 + ".lore", "&eClick to edit this GUI", "&eClick to edit this GUI layout");
         }
      }

      return var1;
   }

   private static boolean a(ConfigurationSection var0, String var1, String var2, String var3) {
      if (var0 != null && var1 != null && var2 != null && var3 != null && var0.contains(var1)) {
         ArrayList var4 = new ArrayList(var0.getStringList(var1));
         boolean var5 = false;

         for (int var6 = 0; var6 < var4.size(); var6++) {
            if (var2.equals(var4.get(var6))) {
               var4.set(var6, var3);
               var5 = true;
            }
         }

         if (var5) {
            var0.set(var1, var4);
         }

         return var5;
      } else {
         return false;
      }
   }

   private static boolean c(ConfigurationSection var0, String var1, String var2) {
      if (var0 != null && var1 != null && var2 != null && var0.contains(var1)) {
         ArrayList var3 = new ArrayList(var0.getStringList(var1));
         boolean var4 = var3.removeIf(var2::equals);
         if (var4) {
            var0.set(var1, var3);
         }

         return var4;
      } else {
         return false;
      }
   }

   private static boolean b(ConfigurationSection var0, String var1, String var2, String var3) {
      if (var0 != null && var1 != null && var3 != null && var0.contains(var1)) {
         ArrayList var4 = new ArrayList(var0.getStringList(var1));
         if (var4.contains(var3)) {
            return false;
         } else {
            int var5 = var2 == null ? -1 : var4.indexOf(var2);
            if (var5 >= 0 && var5 + 1 <= var4.size()) {
               var4.add(var5 + 1, var3);
            } else {
               var4.add(var3);
            }

            var0.set(var1, var4);
            return true;
         }
      } else {
         return false;
      }
   }

   private static boolean a(ConfigurationSection var0, String var1, int var2, int var3) {
      if (var0 != null && var1 != null && var0.contains(var1)) {
         if (var0.getInt(var1) != var2) {
            return false;
         } else {
            var0.set(var1, var3);
            return true;
         }
      } else {
         return false;
      }
   }

   private static boolean c(ConfigurationSection var0, String var1, String var2, String var3) {
      if (var0 != null && var1 != null && var0.contains(var1)) {
         String var4 = var0.getString(var1);
         if (var4 != null && var4.equalsIgnoreCase(var2)) {
            var0.set(var1, var3);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static void a(JavaPlugin var0, File var1, YamlConfiguration var2, String var3) {
      try {
         var2.save(var1);
      } catch (IOException var5) {
         var0.getLogger().warning("Failed to save merged YAML " + var3 + ": " + var5.getMessage());
      }
   }
}
