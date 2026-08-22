package com.sheldera.practicebot.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class StringObfuscator {
   private static final byte[] rt = new byte[]{74, 123, 44, 93, 30, 111, 58, -117, -100, 13, -2, 33};

   private StringObfuscator() {
   }

   public static String d(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         try {
            byte[] var1 = Base64.getDecoder().decode(var0);
            byte[] var2 = new byte[var1.length];

            for (int var3 = 0; var3 < var1.length; var3++) {
               var2[var3] = (byte)((var1[var3] ^ rt[var3 % rt.length]) & 0xFF);
            }

            return new String(var2, StandardCharsets.UTF_8);
         } catch (Exception var4) {
            return "";
         }
      } else {
         return "";
      }
   }

   private static String h0(String var0) {
      byte[] var1 = var0.getBytes(StandardCharsets.UTF_8);
      byte[] var2 = new byte[var1.length];

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2[var3] = (byte)((var1[var3] ^ rt[var3 % rt.length]) & 0xFF);
      }

      return Base64.getEncoder().encodeToString(var2);
   }

   public static void b(String[] var0) {
      String[] var1 = new String[]{
         "https://your-api.com/license/validate",
         "license",
         "code",
         "valid",
         "true",
         "false",
         "error",
         "License validated successfully",
         "Invalid license",
         "Connection error",
         "Content-Type",
         "application/json",
         "POST",
         "GET"
      };
      System.out.println("// ========== ENCODED STRINGS ==========");
      System.out.println("// Copy these into LicenseManager.java");
      System.out.println();

      for (String var5 : var1) {
         System.out.println("// \"" + var5 + "\"");
         System.out
            .println("private static final String E_" + var5.toUpperCase().replaceAll("[^A-Z0-9]", "_").replaceAll("_+", "_") + " = \"" + h0(var5) + "\";");
         System.out.println();
      }
   }
}
