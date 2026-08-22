package com.sheldera.practicebot.x;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class i implements Listener {
   private final PracticeBotPlugin av;

   public i(PracticeBotPlugin var1) {
      this.av = var1;
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onEntityKnockback(EntityKnockbackByEntityEvent var1) {
      if (this.av.isLicenseActive()) {
         if (this.av.isCitizensReady()) {
            if (CitizensAPI.getNPCRegistry().isNPC(var1.getEntity())) {
               NPC var2 = CitizensAPI.getNPCRegistry().getNPC(var1.getEntity());
               if (var2 != null && var2.hasTrait(BotTrait.class)) {
                  BotTrait var3 = (BotTrait)var2.getTraitNullable(BotTrait.class);
                  if (var3 != null) {
                     if (var3.isEditorPreview()) {
                        var1.setCancelled(true);
                     } else if (var3.isFrozen()) {
                        var3.clearKnockbackState();
                        var3.inKnockback = false;
                        var3.knockbackUntil = 0L;
                        var2.getNavigator().cancelNavigation();
                        var1.setCancelled(true);
                     } else {
                        long var4 = System.currentTimeMillis();
                        var3.wasKnockedBack = true;
                        var3.knockbackTime = var4;
                        var3.setKnockback(300L);
                        var2.getNavigator().cancelNavigation();
                        if (var2.getEntity() instanceof Player var6) {
                           this.a(var2, var6, var3);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void a(NPC var1, Player var2, BotTrait var3) {
      if (var3.isCpvpEnabled()) {
         this.av.getCrystalPvpModule().b(var1, var2, var3.getBehaviorTargetPlayer());
      } else {
         if (var3.isPvpEnabled()) {
            this.av.getBotTickHandler().Z().a(var1, var2, var3.getBehaviorTargetPlayer());
         }
      }
   }
}
