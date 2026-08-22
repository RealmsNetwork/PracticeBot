package com.sheldera.practicebot.x;

record e(boolean aj, String ak, String al) {
   static e y(String var0) {
      return new e(true, null, var0);
   }

   static e z(String var0) {
      return new e(false, var0, null);
   }

   public boolean y1() {
      return this.aj;
   }

   public String z0() {
      return this.ak;
   }

   public String z1() {
      return this.al;
   }
}
