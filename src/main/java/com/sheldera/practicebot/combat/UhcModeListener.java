package com.sheldera.practicebot.combat;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.x.a;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

/** Applies the no-natural-regeneration rule to UHC bots only. */
public final class UhcModeListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onNaturalRegeneration(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player) ||
            event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) {
            return;
        }

        if (!CitizensAPI.getNPCRegistry().isNPC(player)) {
            return;
        }

        NPC npc = CitizensAPI.getNPCRegistry().getNPC(player);
        if (npc == null || !npc.hasTrait(BotTrait.class)) {
            return;
        }

        BotTrait trait = npc.getTraitNullable(BotTrait.class);
        if (trait != null && trait.getBotType() == a.UHC) {
            event.setCancelled(true);
        }
    }
}
