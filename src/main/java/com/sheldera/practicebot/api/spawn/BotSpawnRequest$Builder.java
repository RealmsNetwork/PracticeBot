package com.sheldera.practicebot.api.spawn;

import java.util.Objects;
import java.util.UUID;
import org.bukkit.Location;

public final class BotSpawnRequest$Builder {
   final String templateKey;
   final Location spawnLocation;
   UUID targetEntityUuid;

   BotSpawnRequest$Builder(String var1, Location var2) {
      this.templateKey = Objects.requireNonNull(var1, "templateKey");
      this.spawnLocation = Objects.requireNonNull(var2, "spawnLocation");
   }

   public BotSpawnRequest$Builder targetEntityUuid(UUID var1) {
      this.targetEntityUuid = var1;
      return this;
   }

   public BotSpawnRequest build() {
      return new BotSpawnRequest(this);
   }
}
