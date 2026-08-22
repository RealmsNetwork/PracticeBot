package com.sheldera.practicebot.x;

import org.bukkit.entity.Pose;

// $VF: synthetic class
class am {
   static final int[] bE = new int[Pose.values().length];

   static {
      try {
         bE[Pose.SWIMMING.ordinal()] = 1;
      } catch (NoSuchFieldError var5) {
      }

      try {
         bE[Pose.FALL_FLYING.ordinal()] = 2;
      } catch (NoSuchFieldError var4) {
      }

      try {
         bE[Pose.SPIN_ATTACK.ordinal()] = 3;
      } catch (NoSuchFieldError var3) {
      }

      try {
         bE[Pose.SNEAKING.ordinal()] = 4;
      } catch (NoSuchFieldError var2) {
      }

      try {
         bE[Pose.SLEEPING.ordinal()] = 5;
      } catch (NoSuchFieldError var1) {
      }
   }
}
