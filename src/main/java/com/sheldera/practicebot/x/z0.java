package com.sheldera.practicebot.x;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

// $VF: synthetic class
class z0 {
   static final int[] rq = new int[DamageCause.values().length];

   static {
      try {
         rq[DamageCause.ENTITY_ATTACK.ordinal()] = 1;
      } catch (NoSuchFieldError var2) {
      }

      try {
         rq[DamageCause.ENTITY_SWEEP_ATTACK.ordinal()] = 2;
      } catch (NoSuchFieldError var1) {
      }
   }
}
