package com.sheldera.practicebot.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class p0 implements Listener {
   private final JavaPlugin pA;
   private final o1 pB;
   private final Map<UUID, r0> pC = new ConcurrentHashMap<>();
   private final Map<UUID, ItemStack[]> pD = new ConcurrentHashMap<>();

   public p0(JavaPlugin var1, o1 var2) {
      this.pA = var1;
      this.pB = var2;
   }

   public void bb(Player var1) {
      this.bc(var1);
      o0 var2 = this.pB.eM();
      int var3 = 45;
      String var4 = "§d§lGUI Editor";
      if (var2 != null) {
         var3 = var2.ez();
         var4 = var2.ey();
      }

      Inventory var5 = Bukkit.createInventory(new q1(), var3, var4);
      ItemStack var6 = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
      ItemMeta var7 = var6.getItemMeta();
      if (var7 != null) {
         var7.setDisplayName(" ");
         var6.setItemMeta(var7);
      }

      for (int var8 = 0; var8 < var3; var8++) {
         var5.setItem(var8, var6);
      }

      if (var2 != null) {
         for (s0 var9 : var2.eD().values()) {
            int var10 = var9.ff();
            if (var10 >= 0 && var10 < var3) {
               HashMap var11 = new HashMap();
               var11.put("{gui_name}", this.pB.bs(var9.fe()));
               var11.put("{gui_desc}", "Click to edit");
               ItemStack var12 = var2.a(var9, var11);
               var5.setItem(var10, var12);
            }
         }
      } else {
         String[] var14 = new String[]{"settings_menu", "pvp_settings", "cpvp_settings", "bot_inventory", "admin_inventory"};
         int[] var16 = new int[]{19, 20, 21, 24, 25};
         Material[] var17 = new Material[]{Material.CRAFTING_TABLE, Material.DIAMOND_SWORD, Material.END_CRYSTAL, Material.CHEST, Material.ENDER_CHEST};

         for (int var18 = 0; var18 < var14.length; var18++) {
            ItemStack var20 = this.b(var17[var18], "§e" + this.pB.bs(var14[var18]), Arrays.asList("§7" + this.pB.bt(var14[var18]), "", "§eClick to edit"));
            this.a(var20, "action", "edit:" + var14[var18]);
            var5.setItem(var16[var18], var20);
         }

         ItemStack var19 = this.b(Material.LIME_DYE, "§a§lReload All GUIs", Arrays.asList("§7Reload all GUI configurations", "", "§eClick to reload"));
         this.a(var19, "action", "reload");
         var5.setItem(39, var19);
         ItemStack var21 = this.b(Material.BARRIER, "§c§lClose", Collections.singletonList("§7Close the GUI Editor"));
         this.a(var21, "action", "close");
         var5.setItem(41, var21);
      }

      r0 var15 = new r0(var1.getUniqueId());
      var15.a(r1.SELECTING_GUI);
      this.pC.put(var1.getUniqueId(), var15);
      var1.openInventory(var5);
      var1.playSound(var1.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 1.5F);
   }

   public void a(Player var1, String var2) {
      this.e(var1, var2, true);
   }

   public void e(Player var1, String var2, boolean var3) {
      o0 var4 = this.pB.bp(var2);
      if (var4 != null) {
         if (var3) {
            var4.C();
         }

         int var5 = var4.ez();
         Inventory var6 = Bukkit.createInventory(new p1(var2), var5, "§c§lEDITING: " + this.pB.bs(var2));

         for (int var7 = 0; var7 < var5; var7++) {
            var6.setItem(var7, this.q(var7));
         }

         for (s0 var8 : var4.eD().values()) {
            int var9 = var8.ff();
            if (var9 >= 0 && var9 < var5) {
               ItemStack var10 = var4.a(var8, this.a(var1, var2, var8));
               this.b(var10);
               this.c(var10, var8.fe(), var9);
               var6.setItem(var9, var10);
            }
         }

         this.b(var1, var2);
         r0 var12 = this.pC.computeIfAbsent(var1.getUniqueId(), var1x -> new r0(var1.getUniqueId()));
         var12.a(r1.EDITING_GUI);
         var12.bx(var2);
         var12.fc();
         var1.openInventory(var6);
      }
   }

   private void b(Player var1, String var2) {
      if (!this.pD.containsKey(var1.getUniqueId())) {
         ItemStack[] var3 = new ItemStack[9];

         for (int var4 = 0; var4 < 9; var4++) {
            ItemStack var5 = var1.getInventory().getItem(var4);
            var3[var4] = var5 != null ? var5.clone() : null;
         }

         this.pD.put(var1.getUniqueId(), var3);
      }

      for (int var8 = 0; var8 < 9; var8++) {
         var1.getInventory().setItem(var8, null);
      }

      ItemStack var9 = this.b(Material.ARROW, "§c§l« Back", Arrays.asList("§7Return to GUI selection", "", "§eClick to go back"));
      this.a(var9, "action", "back");
      var1.getInventory().setItem(0, var9);
      ItemStack var10 = this.b(Material.LIME_CONCRETE, "§a§lSave Changes", Arrays.asList("§7Save all changes to config", "", "§eClick to save"));
      this.a(var10, "action", "save:" + var2);
      var1.getInventory().setItem(4, var10);
      ItemStack var11 = this.b(Material.TNT, "§c§lReset to Default", Arrays.asList("§7Reset this GUI to defaults", "", "§eClick to reset"));
      this.a(var11, "action", "reset:" + var2);
      var1.getInventory().setItem(8, var11);
      ItemStack var6 = this.b(Material.BARRIER, "§c§lCancel Move", Arrays.asList("§7Cancel current move", "", "§eClick to cancel"));
      this.a(var6, "action", "cancel_move");
      var1.getInventory().setItem(2, var6);
      ItemStack var7 = this.b(
         Material.BOOK,
         "§b§lHelp",
         Arrays.asList(
            "§eHow to use:",
            "§7• Click an item to select it",
            "§7• Click another slot to move/swap",
            "§7• Item material/name/lore are config-only",
            "",
            "§eHotbar:",
            "§7[1] Back  [3] Cancel",
            "§7[5] Save  [9] Reset"
         )
      );
      var1.getInventory().setItem(6, var7);
   }

   private Map<String, String> a(Player var1, String var2, s0 var3) {
      HashMap var4 = new HashMap();
      String var5 = var1 != null ? var1.getName() : "Player";
      String var6 = var3 != null ? var3.fe() : "item";
      var4.put("{player_name}", var5);
      var4.put("{player}", var5);
      var4.put("{gui}", var2 != null ? this.pB.bs(var2) : "GUI");
      var4.put("{gui_name}", var2 != null ? this.pB.bs(var2) : "GUI");
      var4.put("{gui_desc}", var2 != null ? this.pB.bt(var2) : "Layout preview");
      var4.put("{key}", var6);
      var4.put("{slot}", var3 != null ? String.valueOf(var3.ff()) : "0");
      var4.put("{material}", var3 != null ? this.bw(var3.fg().name()) : "Item");
      var4.put("{amount}", var3 != null ? String.valueOf(var3.fj()) : "1");
      String var7 = "§aON";
      String var8 = "§cOFF";
      var4.put("{status}", var7);
      var4.put("{value}", "§fCurrent");
      var4.put("{state}", var7);
      var4.put("{look_status}", var7);
      var4.put("{pvp_status}", var7);
      var4.put("{follow_status}", var7);
      var4.put("{random_status}", var8);
      var4.put("{shield_status}", var7);
      var4.put("{use_shield_status}", var7);
      var4.put("{resistance_status}", var8);
      var4.put("{strafe_status}", var7);
      var4.put("{wtap_status}", var7);
      var4.put("{stap_status}", var8);
      var4.put("{crit_status}", var7);
      var4.put("{shield_breaker_status}", var7);
      var4.put("{retreat_status}", var8);
      var4.put("{shield_hand}", "§7Off Hand");
      var4.put("{armor_type}", "§5Netherite");
      var4.put("{helmet_material}", "§5Netherite");
      var4.put("{chestplate_material}", "§5Netherite");
      var4.put("{leggings_material}", "§5Netherite");
      var4.put("{boots_material}", "§5Netherite");
      var4.put("{helmet_enchant}", "§fProtection");
      var4.put("{chestplate_enchant}", "§fProtection");
      var4.put("{leggings_enchant}", "§fBlast Protection");
      var4.put("{boots_enchant}", "§fProtection");
      var4.put("{mainhand_type}", "Netherite Sword");
      var4.put("{mainhand_item}", "Netherite Sword");
      var4.put("{mainhand_name}", "§fNetherite Sword");
      var4.put("{totem_count}", "64");
      var4.put("{offhand_count}", "64");
      var4.put("{offhand_type}", "Totem of Undying");
      var4.put("{offhand_amount}", "64");
      var4.put("{skill_level}", "PRO");
      var4.put("{skill_color}", "§c");
      var4.put("{place_delay}", "3-14ms");
      var4.put("{break_delay}", "2-18ms");
      var4.put("{realism_profile}", "§7Clean / competitive");
      var4.put("{fov_degrees}", "100°");
      var4.put("{reach}", "3.0");
      var4.put("{crit_chance}", "50%");
      var4.put("{crit_speed}", "Normal");
      var4.put("{aggression}", "Balanced");
      var4.put("{death_mode}", "Remove");
      var4.put("{kill_mode}", "Despawn");
      var4.put("{target_binding}", "Required");
      var4.put("{bot_type}", "NORMAL");
      var4.put("{respawn_delay}", "60s");
      var4.put("{warmup}", "0 ticks");
      var4.put("{template}", "Template");
      var4.put("{name}", "Name");
      var4.put("{display_name}", "Display Name");
      var4.put("{author}", "Author");
      var4.put("{skin}", "Default");
      var4.put("{world}", var1 != null && var1.getWorld() != null ? var1.getWorld().getName() : "world");
      var4.put("{target}", var5);
      var4.put("{bot}", "PracticeBot");
      var4.put("{count}", "0");
      var4.put("{version}", this.pA.getDescription().getVersion());
      var4.put("{citizens_status}", Bukkit.getPluginManager().isPluginEnabled("Citizens") ? var7 : var8);
      var4.put("{easy_marker}", "");
      var4.put("{medium_marker}", "");
      var4.put("{hard_marker}", "");
      var4.put("{pro_marker}", "§a✓");
      var4.put("{settings_name}", "Setting");
      var4.put("{settings_description}", "Configurable setting");
      var4.put("{placeholder}", "Value");
      var4.put("{field}", "Field");
      var4.put("{color}", "Color");
      var4.put("{bar}", "||||||||||");
      var4.put("{brackets}", "[]");
      var4.put("{last}", "Never");
      var4.put("{time}", "0s");
      var4.put("{frozen}", var8);
      var4.put("{respawns}", "0");
      var4.put("{look}", var7);
      var4.put("{follow}", var7);
      var4.put("{random}", var8);
      var4.put("{hold_shield}", var7);
      var4.put("{use_shield}", var7);
      var4.put("{resistance}", var8);
      var4.put("{pvp}", var7);
      var4.put("{strafe}", var7);
      var4.put("{wtap}", var7);
      var4.put("{stap}", var8);
      var4.put("{crits}", var7);
      var4.put("{shield_breaker}", var7);
      var4.put("{retreat}", var8);
      return var4;
   }

   private void b(ItemStack var1) {
      if (var1 != null && var1.hasItemMeta()) {
         ItemMeta var2 = var1.getItemMeta();
         if (var2 != null) {
            if (var2.hasDisplayName()) {
               var2.setDisplayName(this.bv(var2.getDisplayName()));
            }

            if (var2.hasLore()) {
               List var3 = var2.getLore();
               if (var3 != null) {
                  var3.replaceAll(varStr -> this.bv((String)varStr));
                  var2.setLore(var3);
               }
            }

            var1.setItemMeta(var2);
         }
      }
   }

   private String bv(String var1) {
      if (var1 != null && var1.indexOf(123) >= 0) {
         String var2 = var1;
         int var3 = 0;

         while (var3++ < 32) {
            int var4 = var2.indexOf(123);
            if (var4 < 0) {
               break;
            }

            int var5 = var2.indexOf(125, var4 + 1);
            if (var5 <= var4) {
               break;
            }

            String var6 = var2.substring(var4, var5 + 1);
            String var7 = var2.substring(var4 + 1, var5);
            if (var7.isEmpty() || !var7.matches("[A-Za-z0-9_-]+")) {
               break;
            }

            var2 = var2.replace(var6, "§f" + this.bw(var7));
         }

         return var2;
      } else {
         return var1;
      }
   }

   private String bw(String var1) {
      if (var1 != null && !var1.isBlank()) {
         String[] var2 = var1.toLowerCase(Locale.ROOT).replace('-', '_').split("_");
         StringBuilder var3 = new StringBuilder();

         for (String var7 : var2) {
            if (!var7.isEmpty()) {
               if (!var3.isEmpty()) {
                  var3.append(' ');
               }

               var3.append(Character.toUpperCase(var7.charAt(0))).append(var7.substring(1));
            }
         }

         return var3.isEmpty() ? "Value" : var3.toString();
      } else {
         return "Value";
      }
   }

   private void bc(Player var1) {
      ItemStack[] var2 = this.pD.remove(var1.getUniqueId());
      if (var2 != null) {
         for (int var3 = 0; var3 < 9; var3++) {
            var1.getInventory().setItem(var3, var2[var3]);
         }
      }
   }

   public void a(Player var1, String var2, String var3) {
      var1.sendMessage("§c[PracticeBot] GUI item editing was removed. Edit item material/name/lore in gui/*.yml.");
      Bukkit.getScheduler().runTask(this.pA, () -> this.e(var1, var2, true));
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onInventoryClick(InventoryClickEvent var1) {
      if (var1.getWhoClicked() instanceof Player var2) {
         Inventory var6 = var1.getInventory();
         InventoryHolder var4 = var6.getHolder();
         if (var4 instanceof q1) {
            var1.setCancelled(true);
            this.a(var2, var1);
         } else if (var4 instanceof p1 var7) {
            var1.setCancelled(true);
            this.a(var2, var1, var7, var6);
         } else if (var4 instanceof q0 var5) {
            var1.setCancelled(true);
            this.a(var2, var1, var5);
         }
      }
   }

   private void a(Player var1, InventoryClickEvent var2) {
      ItemStack var3 = var2.getCurrentItem();
      if (var3 != null && var3.getType() != Material.AIR) {
         String var4 = this.a(var3, "action");
         if (var4 == null) {
            o0 var5 = this.pB.eM();
            if (var5 != null) {
               s0 var6 = var5.n(var2.getRawSlot());
               if (var6 != null) {
                  var4 = var6.fl();
               }
            }
         }

         if (var4 != null) {
            if (var4.startsWith("edit:")) {
               String var7 = var4.substring(5);
               this.e(var1, var7, true);
            } else if (var4.equals("reload")) {
               this.pB.eH();
               var1.playSound(var1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.2F);
               this.bb(var1);
            } else if (var4.equals("close")) {
               var1.closeInventory();
               this.bc(var1);
               this.pC.remove(var1.getUniqueId());
            }
         }
      }
   }

   private void a(Player var1, InventoryClickEvent var2, p1 var3, Inventory var4) {
      int var5 = var2.getRawSlot();
      String var6 = var3.ex();
      o0 var7 = this.pB.bp(var6);
      if (var7 != null) {
         r0 var8 = this.pC.get(var1.getUniqueId());
         if (var8 == null) {
            var8 = new r0(var1.getUniqueId());
            this.pC.put(var1.getUniqueId(), var8);
         }

         int var9 = var4.getSize();
         if (var5 >= var9) {
            ItemStack var10 = var2.getCurrentItem();
            if (var10 != null) {
               String var16 = this.a(var10, "action");
               if (var16 != null) {
                  this.a(var1, var16, var6, var8);
               }
            }
         } else if (var5 >= 0 && var5 < var9) {
            s0 var11 = var7.n(var5);
            boolean var12 = var11 != null;
            if (var8.eY() != r1.MOVING_ITEM) {
               if (var12 && var11 != null) {
                  var8.a(r1.MOVING_ITEM);
                  var8.by(var11.fe());
                  var8.r(var5);
                  var1.playSound(var1.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.5F, 1.5F);
               }
            } else {
               String var13 = var8.fa();
               int var14 = var8.fb();
               if (var13 != null && var14 >= 0) {
                  s0 var15 = var7.bj(var13);
                  if (var15 == null) {
                     var8.fc();
                     var8.a(r1.EDITING_GUI);
                  } else if (var5 == var14) {
                     var8.fc();
                     var8.a(r1.EDITING_GUI);
                     var1.playSound(var1.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
                     this.e(var1, var6, false);
                  } else {
                     if (var12 && var11 != null) {
                        var11.s(var14);
                        var15.s(var5);
                     } else {
                        var15.s(var5);
                     }

                     var8.fc();
                     var8.a(r1.EDITING_GUI);
                     var1.playSound(var1.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 1.8F);
                     this.e(var1, var6, false);
                  }
               } else {
                  var8.fc();
                  var8.a(r1.EDITING_GUI);
               }
            }
         }
      }
   }

   private void a(Player var1, String var2, String var3, r0 var4) {
      if (var2.equals("back")) {
         var4.fc();
         this.bb(var1);
      } else if (var2.startsWith("save:")) {
         o0 var5 = this.pB.bp(var3);
         if (var5 != null) {
            var5.eu();
            var1.playSound(var1.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1.5F);
         }
      } else if (var2.startsWith("reset:")) {
         this.c(var1, var3);
      } else if (var2.equals("cancel_move") && var4.eY() == r1.MOVING_ITEM) {
         var4.fc();
         var4.a(r1.EDITING_GUI);
         var1.playSound(var1.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
         this.e(var1, var3, false);
      }
   }

   private void a(Player var1, InventoryClickEvent var2, q0 var3) {
      var1.sendMessage("§c[PracticeBot] GUI item editing was removed. Edit item material/name/lore in gui/*.yml.");
      this.e(var1, var3.pF, true);
   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent var1) {
      if (var1.getPlayer() instanceof Player var2) {
         InventoryHolder var4 = var1.getInventory().getHolder();
         if (var4 instanceof q1 || var4 instanceof p1 || var4 instanceof q0) {
            Bukkit.getScheduler().runTaskLater(this.pA, () -> {
               InventoryHolder var2x = var2.getOpenInventory().getTopInventory().getHolder();
               if (!(var2x instanceof q1) && !(var2x instanceof p1) && !(var2x instanceof q0)) {
                  this.bc(var2);
                  this.pC.remove(var2.getUniqueId());
               }
            }, 5L);
         }
      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent var1) {
      UUID var2 = var1.getPlayer().getUniqueId();
      this.bc(var1.getPlayer());
      this.pC.remove(var2);
   }

   private void c(Player var1, String var2) {
      this.pB.br(var2);
      var1.playSound(var1.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 1.0F);
      Bukkit.getScheduler().runTaskLater(this.pA, () -> this.e(var1, var2, true), 3L);
   }

   private ItemStack q(int var1) {
      ItemStack var2 = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
      ItemMeta var3 = var2.getItemMeta();
      if (var3 != null) {
         var3.setDisplayName("§7Slot §8[" + var1 + "]");
         var2.setItemMeta(var3);
      }

      return var2;
   }

   private ItemStack b(Material var1, String var2, List<String> var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName(var2);
         var5.setLore(var3);
         var5.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
         var4.setItemMeta(var5);
      }

      return var4;
   }

   private void c(ItemStack var1, String var2, int var3) {
      ItemMeta var4 = var1.getItemMeta();
      if (var4 != null) {
         java.util.List<String> var5 = var4.getLore();
         if (var5 == null) {
            var5 = new java.util.ArrayList<String>();
         }

         var5.add("");
         var5.add("§8§m                    ");
         var5.add("§7Key: §f" + var2);
         var5.add("§7Slot: §f" + var3);
         var5.add("");
         var5.add("§eClick §7to move/swap");
         var5.add("§8Item details are edited in config files");
         var4.setLore(var5);
         var1.setItemMeta(var4);
      }
   }

   private void a(ItemStack var1, String var2, String var3) {
      ItemMeta var4 = var1.getItemMeta();
      if (var4 != null) {
         java.util.List<String> var5 = var4.getLore();
         if (var5 == null) {
            var5 = new java.util.ArrayList<String>();
         }

         var5.add("§0" + var2 + ":" + var3);
         var4.setLore(var5);
         var1.setItemMeta(var4);
      }
   }

   private String a(ItemStack var1, String var2) {
      if (var1 == null) {
         return null;
      } else {
         ItemMeta var3 = var1.getItemMeta();
         if (var3 != null && var3.hasLore()) {
            String var4 = "§0" + var2 + ":";

            for (String var6 : var3.getLore()) {
               if (var6.startsWith(var4)) {
                  return var6.substring(var4.length());
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }

   public boolean bd(Player var1) {
      return this.pC.containsKey(var1.getUniqueId());
   }
}
