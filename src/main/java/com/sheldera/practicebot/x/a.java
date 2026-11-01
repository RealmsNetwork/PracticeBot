package com.sheldera.practicebot.x;

import java.util.Locale;

public enum a {
   NORMAL,
   CRYSTAL,
   CPVP,
   CART,
   MACE,
   UHC,
   WATER,
   DUMMY;

   public boolean isCrystal() {
      return this == CRYSTAL || this == CPVP;
   }

   public boolean isStandardCombat() {
      return !isCrystal() && this != DUMMY;
   }

   public String defaultWeaponProfile() {
      return switch (this) {
         case CART -> "cart";
         case MACE -> "mace";
         case UHC -> "uhc";
         case WATER -> "water";
         default -> "sword";
      };
   }

   public static a fromName(String name) {
      if (name == null || name.isBlank()) return NORMAL;
      return switch (name.trim().toUpperCase(Locale.ROOT)) {
         case "CRYSTAL", "CPVP" -> CRYSTAL;
         case "NORMAL", "CART", "MACE", "UHC", "WATER", "DUMMY" ->
            valueOf(name.trim().toUpperCase(Locale.ROOT));
         default -> NORMAL;
      };
   }
}
