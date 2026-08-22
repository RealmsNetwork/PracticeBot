package com.sheldera.practicebot.api.bot;

import java.util.UUID;

public record PracticeBotLocationSnapshot(UUID worldUuid, String worldName, double x, double y, double z, float yaw, float pitch) {
}
