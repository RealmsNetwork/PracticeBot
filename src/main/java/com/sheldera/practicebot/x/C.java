package com.sheldera.practicebot.x;

// $VF: synthetic class
class C {
   static final int[] rv = new int[a.values().length];

   static {
      try {
         rv[a.NORMAL.ordinal()] = 1;
      } catch (NoSuchFieldError var3) {
      }

      try {
         rv[a.CPVP.ordinal()] = 2;
      } catch (NoSuchFieldError var2) {
      }

      try {
         rv[a.DUMMY.ordinal()] = 3;
      } catch (NoSuchFieldError var1) {
      }
   }
}
