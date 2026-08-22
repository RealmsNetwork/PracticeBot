package com.sheldera.practicebot.x;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class p1 implements InventoryHolder {
   private final String pE;

   public p1(String var1) {
      this.pE = var1;
   }

   public String ex() {
      return this.pE;
   }

   public Inventory getInventory() {
      return null;
   }
}
