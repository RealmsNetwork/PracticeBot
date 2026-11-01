package com.sheldera.practicebot.api;

public enum PracticeBotType {
   NORMAL,
   CRYSTAL,
   CART,
   MACE,
   UHC,
   WATER,
   /** @deprecated Use CRYSTAL. Kept for source and binary compatibility. */
   @Deprecated CPVP,
   DUMMY;
}
