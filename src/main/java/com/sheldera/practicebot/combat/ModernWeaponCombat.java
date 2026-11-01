package com.sheldera.practicebot.combat;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.TemplateDifficultyTuning;
import com.sheldera.practicebot.MaceDifficultyTuning;
import com.sheldera.practicebot.PracticeBotPlugin;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
    private static final double MACE_MIN_SMASH_FALL = 1.5D;
    private static final double MACE_MAX_REACH = 3.0D;

    private static final double SPEAR_MIN_REACH = 2.0D;
    private static final double SPEAR_MAX_REACH = 4.5D;
    private static final double SPEAR_HITBOX_MARGIN = 0.25D;

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
                ? MACE_MAX_REACH + SPEAR_HITBOX_MARGIN
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
            if (bot != null && trait != null) restoreMaceWeapon(bot, trait);
            reset(trait);
            return;
        }

        restoreMaceWeaponIfDue(bot, trait, now);
        if (trait.maceWindChargePending) {
            tickMace(bot, target, trait, now);
            return;
        }

        if ("spear_mace".equals(trait.getWeaponProfile())) {
            selectHybridWeapon(bot, target, trait);
        }

        ItemStack item = bot.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            reset(trait);
            return;
        }

        if (item.getType() == Material.MACE) {
            tickMace(bot, target, trait, now);
        } else if (isSpear(item)) {
            tickSpear(bot, target, trait, now);
        } else {
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

        if (isSpear(item) &&
            trait.spearChargeStartedAt > 0L &&
            now - trait.spearChargeStartedAt >= spearDelayMs(item)) {
            return now >= trait.modernWeaponCooldownUntil;
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
        boolean useMace = trait.maceLaunchActive || !bot.isOnGround() ||
            (distance >= 3.5D && distance <= plugin.getConfig().getDouble(
                "modern-weapons.mace.launch-max-distance", 10.0D
            ));
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
            bot.getEyeLocation(), direction, MACE_MAX_REACH + SPEAR_HITBOX_MARGIN, 0.08D,
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
        if (distance > MACE_MAX_REACH + SPEAR_HITBOX_MARGIN) return true;
        if (maceTargetChanged(trait, target)) {
            trait.maceLaunchActive = false;
            trait.maceLaunchStartedAt = 0L;
        }
        trait.modernWeaponTarget = target.getUniqueId();

        boolean falling = !bot.isOnGround() && !bot.isGliding() &&
            bot.getVelocity().getY() < -0.02D;
        double fallDistance = Math.max(0.0D, bot.getFallDistance());

        if (falling && fallDistance > MACE_MIN_SMASH_FALL &&
            !bot.hasPotionEffect(PotionEffectType.SLOW_FALLING)) {
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
            if (isShieldUp(target)) {
                shieldBlock(target);
                return true;
            }
            performMaceSmash(bot, target, trait, fallDistance, now);
            return true;
        }

        if (!bot.isOnGround() || now < trait.modernWeaponCooldownUntil ||
            now - trait.lastAttackTime < attackCooldownMs(bot)) return true;
        if (!maceAimHitsTarget(bot, target)) return true;
        if (isShieldUp(target)) {
            shieldBlock(target);
            trait.lastAttackTime = now;
            trait.modernWeaponCooldownUntil = now + attackCooldownMs(bot);
            return true;
        }
        performMaceHit(bot, target, trait, now);
        return true;
    }

    /**
     * Ask the actual server-side player entity to attack. This is important for
     * Mace combat: vanilla's attack pipeline owns smash damage, impact particles,
     * sounds, knockback, enchantments, durability, and Wind Burst behavior.
     *
     * The project compiles against the older public Paper API, so resolve the
     * CraftBukkit handle and its native attack(Entity) method reflectively.
     */
    private boolean invokeNativeMeleeAttack(Player bot, LivingEntity target) {
        try {
            Object botHandle = bot.getClass().getMethod("getHandle").invoke(bot);
            Object targetHandle = target.getClass().getMethod("getHandle").invoke(target);
            java.lang.reflect.Method attack = null;

            for (java.lang.reflect.Method method : botHandle.getClass().getMethods()) {
                if (!"attack".equals(method.getName()) ||
                    method.getParameterCount() != 1 ||
                    !method.getParameterTypes()[0].isInstance(targetHandle)) {
                    continue;
                }
                attack = method;
                break;
            }

            if (attack == null) {
                plugin.debugLog(() -> "Native Mace attack unavailable: no compatible server attack(Entity) method");
                return false;
            }

            // Send the visible swing animation, then let vanilla decide whether
            // this is a normal hit or a full falling Mace smash.
            bot.swingMainHand();
            attack.invoke(botHandle, targetHandle);
            return true;
        } catch (ReflectiveOperationException | RuntimeException ex) {
            plugin.debugLog(() -> "Native Mace attack failed: " + ex.getClass().getSimpleName() +
                ": " + ex.getMessage());
            return false;
        }
    }

    private void performMaceHit(
        Player bot, Player target, BotTrait trait, long now
    ) {
        if (!invokeNativeMeleeAttack(bot, target)) {
            // Do not fake a Mace hit if vanilla's attack pipeline is unavailable.
            // A simulated damage call would lose the actual weapon mechanics again.
            trait.lastAttackTime = now;
            trait.modernWeaponCooldownUntil = now + 350L;
            trait.lastTactic = "mace-native-attack-unavailable";
            applyHumanAttackDelay(trait, now);
            return;
        }

        plugin.addCombatTag(target);
        trait.lastAttackTime = now;
        trait.modernWeaponCooldownUntil = now + attackCooldownMs(bot);
        applyHumanAttackDelay(trait, now);
    }

    private void performMaceSmash(
        Player bot,
        Player target,
        BotTrait trait,
        double fallDistance,
        long now
    ) {
        ItemStack mace = bot.getInventory().getItemInMainHand();
        int windBurst = enchantLevel(mace, "wind_burst");

        if (!invokeNativeMeleeAttack(bot, target)) {
            // Never substitute custom damage/particles for a vanilla Mace smash.
            trait.lastAttackTime = now;
            trait.modernWeaponCooldownUntil = now + 350L;
            trait.lastTactic = "mace-native-smash-unavailable";
            trait.maceLaunchActive = false;
            applyHumanAttackDelay(trait, now);
            return;
        }

        plugin.addCombatTag(target);

        // Vanilla owns smash damage, impact particles/sounds, area knockback,
        // fall-distance reset, enchantments, and the Wind Burst launch.
        if (windBurst > 0) {
            trait.maceLaunchActive = true;
            trait.maceWindChargePending = false;
            trait.maceAttemptedThisFall = false;
            trait.maceMissThisAttack = ThreadLocalRandom.current().nextDouble(100.0D) <
                MaceDifficultyTuning.getDouble(plugin, trait, "miss-chance-percent", 10.0D);
            double missOffset = Math.max(0.0D,
                MaceDifficultyTuning.getDouble(plugin, trait, "miss-offset-degrees", 6.0D));
            trait.maceMissOffsetDegrees = trait.maceMissThisAttack && missOffset > 0.0D
                ? ThreadLocalRandom.current().nextDouble(-missOffset, missOffset) : 0.0D;
            trait.maceLaunchStartedAt = now;
            trait.maceLaunchOriginY = bot.getLocation().getY();
            trait.macePeakY = bot.getLocation().getY();
            trait.lastTactic = "mace-wind-burst";
        } else {
            trait.maceLaunchActive = false;
        }

        trait.lastAttackTime = now;
        trait.modernWeaponCooldownUntil = now + 300L;
        trait.maceLaunchStartedAt = now;
        applyHumanAttackDelay(trait, now);
    }

    private void tickSpear(
        Player bot, Player target, BotTrait trait, long now
    ) {
        if (spearTargetChanged(trait, target)) {
            trait.spearChargeStartedAt = 0L;
            trait.spearLastHitAt = 0L;
        }

        trait.modernWeaponTarget = target.getUniqueId();

        double distance = bot.getLocation().distance(target.getLocation());

        ItemStack spear = bot.getInventory().getItemInMainHand();
        PaperItemDataBridge.AttackRangeData range =
            PaperItemDataBridge.attackRange(spear);

        double minReach = range == null
            ? SPEAR_MIN_REACH
            : range.minReach();
        double maxReach = range == null
            ? SPEAR_MAX_REACH
            : range.maxReach();

        if (distance < minReach + 0.05D) {
            if (!bot.isGliding()) {
                Vector away = bot.getLocation().toVector()
                    .subtract(target.getLocation().toVector());
                away.setY(0.0D);

                if (away.lengthSquared() > 1.0E-5D) {
                    away.normalize();

                    Vector velocity = bot.getVelocity();
                    velocity.setX(velocity.getX() * 0.55D + away.getX() * 0.12D);
                    velocity.setZ(velocity.getZ() * 0.55D + away.getZ() * 0.12D);
                    bot.setVelocity(velocity);
                }
            }

            trait.spearChargeStartedAt = 0L;
            return;
        }

        boolean elytraProfile = isElytraProfile(trait);

        if (!plugin.getConfig().getBoolean(
            "modern-weapons.spear.charge-enabled", true
        )) {
            return;
        }

        if (distance < minReach + 0.1D ||
            distance > maxReach) {
            return;
        }

        // Ground charge starts normally; Elytra Spear charges are initiated
        // by the aerial tactic engine when it commits to the dive.
        if (!bot.isOnGround() && !bot.isGliding()) {
            return;
        }

        if (trait.spearChargeStartedAt == 0L) {
            trait.spearChargeStartedAt = now;
            trait.lastTactic = elytraProfile && bot.isGliding()
                ? "elytra-spear-charge"
                : "spear-charge";
        }

        bot.setSprinting(!bot.isGliding());
    }

    private boolean trySpear(
        Player bot, Player target, BotTrait trait, long now
    ) {
        double distance = bot.getLocation().distance(target.getLocation());

        ItemStack spear = bot.getInventory().getItemInMainHand();
        PaperItemDataBridge.AttackRangeData range =
            PaperItemDataBridge.attackRange(spear);

        double minReach = range == null
            ? SPEAR_MIN_REACH
            : range.minReach();

        double maxReach = range == null
            ? SPEAR_MAX_REACH
            : range.maxReach();

        double margin = range == null
            ? SPEAR_HITBOX_MARGIN
            : range.hitboxMargin();

        if (distance < minReach - margin ||
            distance > maxReach + margin) {
            return true;
        }

        if (spearTargetChanged(trait, target)) {
            trait.spearChargeStartedAt = 0L;
            trait.spearLastHitAt = 0L;
        }

        trait.modernWeaponTarget = target.getUniqueId();

        boolean hasCharge = trait.spearChargeStartedAt > 0L;
        long elapsedMs = hasCharge ? now - trait.spearChargeStartedAt : 0L;

        if (hasCharge && elapsedMs >= spearDelayMs(spear)) {
            long elapsedTicks = elapsedMs / 50L;
            double relativeSpeed = relativeSpeedAlongView(bot, target);

            long damageMaxDuration = kineticDamageMaxDuration(spear);
            double damageMinSpeed = kineticDamageMinRelativeSpeed(spear);

            if (elapsedTicks <= damageMaxDuration &&
                relativeSpeed >= damageMinSpeed &&
                now >= trait.modernWeaponCooldownUntil &&
                !isShieldUp(target)) {
                performSpearCharge(
                    bot, target, trait, now, relativeSpeed, elapsedTicks
                );
                return true;
            }

            if (distance > 3.15D) {
                return true;
            }

            trait.spearChargeStartedAt = 0L;
        }

        if (bot.isOnGround() &&
            now >= trait.modernWeaponCooldownUntil &&
            now - trait.lastAttackTime >= attackCooldownMs(bot)) {
            if (isShieldUp(target)) {
                shieldBlock(target);
                trait.lastAttackTime = now;
                trait.modernWeaponCooldownUntil = now + attackCooldownMs(bot);
                applyHumanAttackDelay(trait, now);
            } else {
                performSpearJab(bot, target, trait, now);
            }
        }

        return true;
    }

    private void performSpearJab(
        Player bot, Player target, BotTrait trait, long now
    ) {
        bot.setSprinting(false);
        bot.swingMainHand();

        double damage = attributeDamageBonus(bot)
            + enchantBonus(bot.getInventory().getItemInMainHand(), "sharpness")
            + attributeSwapBonus(bot, trait);

        if (damage <= 0.0D) {
            damage = spearBaseDamage(bot);
        }

        target.damage(damage, bot);
        applyKnockback(target, bot, 0.45D, 0.28D);

        // Lunge is a Jab-only movement enchantment.
        if (!bot.isGliding()) {
            applyLunge(bot);
        }

        plugin.addCombatTag(target);

        trait.lastAttackTime = now;
        trait.modernWeaponCooldownUntil = now + spearJabCooldownMs(bot);
        trait.spearChargeStartedAt = 0L;
        applyHumanAttackDelay(trait, now);

        bot.getWorld().playSound(
            target.getLocation(),
            Sound.ENTITY_PLAYER_ATTACK_STRONG,
            0.85F,
            1.2F
        );
    }

    private void performSpearCharge(
        Player bot,
        Player target,
        BotTrait trait,
        long now,
        double relativeSpeed,
        long elapsedTicks
    ) {
        bot.swingMainHand();

        ItemStack spear = bot.getInventory().getItemInMainHand();

        PaperItemDataBridge.KineticData kinetic =
            PaperItemDataBridge.kineticWeapon(spear);

        double multiplier = kinetic == null
            ? plugin.getConfig().getDouble(
                "modern-weapons.spear.charge-velocity-multiplier",
                SPEAR_DAMAGE_MULTIPLIER
            )
            : kinetic.damageMultiplier();

        // relativeSpeed is already projected onto the attacker's view axis.
        // Do not apply a second view-angle multiplier here.
        double kineticDamage = Math.floor(
            Math.max(0.0D, relativeSpeed) * multiplier
        );

        // Java's kinetic_weapon calculation adds attribute/enchantment damage
        // after the floor(relative_speed * velocity_multiplier) term.
        double damage = kineticDamage
            + attributeDamageBonus(bot)
            + enchantBonus(spear, "sharpness")
            + attributeSwapBonus(bot, trait);

        if (damage <= 0.0D) {
            damage = spearBaseDamage(bot);
        }

        target.damage(damage, bot);

        long knockbackMaxDuration =
            kineticKnockbackMaxDuration(spear);

        double knockbackMinSpeed =
            kineticKnockbackMinSpeed(spear);

        long dismountMaxDuration =
            kineticDismountMaxDuration(spear);

        double dismountMinSpeed =
            kineticDismountMinSpeed(spear);

        boolean withinKnockbackWindow =
            elapsedTicks <= knockbackMaxDuration &&
            relativeSpeed >= knockbackMinSpeed;

        boolean withinDismountWindow =
            elapsedTicks <= dismountMaxDuration &&
            relativeSpeed >= dismountMinSpeed;

        if (withinKnockbackWindow) {
            applyKnockback(
                target,
                bot,
                Math.min(0.9D, 0.38D + relativeSpeed * 0.045D),
                0.28D
            );
        }

        if (withinDismountWindow) {
            try {
                target.leaveVehicle();
            } catch (Throwable ignored) {
            }
        }

        plugin.addCombatTag(target);

        trait.spearLastHitAt = now;
        trait.lastAttackTime = now;

        long contactCooldown = spearContactCooldownMs(spear);

        trait.modernWeaponCooldownUntil =
            Math.max(now + contactCooldown, now + 500L);

        applyHumanAttackDelay(trait, now);

        bot.getWorld().playSound(
            target.getLocation(),
            Sound.ENTITY_PLAYER_ATTACK_STRONG,
            0.9F,
            1.0F
        );

        if (withinKnockbackWindow) {
            trait.lastTactic = "spear-kinetic-charge";
        } else {
            trait.lastTactic = "spear-kinetic-tired";
        }
    }

    private long spearJabCooldownMs(Player bot) {
        String name = bot.getInventory().getItemInMainHand().getType().name();

        long ticks;
        // These timings mirror the published Mojang spear component data model.
        // The server version remains the source of truth for the actual item.
        if (name.startsWith("WOODEN_")) {
            ticks = 15L;
        } else if (name.startsWith("STONE_")) {
            ticks = 14L;
        } else if (name.startsWith("COPPER_")) {
            ticks = 13L;
        } else if (name.startsWith("IRON_")) {
            ticks = 12L;
        } else if (name.startsWith("GOLDEN_")) {
            ticks = 14L;
        } else if (name.startsWith("DIAMOND_")) {
            ticks = 10L;
        } else {
            ticks = 8L;
        }

        return Math.max(50L, ticks * 50L);
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
                name.endsWith("_AXE")) &&
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

    private double relativeSpeedAlongView(
        Player attacker, Player target
    ) {
        Vector look =
            attacker.getEyeLocation().getDirection().normalize();

        Vector relative =
            attacker.getVelocity().clone().subtract(target.getVelocity());

        return Math.max(
            0.0D,
            relative.dot(look) * 20.0D
        );
    }

    private void applyLunge(Player bot) {
        int lunge = enchantLevel(
            bot.getInventory().getItemInMainHand(), "lunge"
        );

        if (lunge <= 0 ||
            !bot.isOnGround() ||
            bot.isInWater() ||
            bot.getFoodLevel() < 6) {
            return;
        }

        Vector direction =
            bot.getLocation().getDirection().clone();

        direction.setY(0.0D);

        if (direction.lengthSquared() < 1.0E-5D) {
            return;
        }

        direction.normalize();

        double strength =
            0.42D + Math.min(0.36D, lunge * 0.12D);

        Vector velocity = bot.getVelocity();

        velocity.setX(direction.getX() * strength);
        velocity.setZ(direction.getZ() * strength);

        bot.setVelocity(velocity);
        bot.setFoodLevel(
            Math.max(0, bot.getFoodLevel() - lunge)
        );
    }

    private void applyMaceAreaKnockback(
        Player target, Player attacker, double fallDistance
    ) {
        double strength =
            fallDistance > 5.0D ? 1.0D : 0.55D;

        for (Entity entity :
            target.getWorld().getNearbyEntities(
                target.getLocation(),
                3.5D,
                2.0D,
                3.5D
            )) {
            if (!(entity instanceof LivingEntity living) ||
                entity.equals(target) ||
                entity.equals(attacker) ||
                living.isDead()) {
                continue;
            }

            Vector direction =
                living.getLocation().toVector()
                    .subtract(target.getLocation().toVector());

            direction.setY(0.0D);

            if (direction.lengthSquared() < 1.0E-5D) {
                continue;
            }

            direction.normalize();

            Vector current = living.getVelocity();

            living.setVelocity(new Vector(
                current.getX() * 0.5D +
                    direction.getX() * strength * 0.75D,
                Math.max(current.getY(), 0.22D),
                current.getZ() * 0.5D +
                    direction.getZ() * strength * 0.75D
            ));
        }
    }

    private void applyKnockback(
        LivingEntity target,
        Player attacker,
        double horizontal,
        double vertical
    ) {
        Vector direction =
            target.getLocation().toVector()
                .subtract(attacker.getLocation().toVector());

        direction.setY(0.0D);

        if (direction.lengthSquared() < 1.0E-5D) {
            direction =
                attacker.getLocation().getDirection().clone();

            direction.setY(0.0D);
        }

        if (direction.lengthSquared() < 1.0E-5D) {
            return;
        }

        direction.normalize();

        Vector current = target.getVelocity();

        target.setVelocity(new Vector(
            current.getX() * 0.5D +
                direction.getX() * horizontal,
            Math.max(
                current.getY() * 0.5D,
                vertical
            ),
            current.getZ() * 0.5D +
                direction.getZ() * horizontal
        ));
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

    private void shieldBlock(Player target) {
        target.getWorld().playSound(
            target.getLocation(),
            Sound.ITEM_SHIELD_BLOCK,
            0.8F,
            1.0F
        );
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
