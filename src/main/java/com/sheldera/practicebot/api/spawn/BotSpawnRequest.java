package com.sheldera.practicebot.api.spawn;

import java.util.UUID;
import org.bukkit.Location;

public final class BotSpawnRequest {
   private final String templateKey;
   private final Location spawnLocation;
   private final UUID targetEntityUuid;

   BotSpawnRequest(BotSpawnRequest$Builder var1) {
      this.templateKey = normalizeTemplateKey(var1.templateKey);
      this.spawnLocation = var1.spawnLocation.clone();
      this.targetEntityUuid = var1.targetEntityUuid;
      if (this.templateKey.isBlank()) {
         throw new IllegalArgumentException("Template key cannot be blank.");
      } else if (this.spawnLocation.getWorld() == null) {
         throw new IllegalArgumentException("Spawn location must have a world.");
      }
   }

   public String templateKey() {
      return this.templateKey;
   }

   public Location spawnLocation() {
      return this.spawnLocation.clone();
   }

   public UUID targetEntityUuid() {
      return this.targetEntityUuid;
   }

   public static BotSpawnRequest$Builder builder(String var0, Location var1) {
      return new BotSpawnRequest$Builder(var0, var1);
   }

   private static String normalizeTemplateKey(String var0) {
      return var0 == null ? "" : var0.trim();
   }
}
