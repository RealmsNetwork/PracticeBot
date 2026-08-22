package com.sheldera.practicebot.x;

public final class F {
   private final String rL;
   private final String rM;

   public F(String var1, String var2) {
      this.rL = i0(var1);
      this.rM = i0(var2);
   }

   public F(F var1) {
      this(var1.rL, var1.rM);
   }

   public static F gk() {
      return new F("", "");
   }

   public String gl() {
      return this.rL;
   }

   public String gm() {
      return this.rM;
   }

   public boolean gn() {
      return !this.rL.isBlank() && !this.rM.isBlank();
   }

   private static String i0(String var0) {
      return var0 == null ? "" : var0.trim().toLowerCase();
   }
}
