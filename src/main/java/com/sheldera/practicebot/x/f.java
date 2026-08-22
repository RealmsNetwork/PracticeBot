package com.sheldera.practicebot.x;

record f(boolean am, String an) {
   static f A() {
      return new f(true, null);
   }

   static f aa(String var0) {
      return new f(false, var0);
   }

   public boolean y1() {
      return this.am;
   }

   public String z0() {
      return this.an;
   }
}
