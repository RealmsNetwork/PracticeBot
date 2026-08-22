package com.sheldera.practicebot.api.bot;

import com.sheldera.practicebot.api.PracticeBotType;
import java.util.Optional;
import java.util.UUID;

public record PracticeBotStateSnapshot(
   UUID npcUuid,
   UUID entityUuid,
   String name,
   PracticeBotType botType,
   boolean spawned,
   boolean alive,
   boolean inCombat,
   boolean pvpEnabled,
   boolean cpvpEnabled,
   boolean anchoringMode,
   boolean blockBreaking,
   boolean frozen,
   boolean templateControlled,
   boolean autoTargetingEnabled,
   boolean autoTargetBotsOnly,
   UUID ownerUuid,
   String templateKey,
   PracticeBotLocationSnapshot location,
   PracticeBotTargetSnapshot target
) {
   public Optional<UUID> entityUuidOptional() {
      return Optional.ofNullable(this.entityUuid);
   }

   public Optional<UUID> ownerUuidOptional() {
      return Optional.ofNullable(this.ownerUuid);
   }

   public Optional<String> templateKeyOptional() {
      return Optional.ofNullable(this.templateKey);
   }

   public Optional<PracticeBotLocationSnapshot> locationOptional() {
      return Optional.ofNullable(this.location);
   }

   public Optional<PracticeBotTargetSnapshot> targetOptional() {
      return Optional.ofNullable(this.target);
   }
}
