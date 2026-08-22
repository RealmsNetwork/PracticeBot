package com.sheldera.practicebot.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

public class s0 {
   private final String pM;
   private int pN;
   private Material pO;
   private String name;
   private List<String> pP;
   private int pQ;
   private boolean pR;
   private String pS;
   private String pT;
   private String pU;
   private int pV;
   private List<ItemFlag> pW;
   private Map<String, String> pX;

   public s0(String var1) {
      this.pM = var1;
      this.pN = 0;
      this.pO = Material.STONE;
      this.name = "&fItem";
      this.pP = new ArrayList<>();
      this.pQ = 1;
      this.pR = false;
      this.pS = "none";
      this.pT = null;
      this.pU = null;
      this.pV = -1;
      this.pW = new ArrayList<>();
      this.pX = new HashMap<>();
   }

   public s0 fd() {
      s0 var1 = new s0(this.pM);
      var1.pN = this.pN;
      var1.pO = this.pO;
      var1.name = this.name;
      var1.pP = new ArrayList<>(this.pP);
      var1.pQ = this.pQ;
      var1.pR = this.pR;
      var1.pS = this.pS;
      var1.pT = this.pT;
      var1.pU = this.pU;
      var1.pV = this.pV;
      var1.pW = new ArrayList<>(this.pW);
      var1.pX = new HashMap<>(this.pX);
      return var1;
   }

   public String fe() {
      return this.pM;
   }

   public int ff() {
      return this.pN;
   }

   public Material fg() {
      return this.pO;
   }

   public String fh() {
      return this.name;
   }

   public List<String> fi() {
      return this.pP;
   }

   public int fj() {
      return this.pQ;
   }

   public boolean fk() {
      return this.pR;
   }

   public String fl() {
      return this.pS;
   }

   public String fm() {
      return this.pT;
   }

   public String fn() {
      return this.pU;
   }

   public int fo() {
      return this.pV;
   }

   public List<ItemFlag> fp() {
      return this.pW;
   }

   public Map<String, String> fq() {
      return this.pX;
   }

   public void s(int var1) {
      this.pN = var1;
   }

   public void r(Material var1) {
      this.pO = var1;
   }

   public void bz(String var1) {
      this.name = var1;
   }

   public void a(List<String> var1) {
      this.pP = (List<String>)(var1 != null ? var1 : new ArrayList<>());
   }

   public void t(int var1) {
      this.pQ = Math.max(1, Math.min(64, var1));
   }

   public void r(boolean var1) {
      this.pR = var1;
   }

   public void c0(String var1) {
      this.pS = var1 != null ? var1 : "none";
   }

   public void c1(String var1) {
      this.pT = var1;
   }

   public void d0(String var1) {
      this.pU = var1;
   }

   public void u(int var1) {
      this.pV = var1;
   }

   public void b(List<ItemFlag> var1) {
      this.pW = (List<ItemFlag>)(var1 != null ? var1 : new ArrayList<>());
   }

   public void b(Map<String, String> var1) {
      this.pX = (Map<String, String>)(var1 != null ? var1 : new HashMap<>());
   }

   public void d1(String var1) {
      this.pP.add(var1);
   }

   public void v(int var1) {
      if (var1 >= 0 && var1 < this.pP.size()) {
         this.pP.remove(var1);
      }
   }

   public void fr() {
      this.pP.clear();
   }

   public void a(ItemFlag var1) {
      if (!this.pW.contains(var1)) {
         this.pW.add(var1);
      }
   }

   public void b(ItemFlag var1) {
      this.pW.remove(var1);
   }

   @Override
   public String toString() {
      return "GuiItem{key='" + this.pM + "', slot=" + this.pN + ", material=" + this.pO + ", name='" + this.name + "', action='" + this.pS + "'}";
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         s0 var2 = (s0)var1;
         return Objects.equals(this.pM, var2.pM);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.pM);
   }
}
