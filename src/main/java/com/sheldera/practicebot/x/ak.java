package com.sheldera.practicebot.x;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import java.util.UUID;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;

public class ak {
   private final PracticeBotPlugin aX;
   private final av aY;

   public ak(PracticeBotPlugin var1) {
      this.aX = var1;
      this.aY = new av(var1);
   }

   public void a(NPC var1, Player var2, Player var3, BotTrait var4) {
      if (this.aX.isLicenseActive()) {
         this.aY.a(var1, var2, var3, var4);
      }
   }

   public void a(NPC var1, Player var2, Player var3) {
      if (this.aX.isLicenseActive()) {
         BotTrait var4 = var1 == null ? null : (BotTrait)var1.getTraitNullable(BotTrait.class);
         this.aY.a(var1, var2, var3, var4 == null ? null : var4.getCpvpSettings());
      }
   }

   public void b(NPC var1, Player var2, Player var3) {
      if (this.aX.isLicenseActive()) {
         BotTrait var4 = var1 == null ? null : (BotTrait)var1.getTraitNullable(BotTrait.class);
         this.aY.b(var1, var2, var3, var4 == null ? null : var4.getCpvpSettings());
      }
   }

   public void c(Player var1, BotTrait var2) {
      if (this.aX.isLicenseActive()) {
         this.aY.c(var1, var2);
      }
   }

   public void d(Player var1, BotTrait var2) {
      this.aY.d(var1, var2);
   }

   public void d(UUID var1) {
      this.aY.d(var1);
   }
}
