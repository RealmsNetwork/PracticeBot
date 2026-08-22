package com.sheldera.practicebot.x;

import com.sheldera.practicebot.PracticeBotPlugin;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class N {
   private static final Pattern sV = Pattern.compile("[a-z0-9_-]+");
   private static final String sW = "deleted-default-templates";
   private final PracticeBotPlugin sX;
   private File sY;
   private FileConfiguration pe;
   private final Map<String, E> sZ = new LinkedHashMap<>();

   public N(PracticeBotPlugin var1) {
      this.sX = var1;
   }

   public void a() {
      this.sY = new File(this.sX.getDataFolder(), "bot_templates.yml");
      this.pe = Z.a(this.sX, this.sY, "bot_templates.yml");
      if (this.gH()) {
         this.gK();
      }

      this.sZ.clear();
      ConfigurationSection var1 = this.pe.getConfigurationSection("templates");
      if (var1 == null) {
         this.sX.getLogger().warning("bot_templates.yml does not contain a templates section.");
      } else {
         for (String var3 : var1.getKeys(false)) {
            ConfigurationSection var4 = var1.getConfigurationSection(var3);
            if (var4 != null) {
               E var5 = this.b(var3, var4);
               if (var5 != null) {
                  this.sZ.put(var3.toLowerCase(Locale.ROOT), var5);
               }
            }
         }

         this.sX.debugLog("Loaded " + this.sZ.size() + " bot template(s).");
      }
   }

   public E i1(String var1) {
      if (var1 == null) {
         return null;
      } else {
         E var2 = this.sZ.get(var1.toLowerCase(Locale.ROOT));
         return var2 != null ? var2.fX() : null;
      }
   }

   public Set<String> gG() {
      return Collections.unmodifiableSet(this.sZ.keySet());
   }

   public boolean j0(String var1) {
      return var1 != null && this.sZ.containsKey(var1.toLowerCase(Locale.ROOT));
   }

   public String normalizeTemplateKey(String var1) {
      return var1 == null ? "" : var1.trim().toLowerCase(Locale.ROOT);
   }

   public String j1(String var1) {
      if (var1 == null) {
         return "";
      } else {
         String var2 = var1.trim()
            .toLowerCase(Locale.ROOT)
            .replaceAll("\\s+", "_")
            .replaceAll("[^a-z0-9_-]", "_")
            .replaceAll("_+", "_")
            .replaceAll("^[_-]+", "")
            .replaceAll("[_-]+$", "");
         return this.normalizeTemplateKey(var2);
      }
   }

   public boolean k0(String var1) {
      String var2 = this.normalizeTemplateKey(var1);
      return !var2.isEmpty() && sV.matcher(var2).matches();
   }

   public E k1(String var1) {
      String var2 = this.normalizeTemplateKey(var1);
      b var3 = this.sX.getConfigManager();
      FileConfiguration var4 = this.sX.getDefaultInvConfig();
      af var5 = var3.e();
      String var6 = var5 != null ? var5.skillLevel : "MEDIUM";
      ag var7 = this.q0(var6);
      return new E(
         var2,
         var2,
         var2,
         "",
         a.NORMAL,
         false,
         M.REQUIRED,
         J.REMOVE,
         L.DESPAWN,
         60L,
         0L,
         new G(var3.p(), var3.q(), var3.r(), var3.s(), var3.t(), false, false, false, true),
         new H(false, false, false, false, false, false, 1, 1, 2, 0),
         new K(
            var4.getString("armor-type", "netherite").toLowerCase(Locale.ROOT),
            var4.getString("armor-type", "netherite").toLowerCase(Locale.ROOT),
            var4.getString("leggings-material", "netherite").toLowerCase(Locale.ROOT),
            var4.getString("armor-type", "netherite").toLowerCase(Locale.ROOT),
            var4.getString("helmet-enchant", "protection").toLowerCase(Locale.ROOT),
            var4.getString("chestplate-enchant", "protection").toLowerCase(Locale.ROOT),
            var4.getString("leggings-enchant", "protection").toLowerCase(Locale.ROOT),
            var4.getString("boots-enchant", "protection").toLowerCase(Locale.ROOT),
            var4.getString("offhand-type", "totem").toLowerCase(Locale.ROOT),
            Math.max(0, var4.getInt("totem-amount", 64)),
            null,
            new F(var4.getString("helmet-trim-pattern", ""), var4.getString("helmet-trim-material", "")),
            new F(var4.getString("chest-trim-pattern", ""), var4.getString("chest-trim-material", "")),
            new F(var4.getString("legs-trim-pattern", ""), var4.getString("legs-trim-material", "")),
            new F(var4.getString("boots-trim-pattern", ""), var4.getString("boots-trim-material", ""))
         ),
         new I(
            false,
            var5 != null ? var5.skillLevel : "MEDIUM",
            var5 != null ? var5.aggressionWeight : 1.3,
            var5 == null || var5.usePearls,
            var5 != null && var5.useMace,
            var5 != null && var5.useGoldenApples,
            var5 == null || var5.placeObsidian,
            var5 != null && var5.breakBlocks,
            var5 != null && var5.strafingEnabled,
            var5 != null && var5.anchoringMode,
            var5 != null ? var5.healThreshold : 0.4,
            var5 != null ? var5.lowHpCrystalLethalReserve : 0.05,
            var5 != null ? var5.pearlCooldownMs : 1800L,
            var5 != null ? var5.J : 100.0,
            var7.aF,
            var7.aG,
            var7.aH,
            var7.aI,
            var7.aJ,
            var7.aK,
            var7.aL,
            var7.aM,
            var7.aN,
            var7.aO,
            var7.aP,
            var7.aQ
         )
      );
   }

   public synchronized boolean a(E var1, String var2) {
      if (!this.sX.isLicenseActive()) {
         return false;
      } else if (var1 == null) {
         return false;
      } else {
         this.gJ();
         String var3 = this.normalizeTemplateKey(var1.fe());
         if (!this.k0(var3)) {
            return false;
         } else {
            ConfigurationSection var4 = this.pe.getConfigurationSection("templates");
            if (var4 == null) {
               var4 = this.pe.createSection("templates");
            }

            String var5 = this.normalizeTemplateKey(var2);
            if (!var5.isEmpty() && !var5.equals(var3)) {
               this.pe.set("templates." + var5, null);
               this.q1(var5);
            }

            this.r0(var3);
            this.pe.set("templates." + var3, null);
            this.a(var4.createSection(var3), var1);
            if (!this.gK()) {
               return false;
            } else {
               this.a();
               return true;
            }
         }
      }
   }

   public synchronized boolean l0(String var1) {
      if (!this.sX.isLicenseActive()) {
         return false;
      } else {
         this.gJ();
         String var2 = this.normalizeTemplateKey(var1);
         if (!this.sZ.containsKey(var2)) {
            return false;
         } else {
            this.pe.set("templates." + var2, null);
            this.q1(var2);
            if (!this.gK()) {
               return false;
            } else {
               this.a();
               return true;
            }
         }
      }
   }

   private E b(String var1, ConfigurationSection var2) {
      try {
         b var3 = this.sX.getConfigManager();
         FileConfiguration var4 = this.sX.getDefaultInvConfig();
         af var5 = var3.e();
         a var6 = this.m0(var2.getString("bot-type", "NORMAL"));
         boolean var7 = var2.getBoolean("allow-gui", false);
         M var8 = this.m1(var2.getString("target-binding", "REQUIRED"));
         J var9 = this.n0(var2.getString("death-mode", "REMOVE"));
         L var10 = this.n1(var2.getString("kill-mode", "DESPAWN"));
         long var11 = var2.contains("respawn-delay-ticks")
            ? Math.max(0L, var2.getLong("respawn-delay-ticks"))
            : Math.max(0L, Math.round(var2.getDouble("respawn-delay-seconds", 3.0) * 20.0));
         long var13 = var2.contains("warm-up-ticks")
            ? Math.max(0L, var2.getLong("warm-up-ticks"))
            : Math.max(0L, Math.round(var2.getDouble("warm-up-seconds", 0.0) * 20.0));
         ConfigurationSection var15 = var2.getConfigurationSection("settings");
         boolean var16 = this.a(var15, "look-at-target", this.a(var15, "look-at-owner", var3.p()));
         boolean var17 = this.a(var15, "follow-target", this.a(var15, "follow-owner", var3.q()));
         boolean var18 = this.a(var15, "random-walk", var3.r());
         boolean var19 = this.a(var15, "hold-shield", var3.s());
         boolean var20 = this.a(var15, "use-shield", var3.t());
         boolean var21 = this.a(var15, "resistance", false);
         boolean var22 = this.a(var15, "frozen", false);
         boolean var23 = this.a(var15, "shield-in-main-hand", false);
         boolean var24 = this.a(var15, "pvp-enabled", var6 == a.NORMAL);
         G var25 = new G(var16, var17, var18, var19, var20, var21, var22, var23, var24);
         ConfigurationSection var26 = var2.getConfigurationSection("combat");
         H var27 = new H(
            this.a(var26, "strafe", false),
            this.a(var26, "wtap", false),
            this.a(var26, "stap", false),
            this.a(var26, "crits", false),
            this.a(var26, "shield-breaker", false),
            this.a(var26, "retreat", false),
            this.o0(this.a(var26, "reach-mode", "NORMAL")),
            this.o1(this.a(var26, "aggression", "MEDIUM")),
            this.p0(this.a(var26, "crit-chance", "75")),
            this.p1(this.a(var26, "crit-speed", "SLOW"))
         );
         ConfigurationSection var28 = var2.getConfigurationSection("inventory");
         String var29 = this.a(var28, "armor-type", var4.getString("armor-type", "netherite")).toLowerCase(Locale.ROOT);
         K var30 = new K(
            this.a(var28, "helmet-material", var29).toLowerCase(Locale.ROOT),
            this.a(var28, "chestplate-material", var29).toLowerCase(Locale.ROOT),
            this.a(var28, "leggings-material", var4.getString("leggings-material", "netherite")).toLowerCase(Locale.ROOT),
            this.a(var28, "boots-material", var29).toLowerCase(Locale.ROOT),
            this.a(var28, "helmet-enchant", var4.getString("helmet-enchant", "protection")).toLowerCase(Locale.ROOT),
            this.a(var28, "chestplate-enchant", var4.getString("chestplate-enchant", "protection")).toLowerCase(Locale.ROOT),
            this.a(var28, "leggings-enchant", var4.getString("leggings-enchant", "protection")).toLowerCase(Locale.ROOT),
            this.a(var28, "boots-enchant", var4.getString("boots-enchant", "protection")).toLowerCase(Locale.ROOT),
            this.a(var28, "offhand-type", var4.getString("offhand-type", "totem")).toLowerCase(Locale.ROOT),
            Math.max(0, this.a(var28, "totem-count", var4.getInt("totem-amount", 64))),
            this.a(var28 != null ? var28.getConfigurationSection("main-hand") : null),
            this.a(var28, "helmet", var4, "helmet-trim-pattern", "helmet-trim-material"),
            this.a(var28, "chest", var4, "chest-trim-pattern", "chest-trim-material"),
            this.a(var28, "legs", var4, "legs-trim-pattern", "legs-trim-material"),
            this.a(var28, "boots", var4, "boots-trim-pattern", "boots-trim-material")
         );
         ConfigurationSection var31 = var2.getConfigurationSection("cpvp");
         String var32 = this.a(var31, "skill-level", var5 != null ? var5.skillLevel : "MEDIUM").toUpperCase(Locale.ROOT);
         ag var33 = this.q0(var32);
         long var34 = this.a(var31, "place-delay-min-ms", "place-delay-ms", var33.aF);
         long var36 = this.a(var31, "place-delay-max-ms", "place-delay-ms", var33.aG);
         long var38 = this.a(var31, "break-delay-min-ms", "break-delay-ms", var33.aH);
         long var40 = this.a(var31, "break-delay-max-ms", "break-delay-ms", var33.aI);
         I var42 = new I(
            this.a(var31, "enabled", var6 == a.CPVP),
            var32,
            this.a(var31, "aggression-weight", var5 != null ? var5.aggressionWeight : var33.aB),
            this.a(var31, "use-pearls", var5 == null || var5.usePearls),
            this.a(var31, "use-mace", var5 != null && var5.useMace),
            this.a(var31, "use-golden-apples", var5 != null && var5.useGoldenApples),
            this.a(var31, "place-obsidian", var5 == null || var5.placeObsidian),
            this.a(var31, "break-blocks", var5 != null && var5.breakBlocks),
            this.a(var31, "strafing-enabled", var5 != null && var5.strafingEnabled),
            this.a(var31, "anchoring-mode", var5 != null && var5.anchoringMode),
            this.a(var31, "heal-threshold", var5 != null ? var5.healThreshold : 0.4),
            this.a(var31, "low-hp-crystal-lethal-reserve", var5 != null ? var5.lowHpCrystalLethalReserve : 0.05),
            Math.max(500L, this.a(var31, "pearl-cooldown-ms", var5 != null ? var5.pearlCooldownMs : 1800L)),
            this.a(var31, "fov-degrees", var5 != null ? var5.J : 100.0),
            var34,
            var36,
            var38,
            var40,
            Math.max(100L, this.a(var31, "sword-delay-ms", var33.aJ)),
            this.a(var31, "reaction-hesitation-chance-percent", var33.aK),
            this.a(var31, "reaction-hesitation-min-ms", var33.aL),
            this.a(var31, "reaction-hesitation-max-ms", var33.aM),
            this.a(var31, "crystal-miss-chance-percent", var33.aN),
            this.a(var31, "crystal-miss-min-ms", var33.aO),
            this.a(var31, "crystal-miss-max-ms", var33.aP),
            this.a(var31, "non-urgent-pearl-restraint-chance-percent", var33.aQ)
         );
         String var43 = var2.getString("name", var1);
         String var44 = var2.getString("display-name", var43);
         String var45 = var2.getString("skin-name", "");
         return new E(var1, var43, var44, var45, var6, var7, var8, var9, var10, var11, var13, var25, var27, var30, var42);
      } catch (Exception var46) {
         this.sX.getLogger().warning("Failed to load bot template '" + var1 + "': " + var46.getMessage());
         return null;
      }
   }

   private F a(ConfigurationSection var1, String var2, FileConfiguration var3, String var4, String var5) {
      ConfigurationSection var6 = var1 != null ? var1.getConfigurationSection("trims") : null;
      ConfigurationSection var7 = var6 != null ? var6.getConfigurationSection(var2) : null;
      String var8 = var7 != null && var7.contains("pattern") ? var7.getString("pattern", "") : var3.getString(var4, "");
      String var9 = var7 != null && var7.contains("material") ? var7.getString("material", "") : var3.getString(var5, "");
      return new F(var8, var9);
   }

   private ItemStack a(ConfigurationSection var1) {
      if (var1 != null && var1.contains("material")) {
         Material var2 = Material.matchMaterial(var1.getString("material", ""));
         if (var2 != null && !var2.isAir()) {
            ItemStack var3 = new ItemStack(var2, Math.max(1, var1.getInt("amount", 1)));
            ItemMeta var4 = var3.getItemMeta();
            if (var4 != null) {
               if (var1.contains("name")) {
                  var4.setDisplayName(Y.ag(var1.getString("name", "")));
               }

               if (var1.isList("lore")) {
                  ArrayList var5 = new ArrayList();

                  for (String var7 : var1.getStringList("lore")) {
                     var5.add(Y.ag(var7));
                  }

                  var4.setLore(var5);
               }

               var4.setUnbreakable(var1.getBoolean("unbreakable", true));
               var4.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE});
               var3.setItemMeta(var4);
            }

            ConfigurationSection var9 = var1.getConfigurationSection("enchantments");
            if (var9 != null) {
               for (String var11 : var9.getKeys(false)) {
                  Enchantment var8 = this.l1(var11);
                  if (var8 == null) {
                     this.sX.getLogger().warning("Ignoring unknown main-hand enchantment '" + var11 + "' in bot template.");
                  } else {
                     var3.addUnsafeEnchantment(var8, Math.max(1, var9.getInt(var11, 1)));
                  }
               }
            }

            return var3;
         } else {
            this.sX.getLogger().warning("Ignoring template main-hand item with invalid material: " + var1.getString("material", ""));
            return null;
         }
      } else {
         return null;
      }
   }

   private Enchantment l1(String var1) {
      if (var1 != null && !var1.isBlank()) {
         Enchantment var2 = (Enchantment)Registry.ENCHANTMENT.get(NamespacedKey.minecraft(var1.toLowerCase(Locale.ROOT)));
         if (var2 != null) {
            return var2;
         } else {
            String var4 = var1.toLowerCase(Locale.ROOT);

            String var3 = switch (var4) {
               case "sharpness" -> "DAMAGE_ALL";
               case "knockback" -> "KNOCKBACK";
               case "fire_aspect" -> "FIRE_ASPECT";
               case "unbreaking" -> "DURABILITY";
               default -> var1.toUpperCase(Locale.ROOT);
            };
            return Enchantment.getByName(var3);
         }
      } else {
         return null;
      }
   }

   private a m0(String var1) {
      if (var1 == null) {
         return a.NORMAL;
      } else {
         try {
            return a.valueOf(var1.trim().toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var3) {
            this.sX.getLogger().warning("Unknown bot type '" + var1 + "' in bot_templates.yml, using NORMAL.");
            return a.NORMAL;
         }
      }
   }

   private M m1(String var1) {
      if (var1 == null) {
         return M.REQUIRED;
      } else {
         try {
            return M.valueOf(var1.trim().toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var3) {
            this.sX.getLogger().warning("Unknown target-binding mode '" + var1 + "' in bot_templates.yml, using REQUIRED.");
            return M.REQUIRED;
         }
      }
   }

   private J n0(String var1) {
      if (var1 == null) {
         return J.REMOVE;
      } else {
         try {
            return J.valueOf(var1.trim().toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var3) {
            this.sX.getLogger().warning("Unknown death-mode '" + var1 + "' in bot_templates.yml, using REMOVE.");
            return J.REMOVE;
         }
      }
   }

   private L n1(String var1) {
      if (var1 == null) {
         return L.DESPAWN;
      } else {
         String var2 = var1.trim().toUpperCase(Locale.ROOT);

         return switch (var2) {
            case "DESPAWN", "REMOVE" -> L.DESPAWN;
            case "RESPAWN" -> L.RESPAWN;
            case "NONE" -> L.NONE;
            default -> {
               this.sX.getLogger().warning("Unknown kill-mode '" + var1 + "' in bot_templates.yml, using DESPAWN.");
               yield L.DESPAWN;
            }
         };
      }
   }

   private int o0(String var1) {
      if (var1 == null) {
         return 1;
      } else {
         String var2 = var1.trim().toUpperCase(Locale.ROOT);

         return switch (var2) {
            case "0", "SHORT" -> 0;
            case "1", "NORMAL" -> 1;
            case "2", "EXTENDED" -> 2;
            case "3", "ADVANCED" -> 3;
            default -> 1;
         };
      }
   }

   private int o1(String var1) {
      if (var1 == null) {
         return 1;
      } else {
         String var2 = var1.trim().toUpperCase(Locale.ROOT);

         return switch (var2) {
            case "0", "LOW" -> 0;
            case "1", "MEDIUM" -> 1;
            case "2", "HIGH" -> 2;
            default -> 1;
         };
      }
   }

   private int p0(String var1) {
      if (var1 == null) {
         return 2;
      } else {
         String var2 = var1.trim().toUpperCase(Locale.ROOT);

         return switch (var2) {
            case "0", "25", "25%", "LOW" -> 0;
            case "1", "50", "50%", "MEDIUM" -> 1;
            case "2", "75", "75%", "HIGH" -> 2;
            case "3", "100", "100%", "MAX" -> 3;
            default -> 2;
         };
      }
   }

   private int p1(String var1) {
      if (var1 == null) {
         return 0;
      } else {
         String var2 = var1.trim().toUpperCase(Locale.ROOT);

         return switch (var2) {
            case "0", "SLOW" -> 0;
            case "1", "NORMAL" -> 1;
            case "2", "FAST" -> 2;
            default -> 0;
         };
      }
   }

   private ag q0(String var1) {
      ag var2 = ae.aw(var1 == null ? "" : var1.toUpperCase(Locale.ROOT));
      if (var2 != null) {
         return var2;
      } else {
         var2 = ae.aw("MEDIUM");
         return var2 != null ? var2 : new ag(1.3, 1.0, 120L, 190L, 80L, 135L, 600L, 14, 70L, 160L, 7, 90L, 210L, 18);
      }
   }

   private long a(ConfigurationSection var1, String var2, String var3, long var4) {
      if (var1 == null) {
         return var4;
      } else if (var1.contains(var2)) {
         return var1.getLong(var2, var4);
      } else {
         return var3 != null && var1.contains(var3) ? var1.getLong(var3, var4) : var4;
      }
   }

   private boolean a(ConfigurationSection var1, String var2, boolean var3) {
      return var1 != null ? var1.getBoolean(var2, var3) : var3;
   }

   private String a(ConfigurationSection var1, String var2, String var3) {
      return var1 != null ? var1.getString(var2, var3) : var3;
   }

   private int a(ConfigurationSection var1, String var2, int var3) {
      return var1 != null ? var1.getInt(var2, var3) : var3;
   }

   private long a(ConfigurationSection var1, String var2, long var3) {
      return var1 != null ? var1.getLong(var2, var3) : var3;
   }

   private double a(ConfigurationSection var1, String var2, double var3) {
      return var1 != null ? var1.getDouble(var2, var3) : var3;
   }

   private boolean gH() {
      if (this.pe == null) {
         return false;
      } else {
         List var1 = this.gI();
         boolean var2 = false;
         ConfigurationSection var3 = this.pe.getConfigurationSection("templates");
         if (var3 != null) {
            for (String var5 : (java.util.Collection<String>)(java.util.Collection<?>) var1) {
               if (var3.contains(var5)) {
                  var3.set(var5, null);
                  var2 = true;
               }
            }
         }

         List var6 = this.pe.getStringList("deleted-default-templates");
         if (!var6.equals(var1)) {
            this.pe.set("deleted-default-templates", var1);
            var2 = true;
         }

         return var2;
      }
   }

   private void q1(String var1) {
      String var2 = this.normalizeTemplateKey(var1);
      if (this.k0(var2)) {
         List var3 = this.gI();
         if (!var3.contains(var2)) {
            var3.add(var2);
            this.pe.set("deleted-default-templates", var3);
         }
      }
   }

   private void r0(String var1) {
      String var2 = this.normalizeTemplateKey(var1);
      if (this.k0(var2)) {
         List var3 = this.gI();
         if (var3.remove(var2)) {
            this.pe.set("deleted-default-templates", var3);
         }
      }
   }

   private List<String> gI() {
      ArrayList var1 = new ArrayList();
      if (this.pe == null) {
         return var1;
      } else {
         for (String var3 : this.pe.getStringList("deleted-default-templates")) {
            String var4 = this.normalizeTemplateKey(var3);
            if (this.k0(var4) && !var1.contains(var4)) {
               var1.add(var4);
            }
         }

         return var1;
      }
   }

   private void gJ() {
      if (this.pe == null || this.sY == null) {
         this.a();
      }
   }

   private boolean gK() {
      try {
         this.pe.save(this.sY);
         return true;
      } catch (IOException var2) {
         this.sX.getLogger().severe("Failed to save bot_templates.yml: " + var2.getMessage());
         return false;
      }
   }

   private void a(ConfigurationSection var1, E var2) {
      var1.set("name", var2.fh());
      var1.set("display-name", var2.fY());
      var1.set("skin-name", var2.fZ());
      var1.set("bot-type", var2.getBotType().name());
      var1.set("allow-gui", var2.ga());
      var1.set("target-binding", var2.gb().name());
      var1.set("death-mode", var2.gc().name());
      var1.set("kill-mode", var2.gd().name());
      var1.set("respawn-delay-ticks", null);
      var1.set("respawn-delay-seconds", var2.ge() / 20.0);
      var1.set("warm-up-ticks", null);
      var1.set("warm-up-seconds", var2.gf() / 20.0);
      a var3 = var2.getBotType();
      if (var3 != a.NORMAL && var3 != a.DUMMY) {
         var1.set("settings", null);
      } else {
         G var4 = var2.gg();
         var1.set("settings.look-at-target", var4.go());
         var1.set("settings.follow-target", var4.gp());
         var1.set("settings.random-walk", var4.isRandomWalk());
         var1.set("settings.hold-shield", var4.isHoldShield());
         var1.set("settings.use-shield", var4.isUseShield());
         var1.set("settings.resistance", var4.isResistance());
         var1.set("settings.frozen", var3 == a.DUMMY && var4.isFrozen());
         var1.set("settings.shield-in-main-hand", var4.isShieldInMainHand());
         if (var3 == a.NORMAL) {
            var1.set("settings.pvp-enabled", var4.isPvpEnabled());
         } else {
            var1.set("settings.pvp-enabled", null);
         }
      }

      if (var3 == a.NORMAL) {
         H var6 = var2.gh();
         var1.set("combat.strafe", var6.gq());
         var1.set("combat.wtap", var6.gr());
         var1.set("combat.stap", var6.gs());
         var1.set("combat.crits", var6.gt());
         var1.set("combat.shield-breaker", var6.gu());
         var1.set("combat.retreat", var6.gv());
         var1.set("combat.reach-mode", this.x(var6.gw()));
         var1.set("combat.aggression", this.y(var6.gx()));
         var1.set("combat.crit-chance", this.z(var6.gy()));
         var1.set("combat.crit-speed", this.aa(var6.gz()));
      } else {
         var1.set("combat", null);
      }

      K var7 = var2.gi();
      var1.set("inventory.armor-type", this.a(var7));
      var1.set("inventory.helmet-material", var7.getHelmetMaterial());
      var1.set("inventory.chestplate-material", var7.getChestplateMaterial());
      var1.set("inventory.leggings-material", var7.getLeggingsMaterial());
      var1.set("inventory.boots-material", var7.getBootsMaterial());
      var1.set("inventory.helmet-enchant", var7.getHelmetEnchant());
      var1.set("inventory.chestplate-enchant", var7.getChestplateEnchant());
      var1.set("inventory.leggings-enchant", var7.getLeggingsEnchant());
      var1.set("inventory.boots-enchant", var7.getBootsEnchant());
      var1.set("inventory.offhand-type", var7.getOffhandType());
      var1.set("inventory.totem-count", var7.getTotemCount());
      this.a(var1, var7.getCustomMainHand());
      this.a(var1, "helmet", var7.gC());
      this.a(var1, "chest", var7.gD());
      this.a(var1, "legs", var7.gE());
      this.a(var1, "boots", var7.gF());
      if (var3 == a.CPVP) {
         I var5 = var2.gj();
         var1.set("cpvp.enabled", true);
         var1.set("cpvp.skill-level", var5.gB());
         var1.set("cpvp.aggression-weight", var5.aI());
         var1.set("cpvp.use-pearls", var5.aK());
         var1.set("cpvp.use-mace", var5.aL());
         var1.set("cpvp.use-golden-apples", var5.aM());
         var1.set("cpvp.place-obsidian", var5.aN());
         var1.set("cpvp.break-blocks", var5.aO());
         var1.set("cpvp.strafing-enabled", var5.aP());
         var1.set("cpvp.anchoring-mode", var5.aQ());
         var1.set("cpvp.heal-threshold", var5.aR());
         var1.set("cpvp.low-hp-crystal-lethal-reserve", var5.aS());
         var1.set("cpvp.pearl-cooldown-ms", var5.aT());
         var1.set("cpvp.fov-degrees", var5.aU());
         var1.set("cpvp.place-delay-min-ms", var5.aW());
         var1.set("cpvp.place-delay-max-ms", var5.aX());
         var1.set("cpvp.break-delay-min-ms", var5.aZ());
         var1.set("cpvp.break-delay-max-ms", var5.bA());
         var1.set("cpvp.sword-delay-ms", var5.bJ());
         var1.set("cpvp.reaction-hesitation-chance-percent", var5.bB());
         var1.set("cpvp.reaction-hesitation-min-ms", var5.bC());
         var1.set("cpvp.reaction-hesitation-max-ms", var5.bD());
         var1.set("cpvp.crystal-miss-chance-percent", var5.bE());
         var1.set("cpvp.crystal-miss-min-ms", var5.bF());
         var1.set("cpvp.crystal-miss-max-ms", var5.bG());
         var1.set("cpvp.non-urgent-pearl-restraint-chance-percent", var5.bH());
      } else {
         var1.set("cpvp", null);
      }
   }

   private String a(K var1) {
      return var1.getHelmetMaterial().equalsIgnoreCase(var1.getChestplateMaterial()) && var1.getChestplateMaterial().equalsIgnoreCase(var1.getBootsMaterial())
         ? var1.getChestplateMaterial()
         : null;
   }

   private void a(ConfigurationSection var1, String var2, F var3) {
      String var4 = "inventory.trims." + var2;
      if (var3 == null) {
         var1.set(var4, null);
      } else {
         var1.set(var4 + ".pattern", var3.gl());
         var1.set(var4 + ".material", var3.gm());
      }
   }

   private void a(ConfigurationSection var1, ItemStack var2) {
      String var3 = "inventory.main-hand";
      if (var2 != null && !var2.getType().isAir()) {
         var1.set(var3 + ".material", var2.getType().name());
         var1.set(var3 + ".amount", Math.max(1, var2.getAmount()));
         ItemMeta var4 = var2.getItemMeta();
         if (var4 != null) {
            var1.set(var3 + ".name", var4.hasDisplayName() ? Y.bm(var4.getDisplayName()) : null);
            var1.set(var3 + ".lore", this.c(var4.getLore()));
            var1.set(var3 + ".unbreakable", var4.isUnbreakable());
            LinkedHashMap var5 = new LinkedHashMap();

            for (Entry var7 : var4.getEnchants().entrySet()) {
               String var8 = this.a((Enchantment)var7.getKey());
               if (var8 != null) {
                  var5.put(var8, (Integer)var7.getValue());
               }
            }

            var1.set(var3 + ".enchantments", var5.isEmpty() ? null : var5);
         } else {
            var1.set(var3 + ".name", null);
            var1.set(var3 + ".lore", null);
            var1.set(var3 + ".unbreakable", false);
            var1.set(var3 + ".enchantments", null);
         }
      } else {
         var1.set(var3, null);
      }
   }

   private List<String> c(List<String> var1) {
      if (var1 != null && !var1.isEmpty()) {
         ArrayList var2 = new ArrayList();

         for (String var4 : var1) {
            var2.add(Y.bm(var4));
         }

         return var2;
      } else {
         return null;
      }
   }

   private String a(Enchantment var1) {
      if (var1 == null) {
         return null;
      } else {
         NamespacedKey var2 = Registry.ENCHANTMENT.getKey(var1);
         return var2 != null ? var2.getKey() : var1.getKey().getKey();
      }
   }

   private String x(int var1) {
      return switch (var1) {
         case 0 -> "SHORT";
         default -> "NORMAL";
         case 2 -> "EXTENDED";
         case 3 -> "ADVANCED";
      };
   }

   private String y(int var1) {
      return switch (var1) {
         case 0 -> "LOW";
         case 2 -> "HIGH";
         default -> "MEDIUM";
      };
   }

   private String z(int var1) {
      return switch (var1) {
         case 0 -> "25";
         case 1 -> "50";
         default -> "75";
         case 3 -> "100";
      };
   }

   private String aa(int var1) {
      return switch (var1) {
         case 1 -> "NORMAL";
         case 2 -> "FAST";
         default -> "SLOW";
      };
   }
}
