package com.sheldera.practicebot.x;

// $VF: synthetic class
class l {
   static final int[] bp = new int[L.values().length];

   static {
      try {
         bp[L.DESPAWN.ordinal()] = 1;
      } catch (NoSuchFieldError var3) {
      }

      try {
         bp[L.RESPAWN.ordinal()] = 2;
      } catch (NoSuchFieldError var2) {
      }

      try {
         bp[L.NONE.ordinal()] = 3;
      } catch (NoSuchFieldError var1) {
      }
   }
}
