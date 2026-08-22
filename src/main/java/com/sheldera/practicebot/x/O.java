package com.sheldera.practicebot.x;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.FollowTrait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class O implements Listener {
   private static final int ta = 40;
   private static final double tb = 2.0;
   private static final double tc = 1.0E-4;
   private static final float td = 1.0F;
   private static final long te = 200L;
   private static final List<String> tf = List.of("protection", "blast_protection", "none");
   private static final List<Integer> tg = List.of(0, 1, 2, 4, 8, 16, 32, 64, 99);
   private final PracticeBotPlugin th;
   private final Map<UUID, X> ti = new HashMap<>();
   private final Map<Integer, UUID> tj = new HashMap<>();
   private final Map<UUID, Q> tk = new HashMap<>();
   private BukkitTask tl;

   public O(PracticeBotPlugin var1) {
      this.th = var1;
   }

   public void gL() {
      if (this.th.isLicenseActive()) {
         if (this.tl == null) {
            this.tl = Bukkit.getScheduler().runTaskTimer(this.th, this::gM, 1L, 1L);
         }
      }
   }

   public void m1() {
      if (this.tl != null) {
         this.tl.cancel();
         this.tl = null;
      }

      for (X var3 : new ArrayList<>(this.ti.values())) {
         this.a(var3, false, false, null);
      }

      this.ti.clear();
      this.tj.clear();
      this.tk.clear();
   }

   public boolean bd(Player var1) {
      return this.th.isLicenseActive() && var1 != null && this.ti.containsKey(var1.getUniqueId());
   }

   public boolean g(Player var1, String var2) {
      if (!this.th.isLicenseActive()) {
         if (var1 != null) {
            var1.sendMessage(this.th.licenseLockMessage());
         }

         return true;
      } else if (!this.bt(var1)) {
         return true;
      } else {
         X var3 = this.ti.get(var1.getUniqueId());
         if (var3 != null) {
            var1.sendMessage(h.ac("template-editor.session-active"));
            this.a(var1, var3, T.MAIN, true);
            return true;
         } else {
            String var4 = this.th.getBotTemplateManager().normalizeTemplateKey(var2);
            if (!this.th.getBotTemplateManager().k0(var4)) {
               var1.sendMessage(h.a("template-editor.invalid-key", "{key}", var2));
               return true;
            } else if (this.th.getBotTemplateManager().j0(var4)) {
               var1.sendMessage(h.a("template-editor.key-exists", "{key}", var4));
               return true;
            } else if (this.a(null, var4)) {
               var1.sendMessage(h.a("template-editor.key-reserved", "{key}", var4));
               return true;
            } else {
               E var5 = this.th.getBotTemplateManager().k1(var4);
               return this.a(var1, null, var5, "template-editor.created");
            }
         }
      }
   }

   public boolean h(Player var1, String var2) {
      if (!this.th.isLicenseActive()) {
         if (var1 != null) {
            var1.sendMessage(this.th.licenseLockMessage());
         }

         return true;
      } else if (!this.bt(var1)) {
         return true;
      } else {
         X var3 = this.ti.get(var1.getUniqueId());
         if (var3 != null) {
            var1.sendMessage(h.ac("template-editor.session-active"));
            this.a(var1, var3, T.MAIN, true);
            return true;
         } else {
            E var4 = this.th.getBotTemplateManager().i1(var2);
            if (var4 == null) {
               var1.sendMessage(h.a("errors.template-not-found", "{template}", var2));
               return true;
            } else if (this.a(null, var4.fe())) {
               var1.sendMessage(h.a("template-editor.key-reserved", "{key}", var4.fe()));
               return true;
            } else {
               return this.a(var1, var4.fe(), var4, "template-editor.loaded");
            }
         }
      }
   }

   public boolean a(Player var1, boolean var2) {
      if (var1 == null) {
         return true;
      } else {
         X var3 = this.ti.get(var1.getUniqueId());
         if (var3 == null) {
            var1.sendMessage(h.ac("template-editor.no-session"));
            return true;
         } else {
            this.a(var3, var2, true, h.ac("template-editor.cancelled"));
            return true;
         }
      }
   }

   public boolean c(CommandSender var1, String var2) {
      if (!this.th.isLicenseActive()) {
         var1.sendMessage(this.th.licenseLockMessage());
         return true;
      } else {
         String var3 = this.th.getBotTemplateManager().normalizeTemplateKey(var2);
         if (!this.th.getBotTemplateManager().j0(var3)) {
            var1.sendMessage(h.a("errors.template-not-found", "{template}", var2));
            return true;
         } else if (this.a(null, var3)) {
            var1.sendMessage(h.a("template-editor.delete-active", "{key}", var3));
            return true;
         } else if (!this.th.getBotTemplateManager().l0(var3)) {
            var1.sendMessage(h.a("template-editor.delete-failed", "{key}", var3));
            return true;
         } else {
            this.th.getBotManager().a(var3, n.TEMPLATE_DESPAWN);
            var1.sendMessage(h.a("template-editor.deleted", "{key}", var3));
            return true;
         }
      }
   }

   public boolean b(Player var1, NPC var2) {
      if (!this.th.isLicenseActive()) {
         if (var1 != null) {
            var1.sendMessage(this.th.licenseLockMessage());
         }

         return true;
      } else {
         X var3 = this.ag(var2);
         if (var3 == null) {
            return false;
         } else if (!this.b(var1, var3)) {
            var1.sendMessage(h.ac("no-permission"));
            return true;
         } else {
            this.a(var1, var3, T.MAIN, true);
            return true;
         }
      }
   }

   private boolean a(Player var1, String var2, E var3, String var4) {
      Location var5 = this.bu(var1);
      NPC var6 = this.th.getBotManager().a(var3, var5, var1.getUniqueId());
      if (var6 == null) {
         var1.sendMessage(h.ac("template-editor.preview-spawn-failed"));
         return true;
      } else {
         X var7 = new X(
            var1.getUniqueId(),
            var2,
            var3.fe(),
            var3.fh(),
            var3.fY(),
            var3.fZ(),
            var3.getBotType(),
            var3.ga(),
            var3.gb(),
            var3.gc(),
            var3.gd(),
            var3.ge(),
            var3.gf(),
            var6.getId(),
            var5,
            "SHELDERA",
            "SHELDERA"
         );
         this.a(var7, var3);
         this.ti.put(var1.getUniqueId(), var7);
         this.tj.put(var6.getId(), var1.getUniqueId());
         var1.sendMessage(h.a(var4, "{key}", var7.key));
         this.c(var7);
         this.a(var1, var7, T.MAIN, true);
         return true;
      }
   }

   private boolean bt(Player var1) {
      if (!this.th.isCitizensReady()) {
         var1.sendMessage(h.ac("citizens-not-loaded"));
         return false;
      } else {
         return true;
      }
   }

   private void gM() {
      for (X var3 : new ArrayList<>(this.ti.values())) {
         if (!var3.tE) {
            NPC var4 = this.h(var3);
            if (var4 != null && var4.isSpawned() && var4.getEntity() instanceof Player var5) {
               var3.tC = 0;
               var4.getNavigator().cancelNavigation();
               FollowTrait var11 = (FollowTrait)var4.getTraitNullable(FollowTrait.class);
               if (var11 != null) {
                  var11.follow(null);
               }

               LookClose var7 = (LookClose)var4.getTraitNullable(LookClose.class);
               if (var7 != null) {
                  var7.lookClose(false);
               }

               var5.setInvulnerable(true);
               var5.setCollidable(false);
               var5.setGravity(false);
               var5.setVelocity(new Vector(0, 0, 0));
               var5.setFireTicks(0);
               var5.setFallDistance(0.0F);
               AttributeInstance var8 = var5.getAttribute(Attribute.GENERIC_MAX_HEALTH);
               if (var8 != null && var5.getHealth() < var8.getBaseValue()) {
                  var5.setHealth(var8.getBaseValue());
               }

               Location var9 = var3.ty;
               if (var9.getWorld() != null) {
                  boolean var10 = var3.tB == null || !var3.tB.equals(var5.getEntityId());
                  if (!var3.tD && !var10 && !this.k(var5.getLocation(), var9)) {
                     var5.setRotation(var9.getYaw(), var9.getPitch());
                  } else {
                     this.a(var3, var4, var5, var3.tD || var10);
                  }
               }
            } else {
               var3.tC++;
               if (var3.tC >= 40) {
                  this.a(var3, true, true, h.ac("template-editor.preview-lost"));
               }
            }
         }
      }
   }

   private Location bu(Player var1) {
      Location var2 = var1.getLocation().clone();
      Vector var3 = var2.getDirection();
      var3.setY(0.0);
      if (var3.lengthSquared() < 1.0E-6) {
         var3 = new Vector(0, 0, 1);
      }

      Location var4 = var2.clone().add(var3.normalize().multiply(2.0));
      Location var5 = var4.clone().add(0.0, var1.getEyeHeight(), 0.0);
      Vector var6 = var1.getEyeLocation().toVector().subtract(var5.toVector());
      if (var6.lengthSquared() < 1.0E-6) {
         var6 = var2.getDirection().clone().multiply(-1);
         var6.setY(0.0);
      }

      var4.setDirection(var6);
      return var4;
   }

   private boolean k(Location var1, Location var2) {
      return !var1.getWorld().getUID().equals(var2.getWorld().getUID())
         || var1.distanceSquared(var2) > 1.0E-4
         || this.a(var1.getYaw(), var2.getYaw()) > 1.0F
         || this.a(var1.getPitch(), var2.getPitch()) > 1.0F;
   }

   private float a(float var1, float var2) {
      float var3 = (var1 - var2) % 360.0F;
      if (var3 < -180.0F) {
         var3 += 360.0F;
      } else if (var3 > 180.0F) {
         var3 -= 360.0F;
      }

      return Math.abs(var3);
   }

   private void a(X var1, NPC var2, Player var3, boolean var4) {
      BotTrait var5 = (BotTrait)var2.getTraitNullable(BotTrait.class);
      if (var5 != null) {
         this.th.getBotManager().b(var2, var5);
         if (var4) {
            if (var5.isCpvpEnabled()) {
               this.th.getCrystalPvpModule().c(var3, var5);
            } else {
               if (var5.hasStoredInventory()) {
                  this.th.getCrystalPvpModule().d(var3, var5);
               }

               this.th.getBotManager().H().a(var2, var5);
            }
         }

         var3.setInvulnerable(true);
         var3.setCollidable(false);
         var3.setGravity(false);
         var3.setVelocity(new Vector(0, 0, 0));
         var3.setFireTicks(0);
         var3.setFallDistance(0.0F);
         AttributeInstance var6 = var3.getAttribute(Attribute.GENERIC_MAX_HEALTH);
         if (var6 != null && var3.getHealth() < var6.getBaseValue()) {
            var3.setHealth(var6.getBaseValue());
         }

         Location var7 = var1.ty;
         if (var7.getWorld() != null) {
            var3.teleport(var7.clone());
         }

         var1.tD = false;
         var1.tB = var3.getEntityId();
      }
   }

   private void a(Player var1, X var2, T var3, boolean var4) {
      var3 = this.a(var2, var3);
      o0 var5 = this.th.getGuiConfigManager().bp(var3.guiId);
      int var6 = var5 != null ? var5.ez() : var3.defaultSize;
      String var7 = var5 != null ? var5.ey() : var3.defaultTitle;
      Inventory var8 = Bukkit.createInventory(new W(var2.tv, var3), var6, var7);
      this.a(var8, var2, var3);
      var1.openInventory(var8);
      if (var4) {
         this.bv(var1);
      }
   }

   private void a(Player var1, X var2, T var3) {
      var3 = this.a(var2, var3);
      if (var1.getOpenInventory().getTopInventory().getHolder() instanceof W var4 && var4.tt().equals(var2.tv) && var4.tu() == var3) {
         this.a(var1.getOpenInventory().getTopInventory(), var2, var3);
         var1.updateInventory();
         this.bv(var1);
      } else {
         this.a(var1, var2, var3, false);
         this.bv(var1);
      }
   }

   private void a(Inventory var1, X var2, T var3) {
      o0 var4 = this.th.getGuiConfigManager().bp(var3.guiId);
      ItemStack var5 = var4 != null && var4.eC() ? var4.ev() : this.a(Material.GRAY_STAINED_GLASS_PANE, " ", List.of(), false);

      for (int var6 = 0; var6 < var1.getSize(); var6++) {
         var1.setItem(var6, var5);
      }

      if (var4 != null) {
         for (s0 var7 : var4.eD().values()) {
            if (var7.ff() >= 0 && var7.ff() < var1.getSize()) {
               ItemStack var8 = this.a(var2, var3, var7);
               if (var8 != null) {
                  var1.setItem(var7.ff(), var8);
               }
            }
         }
      }
   }

   private ItemStack a(X var1, T var2, s0 var3) {
      return switch (var2) {
         case MAIN -> this.a(var1, var3);
         case BEHAVIOR -> this.b(var1, var3);
         case COMBAT -> this.c(var1, var3);
         case INVENTORY -> this.d(var1, var3);
         case CPVP -> this.e(var1, var3);
      };
   }

   private ItemStack a(X var1, s0 var2) {
      if (!"gui_enabled".equals(var2.fe()) && !"behavior_menu".equals(var2.fe()) && !"cpvp_menu".equals(var2.fe())) {
         Map var3 = this.a(var1);
         var3.put("{name}", this.k(var1.name, var1.key));
         var3.put("{display_name}", this.k(var1.displayName, var1.key));
         var3.put("{skin}", var1.bq != null && !var1.bq.isBlank() ? var1.bq : "Auto");
         var3.put("{bot_type}", var1.botType.name());
         var3.put("{target_binding}", this.b(var1.rC));
         var3.put("{death_mode}", var1.rD.name());
         var3.put("{kill_mode}", this.b(var1.rE));
         var3.put("{respawn_delay}", this.g(var1.respawnDelayTicks));
         var3.put("{warmup}", this.g(var1.warmupTicks));

         var3.put("{settings_name}", switch (var1.botType) {
            case CPVP -> "CPvP Settings";
            case DUMMY -> "Behavior Settings";
            default -> "Combat Settings";
         });

         var3.put("{settings_description}", switch (var1.botType) {
            case CPVP -> "Edit Crystal PvP settings";
            case DUMMY -> "Edit dummy behavior settings";
            default -> "Edit reach, crit, and combat logic";
         });
         Material var4 = var2.fg();
         String var5 = var2.fe();
         switch (var5) {
            case "bot_type":
               var4 = switch (var1.botType) {
                  case CPVP -> Material.END_CRYSTAL;
                  case DUMMY -> Material.TARGET;
                  default -> Material.IRON_SWORD;
               };
               break;
            case "target_binding":
               var4 = switch (var1.rC) {
                  case NONE -> Material.BARRIER;
                  case BOT -> Material.ARMOR_STAND;
                  case REQUIRED -> Material.PLAYER_HEAD;
               };
               break;
            case "death_mode":
               var4 = var1.rD == J.RESPAWN ? Material.TOTEM_OF_UNDYING : Material.BONE;
               break;
            case "kill_mode":
               var4 = switch (var1.rE) {
                  case DESPAWN -> Material.BONE;
                  case RESPAWN -> Material.TOTEM_OF_UNDYING;
                  case NONE -> Material.BARRIER;
               };
               break;
            case "respawn_delay":
               var4 = Material.CLOCK;
               break;
            case "warmup":
               var4 = var1.warmupTicks > 0L ? Material.BLAZE_POWDER : Material.GRAY_DYE;
               break;
            case "combat_menu":
               var4 = switch (var1.botType) {
                  case CPVP -> Material.END_CRYSTAL;
                  case DUMMY -> Material.REDSTONE_TORCH;
                  default -> Material.DIAMOND_SWORD;
               };
         }

         ItemStack var7 = this.b(var2, var3, var4);
         if ("skin_name".equals(var2.fe()) || "target_binding".equals(var2.fe()) && var4 == Material.PLAYER_HEAD) {
            this.b(var7, this.d(var1));
         }

         return var7;
      } else {
         return null;
      }
   }

   private ItemStack b(X var1, s0 var2) {
      if ("pvp_enabled".equals(var2.fe())) {
         return null;
      } else {
         BotTrait var3 = this.i(var1);
         if (var3 == null) {
            return this.b(var2, this.a(var1), var2.fg());
         } else {
            Map var4 = this.a(var1);
            var4.put("{look}", this.u(var3.isLookAtOwner()));
            var4.put("{follow}", this.u(var3.isFollowOwner()));
            var4.put("{random}", this.u(var3.isRandomWalk()));
            var4.put("{hold_shield}", this.u(var3.isHoldShield()));
            var4.put("{use_shield}", this.u(var3.isUseShield()));
            var4.put("{resistance}", this.u(var3.isResistance()));
            var4.put("{frozen}", this.u(var3.isFrozen()));
            var4.put("{shield_hand}", var3.isShieldInMainHand() ? "MAIN" : "OFF");
            var4.put("{pvp_enabled}", this.u(var3.isPvpEnabled()));
            String var6 = var2.fe();

            Material var5 = switch (var6) {
               case "look" -> var3.isLookAtOwner() ? Material.LIME_DYE : Material.RED_DYE;
               case "follow" -> var3.isFollowOwner() ? Material.LIME_DYE : Material.RED_DYE;
               case "random" -> var3.isRandomWalk() ? Material.LIME_DYE : Material.RED_DYE;
               case "hold_shield" -> var3.isHoldShield() ? Material.SHIELD : Material.RED_DYE;
               case "use_shield" -> var3.isUseShield() ? Material.LIME_DYE : Material.RED_DYE;
               case "resistance" -> var3.isResistance() ? Material.TOTEM_OF_UNDYING : Material.RED_DYE;
               case "frozen" -> var3.isFrozen() ? Material.BLUE_ICE : Material.RED_DYE;
               case "shield_hand" -> var3.isShieldInMainHand() ? Material.NETHERITE_SWORD : Material.SHIELD;
               case "pvp_enabled" -> var3.isPvpEnabled() ? Material.LIME_DYE : Material.RED_DYE;
               default -> var2.fg();
            };
            return this.b(var2, var4, var5);
         }
      }
   }

   private ItemStack c(X var1, s0 var2) {
      BotTrait var3 = this.i(var1);
      if (var3 == null) {
         return this.b(var2, this.a(var1), var2.fg());
      } else {
         Map var4 = this.a(var1);
         var4.put("{strafe}", this.u(var3.isPvpStrafe()));
         var4.put("{wtap}", this.u(var3.isPvpWTap()));
         var4.put("{stap}", this.u(var3.isPvpSTap()));
         var4.put("{crits}", this.u(var3.isPvpCrits()));
         var4.put("{shield_breaker}", this.u(var3.isPvpShieldBreaker()));
         var4.put("{retreat}", this.u(var3.isPvpRetreat()));

         var4.put("{reach}", switch (var3.getPvpReachMode()) {
            case 0 -> "SHORT";
            default -> "NORMAL";
            case 2 -> "EXTENDED";
            case 3 -> "ADVANCED";
         });

         var4.put("{aggression}", switch (var3.getPvpAggression()) {
            case 0 -> "LOW";
            case 2 -> "HIGH";
            default -> "MEDIUM";
         });

         var4.put("{crit_chance}", switch (var3.getPvpCritChance()) {
            case 0 -> "25%";
            case 1 -> "50%";
            default -> "75%";
            case 3 -> "100%";
         });

         var4.put("{crit_speed}", switch (var3.getPvpCritSpeed()) {
            case 1 -> "NORMAL";
            case 2 -> "FAST";
            default -> "SLOW";
         });
         String var6 = var2.fe();

         Material var5 = switch (var6) {
            case "strafe" -> var3.isPvpStrafe() ? Material.LIME_DYE : Material.RED_DYE;
            case "wtap" -> var3.isPvpWTap() ? Material.LIME_DYE : Material.RED_DYE;
            case "stap" -> var3.isPvpSTap() ? Material.LIME_DYE : Material.RED_DYE;
            case "crits" -> var3.isPvpCrits() ? Material.LIME_DYE : Material.RED_DYE;
            case "shield_breaker" -> var3.isPvpShieldBreaker() ? Material.LIME_DYE : Material.RED_DYE;
            case "retreat" -> var3.isPvpRetreat() ? Material.LIME_DYE : Material.RED_DYE;
            default -> var2.fg();
         };
         return this.b(var2, var4, var5);
      }
   }

   private ItemStack d(X var1, s0 var2) {
      BotTrait var3 = this.i(var1);
      if (var3 == null) {
         return this.b(var2, this.a(var1), var2.fg());
      } else {
         String var4 = var2.fe();

         return switch (var4) {
            case "helmet_pattern" -> this.e(this.th.getGuiManager().a("pattern", this.th.getBotManager().H().am(var3.getHelmetTrimPattern())));
            case "helmet_material" -> this.e(this.th.getGuiManager().a("material", this.th.getBotManager().H().an(var3.getHelmetTrimMaterial())));
            case "chest_pattern" -> this.e(this.th.getGuiManager().a("pattern", this.th.getBotManager().H().am(var3.getChestTrimPattern())));
            case "chest_material" -> this.e(this.th.getGuiManager().a("material", this.th.getBotManager().H().an(var3.getChestTrimMaterial())));
            case "legs_pattern" -> this.e(this.th.getGuiManager().a("pattern", this.th.getBotManager().H().am(var3.getLegsTrimPattern())));
            case "legs_material" -> this.e(this.th.getGuiManager().a("material", this.th.getBotManager().H().an(var3.getLegsTrimMaterial())));
            case "boots_pattern" -> this.e(this.th.getGuiManager().a("pattern", this.th.getBotManager().H().am(var3.getBootsTrimPattern())));
            case "boots_material" -> this.e(this.th.getGuiManager().a("material", this.th.getBotManager().H().an(var3.getBootsTrimMaterial())));
            case "helmet_display" -> this.a(
               "Helmet",
               var3.getHelmetEnchant(),
               this.th
                  .getBotManager()
                  .H()
                  .a(
                     var3.getHelmetMaterial(),
                     var3.getHelmetEnchant(),
                     this.th.getBotManager().H().am(var3.getHelmetTrimPattern()),
                     this.th.getBotManager().H().an(var3.getHelmetTrimMaterial())
                  )
            );
            case "chest_display" -> this.a(
               "Chestplate",
               var3.getChestplateEnchant(),
               this.th
                  .getBotManager()
                  .H()
                  .b(
                     var3.getChestplateMaterial(),
                     var3.getChestplateEnchant(),
                     this.th.getBotManager().H().am(var3.getChestTrimPattern()),
                     this.th.getBotManager().H().an(var3.getChestTrimMaterial())
                  )
            );
            case "legs_display" -> this.a(
               "Leggings",
               var3.getLeggingsEnchant(),
               this.th
                  .getBotManager()
                  .H()
                  .c(
                     var3.getLeggingsMaterial(),
                     var3.getLeggingsEnchant(),
                     this.th.getBotManager().H().am(var3.getLegsTrimPattern()),
                     this.th.getBotManager().H().an(var3.getLegsTrimMaterial())
                  )
            );
            case "boots_display" -> this.a(
               "Boots",
               var3.getBootsEnchant(),
               this.th
                  .getBotManager()
                  .H()
                  .d(
                     var3.getBootsMaterial(),
                     var3.getBootsEnchant(),
                     this.th.getBotManager().H().am(var3.getBootsTrimPattern()),
                     this.th.getBotManager().H().an(var3.getBootsTrimMaterial())
                  )
            );
            case "main_hand" -> this.h(var3);
            case "totem_count" -> this.k(var3);
            case "helmet_piece_material" -> this.b(
               var2, Map.of("{value}", this.s0(var3.getHelmetMaterial())), this.th.getBotManager().H().g(var3.getHelmetMaterial(), "HELMET")
            );
            case "chest_piece_material" -> this.b(
               var2, Map.of("{value}", this.s0(var3.getChestplateMaterial())), this.th.getBotManager().H().g(var3.getChestplateMaterial(), "CHESTPLATE")
            );
            case "legs_piece_material" -> this.b(
               var2, Map.of("{value}", this.s0(var3.getLeggingsMaterial())), this.th.getBotManager().H().g(var3.getLeggingsMaterial(), "LEGGINGS")
            );
            case "boots_piece_material" -> this.b(
               var2, Map.of("{value}", this.s0(var3.getBootsMaterial())), this.th.getBotManager().H().g(var3.getBootsMaterial(), "BOOTS")
            );
            default -> this.b(var2, this.a(var1), var2.fg());
         };
      }
   }

   private ItemStack e(X var1, s0 var2) {
      if ("enabled".equals(var2.fe())) {
         return null;
      } else {
         BotTrait var3 = this.i(var1);
         if (var3 == null) {
            return this.b(var2, this.a(var1), var2.fg());
         } else {
            ae var4 = var3.getCpvpSettings();
            Map var5 = this.a(var1, var3, var4, var2.fe());
            Material var6 = this.b(var2.fe(), var4, var3, var2.fg());
            return this.b(var2, var5, var6);
         }
      }
   }

   private Map<String, String> a(X var1) {
      HashMap var2 = new HashMap();
      var2.put("{key}", var1.key);
      var2.put("{display_name}", this.k(var1.displayName, var1.key));
      var2.put("{template_name}", this.k(var1.name, var1.key));
      var2.put("{target_binding}", this.b(var1.rC));
      var2.put("{death_mode}", var1.rD.name());
      var2.put("{kill_mode}", this.b(var1.rE));
      return var2;
   }

   private String b(M var1) {
      return switch (var1) {
         case NONE -> "AUTO PLAYER";
         case BOT -> "AUTO BOT";
         case REQUIRED -> "REQUIRED PLAYER";
      };
   }

   private String b(L var1) {
      return switch (var1) {
         case DESPAWN -> "REMOVE";
         case RESPAWN -> "RESPAWN";
         case NONE -> "NONE";
      };
   }

   private String i(long var1, long var3) {
      long var5 = Math.max(1L, Math.min(var1, var3));
      long var7 = Math.max(1L, Math.max(var1, var3));
      return var5 == var7 ? String.valueOf(var5) : var5 + "-" + var7;
   }

   private String o(ae var1) {
      if (var1 == null) {
         return "Default";
      } else {
         return !var1.bI() ? "Clean / no misses" : var1.bB() + "% hesitate, " + var1.bE() + "% miss, " + var1.bH() + "% pearl restraint";
      }
   }

   private Map<String, String> a(X var1, BotTrait var2, ae var3, String var4) {
      Map var5 = this.a(var1);
      String var6 = var3.aG();
      var5.put("{enabled}", this.u(var2.isCpvpEnabled()));
      var5.put("{status}", var2.isCpvpEnabled() ? "§a§lENABLED" : "§c§lDISABLED");
      var5.put("{state}", this.b(var4, var3, var2));
      var5.put("{skill}", var6);
      var5.put("{skill_level}", var6);
      var5.put("{skill_color}", this.r1(var6));
      var5.put("{easy_marker}", var6.equalsIgnoreCase("EASY") ? "§a▶ " : "§8  ");
      var5.put("{medium_marker}", var6.equalsIgnoreCase("MEDIUM") ? "§e▶ " : "§8  ");
      var5.put("{hard_marker}", var6.equalsIgnoreCase("HARD") ? "§6▶ " : "§8  ");
      var5.put("{pro_marker}", var6.equalsIgnoreCase("PRO") ? "§c▶ " : "§8  ");
      var5.put("{place_delay}", this.i(var3.aW(), var3.aX()));
      var5.put("{break_delay}", this.i(var3.aZ(), var3.bA()));
      var5.put("{realism_profile}", this.o(var3));
      var5.put("{fov_degrees}", String.format(Locale.US, "%.1f", var3.aU()));
      var5.put("{value}", this.c(var4, var3));
      var5.put("{bar}", this.d(var4, var3));
      var5.put("{aggression}", String.format(Locale.US, "%.1f", var3.aI()));
      var5.put("{pearls}", this.u(var3.aK()));
      var5.put("{mace}", this.u(var3.aL()));
      var5.put("{gapples}", this.u(var3.aM()));
      var5.put("{obsidian}", this.u(var3.aN()));
      var5.put("{block_breaking}", this.u(var3.aO()));
      var5.put("{strafe}", this.u(var3.aP()));
      var5.put("{anchoring}", this.u(var3.aQ()));
      var5.put("{heal_threshold}", String.valueOf((int)Math.round(var3.aR() * 100.0)));
      var5.put("{pearl_cooldown}", String.format(Locale.US, "%.1f", var3.aT() / 1000.0));
      return var5;
   }

   private Material b(String var1, ae var2, BotTrait var3, Material var4) {
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

   private String b(String var1, ae var2, BotTrait var3) {
      return switch (var1) {
         case "enabled" -> var3.isCpvpEnabled() ? "§a§lON" : "§c§lOFF";
         case "pearls" -> var2.aK() ? "§aON" : "§cOFF";
         case "mace" -> var2.aL() ? "§aON" : "§cOFF";
         case "gapples" -> var2.aM() ? "§aON" : "§cOFF";
         case "strafe" -> var2.aP() ? "§aON" : "§cOFF";
         case "obsidian" -> var2.aN() ? "§aON" : "§cOFF";
         case "block_breaking" -> var2.aO() ? "§aON" : "§cOFF";
         case "anchoring" -> var2.aQ() ? "§aON" : "§cOFF";
         default -> "";
      };
   }

   private String c(String var1, ae var2) {
      return switch (var1) {
         case "aggression" -> String.format(Locale.US, "%.1f", var2.aI());
         case "pearl_cooldown" -> String.format(Locale.US, "%.1f", var2.aT() / 1000.0);
         case "heal_threshold" -> String.valueOf((int)Math.round(var2.aR() * 100.0));
         default -> "";
      };
   }

   private String d(String var1, ae var2) {
      return switch (var1) {
         case "aggression" -> this.b(var2.aI(), 0.5, 2.0, "§6");
         case "heal_threshold" -> this.b(var2.aR(), 0.2, 0.8, "§c");
         default -> "";
      };
   }

   private String r1(String var1) {
      if (var1 == null) {
         return "§7";
      } else {
         String var2 = var1.toUpperCase(Locale.ROOT);

         return switch (var2) {
            case "EASY" -> "§a";
            case "MEDIUM" -> "§e";
            case "HARD" -> "§6";
            case "PRO" -> "§c";
            default -> "§d";
         };
      }
   }

   private String b(double var1, double var3, double var5, String var7) {
      byte var8 = 20;
      double var9 = (var1 - var3) / (var5 - var3);
      int var11 = (int)Math.round(var9 * var8);
      var11 = Math.max(0, Math.min(var8, var11));
      StringBuilder var12 = new StringBuilder("§8[");

      for (int var13 = 0; var13 < var8; var13++) {
         var12.append(var13 < var11 ? var7 : "§7").append("|");
      }

      var12.append("§8]");
      return var12.toString();
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onInventoryClick(InventoryClickEvent var1) {
      if (!this.th.isLicenseActive()) {
         if (var1.getInventory().getHolder() instanceof W) {
            var1.setCancelled(true);
            var1.getWhoClicked().closeInventory();
         }
      } else if (var1.getWhoClicked() instanceof Player var2) {
         if (var1.getInventory().getHolder() instanceof W var9) {
            int var10 = var1.getView().getTopInventory().getSize();
            if (var1.getRawSlot() >= 0) {
               if (var1.getRawSlot() >= var10) {
                  if (this.a(var1)) {
                     var1.setCancelled(true);
                  }
               } else {
                  var1.setCancelled(true);
                  X var5 = this.ti.get(var9.tt());
                  if (var5 == null) {
                     var2.closeInventory();
                  } else if (!this.b(var2, var5)) {
                     var2.sendMessage(h.ac("no-permission"));
                  } else {
                     long var6 = System.currentTimeMillis();
                     Long var8 = this.th.getInventoryClickCooldown().get(var2.getUniqueId());
                     if (var8 == null || var6 - var8 >= 200L) {
                        this.th.getInventoryClickCooldown().put(var2.getUniqueId(), var6);
                        switch (var9.tu()) {
                           case MAIN:
                              this.a(var2, var5, var1);
                              break;
                           case BEHAVIOR:
                              this.b(var2, var5, var1);
                              break;
                           case COMBAT:
                              this.c(var2, var5, var1);
                              break;
                           case INVENTORY:
                              this.d(var2, var5, var1);
                              break;
                           case CPVP:
                              this.e(var2, var5, var1);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent var1) {
      if (var1.getInventory().getHolder() instanceof W) {
         if (var1.getPlayer() instanceof Player var2) {
            if (!this.tk.containsKey(var2.getUniqueId())) {
               Bukkit.getScheduler().runTask(this.th, () -> {
                  if (!(var2.getOpenInventory().getTopInventory().getHolder() instanceof W)) {
                     try {
                        var2.clearActiveItem();
                     } catch (Exception var2x) {
                     }
                  }
               });
            }
         }
      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent var1) {
      this.tk.remove(var1.getPlayer().getUniqueId());
      X var2 = this.ti.get(var1.getPlayer().getUniqueId());
      if (var2 != null) {
         this.a(var2, false, true, null);
      }
   }

   @EventHandler
   public void onPlayerChangedWorld(PlayerChangedWorldEvent var1) {
      X var2 = this.ti.get(var1.getPlayer().getUniqueId());
      if (var2 != null) {
         this.a(var2, true, true, h.ac("template-editor.cancelled-world"));
      }
   }

   @EventHandler
   public void onWorldUnload(WorldUnloadEvent var1) {
      for (X var4 : new ArrayList<>(this.ti.values())) {
         if (var4.ty.getWorld() != null && var4.ty.getWorld().getUID().equals(var1.getWorld().getUID())) {
            this.a(var4, true, true, h.ac("template-editor.preview-lost"));
         }
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onNpcRemove(NPCRemoveEvent var1) {
      this.af(var1.getNPC());
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onAsyncChat(AsyncPlayerChatEvent var1) {
      if (!this.th.isLicenseActive()) {
         this.tk.remove(var1.getPlayer().getUniqueId());
      } else {
         Q var2 = this.tk.remove(var1.getPlayer().getUniqueId());
         if (var2 != null) {
            var1.setCancelled(true);
            String var3 = var1.getMessage();
            Bukkit.getScheduler().runTask(this.th, () -> {
               X var4 = this.ti.get(var2.tq());
               Player var5 = var1.getPlayer();
               if (var4 != null) {
                  if ("cancel".equalsIgnoreCase(var3)) {
                     this.a(var5, var4, var2.ts(), true);
                  } else {
                     this.a(var5, var4, var2, var3);
                  }
               }
            });
         }
      }
   }

   private void a(Player var1, X var2, InventoryClickEvent var3) {
      String var4 = this.a(T.MAIN, var3.getRawSlot());
      if (var4 != null) {
         switch (var4) {
            case "template_name":
               this.a(var1, var2, V.NAME, T.MAIN);
               break;
            case "display_name":
               this.a(var1, var2, V.DISPLAY_NAME, T.MAIN);
               break;
            case "skin_name":
               this.a(var1, var2, V.SKIN_NAME, T.MAIN);
               break;
            case "bot_type":
               BotTrait var11 = this.i(var2);
               if (var11 == null) {
                  return;
               }

               a var8 = this.b(var2.botType);
               this.a(var2, var11, var8);
               this.c(var2);
               this.a(var1, var2, T.MAIN);
               break;
            case "target_binding":
               var2.rC = switch (var2.rC) {
                  case NONE -> M.BOT;
                  case BOT -> M.REQUIRED;
                  case REQUIRED -> M.NONE;
               };
               this.a(var1, var2, T.MAIN);
               break;
            case "death_mode":
               var2.rD = var2.rD == J.REMOVE ? J.RESPAWN : J.REMOVE;
               this.a(var1, var2, T.MAIN);
               break;
            case "kill_mode":
               var2.rE = switch (var2.rE) {
                  case DESPAWN -> L.RESPAWN;
                  case RESPAWN -> L.NONE;
                  case NONE -> L.DESPAWN;
               };
               this.a(var1, var2, T.MAIN);
               break;
            case "respawn_delay":
               if (var3.isShiftClick()) {
                  this.a(var1, var2, V.RESPAWN_DELAY, T.MAIN);
               } else {
                  long var10 = var3.isRightClick() ? -20L : 20L;
                  var2.respawnDelayTicks = Math.max(0L, Math.min(12000L, var2.respawnDelayTicks + var10));
                  this.a(var1, var2, T.MAIN);
               }
               break;
            case "warmup":
               if (var3.isShiftClick()) {
                  this.a(var1, var2, V.WARMUP, T.MAIN);
               } else {
                  long var7 = var3.isRightClick() ? -20L : 20L;
                  var2.warmupTicks = Math.max(0L, Math.min(1200L, var2.warmupTicks + var7));
                  BotTrait var9 = this.i(var2);
                  if (var9 != null) {
                     var9.setAttackWarmupTicks(var2.warmupTicks);
                  }

                  this.a(var1, var2, T.MAIN);
               }
               break;
            case "combat_menu":
               this.a(var1, var2, this.c(var2.botType), true);
               break;
            case "inventory_menu":
               this.a(var1, var2, T.INVENTORY, true);
               break;
            case "finish":
               this.a(var1, var2);
               break;
            case "cancel":
               this.a(var2, true, true, h.ac("template-editor.cancelled"));
         }
      }
   }

   private void b(Player var1, X var2, InventoryClickEvent var3) {
      if (!this.e(var2)) {
         this.a(var1, var2, T.MAIN, true);
      } else {
         BotTrait var4 = this.i(var2);
         if (var4 == null) {
            var1.closeInventory();
         } else {
            String var5 = this.a(T.BEHAVIOR, var3.getRawSlot());
            if (var5 != null) {
               switch (var5) {
                  case "look":
                     var4.setLookAtOwner(!var4.isLookAtOwner());
                     if (var4.isLookAtOwner()) {
                        var4.setFollowOwner(false);
                        var4.setRandomWalk(false);
                     }
                     break;
                  case "follow":
                     var4.setFollowOwner(!var4.isFollowOwner());
                     if (var4.isFollowOwner()) {
                        var4.setLookAtOwner(false);
                        var4.setRandomWalk(false);
                        var4.setFrozen(false);
                        var4.setPvpEnabled(false);
                     }
                     break;
                  case "random":
                     var4.setRandomWalk(!var4.isRandomWalk());
                     if (var4.isRandomWalk()) {
                        var4.setLookAtOwner(false);
                        var4.setFollowOwner(false);
                        var4.setFrozen(false);
                        var4.setPvpEnabled(false);
                     }
                     break;
                  case "hold_shield":
                     var4.setHoldShield(!var4.isHoldShield());
                     if (!var4.isHoldShield()) {
                        var4.setUseShield(false);
                        var4.setShieldInMainHand(false);
                     }
                     break;
                  case "use_shield":
                     var4.setUseShield(!var4.isUseShield());
                     if (var4.isUseShield()) {
                        var4.setHoldShield(true);
                     }
                     break;
                  case "resistance":
                     var4.setResistance(!var4.isResistance());
                     break;
                  case "frozen":
                     var4.setFrozen(!var4.isFrozen());
                     if (var4.isFrozen()) {
                        var4.setFollowOwner(false);
                        var4.setRandomWalk(false);
                     }
                     break;
                  case "shield_hand":
                     if (var4.isHoldShield() || var4.isUseShield()) {
                        var4.setShieldInMainHand(!var4.isShieldInMainHand());
                     }
                     break;
                  case "pvp_enabled":
                     var4.setPvpEnabled(!var4.isPvpEnabled());
                     if (var4.isPvpEnabled()) {
                        var4.setLookAtOwner(false);
                        var4.setFollowOwner(false);
                        var4.setRandomWalk(false);
                     }
                     break;
                  case "back":
                     this.a(var1, var2, T.MAIN, true);
                     return;
                  case "finish":
                     this.a(var1, var2);
                     return;
                  case "cancel":
                     this.a(var2, true, true, h.ac("template-editor.cancelled"));
                     return;
                  default:
                     return;
               }

               this.a(var2, var4);
               this.c(var2);
               this.a(var1, var2, T.BEHAVIOR);
            }
         }
      }
   }

   private void c(Player var1, X var2, InventoryClickEvent var3) {
      if (!this.f(var2)) {
         this.a(var1, var2, T.MAIN, true);
      } else {
         BotTrait var4 = this.i(var2);
         if (var4 == null) {
            var1.closeInventory();
         } else {
            String var5 = this.a(T.COMBAT, var3.getRawSlot());
            if (var5 != null) {
               switch (var5) {
                  case "strafe":
                     var4.setPvpStrafe(!var4.isPvpStrafe());
                     break;
                  case "wtap":
                     var4.setPvpWTap(!var4.isPvpWTap());
                     break;
                  case "stap":
                     var4.setPvpSTap(!var4.isPvpSTap());
                     var4.sTapActive = false;
                     var4.sTapDirection = null;
                     break;
                  case "crits":
                     var4.setPvpCrits(!var4.isPvpCrits());
                     break;
                  case "shield_breaker":
                     var4.setPvpShieldBreaker(!var4.isPvpShieldBreaker());
                     break;
                  case "retreat":
                     var4.setPvpRetreat(!var4.isPvpRetreat());
                     break;
                  case "reach":
                     var4.setPvpReachMode((var4.getPvpReachMode() + 1) % 4);
                     break;
                  case "aggression":
                     var4.setPvpAggression((var4.getPvpAggression() + 1) % 3);
                     break;
                  case "crit_chance":
                     var4.setPvpCritChance((var4.getPvpCritChance() + 1) % 4);
                     break;
                  case "crit_speed":
                     var4.setPvpCritSpeed((var4.getPvpCritSpeed() + 1) % 3);
                     break;
                  case "back":
                     this.a(var1, var2, T.MAIN, true);
                     return;
                  case "finish":
                     this.a(var1, var2);
                     return;
                  case "cancel":
                     this.a(var2, true, true, h.ac("template-editor.cancelled"));
                     return;
                  default:
                     return;
               }

               this.a(var2, var4);
               this.c(var2);
               this.a(var1, var2, T.COMBAT);
            }
         }
      }
   }

   private void d(Player var1, X var2, InventoryClickEvent var3) {
      BotTrait var4 = this.i(var2);
      if (var4 == null) {
         var1.closeInventory();
      } else {
         String var5 = this.a(T.INVENTORY, var3.getRawSlot());
         if (var5 != null) {
            ItemStack var6 = var3.getCursor();
            ItemStack var7 = null;
            switch (var5) {
               case "helmet_pattern":
                  this.a(var6, var3.isRightClick(), var4::setHelmetTrimPattern);
                  var7 = this.a(var6, var3.isRightClick());
                  break;
               case "helmet_material":
                  this.b(var6, var3.isRightClick(), var4::setHelmetTrimMaterial);
                  var7 = this.a(var6, var3.isRightClick());
                  break;
               case "chest_pattern":
                  this.a(var6, var3.isRightClick(), var4::setChestTrimPattern);
                  var7 = this.a(var6, var3.isRightClick());
                  break;
               case "chest_material":
                  this.b(var6, var3.isRightClick(), var4::setChestTrimMaterial);
                  var7 = this.a(var6, var3.isRightClick());
                  break;
               case "legs_pattern":
                  this.a(var6, var3.isRightClick(), var4::setLegsTrimPattern);
                  var7 = this.a(var6, var3.isRightClick());
                  break;
               case "legs_material":
                  this.b(var6, var3.isRightClick(), var4::setLegsTrimMaterial);
                  var7 = this.a(var6, var3.isRightClick());
                  break;
               case "boots_pattern":
                  this.a(var6, var3.isRightClick(), var4::setBootsTrimPattern);
                  var7 = this.a(var6, var3.isRightClick());
                  break;
               case "boots_material":
                  this.b(var6, var3.isRightClick(), var4::setBootsTrimMaterial);
                  var7 = this.a(var6, var3.isRightClick());
                  break;
               case "helmet_display":
                  var4.setHelmetEnchant(this.s1(var4.getHelmetEnchant()));
                  break;
               case "chest_display":
                  var4.setChestplateEnchant(this.s1(var4.getChestplateEnchant()));
                  break;
               case "legs_display":
                  var4.setLeggingsEnchant(this.s1(var4.getLeggingsEnchant()));
                  break;
               case "boots_display":
                  var4.setBootsEnchant(this.s1(var4.getBootsEnchant()));
                  break;
               case "helmet_piece_material":
                  var4.setHelmetMaterial(this.th.getBotManager().H().b(var4.getHelmetMaterial(), var3.isRightClick()));
                  break;
               case "chest_piece_material":
                  var4.setChestplateMaterial(this.th.getBotManager().H().b(var4.getChestplateMaterial(), var3.isRightClick()));
                  break;
               case "legs_piece_material":
                  var4.setLeggingsMaterial(this.th.getBotManager().H().b(var4.getLeggingsMaterial(), var3.isRightClick()));
                  break;
               case "boots_piece_material":
                  var4.setBootsMaterial(this.th.getBotManager().H().b(var4.getBootsMaterial(), var3.isRightClick()));
                  break;
               case "totem_count":
                  int var10 = this.a(var4.getTotemCount(), var3.isRightClick());
                  var4.setTotemCount(var10);
                  if (var10 > 0 && "none".equalsIgnoreCase(var4.getOffhandType())) {
                     var4.setOffhandType("totem");
                  }
                  break;
               case "main_hand":
                  if (var3.isRightClick()) {
                     var4.setCustomMainHand(null);
                  } else if (var6 != null && !var6.getType().isAir()) {
                     var4.setCustomMainHand(var6.clone());
                     var7 = var6.clone();
                  }
                  break;
               case "back":
                  this.a(var1, var2, T.MAIN, true);
                  return;
               case "finish":
                  this.a(var1, var2);
                  return;
               case "cancel":
                  this.a(var2, true, true, h.ac("template-editor.cancelled"));
                  return;
               default:
                  return;
            }

            this.c(var2);
            this.a(var1, var2, T.INVENTORY);
            this.b(var1, var7);
         }
      }
   }

   private void e(Player var1, X var2, InventoryClickEvent var3) {
      if (!this.g(var2)) {
         this.a(var1, var2, T.MAIN, true);
      } else {
         BotTrait var4 = this.i(var2);
         if (var4 == null) {
            var1.closeInventory();
         } else {
            ae var5 = var4.getCpvpSettings();
            String var6 = this.a(T.CPVP, var3.getRawSlot());
            if (var6 != null) {
               switch (var6) {
                  case "skill":
                     var5.aH();
                     break;
                  case "aggression":
                     var5.b(this.a(var5.aI(), var3, 0.5, 2.0, 1.0, 0.1));
                     break;
                  case "pearls":
                     var5.a(!var5.aK());
                     break;
                  case "pearl_cooldown":
                     var5.b(this.a(var5.aT(), var3, 500L, 3000L, 1800L, 250L));
                     break;
                  case "mace":
                     var5.b(!var5.aL());
                     break;
                  case "gapples":
                     var5.c(!var5.aM());
                     break;
                  case "heal_threshold":
                     var5.c(this.a(var5.aR(), var3, 0.2, 0.8, 0.4, 0.1));
                     break;
                  case "strafe":
                     var5.f(!var5.aP());
                     break;
                  case "obsidian":
                     var5.d(!var5.aN());
                     break;
                  case "block_breaking":
                     var5.e(!var5.aO());
                     break;
                  case "anchoring":
                     var5.g(!var5.aQ());
                     break;
                  case "back":
                     this.a(var1, var2, T.MAIN, true);
                     return;
                  case "finish":
                     this.a(var1, var2);
                     return;
                  case "cancel":
                     this.a(var2, true, true, h.ac("template-editor.cancelled"));
                     return;
                  default:
                     return;
               }

               this.a(var2, var4);
               this.c(var2);
               this.a(var1, var2, T.CPVP);
            }
         }
      }
   }

   private void a(Player var1, X var2, V var3, T var4) {
      this.tk.put(var1.getUniqueId(), new Q(var2.tv, var3, var4));
      var1.closeInventory();
      var1.sendMessage(h.a("template-editor.prompt", "{field}", var3.displayName));
   }

   private void a(Player var1, X var2, Q var3, String var4) {
      switch (var3.tr()) {
         case NAME:
            String var11 = var4.trim();
            String var6 = this.th.getBotTemplateManager().j1(var11);
            if (var11.isEmpty() || !this.th.getBotTemplateManager().k0(var6)) {
               var1.sendMessage(h.a("template-editor.invalid-key", "{key}", var4));
               this.a(var1, var2, var3.ts(), true);
               return;
            }

            var2.name = var11;
            var2.key = var6;
            break;
         case DISPLAY_NAME:
            var2.displayName = var4.trim();
            break;
         case SKIN_NAME:
            var2.bq = var4.trim();
            var2.tz = var2.bq;
            break;
         case RESPAWN_DELAY:
            try {
               double var10 = Double.parseDouble(var4);
               var2.respawnDelayTicks = Math.max(0L, Math.round(var10 * 20.0));
               break;
            } catch (NumberFormatException var9) {
               var1.sendMessage(h.a("template-editor.invalid-number", "{value}", var4));
               this.a(var1, var2, var3.ts(), true);
               return;
            }
         case WARMUP:
            try {
               double var5 = Double.parseDouble(var4);
               var2.warmupTicks = Math.max(0L, Math.round(var5 * 20.0));
               BotTrait var7 = this.i(var2);
               if (var7 != null) {
                  var7.setAttackWarmupTicks(var2.warmupTicks);
               }
            } catch (NumberFormatException var8) {
               var1.sendMessage(h.a("template-editor.invalid-number", "{value}", var4));
               this.a(var1, var2, var3.ts(), true);
               return;
            }
      }

      this.c(var2);
      this.a(var1, var2, var3.ts(), true);
   }

   private void a(Player var1, X var2) {
      E var3 = this.b(var2);
      String var4 = this.th.getBotTemplateManager().normalizeTemplateKey(var3.fe());
      if (!this.th.getBotTemplateManager().k0(var4)) {
         var1.sendMessage(h.a("template-editor.invalid-key", "{key}", var3.fe()));
         this.a(var1, var2, T.MAIN, true);
      } else if (!this.th.getBotTemplateManager().j0(var4) || var2.tw != null && var2.tw.equalsIgnoreCase(var4)) {
         if (this.a(var2, var4)) {
            var1.sendMessage(h.a("template-editor.key-reserved", "{key}", var4));
            this.a(var1, var2, T.MAIN, true);
         } else if (!this.th.getBotTemplateManager().a(var3, var2.tw)) {
            var1.sendMessage(h.a("template-editor.save-failed", "{key}", var4));
            this.a(var1, var2, T.MAIN, true);
         } else {
            this.a(var2, true, true, h.a("template-editor.saved", "{key}", var4));
         }
      } else {
         var1.sendMessage(h.a("template-editor.key-exists", "{key}", var4));
         this.a(var1, var2, T.MAIN, true);
      }
   }

   private E b(X var1) {
      BotTrait var2 = Objects.requireNonNull(this.i(var1), "Missing preview trait for template session");
      this.a(var1, var2);
      E var3 = this.th.getBotTemplateManager().k1(var1.key);
      G var4 = var3.gg();
      H var5 = var3.gh();
      I var6 = var3.gj();
      U var7 = var1.tF;
      S var8 = var1.tG;
      R var9 = var1.tH;
      boolean var10 = var1.botType == a.NORMAL;
      boolean var11 = var1.botType == a.CPVP;
      boolean var12 = var1.botType == a.DUMMY;
      return new E(
         var1.key,
         this.k(var1.name, var1.key),
         this.k(var1.displayName, var1.key),
         var1.bq == null ? "" : var1.bq.trim(),
         var1.botType,
         false,
         var1.rC,
         var1.rD,
         var1.rE,
         var1.respawnDelayTicks,
         var1.warmupTicks,
         new G(
            var10 ? var7.lookAtTarget : (var12 ? var8.lookAtTarget : var4.go()),
            var10 ? var7.followTarget : (var12 ? var8.followTarget : var4.gp()),
            var10 ? var7.randomWalk : (var12 ? var8.randomWalk : var4.isRandomWalk()),
            var10 ? var7.holdShield : (var12 ? var8.holdShield : var4.isHoldShield()),
            var10 ? var7.useShield : (var12 ? var8.useShield : var4.isUseShield()),
            var10 ? var7.resistance : (var12 ? var8.resistance : var4.isResistance()),
            var12 && var8.frozen,
            var10 ? var7.shieldInMainHand : (var12 ? var8.shieldInMainHand : var4.isShieldInMainHand()),
            var10 && var7.pvpEnabled
         ),
         new H(
            var10 ? var7.strafe : var5.gq(),
            var10 ? var7.wTap : var5.gr(),
            var10 ? var7.sTap : var5.gs(),
            var10 ? var7.crits : var5.gt(),
            var10 ? var7.shieldBreaker : var5.gu(),
            var10 ? var7.retreat : var5.gv(),
            var10 ? var7.reachMode : var5.gw(),
            var10 ? var7.aggression : var5.gx(),
            var10 ? var7.critChance : var5.gy(),
            var10 ? var7.critSpeed : var5.gz()
         ),
         new K(
            var2.getHelmetMaterial(),
            var2.getChestplateMaterial(),
            var2.getLeggingsMaterial(),
            var2.getBootsMaterial(),
            var2.getHelmetEnchant(),
            var2.getChestplateEnchant(),
            var2.getLeggingsEnchant(),
            var2.getBootsEnchant(),
            var2.getTotemCount() <= 0 ? "none" : var2.getOffhandType(),
            var2.getTotemCount(),
            var2.getCustomMainHand(),
            new F(var2.getHelmetTrimPattern(), var2.getHelmetTrimMaterial()),
            new F(var2.getChestTrimPattern(), var2.getChestTrimMaterial()),
            new F(var2.getLegsTrimPattern(), var2.getLegsTrimMaterial()),
            new F(var2.getBootsTrimPattern(), var2.getBootsTrimMaterial())
         ),
         new I(
            var11,
            var11 ? var9.skillLevel : var6.gB(),
            var11 ? var9.aggressionWeight : var6.aI(),
            var11 ? var9.usePearls : var6.aK(),
            var11 ? var9.useMace : var6.aL(),
            var11 ? var9.useGoldenApples : var6.aM(),
            var11 ? var9.placeObsidian : var6.aN(),
            var11 ? var9.breakBlocks : var6.aO(),
            var11 ? var9.strafingEnabled : var6.aP(),
            var11 ? var9.anchoringMode : var6.aQ(),
            var11 ? var9.healThreshold : var6.aR(),
            var11 ? var9.lowHpCrystalLethalReserve : var6.aS(),
            var11 ? var9.pearlCooldownMs : var6.aT(),
            var11 ? var9.J : var6.aU(),
            var11 ? var9.M : var6.aW(),
            var11 ? var9.N : var6.aX(),
            var11 ? var9.O : var6.aZ(),
            var11 ? var9.P : var6.bA(),
            var11 ? var9.Q : var6.bJ(),
            var11 ? var9.R : var6.bB(),
            var11 ? var9.S : var6.bC(),
            var11 ? var9.T : var6.bD(),
            var11 ? var9.U : var6.bE(),
            var11 ? var9.V : var6.bF(),
            var11 ? var9.W : var6.bG(),
            var11 ? var9.X : var6.bH()
         )
      );
   }

   private void c(X var1) {
      NPC var2 = this.h(var1);
      BotTrait var3 = this.i(var1);
      if (var2 != null && var3 != null) {
         this.b(var1, var3);
         var3.setEditorPreview(true);
         var3.setEditorSessionOwner(var1.tv);
         var3.setGuiEnabled(false);
         var2.setName(this.j(var1));
         String var4 = this.d(var1);
         if (!var4.equalsIgnoreCase(var1.tA)) {
            SkinTrait var5 = (SkinTrait)var2.getOrAddTrait(SkinTrait.class);
            var5.setSkinName(var4);
            var5.setShouldUpdateSkins(false);
            var1.tA = var4;
            var1.tB = null;
         }

         var1.tD = true;
         if (var2.isSpawned() && var2.getEntity() instanceof Player var7) {
            boolean var8 = var1.tD || var1.tB == null || !var1.tB.equals(var7.getEntityId());
            this.a(var1, var2, var7, var8);
         }
      }
   }

   private String d(X var1) {
      String var2 = var1.tz == null ? "" : var1.tz.trim();
      return var2.isEmpty() ? "SHELDERA" : var2;
   }

   private void a(X var1, E var2) {
      E var3 = this.th.getBotTemplateManager().k1(var1.key);
      var1.tF = U.e(var2.getBotType() == a.NORMAL ? var2 : var3);
      var1.tG = S.d(var2.getBotType() == a.DUMMY ? var2 : var3);
      var1.tH = R.c(var2.getBotType() == a.CPVP ? var2 : var3);
   }

   private void a(X var1, BotTrait var2) {
      if (var1 != null && var2 != null) {
         switch (var1.botType) {
            case CPVP:
               var1.tH.p(var2.getCpvpSettings());
               break;
            case DUMMY:
               var1.tG.l(var2);
               break;
            case NORMAL:
               var1.tF.l(var2);
         }
      }
   }

   private void b(X var1, BotTrait var2) {
      if (var1 != null && var2 != null) {
         var2.setBotType(var1.botType);
         var2.setAttackWarmupTicks(var1.warmupTicks);
         switch (var1.botType) {
            case CPVP:
               var2.setLookAtOwner(false);
               var2.setFollowOwner(false);
               var2.setRandomWalk(false);
               var2.setHoldShield(false);
               var2.setUseShield(false);
               var2.setResistance(false);
               var2.setFrozen(false);
               var2.setShieldInMainHand(false);
               var2.setPvpEnabled(false);
               var2.setCpvpEnabled(true);
               ae var6 = var2.getCpvpSettings();
               R var4 = var1.tH;
               var6.ax(var4.skillLevel);
               var6.a(var4.M, var4.N);
               var6.b(var4.O, var4.P);
               var6.e(var4.Q);
               var6.b(var4.R);
               var6.c(var4.S, var4.T);
               var6.c(var4.U);
               var6.d(var4.V, var4.W);
               var6.d(var4.X);
               var6.b(var4.aggressionWeight);
               var6.a(var4.usePearls);
               var6.b(var4.useMace);
               var6.c(var4.useGoldenApples);
               var6.d(var4.placeObsidian);
               var6.e(var4.breakBlocks);
               var6.f(var4.strafingEnabled);
               var6.g(var4.anchoringMode);
               var6.c(var4.healThreshold);
               var6.d(var4.lowHpCrystalLethalReserve);
               var6.b(var4.pearlCooldownMs);
               break;
            case DUMMY:
               S var5 = var1.tG;
               var2.setLookAtOwner(var5.lookAtTarget);
               var2.setFollowOwner(var5.followTarget);
               var2.setRandomWalk(var5.randomWalk);
               var2.setHoldShield(var5.holdShield);
               var2.setUseShield(var5.useShield);
               var2.setResistance(var5.resistance);
               var2.setFrozen(var5.frozen);
               var2.setShieldInMainHand(var5.shieldInMainHand);
               var2.setCpvpEnabled(false);
               var2.setPvpEnabled(false);
               if (var2.isFrozen()) {
                  var2.setFollowOwner(false);
                  var2.setRandomWalk(false);
               }
               break;
            case NORMAL:
               U var3 = var1.tF;
               var2.setLookAtOwner(var3.lookAtTarget);
               var2.setFollowOwner(var3.followTarget);
               var2.setRandomWalk(var3.randomWalk);
               var2.setHoldShield(var3.holdShield);
               var2.setUseShield(var3.useShield);
               var2.setResistance(var3.resistance);
               var2.setFrozen(false);
               var2.setShieldInMainHand(var3.shieldInMainHand);
               var2.setPvpEnabled(var3.pvpEnabled);
               var2.setCpvpEnabled(false);
               var2.setPvpStrafe(var3.strafe);
               var2.setPvpWTap(var3.wTap);
               var2.setPvpSTap(var3.sTap);
               var2.setPvpCrits(var3.crits);
               var2.setPvpShieldBreaker(var3.shieldBreaker);
               var2.setPvpRetreat(var3.retreat);
               var2.setPvpReachMode(var3.reachMode);
               var2.setPvpAggression(var3.aggression);
               var2.setPvpCritChance(var3.critChance);
               var2.setPvpCritSpeed(var3.critSpeed);
         }
      }
   }

   private void a(X var1, BotTrait var2, a var3) {
      this.a(var1, var2);
      var1.botType = var3;
      this.b(var1, var2);
   }

   private a b(a var1) {
      return switch (var1) {
         case CPVP -> a.DUMMY;
         case DUMMY -> a.NORMAL;
         default -> a.CPVP;
      };
   }

   private T c(a var1) {
      return switch (var1) {
         case CPVP -> T.CPVP;
         case DUMMY -> T.BEHAVIOR;
         default -> T.COMBAT;
      };
   }

   private T a(X var1, T var2) {
      if (var2 == T.BEHAVIOR && !this.e(var1)) {
         return T.MAIN;
      } else if (var2 == T.COMBAT && !this.f(var1)) {
         return T.MAIN;
      } else {
         return var2 == T.CPVP && !this.g(var1) ? T.MAIN : var2;
      }
   }

   private boolean e(X var1) {
      return var1 != null && var1.botType == a.DUMMY;
   }

   private boolean f(X var1) {
      return var1 != null && var1.botType == a.NORMAL;
   }

   private boolean g(X var1) {
      return var1 != null && var1.botType == a.CPVP;
   }

   private boolean a(InventoryClickEvent var1) {
      if (var1.isShiftClick()) {
         return true;
      } else if (var1.getClick() != ClickType.NUMBER_KEY && var1.getClick() != ClickType.DOUBLE_CLICK) {
         return switch (var1.getAction()) {
            case MOVE_TO_OTHER_INVENTORY, HOTBAR_SWAP, COLLECT_TO_CURSOR -> true;
            default -> false;
         };
      } else {
         return true;
      }
   }

   private void a(X var1, boolean var2, boolean var3, String var4) {
      if (var1 != null && !var1.tE) {
         var1.tE = true;
         this.ti.remove(var1.tv);
         this.tj.remove(var1.tx);
         this.tk.entrySet().removeIf(var1x -> var1x.getValue().tq().equals(var1.tv));
         if (var3) {
            this.r(var1.tv);
         }

         NPC var5 = this.h(var1);
         if (var5 != null) {
            this.th.getBotManager().a(var5, n.MANUAL);
         }

         if (var2) {
            Player var6 = Bukkit.getPlayer(var1.tv);
            if (var6 != null && var6.isOnline() && var4 != null && !var4.isBlank()) {
               var6.sendMessage(var4);
            }
         }
      }
   }

   private void r(UUID var1) {
      for (Player var3 : Bukkit.getOnlinePlayers()) {
         if (var3.getOpenInventory().getTopInventory().getHolder() instanceof W var4 && var4.tt().equals(var1)) {
            var3.closeInventory();
         }
      }
   }

   private void af(NPC var1) {
      if (var1 != null) {
         UUID var2 = this.tj.get(var1.getId());
         if (var2 != null) {
            X var3 = this.ti.get(var2);
            if (var3 != null && !var3.tE) {
               this.a(var3, true, true, h.ac("template-editor.preview-lost"));
            } else {
               this.tj.remove(var1.getId());
            }
         }
      }
   }

   private boolean b(Player var1, X var2) {
      return var1.getUniqueId().equals(var2.tv) || var1.hasPermission("practicebot.admin") || var1.hasPermission(this.th.getConfigManager().bl());
   }

   private boolean a(X var1, String var2) {
      for (X var4 : this.ti.values()) {
         if ((var1 == null || !var4.tv.equals(var1.tv)) && (var4.key.equalsIgnoreCase(var2) || var4.tw != null && var4.tw.equalsIgnoreCase(var2))) {
            return true;
         }
      }

      return false;
   }

   private X ag(NPC var1) {
      if (var1 == null) {
         return null;
      } else {
         UUID var2 = this.tj.get(var1.getId());
         return var2 != null ? this.ti.get(var2) : null;
      }
   }

   private NPC h(X var1) {
      return var1 == null ? null : CitizensAPI.getNPCRegistry().getById(var1.tx);
   }

   private BotTrait i(X var1) {
      NPC var2 = this.h(var1);
      return var2 != null ? (BotTrait)var2.getTraitNullable(BotTrait.class) : null;
   }

   private String a(T var1, int var2) {
      o0 var3 = this.th.getGuiConfigManager().bp(var1.guiId);
      if (var3 == null) {
         return null;
      } else {
         s0 var4 = var3.n(var2);
         return var4 != null ? var4.fe() : null;
      }
   }

   private ItemStack b(s0 var1, Map<String, String> var2, Material var3) {
      return this.c(var1, var2, var3);
   }

   private ItemStack b(s0 var1, Map<String, String> var2) {
      return this.c(var1, var2, var1 != null ? var1.fg() : null);
   }

   private ItemStack c(s0 var1, Map<String, String> var2, Material var3) {
      if (var1 == null) {
         return null;
      } else {
         Material var4 = var3 != null ? var3 : var1.fg();
         ItemStack var5 = new ItemStack(var4, var1.fj());
         ItemMeta var6 = var5.getItemMeta();
         if (var6 == null) {
            return var5;
         } else {
            String var7 = var1.fh();
            ArrayList var8 = new ArrayList<>(var1.fi());
            if (var2 != null) {
               for (Entry var10 : var2.entrySet()) {
                  var7 = var7.replace((CharSequence)var10.getKey(), (CharSequence)var10.getValue());
                  var8.replaceAll(var1x -> ((String)var1x).replace((CharSequence)var10.getKey(), (CharSequence)var10.getValue()));
               }
            }

            var6.setDisplayName(var7);
            var6.setLore(var8);
            this.a(var6);

            for (ItemFlag var12 : var1.fp()) {
               var6.addItemFlags(new ItemFlag[]{var12});
            }

            if (var1.fk()) {
               var6.addEnchant(Enchantment.UNBREAKING, 1, true);
               var6.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            }

            var5.setItemMeta(var6);
            return var5;
         }
      }
   }

   private ItemStack a(Material var1, String var2, List<String> var3, boolean var4) {
      ItemStack var5 = new ItemStack(var1);
      ItemMeta var6 = var5.getItemMeta();
      if (var6 == null) {
         return var5;
      } else {
         var6.setDisplayName(var2);
         var6.setLore(var3);
         this.a(var6);
         if (var4) {
            var6.addEnchant(Enchantment.UNBREAKING, 1, true);
            var6.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
         }

         var5.setItemMeta(var6);
         return var5;
      }
   }

   private void bv(Player var1) {
      Sound var2 = this.gN();
      var1.playSound(var1.getLocation(), var2, 0.45F, 1.15F);
      var1.playSound(var1.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.18F, 1.75F);
   }

   private ItemStack e(ItemStack var1) {
      if (var1 == null) {
         return null;
      } else {
         ItemStack var2 = var1.clone();
         ItemMeta var3 = var2.getItemMeta();
         if (var3 != null) {
            this.a(var3);
            var2.setItemMeta(var3);
         }

         return var2;
      }
   }

   private String s0(String var1) {
      String var2 = this.th.getBotManager().H().ai(var1);

      return switch (var2) {
         case "chain" -> "CHAIN";
         case "gold" -> "GOLD";
         default -> var2.toUpperCase(Locale.ROOT);
      };
   }

   private ItemStack h(BotTrait var1) {
      ItemStack var2 = var1.getCustomMainHand() != null ? var1.getCustomMainHand().clone() : this.th.getBotManager().H().E();
      ItemMeta var3 = var2.getItemMeta();
      if (var3 != null) {
         var3.setDisplayName("§c§lMain Hand");
         var3.setLore(
            List.of(
               "§7Current: §f" + this.th.getBotManager().H().b(var2.getType()),
               "§7Place any tool, weapon, or item here",
               "§7Right-click to clear the custom item"
            )
         );
         this.a(var3);
         var2.setItemMeta(var3);
      }

      return this.e(var2);
   }

   private ItemStack a(String var1, String var2, ItemStack var3) {
      ItemStack var4 = this.e(var3);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName("§b§l" + var1);
         var5.setLore(List.of("§7Enchant: §f" + this.t0(var2), "§7Left-click to cycle enchant", "§7Use trim slots for pattern/material"));
         this.a(var5);
         var4.setItemMeta(var5);
      }

      return var4;
   }

   private ItemStack b(String var1, String var2, ItemStack var3) {
      ItemStack var4 = var3.clone();
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         ArrayList var6 = new ArrayList();
         var6.add("§7Enchant: §f" + this.t0(var2));
         var6.add("§7Left-click to cycle enchant");
         var6.add("§7Use trim slots for pattern/material");
         var5.setDisplayName("§b§l" + var1);
         var5.setLore(var6);
         var5.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ARMOR_TRIM});
         var4.setItemMeta(var5);
      }

      return var4;
   }

   private ItemStack i(BotTrait var1) {
      ItemStack var2 = this.th.getGuiManager().f(var1);
      ItemMeta var3 = var2.getItemMeta();
      if (var3 != null) {
         ArrayList var4 = var3.hasLore() ? new ArrayList(var3.getLore()) : new ArrayList();
         var4.add(" ");
         var4.add("§eRight-click to clear custom sword");
         var3.setLore(var4);
         var2.setItemMeta(var3);
      }

      return var2;
   }

   private ItemStack j(BotTrait var1) {
      Material var2 = var1.getOffhandType().equalsIgnoreCase("shield") ? Material.SHIELD : Material.TOTEM_OF_UNDYING;
      return this.a(
         var2, "§6§lOffhand Type", List.of("§7Current: §f" + var1.getOffhandType().toUpperCase(Locale.ROOT), " ", "§eClick to toggle shield/totem"), false
      );
   }

   private ItemStack k(BotTrait var1) {
      boolean var2 = var1.getTotemCount() <= 0;
      return this.a(
         var2 ? Material.BARRIER : Material.TOTEM_OF_UNDYING,
         "§e§lTotem Count",
         List.of(
            "§7Current: §f" + var1.getTotemCount(),
            var2 ? "§cNo totem in offhand" : "§aTotem enabled",
            " ",
            "§eLeft-click to increase",
            "§eRight-click to decrease"
         ),
         false
      );
   }

   private void a(ItemStack var1, boolean var2, Consumer<String> var3) {
      if (var2) {
         var3.accept("");
      } else {
         TrimPattern var4 = this.th.getGuiManager().c(var1);
         if (var4 != null) {
            String var5 = this.th.getBotManager().H().a(var4);
            var3.accept(var5 != null ? var5 : "");
         }
      }
   }

   private void b(ItemStack var1, boolean var2, Consumer<String> var3) {
      if (var2) {
         var3.accept("");
      } else {
         TrimMaterial var4 = this.th.getGuiManager().d(var1);
         if (var4 != null) {
            String var5 = this.th.getBotManager().H().a(var4);
            var3.accept(var5 != null ? var5 : "");
         }
      }
   }

   private int a(int var1, boolean var2) {
      int var3 = tg.indexOf(var1);
      if (var3 == -1) {
         return var2 ? tg.get(tg.size() - 1) : tg.get(0);
      } else {
         int var4 = var2 ? (var3 - 1 + tg.size()) % tg.size() : (var3 + 1) % tg.size();
         return tg.get(var4);
      }
   }

   private String s1(String var1) {
      String var2 = var1 == null ? "protection" : var1.toLowerCase(Locale.ROOT);
      int var3 = tf.indexOf(var2);
      return var3 == -1 ? tf.get(0) : tf.get((var3 + 1) % tf.size());
   }

   private String t0(String var1) {
      String var2 = var1 == null ? "" : var1.toLowerCase(Locale.ROOT);

      return switch (var2) {
         case "blast_protection" -> "Blast Protection IV";
         case "none" -> "No Enchant";
         default -> "Protection IV";
      };
   }

   private void a(ItemMeta var1) {
      var1.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ARMOR_TRIM});

      try {
         var1.addItemFlags(new ItemFlag[]{ItemFlag.valueOf("HIDE_ADDITIONAL_TOOLTIP")});
      } catch (IllegalArgumentException var3) {
      }
   }

   private Sound gN() {
      try {
         return Sound.valueOf(this.th.getConfigManager().bt().trim().toUpperCase(Locale.ROOT));
      } catch (Exception var2) {
         return Sound.UI_BUTTON_CLICK;
      }
   }

   private void b(ItemStack var1, String var2) {
      if (var1 != null && var1.getType() == Material.PLAYER_HEAD) {
         if (var1.getItemMeta() instanceof SkullMeta var4) {
            String var5 = var2 != null && !var2.isBlank() ? var2.trim() : "SHELDERA";

            try {
               var4.setOwner(var5);
            } catch (Exception var10) {
            }

            try {
               Player var6 = Bukkit.getPlayerExact(var5);
               if (var6 != null) {
                  var4.setOwningPlayer(var6);
               } else {
                  var4.setOwningPlayer(Bukkit.getOfflinePlayer(var5));
               }
            } catch (Exception var9) {
               try {
                  var4.setOwningPlayer(Bukkit.getOfflinePlayer(var5));
               } catch (Exception var8) {
               }
            }

            this.a(var4);
            var1.setItemMeta(var4);
         }
      }
   }

   private ItemStack a(ItemStack var1, boolean var2) {
      return !var2 && var1 != null && !var1.getType().isAir() ? var1.clone() : null;
   }

   private void b(Player var1, ItemStack var2) {
      if (var2 != null) {
         Bukkit.getScheduler().runTask(this.th, () -> var1.setItemOnCursor(var2.clone()));
      }
   }

   private double a(double var1, ClickType var3, double var4, double var6, double var8, double var10) {
      if (var3.isShiftClick()) {
         return var8;
      } else {
         return var3.isRightClick() ? Math.max(var4, var1 - var10) : Math.min(var6, var1 + var10);
      }
   }

   private double a(double var1, InventoryClickEvent var3, double var4, double var6, double var8, double var10) {
      return this.a(var1, var3.getClick(), var4, var6, var8, var10);
   }

   private long a(long var1, InventoryClickEvent var3, long var4, long var6, long var8, long var10) {
      if (var3.isShiftClick()) {
         return var8;
      } else {
         return var3.isRightClick() ? Math.max(var4, var1 - var10) : Math.min(var6, var1 + var10);
      }
   }

   private String k(String var1, String var2) {
      String var3 = var1 == null ? "" : var1.trim();
      return var3.isEmpty() ? var2 : var3;
   }

   private String j(X var1) {
      String var2 = "Editor:" + this.k(var1.displayName, var1.key);
      String var3 = Y.t1(var2);
      return var3.length() > 16 ? var3.substring(0, 16) : var3;
   }

   private String u(boolean var1) {
      return var1 ? "§aON" : "§cOFF";
   }

   private String g(long var1) {
      return String.format(Locale.US, "%.1fs", var1 / 20.0);
   }
}
