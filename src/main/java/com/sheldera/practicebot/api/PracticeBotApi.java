package com.sheldera.practicebot.api;

import com.sheldera.practicebot.api.bot.PracticeBotHandle;
import com.sheldera.practicebot.api.cpvp.CpvpSettingsUpdateRequest;
import com.sheldera.practicebot.api.spawn.BotSpawnRequest;
import com.sheldera.practicebot.api.template.BotTemplateView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PracticeBotApi {
   String getApiVersion();

   Optional<PracticeBotHandle> getBotByNpcUuid(UUID var1);

   Optional<PracticeBotHandle> getBotByEntityUuid(UUID var1);

   List<PracticeBotHandle> getActiveBots();

   Optional<BotTemplateView> getTemplate(String var1);

   List<BotTemplateView> getTemplates();

   PracticeBotHandle spawnBot(BotSpawnRequest var1);

   boolean despawnBot(UUID var1);

   PracticeBotHandle applyTemplate(UUID var1, String var2, UUID var3);

   PracticeBotHandle updateCpvpSettings(UUID var1, CpvpSettingsUpdateRequest var2);
}
