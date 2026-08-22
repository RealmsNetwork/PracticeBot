package com.sheldera.practicebot.x;

import com.sheldera.practicebot.PracticeBotPlugin;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.plugin.java.JavaPlugin;

public class o1 {
   public static final String pl = "settings_menu";
   public static final String pm = "pvp_settings";
   public static final String pn = "bot_inventory";
   public static final String po = "admin_inventory";
   public static final String pp = "editor_menu";
   public static final String pq = "editor_item";
   public static final String pr = "cpvp_settings";
   public static final String ps = "template_editor_main";
   public static final String pt = "template_editor_behavior";
   public static final String pu = "template_editor_combat";
   public static final String pv = "template_editor_inventory";
   public static final String pw = "template_editor_cpvp";
   private final JavaPlugin px;
   private final Map<String, o0> py = new HashMap<>();
   private static final String[] pz = new String[]{
      "settings_menu",
      "pvp_settings",
      "bot_inventory",
      "admin_inventory",
      "editor_menu",
      "cpvp_settings",
      "template_editor_main",
      "template_editor_behavior",
      "template_editor_combat",
      "template_editor_inventory",
      "template_editor_cpvp"
   };

   public o1(JavaPlugin var1) {
      this.px = var1;
   }

   private void debugLog(String var1) {
      if (this.px instanceof PracticeBotPlugin var2) {
         var2.debugLog(var1);
      }
   }

   public void eG() {
      File var1 = new File(this.px.getDataFolder(), "gui");
      if (!var1.exists()) {
         var1.mkdirs();
      }

      for (String var5 : pz) {
         this.bn(var5);
      }

      this.debugLog("Loaded " + this.py.size() + " GUI configurations.");
   }

   public void bn(String var1) {
      o0 var2 = new o0(this.px, var1);
      var2.a();
      this.py.put(var1, var2);
   }

   public void eH() {
      for (o0 var2 : this.py.values()) {
         var2.C();
      }

      this.debugLog("Reloaded all GUI configurations.");
   }

   public void bo(String var1) {
      o0 var2 = this.py.get(var1);
      if (var2 != null) {
         var2.C();
      } else {
         this.bn(var1);
      }
   }

   public o0 bp(String var1) {
      return this.py.get(var1);
   }

   public o0 eI() {
      return this.bp("settings_menu");
   }

   public o0 eJ() {
      return this.bp("pvp_settings");
   }

   public o0 eK() {
      return this.bp("bot_inventory");
   }

   public o0 eL() {
      return this.bp("admin_inventory");
   }

   public o0 eM() {
      return this.bp("editor_menu");
   }

   public o0 eN() {
      return this.bp("editor_item");
   }

   public o0 eO() {
      return this.bp("cpvp_settings");
   }

   public o0 eP() {
      return this.bp("template_editor_main");
   }

   public o0 eQ() {
      return this.bp("template_editor_behavior");
   }

   public o0 eR() {
      return this.bp("template_editor_combat");
   }

   public o0 eS() {
      return this.bp("template_editor_inventory");
   }

   public o0 eT() {
      return this.bp("template_editor_cpvp");
   }

   public void bq(String var1) {
      o0 var2 = this.py.get(var1);
      if (var2 != null) {
         var2.eu();
      }
   }

   public void eU() {
      for (o0 var2 : this.py.values()) {
         var2.eu();
      }
   }

   public void br(String var1) {
      o0 var2 = this.py.get(var1);
      if (var2 != null) {
         var2.ew();
         this.py.put(var1, var2);
      }
   }

   public Set<String> eV() {
      return this.py.keySet();
   }

   public String[] eW() {
      return pz;
   }

   public String bs(String var1) {
      return switch (var1) {
         case "settings_menu" -> "Settings Menu";
         case "pvp_settings" -> "PvP Settings";
         case "bot_inventory" -> "Bot Equipment";
         case "admin_inventory" -> "Default Inventory";
         case "editor_menu" -> "GUI Editor Menu";
         case "editor_item" -> "Item Editor (disabled)";
         case "cpvp_settings" -> "Crystal PvP Settings";
         case "template_editor_main" -> "Template Editor";
         case "template_editor_behavior" -> "Template Behavior";
         case "template_editor_combat" -> "Template Combat";
         case "template_editor_inventory" -> "Template Inventory";
         case "template_editor_cpvp" -> "Template CPvP";
         default -> var1;
      };
   }

   public String bt(String var1) {
      return switch (var1) {
         case "settings_menu" -> "Main settings menu for bot configuration";
         case "pvp_settings" -> "PvP combat behavior settings";
         case "bot_inventory" -> "Bot equipment and armor settings";
         case "admin_inventory" -> "Default inventory for new bots";
         case "editor_menu" -> "GUI Editor selection menu";
         case "editor_item" -> "Disabled; item details are edited in config files";
         case "cpvp_settings" -> "Crystal PvP combat settings";
         case "template_editor_main" -> "Main template editor menu";
         case "template_editor_behavior" -> "Template behavior settings";
         case "template_editor_combat" -> "Template combat settings";
         case "template_editor_inventory" -> "Template inventory settings";
         case "template_editor_cpvp" -> "Template Crystal PvP settings";
         default -> "No description available";
      };
   }

   public boolean bu(String var1) {
      return this.py.containsKey(var1);
   }
}
