package com.sheldera.practicebot.x;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class z1 implements Listener {
   private final PracticeBotPlugin rr;

   public z1(PracticeBotPlugin var1) {
      this.rr = var1;
   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent var1) {
      if (this.rr.isLicenseActive()) {
         if (this.rr.isCitizensReady()) {
            Inventory var2 = var1.getInventory();
            InventoryHolder var3 = var2.getHolder();
            if (var1.getPlayer() instanceof Player var4) {
               Bukkit.getScheduler().runTask(this.rr, () -> {
                  try {
                     var4.clearActiveItem();
                  } catch (Exception var2x) {
                  }
               });
               if (var3 instanceof u0) {
                  ai var6 = this.rr.getCrystalPvpGui();
                  if (var6 != null) {
                     var6.c(var4.getUniqueId());
                  }
               }
            }

            if (var3 instanceof t1) {
               this.rr.saveDefaultInventoryConfig();
               this.rr.getGuiManager().f((Player)var1.getPlayer(), this.rr.getConfigManager().bs());
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onEntityResurrect(EntityResurrectEvent var1) {
      if (this.rr.isLicenseActive()) {
         if (this.rr.isCitizensReady()) {
            if (!var1.isCancelled()) {
               if (CitizensAPI.getNPCRegistry().isNPC(var1.getEntity())) {
                  NPC var2 = CitizensAPI.getNPCRegistry().getNPC(var1.getEntity());
                  if (var2 != null && var2.hasTrait(BotTrait.class)) {
                     BotTrait var3 = (BotTrait)var2.getTraitNullable(BotTrait.class);
                     if (var3 != null) {
                        if (var3.isEditorPreview()) {
                           var1.setCancelled(true);
                        } else if (var2.getEntity() instanceof Player var4) {
                           var3.decrementTotemCount();
                           int var6 = var3.getTotemCount();
                           if (var6 > 0) {
                              Bukkit.getScheduler().runTaskLater(this.rr, () -> {
                                 if (var4.isValid() && var2.isSpawned()) {
                                    var4.getInventory().setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING));
                                 }
                              }, 1L);
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
