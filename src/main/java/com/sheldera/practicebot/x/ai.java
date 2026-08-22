package com.sheldera.practicebot.x;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ai implements Listener {
   private final PracticeBotPlugin aR;
   private final Map<UUID, Integer> aS = new HashMap<>();
   private final Map<UUID, Integer> aT = new HashMap<>();
   private static final long aU = 150L;
   private final Map<UUID, Long> aV = new HashMap<>();

   public ai(PracticeBotPlugin var1) {
      this.aR = var1;
   }

   public boolean a(UUID var1, int var2) {
      Integer var3 = this.aS.get(var1);
      if (var3 != null && var3 == var2) {
         return true;
      } else {
         Integer var4 = this.aT.get(var1);
         return var4 != null && var4 == var2;
      }
   }

   public boolean a(UUID var1) {
      return this.aS.containsKey(var1) || this.aT.containsKey(var1);
   }

   public boolean f(int var1) {
      return this.aS.containsValue(var1) || this.aT.containsValue(var1);
   }

   public int b(UUID var1) {
      return this.aS.getOrDefault(var1, -1);
   }

   public void b(UUID var1, int var2) {
      this.aT.put(var1, var2);
   }

   public void c(UUID var1) {
      this.aT.remove(var1);
   }

   public void a(Player var1, NPC var2, BotTrait var3) {
      o0 var4 = this.aR.getGuiConfigManager().eO();
      int var5 = 45;
      String var6 = "§d§lCrystal PvP Settings";
      Material var7 = Material.GRAY_STAINED_GLASS_PANE;
      if (var4 != null) {
         var5 = var4.ez();
         var6 = var4.ey();
         var7 = var4.eA();
      }

      Inventory var8 = Bukkit.createInventory(new aj(var2.getId()), var5, var6);
      ItemStack var9 = new ItemStack(var7);
      ItemMeta var10 = var9.getItemMeta();
      if (var10 != null) {
         var10.setDisplayName(" ");
         var9.setItemMeta(var10);
      }

      for (int var11 = 0; var11 < var5; var11++) {
         var8.setItem(var11, var9);
      }

      this.a(var8, var3, var4);
      this.aS.put(var1.getUniqueId(), var2.getId());
      var1.playSound(var1.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 0.8F, 1.2F);
      var1.openInventory(var8);
   }

   private void a(Inventory var1, BotTrait var2, o0 var3) {
      ae var4 = var2.getCpvpSettings();
      if (var3 != null) {
         for (s0 var6 : var3.eD().values()) {
            Map var7 = this.a(var2, var4, var6.fe());
            Material var8 = var6.fg();
            var8 = this.a(var6.fe(), var4, var2, var8);
            ItemStack var9 = this.a(var6, var7, var8);
            var1.setItem(var6.ff(), var9);
         }
      } else {
         this.a(var1, var2, var4);
      }
   }

   private Material a(String var1, ae var2, BotTrait var3, Material var4) {
      return switch (var1) {
         case "enabled" -> var3.isCpvpEnabled() ? Material.LIME_DYE : Material.GRAY_DYE;
         case "pearls" -> var2.aK() ? Material.ENDER_PEARL : Material.SNOWBALL;
         case "mace" -> Material.MACE;
         case "gapples" -> var2.aM() ? Material.GOLDEN_APPLE : Material.APPLE;
         case "block_breaking" -> Material.NETHERITE_PICKAXE;
         case "strafe" -> var2.aP() ? Material.RABBIT_FOOT : Material.LEATHER_BOOTS;
         case "anchoring" -> var2.aQ() ? Material.RESPAWN_ANCHOR : Material.GLOWSTONE;
         default -> var4;
      };
   }

   private Map<String, String> a(BotTrait var1, ae var2, String var3) {
      HashMap var4 = new HashMap();
      var4.put("{status}", var1.isCpvpEnabled() ? "§a§lENABLED" : "§c§lDISABLED");
      var4.put("{state}", this.a(var3, var2, var1));
      String var5 = var2.aG();
      var4.put("{skill_level}", var5);
      var4.put("{skill_color}", this.az(var5));
      var4.put("{easy_marker}", var5.equals("EASY") ? "§a► " : "§8  ");
      var4.put("{medium_marker}", var5.equals("MEDIUM") ? "§e► " : "§8  ");
      var4.put("{hard_marker}", var5.equals("HARD") ? "§6► " : "§8  ");
      var4.put("{pro_marker}", var5.equals("PRO") ? "§c► " : "§8  ");
      var4.put("{place_delay}", this.i(var2.aW(), var2.aX()));
      var4.put("{break_delay}", this.i(var2.aZ(), var2.bA()));
      var4.put("{realism_profile}", this.a(var2));
      var4.put("{fov_degrees}", String.format(Locale.US, "%.1f", var2.aU()));
      var4.put("{value}", this.a(var3, var2));
      var4.put("{bar}", this.b(var3, var2));
      return var4;
   }

   private String i(long var1, long var3) {
      long var5 = Math.max(1L, Math.min(var1, var3));
      long var7 = Math.max(1L, Math.max(var1, var3));
      return var5 == var7 ? String.valueOf(var5) : var5 + "-" + var7;
   }

   private String a(ae var1) {
      if (var1 == null) {
         return "Default";
      } else {
         return !var1.bI() ? "Clean / no misses" : var1.bB() + "% hesitate, " + var1.bE() + "% miss, " + var1.bH() + "% pearl restraint";
      }
   }

   private String az(String var1) {
      if (var1 == null) {
         return "§7";
      } else {
         String var2 = var1.toUpperCase();

         return switch (var2) {
            case "EASY" -> "§a";
            case "MEDIUM" -> "§e";
            case "HARD" -> "§6";
            case "PRO" -> "§c";
            default -> "§d";
         };
      }
   }

   private String a(String var1, ae var2, BotTrait var3) {
      return switch (var1) {
         case "enabled" -> var3.isCpvpEnabled() ? "§a§lON" : "§c§lOFF";
         case "pearls" -> var2.aK() ? "§aON" : "§cOFF";
         case "mace" -> var2.aL() ? "§aON" : "§cOFF";
         case "gapples" -> var2.aM() ? "§aON" : "§cOFF";
         case "block_breaking" -> var2.aO() ? "§aON" : "§cOFF";
         case "strafe" -> var2.aP() ? "§aON" : "§cOFF";
         case "obsidian" -> var2.aN() ? "§aON" : "§cOFF";
         case "anchoring" -> var2.aQ() ? "§aON" : "§cOFF";
         default -> "";
      };
   }

   private String a(String var1, ae var2) {
      return switch (var1) {
         case "aggression" -> String.format("%.1f", var2.aI());
         case "pearl_cooldown" -> String.format("%.1f", var2.aT() / 1000.0);
         case "heal_threshold" -> String.valueOf((int)(var2.aR() * 100.0));
         default -> "";
      };
   }

   private String b(String var1, ae var2) {
      double var3;
      double var5;
      double var7;
      String var9;
      switch (var1) {
         case "aggression":
            var3 = var2.aI();
            var5 = 0.5;
            var7 = 2.0;
            var9 = "§6";
            break;
         case "heal_threshold":
            var3 = var2.aR();
            var5 = 0.2;
            var7 = 0.8;
            var9 = "§c";
            break;
         default:
            return "";
      }

      return this.a(var3, var5, var7, var9);
   }

   private String a(double var1, double var3, double var5, String var7) {
      byte var8 = 20;
      double var9 = (var1 - var3) / (var5 - var3);
      int var11 = (int)Math.round(var9 * var8);
      var11 = Math.max(0, Math.min(var8, var11));
      StringBuilder var12 = new StringBuilder("§8[");

      for (int var13 = 0; var13 < var8; var13++) {
         if (var13 < var11) {
            var12.append(var7).append("|");
         } else {
            var12.append("§7|");
         }
      }

      var12.append("§8]");
      return var12.toString();
   }

   private ItemStack a(s0 var1, Map<String, String> var2, Material var3) {
      ItemStack var4 = new ItemStack(var3, var1.fj());
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         String var6 = var1.fh();
         ArrayList var7 = new ArrayList<>(var1.fi());

         for (Entry var9 : var2.entrySet()) {
            var6 = var6.replace((CharSequence)var9.getKey(), (CharSequence)var9.getValue());
            var7.replaceAll(var1x -> ((String)var1x).replace((CharSequence)var9.getKey(), (CharSequence)var9.getValue()));
         }

         var5.setDisplayName(var6);
         var5.setLore(var7);
         var5.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS});
         var4.setItemMeta(var5);
      }

      return var4;
   }

   private void a(Inventory var1, BotTrait var2, ae var3) {
      var1.setItem(4, this.a(Material.END_CRYSTAL, "§d§lCrystal PvP Settings", Arrays.asList("§7Configure Crystal PvP combat")));
      var1.setItem(
         10,
         this.a(
            var2.isCpvpEnabled() ? Material.LIME_DYE : Material.GRAY_DYE,
            "§eCrystal PvP: " + (var2.isCpvpEnabled() ? "§a§lON" : "§c§lOFF"),
            Arrays.asList("§eClick to toggle")
         )
      );
      var1.setItem(
         16,
         this.a(
            var3.aQ() ? Material.RESPAWN_ANCHOR : Material.GLOWSTONE,
            "§5Anchoring Mode: " + (var3.aQ() ? "§aON" : "§cOFF"),
            Arrays.asList("§7Use glowstone and respawn anchors", "§7when they beat crystals.")
         )
      );
      var1.setItem(34, this.a(Material.STRUCTURE_VOID, "§c§lReset Settings", Arrays.asList("§7Reset all settings to", "§7default values")));
      var1.setItem(40, this.a(Material.BARRIER, "§c§lClose", Arrays.asList("§7Close this menu")));
   }

   private ItemStack a(Material var1, String var2, List<String> var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName(var2);
         var5.setLore(var3);
         var5.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS});
         var4.setItemMeta(var5);
      }

      return var4;
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      if (var1.getWhoClicked() instanceof Player var2) {
         if (var1.getInventory().getHolder() instanceof aj var14) {
            var1.setCancelled(true);
            long var15 = System.currentTimeMillis();
            Long var6 = this.aV.get(var2.getUniqueId());
            if (var6 == null || var15 - var6 >= 150L) {
               this.aV.put(var2.getUniqueId(), var15);
               int var7 = var1.getRawSlot();
               if (var7 >= 0 && var7 < var1.getInventory().getSize()) {
                  NPC var8 = CitizensAPI.getNPCRegistry().getById(var14.bL());
                  if (var8 != null && var8.hasTrait(BotTrait.class)) {
                     BotTrait var9 = (BotTrait)var8.getTraitNullable(BotTrait.class);
                     if (var9 == null) {
                        var2.closeInventory();
                     } else {
                        ae var10 = var9.getCpvpSettings();
                        o0 var11 = this.aR.getGuiConfigManager().eO();
                        String var12 = null;
                        if (var7 == 38) {
                           var12 = "open_bot_inventory";
                        } else if (var7 == 42) {
                           var12 = "despawn_bot";
                        } else if (var11 == null && var7 == 16) {
                           var12 = "toggle_anchoring";
                        } else if (var11 == null && var7 == 34) {
                           var12 = "reset_settings";
                        } else if (var11 == null && var7 == 10) {
                           var12 = "toggle_enabled";
                        } else if (var11 != null) {
                           s0 var13 = var11.n(var7);
                           if (var13 != null) {
                              var12 = var13.fl();
                           }
                        }

                        if (var12 != null && !var12.equals("none")) {
                           boolean var16 = this.a(var12, var2, var8, var9, var10, var1);
                           if (var16) {
                              this.a(var1.getInventory(), var9, var11);
                              var2.playSound(var2.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.2F);
                           }
                        }
                     }
                  } else {
                     var2.closeInventory();
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent var1) {
      if (var1.getPlayer() instanceof Player var2) {
         if (var1.getInventory().getHolder() instanceof aj) {
            this.aS.remove(var2.getUniqueId());
            this.aV.remove(var2.getUniqueId());
         }

         this.aT.remove(var2.getUniqueId());
      }
   }

   private boolean a(String var1, Player var2, NPC var3, BotTrait var4, ae var5, InventoryClickEvent var6) {
      switch (var1) {
         case "close":
            var2.closeInventory();
            var2.playSound(var2.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 0.8F);
            return false;
         case "despawn_bot":
            var2.closeInventory();
            this.aS.remove(var2.getUniqueId());
            this.aT.remove(var2.getUniqueId());
            this.aR.getBotManager().a(var3, n.MANUAL);
            var2.sendMessage(h.ac("cpvp-bot-despawned"));
            var2.playSound(var2.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5F, 1.2F);
            return false;
         case "open_bot_inventory":
            var2.closeInventory();
            this.aR.getGuiManager().d(var2, var3, var4);
            return false;
         case "toggle_enabled":
            boolean var19 = !var4.isCpvpEnabled();
            var4.setCpvpEnabled(var19);
            this.aR.getBotManager().b(var3, var4);
            if (var3.isSpawned() && var3.getEntity() instanceof Player var21) {
               if (var19) {
                  this.aR.getCrystalPvpModule().c(var21, var4);
               } else {
                  this.aR.getCrystalPvpModule().d(var21, var4);
               }
            }

            return true;
         case "cycle_skill":
            var5.aH();
            return true;
         case "adjust_aggression":
            double var17 = var5.aI();
            if (var6.isShiftClick()) {
               var17 = 1.0;
            } else if (var6.isRightClick()) {
               var17 = Math.max(0.5, var17 - 0.1);
            } else {
               var17 = Math.min(2.0, var17 + 0.1);
            }

            var5.b(var17);
            return true;
         case "toggle_pearls":
            var5.a(!var5.aK());
            return true;
         case "adjust_pearl_cooldown":
            long var15 = var5.aT();
            if (var6.isShiftClick()) {
               var15 = 1800L;
            } else if (var6.isRightClick()) {
               var15 = Math.max(500L, var15 - 250L);
            } else {
               var15 = Math.min(3000L, var15 + 250L);
            }

            var5.b(var15);
            return true;
         case "toggle_mace":
            var5.b(!var5.aL());
            return true;
         case "toggle_gapples":
            var5.c(!var5.aM());
            return true;
         case "toggle_block_breaking":
            var5.e(!var5.aO());
            return true;
         case "adjust_heal_threshold":
            double var13 = var5.aR();
            if (var6.isShiftClick()) {
               var13 = 0.4;
            } else if (var6.isRightClick()) {
               var13 = Math.max(0.2, var13 - 0.1);
            } else {
               var13 = Math.min(0.8, var13 + 0.1);
            }

            var5.c(var13);
            return true;
         case "toggle_strafe":
            var5.f(!var5.aP());
            return true;
         case "toggle_obsidian":
            var5.d(!var5.aN());
            return true;
         case "toggle_anchoring":
            var5.g(!var5.aQ());
            if (var3.isSpawned() && var3.getEntity() instanceof Player var12 && var4.isCpvpEnabled()) {
               this.aR.getCrystalPvpModule().c(var12, var4);
            }

            return true;
         case "reset_settings":
            var5.bK();
            if (var3.isSpawned() && var3.getEntity() instanceof Player var9 && var4.isCpvpEnabled()) {
               this.aR.getCrystalPvpModule().c(var9, var4);
            }

            var2.playSound(var2.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8F, 0.5F);
            return true;
         default:
            return false;
      }
   }
}
