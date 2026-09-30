package com.sheldera.practicebot;

import com.sheldera.practicebot.x.a;
import com.sheldera.practicebot.x.ae;
import com.sheldera.practicebot.x.af;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@TraitName("practicebot")
public class BotTrait extends Trait {
   public long canBlockAfterAttackTime = 0L;
   private UUID owner;
   private UUID boundTarget;
   private transient int cachedBoundTargetTick = Integer.MIN_VALUE;
   private transient UUID cachedBoundTargetUuid = null;
   private transient Player cachedBoundTargetPlayer = null;
   private boolean autoTargetingEnabled = false;
   private boolean autoTargetBotsOnly = false;
   private boolean editorPreview = false;
   private UUID editorSessionOwner;
   private boolean lookAtOwner = true;
   private boolean pvpEnabled = false;
   private boolean followOwner = false;
   private boolean randomWalk = false;
   private boolean holdShield = false;
   private boolean useShield = false;
   private boolean resistance = false;
   private boolean frozen = false;
   private boolean shieldInMainHand = false;
   private boolean guiEnabled = true;
   private a botType = a.NORMAL;
   private String frozenAnchorWorld = null;
   private double frozenAnchorX = 0.0;
   private double frozenAnchorY = 0.0;
   private double frozenAnchorZ = 0.0;
   private float frozenAnchorYaw = 0.0F;
   private float frozenAnchorPitch = 0.0F;
   private ItemStack[] storedInventory = null;
   private ItemStack[] storedArmor = null;
   private ItemStack storedOffhand = null;
   private String armorType = "netherite";
   private String helmetMaterial = "netherite";
   private String chestplateMaterial = "netherite";
   private String bootsMaterial = "netherite";
   private String helmetEnchant = "protection";
   private String chestplateEnchant = "protection";
   private String leggingsEnchant = "protection";
   private String bootsEnchant = "protection";
   private String leggingsMaterial = "netherite";
   private String swordType = "sharp_kb";
   private String weaponProfile = "sword";
   private String offhandType = "totem";
   private int totemCount = 64;
   private ItemStack customMainHand = null;
   private long attackWarmupTicks = 0L;
   private long attackWarmupEndMillis = 0L;
   private String helmetTrimPattern = "";
   private String helmetTrimMaterial = "";
   private String chestTrimPattern = "";
   private String chestTrimMaterial = "";
   private String legsTrimPattern = "";
   private String legsTrimMaterial = "";
   private String bootsTrimPattern = "";
   private String bootsTrimMaterial = "";
   private boolean pvpStrafe = false;
   private boolean pvpWTap = false;
   private boolean pvpSTap = false;
   private boolean pvpCrits = false;
   private boolean pvpShieldBreaker = false;
   private boolean pvpRetreat = false;
   private int pvpReachMode = 1;
   private int pvpAggression = 1;
   private int pvpCritChance = 2;
   private int pvpCritSpeed = 0;
   private boolean cpvpEnabled = false;
   private ae cpvpSettings = null;
   public long shieldDisabledUntil = 0L;
   public long lastAttackTime = 0L;
   public boolean pendingDelayedAttack = false;
   public long pendingDelayedAttackId = 0L;
   public long lastHitTime = 0L;
   public long lastHitByPlayerTime = 0L;
   public long lastBlockDecision = 0L;
   public boolean isBlocking = false;
   public boolean wasBlocking = false;
   public long blockStartTime = 0L;
   public long reactiveBlockUntil = 0L;
   public boolean reactiveBlockTriggered = false;
   public boolean strafeRight = true;
   public long lastStrafeSwitch = 0L;
   public double currentStrafeSpeed = 0.0;
   public double targetStrafeSpeed = 0.0;
   public boolean strafeMovingForward = false;
   public long lastDirectionChange = 0L;
   public boolean sTapActive = false;
   public long sTapEndTime = 0L;
   public long sTapCooldown = 0L;
   public Vector sTapDirection = null;
   public boolean isLaunched = false;
   public long launchTime = 0L;
   public int airTicks = 0;
   public double lastY = 0.0;
   public long lastGroundTime = 0L;
   public boolean wasKnockedBack = false;
   public long knockbackTime = 0L;
   public boolean attemptingCrit = false;
   public long critJumpTime = 0L;
   public long lastLandingTime = 0L;
   public boolean wasInAir = false;
   public int critPhase = 0;
   public double critStartY = 0.0;
   public double peakY = 0.0;
   public int critAirborneTicks = 0;
   public int critDescendingTicks = 0;
   public boolean critSawAscent = false;
   public boolean critSawDescent = false;
   public boolean forceNormalAttackAfterCritFail = false;
   public long knockbackUntil = 0L;
   public boolean inKnockback = false;
   public long lastSprintJumpTime = 0L;
   public long modernWeaponCooldownUntil = 0L;
   public long maceLaunchStartedAt = 0L;
   public boolean maceLaunchActive = false;
   public long spearChargeStartedAt = 0L;
   public long spearLastHitAt = 0L;
   public UUID modernWeaponTarget = null;
   public boolean shouldRetreat = false;
   public long retreatUntil = 0L;
   public long lastRetreatTime = 0L;
   public int consecutiveHitsTaken = 0;
   public long lastHitTakenTime = 0L;
   public long lastShieldPressureTime = 0L;
   private final long[] recentHitTimes = new long[6];
   private final double[] recentHitDamages = new double[6];
   private int recentHitCursor = 0;
   public boolean isInWater = false;
   public boolean isSwitchingToAxe = false;
   public ItemStack previousMainHand = null;
   public long switchBackTime = 0L;
   public boolean justBrokeShield = false;
   public long shieldBreakTime = 0L;
   public double lastMoveX = 0.0;
   public double lastMoveZ = 0.0;
   public double lastPosX = 0.0;
   public double lastPosZ = 0.0;
   public int stuckTicks = 0;
   public long lastJumpTime = 0L;
   public long lastStuckCheck = 0L;
   public long lastPathUpdate = 0L;
   public boolean overheadOrbitClockwise = true;
   public long overheadAvoidLockUntil = 0L;
   public boolean followDisabledByKnockback = false;
   public long lastRandomWalkTime = 0L;

   public a getBotType() {
      return this.botType;
   }

   public void setBotType(a var1) {
      this.botType = var1 == null ? a.NORMAL : var1;
      this.enforceCpvpLookLock();
   }

   private boolean isCpvpBotMode() {
      return this.botType == a.CPVP || this.cpvpEnabled;
   }

   private void enforceCpvpLookLock() {
      if (this.isCpvpBotMode()) {
         this.lookAtOwner = false;
      }
   }

   public BotTrait() {
      super("practicebot");
   }

   public void onAttach() {
   }

   public void onSpawn() {
      this.resetAllStates();
      this.armAttackWarmup();
      if (this.frozen && this.npc != null && this.npc.getEntity() != null) {
         this.captureFrozenAnchor(this.npc.getEntity().getLocation());
      }
   }

   public void resetAllStates() {
      long var1 = System.currentTimeMillis();
      this.lastAttackTime = 0L;
      this.pendingDelayedAttack = false;
      this.pendingDelayedAttackId = 0L;
      this.canBlockAfterAttackTime = 0L;
      this.lastHitTime = 0L;
      this.lastHitByPlayerTime = 0L;
      this.shieldDisabledUntil = 0L;
      this.followDisabledByKnockback = false;
      this.knockbackUntil = 0L;
      this.inKnockback = false;
      this.lastPathUpdate = 0L;
      this.overheadOrbitClockwise = true;
      this.overheadAvoidLockUntil = 0L;
      this.sTapActive = false;
      this.sTapEndTime = 0L;
      this.sTapCooldown = 0L;
      this.sTapDirection = null;
      this.attemptingCrit = false;
      this.critJumpTime = 0L;
      this.critPhase = 0;
      this.critStartY = 0.0;
      this.peakY = 0.0;
      this.critAirborneTicks = 0;
      this.critDescendingTicks = 0;
      this.critSawAscent = false;
      this.critSawDescent = false;
      this.forceNormalAttackAfterCritFail = false;
      this.lastLandingTime = var1;
      this.wasInAir = false;
      this.strafeRight = true;
      this.lastStrafeSwitch = 0L;
      this.currentStrafeSpeed = 0.0;
      this.targetStrafeSpeed = 0.0;
      this.strafeMovingForward = false;
      this.lastDirectionChange = 0L;
      this.lastBlockDecision = 0L;
      this.isBlocking = false;
      this.wasBlocking = false;
      this.blockStartTime = 0L;
      this.reactiveBlockUntil = 0L;
      this.reactiveBlockTriggered = false;
      this.isSwitchingToAxe = false;
      this.previousMainHand = null;
      this.switchBackTime = 0L;
      this.justBrokeShield = false;
      this.shieldBreakTime = 0L;
      this.lastSprintJumpTime = 0L;
      this.shouldRetreat = false;
      this.retreatUntil = 0L;
      this.lastRetreatTime = 0L;
      this.consecutiveHitsTaken = 0;
      this.lastHitTakenTime = 0L;
      this.lastShieldPressureTime = 0L;
      Arrays.fill(this.recentHitTimes, 0L);
      Arrays.fill(this.recentHitDamages, 0.0);
      this.recentHitCursor = 0;
      this.lastMoveX = 0.0;
      this.lastMoveZ = 0.0;
      this.isLaunched = false;
      this.launchTime = 0L;
      this.airTicks = 0;
      this.lastY = 0.0;
      this.lastGroundTime = var1;
      this.wasKnockedBack = false;
      this.knockbackTime = 0L;
      this.lastPosX = 0.0;
      this.lastPosZ = 0.0;
      this.stuckTicks = 0;
      this.lastJumpTime = 0L;
      this.lastStuckCheck = 0L;
      this.isInWater = false;
      this.lastRandomWalkTime = 0L;
      this.attackWarmupEndMillis = 0L;
   }

   public long getAttackWarmupTicks() {
      return this.attackWarmupTicks;
   }

   public void setAttackWarmupTicks(long var1) {
      this.attackWarmupTicks = Math.max(0L, var1);
   }

   public void armAttackWarmup() {
      this.attackWarmupEndMillis = this.attackWarmupTicks <= 0L ? 0L : System.currentTimeMillis() + this.attackWarmupTicks * 50L;
   }

   public boolean isAttackWarmupActive(long var1) {
      return this.attackWarmupEndMillis > var1;
   }

   public long getAttackWarmupRemainingTicks(long var1) {
      return !this.isAttackWarmupActive(var1) ? 0L : Math.max(0L, (this.attackWarmupEndMillis - var1 + 49L) / 50L);
   }

   public void clearAttackWarmup() {
      this.attackWarmupEndMillis = 0L;
   }

   public void setOwner(UUID var1) {
      this.owner = var1;
   }

   public UUID getOwnerUUID() {
      return this.owner;
   }

   public Player getOwnerPlayer() {
      if (this.owner == null) {
         return null;
      } else {
         Player var1 = Bukkit.getPlayer(this.owner);
         return var1 != null && var1.isOnline() ? var1 : null;
      }
   }

   public boolean isOwner(Player var1) {
      return var1 != null && this.owner != null && var1.getUniqueId().equals(this.owner);
   }

   public void setBoundTarget(UUID var1) {
      this.boundTarget = var1;
      this.cachedBoundTargetTick = Integer.MIN_VALUE;
      this.cachedBoundTargetUuid = null;
      this.cachedBoundTargetPlayer = null;
   }

   public UUID getBoundTargetUUID() {
      return this.boundTarget;
   }

   public boolean isAutoTargetingEnabled() {
      return this.autoTargetingEnabled;
   }

   public void setAutoTargetingEnabled(boolean var1) {
      this.autoTargetingEnabled = var1;
   }

   public boolean isAutoTargetBotsOnly() {
      return this.autoTargetBotsOnly;
   }

   public void setAutoTargetBotsOnly(boolean var1) {
      this.autoTargetBotsOnly = var1;
   }

   public Player getBoundTargetPlayer() {
      if (this.boundTarget == null) {
         return null;
      } else {
         int var1 = Bukkit.getCurrentTick();
         if (var1 != this.cachedBoundTargetTick || !this.boundTarget.equals(this.cachedBoundTargetUuid)) {
            Player var2 = Bukkit.getPlayer(this.boundTarget);
            if (var2 == null && Bukkit.getEntity(this.boundTarget) instanceof Player var4) {
               var2 = var4;
            }

            this.cachedBoundTargetTick = var1;
            this.cachedBoundTargetUuid = this.boundTarget;
            this.cachedBoundTargetPlayer = var2;
            return var2;
         } else {
            return this.cachedBoundTargetPlayer != null && this.cachedBoundTargetPlayer.isValid() && !this.cachedBoundTargetPlayer.isDead()
               ? this.cachedBoundTargetPlayer
               : null;
         }
      }
   }

   public boolean isBoundTarget(Player var1) {
      return var1 != null && this.boundTarget != null && var1.getUniqueId().equals(this.boundTarget);
   }

   public Player getBehaviorTargetPlayer() {
      Player var1 = this.getBoundTargetPlayer();
      return var1 != null ? var1 : this.getOwnerPlayer();
   }

   public static boolean isNpcPlayer(Player var0) {
      return var0 != null && CitizensAPI.getNPCRegistry().isNPC(var0);
   }

   public static boolean isLiveCombatTarget(Player var0) {
      return var0 != null && var0.isValid() && !var0.isDead() && var0.getGameMode() != GameMode.SPECTATOR;
   }

   public static boolean isLiveHumanTarget(Player var0) {
      return isLiveCombatTarget(var0) && var0.isOnline() && !isNpcPlayer(var0);
   }

   public static boolean isLiveBotTarget(Player var0) {
      return isLiveCombatTarget(var0) && isNpcPlayer(var0);
   }

   public boolean isEditorPreview() {
      return this.editorPreview;
   }

   public void setEditorPreview(boolean var1) {
      this.editorPreview = var1;
   }

   public UUID getEditorSessionOwnerUUID() {
      return this.editorSessionOwner;
   }

   public void setEditorSessionOwner(UUID var1) {
      this.editorSessionOwner = var1;
   }

   public boolean isEditorSessionOwner(Player var1) {
      return var1 != null && this.editorSessionOwner != null && this.editorSessionOwner.equals(var1.getUniqueId());
   }

   public boolean isLookAtOwner() {
      return this.isCpvpBotMode() ? false : this.lookAtOwner;
   }

   public void setLookAtOwner(boolean var1) {
      this.lookAtOwner = this.isCpvpBotMode() ? false : var1;
   }

   public boolean isPvpEnabled() {
      return this.pvpEnabled;
   }

   public void setPvpEnabled(boolean var1) {
      this.pvpEnabled = var1;
      if (var1) {
         this.cpvpEnabled = false;
      }
   }

   public ItemStack[] getStoredInventory() {
      return this.storedInventory;
   }

   public void setStoredInventory(ItemStack[] var1) {
      this.storedInventory = var1;
   }

   public ItemStack[] getStoredArmor() {
      return this.storedArmor;
   }

   public void setStoredArmor(ItemStack[] var1) {
      this.storedArmor = var1;
   }

   public ItemStack getStoredOffhand() {
      return this.storedOffhand;
   }

   public void setStoredOffhand(ItemStack var1) {
      this.storedOffhand = var1;
   }

   public boolean hasStoredInventory() {
      return this.storedInventory != null;
   }

   public void clearStoredInventory() {
      this.storedInventory = null;
      this.storedArmor = null;
      this.storedOffhand = null;
   }

   public boolean isFollowOwner() {
      return this.followOwner;
   }

   public void setFollowOwner(boolean var1) {
      this.followOwner = var1;
   }

   public boolean isRandomWalk() {
      return this.randomWalk;
   }

   public void setRandomWalk(boolean var1) {
      this.randomWalk = var1;
   }

   public boolean isHoldShield() {
      return this.holdShield;
   }

   public void setHoldShield(boolean var1) {
      this.holdShield = var1;
   }

   public boolean isUseShield() {
      return this.useShield;
   }

   public void setUseShield(boolean var1) {
      this.useShield = var1;
   }

   public boolean isResistance() {
      return this.resistance;
   }

   public void setResistance(boolean var1) {
      this.resistance = var1;
   }

   public boolean isFrozen() {
      return this.frozen;
   }

   public void setFrozen(boolean var1) {
      this.frozen = var1;
      if (!var1) {
         this.clearFrozenAnchor();
      }
   }

   public boolean isShieldInMainHand() {
      return this.shieldInMainHand;
   }

   public void setShieldInMainHand(boolean var1) {
      this.shieldInMainHand = var1;
   }

   public boolean isGuiEnabled() {
      return this.guiEnabled;
   }

   public void setGuiEnabled(boolean var1) {
      this.guiEnabled = var1;
   }

   public String getArmorType() {
      return this.armorType;
   }

   public void setArmorType(String var1) {
      String var2 = this.normalizeArmorMaterial(var1, "netherite");
      this.armorType = var2;
      this.helmetMaterial = var2;
      this.chestplateMaterial = var2;
      this.bootsMaterial = var2;
   }

   public String getHelmetMaterial() {
      return this.helmetMaterial;
   }

   public void setHelmetMaterial(String var1) {
      this.helmetMaterial = this.normalizeArmorMaterial(var1, this.helmetMaterial);
      this.refreshArmorTypeSnapshot();
   }

   public String getChestplateMaterial() {
      return this.chestplateMaterial;
   }

   public void setChestplateMaterial(String var1) {
      this.chestplateMaterial = this.normalizeArmorMaterial(var1, this.chestplateMaterial);
      this.refreshArmorTypeSnapshot();
   }

   public String getHelmetEnchant() {
      return this.helmetEnchant;
   }

   public void setHelmetEnchant(String var1) {
      this.helmetEnchant = var1;
   }

   public String getChestplateEnchant() {
      return this.chestplateEnchant;
   }

   public void setChestplateEnchant(String var1) {
      this.chestplateEnchant = var1;
   }

   public String getLeggingsEnchant() {
      return this.leggingsEnchant;
   }

   public void setLeggingsEnchant(String var1) {
      this.leggingsEnchant = var1;
   }

   public String getBootsEnchant() {
      return this.bootsEnchant;
   }

   public void setBootsEnchant(String var1) {
      this.bootsEnchant = var1;
   }

   public String getLeggingsMaterial() {
      return this.leggingsMaterial;
   }

   public void setLeggingsMaterial(String var1) {
      this.leggingsMaterial = this.normalizeArmorMaterial(var1, this.leggingsMaterial);
   }

   public String getBootsMaterial() {
      return this.bootsMaterial;
   }

   public void setBootsMaterial(String var1) {
      this.bootsMaterial = this.normalizeArmorMaterial(var1, this.bootsMaterial);
      this.refreshArmorTypeSnapshot();
   }

   public String getSwordType() {
      return this.swordType;
   }

   public void setSwordType(String var1) {
      this.swordType = var1;
   }

   public String getWeaponProfile() {
      return this.weaponProfile;
   }

   public void setWeaponProfile(String profile) {
      this.weaponProfile = this.normalizeWeaponProfile(profile);
      this.modernWeaponCooldownUntil = 0L;
      this.maceLaunchStartedAt = 0L;
      this.maceLaunchActive = false;
      this.spearChargeStartedAt = 0L;
      this.spearLastHitAt = 0L;
      this.modernWeaponTarget = null;
   }

   private String normalizeWeaponProfile(String profile) {
      if (profile == null) return "sword";
      String normalized = profile.trim().toLowerCase(Locale.ROOT);
      return switch (normalized) {
         case "mace", "spear", "sword" -> normalized;
         default -> "sword";
      };
   }

   public String getOffhandType() {
      return this.offhandType;
   }

   public void setOffhandType(String var1) {
      this.offhandType = var1;
   }

   public int getTotemCount() {
      return this.totemCount;
   }

   public void setTotemCount(int var1) {
      this.totemCount = Math.max(0, var1);
   }

   public void decrementTotemCount() {
      if (this.totemCount > 0) {
         this.totemCount--;
      }
   }

   public ItemStack getCustomMainHand() {
      return this.customMainHand;
   }

   public void setCustomMainHand(ItemStack var1) {
      this.customMainHand = var1 != null ? var1.clone() : null;
   }

   public String getHelmetTrimPattern() {
      return this.helmetTrimPattern;
   }

   public void setHelmetTrimPattern(String var1) {
      this.helmetTrimPattern = this.normalizeTrimValue(var1);
   }

   public String getHelmetTrimMaterial() {
      return this.helmetTrimMaterial;
   }

   public void setHelmetTrimMaterial(String var1) {
      this.helmetTrimMaterial = this.normalizeTrimValue(var1);
   }

   public String getChestTrimPattern() {
      return this.chestTrimPattern;
   }

   public void setChestTrimPattern(String var1) {
      this.chestTrimPattern = this.normalizeTrimValue(var1);
   }

   public String getChestTrimMaterial() {
      return this.chestTrimMaterial;
   }

   public void setChestTrimMaterial(String var1) {
      this.chestTrimMaterial = this.normalizeTrimValue(var1);
   }

   public String getLegsTrimPattern() {
      return this.legsTrimPattern;
   }

   public void setLegsTrimPattern(String var1) {
      this.legsTrimPattern = this.normalizeTrimValue(var1);
   }

   public String getLegsTrimMaterial() {
      return this.legsTrimMaterial;
   }

   public void setLegsTrimMaterial(String var1) {
      this.legsTrimMaterial = this.normalizeTrimValue(var1);
   }

   public String getBootsTrimPattern() {
      return this.bootsTrimPattern;
   }

   public void setBootsTrimPattern(String var1) {
      this.bootsTrimPattern = this.normalizeTrimValue(var1);
   }

   public String getBootsTrimMaterial() {
      return this.bootsTrimMaterial;
   }

   public void setBootsTrimMaterial(String var1) {
      this.bootsTrimMaterial = this.normalizeTrimValue(var1);
   }

   public boolean isPvpStrafe() {
      return this.pvpStrafe;
   }

   public void setPvpStrafe(boolean var1) {
      this.pvpStrafe = var1;
   }

   public boolean isPvpWTap() {
      return this.pvpWTap;
   }

   public void setPvpWTap(boolean var1) {
      this.pvpWTap = var1;
   }

   public boolean isPvpSTap() {
      return this.pvpSTap;
   }

   public void setPvpSTap(boolean var1) {
      this.pvpSTap = var1;
   }

   public boolean isPvpCrits() {
      return this.pvpCrits;
   }

   public void setPvpCrits(boolean var1) {
      this.pvpCrits = var1;
   }

   public boolean isPvpShieldBreaker() {
      return this.pvpShieldBreaker;
   }

   public void setPvpShieldBreaker(boolean var1) {
      this.pvpShieldBreaker = var1;
   }

   public boolean isPvpRetreat() {
      return this.pvpRetreat;
   }

   public void setPvpRetreat(boolean var1) {
      this.pvpRetreat = var1;
   }

   public int getPvpReachMode() {
      return this.pvpReachMode;
   }

   public void setPvpReachMode(int var1) {
      this.pvpReachMode = var1;
   }

   public int getPvpAggression() {
      return this.pvpAggression;
   }

   public void setPvpAggression(int var1) {
      this.pvpAggression = var1;
   }

   public int getPvpCritChance() {
      return this.pvpCritChance;
   }

   public void setPvpCritChance(int var1) {
      this.pvpCritChance = var1;
   }

   public int getPvpCritSpeed() {
      return this.pvpCritSpeed;
   }

   public void setPvpCritSpeed(int var1) {
      this.pvpCritSpeed = var1;
   }

   public boolean isCpvpEnabled() {
      return this.cpvpEnabled;
   }

   public void setCpvpEnabled(boolean var1) {
      this.cpvpEnabled = var1;
      this.enforceCpvpLookLock();
      if (var1) {
         this.pvpEnabled = false;
         this.lookAtOwner = false;
         this.followOwner = false;
         this.randomWalk = false;
         if (this.cpvpSettings == null) {
            this.initializeCpvpSettingsInternal();
         }
      }
   }

   public ae getCpvpSettings() {
      if (this.cpvpSettings == null) {
         this.initializeCpvpSettingsInternal();
      }

      return this.cpvpSettings;
   }

   private void initializeCpvpSettingsInternal() {
      this.cpvpSettings = new ae();
      PracticeBotPlugin var1 = PracticeBotPlugin.getInstance();
      if (var1 != null && var1.getConfigManager() != null) {
         af var2 = var1.getConfigManager().e();
         if (var2 != null) {
            this.cpvpSettings.a(var2);
         }
      }
   }

   public void initializeCpvpSettings(PracticeBotPlugin var1) {
      this.initializeCpvpSettingsInternal();
   }

   public void setCpvpSettings(ae var1) {
      this.cpvpSettings = var1;
   }

   public boolean isShieldDisabled(long var1) {
      return var1 < this.shieldDisabledUntil;
   }

   public boolean isInKnockback(long var1) {
      if (var1 >= this.knockbackUntil) {
         this.inKnockback = false;
      }

      return this.inKnockback;
   }

   public void setKnockback(long var1) {
      this.knockbackUntil = System.currentTimeMillis() + var1;
      this.inKnockback = true;
   }

   public void triggerKnockback(long var1) {
      this.wasKnockedBack = true;
      this.knockbackTime = var1;
      this.inKnockback = true;
      this.knockbackUntil = var1 + 500L;
   }

   public boolean isRecentlyKnockedBack(long var1) {
      return this.wasKnockedBack && var1 - this.knockbackTime < 600L;
   }

   public long beginDelayedAttack() {
      this.pendingDelayedAttackId++;
      this.pendingDelayedAttack = true;
      return this.pendingDelayedAttackId;
   }

   public boolean isPendingDelayedAttack(long var1) {
      return this.pendingDelayedAttack && this.pendingDelayedAttackId == var1;
   }

   public void finishDelayedAttack(long var1) {
      if (this.pendingDelayedAttackId == var1) {
         this.pendingDelayedAttack = false;
      }
   }

   public void cancelDelayedAttack() {
      this.pendingDelayedAttack = false;
      this.pendingDelayedAttackId++;
   }

   public void beginCritAttempt(long var1, double var3) {
      this.attemptingCrit = true;
      this.critJumpTime = var1;
      this.critPhase = 0;
      this.critStartY = var3;
      this.peakY = var3;
      this.critAirborneTicks = 0;
      this.critDescendingTicks = 0;
      this.critSawAscent = false;
      this.critSawDescent = false;
      this.forceNormalAttackAfterCritFail = false;
   }

   public void finishCritAttempt(boolean var1) {
      this.attemptingCrit = false;
      this.critJumpTime = 0L;
      this.critPhase = 0;
      this.critStartY = 0.0;
      this.peakY = 0.0;
      this.critAirborneTicks = 0;
      this.critDescendingTicks = 0;
      this.critSawAscent = false;
      this.critSawDescent = false;
      this.forceNormalAttackAfterCritFail = var1;
   }

   public void clearCritFallback() {
      this.forceNormalAttackAfterCritFail = false;
   }

   public void captureFrozenAnchor(Location var1) {
      if (var1 != null && var1.getWorld() != null) {
         this.frozenAnchorWorld = var1.getWorld().getName();
         this.frozenAnchorX = var1.getX();
         this.frozenAnchorY = var1.getY();
         this.frozenAnchorZ = var1.getZ();
         this.frozenAnchorYaw = var1.getYaw();
         this.frozenAnchorPitch = var1.getPitch();
      }
   }

   public void ensureFrozenAnchor(Location var1) {
      if (this.frozen) {
         if (this.getFrozenAnchorLocation() == null) {
            this.captureFrozenAnchor(var1);
         }
      }
   }

   public Location getFrozenAnchorLocation() {
      if (this.frozenAnchorWorld != null && !this.frozenAnchorWorld.isBlank()) {
         World var1 = Bukkit.getWorld(this.frozenAnchorWorld);
         return var1 == null
            ? null
            : new Location(var1, this.frozenAnchorX, this.frozenAnchorY, this.frozenAnchorZ, this.frozenAnchorYaw, this.frozenAnchorPitch);
      } else {
         return null;
      }
   }

   public void clearFrozenAnchor() {
      this.frozenAnchorWorld = null;
      this.frozenAnchorX = 0.0;
      this.frozenAnchorY = 0.0;
      this.frozenAnchorZ = 0.0;
      this.frozenAnchorYaw = 0.0F;
      this.frozenAnchorPitch = 0.0F;
   }

   public void clearKnockbackState() {
      this.wasKnockedBack = false;
      this.isLaunched = false;
   }

   public boolean shouldRetreatNow(long var1) {
      return this.shouldRetreat && var1 < this.retreatUntil;
   }

   public void triggerRetreat(long var1) {
      long var3 = System.currentTimeMillis();
      if (var3 - this.lastRetreatTime >= 1500L) {
         this.shouldRetreat = true;
         this.retreatUntil = var3 + var1;
         this.lastRetreatTime = var3;
      }
   }

   public void endRetreat() {
      this.shouldRetreat = false;
   }

   public void registerHitTaken(long var1) {
      this.registerHitTaken(var1, 0.0);
   }

   public void registerHitTaken(long var1, double var3) {
      if (var1 - this.lastHitTakenTime < 800L) {
         this.consecutiveHitsTaken++;
      } else {
         this.consecutiveHitsTaken = 1;
      }

      this.lastHitTakenTime = var1;
      this.recentHitTimes[this.recentHitCursor] = var1;
      this.recentHitDamages[this.recentHitCursor] = Math.max(0.0, var3);
      this.recentHitCursor = (this.recentHitCursor + 1) % this.recentHitTimes.length;
   }

   public void registerShieldPressure(long var1) {
      this.lastShieldPressureTime = var1;
   }

   public int countRecentHits(long var1, long var3) {
      int var5 = 0;

      for (long var9 : this.recentHitTimes) {
         if (var9 > 0L && var1 - var9 <= var3) {
            var5++;
         }
      }

      return var5;
   }

   public double getRecentDamageTaken(long var1, long var3) {
      double var5 = 0.0;

      for (int var7 = 0; var7 < this.recentHitTimes.length; var7++) {
         long var8 = this.recentHitTimes[var7];
         if (var8 > 0L && var1 - var8 <= var3) {
            var5 += this.recentHitDamages[var7];
         }
      }

      return var5;
   }

   public double getCritChancePercent() {
      return switch (this.pvpCritChance) {
         case 0 -> 0.25;
         case 1 -> 0.5;
         case 2 -> 0.75;
         default -> 1.0;
      };
   }

   public long getCritJumpDelay() {
      return switch (this.pvpCritSpeed) {
         case 0 -> 350L;
         case 1 -> 120L;
         default -> 0L;
      };
   }

   public String formatEnchantName(String var1) {
      if (var1 == null) {
         return "Protection IV";
      } else {
         return switch (var1) {
            case "blast_protection" -> "Blast Protection IV";
            case "fire_protection" -> "Fire Protection IV";
            case "projectile_protection" -> "Projectile Protection IV";
            case "none" -> "No Enchant";
            default -> "Protection IV";
         };
      }
   }

   private String normalizeTrimValue(String var1) {
      return var1 == null ? "" : var1.trim().toLowerCase();
   }

   private String normalizeArmorMaterial(String var1, String var2) {
      if (var1 != null && !var1.isBlank()) {
         return var1.trim().toLowerCase(Locale.ROOT);
      } else {
         return var2 != null && !var2.isBlank() ? var2.trim().toLowerCase(Locale.ROOT) : "netherite";
      }
   }

   private void refreshArmorTypeSnapshot() {
      this.armorType = this.normalizeArmorMaterial(this.chestplateMaterial, this.armorType);
   }

   public void load(DataKey var1) {
      String var2 = var1.getString("owner");
      if (var2 != null && !var2.isEmpty()) {
         try {
            this.owner = UUID.fromString(var2);
         } catch (IllegalArgumentException var9) {
            this.owner = null;
         }
      }

      String var3 = var1.getString("boundTarget");
      if (var3 != null && !var3.isEmpty()) {
         try {
            this.boundTarget = UUID.fromString(var3);
         } catch (IllegalArgumentException var8) {
            this.boundTarget = null;
         }
      } else {
         this.boundTarget = null;
      }

      this.autoTargetingEnabled = var1.getBoolean("autoTargetingEnabled", this.autoTargetingEnabled);
      this.autoTargetBotsOnly = var1.getBoolean("autoTargetBotsOnly", this.autoTargetBotsOnly);
      String var4 = var1.getString("botType", "NORMAL");

      try {
         this.botType = a.valueOf(var4);
      } catch (IllegalArgumentException var7) {
         this.botType = a.NORMAL;
      }

      this.lookAtOwner = var1.getBoolean("lookAtOwner", this.lookAtOwner);
      this.pvpEnabled = var1.getBoolean("pvpEnabled", this.pvpEnabled);
      this.followOwner = var1.getBoolean("followOwner", this.followOwner);
      this.randomWalk = var1.getBoolean("randomWalk", this.randomWalk);
      this.holdShield = var1.getBoolean("holdShield", this.holdShield);
      this.useShield = var1.getBoolean("useShield", this.useShield);
      this.resistance = var1.getBoolean("resistance", this.resistance);
      this.frozen = var1.getBoolean("frozen", this.frozen);
      this.shieldInMainHand = var1.getBoolean("shieldInMainHand", this.shieldInMainHand);
      this.guiEnabled = var1.getBoolean("guiEnabled", this.guiEnabled);
      this.frozenAnchorWorld = var1.getString("frozenAnchorWorld", this.frozenAnchorWorld);
      this.frozenAnchorX = var1.getDouble("frozenAnchorX", this.frozenAnchorX);
      this.frozenAnchorY = var1.getDouble("frozenAnchorY", this.frozenAnchorY);
      this.frozenAnchorZ = var1.getDouble("frozenAnchorZ", this.frozenAnchorZ);
      this.frozenAnchorYaw = (float)var1.getDouble("frozenAnchorYaw", this.frozenAnchorYaw);
      this.frozenAnchorPitch = (float)var1.getDouble("frozenAnchorPitch", this.frozenAnchorPitch);
      this.armorType = var1.getString("armorType", this.armorType);
      this.helmetMaterial = var1.getString("helmetMaterial", this.armorType);
      this.chestplateMaterial = var1.getString("chestplateMaterial", this.armorType);
      this.bootsMaterial = var1.getString("bootsMaterial", this.armorType);
      this.helmetEnchant = var1.getString("helmetEnchant", "protection");
      this.chestplateEnchant = var1.getString("chestplateEnchant", "protection");
      this.leggingsEnchant = var1.getString("leggingsEnchant", "protection");
      this.bootsEnchant = var1.getString("bootsEnchant", "protection");
      this.leggingsMaterial = var1.getString("leggingsMaterial", this.leggingsMaterial);
      this.refreshArmorTypeSnapshot();
      this.swordType = var1.getString("swordType", this.swordType);
      this.weaponProfile = this.normalizeWeaponProfile(var1.getString("weaponProfile", this.weaponProfile));
      this.offhandType = var1.getString("offhandType", this.offhandType);
      this.totemCount = var1.getInt("totemCount", this.totemCount);
      this.attackWarmupTicks = Math.max(0L, var1.getLong("attackWarmupTicks", this.attackWarmupTicks));
      this.setHelmetTrimPattern(var1.getString("helmetTrimPattern", this.helmetTrimPattern));
      this.setHelmetTrimMaterial(var1.getString("helmetTrimMaterial", this.helmetTrimMaterial));
      this.setChestTrimPattern(var1.getString("chestTrimPattern", this.chestTrimPattern));
      this.setChestTrimMaterial(var1.getString("chestTrimMaterial", this.chestTrimMaterial));
      this.setLegsTrimPattern(var1.getString("legsTrimPattern", this.legsTrimPattern));
      this.setLegsTrimMaterial(var1.getString("legsTrimMaterial", this.legsTrimMaterial));
      this.setBootsTrimPattern(var1.getString("bootsTrimPattern", this.bootsTrimPattern));
      this.setBootsTrimMaterial(var1.getString("bootsTrimMaterial", this.bootsTrimMaterial));
      this.pvpStrafe = var1.getBoolean("pvpStrafe", this.pvpStrafe);
      this.pvpWTap = var1.getBoolean("pvpWTap", this.pvpWTap);
      this.pvpSTap = var1.getBoolean("pvpSTap", this.pvpSTap);
      this.pvpCrits = var1.getBoolean("pvpCrits", this.pvpCrits);
      this.pvpShieldBreaker = var1.getBoolean("pvpShieldBreaker", this.pvpShieldBreaker);
      this.pvpRetreat = var1.getBoolean("pvpRetreat", this.pvpRetreat);
      this.pvpReachMode = var1.getInt("pvpReachMode", this.pvpReachMode);
      this.pvpAggression = var1.getInt("pvpAggression", this.pvpAggression);
      this.pvpCritChance = var1.getInt("pvpCritChance", this.pvpCritChance);
      this.pvpCritSpeed = var1.getInt("pvpCritSpeed", this.pvpCritSpeed);
      this.cpvpEnabled = var1.getBoolean("cpvpEnabled", this.cpvpEnabled);
      this.cpvpSettings = new ae();
      PracticeBotPlugin var5 = PracticeBotPlugin.getInstance();
      if (var5 != null && var5.getConfigManager() != null) {
         af var6 = var5.getConfigManager().e();
         if (var6 != null) {
            this.cpvpSettings.a(var6);
         }
      }

      String var10 = var1.getString("cpvp.skillLevel", "EASY");
      this.cpvpSettings.ax(var10);
      if (var1.getBoolean("cpvp.hasCustomSettings", false)) {
         this.cpvpSettings.b(var1.getDouble("cpvp.aggressionWeight", this.cpvpSettings.aI()));
         this.cpvpSettings.a(var1.getBoolean("cpvp.usePearls", this.cpvpSettings.aK()));
         this.cpvpSettings.b(var1.getBoolean("cpvp.useMace", this.cpvpSettings.aL()));
         this.cpvpSettings.c(var1.getBoolean("cpvp.useGoldenApples", this.cpvpSettings.aM()));
         this.cpvpSettings.d(var1.getBoolean("cpvp.placeObsidian", this.cpvpSettings.aN()));
         this.cpvpSettings.e(var1.getBoolean("cpvp.breakBlocks", this.cpvpSettings.aO()));
         this.cpvpSettings.f(var1.getBoolean("cpvp.strafingEnabled", this.cpvpSettings.aP()));
         this.cpvpSettings.g(var1.getBoolean("cpvp.anchoringMode", this.cpvpSettings.aQ()));
         this.cpvpSettings.c(var1.getDouble("cpvp.healThreshold", this.cpvpSettings.aR()));
         this.cpvpSettings.d(var1.getDouble("cpvp.lowHpCrystalLethalReserve", this.cpvpSettings.aS()));
         this.cpvpSettings.b(var1.getLong("cpvp.pearlCooldownMs", this.cpvpSettings.aT()));
         this.cpvpSettings.e(var1.getDouble("cpvp.fovDegrees", this.cpvpSettings.aU()));
         this.cpvpSettings
            .a(
               var1.getLong("cpvp.placeDelayMinMs", var1.getLong("cpvp.placeDelayMs", this.cpvpSettings.aW())),
               var1.getLong("cpvp.placeDelayMaxMs", var1.getLong("cpvp.placeDelayMs", this.cpvpSettings.aX()))
            );
         this.cpvpSettings
            .b(
               var1.getLong("cpvp.breakDelayMinMs", var1.getLong("cpvp.breakDelayMs", this.cpvpSettings.aZ())),
               var1.getLong("cpvp.breakDelayMaxMs", var1.getLong("cpvp.breakDelayMs", this.cpvpSettings.bA()))
            );
         this.cpvpSettings.e(var1.getLong("cpvp.swordDelayMs", this.cpvpSettings.bJ()));
         this.cpvpSettings.b(var1.getInt("cpvp.reactionHesitationChancePercent", this.cpvpSettings.bB()));
         this.cpvpSettings
            .c(var1.getLong("cpvp.reactionHesitationMinMs", this.cpvpSettings.bC()), var1.getLong("cpvp.reactionHesitationMaxMs", this.cpvpSettings.bD()));
         this.cpvpSettings.c(var1.getInt("cpvp.crystalMissChancePercent", this.cpvpSettings.bE()));
         this.cpvpSettings.d(var1.getLong("cpvp.crystalMissMinMs", this.cpvpSettings.bF()), var1.getLong("cpvp.crystalMissMaxMs", this.cpvpSettings.bG()));
         this.cpvpSettings.d(var1.getInt("cpvp.nonUrgentPearlRestraintChancePercent", this.cpvpSettings.bH()));
      }

      this.enforceCpvpLookLock();
   }

   public void onRemove() {
      this.cleanupCpvpRuntime();
      this.clearTransientCombatReferences();
      this.clearStoredInventory();
   }

   public void onDespawn() {
      this.cleanupCpvpRuntime();
      this.clearTransientCombatReferences();
   }

   private void cleanupCpvpRuntime() {
      if (this.npc != null && this.npc.getEntity() instanceof Player var1) {
         PracticeBotPlugin var3 = (PracticeBotPlugin)Bukkit.getPluginManager().getPlugin("PracticeBot");
         if (var3 != null && var3.getCrystalPvpModule() != null) {
            var3.getCrystalPvpModule().d(var1.getUniqueId());
         }
      }
   }

   private void clearTransientCombatReferences() {
      this.sTapDirection = null;
      this.previousMainHand = null;
      this.cancelDelayedAttack();
   }

   public void save(DataKey var1) {
      if (this.owner != null) {
         var1.setString("owner", this.owner.toString());
      }

      if (this.boundTarget != null) {
         var1.setString("boundTarget", this.boundTarget.toString());
      }

      var1.setBoolean("autoTargetingEnabled", this.autoTargetingEnabled);
      var1.setBoolean("autoTargetBotsOnly", this.autoTargetBotsOnly);
      var1.setString("botType", this.botType.name());
      var1.setBoolean("lookAtOwner", this.isLookAtOwner());
      var1.setBoolean("pvpEnabled", this.pvpEnabled);
      var1.setBoolean("followOwner", this.followOwner);
      var1.setBoolean("randomWalk", this.randomWalk);
      var1.setBoolean("holdShield", this.holdShield);
      var1.setBoolean("useShield", this.useShield);
      var1.setBoolean("resistance", this.resistance);
      var1.setBoolean("frozen", this.frozen);
      var1.setBoolean("shieldInMainHand", this.shieldInMainHand);
      var1.setBoolean("guiEnabled", this.guiEnabled);
      var1.setString("frozenAnchorWorld", this.frozenAnchorWorld);
      var1.setDouble("frozenAnchorX", this.frozenAnchorX);
      var1.setDouble("frozenAnchorY", this.frozenAnchorY);
      var1.setDouble("frozenAnchorZ", this.frozenAnchorZ);
      var1.setDouble("frozenAnchorYaw", this.frozenAnchorYaw);
      var1.setDouble("frozenAnchorPitch", this.frozenAnchorPitch);
      this.refreshArmorTypeSnapshot();
      var1.setString("armorType", this.armorType);
      var1.setString("helmetMaterial", this.helmetMaterial);
      var1.setString("chestplateMaterial", this.chestplateMaterial);
      var1.setString("bootsMaterial", this.bootsMaterial);
      var1.setString("helmetEnchant", this.helmetEnchant);
      var1.setString("chestplateEnchant", this.chestplateEnchant);
      var1.setString("leggingsEnchant", this.leggingsEnchant);
      var1.setString("bootsEnchant", this.bootsEnchant);
      var1.setString("leggingsMaterial", this.leggingsMaterial);
      var1.setString("swordType", this.swordType);
      var1.setString("weaponProfile", this.weaponProfile);
      var1.setString("offhandType", this.offhandType);
      var1.setInt("totemCount", this.totemCount);
      var1.setLong("attackWarmupTicks", this.attackWarmupTicks);
      var1.setString("helmetTrimPattern", this.helmetTrimPattern);
      var1.setString("helmetTrimMaterial", this.helmetTrimMaterial);
      var1.setString("chestTrimPattern", this.chestTrimPattern);
      var1.setString("chestTrimMaterial", this.chestTrimMaterial);
      var1.setString("legsTrimPattern", this.legsTrimPattern);
      var1.setString("legsTrimMaterial", this.legsTrimMaterial);
      var1.setString("bootsTrimPattern", this.bootsTrimPattern);
      var1.setString("bootsTrimMaterial", this.bootsTrimMaterial);
      var1.setBoolean("pvpStrafe", this.pvpStrafe);
      var1.setBoolean("pvpWTap", this.pvpWTap);
      var1.setBoolean("pvpSTap", this.pvpSTap);
      var1.setBoolean("pvpCrits", this.pvpCrits);
      var1.setBoolean("pvpShieldBreaker", this.pvpShieldBreaker);
      var1.setBoolean("pvpRetreat", this.pvpRetreat);
      var1.setInt("pvpReachMode", this.pvpReachMode);
      var1.setInt("pvpAggression", this.pvpAggression);
      var1.setInt("pvpCritChance", this.pvpCritChance);
      var1.setInt("pvpCritSpeed", this.pvpCritSpeed);
      var1.setBoolean("cpvpEnabled", this.cpvpEnabled);
      if (this.cpvpSettings != null) {
         var1.setBoolean("cpvp.hasCustomSettings", true);
         var1.setString("cpvp.skillLevel", this.cpvpSettings.aG());
         var1.setDouble("cpvp.aggressionWeight", this.cpvpSettings.aI());
         var1.setBoolean("cpvp.usePearls", this.cpvpSettings.aK());
         var1.setBoolean("cpvp.useMace", this.cpvpSettings.aL());
         var1.setBoolean("cpvp.useGoldenApples", this.cpvpSettings.aM());
         var1.setBoolean("cpvp.placeObsidian", this.cpvpSettings.aN());
         var1.setBoolean("cpvp.breakBlocks", this.cpvpSettings.aO());
         var1.setBoolean("cpvp.strafingEnabled", this.cpvpSettings.aP());
         var1.setBoolean("cpvp.anchoringMode", this.cpvpSettings.aQ());
         var1.setDouble("cpvp.healThreshold", this.cpvpSettings.aR());
         var1.setDouble("cpvp.lowHpCrystalLethalReserve", this.cpvpSettings.aS());
         var1.setLong("cpvp.pearlCooldownMs", this.cpvpSettings.aT());
         var1.setDouble("cpvp.fovDegrees", this.cpvpSettings.aU());
         var1.setLong("cpvp.placeDelayMinMs", this.cpvpSettings.aW());
         var1.setLong("cpvp.placeDelayMaxMs", this.cpvpSettings.aX());
         var1.setLong("cpvp.breakDelayMinMs", this.cpvpSettings.aZ());
         var1.setLong("cpvp.breakDelayMaxMs", this.cpvpSettings.bA());
         var1.setLong("cpvp.swordDelayMs", this.cpvpSettings.bJ());
         var1.setInt("cpvp.reactionHesitationChancePercent", this.cpvpSettings.bB());
         var1.setLong("cpvp.reactionHesitationMinMs", this.cpvpSettings.bC());
         var1.setLong("cpvp.reactionHesitationMaxMs", this.cpvpSettings.bD());
         var1.setInt("cpvp.crystalMissChancePercent", this.cpvpSettings.bE());
         var1.setLong("cpvp.crystalMissMinMs", this.cpvpSettings.bF());
         var1.setLong("cpvp.crystalMissMaxMs", this.cpvpSettings.bG());
         var1.setInt("cpvp.nonUrgentPearlRestraintChancePercent", this.cpvpSettings.bH());
      }
   }
}
