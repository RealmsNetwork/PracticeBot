package com.sheldera.practicebot.api.metadata;

import java.util.UUID;

public enum PracticeBotMetadataType {
   STRING(String.class),
   BOOLEAN(Boolean.class),
   INTEGER(Integer.class),
   LONG(Long.class),
   DOUBLE(Double.class),
   UUID(UUID.class);

   private final Class<?> valueClass;

   private PracticeBotMetadataType(Class<?> var3) {
      this.valueClass = var3;
   }

   public Object normalize(Object var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Metadata value cannot be null.");
      } else {
         return switch (this) {
            case STRING -> String.valueOf(var1);
            case BOOLEAN -> (Boolean)requireType(var1, Boolean.class);
            case INTEGER -> (Integer)requireType(var1, Integer.class);
            case LONG -> (Long)requireType(var1, Long.class);
            case DOUBLE -> (Double)requireType(var1, Double.class);
            case UUID -> (UUID)requireType(var1, UUID.class);
         };
      }
   }

   public boolean supports(Object var1) {
      return var1 != null && (this == STRING || this.valueClass.isInstance(var1));
   }

   private static <T> T requireType(Object var0, Class<T> var1) {
      if (!var1.isInstance(var0)) {
         throw new IllegalArgumentException("Expected metadata value of type " + var1.getSimpleName() + ".");
      } else {
         return (T)var1.cast(var0);
      }
   }
}
