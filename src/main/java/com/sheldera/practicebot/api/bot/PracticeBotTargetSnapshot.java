package com.sheldera.practicebot.api.bot;

import java.util.UUID;

public record PracticeBotTargetSnapshot(UUID entityUuid, UUID npcUuid, String name, PracticeBotTargetSnapshot$Kind kind) {
}
