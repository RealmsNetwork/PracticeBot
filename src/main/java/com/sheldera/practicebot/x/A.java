package com.sheldera.practicebot.x;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class A implements Listener {
   private final PracticeBotPlugin rs;

   public A(PracticeBotPlugin var1) {
      this.rs = var1;
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent var1) {
      if (this.rs.isCitizensReady()) {
         Bukkit.getScheduler().runTaskLater(this.rs, () -> this.rs.getBotManager().K(), 1L);
         Bukkit.getScheduler().runTaskLater(this.rs, () -> this.rs.getBotManager().K(), 20L);
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent var1) {
      if (this.rs.isCitizensReady()) {
         Player var2 = var1.getPlayer();
         NPC var3 = this.rs.getBotManager().a(var2);
         if (var3 != null) {
            this.rs.getBotManager().a(var3, n.OWNER_QUIT);
         }

         this.rs.getBotManager().a(var2.getUniqueId(), n.TARGET_QUIT);
         this.rs.getCrystalPvpModule().d(var2.getUniqueId());
         this.rs.getSpawnCooldown().remove(var2.getUniqueId());
         this.rs.getInventoryClickCooldown().remove(var2.getUniqueId());
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onPlayerInteractEntity(PlayerInteractEntityEvent var1) {
      if (this.rs.isLicenseActive()) {
         if (this.rs.isCitizensReady()) {
            if (CitizensAPI.getNPCRegistry().isNPC(var1.getRightClicked())) {
               NPC var2 = CitizensAPI.getNPCRegistry().getNPC(var1.getRightClicked());
               if (var2 != null && var2.hasTrait(BotTrait.class)) {
                  Player var3 = var1.getPlayer();
                  if (var3.isSneaking()) {
                     try {
                        var3.clearActiveItem();
                     } catch (Exception var5) {
                     }

                     Bukkit.getScheduler().runTask(this.rs, () -> {
                        try {
                           var3.clearActiveItem();
                        } catch (Exception var2x) {
                        }
                     });
                  }
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onNpcRightClick(NPCRightClickEvent var1) {
      if (this.rs.isLicenseActive()) {
         if (this.rs.isCitizensReady()) {
            NPC var2 = var1.getNPC();
            if (var2.hasTrait(BotTrait.class)) {
               Player var3 = var1.getClicker();
               BotTrait var4 = (BotTrait)var2.getTraitNullable(BotTrait.class);
               if (var4 != null) {
                  if (var4.isEditorPreview()) {
                     if (var3.isSneaking()) {
                        var1.setCancelled(true);
                        Bukkit.getScheduler().runTask(this.rs, () -> {
                           try {
                              var3.clearActiveItem();
                           } catch (Exception var2x) {
                           }
                        });
                        this.rs.getTemplateEditorManager().b(var3, var2);
                     }
                  } else if (this.rs.getBotManager().e(var2)) {
                     if (var3.isSneaking()) {
                        var1.setCancelled(true);
                        Bukkit.getScheduler().runTask(this.rs, () -> {
                           try {
                              var3.clearActiveItem();
                           } catch (Exception var2x) {
                           }
                        });
                     }
                  } else if (var3.isSneaking()) {
                     var1.setCancelled(true);
                     Bukkit.getScheduler().runTask(this.rs, () -> {
                        try {
                           var3.clearActiveItem();
                        } catch (Exception var2x) {
                        }
                     });
                     if (!var4.isGuiEnabled()) {
                        var3.sendMessage(h.ac("bot-gui-disabled"));
                     } else {
                        boolean var5 = var4.isOwner(var3);
                        boolean var6 = var3.hasPermission(this.rs.getConfigManager().bl()) || var3.isOp();
                        if (!var5 && !var6) {
                           var3.sendMessage(h.ac("not-your-bot"));
                        } else {
                           if (var4.getBotType() == a.CPVP) {
                              Bukkit.getScheduler().runTask(this.rs, () -> this.rs.getCrystalPvpGui().a(var3, var2, var4));
                           } else {
                              Bukkit.getScheduler().runTask(this.rs, () -> this.rs.getGuiManager().b(var3, var2, var4));
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
      priority = EventPriority.HIGHEST
   )
   public void onPlayerDeath(PlayerDeathEvent var1) {
      if (this.rs.isLicenseActive()) {
         if (this.rs.isCitizensReady()) {
            Player var2 = var1.getEntity();
            if (CitizensAPI.getNPCRegistry().isNPC(var2)) {
               var1.setDeathMessage(null);
               var1.setDroppedExp(0);
               var1.getDrops().clear();
               var1.setKeepInventory(true);
               var1.setKeepLevel(true);
               NPC var6 = CitizensAPI.getNPCRegistry().getNPC(var2);
               if (var6 != null && this.rs.getBotManager().b(var6)) {
                  this.rs.getBotManager().a(var6);
               }

               Player var7 = var2.getKiller();
               if (var7 != null && !CitizensAPI.getNPCRegistry().isNPC(var7)) {
                  this.bi(var7);
               }
            } else {
               Player var3 = var2.getKiller();
               if (var3 != null && CitizensAPI.getNPCRegistry().isNPC(var3)) {
                  NPC var4 = CitizensAPI.getNPCRegistry().getNPC(var3);
                  if (var4 == null || !this.rs.getBotManager().b(var4)) {
                     return;
                  }

                  var1.setDeathMessage(null);
                  BotTrait var5 = (BotTrait)var4.getTraitNullable(BotTrait.class);
                  this.rs.getApiService().c(var4, var2.getUniqueId());
                  if (this.rs.getBotManager().e(var4) && var5 != null && var5.isBoundTarget(var2)) {
                     this.rs.getBotManager().a(var4, var2);
                  }

                  if (this.rs.getConfigManager().n() && var4.hasTrait(BotTrait.class) && var5 != null && var5.isOwner(var2)) {
                     this.rs.getBotManager().a(var4, n.OWNER_DEATH);
                     var2.sendMessage(h.ac("bot-despawned-owner-death"));
                  }

                  this.bj(var2);
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onWorldUnload(WorldUnloadEvent var1) {
      if (this.rs.isLicenseActive()) {
         if (this.rs.isCitizensReady()) {
            this.rs.getBotManager().a(var1.getWorld(), n.WORLD_UNLOAD);
         }
      }
   }

   private void bi(Player var1) {
      Bukkit.getScheduler().runTaskLater(this.rs, () -> {
         this.a(var1, Statistic.PLAYER_KILLS);
         this.a(var1, Statistic.KILL_ENTITY, EntityType.PLAYER);
      }, 1L);
   }

   private void bj(Player var1) {
      Bukkit.getScheduler().runTaskLater(this.rs, () -> {
         this.a(var1, Statistic.DEATHS);
         this.a(var1, Statistic.ENTITY_KILLED_BY, EntityType.PLAYER);
      }, 1L);
   }

   private void a(Player var1, Statistic var2) {
      try {
         int var3 = var1.getStatistic(var2);
         if (var3 > 0) {
            var1.setStatistic(var2, var3 - 1);
         }
      } catch (Exception var4) {
      }
   }

   private void a(Player var1, Statistic var2, EntityType var3) {
      try {
         int var4 = var1.getStatistic(var2, var3);
         if (var4 > 0) {
            var1.setStatistic(var2, var3, var4 - 1);
         }
      } catch (Exception var5) {
      }
   }
}
