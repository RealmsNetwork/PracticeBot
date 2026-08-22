package com.sheldera.practicebot.x;

record g(boolean ao, String ap, long aq) {
   static g a(long var0) {
      return new g(true, null, var0);
   }

   static g ab(String var0) {
      return new g(false, var0, 0L);
   }

   public boolean x1() {
      return this.ao;
   }

   public String B() {
      return this.ap;
   }

   public long y0() {
      return this.aq;
   }
}
