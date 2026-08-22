package com.sheldera.practicebot.x;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class q0 implements InventoryHolder {
   final String pF;
   private final String pG;

   public q0(String var1, String var2) {
      this.pF = var1;
      this.pG = var2;
   }

   public String ex() {
      return this.pF;
   }

   public String eX() {
      return this.pG;
   }

   public Inventory getInventory() {
      return null;
   }
}
