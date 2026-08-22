package com.sheldera.practicebot.x;

import java.util.UUID;

class r0 {
   private final UUID pH;
   private r1 pI;
   private String pJ;
   private String pK;
   private int pL = -1;

   public r0(UUID var1) {
      this.pH = var1;
      this.pI = r1.SELECTING_GUI;
   }

   public r1 eY() {
      return this.pI;
   }

   public void a(r1 var1) {
      this.pI = var1;
   }

   public String eZ() {
      return this.pJ;
   }

   public void bx(String var1) {
      this.pJ = var1;
   }

   public String fa() {
      return this.pK;
   }

   public void by(String var1) {
      this.pK = var1;
   }

   public int fb() {
      return this.pL;
   }

   public void r(int var1) {
      this.pL = var1;
   }

   public void fc() {
      this.pK = null;
      this.pL = -1;
   }
}
