package com.sheldera.practicebot.x;

import com.sheldera.practicebot.PracticeBotPlugin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class ad implements CommandExecutor, TabCompleter {
   private final PracticeBotPlugin plugin;

   public ad(PracticeBotPlugin plugin) {
      this.plugin = plugin;
   }

   private boolean allowed(CommandSender sender, String permission) {
      return sender.hasPermission(permission) || sender.hasPermission(plugin.getConfigManager().bm());
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player player)) {
         sender.sendMessage(h.ad("player-only"));
         return true;
      }
      if (!plugin.isLicenseActive()) {
         player.sendMessage(plugin.licenseLockMessage());
         return true;
      }
      if (!plugin.isCitizensReady()) {
         player.sendMessage(h.ac("citizens-not-loaded"));
         return true;
      }
      if (args.length == 0) {
         if (!hasAnySpawnPermission(player)) {
            player.sendMessage(h.ac("no-permission"));
            return true;
         }
         plugin.getGuiManager().bf(player);
         return true;
      }

      String type = args[0].trim().toLowerCase(Locale.ROOT);
      String permission;
      String profile = null;
      a botType = null;
      switch (type) {
         case "normal" -> { permission = "practicebot.spawn.normal"; botType = a.NORMAL; }
         case "crystal", "cpvp" -> { permission = "practicebot.spawn.cpvp"; botType = a.CRYSTAL; }
         case "cart" -> { permission = "practicebot.spawn.cart"; profile = "cart"; }
         case "cartelytra", "cart_elytra" -> { permission = "practicebot.spawn.cart"; profile = "cart_elytra"; }
         case "mace" -> { permission = "practicebot.spawn.mace"; profile = "mace"; }
         case "maceelytra", "mace_elytra" -> { permission = "practicebot.spawn.mace"; profile = "mace_elytra"; }
         case "spear" -> { permission = "practicebot.spawn.mace"; profile = "spear"; }
         case "spearelytra", "spear_elytra" -> { permission = "practicebot.spawn.mace"; profile = "spear_elytra"; }
         case "spearmace", "spear_mace" -> { permission = "practicebot.spawn.mace"; profile = "spear_mace"; }
         case "bow" -> { permission = "practicebot.spawn.uhc"; profile = "bow"; }
         case "uhc" -> { permission = "practicebot.spawn.uhc"; profile = "uhc"; }
         case "water" -> { permission = "practicebot.spawn.water"; profile = "water"; }
         default -> { player.sendMessage(h.ac("invalid-usage.spawnbot")); return true; }
      }
      if (!allowed(player, permission)) {
         player.sendMessage(h.ac("no-permission"));
         return true;
      }
      return botType != null
         ? plugin.getBotManager().a(player, botType)
         : plugin.getBotManager().a(player, profile);
   }

   private boolean hasAnySpawnPermission(CommandSender sender) {
      return allowed(sender, "practicebot.spawn.normal") ||
         allowed(sender, "practicebot.spawn.cpvp") || allowed(sender, "practicebot.spawn.crystal") ||
         allowed(sender, "practicebot.spawn.cart") || allowed(sender, "practicebot.spawn.mace") ||
         allowed(sender, "practicebot.spawn.uhc") || allowed(sender, "practicebot.spawn.water");
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (args.length != 1 || !plugin.isLicenseActive()) return Collections.emptyList();
      String prefix = args[0].toLowerCase(Locale.ROOT);
      List<String> choices = new ArrayList<>();
      add(choices, sender, "normal", prefix, "practicebot.spawn.normal");
      add(choices, sender, "crystal", prefix, "practicebot.spawn.cpvp");
      add(choices, sender, "cart", prefix, "practicebot.spawn.cart");
      add(choices, sender, "cartelytra", prefix, "practicebot.spawn.cart");
      add(choices, sender, "mace", prefix, "practicebot.spawn.mace");
      add(choices, sender, "maceelytra", prefix, "practicebot.spawn.mace");
      add(choices, sender, "spear", prefix, "practicebot.spawn.mace");
      add(choices, sender, "spearelytra", prefix, "practicebot.spawn.mace");
      add(choices, sender, "spearmace", prefix, "practicebot.spawn.mace");
      add(choices, sender, "bow", prefix, "practicebot.spawn.uhc");
      add(choices, sender, "uhc", prefix, "practicebot.spawn.uhc");
      add(choices, sender, "water", prefix, "practicebot.spawn.water");
      return choices;
   }

   private void add(List<String> result, CommandSender sender, String value, String prefix, String permission) {
      if (value.startsWith(prefix) && allowed(sender, permission)) result.add(value);
   }
}
