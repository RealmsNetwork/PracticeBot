package com.sheldera.practicebot.x;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import com.sheldera.practicebot.api.event.PracticeBotTargetChangeCause;
import com.sheldera.practicebot.api.event.PracticeBotTemplateApplyCause;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPC.Metadata;
import net.citizensnpcs.trait.FollowTrait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class k {
   public static final String ay = "SHELDERA";
   private static final long az = 10L;
   private static final int ba = 24;
   private static final float bb = 64.0F;
   private static final float bc = 128.0F;
   private static final float bd = 512.0F;
   private static final String be = "practicebot";
   private static final String bf = "practicebot-template-controller";
   private static final String bg = "practicebot-editor-preview";
   private static final String bh = "practicebot-editor-owner";
   public static final String bi = "practicebot-api-template";
   private final PracticeBotPlugin bj;
   private final b bk;
   private final j bl;
   private final Map<UUID, r> bm = new HashMap<>();
   private final Map<Integer, UUID> bn = new HashMap<>();
   private final Map<Integer, NPC> bo = new HashMap<>();
   private final Map<UUID, String> pendingWeaponProfiles = new HashMap<>();

   public k(PracticeBotPlugin var1) {
      this.bj = var1;
      this.bk = var1.getConfigManager();
      this.bl = new j(var1);
   }

   public j H() {
      return this.bl;
   }

   public boolean a(Player var1, a var2) {
      if (!this.bj.isLicenseActive()) {
         var1.sendMessage(this.bj.licenseLockMessage());
         return true;
      } else {
         long var3 = System.currentTimeMillis();
         long var5 = this.bj.getSpawnCooldown().getOrDefault(var1.getUniqueId(), 0L);
         if (var3 - var5 < 5000L) {
            String var7 = String.format(Locale.US, "%.1f", (5000L - (var3 - var5)) / 1000.0);
            var1.sendMessage(h.a("errors.spawn-cooldown", "{time}", var7));
            return true;
         } else {
            this.bj.getSpawnCooldown().put(var1.getUniqueId(), var3);
            this.b(var1, var2);
            return true;
         }
      }
   }

   public boolean a(Player player, String weaponProfile) {
      if (player == null || !this.bj.isLicenseActive()) {
         return false;
      }

      String profile = weaponProfile == null ? "sword" : weaponProfile.trim().toLowerCase(Locale.ROOT);
      if (!profile.equals("mace") && !profile.equals("spear")) {
         profile = "sword";
      }

      if ("spear".equals(profile) && Material.matchMaterial("NETHERITE_SPEAR") == null) {
         player.sendMessage("§cSpear support requires Minecraft Java 1.21.11 or newer.");
         return true;
      }

      this.pendingWeaponProfiles.put(player.getUniqueId(), profile);
      try {
         return this.a(player, a.NORMAL);
      } finally {
         this.pendingWeaponProfiles.remove(player.getUniqueId());
      }
   }

   public void b(Player var1, a var2) {
      if (!this.bj.isLicenseActive()) {
         if (var1 != null) {
            var1.sendMessage(this.bj.licenseLockMessage());
         }
      } else {
         this.a((Callable)(() -> {
            this.c(var1, var2);
            return null;
         }));
      }
   }

   public NPC a(E var1, Location var2, Player var3) {
      return !this.bj.isLicenseActive() ? null : this.a((Callable<NPC>)(() -> this.b(var1, var2, var3)));
   }

   public NPC a(E var1, Location var2, UUID var3) {
      return !this.bj.isLicenseActive() ? null : this.a((Callable<NPC>)(() -> this.b(var1, var2, var3)));
   }

   public NPC a(Player var1) {
      if (!this.bj.isLicenseActive()) {
         return null;
      } else {
         return !this.bj.isCitizensReady() ? null : this.a((Callable<NPC>)(() -> {
            for (NPC var2 : CitizensAPI.getNPCRegistry()) {
               if (var2.hasTrait(BotTrait.class)) {
                  BotTrait var3 = (BotTrait)var2.getTraitNullable(BotTrait.class);
                  if (var3 != null && !var3.isEditorPreview() && var3.isOwner(var1)) {
                     return var2;
                  }
               }
            }

            return null;
         }));
      }
   }

   public void b(NPC var1, BotTrait var2) {
      if (!this.bj.isLicenseActive()) {
         if (var1 != null && var1.isSpawned()) {
            var1.getNavigator().cancelNavigation();
         }
      } else if (var1 != null && var2 != null && var1.isSpawned()) {
         this.f(var1);
         if (var2.isEditorPreview()) {
            FollowTrait var8 = (FollowTrait)var1.getOrAddTrait(FollowTrait.class);
            var8.follow(null);
            var1.getNavigator().cancelNavigation();
            LookClose var10 = (LookClose)var1.getOrAddTrait(LookClose.class);
            var10.lookClose(false);
         } else if (var2.isFrozen()) {
            var2.setFollowOwner(false);
            var2.setRandomWalk(false);
            FollowTrait var7 = (FollowTrait)var1.getOrAddTrait(FollowTrait.class);
            var7.follow(null);
            var1.getNavigator().cancelNavigation();
            LookClose var9 = (LookClose)var1.getOrAddTrait(LookClose.class);
            var9.lookClose(false);
         } else {
            if (var2.isPvpEnabled()) {
               var2.setFollowOwner(false);
               var2.setRandomWalk(false);
            }

            Player var3 = var2.getBehaviorTargetPlayer();
            FollowTrait var4 = (FollowTrait)var1.getOrAddTrait(FollowTrait.class);
            if (var2.isFollowOwner() && var3 != null) {
               var4.follow(var3);

               try {
                  var4.getClass().getMethod("setFollowingMargin", double.class).invoke(var4, 2.0);
               } catch (Exception var6) {
                  var1.getNavigator().getDefaultParameters().distanceMargin(2.0);
               }
            } else {
               var4.follow(null);
               if (!var2.isPvpEnabled() && !var2.isRandomWalk()) {
                  var1.getNavigator().cancelNavigation();
               }
            }

            LookClose var5 = (LookClose)var1.getOrAddTrait(LookClose.class);
            if (var2.isLookAtOwner() && var3 != null && !var2.isPvpEnabled() && !var2.isCpvpEnabled()) {
               var5.lookClose(true);
               var5.setRange(this.g(var1));
               var5.setRealisticLooking(true);
            } else {
               var5.lookClose(false);
            }
         }
      }
   }

   public boolean a(NPC var1, n var2) {
      return this.a((Callable<Boolean>)(() -> this.b(var1, var2)));
   }

   public void a(NPC var1) {
      if (var1 != null) {
         Bukkit.getScheduler().runTask(this.bj, () -> {
            UUID var2 = null;
            if (var1.getEntity() instanceof Player var3) {
               Player var5 = var3.getKiller();
               if (var5 != null) {
                  var2 = var5.getUniqueId();
               }
            }

            this.bj.getApiService().b(var1, var2);
            this.b(var1, n.BOT_DEATH);
         });
      }
   }

   public void a(NPC var1, Player var2) {
      if (var1 != null && var2 != null) {
         this.a((Callable)(() -> {
            this.b(var1, var2);
            return null;
         }));
      }
   }

   public t a(World var1, n var2) {
      return this.a((Callable<t>)(() -> this.b(var1, var2)));
   }

   public q a(String var1, n var2) {
      return this.a((Callable<q>)(() -> this.b(var1, var2)));
   }

   public q a(UUID var1, n var2) {
      return this.a((Callable<q>)(() -> this.b(var1, var2)));
   }

   public boolean a(World var1, String var2, n var3) {
      return this.a((Callable<Boolean>)(() -> this.b(var1, var2, var3)));
   }

   public List<String> a(World var1) {
      return this.a((Callable<List<String>>)(() -> this.b(var1)));
   }

   public void a(n var1) {
      this.a((Callable)(() -> {
         this.b(var1);
         return null;
      }));
   }

   public int I() {
      return this.a(this::M);
   }

   public boolean b(NPC var1) {
      if (var1 == null) {
         return false;
      } else if (var1.hasTrait(BotTrait.class)) {
         return true;
      } else {
         try {
            Object var2 = var1.data().get("practicebot");
            return var2 != null && Boolean.parseBoolean(String.valueOf(var2));
         } catch (Exception var3) {
            return false;
         }
      }
   }

   public boolean c(NPC var1) {
      if (!this.b(var1)) {
         return false;
      } else {
         BotTrait var2 = (BotTrait)var1.getTraitNullable(BotTrait.class);
         if (var2 != null && var2.isEditorPreview()) {
            return true;
         } else {
            try {
               Object var3 = var1.data().get("practicebot-editor-preview");
               return var3 != null && Boolean.parseBoolean(String.valueOf(var3));
            } catch (Exception var4) {
               return false;
            }
         }
      }
   }

   public boolean d(NPC var1) {
      return this.b(var1) && !this.c(var1);
   }

   public List<NPC> J() {
      return this.a(this::N);
   }

   public void K() {
      this.a((Callable)(() -> {
         for (NPC var2 : CitizensAPI.getNPCRegistry()) {
            if (this.b(var2)) {
               this.i(var2);
            }
         }

         return null;
      }));
   }

   public int L() {
      return this.a((Callable<Integer>)(() -> {
         int var1 = 0;

         for (NPC var3 : this.N()) {
            BotTrait var4 = (BotTrait)var3.getTraitNullable(BotTrait.class);
            if (var4 != null && var4.isCpvpEnabled() && var3.getEntity() instanceof Player) {
               var1++;
            }
         }

         return var1;
      }));
   }

   public boolean e(NPC var1) {
      return this.a((Callable<Boolean>)(() -> this.j(var1) != null));
   }

   private void c(Player var1, a var2) {
      if (!this.bj.isCitizensReady()) {
         var1.sendMessage(h.ac("citizens-not-loaded"));
      } else {
         String var3 = var1.getWorld().getName();
         if (this.bk.j(var3)) {
            var1.sendMessage(h.a("errors.world-disabled", "{world}", var3));
         } else {
            if (!this.bk.l()) {
               NPC var4 = this.a(var1);
               if (var4 != null) {
                  this.b(var4, n.REPLACED);
               }
            }

            FileConfiguration var9 = this.bj.getDefaultInvConfig();
            Location var5 = this.a(var1.getLocation());
            p var6 = new p(this.e(var1), var5, this.d(var1), var4x -> {
               var4x.setOwner(var1.getUniqueId());
               var4x.setBoundTarget(var1.getUniqueId());
               var4x.setBotType(var2);
               var4x.setLookAtOwner(this.bk.p());
               var4x.setFollowOwner(this.bk.q());
               var4x.setRandomWalk(this.bk.r());
               var4x.setHoldShield(this.bk.s());
               var4x.setUseShield(this.bk.t());
               var4x.setResistance(false);
               var4x.setShieldInMainHand(false);
               var4x.setGuiEnabled(true);
               var4x.setPvpStrafe(false);
               var4x.setPvpWTap(false);
               var4x.setPvpSTap(false);
               var4x.setPvpCrits(false);
               var4x.setPvpShieldBreaker(false);
               var4x.setPvpRetreat(false);
               var4x.setPvpReachMode(1);
               var4x.setPvpAggression(1);
               var4x.setPvpCritChance(2);
               var4x.setPvpCritSpeed(0);
               var4x.setArmorType(var9.getString("armor-type", "netherite"));
               var4x.setHelmetEnchant(var9.getString("helmet-enchant", "protection"));
               var4x.setChestplateEnchant(var9.getString("chestplate-enchant", "protection"));
               var4x.setLeggingsEnchant(var2 == a.CPVP ? "blast_protection" : var9.getString("leggings-enchant", "protection"));
               var4x.setBootsEnchant(var9.getString("boots-enchant", "protection"));
               var4x.setLeggingsMaterial(var9.getString("leggings-material", "netherite"));
               var4x.setOffhandType(var9.getString("offhand-type", "totem"));
               var4x.setTotemCount(var9.getInt("totem-amount", 64));
               var4x.setCustomMainHand(null);
               String requestedWeapon = this.pendingWeaponProfiles.get(var1.getUniqueId());
               String defaultWeapon = var9.getString("weapon.type", "sword");
               if (var2 == a.CPVP) {
                  var4x.setWeaponProfile("sword");
               } else {
                  var4x.setWeaponProfile(requestedWeapon != null ? requestedWeapon : defaultWeapon);
               }
               var4x.setHelmetTrimPattern(var9.getString("helmet-trim-pattern", ""));
               var4x.setHelmetTrimMaterial(var9.getString("helmet-trim-material", ""));
               var4x.setChestTrimPattern(var9.getString("chest-trim-pattern", ""));
               var4x.setChestTrimMaterial(var9.getString("chest-trim-material", ""));
               var4x.setLegsTrimPattern(var9.getString("legs-trim-pattern", ""));
               var4x.setLegsTrimMaterial(var9.getString("legs-trim-material", ""));
               var4x.setBootsTrimPattern(var9.getString("boots-trim-pattern", ""));
               var4x.setBootsTrimMaterial(var9.getString("boots-trim-material", ""));
               if (var2 == a.CPVP) {
                  var4x.setPvpEnabled(false);
                  var4x.setFollowOwner(false);
                  var4x.setRandomWalk(false);
                  var4x.initializeCpvpSettings(this.bj);
                  var4x.setCpvpEnabled(false);
               } else {
                  var4x.setCpvpEnabled(false);
               }
            }, null, false);
            NPC var7 = this.a(var6);
            if (var7 == null) {
               var1.sendMessage(h.ac("errors.spawn-failed"));
            } else {
               var7.data().remove("practicebot-api-template");
               this.bj.getApiService().y(var7);
               String var8 = var2 == a.CPVP ? h.ac("cpvp-bot-spawned") : h.ac("bot-spawned");
               var1.sendMessage(var8);
               var1.playSound(var1.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.2F);
            }
         }
      }
   }

   private NPC b(E var1, Location var2, Player var3) {
      if (this.bj.isCitizensReady() && var1 != null && var2 != null && var2.getWorld() != null) {
         E var4 = var1.fX();
         UUID var5 = var3 != null ? var3.getUniqueId() : null;
         String var6 = this.a(var4, var3);
         o var7 = this.a(var4, var3, var6);
         r var8 = new r(UUID.randomUUID(), var4, var2.clone(), var5, var6, var7);
         this.bm.put(var8.c1, var8);
         NPC var9 = this.a(var8, false);
         if (var9 == null) {
            this.b(var8, true);
            return null;
         } else {
            this.bj.debugLog(() -> "Spawned bot template '" + var4.fe() + "' as NPC " + var9.getId() + " in world '" + var2.getWorld().getName() + "'.");
            return var9;
         }
      } else {
         return null;
      }
   }

   private NPC b(E var1, Location var2, UUID var3) {
      if (this.bj.isCitizensReady() && var1 != null && var2 != null && var2.getWorld() != null) {
         E var4 = var1.fX();
         String var5 = this.a(var4);
         o var6 = new o("SHELDERA", null, null);
         return this.a(new p(var5, var2.clone(), var6, var3x -> {
            this.a(var3x, var4, null);
            var3x.setOwner(null);
            var3x.setBoundTarget(null);
            var3x.setEditorPreview(true);
            var3x.setEditorSessionOwner(var3);
         }, (var2x, var3x) -> this.a(var2x, var3), true));
      } else {
         return null;
      }
   }

   private NPC a(r var1, boolean var2) {
      E var3 = var1.d0;
      p var4 = new p(var1.e1, var1.d1.clone(), var1.f0, var3x -> this.a(var3x, var3, var1.e0), (var3x, var4x) -> {
         this.a(var1, var3x);
         var3x.data().set("practicebot-api-template", var3.fe());
         this.bj.getApiService().a(var3x, null, var3.fe(), PracticeBotTemplateApplyCause.SPAWN);
         this.bj.getApiService().y(var3x);
      }, false);
      NPC var5 = this.a(var4);
      if (var5 != null && var2) {
         this.bj.debugLog(() -> "Respawned bot template '" + var3.fe() + "' as NPC " + var5.getId() + ".");
      }

      if (var5 != null) {
         this.c(var1);
      }

      return var5;
   }

   private NPC a(p var1) {
      NPC var2 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, this.h(var1.bt(), "PracticeBot"));
      this.h(var2);
      this.i(var2);
      BotTrait var3 = (BotTrait)var2.getOrAddTrait(BotTrait.class);
      if (var3 == null) {
         this.m(var2);
         return null;
      } else {
         try {
            var1.bw().apply(var3);
            if (var1.by()) {
               this.a(var2, var3.getEditorSessionOwnerUUID());
            }

            this.a(var2, var1.bv());
            this.b(var1.bu());
            var2.spawn(var1.bu());
            if (!var2.isSpawned()) {
               this.m(var2);
               return null;
            } else {
               var2.setProtected(var1.by() || this.bk.k());
               var2.data().set("collidable", var1.by() ? false : this.bk.j());
               this.i(var2);
               this.a(var2.getNavigator());
               this.a((FollowTrait)var2.getOrAddTrait(FollowTrait.class));
               this.f(var2);
               if (var2.getEntity() instanceof Player var4) {
                  if (var1.by()) {
                     this.c(var4);
                  } else {
                     this.b(var4);
                  }
               }

               this.a(var2, var1.bu());
               Bukkit.getScheduler().runTaskLater(this.bj, () -> this.a(var2, var1.bu()), 1L);
               if (var1.bx() != null) {
                  var1.bx().apply(var2, var3);
               }

               this.n(var2);
               this.a(var2, var3, 5L);
               this.a(var2, var3, 20L);
               return var2;
            }
         } catch (Exception var6) {
            this.bj.getLogger().warning("Failed to spawn PracticeBot NPC '" + var1.bt() + "': " + var6.getMessage());
            this.m(var2);
            return null;
         }
      }
   }

   private void a(NPC var1, BotTrait var2, long var3) {
      Bukkit.getScheduler().runTaskLater(this.bj, () -> {
         if (var1.isSpawned() && var1.getEntity() instanceof Player var3x) {
            this.i(var1);
            if (var2.isFrozen()) {
               var2.captureFrozenAnchor(var3x.getLocation());
            }

            if (var2.isCpvpEnabled()) {
               this.bj.getCrystalPvpModule().c(var3x, var2);
            } else {
               if (var2.hasStoredInventory()) {
                  this.bj.getCrystalPvpModule().d(var3x, var2);
               }

               this.bl.a(var1, var2);
            }

            this.b(var1, var2);
         }
      }, var3);
   }

   private void a(BotTrait var1, E var2, UUID var3) {
      G var4 = var2.gg();
      H var5 = var2.gh();
      K var6 = var2.gi();
      I var7 = var2.gj();
      boolean var8 = var2.getBotType() == a.CPVP;
      boolean var9 = var2.getBotType() == a.DUMMY;
      var1.setOwner(null);
      M var10 = var2.gb();
      var1.setAutoTargetingEnabled(var10 == M.NONE || var10 == M.BOT);
      var1.setAutoTargetBotsOnly(var10 == M.BOT);
      var1.setBoundTarget(var3);
      var1.setBotType(var2.getBotType());
      var1.setAttackWarmupTicks(var2.gf());
      var1.setLookAtOwner(var8 ? false : var4.go());
      var1.setFollowOwner(var4.gp());
      var1.setRandomWalk(var4.isRandomWalk());
      var1.setHoldShield(var4.isHoldShield());
      var1.setUseShield(var4.isUseShield());
      var1.setResistance(var4.isResistance());
      var1.setFrozen(var9 && var4.isFrozen());
      var1.setShieldInMainHand(var4.isShieldInMainHand());
      var1.setGuiEnabled(false);
      var1.setHelmetMaterial(var6.getHelmetMaterial());
      var1.setChestplateMaterial(var6.getChestplateMaterial());
      var1.setLeggingsMaterial(var6.getLeggingsMaterial());
      var1.setBootsMaterial(var6.getBootsMaterial());
      var1.setHelmetEnchant(var6.getHelmetEnchant());
      var1.setChestplateEnchant(var6.getChestplateEnchant());
      var1.setLeggingsEnchant(var8 ? "blast_protection" : var6.getLeggingsEnchant());
      var1.setBootsEnchant(var6.getBootsEnchant());
      String var11 = var6.getTotemCount() <= 0 ? "none" : var6.getOffhandType();
      var1.setOffhandType(var11);
      var1.setTotemCount(var6.getTotemCount());
      var1.setCustomMainHand(var6.getCustomMainHand());
      var1.setHelmetTrimPattern(var6.gC().gl());
      var1.setHelmetTrimMaterial(var6.gC().gm());
      var1.setChestTrimPattern(var6.gD().gl());
      var1.setChestTrimMaterial(var6.gD().gm());
      var1.setLegsTrimPattern(var6.gE().gl());
      var1.setLegsTrimMaterial(var6.gE().gm());
      var1.setBootsTrimPattern(var6.gF().gl());
      var1.setBootsTrimMaterial(var6.gF().gm());
      var1.setPvpStrafe(var5.gq());
      var1.setPvpWTap(var5.gr());
      var1.setPvpSTap(var5.gs());
      var1.setPvpCrits(var5.gt());
      var1.setPvpShieldBreaker(var5.gu());
      var1.setPvpRetreat(var5.gv());
      var1.setPvpReachMode(var5.gw());
      var1.setPvpAggression(var5.gx());
      var1.setPvpCritChance(var5.gy());
      var1.setPvpCritSpeed(var5.gz());
      var1.initializeCpvpSettings(this.bj);
      ae var12 = var1.getCpvpSettings();
      var12.ax(var7.gB());
      var12.a(var7.aW(), var7.aX());
      var12.b(var7.aZ(), var7.bA());
      var12.e(var7.bJ());
      var12.b(var7.bB());
      var12.c(var7.bC(), var7.bD());
      var12.c(var7.bE());
      var12.d(var7.bF(), var7.bG());
      var12.d(var7.bH());
      var12.b(var7.aI());
      var12.a(var7.aK());
      var12.b(var7.aL());
      var12.c(var7.aM());
      var12.d(var7.aN());
      var12.e(var7.aO());
      var12.f(var7.aP());
      var12.g(var7.aQ());
      var12.c(var7.aR());
      var12.b(var7.aT());
      var12.e(var7.aU());
      var1.setCpvpEnabled(var8);
      if (!var1.isCpvpEnabled()) {
         var1.setPvpEnabled(!var9 && var4.isPvpEnabled());
      }

      if (var1.isFrozen()) {
         var1.setFollowOwner(false);
         var1.setRandomWalk(false);
      }
   }

   private boolean b(NPC var1, n var2) {
      if (var1 != null && this.b(var1)) {
         this.bo.remove(var1.getId());
         r var3 = this.j(var1);
         if (var3 != null) {
            this.bn.remove(var1.getId());
            var3.f1 = null;
         }

         this.bj.getApiService().d(var1, var2);
         this.k(var1);
         this.l(var1);
         this.bj.getApiService().aa(var1);
         if (var3 != null) {
            if (this.a(var3, var2)) {
               this.a(var3);
            } else {
               this.b(var3, true);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void b(NPC var1, Player var2) {
      if (this.b(var1)) {
         r var3 = this.j(var1);
         if (var3 != null) {
            BotTrait var4 = (BotTrait)var1.getTraitNullable(BotTrait.class);
            if (var4 != null && var4.isBoundTarget(var2)) {
               switch (var3.d0.gd()) {
                  case DESPAWN:
                  case RESPAWN:
                     this.b(var1, n.TARGET_KILLED);
                     break;
                  case NONE:
                     if (var4.isAutoTargetingEnabled()) {
                        this.a(var1, var4, null, PracticeBotTargetChangeCause.TEMPLATE_CLEAR, true);
                     }
               }
            }
         }
      }
   }

   private boolean a(r var1, n var2) {
      if (var1 == null) {
         return false;
      } else {
         return switch (var2) {
            case BOT_DEATH -> var1.d0.gc() == J.RESPAWN;
            case TARGET_KILLED -> var1.d0.gd() == L.RESPAWN;
            default -> false;
         };
      }
   }

   private void a(r var1) {
      this.b(var1);
      long var2 = Math.max(0L, var1.d0.ge());
      var1.g0 = Bukkit.getScheduler().runTaskLater(this.bj, () -> {
         var1.g0 = null;
         if (!this.bj.isEnabled() || !this.bj.isCitizensReady()) {
            this.b(var1, false);
         } else if (var1.d1.getWorld() == null) {
            this.bj.getLogger().warning("Skipping respawn for template '" + var1.d0.fe() + "' because its world is no longer available.");
            this.b(var1, false);
         } else {
            NPC var2x = this.a(var1, true);
            if (var2x == null) {
               this.bj.getLogger().warning("Template respawn failed for '" + var1.d0.fe() + "'. Removing runtime controller to avoid retries.");
               this.b(var1, false);
            }
         }
      }, var2);
      this.bj.debugLog(() -> "Scheduled respawn for bot template '" + var1.d0.fe() + "' in " + var2 + " tick(s).");
   }

   private t b(World var1, n var2) {
      if (var1 == null) {
         return new t(0, 0);
      } else {
         int var3 = 0;
         int var4 = 0;
         ArrayList var5 = new ArrayList();

         for (NPC var7 : CitizensAPI.getNPCRegistry()) {
            if (this.d(var7) && var7.isSpawned() && var7.getEntity() != null && var7.getEntity().getWorld().getUID().equals(var1.getUID())) {
               var5.add(var7);
            }
         }

         for (NPC var11 : (java.util.Collection<NPC>)(java.util.Collection<?>) var5) {
            if (this.b(var11, var2)) {
               var3++;
            }
         }

         for (r var8 : new ArrayList<>(this.bm.values())) {
            if (var8.f1 == null && this.a(var8.d1, var1)) {
               if (this.b(var8)) {
                  var4++;
               }

               this.b(var8, false);
            }
         }

         return new t(var3, var4);
      }
   }

   private q b(String var1, n var2) {
      if (var1 != null && !var1.isBlank()) {
         int var3 = 0;
         int var4 = 0;
         String var5 = var1.trim().toLowerCase(Locale.ROOT);
         ArrayList var6 = new ArrayList();

         for (r var8 : new ArrayList<>(this.bm.values())) {
            if (var8.d0.fe().equalsIgnoreCase(var5)) {
               var6.add(var8);
            }
         }

         for (r var11 : (java.util.Collection<r>)(java.util.Collection<?>) var6) {
            if (var11.f1 != null) {
               NPC var9 = CitizensAPI.getNPCRegistry().getById(var11.f1);
               if (var9 != null && this.b(var9, var2)) {
                  var3++;
                  continue;
               }
            }

            if (this.b(var11)) {
               var4++;
            }

            this.b(var11, false);
         }

         return new q(var3, var4);
      } else {
         return new q(0, 0);
      }
   }

   private boolean b(World var1, String var2, n var3) {
      if (var1 != null && var2 != null && !var2.isBlank()) {
         String var4 = var2.trim();

         for (NPC var6 : CitizensAPI.getNPCRegistry()) {
            if (this.d(var6)
               && var6.isSpawned()
               && var6.getEntity() != null
               && var6.getEntity().getWorld().getUID().equals(var1.getUID())
               && var6.getName().equalsIgnoreCase(var4)) {
               return this.b(var6, var3);
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private q b(UUID var1, n var2) {
      if (var1 == null) {
         return new q(0, 0);
      } else {
         int var3 = 0;
         int var4 = 0;
         ArrayList var5 = new ArrayList();

         for (r var7 : new ArrayList<>(this.bm.values())) {
            if (var1.equals(var7.e0)) {
               var5.add(var7);
            }
         }

         for (r var10 : (java.util.Collection<r>)(java.util.Collection<?>) var5) {
            if (var10.f1 != null) {
               NPC var8 = CitizensAPI.getNPCRegistry().getById(var10.f1);
               if (var8 != null && this.b(var8, var2)) {
                  var3++;
                  continue;
               }
            }

            if (this.b(var10)) {
               var4++;
            }

            this.b(var10, false);
         }

         return new q(var3, var4);
      }
   }

   private List<String> b(World var1) {
      if (var1 == null) {
         return List.of();
      } else {
         TreeSet var2 = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

         for (NPC var4 : CitizensAPI.getNPCRegistry()) {
            if (this.d(var4) && var4.isSpawned() && var4.getEntity() != null && var4.getEntity().getWorld().getUID().equals(var1.getUID())) {
               var2.add(var4.getName());
            }
         }

         return new ArrayList<>(var2);
      }
   }

   private void b(n var1) {
      ArrayList var2 = new ArrayList();

      for (NPC var4 : CitizensAPI.getNPCRegistry()) {
         if (this.b(var4)) {
            var2.add(var4);
         }
      }

      for (NPC var8 : (java.util.Collection<NPC>)(java.util.Collection<?>) var2) {
         this.b(var8, var1);
      }

      for (r var5 : new ArrayList<>(this.bm.values())) {
         this.b(var5, true);
      }
   }

   private int M() {
      ArrayList var1 = new ArrayList();

      for (NPC var3 : CitizensAPI.getNPCRegistry()) {
         if (this.b(var3)) {
            var1.add(var3);
         }
      }

      for (NPC var5 : (java.util.Collection<NPC>)(java.util.Collection<?>) var1) {
         this.b(var5, n.STARTUP_CLEANUP);
      }

      this.bm.clear();
      this.bn.clear();
      return var1.size();
   }

   private void a(Navigator var1) {
      var1.getDefaultParameters().useNewPathfinder(true);
      var1.getDefaultParameters().avoidWater(false);
      var1.getDefaultParameters().baseSpeed(1.0F);
      var1.getDefaultParameters().range(128.0F);
      var1.getDefaultParameters().stationaryTicks(20);
      var1.getDefaultParameters().distanceMargin(0.5);
      var1.getDefaultParameters().pathDistanceMargin(0.5);
      var1.getDefaultParameters().stuckAction(null);
   }

   private void f(NPC var1) {
      if (var1 != null && var1.isSpawned() && var1.getEntity() != null) {
         float var2 = (float)Math.max(64.0, Math.min(512.0, this.g(var1)));
         BotTrait var3 = (BotTrait)var1.getTraitNullable(BotTrait.class);
         float var4 = var3 != null && var3.isCpvpEnabled() ? Math.min(128.0F, var2) : var2;
         var1.getNavigator().getDefaultParameters().range(var4);

         try {
            var1.getNavigator().getLocalParameters().range(var4);
         } catch (Exception var6) {
         }

         LookClose var5 = (LookClose)var1.getTraitNullable(LookClose.class);
         if (var5 != null) {
            var5.setRange(var2);
         }
      }
   }

   private double g(NPC var1) {
      return var1 != null && var1.isSpawned() && var1.getEntity() != null ? this.c(var1.getEntity().getWorld()) : 128.0;
   }

   private double c(World var1) {
      int var2 = 8;
      Integer var3 = this.a(var1, "getViewDistance");
      if (var3 != null && var3 > 0) {
         var2 = Math.max(var2, var3);
      }

      Integer var4 = this.a(var1, "getSendViewDistance");
      if (var4 != null && var4 > 0) {
         var2 = Math.max(var2, var4);
      }

      Integer var5 = this.a(Bukkit.getServer(), "getViewDistance");
      if (var5 != null && var5 > 0) {
         var2 = Math.max(var2, var5);
      }

      return Math.max(128.0, var2 * 16.0);
   }

   private Integer a(Object var1, String var2) {
      if (var1 == null) {
         return null;
      } else {
         try {
            Method var3 = var1.getClass().getMethod(var2);
            if (var3.invoke(var1) instanceof Number var5) {
               return var5.intValue();
            }
         } catch (Exception var6) {
         }

         return null;
      }
   }

   private void a(FollowTrait var1) {
      try {
         var1.getClass().getMethod("setFollowingMargin", double.class).invoke(var1, 2.0);
      } catch (Exception var3) {
      }
   }

   private void b(Player var1) {
      var1.setInvulnerable(this.bk.k());
      var1.setCollidable(this.bk.j());
      AttributeInstance var2 = var1.getAttribute(Attribute.GENERIC_MAX_HEALTH);
      if (var2 != null) {
         double var3 = this.bk.i();
         var2.setBaseValue(var3);
         var1.setHealth(var3);
      }

      try {
         AttributeInstance var7 = var1.getAttribute(Attribute.GENERIC_STEP_HEIGHT);
         if (var7 != null) {
            var7.setBaseValue(0.6);
         }
      } catch (Exception var6) {
      }

      try {
         AttributeInstance var8 = var1.getAttribute(Attribute.GENERIC_JUMP_STRENGTH);
         if (var8 != null) {
            var8.setBaseValue(0.42);
         }
      } catch (Exception var5) {
      }
   }

   private void c(Player var1) {
      this.b(var1);
      var1.setInvulnerable(true);
      var1.setCollidable(false);
      var1.setGravity(false);
      var1.setVelocity(new Vector(0, 0, 0));
   }

   private void a(NPC var1, o var2) {
      SkinTrait var3 = (SkinTrait)var1.getOrAddTrait(SkinTrait.class);
      if (var2.Q()) {
         var3.setSkinPersistent(var2.bq(), var2.bs(), var2.br());
      } else {
         var3.setSkinName(var2.bq());
      }

      var3.setShouldUpdateSkins(false);
   }

   private o d(Player var1) {
      String var2 = null;
      String var3 = null;

      try {
         PlayerProfile var4 = var1.getPlayerProfile();

         for (ProfileProperty var6 : var4.getProperties()) {
            if ("textures".equals(var6.getName())) {
               var2 = var6.getValue();
               var3 = var6.getSignature();
               break;
            }
         }
      } catch (Exception var7) {
      }

      return new o(var1.getName(), var2, var3);
   }

   private o a(E var1, Player var2, String var3) {
      if (var1.fZ() != null && !var1.fZ().isBlank()) {
         return new o(var1.fZ().trim(), null, null);
      } else {
         return var2 != null ? this.d(var2) : new o(var3, null, null);
      }
   }

   private Location a(Location var1) {
      Location var2 = var1.clone();
      Vector var3 = var2.getDirection();
      var3.setY(0.0);
      if (var3.lengthSquared() < 1.0E-6) {
         double var4 = Math.toRadians(var2.getYaw());
         var3 = new Vector(-Math.sin(var4), 0.0, Math.cos(var4));
      }

      var3.normalize().multiply(2.0);
      Location var6 = var2.clone().add(var3.getX(), 0.0, var3.getZ());
      this.a(var6, var2);
      return var6;
   }

   private void a(Location var1, Location var2) {
      if (var1 != null && var2 != null && var1.getWorld() != null && var1.getWorld().equals(var2.getWorld())) {
         Vector var3 = var2.toVector().subtract(var1.toVector());
         var3.setY(0.0);
         if (var3.lengthSquared() < 1.0E-6) {
            var1.setYaw(this.a(var2.getYaw() + 180.0F));
            var1.setPitch(0.0F);
         } else {
            var1.setDirection(var3);
            var1.setPitch(0.0F);
         }
      }
   }

   private float a(float var1) {
      var1 %= 360.0F;
      if (var1 <= -180.0F) {
         var1 += 360.0F;
      } else if (var1 > 180.0F) {
         var1 -= 360.0F;
      }

      return var1;
   }

   private void a(NPC var1, Location var2) {
      if (var1 != null && var2 != null && var1.isSpawned() && var1.getEntity() != null) {
         Location var3 = var1.getEntity().getLocation();
         var3.setYaw(var2.getYaw());
         var3.setPitch(var2.getPitch());
         var1.getEntity().teleport(var3);
      }
   }

   private void b(Location var1) {
      if (var1 != null && var1.getWorld() != null) {
         try {
            Chunk var2 = var1.getChunk();
            if (!var2.isLoaded()) {
               var2.load();
            }
         } catch (Exception var3) {
            this.bj
               .getLogger()
               .warning(
                  "Failed to load chunk for PracticeBot spawn at "
                     + var1.getWorld().getName()
                     + " "
                     + var1.getBlockX()
                     + ","
                     + var1.getBlockY()
                     + ","
                     + var1.getBlockZ()
                     + ": "
                     + var3.getMessage()
               );
         }
      }
   }

   private void h(NPC var1) {
      try {
         var1.data().setPersistent(Metadata.SHOULD_SAVE, false);
      } catch (Exception var5) {
         try {
            var1.data().set(Metadata.SHOULD_SAVE, false);
         } catch (Exception var4) {
         }
      }

      var1.data().set("practicebot", "true");
      this.i(var1);
   }

   private void i(NPC var1) {
      if (var1 != null) {
         try {
            var1.data().setPersistent(Metadata.REMOVE_FROM_TABLIST, true);
            var1.data().setPersistent(Metadata.REMOVE_FROM_PLAYERLIST, true);
         } catch (Exception var6) {
            try {
               var1.data().set(Metadata.REMOVE_FROM_TABLIST, true);
               var1.data().set(Metadata.REMOVE_FROM_PLAYERLIST, true);
            } catch (Exception var5) {
            }
         }

         try {
            var1.data().set(Metadata.FORCE_PACKET_UPDATE, true);
         } catch (Exception var4) {
         }
      }
   }

   private void a(NPC var1, UUID var2) {
      var1.data().set("practicebot-editor-preview", "true");
      var1.data().set("practicebot-editor-owner", var2 != null ? var2.toString() : "");
   }

   private void a(r var1, NPC var2) {
      var1.f1 = var2.getId();
      this.bn.put(var2.getId(), var1.c1);
      var2.data().set("practicebot-template-controller", var1.c1.toString());
   }

   private r j(NPC var1) {
      if (var1 == null) {
         return null;
      } else {
         UUID var2 = this.bn.get(var1.getId());
         if (var2 == null) {
            try {
               if (var1.data().get("practicebot-template-controller") instanceof String var4 && !var4.isBlank()) {
                  var2 = UUID.fromString(var4);
               }
            } catch (Exception var5) {
            }
         }

         return var2 != null ? this.bm.get(var2) : null;
      }
   }

   private boolean b(r var1) {
      if (var1.g0 == null) {
         return false;
      } else {
         var1.g0.cancel();
         var1.g0 = null;
         return true;
      }
   }

   private void b(r var1, boolean var2) {
      if (var1 != null) {
         this.d(var1);
         if (var2) {
            this.b(var1);
         }

         if (var1.f1 != null) {
            this.bo.remove(var1.f1);
            this.bn.remove(var1.f1);
            var1.f1 = null;
         }

         this.bm.remove(var1.c1);
      }
   }

   private void k(NPC var1) {
      if (var1 != null && var1.getEntity() instanceof Player var2) {
         this.bj.getCrystalPvpModule().d(var2.getUniqueId());
      }
   }

   private void c(r var1) {
      if (var1 != null) {
         this.d(var1);
         var1.h1 = 24;
         var1.h0 = null;
         var1.g1 = Bukkit.getScheduler().runTaskTimer(this.bj, () -> {
            if (!this.bj.isEnabled() || !this.bj.isCitizensReady()) {
               this.d(var1);
            } else if (var1.f1 == null) {
               this.d(var1);
            } else {
               NPC var2 = CitizensAPI.getNPCRegistry().getById(var1.f1);
               if (var2 != null && var2.isSpawned() && var2.getEntity() instanceof Player var3) {
                  BotTrait var8 = (BotTrait)var2.getTraitNullable(BotTrait.class);
                  if (var8 == null) {
                     this.d(var1);
                  } else {
                     int var5 = var3.getEntityId();
                     this.i(var2);
                     boolean var6 = var1.h0 == null || var1.h0 != var5;
                     boolean var7 = this.a(var3, var8);
                     if (var6 || var7) {
                        if (var8.isCpvpEnabled()) {
                           this.bj.getCrystalPvpModule().c(var3, var8);
                        } else {
                           if (var8.hasStoredInventory()) {
                              this.bj.getCrystalPvpModule().d(var3, var8);
                           }

                           this.bl.a(var2, var8);
                        }

                        this.b(var2, var8);
                     }

                     var1.h0 = var5;
                     if (--var1.h1 <= 0) {
                        this.d(var1);
                     }
                  }
               } else {
                  if (--var1.h1 <= 0) {
                     this.d(var1);
                  }
               }
            }
         }, 1L, 10L);
      }
   }

   private void d(r var1) {
      if (var1 != null && var1.g1 != null) {
         var1.g1.cancel();
         var1.g1 = null;
         var1.h1 = 0;
         var1.h0 = null;
      }
   }

   private boolean a(Player var1, BotTrait var2) {
      if (var1.getInventory().getHelmet() == null || var1.getInventory().getHelmet().getType().isAir()) {
         return true;
      } else if (var1.getInventory().getChestplate() == null || var1.getInventory().getChestplate().getType().isAir()) {
         return true;
      } else if (var1.getInventory().getLeggings() == null || var1.getInventory().getLeggings().getType().isAir()) {
         return true;
      } else if (var1.getInventory().getBoots() == null || var1.getInventory().getBoots().getType().isAir()) {
         return true;
      } else if (var2.isCpvpEnabled()) {
         return var1.getInventory().getItem(0) != null && !var1.getInventory().getItem(0).getType().isAir()
            ? var2.getTotemCount() > 0 && (var1.getInventory().getItemInOffHand() == null || var1.getInventory().getItemInOffHand().getType().isAir())
            : true;
      } else {
         return var1.getInventory().getItemInMainHand() == null || var1.getInventory().getItemInMainHand().getType().isAir();
      }
   }

   private void l(NPC var1) {
      try {
         if (var1.isSpawned()) {
            var1.despawn();
         }
      } catch (Exception var3) {
         this.bj.getLogger().warning("Failed to despawn NPC " + var1.getId() + ": " + var3.getMessage());
      }

      this.m(var1);
   }

   private void m(NPC var1) {
      if (var1 != null) {
         this.bo.remove(var1.getId());
      }

      try {
         CitizensAPI.getNPCRegistry().deregister(var1);
      } catch (Exception var3) {
         this.bj.getLogger().warning("Failed to deregister NPC " + var1.getId() + ": " + var3.getMessage());
      }
   }

   private void n(NPC var1) {
      if (var1 != null) {
         if (this.d(var1) && var1.isSpawned() && var1.getEntity() instanceof Player) {
            this.bo.put(var1.getId(), var1);
         } else {
            this.bo.remove(var1.getId());
         }
      }
   }

   private List<NPC> N() {
      if (this.bo.isEmpty()) {
         return List.of();
      } else {
         ArrayList var1 = new ArrayList(this.bo.size());
         ArrayList var2 = null;

         for (Entry var4 : this.bo.entrySet()) {
            NPC var5 = (NPC)var4.getValue();
            if (var5 != null && this.d(var5) && var5.isSpawned() && var5.getEntity() instanceof Player) {
               var1.add(var5);
            } else {
               if (var2 == null) {
                  var2 = new ArrayList();
               }

               var2.add((Integer)var4.getKey());
            }
         }

         if (var2 != null) {
            for (Integer var7 : (java.util.Collection<Integer>)(java.util.Collection<?>) var2) {
               this.bo.remove(var7);
            }
         }

         return var1;
      }
   }

   private String a(E var1, Player var2) {
      String var3 = var1.fY();
      if (var3 == null || var3.isBlank()) {
         var3 = var1.fh();
      }

      if (var3 == null || var3.isBlank()) {
         var3 = var1.fe();
      }

      String var4 = var2 != null ? var2.getName() : "Bot";
      var3 = var3.replace("{template}", var1.fe()).replace("{target}", var4);
      return this.h(var3, var1.fe());
   }

   private String a(E var1) {
      String var2 = var1.fY();
      if (var2 == null || var2.isBlank()) {
         var2 = var1.fh();
      }

      if (var2 == null || var2.isBlank()) {
         var2 = var1.fe();
      }

      return this.h("Editor:" + var2, "EditorBot");
   }

   private String h(String var1, String var2) {
      String var3 = Y.t1(var1 == null ? "" : var1).trim();
      if (var3.isEmpty()) {
         var3 = Y.t1(var2 == null ? "" : var2).trim();
      }

      if (var3.isEmpty()) {
         var3 = "PracticeBot";
      }

      return var3.length() > 16 ? var3.substring(0, 16) : var3;
   }

   private String e(Player var1) {
      String var2 = this.bk.m();
      if (var2 != null && !var2.isEmpty()) {
         String var3 = Y.t1(var2);
         String var4 = var1.getName();
         int var5 = 16 - var4.length();
         if (var5 <= 0) {
            return var4;
         } else {
            if (var3.length() > var5) {
               var3 = var3.substring(0, var5);
            }

            return var3 + var4;
         }
      } else {
         return var1.getName();
      }
   }

   private boolean a(Location var1, World var2) {
      return var1 != null && var1.getWorld() != null && var2 != null && var1.getWorld().getUID().equals(var2.getUID());
   }

   public String o(NPC var1) {
      return !this.bj.isLicenseActive() ? null : this.a((Callable<String>)(() -> this.q(var1)));
   }

   public boolean a(NPC var1, E var2, UUID var3) {
      return !this.bj.isLicenseActive() ? false : this.a((Callable<Boolean>)(() -> this.b(var1, var2, var3)));
   }

   public boolean p(NPC var1) {
      return !this.bj.isLicenseActive() ? false : this.a((Callable<Boolean>)(() -> var1 != null && this.j(var1) != null));
   }

   public boolean a(NPC var1, UUID var2, PracticeBotTargetChangeCause var3) {
      return !this.bj.isLicenseActive() ? false : this.a((Callable<Boolean>)(() -> {
         BotTrait var4 = var1 == null ? null : (BotTrait)var1.getTraitNullable(BotTrait.class);
         return this.a(var1, var4, var2, var3, true);
      }));
   }

   public boolean c(UUID var1, n var2) {
      return this.a((Callable<Boolean>)(() -> {
         if (var1 == null) {
            return false;
         } else {
            for (NPC var4 : CitizensAPI.getNPCRegistry()) {
               if (var1.equals(var4.getUniqueId()) && this.d(var4)) {
                  this.b(var4, var2 == null ? n.MANUAL : var2);
                  return true;
               }
            }

            return false;
         }
      }));
   }

   private String q(NPC var1) {
      if (var1 != null && this.d(var1)) {
         return var1.data().has("practicebot-api-template") ? (String)var1.data().get("practicebot-api-template") : null;
      } else {
         return null;
      }
   }

   private boolean b(NPC var1, E var2, UUID var3) {
      if (var1 == null || var2 == null || !this.d(var1)) {
         return false;
      } else if (this.j(var1) != null) {
         throw new IllegalStateException("Template-controlled bots cannot be mutated through the public API.");
      } else {
         BotTrait var4 = (BotTrait)var1.getTraitNullable(BotTrait.class);
         if (var4 == null) {
            return false;
         } else {
            String var5 = this.q(var1);
            UUID var6 = var4.getBoundTargetUUID();
            this.a(var4, var2, var3);
            this.bl.a(var1, var4);
            this.f(var1);
            this.b(var1, var4);
            var1.data().set("practicebot-api-template", var2.fe());
            UUID var7 = var4.getBoundTargetUUID();
            if (!Objects.equals(var6, var7)) {
               this.bj
                  .getApiService()
                  .a(var1, var6, var7, var7 == null ? PracticeBotTargetChangeCause.TEMPLATE_CLEAR : PracticeBotTargetChangeCause.TEMPLATE_BIND);
            }

            this.bj.getApiService().a(var1, var5, var2.fe(), PracticeBotTemplateApplyCause.API);
            this.bj.getApiService().z(var1);
            return true;
         }
      }
   }

   private boolean a(NPC var1, BotTrait var2, UUID var3, PracticeBotTargetChangeCause var4, boolean var5) {
      if (var1 != null && var2 != null && this.d(var1)) {
         UUID var6 = var2.getBoundTargetUUID();
         if (Objects.equals(var6, var3)) {
            return false;
         } else {
            var2.setBoundTarget(var3);
            if (var5) {
               this.b(var1, var2);
            }

            this.bj.getApiService().a(var1, var6, var3, var4);
            this.bj.getApiService().z(var1);
            return true;
         }
      } else {
         return false;
      }
   }

   private <T> T a(Callable<T> var1) {
      if (Bukkit.isPrimaryThread()) {
         try {
            return (T)var1.call();
         } catch (Exception var3) {
            throw new RuntimeException(var3);
         }
      } else {
         try {
            Future var2 = Bukkit.getScheduler().callSyncMethod(this.bj, var1);
            return (T)var2.get();
         } catch (InterruptedException var4) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(var4);
         } catch (ExecutionException var5) {
            throw new RuntimeException((Throwable)(var5.getCause() != null ? var5.getCause() : var5));
         }
      }
   }
}
