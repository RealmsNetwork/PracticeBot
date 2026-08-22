package com.sheldera.practicebot.x;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class aj implements InventoryHolder {
   private final int aW;

   public aj(int var1) {
      this.aW = var1;
   }

   public int bL() {
      return this.aW;
   }

   public Inventory getInventory() {
      return null;
   }
}
