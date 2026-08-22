package com.sheldera.practicebot.x;

import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;

record bc(EnderCrystal et, Block eu) {
   public EnderCrystal cU() {
      return this.et;
   }

   public Block cV() {
      return this.eu;
   }
}
