package com.sheldera.practicebot.x;

import com.sheldera.practicebot.api.bot.PracticeBotHandle;
import com.sheldera.practicebot.api.bot.PracticeBotStateSnapshot;
import com.sheldera.practicebot.api.bot.PracticeBotTargetSnapshot;
import com.sheldera.practicebot.api.cpvp.CpvpSettingsUpdateRequest;
import com.sheldera.practicebot.api.event.PracticeBotTargetChangeCause;
import com.sheldera.practicebot.api.metadata.PracticeBotMetadataType;
import com.sheldera.practicebot.api.metadata.PracticeBotMetadataValue;
import com.sheldera.practicebot.api.template.BotTemplateView;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.NamespacedKey;

final class w0 implements PracticeBotHandle {
   private final x1 rc;
   private final UUID rd;

   w0(x1 var1, UUID var2) {
      this.rc = Objects.requireNonNull(var1, "service");
      this.rd = Objects.requireNonNull(var2, "npcUuid");
   }

   @Override
   public UUID getNpcUuid() {
      this.rc.fQ();
      return this.rd;
   }

   @Override
   public Optional<UUID> getEntityUuid() {
      this.rc.fQ();
      return this.getStateSnapshot().entityUuidOptional();
   }

   @Override
   public boolean isActive() {
      this.rc.fS();
      this.rc.fQ();

      try {
         this.rc.m(this.rd);
         return true;
      } catch (RuntimeException var2) {
         return false;
      }
   }

   @Override
   public PracticeBotStateSnapshot getStateSnapshot() {
      this.rc.fQ();
      return this.rc.ac(this.fM());
   }

   @Override
   public Optional<PracticeBotTargetSnapshot> getCurrentTarget() {
      this.rc.fQ();
      return this.getStateSnapshot().targetOptional();
   }

   @Override
   public Optional<BotTemplateView> getCurrentTemplate() {
      this.rc.fQ();
      return this.getStateSnapshot().templateKeyOptional().flatMap(this.rc::getTemplate);
   }

   @Override
   public void despawn() {
      this.rc.fS();
      this.rc.fQ();
      this.rc.fN().getBotManager().c(this.rd, n.MANUAL);
   }

   @Override
   public void setTarget(UUID var1) {
      this.rc.fS();
      this.rc.fQ();
      NPC var2 = this.fM();
      if (this.rc.fN().getBotManager().p(var2)) {
         throw new IllegalStateException("Template-controlled bots do not allow explicit target mutation through the public API.");
      } else if (var1 == null) {
         throw new IllegalArgumentException("targetEntityUuid cannot be null. Use clearTarget() instead.");
      } else {
         UUID var3 = this.rc.o(var1);
         boolean var4 = this.rc.fN().getBotManager().a(var2, var3, PracticeBotTargetChangeCause.API);
         if (!var4) {
            throw new IllegalStateException("PracticeBot rejected the target update.");
         }
      }
   }

   @Override
   public void clearTarget() {
      this.rc.fS();
      this.rc.fQ();
      NPC var1 = this.fM();
      if (this.rc.fN().getBotManager().p(var1)) {
         throw new IllegalStateException("Template-controlled bots do not allow explicit target mutation through the public API.");
      } else {
         this.rc.fN().getBotManager().a(var1, null, PracticeBotTargetChangeCause.API);
      }
   }

   @Override
   public PracticeBotHandle updateCpvpSettings(CpvpSettingsUpdateRequest var1) {
      this.rc.fQ();
      return this.rc.updateCpvpSettings(this.rd, var1);
   }

   @Override
   public void setMetadata(NamespacedKey var1, PracticeBotMetadataType var2, Object var3) {
      this.rc.fS();
      this.rc.fQ();
      Objects.requireNonNull(var1, "key");
      Objects.requireNonNull(var2, "type");
      Objects.requireNonNull(var3, "value");
      this.rc.a(this.rd, var1, var2, var3);
   }

   @Override
   public Optional<PracticeBotMetadataValue> getMetadata(NamespacedKey var1) {
      this.rc.fQ();
      Objects.requireNonNull(var1, "key");
      return Optional.ofNullable(this.rc.a(this.rd, var1));
   }

   @Override
   public Map<NamespacedKey, PracticeBotMetadataValue> getMetadataSnapshot() {
      this.rc.fQ();
      return this.rc.l(this.rd);
   }

   @Override
   public boolean hasMetadata(NamespacedKey var1) {
      this.rc.fQ();
      Objects.requireNonNull(var1, "key");
      return this.rc.a(this.rd, var1) != null;
   }

   @Override
   public void removeMetadata(NamespacedKey var1) {
      this.rc.fS();
      this.rc.fQ();
      Objects.requireNonNull(var1, "key");
      this.rc.b(this.rd, var1);
   }

   private NPC fM() {
      this.rc.fQ();
      return this.rc.m(this.rd);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof w0 var2 ? this.rd.equals(var2.rd) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.rd.hashCode();
   }

   @Override
   public String toString() {
      return "ApiPracticeBotHandle{npcUuid=" + this.rd + "}";
   }
}
