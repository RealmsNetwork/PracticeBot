package com.sheldera.practicebot;

import com.sheldera.practicebot.x.A;
import com.sheldera.practicebot.x.N;
import com.sheldera.practicebot.x.O;
import com.sheldera.practicebot.x.Z;
import com.sheldera.practicebot.x.ab;
import com.sheldera.practicebot.x.ad;
import com.sheldera.practicebot.x.ai;
import com.sheldera.practicebot.x.ak;
import com.sheldera.practicebot.x.b;
import com.sheldera.practicebot.x.c;
import com.sheldera.practicebot.x.h;
import com.sheldera.practicebot.x.i;
import com.sheldera.practicebot.x.k;
import com.sheldera.practicebot.x.n;
import com.sheldera.practicebot.x.n1;
import com.sheldera.practicebot.x.o1;
import com.sheldera.practicebot.x.p0;
import com.sheldera.practicebot.x.s1;
import com.sheldera.practicebot.x.u;
import com.sheldera.practicebot.x.x1;
import com.sheldera.practicebot.x.y1;
import com.sheldera.practicebot.x.z;
import com.sheldera.practicebot.x.z1;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PracticeBotPlugin extends JavaPlugin {
   private static PracticeBotPlugin instance;
   private int tickTaskId = -1;
   private boolean citizensReady = false;
   private int citizensRetryCount = 0;
   private static final int MAX_RETRIES = 15;
   private b config;
   private o1 guiConfigManager;
   private p0 guiEditorManager;
   private ak crystalPvpModule;
   private ai crystalPvpGui;
   private k botManager;
   private u botTickHandler;
   private s1 guiManager;
   private n1 guiClickHandler;
   private N botTemplateManager;
   private O templateEditorManager;
   private x1 apiService;
   private c licenseManager;
   private Object placeholderExpansion;
   private volatile boolean licenseActive = false;
   private File defaultInvFile;
   private FileConfiguration defaultInvConfig;
   private final Map<UUID, Long> spawnCooldown = new HashMap<>();
   private final Map<UUID, Long> combatTagged = new HashMap<>();
   private final Map<UUID, Long> inventoryClickCooldown = new HashMap<>();
   private Method startUsingItemMethod = null;
   private boolean shieldVisualSupported = true;

   public static PracticeBotPlugin getInstance() {
      return instance;
   }

   public void onEnable() {
      instance = this;

      try {
         this.initializeConfiguration();
         this.initializeGuiConfigSystem();
         this.initializeManagers();
         this.initializeCrystalPvp();
         this.licenseManager = new c(this, this.config);
         this.loadDefaultInventoryConfig();
         this.initializeBotTemplates();
         this.initializeTemplateEditor();
         this.initializeApiService();
         this.checkShieldVisualSupport();
         this.registerCommands();
         this.registerListeners();
         this.registerPaperKnockbackHandler();
         this.registerPlaceholderApiExpansion();
         this.licenseManager.k1();
         this.initializeCitizensProvider();
         this.debugLog("PracticeBot enabled successfully.");
      } catch (Exception var2) {
         this.getLogger().severe("Failed to enable PracticeBot: " + var2.getMessage());
         var2.printStackTrace();
         Bukkit.getPluginManager().disablePlugin(this);
      }
   }

   public void onDisable() {
      this.cancelTasks();
      this.unregisterPlaceholderApiExpansion();
      if (this.licenseManager != null) {
         this.licenseManager.m1();
         this.licenseManager = null;
      }

      this.shutdownTemplateEditor();
      this.cleanupBotsOnShutdown();
      if (this.apiService != null) {
         this.apiService.fP();
         this.apiService = null;
      }

      Bukkit.getScheduler().cancelTasks(this);
      this.citizensReady = false;
      instance = null;
      this.debugLog("PracticeBot disabled.");
   }

   private void initializeConfiguration() {
      this.config = new b(this);
      this.config.a();
      h.a(this);
      if (this.config.f() != null) {
         this.debugLog("Loaded " + this.config.f().size() + " CPVP difficulty levels");
      }

      this.debugLog("Configuration loaded.");
   }

   private void initializeGuiConfigSystem() {
      this.guiConfigManager = new o1(this);
      this.guiConfigManager.eG();
      this.guiEditorManager = new p0(this, this.guiConfigManager);
      Bukkit.getPluginManager().registerEvents(this.guiEditorManager, this);
      this.debugLog("GUI configuration system loaded.");
   }

   private void initializeManagers() {
      this.botManager = new k(this);
      this.botTickHandler = new u(this);
      this.guiManager = new s1(this);
      this.guiClickHandler = new n1(this);
      this.debugLog("Bot managers initialized.");
   }

   private void initializeCrystalPvp() {
      this.crystalPvpModule = new ak(this);
      this.crystalPvpGui = new ai(this);
      Bukkit.getPluginManager().registerEvents(this.crystalPvpGui, this);
      this.debugLog("Crystal PvP module loaded.");
   }

   private void registerPlaceholderApiExpansion() {
      if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
         try {
            ClassLoader var1 = this.getClass().getClassLoader();
            Class.forName("me.clip.placeholderapi.expansion.PlaceholderExpansion", false, var1);
            Class var8 = Class.forName("com.sheldera.practicebot.placeholder.PracticeBotPlaceholderExpansion", true, var1);
            Object var3 = var8.getConstructor(PracticeBotPlugin.class).newInstance(this);
            Method var4 = var8.getMethod("register");
            Object var5 = var4.invoke(var3);
            if (Boolean.TRUE.equals(var5)) {
               this.placeholderExpansion = var3;
               this.debugLog("PlaceholderAPI expansion registered as %practicebot_...%.");
            }
         } catch (ClassNotFoundException var6) {
            this.placeholderExpansion = null;
         } catch (Throwable var7) {
            this.placeholderExpansion = null;
            Throwable var2 = var7.getCause() != null ? var7.getCause() : var7;
            this.getLogger().warning("Could not register PlaceholderAPI expansion: " + var2.getMessage());
         }
      }
   }

   private void unregisterPlaceholderApiExpansion() {
      Object var1 = this.placeholderExpansion;
      this.placeholderExpansion = null;
      if (var1 != null) {
         try {
            Method var2 = var1.getClass().getMethod("unregister");
            var2.invoke(var1);
         } catch (Throwable var3) {
         }
      }
   }

   private void registerCommands() {
      PluginCommand var1 = this.getCommand("spawnbot");
      if (var1 != null) {
         ad var2 = new ad(this);
         var1.setExecutor(var2);
         var1.setTabCompleter(var2);
      } else {
         this.getLogger().warning("Command 'spawnbot' not found in plugin.yml!");
      }

      PluginCommand var5 = this.getCommand("practicebot");
      if (var5 != null) {
         ab var3 = new ab(this);
         var5.setExecutor(var3);
         var5.setTabCompleter(var3);
      } else {
         this.getLogger().warning("Command 'practicebot' not found in plugin.yml!");
      }

      PluginCommand var6 = this.getCommand("practicebot-admin");
      if (var6 != null) {
         z var4 = new z(this);
         var6.setExecutor(var4);
         var6.setTabCompleter(var4);
      } else {
         this.getLogger().warning("Command 'practicebot-admin' not found in plugin.yml!");
      }

      this.debugLog("Commands registered.");
   }

   private void registerListeners() {
      Bukkit.getPluginManager().registerEvents(new A(this), this);
      Bukkit.getPluginManager().registerEvents(new y1(this), this);
      Bukkit.getPluginManager().registerEvents(new z1(this), this);
      Bukkit.getPluginManager().registerEvents(this.guiClickHandler, this);
      Bukkit.getPluginManager().registerEvents(this.templateEditorManager, this);
      this.debugLog("Event listeners registered.");
   }

   private void checkShieldVisualSupport() {
      try {
         this.startUsingItemMethod = LivingEntity.class.getMethod("startUsingItem", EquipmentSlot.class);
         this.debugLog("Shield visual support enabled (Paper API detected).");
      } catch (NoSuchMethodException var2) {
         this.shieldVisualSupported = false;
         this.debugLog("Shield visual not supported (requires Paper 1.19+).");
      }
   }

   private void initializeBotTemplates() {
      this.botTemplateManager = new N(this);
      this.botTemplateManager.a();
      this.debugLog("Bot template system loaded.");
   }

   private void initializeTemplateEditor() {
      this.templateEditorManager = new O(this);
      this.templateEditorManager.gL();
      this.debugLog("Template editor system loaded.");
   }

   private void initializeCitizensProvider() {
      Plugin var1 = Bukkit.getPluginManager().getPlugin("Citizens");
      if (var1 == null) {
         this.getLogger().severe("Citizens plugin not found! PracticeBot requires Citizens.");
      } else {
         this.debugLog("Citizens detected. Waiting for initialization...");
         Bukkit.getScheduler().runTaskLater(this, this::initializeCitizens, 100L);
      }
   }

   private void initializeApiService() {
      this.apiService = new x1(this);
      this.debugLog("Public PracticeBot API prepared.");
   }

   private void initializeCitizens() {
      if (this.isEnabled()) {
         Plugin var1 = Bukkit.getPluginManager().getPlugin("Citizens");
         if (var1 != null && var1.isEnabled()) {
            try {
               ClassLoader var2 = var1.getClass().getClassLoader();
               Class var3 = Class.forName("net.citizensnpcs.api.CitizensAPI", true, var2);
               Method var4 = var3.getMethod("hasImplementation");
               if (!(Boolean)var4.invoke(null)) {
                  this.scheduleRetry("Citizens implementation not ready");
                  return;
               }

               Method var5 = var3.getMethod("getTraitFactory");
               Object var6 = var5.invoke(null);
               Class var7 = Class.forName("net.citizensnpcs.api.trait.TraitInfo", true, var2);
               Method var8 = var7.getMethod("create", Class.class);
               Object var9 = var8.invoke(null, BotTrait.class);
               Method var10 = var9.getClass().getMethod("withName", String.class);
               var9 = var10.invoke(var9, "practicebot");
               Method var11 = var6.getClass().getMethod("registerTrait", var7);

               try {
                  var11.invoke(var6, var9);
               } catch (Exception var13) {
                  if (!this.isDuplicateTraitRegistration(var13)) {
                     throw var13;
                  }

                  this.debugLog("Citizens PracticeBot trait was already registered; continuing startup.");
               }

               this.citizensReady = true;
               this.debugLog("Citizens integration initialized!");
               this.cleanupLeftoverNPCs();
               this.startBotTickHandler();
            } catch (Exception var14) {
               this.getLogger().warning("Citizens init error: " + var14.getMessage());
               this.scheduleRetry("Exception");
            }
         } else {
            this.scheduleRetry("Citizens not ready");
         }
      }
   }

   private void scheduleRetry(String var1) {
      if (this.isEnabled()) {
         this.citizensRetryCount++;
         if (this.citizensRetryCount <= 15) {
            this.getLogger().warning(var1 + " - retrying... (" + this.citizensRetryCount + "/15)");
            Bukkit.getScheduler().runTaskLater(this, this::initializeCitizens, 40L);
         } else {
            this.getLogger().severe("Failed to initialize Citizens after 15 attempts!");
         }
      }
   }

   private boolean isDuplicateTraitRegistration(Throwable var1) {
      for (Throwable var2 = var1; var2 != null; var2 = var2.getCause()) {
         String var3 = var2.getMessage();
         if (var3 != null) {
            String var4 = var3.toLowerCase(Locale.ROOT);
            if (var4.contains("already") && var4.contains("trait")) {
               return true;
            }
         }
      }

      return false;
   }

   private void startBotTickHandler() {
      if (!this.isLicenseActive()) {
         this.getLogger().warning("PracticeBot combat/spawn features are locked until the license is valid.");
      } else if (this.tickTaskId == -1) {
         int var1 = Math.max(1, this.config.v());
         this.tickTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> this.botTickHandler.aC(), 1L, var1);
      }
   }

   private void cancelTasks() {
      if (this.tickTaskId != -1) {
         Bukkit.getScheduler().cancelTask(this.tickTaskId);
         this.tickTaskId = -1;
      }
   }

   private void cleanupBotsOnShutdown() {
      if (this.citizensReady) {
         try {
            int var1 = 0;

            for (NPC var3 : CitizensAPI.getNPCRegistry()) {
               if (this.botManager.b(var3)) {
                  var1++;
               }
            }

            this.botManager.a(n.PLUGIN_DISABLE);
            if (var1 > 0) {
               this.debugLog("Removed " + var1 + " PracticeBot NPC(s) on shutdown.");
            }
         } catch (Exception var4) {
            this.getLogger().warning("Error removing NPCs on disable: " + var4.getMessage());
         }
      }
   }

   private void shutdownTemplateEditor() {
      if (this.templateEditorManager != null) {
         this.templateEditorManager.m1();
      }
   }

   private void cleanupLeftoverNPCs() {
      if (this.citizensReady) {
         try {
            int var1 = this.botManager.I();
            if (var1 > 0) {
               this.debugLog("Cleaned up " + var1 + " leftover PracticeBot NPC(s).");
            }
         } catch (Exception var2) {
            this.getLogger().warning("Error cleaning up leftover NPCs: " + var2.getMessage());
         }
      }
   }

   private void loadDefaultInventoryConfig() {
      this.defaultInvFile = new File(this.getDataFolder(), "default_inv.yml");
      this.defaultInvConfig = Z.a(this, this.defaultInvFile, "default_inv.yml");
      boolean var1 = false;
      if (!this.defaultInvConfig.contains("helmet-trim-pattern")) {
         this.defaultInvConfig.set("helmet-trim-pattern", "");
         this.defaultInvConfig.set("helmet-trim-material", "");
         var1 = true;
      }

      if (!this.defaultInvConfig.contains("chest-trim-pattern")) {
         this.defaultInvConfig.set("chest-trim-pattern", "");
         this.defaultInvConfig.set("chest-trim-material", "");
         var1 = true;
      }

      if (!this.defaultInvConfig.contains("legs-trim-pattern")) {
         this.defaultInvConfig.set("legs-trim-pattern", "");
         this.defaultInvConfig.set("legs-trim-material", "");
         var1 = true;
      }

      if (!this.defaultInvConfig.contains("boots-trim-pattern")) {
         this.defaultInvConfig.set("boots-trim-pattern", "");
         this.defaultInvConfig.set("boots-trim-material", "");
         var1 = true;
      }

      if (this.defaultInvConfig.contains("chestplate-trim-pattern")) {
         String var2 = this.defaultInvConfig.getString("chestplate-trim-pattern", "");
         String var3 = this.defaultInvConfig.getString("chestplate-trim-material", "");
         if (!var2.isEmpty() && this.defaultInvConfig.getString("chest-trim-pattern", "").isEmpty()) {
            this.defaultInvConfig.set("chest-trim-pattern", var2);
            this.defaultInvConfig.set("chest-trim-material", var3);
         }

         this.defaultInvConfig.set("chestplate-trim-pattern", null);
         this.defaultInvConfig.set("chestplate-trim-material", null);
         var1 = true;
      }

      if (this.defaultInvConfig.contains("leggings-trim-pattern")) {
         String var4 = this.defaultInvConfig.getString("leggings-trim-pattern", "");
         String var5 = this.defaultInvConfig.getString("leggings-trim-material", "");
         if (!var4.isEmpty() && this.defaultInvConfig.getString("legs-trim-pattern", "").isEmpty()) {
            this.defaultInvConfig.set("legs-trim-pattern", var4);
            this.defaultInvConfig.set("legs-trim-material", var5);
         }

         this.defaultInvConfig.set("leggings-trim-pattern", null);
         this.defaultInvConfig.set("leggings-trim-material", null);
         var1 = true;
      }

      if (var1) {
         this.saveDefaultInventoryConfig();
      }
   }

   public void saveDefaultInventoryConfig() {
      try {
         this.defaultInvConfig.save(this.defaultInvFile);
      } catch (IOException var2) {
         this.getLogger().severe("Error saving default_inv.yml: " + var2.getMessage());
      }
   }

   private void registerPaperKnockbackHandler() {
      try {
         Class.forName("com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent");
         Bukkit.getPluginManager().registerEvents(new i(this), this);
         this.debugLog("Paper knockback event handler registered.");
      } catch (ClassNotFoundException var2) {
         this.debugLog("Paper knockback event not available.");
      }
   }

   public boolean isLicenseActive() {
      return this.licenseActive && this.licenseManager != null && this.licenseManager.l0();
   }

   public c getLicenseManager() {
      return this.licenseManager;
   }

   public void setLicenseActive(boolean var1, String var2) {
      if (var1) {
         boolean var3 = !this.licenseActive;
         this.licenseActive = true;
         if (this.apiService != null) {
            this.apiService.fO();
         }

         if (this.templateEditorManager != null) {
            this.templateEditorManager.gL();
         }

         if (var3) {
            this.debugLog("PracticeBot licensed features unlocked.");
         }

         if (this.citizensReady && this.tickTaskId == -1 && this.botTickHandler != null) {
            this.startBotTickHandler();
         }
      } else {
         this.licenseActive = false;
         this.cancelTasks();
         if (this.apiService != null) {
            this.apiService.fP();
         }

         if (this.templateEditorManager != null) {
            this.templateEditorManager.m1();
         }

         if (this.citizensReady && this.botManager != null) {
            this.botManager.a(n.PLUGIN_DISABLE);
         }
      }
   }

   public void requireLicenseActive() {
      if (!this.isLicenseActive()) {
         throw new IllegalStateException("PracticeBot license is not valid.");
      }
   }

   public String licenseLockMessage() {
      return h.ac("license-required");
   }

   public boolean isDebugLoggingEnabled() {
      b var1 = this.config;
      return var1 != null && (var1.g0() || var1.g1());
   }

   public void debugLog(String var1) {
      if (this.isDebugLoggingEnabled()) {
         this.getLogger().info(var1);
      }
   }

   public void debugLog(Supplier<String> var1) {
      if (this.isDebugLoggingEnabled()) {
         this.getLogger().info((String)var1.get());
      }
   }

   public boolean isCitizensReady() {
      return this.citizensReady;
   }

   public b getConfigManager() {
      return this.config;
   }

   public o1 getGuiConfigManager() {
      return this.guiConfigManager;
   }

   public p0 getGuiEditorManager() {
      return this.guiEditorManager;
   }

   public ak getCrystalPvpModule() {
      return this.crystalPvpModule;
   }

   public ai getCrystalPvpGui() {
      return this.crystalPvpGui;
   }

   public k getBotManager() {
      return this.botManager;
   }

   public u getBotTickHandler() {
      return this.botTickHandler;
   }

   public s1 getGuiManager() {
      return this.guiManager;
   }

   public n1 getGuiClickHandler() {
      return this.guiClickHandler;
   }

   public N getBotTemplateManager() {
      return this.botTemplateManager;
   }

   public O getTemplateEditorManager() {
      return this.templateEditorManager;
   }

   public x1 getApiService() {
      return this.apiService;
   }

   public FileConfiguration getDefaultInvConfig() {
      return this.defaultInvConfig;
   }

   public Map<UUID, Long> getSpawnCooldown() {
      return this.spawnCooldown;
   }

   public Map<UUID, Long> getCombatTagged() {
      return this.combatTagged;
   }

   public Map<UUID, Long> getInventoryClickCooldown() {
      return this.inventoryClickCooldown;
   }

   public Method getStartUsingItemMethod() {
      return this.startUsingItemMethod;
   }

   public boolean isShieldVisualSupported() {
      return this.shieldVisualSupported;
   }

   public void addCombatTag(Player var1) {
      this.combatTagged.put(var1.getUniqueId(), System.currentTimeMillis());
   }

   public boolean isCombatTagged(Player var1) {
      Long var2 = this.combatTagged.get(var1.getUniqueId());
      if (var2 == null) {
         return false;
      } else if (System.currentTimeMillis() - var2 > 10000L) {
         this.combatTagged.remove(var1.getUniqueId());
         return false;
      } else {
         return true;
      }
   }
}
