package com.sheldera.practicebot.x;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class j {
   private static final List<String> aw = List.of("leather", "copper", "gold", "chain", "iron", "diamond", "netherite");
   private final PracticeBotPlugin ax;

   public j(PracticeBotPlugin var1) {
      this.ax = var1;
   }

   public void a(NPC var1, BotTrait var2) {
      if (this.ax.isLicenseActive()) {
         if (var1.getEntity() instanceof Player var3) {
            FileConfiguration var14 = this.ax.getDefaultInvConfig();
            TrimPattern var5 = this.a(var14, var2.getHelmetTrimPattern(), "helmet-trim-pattern");
            TrimMaterial var6 = this.b(var14, var2.getHelmetTrimMaterial(), "helmet-trim-material");
            TrimPattern var7 = this.a(var14, var2.getChestTrimPattern(), "chest-trim-pattern");
            TrimMaterial var8 = this.b(var14, var2.getChestTrimMaterial(), "chest-trim-material");
            TrimPattern var9 = this.a(var14, var2.getLegsTrimPattern(), "legs-trim-pattern");
            TrimMaterial var10 = this.b(var14, var2.getLegsTrimMaterial(), "legs-trim-material");
            TrimPattern var11 = this.a(var14, var2.getBootsTrimPattern(), "boots-trim-pattern");
            TrimMaterial var12 = this.b(var14, var2.getBootsTrimMaterial(), "boots-trim-material");
            var3.getInventory().setHelmet(this.a(var2.getHelmetMaterial(), var2.getHelmetEnchant(), var5, var6));
            var3.getInventory().setChestplate(this.b(var2.getChestplateMaterial(), var2.getChestplateEnchant(), var7, var8));
            var3.getInventory().setLeggings(this.c(var2.getLeggingsMaterial(), var2.getLeggingsEnchant(), var9, var10));
            var3.getInventory().setBoots(this.d(var2.getBootsMaterial(), var2.getBootsEnchant(), var11, var12));
            if (var2.getCustomMainHand() != null) {
               var3.getInventory().setItemInMainHand(var2.getCustomMainHand().clone());
            } else {
               var3.getInventory().setItemInMainHand(this.createModernWeapon(var2));
            }

            if ("mace".equals(var2.getWeaponProfile())) {
               this.giveWindCharges(var3, var14);
            }

            if (var2.isShieldInMainHand()) {
               var3.getInventory().setItemInMainHand(this.F());
               var3.getInventory().setItemInOffHand(this.a(var2));
            } else if (!var2.isHoldShield() && !var2.isUseShield()) {
               String var13 = var2.getOffhandType();
               if (var13.equalsIgnoreCase("shield")) {
                  var3.getInventory().setItemInOffHand(this.F());
               } else {
                  var3.getInventory().setItemInOffHand(this.a(var2));
               }
            } else {
               var3.getInventory().setItemInOffHand(this.F());
            }
         }
      }
   }

   public ItemStack a(String var1, String var2, TrimPattern var3, TrimMaterial var4) {
      Material var5 = this.a(var1, "HELMET", Material.NETHERITE_HELMET);
      ItemStack var6 = new ItemStack(var5);
      this.a(var6, var2, 4);
      ItemMeta var7 = var6.getItemMeta();
      if (var7 != null) {
         var7.setUnbreakable(true);
         var7.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES});
         if (var3 != null && var4 != null && var7 instanceof ArmorMeta var8) {
            var8.setTrim(new ArmorTrim(var4, var3));
         }

         var6.setItemMeta(var7);
      }

      return var6;
   }

   public ItemStack b(String var1, String var2, TrimPattern var3, TrimMaterial var4) {
      Material var5 = this.a(var1, "CHESTPLATE", Material.NETHERITE_CHESTPLATE);
      ItemStack var6 = new ItemStack(var5);
      this.a(var6, var2, 4);
      ItemMeta var7 = var6.getItemMeta();
      if (var7 != null) {
         var7.setUnbreakable(true);
         var7.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES});
         if (var3 != null && var4 != null && var7 instanceof ArmorMeta var8) {
            var8.setTrim(new ArmorTrim(var4, var3));
         }

         var6.setItemMeta(var7);
      }

      return var6;
   }

   public ItemStack c(String var1, String var2, TrimPattern var3, TrimMaterial var4) {
      Material var5 = this.a(var1, "LEGGINGS", Material.NETHERITE_LEGGINGS);
      ItemStack var6 = new ItemStack(var5);
      this.a(var6, var2, 4);
      ItemMeta var7 = var6.getItemMeta();
      if (var7 != null) {
         var7.setUnbreakable(true);
         var7.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES});
         if (var3 != null && var4 != null && var7 instanceof ArmorMeta var8) {
            var8.setTrim(new ArmorTrim(var4, var3));
         }

         var6.setItemMeta(var7);
      }

      return var6;
   }

   public ItemStack d(String var1, String var2, TrimPattern var3, TrimMaterial var4) {
      Material var5 = this.a(var1, "BOOTS", Material.NETHERITE_BOOTS);
      ItemStack var6 = new ItemStack(var5);
      this.a(var6, var2, 4);
      ItemMeta var7 = var6.getItemMeta();
      if (var7 != null) {
         var7.setUnbreakable(true);
         var7.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES});
         if (var3 != null && var4 != null && var7 instanceof ArmorMeta var8) {
            var8.setTrim(new ArmorTrim(var4, var3));
         }

         var6.setItemMeta(var7);
      }

      return var6;
   }

   private void a(ItemStack var1, String var2, int var3) {
      if (var1 != null) {
         String var4 = var2 == null ? "" : var2.trim();
         if (var4.isEmpty()) {
            this.b(var1, "protection", var3);
         } else if (!"none".equalsIgnoreCase(var4)) {
            boolean var5 = false;

            for (String var9 : var4.split(",")) {
               String var10 = var9.trim();
               if (!var10.isEmpty()) {
                  String[] var11 = var10.split(":", 2);
                  String var12 = this.ah(var11[0]);
                  if ("none".equals(var12)) {
                     var5 = true;
                  } else {
                     int var13 = var3;
                     if (var11.length == 2) {
                        try {
                           var13 = Math.max(1, Integer.parseInt(var11[1].trim()));
                        } catch (NumberFormatException var15) {
                           var13 = var3;
                        }
                     }

                     this.b(var1, var12, var13);
                     var5 = true;
                  }
               }
            }

            if (!var5) {
               this.b(var1, "protection", var3);
            }
         }
      }
   }

   private String ah(String var1) {
      if (var1 != null && !var1.isBlank()) {
         String var2 = var1.trim().toLowerCase(Locale.ROOT);

         return switch (var2) {
            case "none", "off", "disabled", "no_enchant" -> "none";
            case "prot", "protection" -> "protection";
            case "blast", "blast_protection" -> "blast_protection";
            case "fire", "fire_protection" -> "fire_protection";
            case "projectile", "projectile_protection" -> "projectile_protection";
            case "sharp", "sharpness" -> "sharpness";
            case "kb", "knockback" -> "knockback";
            case "fireaspect", "fire_aspect" -> "fire_aspect";
            default -> var1.trim().toLowerCase(Locale.ROOT);
         };
      } else {
         return "protection";
      }
   }

   public ItemStack E() {
      ItemStack var1 = new ItemStack(Material.NETHERITE_SWORD);
      this.b(var1, "sharpness", 5);
      ItemMeta var2 = var1.getItemMeta();
      if (var2 != null) {
         var2.setUnbreakable(true);
         var2.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES});
         var1.setItemMeta(var2);
      }

      return var1;
   }

   private ItemStack createModernWeapon(BotTrait trait) {
      String profile = trait.getWeaponProfile();

      if ("mace".equals(profile)) {
         ItemStack mace = new ItemStack(Material.MACE);
         int density = Math.max(0, this.ax.getDefaultInvConfig().getInt("weapon.mace.density", 5));
         int breach = Math.max(0, this.ax.getDefaultInvConfig().getInt("weapon.mace.breach", 0));
         int windBurst = Math.max(0, this.ax.getDefaultInvConfig().getInt("weapon.mace.wind-burst", 3));

         // Vanilla Mace makes Density and Breach mutually exclusive.
         if (density > 0 && breach > 0) {
            breach = 0;
         }

         if (density > 0) this.b(mace, "density", density);
         if (breach > 0) this.b(mace, "breach", breach);
         if (windBurst > 0) this.b(mace, "wind_burst", windBurst);

         ItemMeta meta = mace.getItemMeta();
         if (meta != null) {
            meta.setUnbreakable(true);
            meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES});
            mace.setItemMeta(meta);
         }

         return mace;
      }

      if ("spear".equals(profile)) {
         String configured = this.ax.getDefaultInvConfig().getString("weapon.spear.material", "netherite");
         String normalized = configured == null ? "NETHERITE" : configured.trim().toUpperCase(Locale.ROOT);
         if ("GOLD".equals(normalized)) normalized = "GOLDEN";

         Material spearMaterial = Material.matchMaterial(normalized + "_SPEAR");
         if (spearMaterial != null) {
            ItemStack spear = new ItemStack(spearMaterial);
            int sharpness = Math.max(0, this.ax.getDefaultInvConfig().getInt("weapon.spear.sharpness", 5));
            int lunge = Math.max(0, this.ax.getDefaultInvConfig().getInt("weapon.spear.lunge", 3));

            if (sharpness > 0) this.b(spear, "sharpness", sharpness);
            // Lunge only exists on 1.21.11+. The enchantment helper already has safe fallbacks.
            if (lunge > 0) this.b(spear, "lunge", lunge);

            ItemMeta meta = spear.getItemMeta();
            if (meta != null) {
               meta.setUnbreakable(true);
               meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES});
               spear.setItemMeta(meta);
            }

            return spear;
         }

         this.ax.getLogger().warning("Spear profile requested, but this server does not expose Spear materials. Falling back to Netherite Sword.");
      }

      return this.E();
   }

   private void giveWindCharges(Player player, FileConfiguration defaults) {
      int amount = Math.max(0, Math.min(64, defaults.getInt("weapon.mace.wind-charges", 16)));
      if (amount <= 0) return;

      ItemStack charges = new ItemStack(Material.WIND_CHARGE, amount);
      for (int slot = 9; slot < player.getInventory().getSize(); slot++) {
         ItemStack current = player.getInventory().getItem(slot);
         if (current == null || current.getType().isAir()) {
            player.getInventory().setItem(slot, charges);
            return;
         }
      }
   }

   public ItemStack F() {
      ItemStack var1 = new ItemStack(Material.SHIELD);
      ItemMeta var2 = var1.getItemMeta();
      if (var2 != null) {
         var2.setUnbreakable(true);
         var2.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE});
         var1.setItemMeta(var2);
      }

      return var1;
   }

   public List<String> G() {
      ArrayList var1 = new ArrayList();

      for (String var3 : aw) {
         if (this.aj(var3)) {
            var1.add(var3);
         }
      }

      if (var1.isEmpty()) {
         var1.add("diamond");
         var1.add("netherite");
      }

      return var1;
   }

   public String ai(String var1) {
      if (var1 != null && !var1.isBlank()) {
         String var2 = var1.trim().toLowerCase(Locale.ROOT);

         var2 = switch (var2) {
            case "golden" -> "gold";
            case "chainmail" -> "chain";
            default -> var2;
         };
         if ("copper".equals(var2) && !this.aj("copper")) {
            return "iron";
         } else {
            return this.G().contains(var2) ? var2 : "netherite";
         }
      } else {
         return "netherite";
      }
   }

   public String b(String var1, boolean var2) {
      List var3 = this.G();
      String var4 = this.ai(var1);
      int var5 = var3.indexOf(var4);
      if (var5 == -1) {
         return (String)var3.get(0);
      } else {
         int var6 = var2 ? (var5 - 1 + var3.size()) % var3.size() : (var5 + 1) % var3.size();
         return (String)var3.get(var6);
      }
   }

   public Material g(String var1, String var2) {
      return this.a(var1, var2, this.ak(var2));
   }

   private boolean aj(String var1) {
      return Material.matchMaterial(this.al(var1) + "_HELMET") != null
         && Material.matchMaterial(this.al(var1) + "_CHESTPLATE") != null
         && Material.matchMaterial(this.al(var1) + "_LEGGINGS") != null
         && Material.matchMaterial(this.al(var1) + "_BOOTS") != null;
   }

   private Material a(String var1, String var2, Material var3) {
      String var4 = this.al(this.ai(var1));
      Material var5 = Material.matchMaterial(var4 + "_" + var2);
      return var5 != null ? var5 : var3;
   }

   private Material ak(String var1) {
      String var2 = var1 == null ? "" : var1.toUpperCase(Locale.ROOT);

      return switch (var2) {
         case "HELMET" -> Material.NETHERITE_HELMET;
         case "LEGGINGS" -> Material.NETHERITE_LEGGINGS;
         case "BOOTS" -> Material.NETHERITE_BOOTS;
         default -> Material.NETHERITE_CHESTPLATE;
      };
   }

   private String al(String var1) {
      String var2 = var1 == null ? "" : var1.trim().toLowerCase(Locale.ROOT);

      return switch (var2) {
         case "gold" -> "GOLDEN";
         case "chain" -> "CHAINMAIL";
         default -> var1 == null ? "NETHERITE" : var1.trim().toUpperCase(Locale.ROOT);
      };
   }

   private ItemStack a(BotTrait var1) {
      return var1 != null && var1.getTotemCount() > 0 && !"none".equalsIgnoreCase(var1.getOffhandType())
         ? new ItemStack(Material.TOTEM_OF_UNDYING)
         : new ItemStack(Material.AIR);
   }

   public void b(ItemStack var1, String var2, int var3) {
      if (var1 != null) {
         try {
            Enchantment var4 = (Enchantment)Registry.ENCHANTMENT.get(NamespacedKey.minecraft(var2));
            if (var4 != null) {
               var1.addUnsafeEnchantment(var4, var3);
               return;
            }
         } catch (Exception var8) {
            this.ax.getLogger().warning("Registry method failed for " + var2 + ": " + var8.getMessage());
         }

         try {
            String var10 = switch (var2) {
               case "protection" -> "PROTECTION_ENVIRONMENTAL";
               case "blast_protection" -> "PROTECTION_EXPLOSIONS";
               case "fire_protection" -> "PROTECTION_FIRE";
               case "projectile_protection" -> "PROTECTION_PROJECTILE";
               case "sharpness" -> "DAMAGE_ALL";
               case "knockback" -> "KNOCKBACK";
               case "fire_aspect" -> "FIRE_ASPECT";
               case "unbreaking" -> "DURABILITY";
               default -> var2.toUpperCase();
            };
            Enchantment var5 = Enchantment.getByName(var10);
            if (var5 != null) {
               var1.addUnsafeEnchantment(var5, var3);
               return;
            }
         } catch (Exception var7) {
            this.ax.getLogger().warning("Legacy method failed for " + var2 + ": " + var7.getMessage());
         }

         try {
            for (Enchantment var12 : Registry.ENCHANTMENT) {
               NamespacedKey var13 = Registry.ENCHANTMENT.getKey(var12);
               if (var13 != null && var13.getKey().equalsIgnoreCase(var2)) {
                  var1.addUnsafeEnchantment(var12, var3);
                  return;
               }
            }
         } catch (Exception var9) {
            this.ax.getLogger().warning("Iteration method failed for " + var2 + ": " + var9.getMessage());
         }

         this.ax.getLogger().severe("FAILED to apply enchantment: " + var2 + " - All methods exhausted!");
      }
   }

   public TrimPattern a(FileConfiguration var1, String var2) {
      String var3 = var1.getString(var2, null);
      return var3 != null && !var3.isEmpty() ? (TrimPattern)Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(var3)) : null;
   }

   public TrimMaterial b(FileConfiguration var1, String var2) {
      String var3 = var1.getString(var2, null);
      return var3 != null && !var3.isEmpty() ? (TrimMaterial)Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(var3)) : null;
   }

   public String a(TrimPattern var1) {
      if (var1 == null) {
         return null;
      } else {
         NamespacedKey var2 = Registry.TRIM_PATTERN.getKey(var1);
         return var2 != null ? var2.getKey() : null;
      }
   }

   public String a(TrimMaterial var1) {
      if (var1 == null) {
         return null;
      } else {
         NamespacedKey var2 = Registry.TRIM_MATERIAL.getKey(var1);
         return var2 != null ? var2.getKey() : null;
      }
   }

   public TrimPattern am(String var1) {
      return var1 != null && !var1.isBlank() ? (TrimPattern)Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(var1.trim().toLowerCase())) : null;
   }

   public TrimMaterial an(String var1) {
      return var1 != null && !var1.isBlank() ? (TrimMaterial)Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(var1.trim().toLowerCase())) : null;
   }

   private TrimPattern a(FileConfiguration var1, String var2, String var3) {
      TrimPattern var4 = this.am(var2);
      return var4 != null ? var4 : this.a(var1, var3);
   }

   private TrimMaterial b(FileConfiguration var1, String var2, String var3) {
      TrimMaterial var4 = this.an(var2);
      return var4 != null ? var4 : this.b(var1, var3);
   }

   public boolean a(Material var1) {
      return var1 == null ? false : var1.name().endsWith("_SWORD");
   }

   public String b(Material var1) {
      if (var1 == null) {
         return "Unknown";
      } else {
         String var2 = var1.name().replace("_", " ");
         StringBuilder var3 = new StringBuilder();

         for (String var7 : var2.split(" ")) {
            if (var3.length() > 0) {
               var3.append(" ");
            }

            var3.append(var7.charAt(0)).append(var7.substring(1).toLowerCase());
         }

         return var3.toString();
      }
   }
}
