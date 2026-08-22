package com.sheldera.practicebot.x;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import com.sheldera.practicebot.api.PracticeBotApi;
import com.sheldera.practicebot.api.bot.PracticeBotHandle;
import com.sheldera.practicebot.api.bot.PracticeBotStateSnapshot;
import com.sheldera.practicebot.api.bot.PracticeBotTargetSnapshot;
import com.sheldera.practicebot.api.cpvp.CpvpSettingsUpdateRequest;
import com.sheldera.practicebot.api.event.PracticeBotCombatStartEvent;
import com.sheldera.practicebot.api.event.PracticeBotCombatStopEvent;
import com.sheldera.practicebot.api.event.PracticeBotDeathEvent;
import com.sheldera.practicebot.api.event.PracticeBotDespawnEvent;
import com.sheldera.practicebot.api.event.PracticeBotKillEvent;
import com.sheldera.practicebot.api.event.PracticeBotRemovalReason;
import com.sheldera.practicebot.api.event.PracticeBotSpawnEvent;
import com.sheldera.practicebot.api.event.PracticeBotTargetChangeCause;
import com.sheldera.practicebot.api.event.PracticeBotTargetChangeEvent;
import com.sheldera.practicebot.api.event.PracticeBotTemplateApplyCause;
import com.sheldera.practicebot.api.event.PracticeBotTemplateApplyEvent;
import com.sheldera.practicebot.api.metadata.PracticeBotMetadataType;
import com.sheldera.practicebot.api.metadata.PracticeBotMetadataValue;
import com.sheldera.practicebot.api.spawn.BotSpawnRequest;
import com.sheldera.practicebot.api.template.BotTemplateView;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;

public final class x1 implements PracticeBotApi {
   private static final String ri = "1.6.0";
   private final PracticeBotPlugin rj;
   private volatile boolean rk = false;
   private final Map<UUID, ConcurrentHashMap<NamespacedKey, PracticeBotMetadataValue>> rl = new ConcurrentHashMap<>();
   private final Map<UUID, Boolean> rm = new ConcurrentHashMap<>();
   private final Map<UUID, PracticeBotTargetSnapshot> rn = new ConcurrentHashMap<>();

   public x1(PracticeBotPlugin var1) {
      this.rj = Objects.requireNonNull(var1, "plugin");
   }

   PracticeBotPlugin fN() {
      return this.rj;
   }

   public void fO() {
      if (!this.rk) {
         this.rj.requireLicenseActive();
         Bukkit.getServicesManager().register(PracticeBotApi.class, this, this.rj, ServicePriority.Normal);
         this.rk = true;
      }
   }

   public void fP() {
      if (this.rk) {
         Bukkit.getServicesManager().unregister(PracticeBotApi.class, this);
         this.rk = false;
      }

      this.rl.clear();
      this.rm.clear();
      this.rn.clear();
   }

   void fQ() {
      this.rj.requireLicenseActive();
   }

   private List<E> fR() {
      return this.rj.getBotTemplateManager().gG().stream().map(this.rj.getBotTemplateManager()::i1).filter(Objects::nonNull).toList();
   }

   public void y(NPC var1) {
      this.fS();
      if (this.rj.isLicenseActive()) {
         PracticeBotHandle var2 = this.ad(var1);
         Bukkit.getPluginManager().callEvent(new PracticeBotSpawnEvent(var2));
         this.z(var1);
      }
   }

   public void d(NPC var1, n var2) {
      this.fS();
      if (this.rj.isLicenseActive()) {
         if (this.rj.getBotManager().d(var1)) {
            PracticeBotHandle var3 = this.ab(var1);
            UUID var4 = var1.getUniqueId();
            boolean var5 = this.rm.getOrDefault(var4, false);
            PracticeBotTargetSnapshot var6 = this.rn.remove(var4);
            if (var5) {
               if (var6 == null) {
                  var6 = this.ac(var1).target();
               }

               Bukkit.getPluginManager().callEvent(new PracticeBotCombatStopEvent(var3, var6));
               this.rm.remove(var4);
            }

            Bukkit.getPluginManager().callEvent(new PracticeBotDespawnEvent(var3, this.d(var2)));
         }
      }
   }

   public void a(NPC var1, UUID var2, UUID var3, PracticeBotTargetChangeCause var4) {
      this.fS();
      if (this.rj.isLicenseActive()) {
         Bukkit.getPluginManager()
            .callEvent(new PracticeBotTargetChangeEvent(this.ad(var1), w1.k(var2), w1.k(var3), var4 == null ? PracticeBotTargetChangeCause.UNKNOWN : var4));
      }
   }

   public void a(NPC var1, String var2, String var3, PracticeBotTemplateApplyCause var4) {
      this.fS();
      if (this.rj.isLicenseActive()) {
         Bukkit.getPluginManager()
            .callEvent(new PracticeBotTemplateApplyEvent(this.ad(var1), this.g0(var2), this.g0(var3), var4 == null ? PracticeBotTemplateApplyCause.API : var4));
      }
   }

   public void b(NPC var1, UUID var2) {
      this.fS();
      if (this.rj.isLicenseActive()) {
         if (this.rj.getBotManager().d(var1)) {
            Bukkit.getPluginManager().callEvent(new PracticeBotDeathEvent(this.ad(var1), var2));
         }
      }
   }

   public void c(NPC var1, UUID var2) {
      this.fS();
      if (this.rj.isLicenseActive()) {
         if (this.rj.getBotManager().d(var1)) {
            Bukkit.getPluginManager().callEvent(new PracticeBotKillEvent(this.ad(var1), var2));
         }
      }
   }

   public void z(NPC var1) {
      this.fS();
      if (this.rj.isLicenseActive()) {
         BotTrait var2 = this.ae(var1);
         PracticeBotStateSnapshot var3 = this.ac(var1);
         boolean var4 = this.d(var1, var2);
         boolean var5 = this.rm.getOrDefault(var3.npcUuid(), false);
         if (var4 != var5) {
            PracticeBotHandle var6 = this.ad(var1);
            if (var4) {
               if (var3.target() != null) {
                  this.rn.put(var3.npcUuid(), var3.target());
               }

               Bukkit.getPluginManager().callEvent(new PracticeBotCombatStartEvent(var6, var3.target()));
            } else {
               PracticeBotTargetSnapshot var7 = this.rn.remove(var3.npcUuid());
               Bukkit.getPluginManager().callEvent(new PracticeBotCombatStopEvent(var6, var7 != null ? var7 : var3.target()));
            }

            if (var4) {
               this.rm.put(var3.npcUuid(), true);
            } else {
               this.rm.remove(var3.npcUuid());
            }
         } else {
            if (var4 && var3.target() != null) {
               this.rn.put(var3.npcUuid(), var3.target());
            } else if (!var4) {
               this.rn.remove(var3.npcUuid());
            }
         }
      }
   }

   public void aa(NPC var1) {
      this.rl.remove(var1.getUniqueId());
      this.rm.remove(var1.getUniqueId());
      this.rn.remove(var1.getUniqueId());
   }

   boolean d(NPC var1, BotTrait var2) {
      if (var1 == null || var2 == null || !var1.isSpawned() || !(var1.getEntity() instanceof Player var3 && !var3.isDead())) {
         return false;
      } else if (!var2.isPvpEnabled() && !var2.isCpvpEnabled()) {
         return false;
      } else {
         Player var5 = var2.getBoundTargetPlayer();
         return BotTrait.isLiveCombatTarget(var5) && var5.getWorld().getUID().equals(var3.getWorld().getUID());
      }
   }

   @Override
   public String getApiVersion() {
      this.fQ();
      return "1.6.0";
   }

   @Override
   public Optional<PracticeBotHandle> getBotByNpcUuid(UUID var1) {
      this.fS();
      this.fQ();
      return var1 != null && this.rj.isCitizensReady()
         ? this.fT().stream().filter(var1x -> var1.equals(var1x.getUniqueId())).findFirst().map(this::ab)
         : Optional.empty();
   }

   @Override
   public Optional<PracticeBotHandle> getBotByEntityUuid(UUID var1) {
      this.fS();
      this.fQ();
      return var1 != null && this.rj.isCitizensReady()
         ? this.fT()
            .stream()
            .filter(var1x -> var1x.isSpawned() && var1x.getEntity() != null && var1.equals(var1x.getEntity().getUniqueId()))
            .findFirst()
            .map(this::ab)
         : Optional.empty();
   }

   @Override
   public List<PracticeBotHandle> getActiveBots() {
      this.fS();
      this.fQ();
      return !this.rj.isCitizensReady() ? List.of() : this.fT().stream().map(this::ab).toList();
   }

   @Override
   public Optional<BotTemplateView> getTemplate(String var1) {
      this.fS();
      this.fQ();
      return Optional.ofNullable(this.g0(var1));
   }

   @Override
   public List<BotTemplateView> getTemplates() {
      this.fS();
      this.fQ();
      return this.fR().stream().map(w1::b).toList();
   }

   @Override
   public PracticeBotHandle spawnBot(BotSpawnRequest var1) {
      this.fS();
      this.fQ();
      Objects.requireNonNull(var1, "request");
      this.fU();
      E var2 = this.f1(var1.templateKey());
      this.a(var2, var1.targetEntityUuid());
      Player var3 = var1.targetEntityUuid() == null ? null : this.p(var1.targetEntityUuid());
      NPC var4 = this.rj.getBotManager().a(var2, var1.spawnLocation(), var3);
      if (var4 == null) {
         throw new IllegalStateException("PracticeBot failed to spawn the requested template bot.");
      } else {
         return this.ab(var4);
      }
   }

   @Override
   public boolean despawnBot(UUID var1) {
      this.fS();
      this.fQ();
      this.fU();
      java.util.Optional<com.sheldera.practicebot.api.bot.PracticeBotHandle> var2 = this.getBotByNpcUuid(var1);
      var2.ifPresent(PracticeBotHandle::despawn);
      return var2.isPresent();
   }

   @Override
   public PracticeBotHandle applyTemplate(UUID var1, String var2, UUID var3) {
      this.fS();
      this.fQ();
      this.fU();
      NPC var4 = this.n(var1);
      E var5 = this.f1(var2);
      this.a(var5, var3);
      UUID var6 = var3 == null ? null : this.p(var3).getUniqueId();
      boolean var7 = this.rj.getBotManager().a(var4, var5, var6);
      if (!var7) {
         throw new IllegalStateException("PracticeBot could not apply the requested template to that bot.");
      } else {
         return this.ad(var4);
      }
   }

   @Override
   public PracticeBotHandle updateCpvpSettings(UUID var1, CpvpSettingsUpdateRequest var2) {
      this.fS();
      this.fQ();
      Objects.requireNonNull(var2, "request");
      NPC var3 = this.m(var1);
      BotTrait var4 = this.ae(var3);
      if (!var4.isCpvpEnabled()) {
         throw new IllegalStateException("That PracticeBot is not running in CPvP mode.");
      } else {
         if (var2.hasChanges()) {
            ae var5 = var4.getCpvpSettings();
            var2.anchoringModeOptional().ifPresent(var5::g);
            var2.breakBlocksOptional().ifPresent(var5::e);
            var2.lowHpCrystalLethalReserveOptional().ifPresent(var5::d);
            if (var3.isSpawned() && var3.getEntity() instanceof Player var6) {
               this.rj.getCrystalPvpModule().c(var6, var4);
            }
         }

         return this.ad(var3);
      }
   }

   PracticeBotHandle ab(NPC var1) {
      this.fQ();
      return new w0(this, var1.getUniqueId());
   }

   PracticeBotStateSnapshot ac(NPC var1) {
      this.fQ();
      BotTrait var2 = this.ae(var1);
      return w1.a(this, var1, var2);
   }

   PracticeBotMetadataValue a(UUID var1, NamespacedKey var2) {
      this.fQ();
      Map var3 = this.rl.get(var1);
      return var3 == null ? null : (PracticeBotMetadataValue)var3.get(var2);
   }

   Map<NamespacedKey, PracticeBotMetadataValue> l(UUID var1) {
      this.fQ();
      Map var2 = this.rl.get(var1);
      return var2 != null && !var2.isEmpty() ? Map.copyOf(var2) : Map.of();
   }

   void a(UUID var1, NamespacedKey var2, PracticeBotMetadataType var3, Object var4) {
      this.fS();
      this.fQ();
      this.m(var1);
      Objects.requireNonNull(var2, "key");
      Objects.requireNonNull(var3, "type");
      this.a(var3, var4);
      this.rl.computeIfAbsent(var1, var0 -> new ConcurrentHashMap<>()).put(var2, new PracticeBotMetadataValue(var3, var4));
   }

   void b(UUID var1, NamespacedKey var2) {
      this.fS();
      this.fQ();
      this.m(var1);
      Objects.requireNonNull(var2, "key");
      Map var3 = this.rl.get(var1);
      if (var3 != null) {
         var3.remove(var2);
         if (var3.isEmpty()) {
            this.rl.remove(var1);
         }
      }
   }

   NPC m(UUID var1) {
      this.fQ();
      NPC var2 = this.n(var1);
      if (!this.rj.getBotManager().d(var2)) {
         throw new IllegalStateException("That NPC is not an active PracticeBot runtime instance.");
      } else {
         return var2;
      }
   }

   NPC n(UUID var1) {
      this.fS();
      this.fQ();
      this.fU();
      Objects.requireNonNull(var1, "npcUuid");
      NPC var2 = StreamSupport.<NPC>stream(CitizensAPI.getNPCRegistry().spliterator(), false)
         .filter(var1x -> var1.equals(var1x.getUniqueId()))
         .findFirst()
         .orElse(null);
      if (var2 == null) {
         throw new IllegalArgumentException("No PracticeBot NPC exists with UUID " + var1 + ".");
      } else {
         return var2;
      }
   }

   void fS() {
      if (!Bukkit.isPrimaryThread()) {
         throw new IllegalStateException("PracticeBot API calls must run on the server main thread.");
      }
   }

   private List<NPC> fT() {
      if (!this.rj.isLicenseActive()) {
         return List.of();
      } else {
         return !this.rj.isCitizensReady() ? List.of() : this.rj.getBotManager().J();
      }
   }

   private void fU() {
      this.fQ();
      if (!this.rj.isCitizensReady()) {
         throw new IllegalStateException("Citizens is not initialized for PracticeBot yet.");
      }
   }

   private PracticeBotHandle ad(NPC var1) {
      if (!this.rj.getBotManager().d(var1)) {
         throw new IllegalStateException("That NPC is not an active PracticeBot runtime instance.");
      } else {
         return this.ab(var1);
      }
   }

   private BotTrait ae(NPC var1) {
      BotTrait var2 = (BotTrait)var1.getTraitNullable(BotTrait.class);
      if (var2 == null) {
         throw new IllegalStateException("PracticeBot runtime trait is not available.");
      } else {
         return var2;
      }
   }

   private E f1(String var1) {
      Objects.requireNonNull(var1, "templateKeyOrName");
      E var2 = this.g1(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("Unknown PracticeBot template: " + var1);
      } else {
         return var2;
      }
   }

   private BotTemplateView g0(String var1) {
      if (var1 != null && !var1.isBlank()) {
         E var2 = this.g1(var1);
         return w1.b(var2);
      } else {
         return null;
      }
   }

   private E g1(String var1) {
      E var2 = this.rj.getBotTemplateManager().i1(var1);
      return var2 != null
         ? var2
         : this.fR()
            .stream()
            .filter(var1x -> var1x.fh().equalsIgnoreCase(var1) || var1x.fY() != null && var1x.fY().equalsIgnoreCase(var1))
            .findFirst()
            .orElse(null);
   }

   private void a(E var1, UUID var2) {
      M var3 = var1.gb();
      if (var3 == M.REQUIRED && var2 == null) {
         throw new IllegalArgumentException("This template requires a live target player.");
      } else if ((var3 == M.NONE || var3 == M.BOT) && var2 != null) {
         throw new IllegalArgumentException("This template does not accept an explicit player target.");
      } else {
         if (var2 != null) {
            this.p(var2);
         }
      }
   }

   UUID o(UUID var1) {
      return this.p(var1).getUniqueId();
   }

   private Player p(UUID var1) {
      Player var2 = Bukkit.getPlayer(var1);
      if (var2 != null && var2.isOnline()) {
         return var2;
      } else {
         throw new IllegalArgumentException("Target player must be online.");
      }
   }

   private void a(PracticeBotMetadataType var1, Object var2) {
      if (!var1.supports(var2)) {
         throw new IllegalArgumentException("Metadata value does not match declared type " + var1 + ".");
      }
   }

   private PracticeBotRemovalReason d(n var1) {
      if (var1 == null) {
         return PracticeBotRemovalReason.UNKNOWN;
      } else {
         return switch (var1) {
            case MANUAL -> PracticeBotRemovalReason.MANUAL;
            case BOT_DEATH -> PracticeBotRemovalReason.BOT_DEATH;
            case OWNER_QUIT -> PracticeBotRemovalReason.OWNER_QUIT;
            case OWNER_DEATH -> PracticeBotRemovalReason.OWNER_DEATH;
            case TARGET_QUIT -> PracticeBotRemovalReason.TARGET_QUIT;
            case TARGET_KILLED -> PracticeBotRemovalReason.TARGET_KILLED;
            case TEMPLATE_DESPAWN -> PracticeBotRemovalReason.TEMPLATE_DESPAWN;
            case WORLD_UNLOAD -> PracticeBotRemovalReason.WORLD_UNLOAD;
            case PLUGIN_DISABLE -> PracticeBotRemovalReason.SHUTDOWN;
            case STARTUP_CLEANUP -> PracticeBotRemovalReason.CITIZENS_CLEANUP;
            case REPLACED -> PracticeBotRemovalReason.REPLACED;
         };
      }
   }
}
