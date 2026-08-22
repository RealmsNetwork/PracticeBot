package com.sheldera.practicebot.x;

public final class E {
   private final String rw;
   private final String rx;
   private final String ry;
   private final String rz;
   private final a rA;
   private final boolean rB;
   private final M rC;
   private final J rD;
   private final L rE;
   private final long rF;
   private final long rG;
   private final G rH;
   private final H rI;
   private final K rJ;
   private final I rK;

   public E(
      String var1,
      String var2,
      String var3,
      String var4,
      a var5,
      boolean var6,
      M var7,
      J var8,
      L var9,
      long var10,
      long var12,
      G var14,
      H var15,
      K var16,
      I var17
   ) {
      this.rw = var1;
      this.rx = var2;
      this.ry = var3;
      this.rz = var4;
      this.rA = var5;
      this.rB = var6;
      this.rC = var7;
      this.rD = var8;
      this.rE = var9;
      this.rF = var10;
      this.rG = var12;
      this.rH = new G(var14);
      this.rI = new H(var15);
      this.rJ = new K(var16);
      this.rK = new I(var17);
   }

   public E(E var1) {
      this(var1.rw, var1.rx, var1.ry, var1.rz, var1.rA, var1.rB, var1.rC, var1.rD, var1.rE, var1.rF, var1.rG, var1.rH, var1.rI, var1.rJ, var1.rK);
   }

   public E fX() {
      return new E(this);
   }

   public String fe() {
      return this.rw;
   }

   public String fh() {
      return this.rx;
   }

   public String fY() {
      return this.ry;
   }

   public String fZ() {
      return this.rz;
   }

   public a getBotType() {
      return this.rA;
   }

   public boolean ga() {
      return this.rB;
   }

   public M gb() {
      return this.rC;
   }

   public J gc() {
      return this.rD;
   }

   public L gd() {
      return this.rE;
   }

   public long ge() {
      return this.rF;
   }

   public long gf() {
      return this.rG;
   }

   public G gg() {
      return new G(this.rH);
   }

   public H gh() {
      return new H(this.rI);
   }

   public K gi() {
      return new K(this.rJ);
   }

   public I gj() {
      return new I(this.rK);
   }
}
