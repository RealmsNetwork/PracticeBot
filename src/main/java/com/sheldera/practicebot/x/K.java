package com.sheldera.practicebot.x;

import org.bukkit.inventory.ItemStack;

public final class K {
   private final String sG;
   private final String sH;
   private final String sI;
   private final String sJ;
   private final String sK;
   private final String sL;
   private final String sM;
   private final String sN;
   private final String sO;
   private final int sP;
   private final ItemStack sQ;
   private final F sR;
   private final F sS;
   private final F sT;
   private final F sU;

   public K(
      String var1,
      String var2,
      String var3,
      String var4,
      String var5,
      String var6,
      String var7,
      String var8,
      String var9,
      int var10,
      ItemStack var11,
      F var12,
      F var13,
      F var14,
      F var15
   ) {
      this.sG = var1;
      this.sH = var2;
      this.sI = var3;
      this.sJ = var4;
      this.sK = var5;
      this.sL = var6;
      this.sM = var7;
      this.sN = var8;
      this.sO = var9;
      this.sP = var10;
      this.sQ = var11 != null ? var11.clone() : null;
      this.sR = var12 != null ? new F(var12) : F.gk();
      this.sS = var13 != null ? new F(var13) : F.gk();
      this.sT = var14 != null ? new F(var14) : F.gk();
      this.sU = var15 != null ? new F(var15) : F.gk();
   }

   public K(K var1) {
      this(var1.sG, var1.sH, var1.sI, var1.sJ, var1.sK, var1.sL, var1.sM, var1.sN, var1.sO, var1.sP, var1.sQ, var1.sR, var1.sS, var1.sT, var1.sU);
   }

   public String getArmorType() {
      return this.sH;
   }

   public String getHelmetMaterial() {
      return this.sG;
   }

   public String getChestplateMaterial() {
      return this.sH;
   }

   public String getHelmetEnchant() {
      return this.sK;
   }

   public String getChestplateEnchant() {
      return this.sL;
   }

   public String getLeggingsEnchant() {
      return this.sM;
   }

   public String getBootsEnchant() {
      return this.sN;
   }

   public String getLeggingsMaterial() {
      return this.sI;
   }

   public String getBootsMaterial() {
      return this.sJ;
   }

   public String getOffhandType() {
      return this.sO;
   }

   public int getTotemCount() {
      return this.sP;
   }

   public ItemStack getCustomMainHand() {
      return this.sQ != null ? this.sQ.clone() : null;
   }

   public F gC() {
      return new F(this.sR);
   }

   public F gD() {
      return new F(this.sS);
   }

   public F gE() {
      return new F(this.sT);
   }

   public F gF() {
      return new F(this.sU);
   }
}
