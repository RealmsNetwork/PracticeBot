package com.sheldera.practicebot.x;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import com.sheldera.practicebot.api.event.PracticeBotTargetChangeCause;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.FollowTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class u {
   private final PracticeBotPlugin j0;
   private final b j1;
   private final v k0;
   private final y k1;
   private final x l0;

   public u(PracticeBotPlugin var1) {
      this.j0 = var1;
      this.j1 = var1.getConfigManager();
      this.k1 = new y(var1);
      this.k0 = new v(var1, this.k1);
      this.l0 = new x(var1);
   }

   public v Z() {
      return this.k0;
   }

   public y aA() {
      return this.k1;
   }

   public x aB() {
      return this.l0;
   }

   public void aC() {
      if (this.j0.isLicenseActive()) {
         if (this.j0.isCitizensReady()) {
            long var1 = System.currentTimeMillis();

            for (NPC var4 : this.j0.getBotManager().J()) {
               if (var4.hasTrait(BotTrait.class) && var4.isSpawned() && var4.getEntity() instanceof Player var5) {
                  BotTrait var15 = (BotTrait)var4.getTraitNullable(BotTrait.class);
                  if (var15 != null) {
                     if (var15.isEditorPreview()) {
                        var4.getNavigator().cancelNavigation();
                        var5.setVelocity(var5.getVelocity().zero());
                     } else {
                        Player var7 = this.b(var4, var5, var15);
                        if (var15.isFrozen()) {
                           this.a(var4, var5, var7, var15, var1);
                        } else {
                           boolean var8 = x.a(var5);
                           double var9 = var5.getVelocity().getY();
                           double var11 = var5.getLocation().getY();
                           Location var13 = var5.getLocation();
                           var15.isInWater = false;
                           if (this.d(var13)) {
                              this.l0.a(var5, var7, var15, var4);
                           }

                           if (var8) {
                              var15.lastGroundTime = var1;
                              var15.airTicks = 0;
                              var15.lastY = var11;
                              var15.isLaunched = false;
                           } else {
                              var15.airTicks++;
                              var15.lastY = var11;
                           }

                           this.a(var15, var11, var9, var8, var1);
                           if (var15.isPvpSTap()) {
                              if (var15.sTapActive && var1 < var15.sTapEndTime && var15.sTapDirection != null) {
                                 if (var8) {
                                    this.l0.a(var5, var15, var4.getNavigator());
                                 }
                              } else if (var15.sTapActive && var1 >= var15.sTapEndTime) {
                                 var15.sTapActive = false;
                                 var15.sTapDirection = null;
                              }
                           } else {
                              var15.sTapActive = false;
                              var15.sTapDirection = null;
                           }

                           if (var15.shouldRetreat && var1 >= var15.retreatUntil) {
                              var15.endRetreat();
                           }

                           if ((var15.isCpvpEnabled() || var15.isPvpEnabled()) && var15.isAttackWarmupActive(var1)) {
                              if (var15.isCpvpEnabled() && !var15.hasStoredInventory()) {
                                 this.j0.getCrystalPvpModule().c(var5, var15);
                              }

                              var15.isBlocking = false;
                              var15.wasBlocking = false;
                              this.k1.m(var5);
                              var4.getNavigator().cancelNavigation();
                              var5.setSprinting(false);
                           } else {
                              this.k1.a(var4, var5, var15, var7, var1);
                              if (var15.isCpvpEnabled()) {
                                 if (!var15.hasStoredInventory()) {
                                    this.j0.getCrystalPvpModule().c(var5, var15);
                                 }

                                 this.j0.getCrystalPvpModule().a(var4, var5, var7, var15);
                              } else if (var15.isPvpEnabled()) {
                                 if (!var15.isPvpWTap()) {
                                    var5.setSprinting(false);
                                 }

                                 if (var15.wasKnockedBack) {
                                    if (var15.airTicks > 40) {
                                       var15.wasKnockedBack = false;
                                       var15.isLaunched = false;
                                       var15.airTicks = 0;
                                       var5.setGravity(true);
                                    }

                                    boolean var14 = var1 - var15.knockbackTime > 500L;
                                    if (var15.wasKnockedBack && var14 && var9 < 0.05 && x.a(var5)) {
                                       var15.wasKnockedBack = false;
                                       var15.isLaunched = false;
                                       var15.airTicks = 0;
                                       var5.setGravity(true);
                                    }
                                 }

                                 if (var8 && var4.getNavigator().isNavigating() && !this.k0.a(var5, var7, var15, var1)) {
                                    this.l0.d(var5, var7, var15, var1);
                                 }

                                 if (BotTrait.isLiveCombatTarget(var7)) {
                                    this.k0.b(var4, var5, var7, var15, var1);
                                 } else {
                                    var4.getNavigator().cancelNavigation();
                                 }
                              } else if (var15.isFollowOwner()) {
                                 this.a(var4, var5, var7, var15, var8, var1);
                              } else if (var15.isRandomWalk()) {
                                 this.a(var4, var5, var15, var13, var8, var1);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void a(NPC var1, Player var2, Player var3, BotTrait var4, boolean var5, long var6) {
      FollowTrait var8 = (FollowTrait)var1.getTraitNullable(FollowTrait.class);
      if (var4.wasKnockedBack || var4.followDisabledByKnockback) {
         if (!var5) {
            if (var8 != null && var8.getFollowing() != null) {
               var8.follow(null);
               var4.followDisabledByKnockback = true;
            }

            var1.getNavigator().cancelNavigation();
            return;
         }

         if (var4.followDisabledByKnockback && var8 != null && var3 != null) {
            var8.follow(var3);

            try {
               var8.getClass().getMethod("setFollowingMargin", double.class).invoke(var8, 2.0);
            } catch (Exception var10) {
            }
         }

         var4.followDisabledByKnockback = false;
         var4.wasKnockedBack = false;
      }

      if (!var4.followDisabledByKnockback && var8 != null && var3 != null && var8.getFollowing() == null) {
         var8.follow(var3);
      }

      if (var5 && var1.getNavigator().isNavigating()) {
         this.l0.d(var2, var3, var4, var6);
      }
   }

   private void a(NPC var1, Player var2, BotTrait var3, Location var4, boolean var5, long var6) {
      if (var3.wasKnockedBack) {
         if (!var5) {
            var1.getNavigator().cancelNavigation();
            return;
         }

         var3.wasKnockedBack = false;
      }

      FollowTrait var8 = (FollowTrait)var1.getTraitNullable(FollowTrait.class);
      if (var8 != null && var8.getFollowing() != null) {
         var8.follow(null);
      }

      Navigator var9 = var1.getNavigator();
      boolean var10 = !var9.isNavigating() || var6 - var3.lastRandomWalkTime > 5000L;
      if (var10 && var5) {
         Location var11 = this.c(var4);
         if (var11 != null) {
            var9.setTarget(var11);
            var9.getDefaultParameters().distanceMargin(0.75);
            var9.getLocalParameters().speedModifier(0.8F);
            var3.lastRandomWalkTime = var6;
         }
      }

      if (var5 && var9.isNavigating()) {
         this.l0.d(var2, null, var3, var6);
      }
   }

   private Location c(Location var1) {
      if (var1 != null && var1.getWorld() != null) {
         double var2 = Math.max(3.0, this.j1.w());
         ThreadLocalRandom var4 = ThreadLocalRandom.current();

         for (int var5 = 0; var5 < 16; var5++) {
            double var6 = var4.nextDouble(0.0, Math.PI * 2);
            double var8 = var4.nextDouble(1.75, var2 + 0.001);
            Location var10 = var1.clone().add(Math.cos(var6) * var8, 0.0, Math.sin(var6) * var8);
            Location var11 = this.b(var1, var10);
            if (var11 != null && this.c(var1, var11) > 1.44) {
               return var11;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private Location b(Location var1, Location var2) {
      if (var1 != null && var2 != null && var1.getWorld() != null) {
         int var3 = var1.getBlockY();
         int[] var4 = new int[]{0, -1, 1, -2, 2, -3, 3, -4, 4};

         for (int var8 : var4) {
            int var9 = var3 + var8;
            if (this.a(var2, var9)) {
               return new Location(var1.getWorld(), var2.getBlockX() + 0.5, var9, var2.getBlockZ() + 0.5, var1.getYaw(), var1.getPitch());
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private boolean a(Location var1, int var2) {
      if (var1 != null && var1.getWorld() != null) {
         Block var3 = var1.getWorld().getBlockAt(var1.getBlockX(), var2 - 1, var1.getBlockZ());
         Block var4 = var1.getWorld().getBlockAt(var1.getBlockX(), var2, var1.getBlockZ());
         Block var5 = var1.getWorld().getBlockAt(var1.getBlockX(), var2 + 1, var1.getBlockZ());
         return var3.getType().isSolid() && !this.c(var3.getType()) ? this.a(var4) && this.a(var5) : false;
      } else {
         return false;
      }
   }

   private boolean a(Block var1) {
      if (var1 == null) {
         return false;
      } else {
         Material var2 = var1.getType();
         return !var2.isSolid() && var2 != Material.WATER && var2 != Material.LAVA && var2 != Material.POWDER_SNOW;
      }
   }

   private boolean c(Material var1) {
      return var1 == Material.CACTUS
         || var1 == Material.MAGMA_BLOCK
         || var1 == Material.CAMPFIRE
         || var1 == Material.SOUL_CAMPFIRE
         || var1 == Material.FIRE
         || var1 == Material.SOUL_FIRE
         || var1 == Material.LAVA;
   }

   private double c(Location var1, Location var2) {
      double var3 = var1.getX() - var2.getX();
      double var5 = var1.getZ() - var2.getZ();
      return var3 * var3 + var5 * var5;
   }

   private boolean d(Location var1) {
      Block var2 = var1.getBlock();
      Block var3 = var1.clone().add(0.0, 1.0, 0.0).getBlock();
      return var2.getType() == Material.WATER || var3.getType() == Material.WATER;
   }

   private void a(BotTrait var1, double var2, double var4, boolean var6, long var7) {
      if (!var6) {
         if (var4 > 0.0 && var1.critPhase == 0) {
            var1.critPhase = 1;
            var1.peakY = var2;
         } else if (var4 > 0.0) {
            var1.peakY = Math.max(var1.peakY, var2);
         } else if (var4 < -0.1 && var1.critPhase == 1) {
            var1.critPhase = 2;
         }
      }

      if (var1.wasInAir && var6) {
         var1.lastLandingTime = var7;
         var1.critPhase = 0;
         var1.attemptingCrit = false;
         var1.clearCritFallback();
      }

      var1.wasInAir = !var6;
   }

   private void a(NPC var1, Player var2, Player var3, BotTrait var4, long var5) {
      var4.ensureFrozenAnchor(var2.getLocation());
      var4.wasKnockedBack = false;
      var4.inKnockback = false;
      var4.knockbackUntil = 0L;
      var4.followDisabledByKnockback = false;
      var4.sTapActive = false;
      var4.sTapDirection = null;
      var4.shouldRetreat = false;
      var1.getNavigator().cancelNavigation();
      FollowTrait var7 = (FollowTrait)var1.getTraitNullable(FollowTrait.class);
      if (var7 != null && var7.getFollowing() != null) {
         var7.follow(null);
      }

      Location var8 = var4.getFrozenAnchorLocation();
      if (var8 != null && var8.getWorld() != null) {
         Location var9 = var2.getLocation();
         if (!var9.getWorld().getUID().equals(var8.getWorld().getUID()) || var9.distanceSquared(var8) > 4.0E-4) {
            var2.teleport(var8.clone());
         }
      }

      var2.setGravity(false);
      var2.setSprinting(false);
      var2.setVelocity(var2.getVelocity().zero());
      var2.setFireTicks(0);
      var2.setFallDistance(0.0F);
      this.k1.a(var1, var2, var4, var3, var5);
      if (BotTrait.isLiveCombatTarget(var3) && var3.getWorld().equals(var2.getWorld()) && var4.isLookAtOwner()) {
         this.a(var2, var3);
      } else if (var8 != null) {
         var2.setRotation(var8.getYaw(), var8.getPitch());
      }
   }

   private void a(Player var1, Player var2) {
      Location var3 = var1.getEyeLocation();
      Location var4 = var2.getEyeLocation();
      double var5 = var4.getX() - var3.getX();
      double var7 = var4.getY() - var3.getY();
      double var9 = var4.getZ() - var3.getZ();
      double var11 = Math.sqrt(var5 * var5 + var9 * var9);
      if (var11 < 0.001) {
         var11 = 0.001;
      }

      float var13 = (float)Math.toDegrees(Math.atan2(-var5, var9));
      float var14 = (float)(-Math.toDegrees(Math.atan2(var7, var11)));
      var14 = Math.max(-90.0F, Math.min(90.0F, var14));
      var1.setRotation(var13, var14);
   }

   private Player b(NPC var1, Player var2, BotTrait var3) {
      if (!var3.isAutoTargetingEnabled()) {
         return var3.getBehaviorTargetPlayer();
      } else {
         Player var4 = var3.getBoundTargetPlayer();
         if (this.a(var2, var4, var3.isAutoTargetBotsOnly())) {
            return var4;
         } else {
            Player var5 = var3.isAutoTargetBotsOnly() ? this.d(var1, var2) : this.g(var2);
            UUID var6 = var5 != null ? var5.getUniqueId() : null;
            UUID var7 = var3.getBoundTargetUUID();
            if (!Objects.equals(var7, var6)) {
               this.j0.getBotManager().a(var1, var6, var5 == null ? PracticeBotTargetChangeCause.AUTO_CLEAR : PracticeBotTargetChangeCause.AUTO_ACQUIRE);
            }

            return var5;
         }
      }
   }

   private boolean a(Player var1, Player var2, boolean var3) {
      if (!BotTrait.isLiveCombatTarget(var2)) {
         return false;
      } else {
         boolean var4 = var3 ? BotTrait.isLiveBotTarget(var2) : BotTrait.isLiveHumanTarget(var2);
         return var4 && var2.getWorld().getUID().equals(var1.getWorld().getUID());
      }
   }

   private Player g(Player var1) {
      Player var2 = null;
      double var3 = Double.MAX_VALUE;
      Location var5 = var1.getLocation();

      for (Player var7 : var1.getWorld().getPlayers()) {
         if (var7 != null && !var7.getUniqueId().equals(var1.getUniqueId()) && BotTrait.isLiveHumanTarget(var7)) {
            double var8 = var7.getLocation().distanceSquared(var5);
            if (var8 < var3) {
               var3 = var8;
               var2 = var7;
            }
         }
      }

      return var2;
   }

   private Player d(NPC var1, Player var2) {
      Player var3 = null;
      double var4 = Double.MAX_VALUE;
      Location var6 = var2.getLocation();

      for (NPC var8 : this.j0.getBotManager().J()) {
         if (var8 != null && !var8.equals(var1) && var8.isSpawned() && this.j0.getBotManager().d(var8) && var8.getEntity() instanceof Player) {
            Player var9 = (Player)var8.getEntity();
            if (BotTrait.isLiveBotTarget(var9) && var9.getWorld().getUID().equals(var2.getWorld().getUID())) {
               double var10 = var9.getLocation().distanceSquared(var6);
               if (var10 < var4) {
                  var4 = var10;
                  var3 = var9;
               }
            }
         }
      }

      return var3;
   }
}
