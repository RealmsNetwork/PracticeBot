package com.sheldera.practicebot.x;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class u1 implements InventoryHolder {
   private final int ra;

   public u1(int var1) {
      this.ra = var1;
   }

   public int bL() {
      return this.ra;
   }

   public Inventory getInventory() {
      return null;
   }
}
