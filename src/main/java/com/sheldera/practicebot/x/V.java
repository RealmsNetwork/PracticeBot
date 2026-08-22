package com.sheldera.practicebot.x;

enum V {
   NAME("template name"),
   DISPLAY_NAME("display name"),
   SKIN_NAME("skin name"),
   RESPAWN_DELAY("respawn delay"),
   WARMUP("warm-up delay");

   final String displayName;

   private V(String var3) {
      this.displayName = var3;
   }
}
