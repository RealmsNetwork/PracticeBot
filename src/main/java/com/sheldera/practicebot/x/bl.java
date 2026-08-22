package com.sheldera.practicebot.x;

import org.bukkit.block.Block;

record bl(Block fI, double fJ) {
   public Block du() {
      return this.fI;
   }

   public double dv() {
      return this.fJ;
   }
}
