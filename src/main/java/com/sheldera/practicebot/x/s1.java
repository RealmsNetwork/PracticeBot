package com.sheldera.practicebot.x;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class s1 {
   private final PracticeBotPlugin pY;
   private final b pZ;
   private final o1 qa;
   private static final int qb = 2;
   private static final int qc = 4;
   private static final int qd = 6;
   private static final int qe = 10;
   private static final int qf = 12;
   private static final int qg = 14;
   private static final int qh = 16;
   private static final int qi = 20;
   private static final int qj = 24;
   private static final int qk = 22;
   private static final int ql = 4;
   private static final int qm = 9;
   private static final int qn = 10;
   private static final int qo = 11;
   private static final int qp = 12;
   private static final int qq = 13;
   private static final int qr = 14;
   private static final int qs = 15;
   private static final int qt = 17;
   private static final int qu = 18;
   private static final int qv = 19;
   private static final int qw = 20;
   private static final int qx = 21;
   private static final int qy = 26;
   private static final int qz = 4;
   private static final int qA = 13;
   private static final int qB = 20;
   private static final int qC = 22;
   private static final int qD = 24;
   private static final int qE = 29;
   private static final int qF = 31;
   private static final int qG = 33;
   private static final int qH = 40;
   private static final int qI = 48;
   private static final int qJ = 50;
   private static final int qK = 4;
   private static final int qL = 13;
   private static final int qM = 11;
   private static final int qN = 15;
   private static final int qO = 22;
   private static final int qP = 20;
   private static final int qQ = 24;
   private static final int qR = 31;
   private static final int qS = 29;
   private static final int qT = 33;
   private static final int qU = 40;
   private static final int qV = 38;
   private static final int qW = 42;
   private static final int qX = 49;

   public s1(PracticeBotPlugin var1) {
      this.pY = var1;
      this.pZ = var1.getConfigManager();
      this.qa = var1.getGuiConfigManager();
   }

   public void f(Player var1, String var2) {
      try {
         var1.playSound(var1.getLocation(), Sound.valueOf(var2), 0.8F, 1.1F);
      } catch (Exception var4) {
      }
   }

   public void bf(Player var1) {
      if (!this.pY.isLicenseActive()) {
         var1.sendMessage(this.pY.licenseLockMessage());
      } else {
         boolean var2 = var1.hasPermission("practicebot.spawn.normal") || var1.hasPermission(this.pZ.bm());
         boolean var3 = var1.hasPermission("practicebot.spawn.cpvp") || var1.hasPermission(this.pZ.bm());
         Inventory var4 = Bukkit.createInventory(new v1(), 27, "§8§lSelect Bot Type");
         ItemStack var5 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
         ItemMeta var6 = var5.getItemMeta();
         if (var6 != null) {
            var6.setDisplayName(" ");
            var5.setItemMeta(var6);
         }

         for (int var7 = 0; var7 < 27; var7++) {
            var4.setItem(var7, var5);
         }

         if (var2) {
            ItemStack var9 = new ItemStack(Material.IRON_SWORD);
            ItemMeta var8 = var9.getItemMeta();
            if (var8 != null) {
               var8.setDisplayName("§a§lNormal PvP Bot");
               var8.setLore(
                  Arrays.asList(
                     "§7A practice bot for standard PvP combat.",
                     " ",
                     "§7Features:",
                     "§8• §fStrafe, W-Tap, S-Tap mechanics",
                     "§8• §fCritical hits & Shield breaking",
                     "§8• §fCustomizable aggression levels",
                     " ",
                     "§eClick to spawn!"
                  )
               );
               var8.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
               var9.setItemMeta(var8);
            }

            var4.setItem(11, var9);
         } else {
            var4.setItem(11, this.e0("§c§lNormal PvP Bot"));
         }

         if (var3) {
            ItemStack var10 = new ItemStack(Material.END_CRYSTAL);
            ItemMeta var12 = var10.getItemMeta();
            if (var12 != null) {
               var12.setDisplayName("§d§lCrystal PvP Bot");
               var12.setLore(
                  Arrays.asList(
                     "§7A practice bot for Crystal PvP combat.",
                     " ",
                     "§7Features:",
                     "§8• §fCrystal placement & breaking",
                     "§8• §fObsidian placement strategies",
                     "§8• §fPearl & Mace combat",
                     "§8• §fMultiple skill levels",
                     " ",
                     "§eClick to spawn!"
                  )
               );
               var10.setItemMeta(var12);
            }

            var4.setItem(15, var10);
         } else {
            var4.setItem(15, this.e0("§c§lCrystal PvP Bot"));
         }

         ItemStack var11 = new ItemStack(Material.BARRIER);
         ItemMeta var13 = var11.getItemMeta();
         if (var13 != null) {
            var13.setDisplayName("§c§lClose");
            var11.setItemMeta(var13);
         }

         var4.setItem(22, var11);
         this.f(var1, this.pZ.br());
         var1.openInventory(var4);
      }
   }

   private ItemStack e0(String var1) {
      ItemStack var2 = new ItemStack(Material.BARRIER);
      ItemMeta var3 = var2.getItemMeta();
      if (var3 != null) {
         var3.setDisplayName(var1);
         var3.setLore(Arrays.asList("§7You don't have permission", "§7to spawn this bot type."));
         var2.setItemMeta(var3);
      }

      return var2;
   }

   public void b(Player var1, NPC var2, BotTrait var3) {
      this.a(var1, var2, var3, true);
   }

   public void a(Player var1, NPC var2, BotTrait var3, boolean var4) {
      if (!this.pY.isLicenseActive()) {
         var1.sendMessage(this.pY.licenseLockMessage());
      } else {
         o0 var5 = this.qa.eI();
         int var6 = var5 != null ? var5.ez() : 27;
         String var7 = var5 != null ? var5.ey() : this.pZ.bn();
         Inventory var8 = Bukkit.createInventory(new u1(var2.getId()), var6, var7);
         ItemStack var9;
         if (var5 != null && var5.eC()) {
            var9 = var5.ev();
         } else {
            var9 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta var10 = var9.getItemMeta();
            if (var10 != null) {
               var10.setDisplayName(" ");
               var9.setItemMeta(var10);
            }
         }

         for (int var19 = 0; var19 < var6; var19++) {
            var8.setItem(var19, var9);
         }

         HashMap var20 = new HashMap();
         var20.put("{player_name}", var1.getName());
         var20.put("{look_status}", this.s(var3.isLookAtOwner()));
         var20.put("{pvp_status}", this.s(var3.isPvpEnabled()));
         var20.put("{follow_status}", this.s(var3.isFollowOwner()));
         var20.put("{random_status}", this.s(var3.isRandomWalk()));
         var20.put("{shield_status}", this.s(var3.isHoldShield()));
         var20.put("{use_shield_status}", this.s(var3.isUseShield()));
         var20.put("{resistance_status}", this.s(var3.isResistance()));
         var20.put("{shield_hand}", var3.isShieldInMainHand() ? "§6Main Hand" : "§7Off Hand");
         if (var5 != null) {
            for (s0 var12 : var5.eD().values()) {
               int var13 = var12.ff();
               if (var13 >= 0 && var13 < var6) {
                  String var14 = var12.fe();
                  HashMap var15 = new HashMap(var20);
                  String var16 = this.a(var14, var3);
                  var15.put("{status}", var16);
                  ItemStack var17 = var5.a(var12, var15);
                  Material var18 = this.a(var14, var3, var12.fg());
                  if (var18 != var12.fg()) {
                     var17.setType(var18);
                  }

                  var8.setItem(var13, var17);
               }
            }
         } else {
            this.a(var8, var1, var3);
         }

         if (var4) {
            this.f(var1, this.pZ.br());
         }

         var1.openInventory(var8);
      }
   }

   private String a(String var1, BotTrait var2) {
      return switch (var1) {
         case "look" -> this.s(var2.isLookAtOwner());
         case "resistance" -> this.s(var2.isResistance());
         case "pvp_settings" -> var2.isPvpEnabled() ? "§a(ON)" : "§c(OFF)";
         case "follow" -> this.s(var2.isFollowOwner());
         case "random" -> this.s(var2.isRandomWalk());
         case "hold_shield" -> !var2.isHoldShield() ? "§cOFF" : (var2.isShieldInMainHand() ? "§6MAIN HAND" : "§aON");
         case "use_shield" -> this.s(var2.isUseShield());
         default -> "";
      };
   }

   private Material a(String var1, BotTrait var2, Material var3) {
      return switch (var1) {
         case "look" -> var2.isLookAtOwner() ? Material.LIME_DYE : Material.RED_DYE;
         case "follow" -> var2.isFollowOwner() ? Material.LIME_DYE : Material.RED_DYE;
         case "random" -> var2.isRandomWalk() ? Material.LIME_DYE : Material.RED_DYE;
         case "use_shield" -> var2.isUseShield() ? Material.LIME_DYE : Material.RED_DYE;
         case "hold_shield" -> var2.isHoldShield() ? Material.SHIELD : Material.RED_DYE;
         default -> var3;
      };
   }

   public void c(Player var1, NPC var2, BotTrait var3) {
      this.b(var1, var2, var3, true);
   }

   public void b(Player var1, NPC var2, BotTrait var3, boolean var4) {
      if (!this.pY.isLicenseActive()) {
         var1.sendMessage(this.pY.licenseLockMessage());
      } else {
         o0 var5 = this.qa.eJ();
         int var6 = var5 != null ? var5.ez() : 45;
         String var7 = var5 != null ? var5.ey() : "§c§lPvP Combat Settings";
         Inventory var8 = Bukkit.createInventory(new v0(var2.getId()), var6, var7);
         ItemStack var9;
         if (var5 != null && var5.eC()) {
            var9 = var5.ev();
         } else {
            var9 = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta var10 = var9.getItemMeta();
            if (var10 != null) {
               var10.setDisplayName(" ");
               var9.setItemMeta(var10);
            }
         }

         for (int var21 = 0; var21 < var6; var21++) {
            var8.setItem(var21, var9);
         }

         HashMap var22 = new HashMap();
         var22.put("{pvp_status}", this.s(var3.isPvpEnabled()));
         var22.put("{strafe_status}", this.s(var3.isPvpStrafe()));
         var22.put("{wtap_status}", this.s(var3.isPvpWTap()));
         var22.put("{stap_status}", this.s(var3.isPvpSTap()));
         var22.put("{crit_status}", this.s(var3.isPvpCrits()));
         var22.put("{shield_breaker_status}", this.s(var3.isPvpShieldBreaker()));
         var22.put("{retreat_status}", this.s(var3.isPvpRetreat()));
         if (var5 != null) {
            for (s0 var12 : var5.eD().values()) {
               int var13 = var12.ff();
               if (var13 >= 0 && var13 < var6) {
                  String var14 = var12.fe();
                  HashMap var15 = new HashMap(var22);
                  String var16 = "§7";
                  String var17 = "";
                  String var18 = "";
                  switch (var14) {
                     case "pvp_enabled":
                        var16 = var3.isPvpEnabled() ? "§a" : "§c";
                        var17 = this.s(var3.isPvpEnabled());
                        break;
                     case "strafe":
                        var16 = var3.isPvpStrafe() ? "§a" : "§c";
                        var17 = this.s(var3.isPvpStrafe());
                        break;
                     case "wtap":
                        var16 = var3.isPvpWTap() ? "§a" : "§c";
                        var17 = this.s(var3.isPvpWTap());
                        break;
                     case "stap":
                        var16 = var3.isPvpSTap() ? "§a" : "§c";
                        var17 = this.s(var3.isPvpSTap());
                        break;
                     case "crits":
                        var16 = var3.isPvpCrits() ? "§a" : "§c";
                        var17 = this.s(var3.isPvpCrits());
                        break;
                     case "shield_breaker":
                        var16 = var3.isPvpShieldBreaker() ? "§a" : "§c";
                        var17 = this.s(var3.isPvpShieldBreaker());
                        break;
                     case "retreat":
                        var16 = var3.isPvpRetreat() ? "§a" : "§c";
                        var17 = this.s(var3.isPvpRetreat());
                        break;
                     case "reach":
                        var18 = switch (var3.getPvpReachMode()) {
                           case 0 -> "§72.0";
                           case 1 -> "§f3.0";
                           case 2 -> "§e3.5";
                           default -> "§c4.0";
                        };
                        break;
                     case "crit_chance":
                        var18 = switch (var3.getPvpCritChance()) {
                           case 0 -> "§a25%";
                           case 1 -> "§e50%";
                           case 2 -> "§675%";
                           default -> "§c100%";
                        };
                        break;
                     case "crit_speed":
                        var18 = switch (var3.getPvpCritSpeed()) {
                           case 0 -> "§aSlow";
                           case 1 -> "§eNormal";
                           default -> "§cFast";
                        };
                        break;
                     case "aggro":
                        var18 = switch (var3.getPvpAggression()) {
                           case 0 -> "§aLow";
                           case 1 -> "§eMedium";
                           default -> "§cHigh";
                        };
                  }

                  var15.put("{color}", var16);
                  var15.put("{status}", var17);
                  var15.put("{value}", var18);
                  ItemStack var19 = var5.a(var12, var15);
                  Material var23 = this.b(var14, var3);
                  if (var23 != null) {
                     var19.setType(var23);
                  }

                  var8.setItem(var13, var19);
               }
            }
         } else {
            this.a(var8, var3);
         }

         if (var4) {
            this.f(var1, this.pZ.br());
         }

         var1.openInventory(var8);
      }
   }

   private Material b(String var1, BotTrait var2) {
      return switch (var1) {
         case "pvp_enabled" -> var2.isPvpEnabled() ? Material.LIME_DYE : Material.RED_DYE;
         case "strafe" -> var2.isPvpStrafe() ? Material.LIME_DYE : Material.RED_DYE;
         case "wtap" -> var2.isPvpWTap() ? Material.LIME_DYE : Material.RED_DYE;
         case "stap" -> var2.isPvpSTap() ? Material.LIME_DYE : Material.RED_DYE;
         case "crits" -> var2.isPvpCrits() ? Material.LIME_DYE : Material.RED_DYE;
         case "shield_breaker" -> var2.isPvpShieldBreaker() ? Material.LIME_DYE : Material.RED_DYE;
         case "retreat" -> var2.isPvpRetreat() ? Material.LIME_DYE : Material.RED_DYE;
         default -> null;
      };
   }

   public void d(Player var1, NPC var2, BotTrait var3) {
      this.c(var1, var2, var3, true);
   }

   public void c(Player var1, NPC var2, BotTrait var3, boolean var4) {
      if (!this.pY.isLicenseActive()) {
         var1.sendMessage(this.pY.licenseLockMessage());
      } else {
         o0 var5 = this.qa.eK();
         int var6 = var5 != null ? var5.ez() : 54;
         String var7 = var5 != null ? var5.ey() : this.pZ.bp();
         u0 var8 = new u0(var2.getId());
         Inventory var9 = Bukkit.createInventory(var8, var6, var7);
         ItemStack var10;
         if (var5 != null && var5.eC()) {
            var10 = var5.ev();
         } else {
            var10 = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta var11 = var10.getItemMeta();
            if (var11 != null) {
               var11.setDisplayName(" ");
               var10.setItemMeta(var11);
            }
         }

         for (int var27 = 0; var27 < var6; var27++) {
            var9.setItem(var27, var10);
         }

         boolean var28 = var3.getArmorType().equals("netherite");
         boolean var12 = var3.getLeggingsMaterial().equals("netherite");
         String var13 = this.e1(var3.getHelmetEnchant());
         String var14 = this.e1(var3.getChestplateEnchant());
         String var15 = this.e1(var3.getLeggingsEnchant());
         String var16 = this.e1(var3.getBootsEnchant());
         HashMap var17 = new HashMap();
         var17.put("{armor_type}", var28 ? "§5Netherite" : "§bDiamond");
         var17.put("{helmet_enchant}", var13);
         var17.put("{helmet_material}", var28 ? "§5Netherite" : "§bDiamond");
         var17.put("{chestplate_enchant}", var14);
         var17.put("{chestplate_material}", var28 ? "§5Netherite" : "§bDiamond");
         var17.put("{leggings_material}", var12 ? "§5Netherite" : "§bDiamond");
         var17.put("{leggings_enchant}", var15);
         var17.put(
            "{leggings_info}",
            (var12 ? "§5Netherite" : "§bDiamond") + " §7(" + (var3.getLeggingsEnchant().equals("protection") ? "Protection" : "Blast Protection") + " IV)"
         );
         var17.put("{boots_enchant}", var16);
         var17.put("{boots_material}", var28 ? "§5Netherite" : "§bDiamond");
         String var18 = var3.getCustomMainHand() != null ? this.pY.getBotManager().H().b(var3.getCustomMainHand().getType()) : "Netherite Sword";
         var17.put("{mainhand_type}", var18);
         var17.put("{mainhand_item}", var18);
         var17.put("{mainhand_name}", "§f" + var18);
         var17.put("{totem_count}", String.valueOf(var3.getTotemCount()));
         var17.put("{offhand_count}", String.valueOf(var3.getTotemCount()));
         var17.put("{offhand_type}", var3.getTotemCount() > 0 ? "Totem of Undying" : "None");
         var17.put("{offhand_amount}", String.valueOf(var3.getTotemCount()));
         if (var5 != null) {
            for (s0 var20 : var5.eD().values()) {
               int var21 = var20.ff();
               if (var21 >= 0 && var21 < var6) {
                  String var22 = var20.fe();
                  if (!var22.equals("crystal_pvp")) {
                     HashMap var23 = new HashMap(var17);
                     ItemStack var24 = var5.a(var20, var23);
                     switch (var22) {
                        case "helmet":
                           var24.setType(var28 ? Material.NETHERITE_HELMET : Material.DIAMOND_HELMET);
                           break;
                        case "chestplate":
                           var24.setType(var28 ? Material.NETHERITE_CHESTPLATE : Material.DIAMOND_CHESTPLATE);
                           break;
                        case "boots":
                           var24.setType(var28 ? Material.NETHERITE_BOOTS : Material.DIAMOND_BOOTS);
                           break;
                        case "leggings":
                           var24.setType(var12 ? Material.NETHERITE_LEGGINGS : Material.DIAMOND_LEGGINGS);
                           break;
                        case "armor_toggle":
                           var24.setType(var28 ? Material.NETHERITE_INGOT : Material.DIAMOND);
                           break;
                        case "leggings_material":
                           var24.setType(var12 ? Material.NETHERITE_INGOT : Material.DIAMOND);
                           break;
                        case "mainhand":
                           if (var3.getCustomMainHand() != null) {
                              var24.setType(var3.getCustomMainHand().getType());
                           } else {
                              var24.setType(Material.NETHERITE_SWORD);
                           }
                           break;
                        case "offhand":
                           if (var3.getTotemCount() <= 0) {
                              var24.setType(Material.BARRIER);
                              var24.setAmount(1);
                           } else {
                              var24.setAmount(Math.min(64, var3.getTotemCount()));
                           }
                     }

                     var9.setItem(var21, var24);
                  }
               }
            }
         } else {
            this.b(var9, var3);
         }

         if (var4) {
            this.f(var1, this.pZ.br());
         }

         var1.openInventory(var9);
         ai var29 = this.pY.getCrystalPvpGui();
         if (var29 != null) {
            var29.b(var1.getUniqueId(), var2.getId());
         }
      }
   }

   private String e1(String var1) {
      if (var1 == null) {
         return "§aProtection IV";
      } else {
         return switch (var1) {
            case "blast_protection" -> "§6Blast Protection IV";
            case "fire_protection" -> "§cFire Protection IV";
            case "projectile_protection" -> "§bProjectile Protection IV";
            default -> "§aProtection IV";
         };
      }
   }

   public void bg(Player var1) {
      if (!this.pY.isLicenseActive()) {
         var1.sendMessage(this.pY.licenseLockMessage());
      } else {
         Inventory var2 = Bukkit.createInventory(new t1(), 54, this.pZ.bq());
         ItemStack var3 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
         ItemMeta var4 = var3.getItemMeta();
         if (var4 != null) {
            var4.setDisplayName(" ");
            var3.setItemMeta(var4);
         }

         for (int var5 = 0; var5 < 54; var5++) {
            var2.setItem(var5, var3);
         }

         this.a(var2);
         this.f(var1, this.pZ.br());
         var1.openInventory(var2);
      }
   }

   public String s(boolean var1) {
      return var1 ? "§aON" : "§cOFF";
   }

   public ItemStack f(BotTrait var1) {
      ItemStack var2;
      if (var1.getCustomMainHand() != null) {
         var2 = var1.getCustomMainHand().clone();
      } else {
         var2 = this.pY.getBotManager().H().E();
      }

      ItemMeta var3 = var2.getItemMeta();
      if (var3 != null) {
         var3.setDisplayName("§c§lMain Hand");
         ArrayList var4 = new ArrayList();
         var4.add("§7Current: §f" + this.pY.getBotManager().H().b(var2.getType()));
         var4.add(" ");
         var4.add("§eClick with a sword to replace");
         var4.add("§7Pick up a sword from your inventory,");
         var4.add("§7then click here to set it.");
         var3.setLore(var4);
         var3.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS});
         var2.setItemMeta(var3);
      }

      return var2;
   }

   public ItemStack g(BotTrait var1) {
      ItemStack var2 = new ItemStack(Material.TOTEM_OF_UNDYING);
      ItemMeta var3 = var2.getItemMeta();
      if (var3 != null) {
         var3.setDisplayName("§6§lOff Hand - Totems");
         ArrayList var4 = new ArrayList();
         var4.add("§7Totem Count: §e" + var1.getTotemCount());
         var4.add(" ");
         var4.add("§eClick to cycle count");
         var4.add("§72 → 4 → 8 → 16 → 32 → 64 → 99");
         var3.setLore(var4);
         var3.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
         var2.setItemMeta(var3);
      }

      return var2;
   }

   public int w(int var1) {
      return switch (var1) {
         case 0 -> 1;
         case 1 -> 2;
         case 2 -> 4;
         case 4 -> 8;
         case 8 -> 16;
         case 16 -> 32;
         case 32 -> 64;
         case 64 -> 99;
         default -> 0;
      };
   }

   private void a(Inventory var1, Player var2, BotTrait var3) {
      ItemStack var4 = new ItemStack(Material.NETHERITE_HELMET);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName("§b" + var2.getName() + "'s Practice Bot");
         var5.setLore(
            Arrays.asList(
               "§7A personal PvP practice bot.",
               " ",
               "§7Look At You: " + this.s(var3.isLookAtOwner()),
               "§7Bot PvP: " + this.s(var3.isPvpEnabled()),
               "§7Follow: " + this.s(var3.isFollowOwner()),
               "§7Random Walk: " + this.s(var3.isRandomWalk()),
               "§7Hold Shield: " + this.s(var3.isHoldShield()),
               "§7Use Shield: " + this.s(var3.isUseShield()),
               "§7Resistance: " + this.s(var3.isResistance())
            )
         );
         var5.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES});
         var4.setItemMeta(var5);
      }

      var1.setItem(13, var4);
      var1.setItem(2, this.a("Look At You", "Bot looks at you.", var3.isLookAtOwner()));
      var1.setItem(4, this.t(var3.isResistance()));
      var1.setItem(
         6,
         this.c(
            Material.IRON_SWORD,
            "§cBot PvP Settings " + (var3.isPvpEnabled() ? "§a(ON)" : "§c(OFF)"),
            Arrays.asList("§7Configure PvP settings.", " ", "§eClick to open.")
         )
      );
      var1.setItem(10, this.a("Follow You", "Bot follows you.", var3.isFollowOwner()));
      var1.setItem(12, this.a("Random Walk", "Bot wanders randomly.", var3.isRandomWalk()));
      var1.setItem(14, this.c(var3.isHoldShield(), var3.isShieldInMainHand()));
      var1.setItem(16, this.a("Use Shield", "Bot blocks hits.", var3.isUseShield()));
      var1.setItem(20, this.c(Material.CHEST, "§6Bot Inventory", Arrays.asList("§7Open bot inventory.")));
      var1.setItem(24, this.c(Material.REDSTONE_BLOCK, "§cDespawn Bot", Collections.singletonList("§7Remove this bot.")));
      var1.setItem(22, this.c(Material.BARRIER, "§cClose", Collections.singletonList("§7Close this menu.")));
   }

   private void a(Inventory var1, BotTrait var2) {
      ItemStack var3 = new ItemStack(Material.DIAMOND_SWORD);
      ItemMeta var4 = var3.getItemMeta();
      if (var4 != null) {
         var4.setDisplayName("§c§lPvP Combat Settings");
         var4.setLore(
            Arrays.asList(
               "§7Configure bot combat behavior.",
               " ",
               "§7PvP: " + this.s(var2.isPvpEnabled()),
               "§7Strafe: " + this.s(var2.isPvpStrafe()),
               "§7W-Tap: " + this.s(var2.isPvpWTap()),
               "§7S-Tap: " + this.s(var2.isPvpSTap()),
               "§7Crits: " + this.s(var2.isPvpCrits()),
               "§7Shield Breaker: " + this.s(var2.isPvpShieldBreaker()),
               "§7Retreat: " + this.s(var2.isPvpRetreat())
            )
         );
         var4.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
         var3.setItemMeta(var4);
      }

      var1.setItem(4, var3);
      var1.setItem(9, this.b("PvP Enabled", "Enable combat AI.", var2.isPvpEnabled()));
      var1.setItem(10, this.b("Strafe", "Circle-strafe around target.", var2.isPvpStrafe()));
      var1.setItem(11, this.b("W-Tap", "Sprint reset for extra KB.", var2.isPvpWTap()));
      var1.setItem(12, this.b("S-Tap", "Reduce KB when hit.", var2.isPvpSTap()));
      var1.setItem(13, this.b("Critical Hits", "Jump for crit damage.", var2.isPvpCrits()));
      var1.setItem(14, this.b("Shield Breaker", "Auto axe switch.", var2.isPvpShieldBreaker()));
      var1.setItem(15, this.b("Retreat", "Move back after taking hits.", var2.isPvpRetreat()));

      String var5 = switch (var2.getPvpReachMode()) {
         case 0 -> "§72.0";
         case 1 -> "§f3.0";
         case 2 -> "§e3.5";
         default -> "§c4.0";
      };
      var1.setItem(18, this.a(Material.ENDER_PEARL, "§bReach", var5, Arrays.asList("§72.0", "§f3.0", "§e3.5", "§c4.0")));

      String var6 = switch (var2.getPvpCritChance()) {
         case 0 -> "§a25%";
         case 1 -> "§e50%";
         case 2 -> "§675%";
         default -> "§c100%";
      };
      var1.setItem(19, this.a(Material.NETHER_STAR, "§eCrit Chance", var6, Arrays.asList("§a25%", "§e50%", "§675%", "§c100%")));

      String var7 = switch (var2.getPvpCritSpeed()) {
         case 0 -> "§aSlow";
         case 1 -> "§eNormal";
         default -> "§cFast";
      };
      var1.setItem(20, this.a(Material.SUGAR, "§dCrit Speed", var7, Arrays.asList("§aSlow", "§eNormal", "§cFast")));

      String var8 = switch (var2.getPvpAggression()) {
         case 0 -> "§aLow";
         case 1 -> "§eMedium";
         default -> "§cHigh";
      };
      var1.setItem(21, this.a(Material.BLAZE_POWDER, "§cAggression", var8, Arrays.asList("§aLow", "§eMedium", "§cHigh")));
      ItemStack var9 = new ItemStack(Material.ARROW);
      ItemMeta var10 = var9.getItemMeta();
      if (var10 != null) {
         var10.setDisplayName("§c« Back");
         var9.setItemMeta(var10);
      }

      var1.setItem(26, var9);
   }

   private void b(Inventory var1, BotTrait var2) {
      boolean var3 = var2.getArmorType().equals("netherite");
      boolean var4 = var2.getLeggingsMaterial().equals("netherite");
      boolean var5 = var2.getLeggingsEnchant().equals("protection");
      ItemStack var6 = new ItemStack(Material.BOOK);
      ItemMeta var7 = var6.getItemMeta();
      if (var7 != null) {
         var7.setDisplayName("§b§lBot Equipment");
         var7.setLore(
            Arrays.asList(
               "§7Configure your bot's equipment",
               " ",
               "§7Armor: " + (var3 ? "§5Netherite" : "§bDiamond"),
               "§7Leggings: " + (var4 ? "§5Netherite" : "§bDiamond") + " §7(" + (var5 ? "Protection" : "Blast Protection") + " IV)",
               "§7Main Hand: §f" + (var2.getCustomMainHand() != null ? this.pY.getBotManager().H().b(var2.getCustomMainHand().getType()) : "Netherite Sword"),
               "§7Totems: §e" + var2.getTotemCount()
            )
         );
         var6.setItemMeta(var7);
      }

      var1.setItem(4, var6);
      var1.setItem(20, this.f(var2));
      var1.setItem(29, this.g(var2));
      ItemStack var8 = new ItemStack(Material.ARROW);
      ItemMeta var9 = var8.getItemMeta();
      if (var9 != null) {
         var9.setDisplayName("§c§l« Back");
         var9.setLore(Collections.singletonList("§7Return to settings menu"));
         var8.setItemMeta(var9);
      }

      var1.setItem(48, var8);
      ItemStack var10 = new ItemStack(Material.BARRIER);
      ItemMeta var11 = var10.getItemMeta();
      if (var11 != null) {
         var11.setDisplayName("§c§lClose");
         var11.setLore(Collections.singletonList("§7Close this menu"));
         var10.setItemMeta(var11);
      }

      var1.setItem(50, var10);
   }

   public void a(Inventory var1) {
      FileConfiguration var2 = this.pY.getDefaultInvConfig();
      TrimPattern var3 = this.pY.getBotManager().H().a(var2, "helmet-trim-pattern");
      TrimMaterial var4 = this.pY.getBotManager().H().b(var2, "helmet-trim-material");
      TrimPattern var5 = this.pY.getBotManager().H().a(var2, "chest-trim-pattern");
      TrimMaterial var6 = this.pY.getBotManager().H().b(var2, "chest-trim-material");
      TrimPattern var7 = this.pY.getBotManager().H().a(var2, "legs-trim-pattern");
      TrimMaterial var8 = this.pY.getBotManager().H().b(var2, "legs-trim-material");
      TrimPattern var9 = this.pY.getBotManager().H().a(var2, "boots-trim-pattern");
      TrimMaterial var10 = this.pY.getBotManager().H().b(var2, "boots-trim-material");
      ItemStack var11 = new ItemStack(Material.BOOK);
      ItemMeta var12 = var11.getItemMeta();
      if (var12 != null) {
         var12.setDisplayName("§6§lDefault Bot Armor Trims");
         var12.setLore(
            Arrays.asList(
               "§7Configure armor trims for new bots",
               " ",
               "§e§lHow to add trims:",
               "§7• Pick up a §fSmithing Template §7from your inventory",
               "§7• Click on a §eTrim Pattern Slot §7(left of armor)",
               "§7• Pick up a §fMaterial (ingot/ore) §7from your inventory",
               "§7• Click on a §6Trim Material Slot §7(right of armor)",
               " ",
               "§c§lTo remove:",
               "§7• §cRight-Click §7on any trim slot to remove it"
            )
         );
         var11.setItemMeta(var12);
      }

      var1.setItem(4, var11);
      var1.setItem(11, this.a("pattern", var3));
      var1.setItem(13, this.a(this.pY.getBotManager().H().a("netherite", "protection", var3, var4), "§b§lHelmet", var3, var4));
      var1.setItem(15, this.a("material", var4));
      var1.setItem(20, this.a("pattern", var5));
      var1.setItem(22, this.a(this.pY.getBotManager().H().b("netherite", "protection", var5, var6), "§b§lChestplate", var5, var6));
      var1.setItem(24, this.a("material", var6));
      var1.setItem(29, this.a("pattern", var7));
      var1.setItem(31, this.a(this.pY.getBotManager().H().c("netherite", "protection", var7, var8), "§b§lLeggings", var7, var8));
      var1.setItem(33, this.a("material", var8));
      var1.setItem(38, this.a("pattern", var9));
      var1.setItem(40, this.a(this.pY.getBotManager().H().d("netherite", "protection", var9, var10), "§b§lBoots", var9, var10));
      var1.setItem(42, this.a("material", var10));
      ItemStack var13 = new ItemStack(Material.BARRIER);
      ItemMeta var14 = var13.getItemMeta();
      if (var14 != null) {
         var14.setDisplayName("§c§lClose & Save");
         var14.setLore(Collections.singletonList("§7Save and close this menu"));
         var13.setItemMeta(var14);
      }

      var1.setItem(49, var13);
   }

   private ItemStack a(String var1, String var2, boolean var3) {
      ItemStack var4 = new ItemStack(var3 ? Material.LIME_DYE : Material.RED_DYE);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName("§e" + var1 + ": " + this.s(var3));
         var5.setLore(Arrays.asList("§7" + var2, " ", "§8Click to toggle."));
         var4.setItemMeta(var5);
      }

      return var4;
   }

   private ItemStack t(boolean var1) {
      ItemStack var2 = new ItemStack(Material.TOTEM_OF_UNDYING);
      ItemMeta var3 = var2.getItemMeta();
      if (var3 != null) {
         var3.setDisplayName("§6Resistance: " + this.s(var1));
         var3.setLore(Arrays.asList("§7Bot cannot die.", " ", "§8Click to toggle."));
         var3.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
         var2.setItemMeta(var3);
      }

      return var2;
   }

   private ItemStack c(boolean var1, boolean var2) {
      ItemStack var3 = new ItemStack(var1 ? Material.SHIELD : Material.RED_DYE);
      ItemMeta var4 = var3.getItemMeta();
      if (var4 != null) {
         String var5 = var1 ? (var2 ? "§6MAIN HAND" : "§aON") : "§cOFF";
         var4.setDisplayName("§eHold Shield: " + var5);
         var4.setLore(
            Arrays.asList(
               "§7Bot holds a shield.",
               " ",
               "§8Left-Click: Toggle on/off",
               "§8Right-Click: Switch hand",
               " ",
               "§7Current: " + (var2 ? "§6Main Hand" : "§7Off Hand")
            )
         );
         var4.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
         var3.setItemMeta(var4);
      }

      return var3;
   }

   private ItemStack c(Material var1, String var2, List<String> var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName(var2);
         if (var3 != null) {
            var5.setLore(var3);
         }

         var5.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
         var4.setItemMeta(var5);
      }

      return var4;
   }

   private ItemStack b(String var1, String var2, boolean var3) {
      ItemStack var4 = new ItemStack(var3 ? Material.LIME_DYE : Material.RED_DYE);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName("§e" + var1 + ": " + this.s(var3));
         var5.setLore(Arrays.asList("§7" + var2, " ", "§eClick to toggle"));
         var5.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
         var4.setItemMeta(var5);
      }

      return var4;
   }

   private ItemStack a(Material var1, String var2, String var3, List<String> var4) {
      ItemStack var5 = new ItemStack(var1);
      ItemMeta var6 = var5.getItemMeta();
      if (var6 != null) {
         var6.setDisplayName(var2 + ": " + var3);
         ArrayList var7 = new ArrayList();
         var7.add(" ");
         var7.addAll(var4);
         var7.add(" ");
         var7.add("§eClick to cycle");
         var6.setLore(var7);
         var6.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
         var5.setItemMeta(var6);
      }

      return var5;
   }

   private ItemStack a(ItemStack var1, String var2, TrimPattern var3, TrimMaterial var4) {
      ItemStack var5 = var1.clone();
      ItemMeta var6 = var5.getItemMeta();
      if (var6 != null) {
         var6.setDisplayName(var2);
         ArrayList var7 = new ArrayList();
         var7.add("§7Protection IV");
         if (var3 != null && var4 != null) {
            String var8 = this.pY.getBotManager().H().a(var3);
            String var9 = this.pY.getBotManager().H().a(var4);
            var7.add("§7Trim: §f" + this.f0(var8 != null ? var8 : "unknown") + " §7+ §f" + this.f0(var9 != null ? var9 : "unknown"));
         } else {
            var7.add("§7Trim: §8None");
         }

         var7.add(" ");
         var7.add("§e§lShift+Right-Click §7to reset trim");
         var6.setLore(var7);
         var6.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ARMOR_TRIM});
         var5.setItemMeta(var6);
      }

      return var5;
   }

   public ItemStack a(String var1, Object var2) {
      if (var1.equals("pattern")) {
         if (var2 != null) {
            TrimPattern var10 = (TrimPattern)var2;
            Material var13 = this.b(var10);
            ItemStack var14 = new ItemStack(var13);
            ItemMeta var15 = var14.getItemMeta();
            if (var15 != null) {
               String var16 = this.pY.getBotManager().H().a(var10);
               var15.setDisplayName("§e§lTrim Pattern");
               var15.setLore(Arrays.asList("§7Current: §f" + this.f0(var16 != null ? var16 : "unknown"), " ", "§eClick to change", "§cRight-click to remove"));
               var14.setItemMeta(var15);
            }

            return var14;
         } else {
            ItemStack var9 = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            ItemMeta var12 = var9.getItemMeta();
            if (var12 != null) {
               var12.setDisplayName("§e§lTrim Pattern Slot");
               var12.setLore(Arrays.asList("§7Place a §fSmithing Template §7here", "§7to add armor trim pattern"));
               var9.setItemMeta(var12);
            }

            return var9;
         }
      } else if (var2 != null) {
         TrimMaterial var8 = (TrimMaterial)var2;
         Material var11 = this.b(var8);
         ItemStack var5 = new ItemStack(var11);
         ItemMeta var6 = var5.getItemMeta();
         if (var6 != null) {
            String var7 = this.pY.getBotManager().H().a(var8);
            var6.setDisplayName("§6§lTrim Material");
            var6.setLore(Arrays.asList("§7Current: §f" + this.f0(var7 != null ? var7 : "unknown"), " ", "§eClick to change", "§cRight-click to remove"));
            var5.setItemMeta(var6);
         }

         return var5;
      } else {
         ItemStack var3 = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
         ItemMeta var4 = var3.getItemMeta();
         if (var4 != null) {
            var4.setDisplayName("§6§lTrim Material Slot");
            var4.setLore(Arrays.asList("§7Place an §fIngot/Material §7here", "§7to set trim color"));
            var3.setItemMeta(var4);
         }

         return var3;
      }
   }

   public String f0(String var1) {
      return var1 != null && !var1.isEmpty() ? var1.substring(0, 1).toUpperCase() + var1.substring(1).replace("_", " ") : "Unknown";
   }

   public Material b(TrimPattern var1) {
      String var2 = this.pY.getBotManager().H().a(var1);
      if (var2 == null) {
         return Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE;
      } else {
         return switch (var2) {
            case "coast" -> Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "dune" -> Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "eye" -> Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "host" -> Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "raiser" -> Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "rib" -> Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "sentry" -> Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "shaper" -> Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "silence" -> Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "snout" -> Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "spire" -> Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "tide" -> Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "vex" -> Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "ward" -> Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "wayfinder" -> Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "wild" -> Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "bolt" -> Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "flow" -> Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE;
            default -> Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE;
         };
      }
   }

   public Material b(TrimMaterial var1) {
      String var2 = this.pY.getBotManager().H().a(var1);
      if (var2 == null) {
         return Material.IRON_INGOT;
      } else {
         return switch (var2) {
            case "amethyst" -> {
               Material var19 = Material.AMETHYST_SHARD;
               yield var19;
            }
            case "copper" -> {
               Material var18 = Material.COPPER_INGOT;
               yield var18;
            }
            case "diamond" -> {
               Material var17 = Material.DIAMOND;
               yield var17;
            }
            case "emerald" -> {
               Material var16 = Material.EMERALD;
               yield var16;
            }
            case "gold" -> {
               Material var15 = Material.GOLD_INGOT;
               yield var15;
            }
            case "iron" -> {
               Material var14 = Material.IRON_INGOT;
               yield var14;
            }
            case "lapis" -> {
               Material var13 = Material.LAPIS_LAZULI;
               yield var13;
            }
            case "netherite" -> {
               Material var12 = Material.NETHERITE_INGOT;
               yield var12;
            }
            case "quartz" -> {
               Material var11 = Material.QUARTZ;
               yield var11;
            }
            case "redstone" -> {
               Material var10 = Material.REDSTONE;
               yield var10;
            }
            case "resin" -> {
               Material var9;
               try {
                  var9 = Material.valueOf("RESIN_BRICK");
               } catch (IllegalArgumentException var7) {
                  var9 = Material.IRON_INGOT;
                  yield var9;
               }

               yield var9;
            }
            default -> {
               Material var5 = Material.IRON_INGOT;
               yield var5;
            }
         };
      }
   }

   public TrimPattern c(ItemStack var1) {
      if (var1 == null) {
         return null;
      } else {
         Material var2 = var1.getType();

         String var3 = switch (var2) {
            case COAST_ARMOR_TRIM_SMITHING_TEMPLATE -> "coast";
            case DUNE_ARMOR_TRIM_SMITHING_TEMPLATE -> "dune";
            case EYE_ARMOR_TRIM_SMITHING_TEMPLATE -> "eye";
            case HOST_ARMOR_TRIM_SMITHING_TEMPLATE -> "host";
            case RAISER_ARMOR_TRIM_SMITHING_TEMPLATE -> "raiser";
            case RIB_ARMOR_TRIM_SMITHING_TEMPLATE -> "rib";
            case SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE -> "sentry";
            case SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE -> "shaper";
            case SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE -> "silence";
            case SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE -> "snout";
            case SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE -> "spire";
            case TIDE_ARMOR_TRIM_SMITHING_TEMPLATE -> "tide";
            case VEX_ARMOR_TRIM_SMITHING_TEMPLATE -> "vex";
            case WARD_ARMOR_TRIM_SMITHING_TEMPLATE -> "ward";
            case WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE -> "wayfinder";
            case WILD_ARMOR_TRIM_SMITHING_TEMPLATE -> "wild";
            case BOLT_ARMOR_TRIM_SMITHING_TEMPLATE -> "bolt";
            case FLOW_ARMOR_TRIM_SMITHING_TEMPLATE -> "flow";
            default -> null;
         };
         return var3 == null ? null : (TrimPattern)Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(var3));
      }
   }

   public TrimMaterial d(ItemStack var1) {
      if (var1 == null) {
         return null;
      } else {
         Material var2 = var1.getType();

         String var3 = switch (var2) {
            case AMETHYST_SHARD -> "amethyst";
            case COPPER_INGOT -> "copper";
            case DIAMOND -> "diamond";
            case EMERALD -> "emerald";
            case GOLD_INGOT -> "gold";
            case IRON_INGOT -> "iron";
            case LAPIS_LAZULI -> "lapis";
            case NETHERITE_INGOT -> "netherite";
            case QUARTZ -> "quartz";
            case REDSTONE -> "redstone";
            default -> var2.name().equals("RESIN_BRICK") ? "resin" : null;
         };
         return var3 == null ? null : (TrimMaterial)Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(var3));
      }
   }

   public int fs() {
      return 11;
   }

   public int ft() {
      return 20;
   }

   public int fu() {
      return 29;
   }

   public int fv() {
      return 38;
   }

   public int fw() {
      return 15;
   }

   public int fx() {
      return 24;
   }

   public int fy() {
      return 33;
   }

   public int fz() {
      return 42;
   }

   public int fA() {
      return 13;
   }

   public int fB() {
      return 22;
   }

   public int fC() {
      return 31;
   }

   public int fD() {
      return 40;
   }

   public int fE() {
      return 49;
   }

   public int fF() {
      return 50;
   }

   public int fG() {
      return 48;
   }

   public int fH() {
      return 24;
   }

   public int fI() {
      return 31;
   }

   public int fJ() {
      return 33;
   }

   public int fK() {
      return 20;
   }

   public int fL() {
      return 29;
   }
}
