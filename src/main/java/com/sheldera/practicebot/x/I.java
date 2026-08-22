package com.sheldera.practicebot.x;

public final class I {
   private final boolean sg;
   private final String sh;
   private final double si;
   private final boolean sj;
   private final boolean sk;
   private final boolean sl;
   private final boolean sm;
   private final boolean sn;
   private final boolean so;
   private final boolean sp;
   private final double sq;
   private final double sr;
   private final long ss;
   private final double st;
   private final long su;
   private final long sv;
   private final long sw;
   private final long sx;
   private final long sy;
   private final int sz;
   private final long sA;
   private final long sB;
   private final int sC;
   private final long sD;
   private final long sE;
   private final int sF;

   public I(
      boolean var1,
      String var2,
      double var3,
      boolean var5,
      boolean var6,
      boolean var7,
      boolean var8,
      boolean var9,
      boolean var10,
      boolean var11,
      double var12,
      double var14,
      long var16
   ) {
      this(
         var1,
         var2,
         var3,
         var5,
         var6,
         var7,
         var8,
         var9,
         var10,
         var11,
         var12,
         var14,
         var16,
         100.0,
         220L,
         320L,
         130L,
         190L,
         800L,
         35,
         180L,
         380L,
         18,
         160L,
         360L,
         45
      );
   }

   public I(
      boolean var1,
      String var2,
      double var3,
      boolean var5,
      boolean var6,
      boolean var7,
      boolean var8,
      boolean var9,
      boolean var10,
      boolean var11,
      double var12,
      double var14,
      long var16,
      long var18,
      long var20,
      long var22,
      long var24,
      long var26,
      int var28,
      long var29,
      long var31,
      int var33,
      long var34,
      long var36,
      int var38
   ) {
      this(
         var1,
         var2,
         var3,
         var5,
         var6,
         var7,
         var8,
         var9,
         var10,
         var11,
         var12,
         var14,
         var16,
         100.0,
         var18,
         var20,
         var22,
         var24,
         var26,
         var28,
         var29,
         var31,
         var33,
         var34,
         var36,
         var38
      );
   }

   public I(
      boolean var1,
      String var2,
      double var3,
      boolean var5,
      boolean var6,
      boolean var7,
      boolean var8,
      boolean var9,
      boolean var10,
      boolean var11,
      double var12,
      double var14,
      long var16,
      double var18,
      long var20,
      long var22,
      long var24,
      long var26,
      long var28,
      int var30,
      long var31,
      long var33,
      int var35,
      long var36,
      long var38,
      int var40
   ) {
      this.sg = var1;
      this.sh = var2;
      this.si = var3;
      this.sj = var5;
      this.sk = var6;
      this.sl = var7;
      this.sm = var8;
      this.sn = var9;
      this.so = var10;
      this.sp = var11;
      this.sq = var12;
      this.sr = var14;
      this.ss = var16;
      this.st = Math.max(1.0, Math.min(360.0, var18));
      this.su = g(var20, var22);
      this.sv = h(var20, var22);
      this.sw = g(var24, var26);
      this.sx = h(var24, var26);
      this.sy = Math.max(100L, var28);
      this.sz = e(var30);
      this.sA = e(var31, var33);
      this.sB = f(var31, var33);
      this.sC = e(var35);
      this.sD = e(var36, var38);
      this.sE = f(var36, var38);
      this.sF = e(var40);
   }

   public I(I var1) {
      this(
         var1.sg,
         var1.sh,
         var1.si,
         var1.sj,
         var1.sk,
         var1.sl,
         var1.sm,
         var1.sn,
         var1.so,
         var1.sp,
         var1.sq,
         var1.sr,
         var1.ss,
         var1.st,
         var1.su,
         var1.sv,
         var1.sw,
         var1.sx,
         var1.sy,
         var1.sz,
         var1.sA,
         var1.sB,
         var1.sC,
         var1.sD,
         var1.sE,
         var1.sF
      );
   }

   private static long g(long var0, long var2) {
      long var4 = Math.max(1L, var0);
      long var6 = Math.max(1L, var2);
      return Math.min(var4, var6);
   }

   private static long h(long var0, long var2) {
      long var4 = Math.max(1L, var0);
      long var6 = Math.max(1L, var2);
      return Math.max(var4, var6);
   }

   private static long e(long var0, long var2) {
      long var4 = Math.max(0L, var0);
      long var6 = Math.max(0L, var2);
      return Math.min(var4, var6);
   }

   private static long f(long var0, long var2) {
      long var4 = Math.max(0L, var0);
      long var6 = Math.max(0L, var2);
      return Math.max(var4, var6);
   }

   private static int e(int var0) {
      return Math.max(0, Math.min(100, var0));
   }

   public boolean gA() {
      return this.sg;
   }

   public String gB() {
      return this.sh;
   }

   public double aI() {
      return this.si;
   }

   public boolean aK() {
      return this.sj;
   }

   public boolean aL() {
      return this.sk;
   }

   public boolean aM() {
      return this.sl;
   }

   public boolean aN() {
      return this.sm;
   }

   public boolean aO() {
      return this.sn;
   }

   public boolean aP() {
      return this.so;
   }

   public boolean aQ() {
      return this.sp;
   }

   public double aR() {
      return this.sq;
   }

   public double aS() {
      return this.sr;
   }

   public long aT() {
      return this.ss;
   }

   public double aU() {
      return this.st;
   }

   public long aW() {
      return this.su;
   }

   public long aX() {
      return this.sv;
   }

   public long aZ() {
      return this.sw;
   }

   public long bA() {
      return this.sx;
   }

   public long bJ() {
      return this.sy;
   }

   public int bB() {
      return this.sz;
   }

   public long bC() {
      return this.sA;
   }

   public long bD() {
      return this.sB;
   }

   public int bE() {
      return this.sC;
   }

   public long bF() {
      return this.sD;
   }

   public long bG() {
      return this.sE;
   }

   public int bH() {
      return this.sF;
   }
}
