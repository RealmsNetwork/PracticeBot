package com.sheldera.practicebot.x;

import java.util.UUID;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

record W(UUID tt, T tu) implements InventoryHolder {

   public Inventory getInventory() {
      return null;
   }

   public UUID gO() {
      return this.tt;
   }

   public T gR() {
      return this.tu;
   }
}
