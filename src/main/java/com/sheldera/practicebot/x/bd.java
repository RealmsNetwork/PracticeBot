package com.sheldera.practicebot.x;

import org.bukkit.block.Block;

record bd(boolean ev, String ew, Block ex) {
   static bd be(String var0) {
      return new bd(false, var0, null);
   }

   static bd a(String var0, Block var1) {
      return new bd(true, var0, var1);
   }

   public boolean cW() {
      return this.ev;
   }

   public String B() {
      return this.ew;
   }

   public Block cV() {
      return this.ex;
   }
}
