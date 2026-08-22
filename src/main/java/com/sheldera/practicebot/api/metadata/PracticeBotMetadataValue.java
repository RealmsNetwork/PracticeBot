package com.sheldera.practicebot.api.metadata;

public record PracticeBotMetadataValue(PracticeBotMetadataType type, Object value) {
   public PracticeBotMetadataValue(PracticeBotMetadataType type, Object value) {
      if (type == null) {
         throw new IllegalArgumentException("Metadata type cannot be null.");
      } else {
         value = type.normalize(value);
         this.type = type;
         this.value = value;
      }
   }
}
