package com.sheldera.practicebot.placeholder;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import com.sheldera.practicebot.x.a;
import java.util.Locale;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public final class PracticeBotPlaceholderExpansion extends PlaceholderExpansion {
   private final PracticeBotPlugin plugin;

   public PracticeBotPlaceholderExpansion(PracticeBotPlugin var1) {
      this.plugin = var1;
   }

   public String getIdentifier() {
      return "practicebot";
   }

   public String getAuthor() {
      return String.join(", ", this.plugin.getDescription().getAuthors());
   }

   public String getVersion() {
      return this.plugin.getDescription().getVersion();
   }

   public boolean persist() {
      return true;
   }

   public String onPlaceholderRequest(Player var1, String var2) {
      if (var2 == null) {
         return "";
      } else {
         String var3 = var2.toLowerCase(Locale.ROOT);

         return switch (var3) {
            case "version" -> this.plugin.getDescription().getVersion();
            case "citizens_ready" -> this.bool(this.plugin.isCitizensReady());
            case "template_count" -> String.valueOf(this.plugin.getBotTemplateManager() == null ? 0 : this.plugin.getBotTemplateManager().gG().size());
            case "total_bots" -> String.valueOf(this.countBots(null, false));
            case "active_bots" -> String.valueOf(this.countBots(null, true));
            case "normal_bots" -> String.valueOf(this.countBots(a.NORMAL, false));
            case "cpvp_bots", "crystal_bots" -> String.valueOf(this.countBots(a.CPVP, false));
            case "dummy_bots" -> String.valueOf(this.countBots(a.DUMMY, false));
            case "player_has_bot" -> this.bool(this.findPlayerBot(var1) != null);
            case "player_bot_name" -> this.playerBotName(var1);
            case "player_bot_type" -> this.playerBotType(var1);
            case "player_bot_target" -> this.playerBotTarget(var1);
            case "player_bot_template" -> this.playerBotTemplate(var1);
            case "player_bot_spawned" -> this.bool(this.isPlayerBotSpawned(var1));
            case "player_bot_health" -> this.playerBotHealth(var1);
            case "player_bot_world" -> this.playerBotWorld(var1);
            default -> null;
         };
      }
   }

   private int countBots(a var1, boolean var2) {
      if (this.plugin.isCitizensReady() && this.plugin.getBotManager() != null) {
         int var3 = 0;

         for (NPC var5 : CitizensAPI.getNPCRegistry()) {
            if (this.plugin.getBotManager().d(var5) && (!var2 || var5.isSpawned())) {
               BotTrait var6 = (BotTrait)var5.getTraitNullable(BotTrait.class);
               if (var6 != null && (var1 == null || var6.getBotType() == var1)) {
                  var3++;
               }
            }
         }

         return var3;
      } else {
         return 0;
      }
   }

   private NPC findPlayerBot(Player var1) {
      if (var1 != null && this.plugin.isCitizensReady() && this.plugin.getBotManager() != null) {
         for (NPC var3 : CitizensAPI.getNPCRegistry()) {
            if (this.plugin.getBotManager().d(var3)) {
               BotTrait var4 = (BotTrait)var3.getTraitNullable(BotTrait.class);
               if (var4 != null && var4.isOwner(var1)) {
                  return var3;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private String playerBotName(Player var1) {
      NPC var2 = this.findPlayerBot(var1);
      return var2 == null ? "None" : var2.getName();
   }

   private String playerBotType(Player var1) {
      NPC var2 = this.findPlayerBot(var1);
      if (var2 == null) {
         return "NONE";
      } else {
         BotTrait var3 = (BotTrait)var2.getTraitNullable(BotTrait.class);
         return var3 == null ? "UNKNOWN" : var3.getBotType().name();
      }
   }

   private String playerBotTarget(Player var1) {
      NPC var2 = this.findPlayerBot(var1);
      if (var2 == null) {
         return "None";
      } else {
         BotTrait var3 = (BotTrait)var2.getTraitNullable(BotTrait.class);
         Player var4 = var3 == null ? null : var3.getBoundTargetPlayer();
         return var4 == null ? "None" : var4.getName();
      }
   }

   private String playerBotTemplate(Player var1) {
      NPC var2 = this.findPlayerBot(var1);
      if (var2 != null && this.plugin.getBotManager() != null) {
         String var3 = this.plugin.getBotManager().o(var2);
         return var3 != null && !var3.isBlank() ? var3 : "None";
      } else {
         return "None";
      }
   }

   private boolean isPlayerBotSpawned(Player var1) {
      NPC var2 = this.findPlayerBot(var1);
      return var2 != null && var2.isSpawned();
   }

   private String playerBotHealth(Player var1) {
      NPC var2 = this.findPlayerBot(var1);
      return var2 != null && var2.isSpawned() && var2.getEntity() instanceof LivingEntity var3
         ? String.format(Locale.ROOT, "%.1f", Math.max(0.0, var3.getHealth()))
         : "0";
   }

   private String playerBotWorld(Player var1) {
      NPC var2 = this.findPlayerBot(var1);
      if (var2 != null && var2.isSpawned() && var2.getEntity() != null) {
         World var3 = var2.getEntity().getWorld();
         return var3 == null ? "None" : var3.getName();
      } else {
         return "None";
      }
   }

   private String bool(boolean var1) {
      return var1 ? "true" : "false";
   }
}
