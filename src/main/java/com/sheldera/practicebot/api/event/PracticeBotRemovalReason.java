package com.sheldera.practicebot.api.event;

public enum PracticeBotRemovalReason {
   MANUAL,
   BOT_DEATH,
   OWNER_QUIT,
   OWNER_DEATH,
   TARGET_QUIT,
   TARGET_DEATH,
   TARGET_INVALID,
   TARGET_KILLED,
   TEMPLATE_DESPAWN,
   WORLD_UNLOAD,
   SHUTDOWN,
   CITIZENS_CLEANUP,
   REPLACED,
   UNKNOWN;
}
