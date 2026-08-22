package com.sheldera.practicebot.x;

import org.bukkit.event.inventory.InventoryAction;

// $VF: synthetic class
class P {
   static final int[] tp = new int[InventoryAction.values().length];
   static final int[] to = new int[L.values().length];
   static final int[] tn = new int[M.values().length];
   static final int[] tm = new int[a.values().length];

   static {
      try {
         tp[InventoryAction.MOVE_TO_OTHER_INVENTORY.ordinal()] = 1;
      } catch (NoSuchFieldError var12) {
      }

      try {
         tp[InventoryAction.HOTBAR_SWAP.ordinal()] = 2;
      } catch (NoSuchFieldError var11) {
      }

      try {
         tp[InventoryAction.COLLECT_TO_CURSOR.ordinal()] = 3;
      } catch (NoSuchFieldError var10) {
      }

      try {
         to[L.DESPAWN.ordinal()] = 1;
      } catch (NoSuchFieldError var9) {
      }

      try {
         to[L.RESPAWN.ordinal()] = 2;
      } catch (NoSuchFieldError var8) {
      }

      try {
         to[L.NONE.ordinal()] = 3;
      } catch (NoSuchFieldError var7) {
      }

      try {
         tn[M.NONE.ordinal()] = 1;
      } catch (NoSuchFieldError var6) {
      }

      try {
         tn[M.BOT.ordinal()] = 2;
      } catch (NoSuchFieldError var5) {
      }

      try {
         tn[M.REQUIRED.ordinal()] = 3;
      } catch (NoSuchFieldError var4) {
      }

      try {
         tm[a.CPVP.ordinal()] = 1;
      } catch (NoSuchFieldError var3) {
      }

      try {
         tm[a.DUMMY.ordinal()] = 2;
      } catch (NoSuchFieldError var2) {
      }

      try {
         tm[a.NORMAL.ordinal()] = 3;
      } catch (NoSuchFieldError var1) {
      }
   }
}
