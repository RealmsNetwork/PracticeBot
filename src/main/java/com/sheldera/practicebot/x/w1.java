package com.sheldera.practicebot.x;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.api.PracticeBotType;
import com.sheldera.practicebot.api.bot.PracticeBotLocationSnapshot;
import com.sheldera.practicebot.api.bot.PracticeBotStateSnapshot;
import com.sheldera.practicebot.api.bot.PracticeBotTargetSnapshot;
import com.sheldera.practicebot.api.bot.PracticeBotTargetSnapshot$Kind;
import com.sheldera.practicebot.api.template.BotTemplateView;
import com.sheldera.practicebot.api.template.BotTemplateView$ArmorTrimView;
import com.sheldera.practicebot.api.template.BotTemplateView$BehaviorView;
import com.sheldera.practicebot.api.template.BotTemplateView$CombatView;
import com.sheldera.practicebot.api.template.BotTemplateView$CpvpView;
import com.sheldera.practicebot.api.template.BotTemplateView$DeathMode;
import com.sheldera.practicebot.api.template.BotTemplateView$InventoryView;
import com.sheldera.practicebot.api.template.BotTemplateView$KillMode;
import com.sheldera.practicebot.api.template.BotTemplateView$SkinView;
import com.sheldera.practicebot.api.template.BotTemplateView$TargetBindingMode;
import java.util.UUID;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

final class w1 {
   private w1() {
   }

   static PracticeBotType a(a var0) {
      if (var0 == null) {
         return PracticeBotType.NORMAL;
      } else {
         return switch (var0) {
            case NORMAL -> PracticeBotType.NORMAL;
            case CPVP -> PracticeBotType.CPVP;
            case DUMMY -> PracticeBotType.DUMMY;
         };
      }
   }

   static BotTemplateView b(E var0) {
      if (var0 == null) {
         return null;
      } else {
         H var1 = var0.gh();
         K var2 = var0.gi();
         G var3 = var0.gg();
         I var4 = var0.gj();
         F var5 = var2.gC();
         F var6 = var2.gD();
         F var7 = var2.gE();
         F var8 = var2.gF();
         ItemStack var9 = var2.getCustomMainHand();
         return new BotTemplateView(
            var0.fe(),
            var0.fh(),
            var0.fY(),
            a(var0.getBotType()),
            a(var0.gb()),
            a(var0.gc()),
            a(var0.gd()),
            var0.ge(),
            var0.gf(),
            new BotTemplateView$BehaviorView(
               var0.ga(),
               var3.isPvpEnabled(),
               var3.go(),
               var3.gp(),
               var3.isRandomWalk(),
               var3.isHoldShield(),
               var3.isUseShield(),
               var3.isResistance(),
               var3.isFrozen(),
               var3.isShieldInMainHand()
            ),
            new BotTemplateView$CombatView(var1.gq(), var1.gr(), var1.gs(), var1.gt(), var1.gu(), var1.gv(), var1.gw(), var1.gx(), var1.gy(), var1.gz()),
            new BotTemplateView$InventoryView(
               var2.getHelmetMaterial(),
               var2.getChestplateMaterial(),
               var2.getLeggingsMaterial(),
               var2.getBootsMaterial(),
               var2.getHelmetEnchant(),
               var2.getChestplateEnchant(),
               var2.getLeggingsEnchant(),
               var2.getBootsEnchant(),
               var2.getOffhandType(),
               var2.getTotemCount(),
               var9 == null ? null : var9.getType().name(),
               new BotTemplateView$ArmorTrimView(var5.gl(), var5.gm()),
               new BotTemplateView$ArmorTrimView(var6.gl(), var6.gm()),
               new BotTemplateView$ArmorTrimView(var7.gl(), var7.gm()),
               new BotTemplateView$ArmorTrimView(var8.gl(), var8.gm())
            ),
            new BotTemplateView$CpvpView(
               var4.gA(), var4.gB(), var4.aI(), var4.aK(), var4.aL(), var4.aM(), var4.aN(), var4.aO(), var4.aP(), var4.aQ(), var4.aR(), var4.aS(), var4.aT()
            ),
            new BotTemplateView$SkinView("PLAYER_NAME", var0.fZ(), null, null)
         );
      }
   }

   static PracticeBotStateSnapshot a(x1 var0, NPC var1, BotTrait var2) {
      UUID var3 = var1.isSpawned() && var1.getEntity() != null ? var1.getEntity().getUniqueId() : null;
      Location var4 = x(var1);
      String var5 = var0.fN().getBotManager().o(var1);
      return new PracticeBotStateSnapshot(
         var1.getUniqueId(),
         var3,
         var1.getName(),
         a(var2.getBotType()),
         var1.isSpawned(),
         var1.isSpawned() && var1.getEntity() != null && !var1.getEntity().isDead(),
         var0.d(var1, var2),
         var2.isPvpEnabled(),
         var2.isCpvpEnabled(),
         var2.isCpvpEnabled() && var2.getCpvpSettings().aQ(),
         var2.isCpvpEnabled() && var2.getCpvpSettings().aO(),
         var2.isFrozen(),
         var5 != null,
         var2.isAutoTargetingEnabled(),
         var2.isAutoTargetBotsOnly(),
         var2.getOwnerUUID(),
         var5,
         var4 == null
            ? null
            : new PracticeBotLocationSnapshot(
               var4.getWorld().getUID(), var4.getWorld().getName(), var4.getX(), var4.getY(), var4.getZ(), var4.getYaw(), var4.getPitch()
            ),
         k(var2.getBoundTargetUUID())
      );
   }

   static PracticeBotTargetSnapshot k(@Nullable UUID var0) {
      if (var0 == null) {
         return null;
      } else {
         Entity var1 = Bukkit.getEntity(var0);
         if (var1 != null) {
            NPC var3 = CitizensAPI.getNPCRegistry().getNPC(var1);
            return var3 != null && var3.hasTrait(BotTrait.class)
               ? new PracticeBotTargetSnapshot(var0, var3.getUniqueId(), var3.getName(), PracticeBotTargetSnapshot$Kind.BOT)
               : new PracticeBotTargetSnapshot(var0, null, var1.getName(), PracticeBotTargetSnapshot$Kind.HUMAN);
         } else {
            OfflinePlayer var2 = Bukkit.getOfflinePlayer(var0);
            return new PracticeBotTargetSnapshot(var0, null, var2.getName(), PracticeBotTargetSnapshot$Kind.HUMAN);
         }
      }
   }

   static BotTemplateView$TargetBindingMode a(M var0) {
      if (var0 == null) {
         return BotTemplateView$TargetBindingMode.REQUIRED;
      } else {
         return switch (var0) {
            case REQUIRED -> BotTemplateView$TargetBindingMode.REQUIRED;
            case NONE -> BotTemplateView$TargetBindingMode.NONE;
            case BOT -> BotTemplateView$TargetBindingMode.BOT;
         };
      }
   }

   static BotTemplateView$DeathMode a(J var0) {
      if (var0 == null) {
         return BotTemplateView$DeathMode.REMOVE;
      } else {
         return switch (var0) {
            case REMOVE -> BotTemplateView$DeathMode.REMOVE;
            case RESPAWN -> BotTemplateView$DeathMode.RESPAWN;
         };
      }
   }

   static BotTemplateView$KillMode a(L var0) {
      if (var0 == null) {
         return BotTemplateView$KillMode.DESPAWN;
      } else {
         return switch (var0) {
            case DESPAWN -> BotTemplateView$KillMode.DESPAWN;
            case RESPAWN -> BotTemplateView$KillMode.RESPAWN;
            case NONE -> BotTemplateView$KillMode.NONE;
         };
      }
   }

   private static Location x(NPC var0) {
      Location var1 = var0.getStoredLocation();
      if (var1 != null && var1.getWorld() != null) {
         return var1.clone();
      } else {
         return var0.isSpawned() && var0.getEntity() != null ? var0.getEntity().getLocation().clone() : null;
      }
   }
}
