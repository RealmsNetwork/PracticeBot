package com.sheldera.practicebot.combat;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.TemplateDifficultyTuning;
import com.sheldera.practicebot.MaceDifficultyTuning;
import com.sheldera.practicebot.PracticeBotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class ModernWeaponCombat {
    private static volatile String lastNativeFailureLogged;
    private static final double MACE_MIN_SMASH_FALL = 1.5D;
    private static final double MACE_MAX_REACH = 3.0D;
    private static final double MACE_HITBOX_MARGIN = 0.125D;

    private static final double SPEAR_MIN_REACH = 2.0D;
    private static final double SPEAR_MAX_REACH = 4.5D;
    private static final double SPEAR_HITBOX_MARGIN = 0.125D;

    // 1.21.11 Netherite Spear kinetic_weapon values.
    private static final long SPEAR_DELAY_TICKS = 8L;
    private static final long SPEAR_DAMAGE_MAX_DURATION_TICKS = 175L;
    private static final long SPEAR_KNOCKBACK_MAX_DURATION_TICKS = 110L;
    private static final long SPEAR_DISMOUNT_MAX_DURATION_TICKS = 50L;
    private static final double SPEAR_DAMAGE_MIN_RELATIVE_SPEED = 4.6D;
    private static final double SPEAR_KNOCKBACK_MIN_SPEED = 5.1D;
    private static final double SPEAR_DISMOUNT_MIN_SPEED = 9.0D;
    private static final double SPEAR_DAMAGE_MULTIPLIER = 1.2D;
    private static final long SPEAR_CONTACT_COOLDOWN_TICKS = 10L;

    private final PracticeBotPlugin plugin;
    private final PvpEnvironment environment;

    public ModernWeaponCombat(PracticeBotPlugin plugin) {
        this.plugin = plugin;
        this.environment = new PvpEnvironment(plugin);
    }

    public boolean isModernWeapon(Player player) {
        return player != null && isModernWeapon(player.getInventory().getItemInMainHand());
    }

    public boolean isModernWeapon(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }

        String name = item.getType().name();
        return "MACE".equals(name) || name.endsWith("_SPEAR");
    }

    public double attackReach(Player player, double configuredReach) {
        ItemStack item = player == null ? null : player.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            return configuredReach;
        }

        if (item.getType() == Material.MACE) {
            PaperItemDataBridge.AttackRangeData range =
                PaperItemDataBridge.attackRange(item);

            return range == null
                ? MACE_MAX_REACH + MACE_HITBOX_MARGIN
                : range.maxReach() + range.hitboxMargin();
        }

        if (isSpear(item)) {
            PaperItemDataBridge.AttackRangeData range =
                PaperItemDataBridge.attackRange(item);

            return range == null
                ? SPEAR_MAX_REACH + SPEAR_HITBOX_MARGIN
                : range.maxReach() + range.hitboxMargin();
        }

        return configuredReach;
    }

    public void tick(Player bot, Player target, BotTrait trait, long now) {
        if (bot == null || target == null || trait == null ||
            bot.isDead() || target.isDead() ||
            !bot.getWorld().equals(target.getWorld())) {
            if (bot != null && trait != null) {
                restoreMaceWeapon(bot, trait);
                NmsCombatBridge.stopUsingItem(bot);
                removeAttributeSwapModifier(bot, trait);
            }
            reset(trait);
            return;
        }

        restoreMaceWeaponIfDue(bot, trait, now);
        if (trait.maceWindChargePending) {
            NmsCombatBridge.stopUsingItem(bot);
            removeAttributeSwapModifier(bot, trait);
            tickMace(bot, target, trait, now);
            return;
        }

        if ("spear_mace".equals(trait.getWeaponProfile()) ||
            "spear_mace_elytra".equals(trait.getWeaponProfile())) {
            selectHybridWeapon(bot, target, trait);
        }

        ItemStack item = bot.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            NmsCombatBridge.stopUsingItem(bot);
            removeAttributeSwapModifier(bot, trait);
            reset(trait);
            return;
        }

        if (item.getType() == Material.MACE) {
            NmsCombatBridge.stopUsingItem(bot);
            removeAttributeSwapModifier(bot, trait);
            trait.spearChargeStartedAt = 0L;
            tickMace(bot, target, trait, now);
        } else if (isSpear(item)) {
            tickSpear(bot, target, trait, now);
        } else {
            NmsCombatBridge.stopUsingItem(bot);
            removeAttributeSwapModifier(bot, trait);
            reset(trait);
        }
    }

    public boolean canAttemptAttack(Player bot, Player target, BotTrait trait, long now) {
        ItemStack item = bot.getInventory().getItemInMainHand();

        if (!isModernWeapon(item) || target == null || target.isDead()) {
            return false;
        }

        if (now < trait.tacticCooldownUntil) {
            return false;
        }

        if (item.getType() == Material.MACE &&
            trait.maceLaunchActive &&
            !bot.isOnGround()) {
            return true;
        }

        if (isSpear(item) && NmsCombatBridge.isUsingItem(bot)) {
            return false;
        }

        return now >= trait.modernWeaponCooldownUntil &&
            now - trait.lastAttackTime >= attackCooldownMs(bot);
    }

    public boolean tryAttack(Player bot, Player target, BotTrait trait, long now) {
        ItemStack item = bot.getInventory().getItemInMainHand();

        if (!isModernWeapon(item)) {
            return false;
        }

        if (bot.getGameMode() == GameMode.SPECTATOR ||
            target.getGameMode() == GameMode.SPECTATOR ||
            target.isDead()) {
            return true;
        }

        if (!bot.getWorld().equals(target.getWorld())) {
            return true;
        }

        if (humanMisplay(trait, now)) {
            return true;
        }

        if (item.getType() == Material.MACE) {
            return tryMace(bot, target, trait, now);
        }

        return trySpear(bot, target, trait, now);
    }

    private void tickMace(Player bot, Player target, BotTrait trait, long now) {
        if (maceTargetChanged(trait, target)) {
            restoreMaceWeapon(bot, trait);
            trait.maceLaunchActive = false;
            trait.maceWindChargePending = false;
            trait.maceAttemptedThisFall = false;
            trait.maceLaunchStartedAt = 0L;
        }
        trait.modernWeaponTarget = target.getUniqueId();

        if (trait.maceWindChargePending) {
            bot.setRotation(bot.getLocation().getYaw(), 90.0F);
            boolean blastDetected = bot.getVelocity().getY() > 0.12D ||
                bot.getLocation().getY() > trait.maceLaunchOriginY + 0.20D;
            if (blastDetected) {
                trait.maceWindChargePending = false;
                trait.lastTactic = "mace-wind-charge-boosted";
                return;
            }
            if (now - trait.maceLaunchStartedAt <= 1200L) {
                return;
            }
            trait.maceWindChargePending = false;
            restoreMaceWeapon(bot, trait);
            if (plugin.getConfig().getBoolean(
                "modern-weapons.mace.assisted-jump-without-wind-charge", false
            )) {
                Vector velocity = bot.getVelocity();
                velocity.setY(Math.max(velocity.getY(), 0.52D));
                bot.setVelocity(velocity);
                trait.lastTactic = "mace-assisted-jump-fallback";
            } else {
                trait.maceLaunchActive = false;
                trait.maceAttemptedThisFall = false;
                trait.lastTactic = "mace-wind-charge-no-blast";
            }
            return;
        }

        if (!bot.isOnGround() || bot.isGliding()) {
            if (trait.maceLaunchActive) {
                trait.macePeakY = Math.max(trait.macePeakY, bot.getLocation().getY());
                guideMaceFlight(bot, target, trait);
                if (now - trait.maceLaunchStartedAt > 6500L) {
                    trait.maceLaunchActive = false;
                    trait.maceWindChargePending = false;
                }
            } else {
                updateMaceAim(bot, target, trait);
            }
            return;
        }

        // Landing ends the previous aerial attempt. Never carry a miss/launch
        // flag into the next jump.
        if (trait.maceLaunchActive || trait.maceWindChargePending) {
            trait.maceLaunchActive = false;
            trait.maceWindChargePending = false;
            trait.maceAttemptedThisFall = false;
            trait.maceMissThisAttack = false;
            trait.maceLaunchStartedAt = 0L;
        }

        double distance = bot.getLocation().distance(target.getLocation());
        double launchMin = plugin.getConfig().getDouble(
            "modern-weapons.mace.launch-min-distance", 3.2D
        );
        double launchMax = plugin.getConfig().getDouble(
            "modern-weapons.mace.launch-max-distance", 10.0D
        );

        if (distance < launchMin || distance > launchMax ||
            now < trait.modernWeaponCooldownUntil ||
            now - trait.lastAttackTime < attackCooldownMs(bot) ||
            bot.hasPotionEffect(PotionEffectType.SLOW_FALLING)) {
            return;
        }

        int windChargeSlot = findWindChargeSlot(bot);
        boolean windChargeEnabled = plugin.getConfig().getBoolean(
            "modern-weapons.mace.wind-charge-enabled", true
        );
        boolean launchedWindCharge = false;

        if (windChargeEnabled && windChargeSlot >= 0) {
            float yaw = bot.getLocation().getYaw();
            // Look straight down and actually throw the Wind Charge at the
            // ground beneath the bot. The server owns the burst particles and
            // the resulting self-knockback.
            bot.setRotation(yaw, 90.0F);
            ItemStack heldWeapon = bot.getInventory().getItemInMainHand();
            trait.maceRestoreWeapon = heldWeapon == null ? null : heldWeapon.clone();
            trait.maceRestoreAt = now + 150L;
            bot.getInventory().setItemInMainHand(new ItemStack(Material.WIND_CHARGE));
            bot.swingMainHand();
            try {
                WindCharge projectile = bot.launchProjectile(
                    WindCharge.class, new Vector(0.0D, -1.0D, 0.0D)
                );
                if (projectile != null && projectile.isValid()) {
                    consumeWindCharge(bot, windChargeSlot);
                    launchedWindCharge = true;
                } else {
                    restoreMaceWeapon(bot, trait);
                }
            } catch (Throwable ex) {
                restoreMaceWeapon(bot, trait);
                plugin.debugLog(() -> "Mace Wind Charge launch failed: " + ex.getMessage());
            }
        }

        if (!launchedWindCharge) {
            if (!plugin.getConfig().getBoolean(
                "modern-weapons.mace.assisted-jump-without-wind-charge", false
            )) {
                return;
            }
            // Explicit fallback for servers where WindCharge projectiles cannot
            // be launched. This is not used when the real projectile succeeds.
            Vector velocity = bot.getVelocity();
            velocity.setY(Math.max(velocity.getY(), 0.52D));
            bot.setVelocity(velocity);
        }

        if (!launchedWindCharge) restoreMaceWeapon(bot, trait);
        trait.maceLaunchActive = true;
        trait.maceWindChargePending = launchedWindCharge;
        trait.maceAttemptedThisFall = false;
        trait.maceMissThisAttack = ThreadLocalRandom.current().nextDouble(100.0D) <
            MaceDifficultyTuning.getDouble(plugin, trait, "miss-chance-percent", 10.0D);
        double missOffset = Math.max(0.0D, MaceDifficultyTuning.getDouble(plugin, trait, "miss-offset-degrees", 6.0D));
        trait.maceMissOffsetDegrees = trait.maceMissThisAttack
            ? randomSigned(missOffset) : 0.0D;
        trait.maceLaunchStartedAt = now;
        trait.maceLaunchOriginY = bot.getLocation().getY();
        trait.macePeakY = bot.getLocation().getY();
        trait.modernWeaponCooldownUntil = now + 700L;
        trait.lastTactic = launchedWindCharge ? "mace-wind-charge-fired" : "mace-assisted-jump";
        bot.setSprinting(true);
    }

    private void guideMaceFlight(Player bot, Player target, BotTrait trait) {
        if (trait.maceWindChargePending) {
            // Keep the thrower's view down until the projectile has detonated.
            bot.setRotation(bot.getLocation().getYaw(), 90.0F);
            return;
        }

        updateMaceAim(bot, target, trait);

        Vector toward = target.getLocation().toVector()
            .subtract(bot.getLocation().toVector());
        toward.setY(0.0D);
        if (toward.lengthSquared() < 1.0E-5D) return;
        toward.normalize();

        // Correct the horizontal line while preserving the server-calculated
        // vertical blast velocity and normal gravity.
        Vector velocity = bot.getVelocity();
        velocity.setX(velocity.getX() * 0.78D + toward.getX() * 0.16D);
        velocity.setZ(velocity.getZ() * 0.78D + toward.getZ() * 0.16D);
        bot.setVelocity(velocity);
    }

    private void updateMaceAim(Player bot, Player target, BotTrait trait) {
        double lead = MaceDifficultyTuning.getDouble(plugin, trait, "target-lead-seconds", 0.08D);
        Vector predicted = target.getEyeLocation().toVector()
            .add(target.getVelocity().clone().multiply(Math.max(0.0D, Math.min(0.25D, lead))));
        Vector direction = predicted.subtract(bot.getEyeLocation().toVector());
        if (direction.lengthSquared() < 1.0E-5D) return;
        direction.normalize();

        Location desired = bot.getLocation().clone();
        desired.setDirection(direction);
        float yawDelta = wrapDegrees(desired.getYaw() - bot.getLocation().getYaw());
        float pitchDelta = desired.getPitch() - bot.getLocation().getPitch();
        float maxTurn = (float) MaceDifficultyTuning.getDouble(plugin, trait, "aim-max-degrees-per-tick", 32.0D);
        yawDelta = Math.max(-maxTurn, Math.min(maxTurn, yawDelta));
        pitchDelta = Math.max(-maxTurn, Math.min(maxTurn, pitchDelta));
        float yaw = bot.getLocation().getYaw() + yawDelta * 0.72F;
        float pitch = bot.getLocation().getPitch() + pitchDelta * 0.72F;

        if (trait.maceMissThisAttack && !trait.maceAttemptedThisFall) {
            yaw += (float) trait.maceMissOffsetDegrees;
        } else {
            float jitter = (float) MaceDifficultyTuning.getDouble(plugin, trait, "aim-jitter-degrees", 0.15D);
            yaw += (float) randomSigned(jitter);
            pitch += (float) randomSigned(jitter);
        }
        bot.setRotation(yaw, Math.max(-90.0F, Math.min(90.0F, pitch)));
    }

    private void restoreMaceWeaponIfDue(Player bot, BotTrait trait, long now) {
        if (trait.maceRestoreWeapon != null && now >= trait.maceRestoreAt) {
            restoreMaceWeapon(bot, trait);
        }
    }

    private void restoreMaceWeapon(Player bot, BotTrait trait) {
        if (bot != null && trait != null && trait.maceRestoreWeapon != null) {
            bot.getInventory().setItemInMainHand(trait.maceRestoreWeapon.clone());
            trait.maceRestoreWeapon = null;
            trait.maceRestoreAt = 0L;
        }
    }

    private int findWindChargeSlot(Player bot) {
        ItemStack[] contents = bot.getInventory().getStorageContents();
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack item = contents[slot];
            if (item != null && item.getType() == Material.WIND_CHARGE && item.getAmount() > 0) {
                return slot;
            }
        }
        return -1;
    }

    private void consumeWindCharge(Player bot, int slot) {
        ItemStack item = bot.getInventory().getItem(slot);
        if (item == null || item.getType() != Material.WIND_CHARGE) return;
        if (item.getAmount() <= 1) bot.getInventory().setItem(slot, null);
        else item.setAmount(item.getAmount() - 1);
    }

    private void selectHybridWeapon(Player bot, Player target, BotTrait trait) {
        double distance = bot.getLocation().distance(target.getLocation());
        ItemStack targetWeapon = target.getInventory().getItemInMainHand();
        boolean targetIsThreateningWithMace = targetWeapon != null &&
            targetWeapon.getType() == Material.MACE;
        double spearReach = SPEAR_MAX_REACH + SPEAR_HITBOX_MARGIN;
        double maceLaunchMax = plugin.getConfig().getDouble(
            "modern-weapons.mace.launch-max-distance", 10.0D
        );

        // Keep the spear selected throughout its actual kinetic charge window.
        // The previous 3.5-block cutoff selected the mace inside spear reach,
        // leaving only a tiny fraction of the spear's 2.0-4.5 block range usable.
        // Switch to mace outside spear reach for the wind-charge launch, and keep
        // it during the aerial smash. Also answer an opponent who is actively
        // holding a mace instead of waiting to be hit/knocked airborne first.
        boolean useMace = trait.maceLaunchActive || !bot.isOnGround() ||
            targetIsThreateningWithMace ||
            (distance > spearReach && distance <= maceLaunchMax &&
                !plugin.getConfig().getBoolean("modern-weapons.spear.wind-charge-approach.enabled", true));

        Material wanted = useMace ? Material.MACE : null;
        int selected = -1;
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = bot.getInventory().getItem(slot);
            if (stack == null || stack.getType().isAir()) continue;
            if (wanted != null && stack.getType() == wanted) { selected = slot; break; }
            if (wanted == null && stack.getType().name().endsWith("_SPEAR")) { selected = slot; break; }
        }
        if (selected >= 0 && bot.getInventory().getHeldItemSlot() != selected) {
            bot.getInventory().setHeldItemSlot(selected);
        }
    }

    private boolean maceAimHitsTarget(Player bot, Player target) {
        if (!bot.hasLineOfSight(target)) return false;
        Vector direction = bot.getEyeLocation().getDirection().normalize();
        return bot.getWorld().rayTraceEntities(
            bot.getEyeLocation(), direction, MACE_MAX_REACH + MACE_HITBOX_MARGIN, 0.08D,
            entity -> entity.getUniqueId().equals(target.getUniqueId())
        ) != null;
    }

    private float wrapDegrees(float value) {
        value %= 360.0F;
        if (value >= 180.0F) value -= 360.0F;
        if (value < -180.0F) value += 360.0F;
        return value;
    }

    private boolean tryMace(Player bot, Player target, BotTrait trait, long now) {
        double distance = bot.getLocation().distance(target.getLocation());
        if (distance > MACE_MAX_REACH + MACE_HITBOX_MARGIN) return true;
        if (maceTargetChanged(trait, target)) {
            trait.maceLaunchActive = false;
            trait.maceLaunchStartedAt = 0L;
        }
        trait.modernWeaponTarget = target.getUniqueId();

        boolean falling = !bot.isOnGround() && !bot.isGliding() &&
            bot.getVelocity().getY() < -0.02D;
        double fallDistance = Math.max(0.0D, bot.getFallDistance());

        if (shouldPerformMaceSmash(
            falling, fallDistance, bot.hasPotionEffect(PotionEffectType.SLOW_FALLING)
        )) {
            if (trait.maceAttemptedThisFall) return true;
            trait.maceAttemptedThisFall = true;
            if (!maceAimHitsTarget(bot, target)) {
                bot.swingMainHand();
                trait.lastAttackTime = now;
                trait.modernWeaponCooldownUntil = now + 350L;
                trait.lastTactic = "mace-missed";
                applyHumanAttackDelay(trait, now);
                return true;
            }
            performMaceSmash(bot, target, trait, fallDistance, now);
            return true;
        }

        if (!bot.isOnGround() || now < trait.modernWeaponCooldownUntil ||
            now - trait.lastAttackTime < attackCooldownMs(bot)) return true;
        if (!maceAimHitsTarget(bot, target)) return true;
        performMaceHit(bot, target, trait, now);
        return true;
    }

    private void performMaceHit(
        Player bot, Player target, BotTrait trait, long now
    ) {
        bot.swingMainHand();
        if (!performNativeAttackWithAttributeSwap(bot, target, trait)) {
            logNativeFailure("mace attack");
            return;
        }
        plugin.addCombatTag(target);
        trait.lastAttackTime = now;
        trait.modernWeaponCooldownUntil = now + attackCooldownMs(bot);
        applyHumanAttackDelay(trait, now);
        trait.lastTactic = "mace-native-hit";
    }

    private void performMaceSmash(
        Player bot,
        Player target,
        BotTrait trait,
        double fallDistance,
        long now
    ) {
        if (attemptStunSlam(bot, target, trait, fallDistance)) return;
        performMaceSmashNow(bot, target, trait, now);
    }

    private boolean performMaceSmashNow(Player bot, Player target, BotTrait trait, long now) {
        bot.swingMainHand();
        if (!performNativeAttackWithAttributeSwap(bot, target, trait)) {
            logNativeFailure("mace smash");
            trait.maceAttemptedThisFall = false;
            return false;
        }

        // The server now owns smash damage, fall-distance reset, area knockback,
        // particles, Wind Burst and all mace enchantment effects.
        plugin.addCombatTag(target);
        int windBurst = enchantLevel(bot.getInventory().getItemInMainHand(), "wind_burst");
        trait.maceLaunchActive = windBurst > 0 && bot.getVelocity().getY() > 0.05D;
        trait.maceWindChargePending = false;
        trait.maceAttemptedThisFall = true;
        trait.maceLaunchStartedAt = now;
        trait.lastAttackTime = now;
        trait.modernWeaponCooldownUntil = now + attackCooldownMs(bot);
        trait.lastTactic = trait.maceLaunchActive ? "mace-native-wind-burst" : "mace-native-smash";
        applyHumanAttackDelay(trait, now);
        return true;
    }

    private boolean attemptStunSlam(
        Player bot, Player target, BotTrait trait, double fallDistance
    ) {
        boolean enabled = plugin.getConfig().getBoolean("modern-weapons.mace.stun-slam.enabled", false);
        boolean requireShield = plugin.getConfig().getBoolean("modern-weapons.mace.stun-slam.require-shield", true);
        int axeSlot = -1;
        for (int slot = 0; slot < 9; slot++) {
            ItemStack item = bot.getInventory().getItem(slot);
            if (item != null && item.getType().name().endsWith("_AXE")) {
                axeSlot = slot;
                break;
            }
        }
        if (!shouldAttemptStunSlam(enabled, requireShield, target.isBlocking(), axeSlot >= 0)) return false;

        int maceSlot = bot.getInventory().getHeldItemSlot();
        boolean axeActionSucceeded = false;
        try {
            bot.getInventory().setHeldItemSlot(axeSlot);
            bot.swingMainHand();
            axeActionSucceeded = NmsCombatBridge.attack(bot, target);
        } catch (Throwable ex) {
            plugin.debugLog(() -> "Optional stun-slam axe step failed: " + ex.getMessage());
        } finally {
            bot.getInventory().setHeldItemSlot(maceSlot);
        }

        boolean shieldDisabled = target.getCooldown(Material.SHIELD) > 0 || !target.isBlocking();
        if (!axeActionSucceeded || (requireShield && !shieldDisabled)) return false;

        int delayTicks = stunSlamDelayTicks(
            plugin.getConfig().getInt("modern-weapons.mace.stun-slam.followup-delay-ticks", 2),
            plugin.getConfig().getInt("modern-weapons.mace.stun-slam.window-ticks", 5)
        );
        trait.maceAttemptedThisFall = true;
        trait.lastTactic = "mace-stun-slam-axe-stun";

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!bot.isValid() || bot.isDead() || !target.isValid() || target.isDead() ||
                !bot.getWorld().equals(target.getWorld()) ||
                !maceAimHitsTarget(bot, target)) {
                trait.maceAttemptedThisFall = false;
                return;
            }

            int currentMaceSlot = findHotbarSlot(bot, Material.MACE);
            if (currentMaceSlot >= 0) bot.getInventory().setHeldItemSlot(currentMaceSlot);
            if (bot.getInventory().getItemInMainHand() == null ||
                bot.getInventory().getItemInMainHand().getType() != Material.MACE) {
                trait.maceAttemptedThisFall = false;
                return;
            }

            // The axe's shield stun is real; clear only the target's short
            // invulnerability window immediately before the native mace hit.
            target.setNoDamageTicks(0);
            long followupNow = System.currentTimeMillis();
            if (performMaceSmashNow(bot, target, trait, followupNow)) {
                trait.lastTactic = "mace-stun-slam";
            }
        }, delayTicks);
        return true;
    }

    private int findHotbarSlot(Player player, Material material) {
        for (int slot = 0; slot < 9; slot++) {
            ItemStack item = player.getInventory().getItem(slot);
            if (item != null && item.getType() == material) return slot;
        }
        return -1;
    }

    static int stunSlamDelayTicks(int requestedDelay, int windowTicks) {
        int safeWindow = Math.max(1, Math.min(5, windowTicks));
        return Math.max(1, Math.min(safeWindow, requestedDelay));
    }

    private void tickSpear(
        Player bot, Player target, BotTrait trait, long now
    ) {
        if (spearTargetChanged(trait, target)) {
            NmsCombatBridge.stopUsingItem(bot);
            removeAttributeSwapModifier(bot, trait);
            trait.spearChargeStartedAt = 0L;
            trait.spearLastHitAt = 0L;
        }

        trait.modernWeaponTarget = target.getUniqueId();
        ItemStack spear = bot.getInventory().getItemInMainHand();
        PaperItemDataBridge.AttackRangeData range = PaperItemDataBridge.attackRange(spear);
        double minReach = range == null ? SPEAR_MIN_REACH : range.minReach();
        double maxReach = range == null ? SPEAR_MAX_REACH : range.maxReach();
        double distance = bot.getLocation().distance(target.getLocation());

        // Jab is the close-range attack. Let the server handle its piercing,
        // cooldown, enchantments and Lunge effects when trySpear invokes attack().
        boolean lungeApproach = shouldUseLungeJab(distance, maxReach,
            enchantLevel(spear, "lunge"), bot.isGliding(), bot.isInWater());
        if (distance <= 3.15D || lungeApproach || distance < minReach + 0.05D || distance > maxReach) {
            NmsCombatBridge.stopUsingItem(bot);
            removeAttributeSwapModifier(bot, trait);
            trait.spearChargeStartedAt = 0L;
            if (distance < minReach + 0.05D && !bot.isGliding()) {
                Vector away = bot.getLocation().toVector().subtract(target.getLocation().toVector());
                away.setY(0.0D);
                if (away.lengthSquared() > 1.0E-5D) {
                    away.normalize();
                    Vector velocity = bot.getVelocity();
                    velocity.setX(velocity.getX() * 0.55D + away.getX() * 0.12D);
                    velocity.setZ(velocity.getZ() * 0.55D + away.getZ() * 0.12D);
                    bot.setVelocity(velocity);
                }
            }
            return;
        }

        if (!plugin.getConfig().getBoolean("modern-weapons.spear.charge-enabled", true) ||
            (!bot.isOnGround() && !bot.isGliding())) {
            NmsCombatBridge.stopUsingItem(bot);
            removeAttributeSwapModifier(bot, trait);
            trait.spearChargeStartedAt = 0L;
            return;
        }

        if (!NmsCombatBridge.isUsingItem(bot)) {
            applyAttributeSwapModifier(bot, trait);
            if (NmsCombatBridge.startUsingMainHand(bot)) {
                trait.spearChargeStartedAt = now;
                trait.lastTactic = isElytraProfile(trait) && bot.isGliding()
                    ? "elytra-spear-charge" : "spear-charge";
            } else {
                removeAttributeSwapModifier(bot, trait);
                trait.spearChargeStartedAt = 0L;
                logNativeFailure("spear charge");
            }
        }
        bot.setSprinting(!bot.isGliding());
    }

    private boolean trySpear(
        Player bot, Player target, BotTrait trait, long now
    ) {
        double distance = bot.getLocation().distance(target.getLocation());
        ItemStack spear = bot.getInventory().getItemInMainHand();
        PaperItemDataBridge.AttackRangeData range = PaperItemDataBridge.attackRange(spear);
        double minReach = range == null ? SPEAR_MIN_REACH : range.minReach();
        double maxReach = range == null ? SPEAR_MAX_REACH : range.maxReach();
        double margin = range == null ? SPEAR_HITBOX_MARGIN : range.hitboxMargin();

        if (distance < minReach - margin || distance > maxReach + margin) return true;
        if (spearTargetChanged(trait, target)) {
            NmsCombatBridge.stopUsingItem(bot);
            trait.spearChargeStartedAt = 0L;
            trait.spearLastHitAt = 0L;
        }
        trait.modernWeaponTarget = target.getUniqueId();

        // While the spear is in its native use state, the server's
        // kinetic_weapon component owns charge timing, contact cooldown,
        // relative-speed damage, knockback and dismount checks.
        if (NmsCombatBridge.isUsingItem(bot)) return true;
        trait.spearChargeStartedAt = 0L;

        boolean lungeApproach = shouldUseLungeJab(distance, maxReach,
            enchantLevel(spear, "lunge"), bot.isGliding(), bot.isInWater());
        if ((distance > 3.15D && !lungeApproach) ||
            (!bot.isOnGround() && !lungeApproach) ||
            now < trait.modernWeaponCooldownUntil ||
            now - trait.lastAttackTime < attackCooldownMs(bot)) return true;

        performSpearJab(bot, target, trait, now);
        return true;
    }

    private void performSpearJab(
        Player bot, Player target, BotTrait trait, long now
    ) {
        NmsCombatBridge.stopUsingItem(bot);
        if (!spearAimHitsTarget(bot, target)) return;
        bot.swingMainHand();
        applyAttributeSwapModifier(bot, trait);
        boolean jabSucceeded;
        try {
            jabSucceeded = NmsCombatBridge.stabAttack(bot);
        } finally {
            removeAttributeSwapModifier(bot, trait);
        }
        if (!jabSucceeded) {
            logNativeFailure("spear jab");
            return;
        }
        plugin.addCombatTag(target);
        trait.lastAttackTime = now;
        trait.modernWeaponCooldownUntil = now + attackCooldownMs(bot);
        trait.spearChargeStartedAt = 0L;
        applyHumanAttackDelay(trait, now);
        trait.lastTactic = "spear-native-jab";
    }

    private boolean spearAimHitsTarget(Player bot, Player target) {
        if (!bot.hasLineOfSight(target)) return false;
        ItemStack spear = bot.getInventory().getItemInMainHand();
        PaperItemDataBridge.AttackRangeData range = PaperItemDataBridge.attackRange(spear);
        double reach = range == null ? SPEAR_MAX_REACH + SPEAR_HITBOX_MARGIN
            : range.maxReach() + range.hitboxMargin();
        Vector direction = bot.getEyeLocation().getDirection().normalize();
        return bot.getWorld().rayTraceEntities(
            bot.getEyeLocation(), direction, reach, 0.08D,
            entity -> entity.getUniqueId().equals(target.getUniqueId())
        ) != null;
    }

    private long spearDelayMs(ItemStack spear) {
        PaperItemDataBridge.KineticData kinetic =
            PaperItemDataBridge.kineticWeapon(spear);

        if (kinetic != null) {
            return kinetic.delayTicks() * 50L;
        }

        return plugin.getConfig().getLong(
            "modern-weapons.spear.charge-delay-ms",
            SPEAR_DELAY_TICKS * 50L
        );
    }

    private long spearContactCooldownMs(ItemStack spear) {
        PaperItemDataBridge.KineticData kinetic =
            PaperItemDataBridge.kineticWeapon(spear);

        if (kinetic != null) {
            return kinetic.contactCooldownTicks() * 50L;
        }

        return plugin.getConfig().getLong(
            "modern-weapons.spear.contact-cooldown-ms",
            SPEAR_CONTACT_COOLDOWN_TICKS * 50L
        );
    }

    private long kineticDamageMaxDuration(ItemStack spear) {
        PaperItemDataBridge.KineticData kinetic =
            PaperItemDataBridge.kineticWeapon(spear);

        if (kinetic != null && kinetic.damage() != null) {
            return kinetic.damage().maxDurationTicks();
        }

        return plugin.getConfig().getLong(
            "modern-weapons.spear.damage-max-duration-ticks",
            SPEAR_DAMAGE_MAX_DURATION_TICKS
        );
    }

    private double kineticDamageMinRelativeSpeed(ItemStack spear) {
        PaperItemDataBridge.KineticData kinetic =
            PaperItemDataBridge.kineticWeapon(spear);

        if (kinetic != null && kinetic.damage() != null) {
            return kinetic.damage().minRelativeSpeed();
        }

        return plugin.getConfig().getDouble(
            "modern-weapons.spear.damage-min-relative-speed-bps",
            SPEAR_DAMAGE_MIN_RELATIVE_SPEED
        );
    }

    private long kineticKnockbackMaxDuration(ItemStack spear) {
        PaperItemDataBridge.KineticData kinetic =
            PaperItemDataBridge.kineticWeapon(spear);

        if (kinetic != null && kinetic.knockback() != null) {
            return kinetic.knockback().maxDurationTicks();
        }

        return plugin.getConfig().getLong(
            "modern-weapons.spear.knockback-max-duration-ticks",
            SPEAR_KNOCKBACK_MAX_DURATION_TICKS
        );
    }

    private double kineticKnockbackMinSpeed(ItemStack spear) {
        PaperItemDataBridge.KineticData kinetic =
            PaperItemDataBridge.kineticWeapon(spear);

        if (kinetic != null && kinetic.knockback() != null) {
            return kinetic.knockback().minSpeed();
        }

        return plugin.getConfig().getDouble(
            "modern-weapons.spear.knockback-min-speed-bps",
            SPEAR_KNOCKBACK_MIN_SPEED
        );
    }

    private long kineticDismountMaxDuration(ItemStack spear) {
        PaperItemDataBridge.KineticData kinetic =
            PaperItemDataBridge.kineticWeapon(spear);

        if (kinetic != null && kinetic.dismount() != null) {
            return kinetic.dismount().maxDurationTicks();
        }

        return plugin.getConfig().getLong(
            "modern-weapons.spear.dismount-max-duration-ticks",
            SPEAR_DISMOUNT_MAX_DURATION_TICKS
        );
    }

    private double kineticDismountMinSpeed(ItemStack spear) {
        PaperItemDataBridge.KineticData kinetic =
            PaperItemDataBridge.kineticWeapon(spear);

        if (kinetic != null && kinetic.dismount() != null) {
            return kinetic.dismount().minSpeed();
        }

        return plugin.getConfig().getDouble(
            "modern-weapons.spear.dismount-min-speed-bps",
            SPEAR_DISMOUNT_MIN_SPEED
        );
    }

    private double spearBaseDamage(Player bot) {
        AttributeInstance attribute =
            bot.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);

        if (attribute == null) {
            return 5.0D;
        }

        double base = attribute.getBaseValue();
        double total = attribute.getValue();
        double itemAndOtherBonus = Math.max(0.0D, total - base);

        return Math.max(
            1.0D,
            plugin.getConfig().getDouble(
                "modern-weapons.spear.base-damage",
                Math.max(1.0D, itemAndOtherBonus)
            )
        );
    }

    private double itemAttackDamage(Player bot) {
        AttributeInstance attribute =
            bot.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);

        return attribute == null
            ? 1.0D
            : Math.max(1.0D, attribute.getValue());
    }

    private double attributeDamageBonus(Player bot) {
        AttributeInstance attribute =
            bot.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);

        if (attribute == null) {
            return 0.0D;
        }

        return Math.max(0.0D, attribute.getValue() - attribute.getBaseValue());
    }

    private void logNativeFailure(String action) {
        String failure = NmsCombatBridge.lastFailure();
        if (failure == null || failure.equals(lastNativeFailureLogged)) return;
        lastNativeFailureLogged = failure;
        plugin.getLogger().warning("Native vanilla " + action + " could not be invoked: " + failure + ". The bot will not substitute simulated damage.");
    }

    private boolean performNativeAttackWithAttributeSwap(Player bot, Player target, BotTrait trait) {
        applyAttributeSwapModifier(bot, trait);
        try {
            return NmsCombatBridge.attack(bot, target);
        } finally {
            removeAttributeSwapModifier(bot, trait);
        }
    }

    private void applyAttributeSwapModifier(Player bot, BotTrait trait) {
        if (trait.attributeSwapModifier != null) return;
        double bonus = attributeSwapBonus(bot, trait);
        if (bonus <= 0.0D) return;
        AttributeInstance attribute = bot.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attribute == null) return;
        NamespacedKey key = new NamespacedKey(plugin, "attribute-swap-" + java.util.UUID.randomUUID().toString().replace("-", ""));
        AttributeModifier modifier = new AttributeModifier(
            key, bonus, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY
        );
        try {
            attribute.addTransientModifier(modifier);
            trait.attributeSwapModifier = modifier;
        } catch (Throwable throwable) {
            plugin.debugLog(() -> "Could not apply native attribute-swap modifier: " + throwable.getMessage());
        }
    }

    private void removeAttributeSwapModifier(Player bot, BotTrait trait) {
        if (trait == null || trait.attributeSwapModifier == null) return;
        AttributeInstance attribute = bot == null ? null : bot.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attribute != null) {
            try {
                attribute.removeModifier(trait.attributeSwapModifier);
            } catch (Throwable ignored) {
            }
        }
        trait.attributeSwapModifier = null;
    }

    private double attributeSwapBonus(Player bot, BotTrait trait) {
        if (!environment.isAttributeSwapAvailable() ||
            !plugin.getConfig().getBoolean(
                "tactics.attribute-swap.enabled", true
            )) {
            return 0.0D;
        }

        double chance = plugin.getConfig().getDouble(
            "tactics.attribute-swap.chance-percent", 10.0D
        );

        if (ThreadLocalRandom.current().nextDouble(100.0D) >= chance) {
            return 0.0D;
        }

        ItemStack original =
            bot.getInventory().getItemInMainHand();

        ItemStack alternate = findAlternateWeapon(
            bot.getInventory(), original
        );

        if (alternate == null) {
            return 0.0D;
        }

        AttributeInstance attribute =
            bot.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);

        if (attribute == null) {
            return 0.0D;
        }

        double current = attribute.getValue();

        // This intentionally measures a real server-side attribute delta from
        // the actual inventory rather than hard-coding weapon damage values.
        bot.getInventory().setItemInMainHand(alternate.clone());

        double swapped = attribute.getValue();

        bot.getInventory().setItemInMainHand(original);

        trait.lastTactic = "attribute-swap";
        return Math.max(0.0D, swapped - current);
    }

    private ItemStack findAlternateWeapon(
        PlayerInventory inventory, ItemStack current
    ) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack candidate = inventory.getItem(slot);

            if (candidate == null || candidate.getType().isAir()) {
                continue;
            }

            String name = candidate.getType().name();

            if ((name.endsWith("_SWORD") ||
                name.endsWith("_AXE") ||
                name.equals("MACE") ||
                name.endsWith("_MACE") ||
                name.endsWith("_SPEAR")) &&
                (current == null ||
                 candidate.getType() != current.getType())) {
                return candidate;
            }
        }

        return null;
    }

    private long attackCooldownMs(Player bot) {
        AttributeInstance attribute =
            bot.getAttribute(Attribute.GENERIC_ATTACK_SPEED);

        double speed =
            attribute == null ? 4.0D : attribute.getValue();

        if (speed < 0.01D) {
            speed = 4.0D;
        }

        return Math.max(
            50L,
            Math.round(1000.0D / speed)
        );
    }

    private boolean humanMisplay(BotTrait trait, long now) {
        double chance = TemplateDifficultyTuning.getDouble(plugin, trait, "mistake-chance-percent", 3.0D,
            "tactics.humanization.mistake-chance-percent");

        if (chance <= 0.0D ||
            ThreadLocalRandom.current().nextDouble(100.0D) >= chance) {
            return false;
        }

        long min = TemplateDifficultyTuning.getLong(plugin, trait, "mistake-delay-min-ms", 55L,
            "tactics.humanization.mistake-delay-min-ms");
        long max = TemplateDifficultyTuning.getLong(plugin, trait, "mistake-delay-max-ms", 180L,
            "tactics.humanization.mistake-delay-max-ms");

        long delay = ThreadLocalRandom.current().nextLong(
            Math.max(0L, Math.min(min, max)),
            Math.max(0L, Math.max(min, max)) + 1L
        );

        trait.tacticCooldownUntil = now + delay;
        trait.lastTactic = "human-misplay";
        return true;
    }

    private void applyHumanAttackDelay(BotTrait trait, long now) {
        int ping = Math.max(0, trait.simulatedPingMs);

        double fraction = TemplateDifficultyTuning.getDouble(plugin, trait, "ping-action-latency-fraction", 0.35D,
            "tactics.humanization.ping.action-latency-fraction");

        double min = TemplateDifficultyTuning.getDouble(plugin, trait, "attack-delay-min-ms", 8.0D,
            "tactics.humanization.attack.min-delay-ms");

        double max = TemplateDifficultyTuning.getDouble(plugin, trait, "attack-delay-max-ms", 42.0D,
            "tactics.humanization.attack.max-delay-ms");

        double reaction = ThreadLocalRandom.current().nextDouble(
            Math.min(min, max),
            Math.max(min, max) + 0.01D
        );

        trait.tacticCooldownUntil =
            now + Math.max(0L, Math.round(reaction + ping * fraction));
    }

    private boolean isShieldUp(Player player) {
        if (!player.isHandRaised()) {
            return false;
        }

        ItemStack active = player.getActiveItem();

        return active != null &&
            active.getType() == Material.SHIELD &&
            player.getCooldown(Material.SHIELD) == 0;
    }

    private int enchantLevel(ItemStack item, String key) {
        if (item == null || !item.hasItemMeta()) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return 0;
        }

        for (Map.Entry<Enchantment, Integer> entry :
            meta.getEnchants().entrySet()) {
            String enchantKey =
                entry.getKey().getKey().getKey();

            if (key.equalsIgnoreCase(enchantKey)) {
                return Math.max(0, entry.getValue());
            }
        }

        return 0;
    }

    private double enchantBonus(ItemStack item, String key) {
        int level = enchantLevel(item, key);
        return level > 0 ? 0.5D + level * 0.5D : 0.0D;
    }

    static boolean shouldUseLungeJab(
        double distance, double maxReach, int lungeLevel, boolean gliding, boolean inWater
    ) {
        return lungeLevel > 0 && Double.isFinite(distance) && distance > 3.15D &&
            distance <= maxReach && !gliding && !inWater;
    }

    static boolean shouldAttemptStunSlam(
        boolean enabled, boolean requireShield, boolean targetBlocking, boolean hasAxe
    ) {
        return enabled && hasAxe && (!requireShield || targetBlocking);
    }

    static boolean shouldPerformMaceSmash(
        boolean falling, double fallDistance, boolean slowFalling
    ) {
        return falling && Double.isFinite(fallDistance) &&
            fallDistance >= MACE_MIN_SMASH_FALL && !slowFalling;
    }

    /** Reference formula retained for regression tests; live mace hits use NMS. */
    static double maceSmashBonus(double fallDistance) {
        if (fallDistance <= MACE_MIN_SMASH_FALL) {
            return 0.0D;
        }

        return Math.min(fallDistance, 3.0D) * 4.0D
            + Math.min(
                Math.max(fallDistance - 3.0D, 0.0D),
                5.0D
            ) * 2.0D
            + Math.max(fallDistance - 8.0D, 0.0D);
    }

    static double maceSmashDamage(
        double baseAttackDamage, double fallDistance, int densityLevel
    ) {
        return Math.max(
            0.0D,
            baseAttackDamage
                + maceSmashBonus(fallDistance)
                + (0.5D * Math.max(0, densityLevel) * Math.max(0.0D, fallDistance))
        );
    }

    /** Reference formula retained for regression tests; live spear charges use NMS. */
    static double spearChargeDamage(
        double baseDamage,
        double relativeSpeed,
        double velocityMultiplier
    ) {
        return Math.max(
            baseDamage,
            Math.floor(
                Math.max(0.0D, relativeSpeed) *
                    Math.max(0.0D, velocityMultiplier)
            )
        );
    }

    static double spearChargeDamage(
        double relativeSpeed,
        double velocityMultiplier
    ) {
        if (relativeSpeed < SPEAR_DAMAGE_MIN_RELATIVE_SPEED) {
            return 0.0D;
        }

        return Math.floor(
            Math.max(0.0D, relativeSpeed) *
                Math.max(0.0D, velocityMultiplier)
        );
    }

    private double compensateForBreach(
        double desiredDamage,
        Player target,
        int breachLevel
    ) {
        AttributeInstance armorAttr =
            target.getAttribute(Attribute.GENERIC_ARMOR);

        AttributeInstance toughnessAttr =
            target.getAttribute(
                Attribute.GENERIC_ARMOR_TOUGHNESS
            );

        double armor =
            armorAttr == null
                ? 0.0D
                : Math.max(0.0D, armorAttr.getValue());

        double toughness =
            toughnessAttr == null
                ? 0.0D
                : Math.max(0.0D, toughnessAttr.getValue());

        if (armor <= 0.0D || breachLevel <= 0) {
            return desiredDamage;
        }

        double breachedArmor =
            armor * Math.max(
                0.0D,
                1.0D - 0.15D * breachLevel
            );

        double desiredAfterArmor =
            vanillaArmorReduced(
                desiredDamage,
                breachedArmor,
                toughness
            );

        double low = desiredDamage;
        double high =
            Math.max(
                desiredDamage * 2.0D,
                desiredDamage + 10.0D
            );

        for (
            int i = 0;
            i < 24 &&
                vanillaArmorReduced(high, armor, toughness)
                    < desiredAfterArmor;
            i++
        ) {
            high *= 1.5D;
        }

        for (int i = 0; i < 48; i++) {
            double mid = (low + high) * 0.5D;

            if (
                vanillaArmorReduced(
                    mid,
                    armor,
                    toughness
                ) >= desiredAfterArmor
            ) {
                high = mid;
            } else {
                low = mid;
            }
        }

        return high;
    }

    private double vanillaArmorReduced(
        double damage, double armor, double toughness
    ) {
        double reduction =
            Math.min(
                20.0D,
                Math.max(
                    armor / 5.0D,
                    armor -
                        damage /
                            (2.0D + toughness / 4.0D)
                )
            );

        return damage *
            (1.0D - reduction / 25.0D);
    }

    private double velocityForHeight(double targetHeight) {
        double low = 0.42D;
        double high = 2.5D;

        for (int i = 0; i < 36; i++) {
            double mid = (low + high) * 0.5D;

            if (simulatedHeight(mid) < targetHeight) {
                low = mid;
            } else {
                high = mid;
            }
        }

        return high;
    }

    private double simulatedHeight(double initialVelocity) {
        double y = 0.0D;
        double velocity = initialVelocity;
        double maxY = 0.0D;

        for (int tick = 0; tick < 200; tick++) {
            y += velocity;
            maxY = Math.max(maxY, y);

            velocity =
                (velocity - 0.08D) * 0.98D;

            if (
                tick > 3 &&
                velocity < 0.0D &&
                y < maxY - 0.25D
            ) {
                break;
            }
        }

        return maxY;
    }

    private static double randomSigned(double magnitude) {
        double safeMagnitude = Math.max(0.0D, magnitude);
        return safeMagnitude == 0.0D
            ? 0.0D
            : ThreadLocalRandom.current().nextDouble(-safeMagnitude, safeMagnitude);
    }

    private boolean isElytraProfile(BotTrait trait) {
        return trait.getWeaponProfile().endsWith("_elytra");
    }

    private boolean isSpear(ItemStack item) {
        return item != null &&
            item.getType().name().endsWith("_SPEAR");
    }

    private boolean maceTargetChanged(
        BotTrait trait, Player target
    ) {
        return trait.modernWeaponTarget != null &&
            !trait.modernWeaponTarget.equals(
                target.getUniqueId()
            );
    }

    private boolean spearTargetChanged(
        BotTrait trait, Player target
    ) {
        return trait.modernWeaponTarget != null &&
            !trait.modernWeaponTarget.equals(
                target.getUniqueId()
            );
    }

    private void reset(BotTrait trait) {
        if (trait == null) {
            return;
        }

        trait.modernWeaponCooldownUntil = 0L;
        trait.maceLaunchStartedAt = 0L;
        trait.maceLaunchActive = false;
        trait.maceWindChargePending = false;
        trait.maceAttemptedThisFall = false;
        trait.maceMissThisAttack = false;
        trait.maceMissOffsetDegrees = 0.0D;
        trait.maceLaunchOriginY = 0.0D;
        trait.macePeakY = 0.0D;
        trait.spearChargeStartedAt = 0L;
        trait.spearLastHitAt = 0L;
        trait.modernWeaponTarget = null;
    }
}
