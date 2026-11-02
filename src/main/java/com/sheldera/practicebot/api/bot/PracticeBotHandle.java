package com.sheldera.practicebot.api.bot;

import com.sheldera.practicebot.api.cpvp.CpvpSettingsUpdateRequest;
import com.sheldera.practicebot.api.metadata.PracticeBotMetadataType;
import com.sheldera.practicebot.api.metadata.PracticeBotMetadataValue;
import com.sheldera.practicebot.api.template.BotTemplateView;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.NamespacedKey;

public interface PracticeBotHandle {
   UUID getNpcUuid();

   Optional<UUID> getEntityUuid();

   boolean isActive();

   PracticeBotStateSnapshot getStateSnapshot();

   Optional<PracticeBotTargetSnapshot> getCurrentTarget();

   Optional<BotTemplateView> getCurrentTemplate();

   void despawn();

   void setTarget(UUID var1);

   void clearTarget();

   /** Binds this bot to a player for combat and disables idle/follow movement. */
   void bindCombatTarget(UUID targetUuid);

   /** Applies a saved storage/armor/offhand layout to this bot. */
   void applyInventoryProfile(String profileKey);

   /** Returns the live Bukkit player entity when the NPC is spawned. */
   Optional<org.bukkit.entity.Player> getPlayer();

   PracticeBotHandle updateCpvpSettings(CpvpSettingsUpdateRequest var1);

   void setMetadata(NamespacedKey var1, PracticeBotMetadataType var2, Object var3);

   Optional<PracticeBotMetadataValue> getMetadata(NamespacedKey var1);

   Map<NamespacedKey, PracticeBotMetadataValue> getMetadataSnapshot();

   boolean hasMetadata(NamespacedKey var1);

   void removeMetadata(NamespacedKey var1);
}
