package com.sheldera.practicebot.x;

import com.sheldera.practicebot.PracticeBotPlugin;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class o0 {
   private final JavaPlugin pb;
   private final String pc;
   private File pd;
   private FileConfiguration pe;
   private String pf;
   private int pg;
   private Material ph;
   private String pi;
   private boolean pj;
   private final Map<String, s0> pk = new LinkedHashMap<>();

   public o0(JavaPlugin var1, String var2) {
      this.pb = var1;
      this.pc = var2;
      File var3 = new File(var1.getDataFolder(), "gui");
      if (!var3.exists()) {
         var3.mkdirs();
      }

      this.pd = new File(var3, var2 + ".yml");
   }

   private void debugLog(String var1) {
      if (this.pb instanceof PracticeBotPlugin var2) {
         var2.debugLog(var1);
      }
   }

   public void a() {
      this.pe = Z.a(this.pb, this.pd, "gui/" + this.pc + ".yml");
      this.et();
      this.debugLog("Loaded GUI: " + this.pc + " with " + this.pk.size() + " items");
   }

   private void et() {
      this.pf = this.ag(this.pe.getString("title", "&8GUI"));
      this.pg = this.pe.getInt("size", 27);
      if (this.pg < 9 || this.pg > 54 || this.pg % 9 != 0) {
         this.pg = 27;
      }

      ConfigurationSection var1 = this.pe.getConfigurationSection("filler");
      if (var1 != null) {
         this.pj = var1.getBoolean("enabled", true);
         String var2 = var1.getString("material", "GRAY_STAINED_GLASS_PANE");

         try {
            this.ph = Material.valueOf(var2.toUpperCase());
         } catch (IllegalArgumentException var7) {
            this.ph = Material.GRAY_STAINED_GLASS_PANE;
         }

         this.pi = this.ag(var1.getString("name", " "));
      } else {
         this.pj = true;
         this.ph = Material.GRAY_STAINED_GLASS_PANE;
         this.pi = " ";
      }

      this.pk.clear();
      ConfigurationSection var8 = this.pe.getConfigurationSection("items");
      if (var8 != null) {
         for (String var4 : var8.getKeys(false)) {
            ConfigurationSection var5 = var8.getConfigurationSection(var4);
            if (var5 != null) {
               s0 var6 = this.a(var4, var5);
               this.pk.put(var4, var6);
            }
         }
      }
   }

   private s0 a(String var1, ConfigurationSection var2) {
      s0 var3 = new s0(var1);
      var3.s(var2.getInt("slot", 0));
      String var4 = var2.getString("material", "STONE");

      try {
         var3.r(Material.valueOf(var4.toUpperCase()));
      } catch (IllegalArgumentException var13) {
         var3.r(Material.STONE);
      }

      var3.bz(this.ag(var2.getString("name", "&fItem")));
      var3.t(var2.getInt("amount", 1));
      var3.r(var2.getBoolean("glow", false));
      var3.c0(var2.getString("action", "none"));
      var3.c1(var2.getString("permission", null));
      var3.d0(var2.getString("sound", null));
      var3.u(var2.getInt("custom-model-data", -1));
      List var5 = var2.getStringList("lore");
      ArrayList var6 = new ArrayList();

      for (String var8 : (java.util.List<String>)(java.util.List<?>) var5) {
         var6.add(this.ag(var8));
      }

      var3.a(var6);
      List var14 = var2.getStringList("item-flags");
      ArrayList var15 = new ArrayList();

      for (String var10 : (java.util.List<String>)(java.util.List<?>) var14) {
         try {
            var15.add(ItemFlag.valueOf(var10.toUpperCase()));
         } catch (IllegalArgumentException var12) {
         }
      }

      var3.b(var15);
      return var3;
   }

   public void eu() {
      this.pe.set("title", this.bm(this.pf));
      this.pe.set("size", this.pg);
      this.pe.set("filler.enabled", this.pj);
      this.pe.set("filler.material", this.ph.name());
      this.pe.set("filler.name", this.bm(this.pi));
      this.pe.set("items", null);

      for (Entry var2 : this.pk.entrySet()) {
         String var3 = (String)var2.getKey();
         s0 var4 = (s0)var2.getValue();
         String var5 = "items." + var3;
         this.pe.set(var5 + ".slot", var4.ff());
         this.pe.set(var5 + ".material", var4.fg().name());
         this.pe.set(var5 + ".name", this.bm(var4.fh()));
         this.pe.set(var5 + ".amount", var4.fj());
         this.pe.set(var5 + ".glow", var4.fk());
         this.pe.set(var5 + ".action", var4.fl());
         if (var4.fm() != null) {
            this.pe.set(var5 + ".permission", var4.fm());
         }

         if (var4.fn() != null) {
            this.pe.set(var5 + ".sound", var4.fn());
         }

         if (var4.fo() > 0) {
            this.pe.set(var5 + ".custom-model-data", var4.fo());
         }

         ArrayList var6 = new ArrayList();

         for (String var8 : var4.fi()) {
            var6.add(this.bm(var8));
         }

         this.pe.set(var5 + ".lore", var6);
         ArrayList var11 = new ArrayList();

         for (ItemFlag var9 : var4.fp()) {
            var11.add(var9.name());
         }

         if (!var11.isEmpty()) {
            this.pe.set(var5 + ".item-flags", var11);
         }
      }

      try {
         this.pe.save(this.pd);
      } catch (IOException var10) {
         this.pb.getLogger().severe("Failed to save GUI config: " + this.pc);
         var10.printStackTrace();
      }
   }

   public ItemStack a(s0 var1) {
      return this.a(var1, null);
   }

   public ItemStack a(s0 var1, Map<String, String> var2) {
      ItemStack var3 = new ItemStack(var1.fg(), var1.fj());
      ItemMeta var4 = var3.getItemMeta();
      if (var4 != null) {
         String var5 = var1.fh();
         ArrayList var6 = new ArrayList<>(var1.fi());
         if (var2 != null) {
            for (Entry var8 : var2.entrySet()) {
               var5 = var5.replace((CharSequence)var8.getKey(), (CharSequence)var8.getValue());
               var6.replaceAll(var1x -> ((String)var1x).replace((CharSequence)var8.getKey(), (CharSequence)var8.getValue()));
            }
         }

         var4.setDisplayName(var5);
         var4.setLore(var6);
         if (var1.fk()) {
            var4.addEnchant(Enchantment.UNBREAKING, 1, true);
            var4.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
         }

         for (ItemFlag var10 : var1.fp()) {
            var4.addItemFlags(new ItemFlag[]{var10});
         }

         if (var1.fo() > 0) {
            var4.setCustomModelData(var1.fo());
         }

         var3.setItemMeta(var4);
      }

      return var3;
   }

   public ItemStack ev() {
      ItemStack var1 = new ItemStack(this.ph);
      ItemMeta var2 = var1.getItemMeta();
      if (var2 != null) {
         var2.setDisplayName(this.pi);
         var1.setItemMeta(var2);
      }

      return var1;
   }

   public void ew() {
      if (this.pd.exists()) {
         boolean var1 = this.pd.delete();
         if (!var1) {
            this.pb.getLogger().warning("Could not delete config file: " + this.pd.getName());
         }
      }

      this.a();
   }

   public String ex() {
      return this.pc;
   }

   public String ey() {
      return this.pf;
   }

   public int ez() {
      return this.pg;
   }

   public Material eA() {
      return this.ph;
   }

   public String eB() {
      return this.pi;
   }

   public boolean eC() {
      return this.pj;
   }

   public Map<String, s0> eD() {
      return this.pk;
   }

   public FileConfiguration eE() {
      return this.pe;
   }

   public File eF() {
      return this.pd;
   }

   public s0 bj(String var1) {
      return this.pk.get(var1);
   }

   public s0 n(int var1) {
      for (s0 var3 : this.pk.values()) {
         if (var3.ff() == var1) {
            return var3;
         }
      }

      return null;
   }

   public void a(String var1, int var2) {
      s0 var3 = this.pk.get(var1);
      if (var3 != null) {
         var3.s(var2);
      }
   }

   public boolean o(int var1) {
      return this.n(var1) != null;
   }

   public void bk(String var1) {
      this.pf = this.ag(var1);
   }

   public void p(int var1) {
      this.pg = var1;
   }

   public void q(Material var1) {
      this.ph = var1;
   }

   public void bl(String var1) {
      this.pi = this.ag(var1);
   }

   public void q(boolean var1) {
      this.pj = var1;
   }

   private String ag(String var1) {
      return Y.ag(var1);
   }

   private String bm(String var1) {
      return Y.bm(var1);
   }

   public void C() {
      this.a();
   }
}
