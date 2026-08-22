package com.sheldera.practicebot.x;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import java.util.Set;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class n1 implements Listener {
   private final PracticeBotPlugin oX;
   private final s1 oY;
   private final o1 oZ;
   private static final long pa = 200L;

   public n1(PracticeBotPlugin var1) {
      this.oX = var1;
      this.oY = var1.getGuiManager();
      this.oZ = var1.getGuiConfigManager();
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onInventoryClick(InventoryClickEvent var1) {
      if (var1.getWhoClicked() instanceof Player var2) {
         Inventory var7 = var1.getInventory();
         InventoryHolder var4 = var7.getHolder();
         boolean var5 = var4 instanceof v1 || var4 instanceof u1 || var4 instanceof v0 || var4 instanceof u0 || var4 instanceof t1;
         if (var5 && !this.oX.isLicenseActive()) {
            var1.setCancelled(true);
            var2.closeInventory();
            var2.sendMessage(this.oX.licenseLockMessage());
         } else if (this.oX.isCitizensReady()) {
            if (var4 instanceof u1 || var4 instanceof u0 || var4 instanceof t1 || var4 instanceof v0) {
               Bukkit.getScheduler().runTask(this.oX, () -> {
                  try {
                     var2.clearActiveItem();
                  } catch (Exception var2x) {
                  }
               });
            }

            if (var4 instanceof v1) {
               this.a(var1, var2);
            } else if (var4 instanceof u1 var9) {
               this.a(var1, var2, var9);
            } else if (var4 instanceof v0 var8) {
               this.a(var1, var2, var8);
            } else if (var4 instanceof u0 var6) {
               this.a(var1, var2, var6);
            } else {
               if (var4 instanceof t1) {
                  this.a(var1, var2, var7);
               }
            }
         }
      }
   }

   private void a(InventoryClickEvent var1, Player var2) {
      var1.setCancelled(true);
      int var3 = var1.getRawSlot();
      if (var3 == 11) {
         if (!var2.hasPermission("practicebot.spawn.normal") && !var2.hasPermission(this.oX.getConfigManager().bm())) {
            var2.sendMessage(h.ac("no-permission"));
         } else {
            var2.closeInventory();
            this.oX.getBotManager().a(var2, a.NORMAL);
         }
      } else if (var3 == 15) {
         if (!var2.hasPermission("practicebot.spawn.cpvp") && !var2.hasPermission(this.oX.getConfigManager().bm())) {
            var2.sendMessage(h.ac("no-permission"));
         } else {
            var2.closeInventory();
            this.oX.getBotManager().a(var2, a.CPVP);
         }
      } else if (var3 == 22) {
         var2.closeInventory();
      }
   }

   private void a(InventoryClickEvent var1, Player var2, u1 var3) {
      var1.setCancelled(true);
      NPC var4 = CitizensAPI.getNPCRegistry().getById(var3.bL());
      if (var4 == null) {
         var2.closeInventory();
      } else {
         BotTrait var5 = (BotTrait)var4.getTraitNullable(BotTrait.class);
         if (var5 == null) {
            var2.closeInventory();
         } else {
            int var6 = var1.getRawSlot();
            if (var6 >= 0 && var6 < var1.getInventory().getSize()) {
               o0 var7 = this.oZ.eI();
               String var8 = null;
               if (var7 != null) {
                  s0 var9 = var7.n(var6);
                  if (var9 != null) {
                     var8 = var9.fe();
                  }
               }

               if (var8 != null) {
                  boolean var12 = false;
                  switch (var8) {
                     case "close":
                        var2.closeInventory();
                        return;
                     case "despawn":
                        this.oX.getBotManager().a(var4, n.MANUAL);
                        var2.closeInventory();
                        var2.playSound(var2.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.3F, 1.5F);
                        var2.sendMessage(h.ac("bot-despawned"));
                        return;
                     case "bot_inventory":
                        this.oY.c(var2, var4, var5, true);
                        return;
                     case "pvp_settings":
                        this.oY.b(var2, var4, var5, true);
                        return;
                     case "look":
                        var5.setLookAtOwner(!var5.isLookAtOwner());
                        if (var5.isLookAtOwner()) {
                           var5.setFollowOwner(false);
                           var5.setRandomWalk(false);
                        }

                        var12 = true;
                        break;
                     case "follow":
                        var5.setFollowOwner(!var5.isFollowOwner());
                        if (var5.isFollowOwner()) {
                           var5.setLookAtOwner(false);
                           var5.setRandomWalk(false);
                           var5.setPvpEnabled(false);
                        }

                        var12 = true;
                        break;
                     case "random":
                        var5.setRandomWalk(!var5.isRandomWalk());
                        if (var5.isRandomWalk()) {
                           var5.setLookAtOwner(false);
                           var5.setFollowOwner(false);
                           var5.setPvpEnabled(false);
                        }

                        var12 = true;
                        break;
                     case "hold_shield":
                        if (var1.isRightClick()) {
                           if (var5.isHoldShield()) {
                              var5.setShieldInMainHand(!var5.isShieldInMainHand());
                           }
                        } else {
                           var5.setHoldShield(!var5.isHoldShield());
                           if (!var5.isHoldShield()) {
                              var5.setUseShield(false);
                              var5.setShieldInMainHand(false);
                           }
                        }

                        var12 = true;
                        break;
                     case "use_shield":
                        var5.setUseShield(!var5.isUseShield());
                        if (var5.isUseShield()) {
                           var5.setHoldShield(true);
                        }

                        var12 = true;
                        break;
                     case "resistance":
                        var5.setResistance(!var5.isResistance());
                        var12 = true;
                     case "info":
                  }

                  if (var12) {
                     this.oX.getBotManager().H().a(var4, var5);
                     this.oX.getBotManager().b(var4, var5);
                     this.oY.a(var2, var4, var5, false);
                     var2.playSound(var2.getLocation(), Sound.UI_BUTTON_CLICK, 0.4F, 1.2F);
                  }
               }
            }
         }
      }
   }

   private void a(InventoryClickEvent var1, Player var2, v0 var3) {
      var1.setCancelled(true);
      NPC var4 = CitizensAPI.getNPCRegistry().getById(var3.bL());
      if (var4 == null) {
         var2.closeInventory();
      } else {
         BotTrait var5 = (BotTrait)var4.getTraitNullable(BotTrait.class);
         if (var5 == null) {
            var2.closeInventory();
         } else {
            int var6 = var1.getRawSlot();
            if (var6 >= 0 && var6 < var1.getInventory().getSize()) {
               o0 var7 = this.oZ.eJ();
               String var8 = null;
               if (var7 != null) {
                  s0 var9 = var7.n(var6);
                  if (var9 != null) {
                     var8 = var9.fe();
                  }
               }

               if (var8 != null) {
                  boolean var12 = false;
                  switch (var8) {
                     case "back":
                        this.oY.a(var2, var4, var5, true);
                        return;
                     case "pvp_enabled":
                        var5.setPvpEnabled(!var5.isPvpEnabled());
                        if (var5.isPvpEnabled()) {
                           var5.setLookAtOwner(false);
                           var5.setFollowOwner(false);
                           var5.setRandomWalk(false);
                        }

                        var12 = true;
                        break;
                     case "strafe":
                        var5.setPvpStrafe(!var5.isPvpStrafe());
                        var12 = true;
                        break;
                     case "wtap":
                        var5.setPvpWTap(!var5.isPvpWTap());
                        var12 = true;
                        break;
                     case "stap":
                        var5.setPvpSTap(!var5.isPvpSTap());
                        var5.sTapActive = false;
                        var5.sTapDirection = null;
                        var12 = true;
                        break;
                     case "crits":
                        var5.setPvpCrits(!var5.isPvpCrits());
                        var12 = true;
                        break;
                     case "shield_breaker":
                        var5.setPvpShieldBreaker(!var5.isPvpShieldBreaker());
                        var12 = true;
                        break;
                     case "retreat":
                        var5.setPvpRetreat(!var5.isPvpRetreat());
                        var12 = true;
                        break;
                     case "crystal_pvp":
                        this.oX.getCrystalPvpGui().a(var2, var4, var5);
                        return;
                     case "reach":
                        var5.setPvpReachMode((var5.getPvpReachMode() + 1) % 4);
                        var12 = true;
                        break;
                     case "crit_chance":
                        var5.setPvpCritChance((var5.getPvpCritChance() + 1) % 4);
                        var12 = true;
                        break;
                     case "crit_speed":
                        var5.setPvpCritSpeed((var5.getPvpCritSpeed() + 1) % 3);
                        var12 = true;
                        break;
                     case "aggro":
                        var5.setPvpAggression((var5.getPvpAggression() + 1) % 3);
                        var12 = true;
                     case "info":
                        break;
                     default:
                        if (var8.startsWith("border_")) {
                        }
                  }

                  if (var12) {
                     this.oX.getBotManager().b(var4, var5);
                     this.oY.b(var2, var4, var5, false);
                     var2.playSound(var2.getLocation(), Sound.UI_BUTTON_CLICK, 0.4F, 1.2F);
                  }
               }
            }
         }
      }
   }

   private void a(InventoryClickEvent var1, Player var2, u0 var3) {
      int var4 = var1.getRawSlot();
      Inventory var5 = var1.getInventory();
      if (var4 >= var5.getSize()) {
         if (var1.isShiftClick()) {
            var1.setCancelled(true);
         }
      } else {
         var1.setCancelled(true);
         long var6 = System.currentTimeMillis();
         Long var8 = this.oX.getInventoryClickCooldown().get(var2.getUniqueId());
         if (var8 == null || var6 - var8 >= 200L) {
            this.oX.getInventoryClickCooldown().put(var2.getUniqueId(), var6);
            NPC var9 = CitizensAPI.getNPCRegistry().getById(var3.bL());
            if (var9 == null) {
               var2.closeInventory();
            } else {
               BotTrait var10 = (BotTrait)var9.getTraitNullable(BotTrait.class);
               if (var10 == null) {
                  var2.closeInventory();
               } else if (var4 >= 0) {
                  o0 var11 = this.oZ.eK();
                  String var12 = null;
                  if (var11 != null) {
                     s0 var13 = var11.n(var4);
                     if (var13 != null) {
                        var12 = var13.fe();
                     }
                  }

                  if (var12 == null) {
                     var12 = switch (var4) {
                        case 13 -> "helmet";
                        default -> null;
                        case 20 -> "mainhand";
                        case 22 -> "chestplate";
                        case 24 -> "armor_toggle";
                        case 29 -> "offhand";
                        case 31 -> "leggings";
                        case 33 -> "leggings_material";
                        case 40 -> "boots";
                        case 48 -> "back";
                        case 50 -> "close";
                     };
                  }

                  if (var12 != null) {
                     boolean var17 = false;
                     switch (var12) {
                        case "close":
                           ai var20 = this.oX.getCrystalPvpGui();
                           if (var20 != null) {
                              var20.c(var2.getUniqueId());
                           }

                           var2.closeInventory();
                           return;
                        case "back":
                           ai var19 = this.oX.getCrystalPvpGui();
                           if (var19 != null) {
                              var19.c(var2.getUniqueId());
                           }

                           if (var10.getBotType() == a.CPVP) {
                              this.oX.getCrystalPvpGui().a(var2, var9, var10);
                           } else {
                              this.oY.a(var2, var9, var10, true);
                           }

                           return;
                        case "armor_toggle":
                           var10.setArmorType(var10.getArmorType().equals("netherite") ? "diamond" : "netherite");
                           var17 = true;
                           break;
                        case "helmet":
                           var10.setHelmetEnchant(var10.getHelmetEnchant().equals("protection") ? "blast_protection" : "protection");
                           var17 = true;
                           break;
                        case "chestplate":
                           var10.setChestplateEnchant(var10.getChestplateEnchant().equals("protection") ? "blast_protection" : "protection");
                           var17 = true;
                           break;
                        case "leggings":
                           var10.setLeggingsEnchant(var10.getLeggingsEnchant().equals("protection") ? "blast_protection" : "protection");
                           var17 = true;
                           break;
                        case "boots":
                           var10.setBootsEnchant(var10.getBootsEnchant().equals("protection") ? "blast_protection" : "protection");
                           var17 = true;
                           break;
                        case "leggings_material":
                           var10.setLeggingsMaterial(var10.getLeggingsMaterial().equals("netherite") ? "diamond" : "netherite");
                           var17 = true;
                           break;
                        case "mainhand":
                           ItemStack var16 = var1.getCursor();
                           if (var16 != null && !var16.getType().isAir() && this.oX.getBotManager().H().a(var16.getType())) {
                              var10.setCustomMainHand(var16.clone());
                              this.oX.getBotManager().H().a(var9, var10);
                              var5.setItem(var4, this.oY.f(var10));
                              var2.playSound(var2.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 2.0F);
                           }
                           break;
                        case "offhand":
                           var10.setTotemCount(this.oY.w(var10.getTotemCount()));
                           var17 = true;
                        case "info":
                     }

                     if (var17) {
                        if (var10.isCpvpEnabled() && var9.isSpawned() && var9.getEntity() instanceof Player var14) {
                           this.oX.getCrystalPvpModule().c(var14, var10);
                        } else {
                           this.oX.getBotManager().H().a(var9, var10);
                        }

                        this.oY.c(var2, var9, var10, false);
                        var2.playSound(var2.getLocation(), Sound.UI_BUTTON_CLICK, 0.4F, 1.2F);
                     }
                  }
               }
            }
         }
      }
   }

   private void a(InventoryClickEvent var1, Player var2, Inventory var3) {
      int var4 = var1.getRawSlot();
      if (var4 < 54 && var4 >= 0) {
         ItemStack var5 = var1.getCursor();
         FileConfiguration var6 = this.oX.getDefaultInvConfig();
         Set var7 = Set.of(this.oY.fs(), this.oY.ft(), this.oY.fu(), this.oY.fv());
         Set var8 = Set.of(this.oY.fw(), this.oY.fx(), this.oY.fy(), this.oY.fz());
         if (var7.contains(var4)) {
            var1.setCancelled(true);
            String var13 = this.j(var4);
            if (var1.isRightClick()) {
               if (var13 != null) {
                  var6.set(var13, null);
                  this.oX.saveDefaultInventoryConfig();
                  this.oY.a(var3);
                  var2.playSound(var2.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 1.0F);
                  var2.sendMessage("§a§l[PracticeBot] §7Trim pattern removed!");
               }
            } else if (var5 != null && !var5.getType().isAir()) {
               TrimPattern var15 = this.oY.c(var5);
               if (var15 != null && var13 != null) {
                  String var17 = this.oX.getBotManager().H().a(var15);
                  if (var17 != null) {
                     var6.set(var13, var17);
                     this.oX.saveDefaultInventoryConfig();
                     this.oY.a(var3);
                     var2.playSound(var2.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 2.0F);
                     var2.sendMessage("§a§l[PracticeBot] §7Trim pattern set to: §f" + this.oY.f0(var17));
                  }
               } else {
                  var2.sendMessage("§c§l[PracticeBot] §7That item is not a valid Smithing Template!");
               }
            }
         } else if (!var8.contains(var4)) {
            Set var12 = Set.of(this.oY.fA(), this.oY.fB(), this.oY.fC(), this.oY.fD());
            if (var12.contains(var4) && var1.isShiftClick() && var1.isRightClick()) {
               var1.setCancelled(true);
               String var14 = this.l(var4);
               String var16 = this.m(var4);
               if (var14 != null && var16 != null) {
                  var6.set(var14, null);
                  var6.set(var16, null);
                  this.oX.saveDefaultInventoryConfig();
                  this.oY.a(var3);
                  var2.playSound(var2.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 1.0F);
                  var2.sendMessage("§a§l[PracticeBot] §7Armor trim reset!");
               }
            } else if (var4 == this.oY.fE()) {
               this.oX.saveDefaultInventoryConfig();
               var2.closeInventory();
               var2.sendMessage(h.ac("save-default-inventory"));
            } else {
               var1.setCancelled(true);
            }
         } else {
            var1.setCancelled(true);
            String var9 = this.k(var4);
            if (var1.isRightClick()) {
               if (var9 != null) {
                  var6.set(var9, null);
                  this.oX.saveDefaultInventoryConfig();
                  this.oY.a(var3);
                  var2.playSound(var2.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 1.0F);
                  var2.sendMessage("§a§l[PracticeBot] §7Trim material removed!");
               }
            } else if (var5 != null && !var5.getType().isAir()) {
               TrimMaterial var10 = this.oY.d(var5);
               if (var10 != null && var9 != null) {
                  String var11 = this.oX.getBotManager().H().a(var10);
                  if (var11 != null) {
                     var6.set(var9, var11);
                     this.oX.saveDefaultInventoryConfig();
                     this.oY.a(var3);
                     var2.playSound(var2.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 2.0F);
                     var2.sendMessage("§a§l[PracticeBot] §7Trim material set to: §f" + this.oY.f0(var11));
                  }
               } else {
                  var2.sendMessage("§c§l[PracticeBot] §7That item is not a valid trim material!");
               }
            }
         }
      }
   }

   private String j(int var1) {
      if (var1 == this.oY.fs()) {
         return "helmet-trim-pattern";
      } else if (var1 == this.oY.ft()) {
         return "chest-trim-pattern";
      } else if (var1 == this.oY.fu()) {
         return "legs-trim-pattern";
      } else {
         return var1 == this.oY.fv() ? "boots-trim-pattern" : null;
      }
   }

   private String k(int var1) {
      if (var1 == this.oY.fw()) {
         return "helmet-trim-material";
      } else if (var1 == this.oY.fx()) {
         return "chest-trim-material";
      } else if (var1 == this.oY.fy()) {
         return "legs-trim-material";
      } else {
         return var1 == this.oY.fz() ? "boots-trim-material" : null;
      }
   }

   private String l(int var1) {
      if (var1 == this.oY.fA()) {
         return "helmet-trim-pattern";
      } else if (var1 == this.oY.fB()) {
         return "chest-trim-pattern";
      } else if (var1 == this.oY.fC()) {
         return "legs-trim-pattern";
      } else {
         return var1 == this.oY.fD() ? "boots-trim-pattern" : null;
      }
   }

   private String m(int var1) {
      if (var1 == this.oY.fA()) {
         return "helmet-trim-material";
      } else if (var1 == this.oY.fB()) {
         return "chest-trim-material";
      } else if (var1 == this.oY.fC()) {
         return "legs-trim-material";
      } else {
         return var1 == this.oY.fD() ? "boots-trim-material" : null;
      }
   }
}
