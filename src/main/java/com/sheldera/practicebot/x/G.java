package com.sheldera.practicebot.x;

public final class G {
   private final boolean rN;
   private final boolean rO;
   private final boolean rP;
   private final boolean rQ;
   private final boolean rR;
   private final boolean rS;
   private final boolean rT;
   private final boolean rU;
   private final boolean rV;

   public G(boolean var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6, boolean var7, boolean var8, boolean var9) {
      this.rN = var1;
      this.rO = var2;
      this.rP = var3;
      this.rQ = var4;
      this.rR = var5;
      this.rS = var6;
      this.rT = var7;
      this.rU = var8;
      this.rV = var9;
   }

   public G(G var1) {
      this(var1.rN, var1.rO, var1.rP, var1.rQ, var1.rR, var1.rS, var1.rT, var1.rU, var1.rV);
   }

   public boolean go() {
      return this.rN;
   }

   public boolean gp() {
      return this.rO;
   }

   public boolean isRandomWalk() {
      return this.rP;
   }

   public boolean isHoldShield() {
      return this.rQ;
   }

   public boolean isUseShield() {
      return this.rR;
   }

   public boolean isResistance() {
      return this.rS;
   }

   public boolean isFrozen() {
      return this.rT;
   }

   public boolean isShieldInMainHand() {
      return this.rU;
   }

   public boolean isPvpEnabled() {
      return this.rV;
   }
}
