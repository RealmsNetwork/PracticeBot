package com.sheldera.practicebot.api;

import java.util.Locale;

/** Supported equipment/combat profiles which can be attached to a bot template. */
public enum PracticeBotWeaponProfile {
   SWORD,
   MACE,
   MACE_ELYTRA,
   MACE_ROCKET,
   SPEAR,
   SPEAR_ELYTRA,
   SPEAR_MACE,
   SPEAR_MACE_ELYTRA,
   CART,
   CART_ELYTRA,
   UHC,
   WATER,
   BOW;

   public static PracticeBotWeaponProfile fromName(String value) {
      if (value == null || value.isBlank()) return SWORD;
      try {
         return valueOf(value.trim().toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException ignored) {
         return SWORD;
      }
   }
}
