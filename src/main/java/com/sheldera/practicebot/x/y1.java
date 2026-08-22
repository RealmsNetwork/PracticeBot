package com.sheldera.practicebot.x;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import com.sheldera.practicebot.api.event.PracticeBotTargetChangeCause;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class y1 implements Listener {
   private final PracticeBotPlugin rp;

   public y1(PracticeBotPlugin var1) {
      this.rp = var1;
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onBotAnyDamage(EntityDamageEvent var1) {
      if (this.rp.isLicenseActive()) {
         if (this.rp.isCitizensReady()) {
            if (CitizensAPI.getNPCRegistry().isNPC(var1.getEntity())) {
               NPC var2 = CitizensAPI.getNPCRegistry().getNPC(var1.getEntity());
               if (var2 != null && var2.hasTrait(BotTrait.class)) {
                  BotTrait var3 = (BotTrait)var2.getTraitNullable(BotTrait.class);
                  if (var3 != null) {
                     if (var2.getEntity() instanceof Player var4) {
                        if (var3.isEditorPreview()) {
                           var1.setCancelled(true);
                        } else {
                           long var14 = System.currentTimeMillis();
                           DamageCause var7 = var1.getCause();
                           if (!var3.isFrozen()
                              && (var7 == DamageCause.ENTITY_EXPLOSION || var7 == DamageCause.BLOCK_EXPLOSION || var7 == DamageCause.SONIC_BOOM)) {
                              var3.wasKnockedBack = true;
                              var3.knockbackTime = var14;
                              var2.getNavigator().cancelNavigation();
                              this.a(var2, var4, var3, null);
                           }

                           if (var3.isResistance()) {
                              double var8 = var4.getHealth();
                              double var10 = var1.getFinalDamage();
                              if (var8 - var10 <= 0.0) {
                                 double var12 = var8 - 1.0;
                                 if (var12 < 0.0) {
                                    var12 = 0.0;
                                 }

                                 var1.setDamage(var12);
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

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onBotDamaged(EntityDamageByEntityEvent var1) {
      if (this.rp.isLicenseActive()) {
         if (this.rp.isCitizensReady()) {
            if (CitizensAPI.getNPCRegistry().isNPC(var1.getDamager())) {
               NPC var2 = CitizensAPI.getNPCRegistry().getNPC(var1.getDamager());
               if (var2 != null && var2.hasTrait(BotTrait.class)) {
                  BotTrait var3 = (BotTrait)var2.getTraitNullable(BotTrait.class);
                  if (var3 != null && var3.isEditorPreview()) {
                     var1.setCancelled(true);
                     return;
                  }
               }
            }

            if (this.rp.isCitizensReady()) {
               if (CitizensAPI.getNPCRegistry().isNPC(var1.getEntity())) {
                  NPC var16 = CitizensAPI.getNPCRegistry().getNPC(var1.getEntity());
                  if (var16 != null && var16.hasTrait(BotTrait.class)) {
                     BotTrait var17 = (BotTrait)var16.getTraitNullable(BotTrait.class);
                     if (var17 != null) {
                        if (var16.getEntity() instanceof Player var4) {
                           if (var17.isEditorPreview()) {
                              var1.setCancelled(true);
                           } else {
                              long var18 = System.currentTimeMillis();
                              String var7 = var1.getDamager().getType().name().toLowerCase();
                              if (!var17.isFrozen()
                                 && (
                                    var7.contains("wind")
                                       || var7.contains("breeze")
                                       || var1.getCause() == DamageCause.ENTITY_EXPLOSION
                                       || var1.getCause() == DamageCause.PROJECTILE
                                 )) {
                                 var17.wasKnockedBack = true;
                                 var17.knockbackTime = var18;
                                 var16.getNavigator().cancelNavigation();
                              }

                              Player var8 = var1.getDamager() instanceof Player var9 ? var9 : null;
                              if (var8 != null) {
                                 var17.lastHitByPlayerTime = var18;
                                 var17.registerHitTaken(var18);
                                 if (BotTrait.isLiveHumanTarget(var8)) {
                                    this.rp.addCombatTag(var8);
                                 }

                                 boolean var19 = var17.isAutoTargetBotsOnly()
                                    ? BotTrait.isLiveBotTarget(var8) && this.rp.getBotManager().d(CitizensAPI.getNPCRegistry().getNPC(var8))
                                    : BotTrait.isLiveHumanTarget(var8);
                                 if (var17.isAutoTargetingEnabled() && this.a(var1) && var19 && !var17.isBoundTarget(var8)) {
                                    this.rp.getBotManager().a(var16, var8.getUniqueId(), PracticeBotTargetChangeCause.DAMAGED_BY_TARGET);
                                 }

                                 if (var17.isUseShield() && !var17.isShieldDisabled(var18)) {
                                    var17.reactiveBlockTriggered = true;
                                    var17.reactiveBlockUntil = var18 + 500L;
                                 }
                              }

                              if (var17.isFrozen()) {
                                 var17.wasKnockedBack = false;
                                 var17.inKnockback = false;
                                 var17.knockbackUntil = 0L;
                                 var17.lastHitTime = var18;
                                 var17.isBlocking = false;
                                 var17.wasBlocking = false;
                                 var16.getNavigator().cancelNavigation();
                              } else {
                                 var17.wasKnockedBack = true;
                                 var17.knockbackTime = var18;
                                 var17.setKnockback(150L);
                                 this.a(var16, var4, var17, var8);
                                 ItemStack var20 = var4.getInventory().getItemInOffHand();
                                 ItemStack var21 = var4.getInventory().getItemInMainHand();
                                 boolean var11 = var20 != null && var20.getType() == Material.SHIELD || var21 != null && var21.getType() == Material.SHIELD;
                                 boolean var12 = var17.isUseShield() && var11 && !var17.isShieldDisabled(var18) && var17.isBlocking;
                                 boolean var13 = true;
                                 if (var1.getDamager() instanceof LivingEntity var14) {
                                    var13 = this.rp.getBotTickHandler().aA().a(var4, var14);
                                 }

                                 boolean var22 = false;
                                 if (var8 != null) {
                                    ItemStack var23 = var8.getInventory().getItemInMainHand();
                                    if (var23 != null && var23.getType().name().endsWith("_AXE")) {
                                       var22 = true;
                                    }
                                 }

                                 if (!var12 || !var13) {
                                    var17.lastHitTime = var18;
                                    var17.isBlocking = false;
                                    var17.wasBlocking = false;
                                 } else if (var22) {
                                    var17.shieldDisabledUntil = var18 + this.rp.getConfigManager().bi();
                                    var17.isBlocking = false;
                                    var17.wasBlocking = false;
                                    this.rp.getBotTickHandler().aA().m(var4);
                                    var4.getWorld().playSound(var4.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0F, 1.0F);
                                    var1.setCancelled(true);
                                 } else {
                                    var4.getWorld().playSound(var4.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F);
                                    var1.setCancelled(true);
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
   }

   private void a(NPC var1, Player var2, BotTrait var3, Player var4) {
      if (var3.isCpvpEnabled()) {
         Player var6 = var3.getBehaviorTargetPlayer();
         if (!BotTrait.isLiveCombatTarget(var6)) {
            var6 = var4;
         }

         this.rp.getCrystalPvpModule().b(var1, var2, var6);
      } else {
         if (var3.isPvpEnabled()) {
            Player var5 = var3.getBehaviorTargetPlayer();
            if (!BotTrait.isLiveCombatTarget(var5)) {
               var5 = var4;
            }

            this.rp.getBotTickHandler().Z().a(var1, var2, var5);
         }
      }
   }

   private boolean a(EntityDamageByEntityEvent var1) {
      return switch (var1.getCause()) {
         case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> var1.getDamager() instanceof Player;
         default -> false;
      };
   }
}
