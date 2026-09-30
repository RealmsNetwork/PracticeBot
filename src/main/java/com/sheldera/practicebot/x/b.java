package com.sheldera.practicebot.x;

import com.sheldera.practicebot.PracticeBotPlugin;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class b {
   private static final String a = "https://practicebot-license.sheeshgd.workers.dev/api/validate";
   private static final int b = 10000;
   private static final int c = 1440;
   private static final int d = 10;
   private final JavaPlugin e;
   private FileConfiguration f;
   private long g = 0L;
   private af h;
   private Map<String, ag> i;

   public b(JavaPlugin var1) {
      this.e = var1;
   }

   private void debugLog(String var1) {
      if (this.e instanceof PracticeBotPlugin var2) {
         var2.debugLog(var1);
      }
   }

   public void a() {
      this.e.saveDefaultConfig();
      Z.b(this.e);
      this.e.reloadConfig();
      this.f = this.e.getConfig();
      this.b();
      this.g++;
      this.c();
      this.d();
   }

   private void b() {
      boolean var1 = false;
      String[] var2 = new String[]{
         "license.server-url",
         "license.fallback-server-urls",
         "license.legacy-fallback-enabled",
         "license.timeout-ms",
         "license.recheck-interval-minutes",
         "license.daily-jitter-minutes"
      };

      for (String var6 : var2) {
         if (this.f.isSet(var6)) {
            this.f.set(var6, null);
            var1 = true;
         }
      }

      if (var1) {
         this.e.saveConfig();
         this.e.reloadConfig();
         this.f = this.e.getConfig();
      }
   }

   private void c() {
      if (this.i() <= 0.0) {
         this.e.getLogger().warning("Config: bot-health must be positive, using default 40.0");
      }

      if (this.v() < 1) {
         this.e.getLogger().warning("Config: tick-interval must be at least 1, using 1");
      }

      if (this.ai() <= 0.0 || this.aj() <= 0.0 || this.ak() <= 0.0 || this.al() <= 0.0) {
         this.e.getLogger().warning("Config: reach distances must be positive");
      }
   }

   private void d() {
      this.debugLog("Loading CPVP configuration...");
      this.h = new af();
      boolean fastCrystals = this.fastCrystalsCompatibilityEnabled();
      this.h.skillLevel = this.f.getString("cpvp.defaults.skill-level", "MEDIUM");
      this.h.aggressionWeight = this.f.getDouble("cpvp.defaults.aggression-weight", 1.3);
      this.h.usePearls = this.f.getBoolean("cpvp.defaults.use-pearls", true);
      this.h.useMace = this.f.getBoolean("cpvp.defaults.use-mace", false);
      this.h.useGoldenApples = this.f.getBoolean("cpvp.defaults.use-golden-apples", false);
      this.h.placeObsidian = this.f.getBoolean("cpvp.defaults.place-obsidian", true);
      this.h.breakBlocks = this.f.getBoolean("cpvp.defaults.break-blocks", false);
      this.h.strafingEnabled = this.f.getBoolean("cpvp.defaults.strafing-enabled", true);
      this.h.anchoringMode = this.f.getBoolean("cpvp.defaults.anchoring-mode", false);
      this.h.healThreshold = this.f.getDouble("cpvp.defaults.heal-threshold", 0.4);
      this.h.lowHpCrystalLethalReserve = this.f.getDouble("cpvp.defaults.low-hp-crystal-lethal-reserve", 0.05);
      this.h.pearlCooldownMs = this.f.getLong("cpvp.defaults.pearl-cooldown-ms", 1800L);
      this.h.J = Math.max(1.0, Math.min(360.0, this.f.getDouble("cpvp.defaults.fov-degrees", 100.0)));
      this.debugLog("CPVP defaults: skill=" + this.h.skillLevel);
      this.i = new LinkedHashMap<>();
      if (this.f.isConfigurationSection("cpvp.difficulty-levels")) {
         Set var1 = this.f.getConfigurationSection("cpvp.difficulty-levels").getKeys(false);
         this.debugLog("Found " + var1.size() + " difficulty levels in config");

         for (String var3 : (java.util.Collection<String>)(java.util.Collection<?>) var1) {
            String var4 = "cpvp.difficulty-levels." + var3;
            double var5 = this.f.getDouble(var4 + ".aggression-weight", 1.0);
            double var7 = this.a(var3, var5);
            long var9 = this.f.getLong(var4 + ".place-delay-ms", 200L);
            long var11 = this.f.getLong(var4 + ".break-delay-ms", 160L);
            long var13 = this.f.getLong(var4 + ".place-delay-min-ms", var9);
            long var15 = this.f.getLong(var4 + ".place-delay-max-ms", var9);
            long var17 = this.f.getLong(var4 + ".break-delay-min-ms", var11);
            long var19 = this.f.getLong(var4 + ".break-delay-max-ms", var11);
            long var21 = this.f.getLong(var4 + ".sword-delay-ms", 600L);

            if (fastCrystals &&
                this.f.getBoolean("tactics.fast-crystals.remove-artificial-delay", true)) {
               double scale = Math.max(0.10D, Math.min(
                  1.0D,
                  this.f.getDouble("tactics.fast-crystals.timing-scale", 0.80D)
               ));
               var13 = Math.max(1L, Math.round(var13 * scale));
               var15 = Math.max(var13, Math.round(var15 * scale));
               var17 = Math.max(1L, Math.round(var17 * scale));
               var19 = Math.max(var17, Math.round(var19 * scale));
            }
            int var23 = this.f.getInt(var4 + ".reaction-hesitation-chance-percent", this.a(var3));
            long var24 = this.f.getLong(var4 + ".reaction-hesitation-min-ms", this.b(var3));
            long var26 = this.f.getLong(var4 + ".reaction-hesitation-max-ms", this.c(var3));
            int var28 = this.f.getInt(var4 + ".crystal-miss-chance-percent", this.e(var3));
            long var29 = this.f.getLong(var4 + ".crystal-miss-min-ms", this.f(var3));
            long var31 = this.f.getLong(var4 + ".crystal-miss-max-ms", this.g(var3));
            int var33 = this.f.getInt(var4 + ".non-urgent-pearl-restraint-chance-percent", this.h(var3));
            ag var34 = new ag(var5, var7, var13, var15, var17, var19, var21, var23, var24, var26, var28, var29, var31, var33);
            this.i.put(var3.toUpperCase(), var34);
            this.debugLog(
               "  Loaded level "
                  + var3
                  + ": place="
                  + var34.aF
                  + "-"
                  + var34.aG
                  + "ms, break="
                  + var34.aH
                  + "-"
                  + var34.aI
                  + "ms, reaction="
                  + var34.aK
                  + "%, miss="
                  + var34.aN
                  + "%, pearl-restraint="
                  + var34.aQ
                  + "%"
            );
         }
      } else {
         this.e.getLogger().warning("No cpvp.difficulty-levels section found in config!");
      }

      if (this.i.isEmpty()) {
         this.e.getLogger().warning("No CPVP difficulty levels loaded, using hardcoded defaults");
         this.i.put("EASY", new ag(0.7, 1.5, 220L, 320L, 130L, 190L, 800L, 35, 180L, 380L, 18, 160L, 360L, 45));
         this.i.put("MEDIUM", new ag(1.3, 1.0, 120L, 190L, 80L, 135L, 600L, 14, 70L, 160L, 7, 90L, 210L, 18));
         this.i.put("HARD", new ag(1.5, 0.8, 15L, 35L, 13L, 45L, 500L, 4, 20L, 55L, 2, 40L, 90L, 5));
         this.i.put("PRO", new ag(2.0, 0.5, 3L, 14L, 2L, 18L, 500L, 0, 0L, 0L, 0, 0L, 0L, 0));
      }

      ae.a(this.i);
      this.debugLog("CPVP config loaded successfully with " + this.i.size() + " difficulty levels");
   }

   private boolean fastCrystalsCompatibilityEnabled() {
      String mode = this.f.getString(
         "tactics.fast-crystals.mode", "auto"
      );

      if ("force-off".equalsIgnoreCase(mode)) {
         return false;
      }

      if ("force-on".equalsIgnoreCase(mode)) {
         return true;
      }

      for (Plugin plugin : org.bukkit.Bukkit.getPluginManager().getPlugins()) {
         if (!plugin.isEnabled()) {
            continue;
         }

         String name = plugin.getName()
            .toLowerCase(java.util.Locale.ROOT)
            .replace(" ", "")
            .replace("-", "")
            .replace("_", "");

         if (name.equals("fastcrystals") ||
             name.equals("fastcrystalsreborn") ||
             name.equals("fastercrystals")) {
            return true;
         }
      }

      return false;
   }

   private int a(String var1) {
      String var2 = var1 == null ? "" : var1.toUpperCase();

      return switch (var2) {
         case "EASY" -> 35;
         case "MEDIUM" -> 14;
         case "HARD" -> 4;
         default -> 0;
      };
   }

   private long b(String var1) {
      String var2 = var1 == null ? "" : var1.toUpperCase();

      return switch (var2) {
         case "EASY" -> 180L;
         case "MEDIUM" -> 70L;
         case "HARD" -> 20L;
         default -> 0L;
      };
   }

   private long c(String var1) {
      String var2 = var1 == null ? "" : var1.toUpperCase();

      return switch (var2) {
         case "EASY" -> 380L;
         case "MEDIUM" -> 160L;
         case "HARD" -> 55L;
         default -> 0L;
      };
   }

   private int e(String var1) {
      String var2 = var1 == null ? "" : var1.toUpperCase();

      return switch (var2) {
         case "EASY" -> 18;
         case "MEDIUM" -> 7;
         case "HARD" -> 2;
         default -> 0;
      };
   }

   private long f(String var1) {
      String var2 = var1 == null ? "" : var1.toUpperCase();

      return switch (var2) {
         case "EASY" -> 160L;
         case "MEDIUM" -> 90L;
         case "HARD" -> 40L;
         default -> 0L;
      };
   }

   private long g(String var1) {
      String var2 = var1 == null ? "" : var1.toUpperCase();

      return switch (var2) {
         case "EASY" -> 360L;
         case "MEDIUM" -> 210L;
         case "HARD" -> 90L;
         default -> 0L;
      };
   }

   private int h(String var1) {
      String var2 = var1 == null ? "" : var1.toUpperCase();

      return switch (var2) {
         case "EASY" -> 45;
         case "MEDIUM" -> 18;
         case "HARD" -> 5;
         default -> 0;
      };
   }

   private double a(String var1, double var2) {
      if (var1 != null) {
         String var4 = var1.toUpperCase();
         switch (var4) {
            case "EASY":
               return 1.5;
            case "MEDIUM":
               return 1.0;
            case "HARD":
               return 0.8;
            case "PRO":
               return 0.5;
         }
      }

      return Math.max(0.5, Math.min(1.5, 2.0 - var2 * 0.6));
   }

   public af e() {
      return this.h;
   }

   public Map<String, ag> f() {
      return this.i;
   }

   public ag i(String var1) {
      return this.i == null ? null : this.i.get(var1.toUpperCase());
   }

   public FileConfiguration g() {
      return this.f;
   }

   public long h() {
      return this.g;
   }

   public double i() {
      return Math.max(1.0, this.f.getDouble("default.bot-health", 40.0));
   }

   public boolean j() {
      return this.f.getBoolean("default.collidable", true);
   }

   public boolean k() {
      return this.f.getBoolean("default.bot-invulnerable", false);
   }

   public boolean l() {
      return this.f.getBoolean("default.allow-multiple-bots", false);
   }

   public String m() {
      return this.f.getString("default.bot-prefix", "");
   }

   public boolean n() {
      return this.f.getBoolean("default.despawn-on-owner-death", true);
   }

   public List<String> o() {
      return this.f.getStringList("default.disabled-worlds");
   }

   public boolean j(String var1) {
      if (var1 == null) {
         return false;
      } else {
         for (String var4 : this.o()) {
            if (var4.equalsIgnoreCase(var1)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean p() {
      return this.f.getBoolean("default-behaviour.look-at-owner", true);
   }

   public boolean q() {
      return this.f.getBoolean("default-behaviour.follow-owner", false);
   }

   public boolean r() {
      return this.f.getBoolean("default-behaviour.random-walk", false);
   }

   public boolean s() {
      return this.f.getBoolean("default-behaviour.hold-shield", false);
   }

   public boolean t() {
      return this.f.getBoolean("default-behaviour.use-shield", false);
   }

   public boolean u() {
      return this.f.getBoolean("default-behaviour.pvp-enabled", false);
   }

   public int v() {
      return Math.max(1, this.f.getInt("ai.tick-interval", 1));
   }

   public double w() {
      return Math.max(1.0, this.f.getDouble("ai.random-walk-radius", 6.0));
   }

   public long aa() {
      return Math.max(0L, this.f.getLong("pvp.base-cooldown-ms", 500L));
   }

   public long ab() {
      return Math.max(0L, this.f.getLong("pvp.crit-cooldowns.slow", 550L));
   }

   public long ac() {
      return Math.max(0L, this.f.getLong("pvp.crit-cooldowns.normal", 450L));
   }

   public long ad() {
      return Math.max(0L, this.f.getLong("pvp.crit-cooldowns.fast", 380L));
   }

   public double ae() {
      return this.f.getDouble("pvp.aggression-cooldown-multipliers.low", 1.1);
   }

   public double af() {
      return this.f.getDouble("pvp.aggression-cooldown-multipliers.medium", 1.0);
   }

   public double ag() {
      return this.f.getDouble("pvp.aggression-cooldown-multipliers.high", 0.9);
   }

   public double ah() {
      return Math.max(1.0, this.f.getDouble("pvp.crit-multiplier", 1.5));
   }

   public double ai() {
      return Math.max(0.5, this.f.getDouble("pvp.reach-distances.short", 2.0));
   }

   public double aj() {
      return Math.max(0.5, this.f.getDouble("pvp.reach-distances.normal", 3.0));
   }

   public double ak() {
      return Math.max(0.5, this.f.getDouble("pvp.reach-distances.extended", 3.5));
   }

   public double al() {
      return Math.max(0.5, this.f.getDouble("pvp.reach-distances.advanced", 4.0));
   }

   public float am() {
      return (float)Math.max(0.1, this.f.getDouble("pvp.chase-speeds.low", 1.0));
   }

   public float an() {
      return (float)Math.max(0.1, this.f.getDouble("pvp.chase-speeds.medium", 1.2));
   }

   public float ao() {
      return (float)Math.max(0.1, this.f.getDouble("pvp.chase-speeds.high", 1.4));
   }

   public double ap() {
      return this.f.getDouble("pvp.knockback.horizontal", 0.4);
   }

   public double aq() {
      return this.f.getDouble("pvp.knockback.vertical", 0.36);
   }

   public double ar() {
      return this.f.getDouble("pvp.knockback.wtap-multiplier", 1.0);
   }

   public double as() {
      return this.f.getDouble("pvp.knockback.wtap-bonus", 0.3);
   }

   public double at() {
      return this.f.getDouble("pvp.strafe.speeds.low", 0.16);
   }

   public double au() {
      return this.f.getDouble("pvp.strafe.speeds.medium", 0.2);
   }

   public double av() {
      return this.f.getDouble("pvp.strafe.speeds.high", 0.24);
   }

   public long aw() {
      return this.f.getLong("pvp.strafe.switch-intervals.low", 800L);
   }

   public long ax() {
      return this.f.getLong("pvp.strafe.switch-intervals.medium", 550L);
   }

   public long ay() {
      return this.f.getLong("pvp.strafe.switch-intervals.high", 350L);
   }

   public double az() {
      return this.f.getDouble("pvp.approach-strafe.speeds.low", 0.1);
   }

   public double ba() {
      return this.f.getDouble("pvp.approach-strafe.speeds.medium", 0.13);
   }

   public double bb() {
      return this.f.getDouble("pvp.approach-strafe.speeds.high", 0.16);
   }

   public long bc() {
      return this.f.getLong("pvp.approach-strafe.switch-intervals.low", 700L);
   }

   public long bd() {
      return this.f.getLong("pvp.approach-strafe.switch-intervals.medium", 500L);
   }

   public long be() {
      return this.f.getLong("pvp.approach-strafe.switch-intervals.high", 350L);
   }

   public double bf() {
      return this.f.getDouble("pvp.approach-strafe.chase-speeds.low", 0.16);
   }

   public double bg() {
      return this.f.getDouble("pvp.approach-strafe.chase-speeds.medium", 0.2);
   }

   public double bh() {
      return this.f.getDouble("pvp.approach-strafe.chase-speeds.high", 0.24);
   }

   public long bi() {
      return Math.max(0L, this.f.getLong("shield.stun-duration-ms", 5000L));
   }

   public double bj() {
      return Math.min(360.0, Math.max(0.0, this.f.getDouble("shield.block-angle", 180.0)));
   }

   public long bk() {
      return Math.max(0L, this.f.getLong("shield.block-delay-ms", 100L));
   }

   public String bl() {
      return this.f.getString("commands.admin-permission", "practicebot.admin");
   }

   public String bm() {
      return this.f.getString("commands.spawnbot-permission", "practicebot.spawn");
   }

   public String bn() {
      return this.f.getString("gui.settings-title", "Practice Bot Settings");
   }

   public String bo() {
      return this.f.getString("gui.pvp-settings-title", "Bot PvP Settings");
   }

   public String bp() {
      return this.f.getString("gui.bot-inventory-title", "Bot Inventory");
   }

   public String bq() {
      return this.f.getString("gui.default-inventory-title", "Default Bot Inventory");
   }

   public String br() {
      return this.f.getString("gui.sounds.open", "BLOCK_CHEST_OPEN");
   }

   public String bs() {
      return this.f.getString("gui.sounds.close", "BLOCK_CHEST_CLOSE");
   }

   public String bt() {
      return this.f.getString("gui.sounds.click", "UI_BUTTON_CLICK");
   }

   public double bu() {
      return this.f.getDouble("pvp.aggression-damage-modifiers.low", 1.0);
   }

   public double bv() {
      return this.f.getDouble("pvp.aggression-damage-modifiers.medium", 1.05);
   }

   public double bw() {
      return this.f.getDouble("pvp.aggression-damage-modifiers.high", 1.1);
   }

   public double a(int var1) {
      return switch (var1) {
         case 0 -> this.bu();
         case 1 -> this.bv();
         case 2 -> this.bw();
         default -> 1.0;
      };
   }

   public boolean bx() {
      return this.f.getBoolean("cpvp.performance.adaptive-enabled", true);
   }

   public int by() {
      return Math.max(1, this.f.getInt("cpvp.performance.low-bot-threshold", 10));
   }

   public int bz() {
      return Math.max(this.by(), this.f.getInt("cpvp.performance.medium-bot-threshold", 20));
   }

   public int c0() {
      return Math.max(this.bz(), this.f.getInt("cpvp.performance.high-bot-threshold", 35));
   }

   public int c1() {
      return Math.max(1, this.f.getInt("cpvp.performance.low-scan-period-ticks", 3));
   }

   public int d0() {
      return Math.max(1, this.f.getInt("cpvp.performance.medium-scan-period-ticks", 4));
   }

   public int d1() {
      return Math.max(1, this.f.getInt("cpvp.performance.high-scan-period-ticks", 6));
   }

   public int e0() {
      return Math.max(1, Math.min(4, this.f.getInt("cpvp.performance.anchor-count-cache-ticks", 3)));
   }

   public int e1() {
      return Math.max(1, Math.min(4, this.f.getInt("cpvp.performance.crystal-search-cache-ticks", 2)));
   }

   public int f0() {
      return Math.max(50, Math.min(1000, this.f.getInt("cpvp.performance.navigation-retarget-ms", 250)));
   }

   public int f1() {
      return Math.max(this.f0(), Math.min(3500, this.f.getInt("cpvp.performance.high-load-navigation-retarget-ms", 1200)));
   }

   public boolean g0() {
      return this.f.getBoolean("debug.verbose-ai", false);
   }

   public boolean g1() {
      return this.f.getBoolean("debug.verbose-pvp", false);
   }

   public String h0() {
      return this.f.getString("license.code", "");
   }

   public void k(String var1) {
      String var2 = var1 == null ? "" : var1.trim();
      this.f.set("license.code", var2);
      this.e.getConfig().set("license.code", var2);
      this.e.saveConfig();
   }

   public String h1() {
      return "https://practicebot-license.sheeshgd.workers.dev/api/validate";
   }

   public List<String> i0() {
      return List.of();
   }

   public boolean i1() {
      return false;
   }

   public int j0() {
      return 10000;
   }

   public long j1() {
      return 86400000L;
   }

   public long k0() {
      return 600000L;
   }
}
