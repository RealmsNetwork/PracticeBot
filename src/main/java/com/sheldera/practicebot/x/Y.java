package com.sheldera.practicebot.x;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;

public final class Y {
   private static final Pattern tI = Pattern.compile("&#([A-Fa-f0-9]{6})");
   private static final Pattern tJ = Pattern.compile("&x&([A-Fa-f0-9])&([A-Fa-f0-9])&([A-Fa-f0-9])&([A-Fa-f0-9])&([A-Fa-f0-9])&([A-Fa-f0-9])");
   private static final Pattern tK = Pattern.compile("rgb:(\\d{1,3}),(\\d{1,3}),(\\d{1,3})");
   private static final Pattern tL = Pattern.compile("<gradient:#([A-Fa-f0-9]{6}):#([A-Fa-f0-9]{6})>(.*?)</gradient>");

   private Y() {
   }

   public static String ag(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         var0 = v1(var0);
         var0 = u0(var0);
         var0 = u1(var0);
         var0 = v0(var0);
         return w0(var0);
      } else {
         return "";
      }
   }

   public static String t1(String var0) {
      return var0 == null ? "" : ChatColor.stripColor(ag(var0));
   }

   public static String bm(String var0) {
      if (var0 == null) {
         return "";
      } else {
         Pattern var1 = Pattern.compile("§x§([A-Fa-f0-9])§([A-Fa-f0-9])§([A-Fa-f0-9])§([A-Fa-f0-9])§([A-Fa-f0-9])§([A-Fa-f0-9])");
         Matcher var2 = var1.matcher(var0);
         StringBuilder var3 = new StringBuilder();

         while (var2.find()) {
            String var4 = var2.group(1) + var2.group(2) + var2.group(3) + var2.group(4) + var2.group(5) + var2.group(6);
            var2.appendReplacement(var3, "&#" + var4);
         }

         var2.appendTail(var3);
         var0 = var3.toString();
         return var0.replace('§', '&');
      }
   }

   private static String u0(String var0) {
      Matcher var1 = tI.matcher(var0);
      StringBuilder var2 = new StringBuilder();

      while (var1.find()) {
         String var3 = var1.group(1);
         var1.appendReplacement(var2, w1(var3));
      }

      var1.appendTail(var2);
      return var2.toString();
   }

   private static String u1(String var0) {
      Matcher var1 = tJ.matcher(var0);
      StringBuilder var2 = new StringBuilder();

      while (var1.find()) {
         String var3 = var1.group(1) + var1.group(2) + var1.group(3) + var1.group(4) + var1.group(5) + var1.group(6);
         var1.appendReplacement(var2, w1(var3));
      }

      var1.appendTail(var2);
      return var2.toString();
   }

   private static String v0(String var0) {
      Matcher var1 = tK.matcher(var0);
      StringBuilder var2 = new StringBuilder();

      while (var1.find()) {
         int var3 = ab(Integer.parseInt(var1.group(1)));
         int var4 = ab(Integer.parseInt(var1.group(2)));
         int var5 = ab(Integer.parseInt(var1.group(3)));
         String var6 = String.format("%02X%02X%02X", var3, var4, var5);
         var1.appendReplacement(var2, w1(var6));
      }

      var1.appendTail(var2);
      return var2.toString();
   }

   private static String v1(String var0) {
      Matcher var1 = tL.matcher(var0);
      StringBuilder var2 = new StringBuilder();

      while (var1.find()) {
         Color var3 = Color.decode("#" + var1.group(1));
         Color var4 = Color.decode("#" + var1.group(2));
         String var5 = var1.group(3);
         StringBuilder var6 = new StringBuilder();
         int var7 = var5.length();

         for (int var8 = 0; var8 < var7; var8++) {
            char var9 = var5.charAt(var8);
            if (var9 == '&' && var8 + 1 < var7) {
               char var10 = var5.charAt(var8 + 1);
               if ("0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(var10) != -1) {
                  var6.append(var9).append(var10);
                  var8++;
                  continue;
               }
            }

            float var15 = var7 > 1 ? (float)var8 / (var7 - 1) : 0.0F;
            int var11 = (int)(var3.getRed() + var15 * (var4.getRed() - var3.getRed()));
            int var12 = (int)(var3.getGreen() + var15 * (var4.getGreen() - var3.getGreen()));
            int var13 = (int)(var3.getBlue() + var15 * (var4.getBlue() - var3.getBlue()));
            String var14 = String.format("%02X%02X%02X", ab(var11), ab(var12), ab(var13));
            var6.append(w1(var14)).append(var9);
         }

         var1.appendReplacement(var2, Matcher.quoteReplacement(var6.toString()));
      }

      var1.appendTail(var2);
      return var2.toString();
   }

   private static String w0(String var0) {
      return ChatColor.translateAlternateColorCodes('&', var0);
   }

   private static String w1(String var0) {
      StringBuilder var1 = new StringBuilder("§x");

      for (char var5 : var0.toCharArray()) {
         var1.append('§').append(Character.toLowerCase(var5));
      }

      return var1.toString();
   }

   private static int ab(int var0) {
      return Math.max(0, Math.min(255, var0));
   }
}
