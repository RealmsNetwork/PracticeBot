package com.sheldera.practicebot.combat;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Map;

public final class ModernWeaponCombat {
    private static final double MACE_MIN_SMASH_FALL = 1.5D;
    private static final double MACE_MAX_REACH = 3.0D;
    private static final double SPEAR_MIN_REACH = 2.0D;
    private static final double SPEAR_MAX_REACH = 4.5D;
    private static final long DEFAULT_SPEAR_CHARGE_DELAY_MS = 400L;

    private final PracticeBotPlugin plugin;

    public ModernWeaponCombat(PracticeBotPlugin plugin) {
        this.plugin = plugin;
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
            return MACE_MAX_REACH;
        }

        if (isSpear(item)) {
            return Math.max(configuredReach, SPEAR_MAX_REACH);
        }

        return configuredReach;
    }

    public void tick(Player bot, Player target, BotTrait trait, long now) {
        if (bot == null || target == null || trait == null ||
            !target.isOnline() || target.isDead() ||
            !bot.getWorld().equals(target.getWorld())) {
            reset(trait);
            return;
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

        if (item.getType() == Material.MACE &&
            trait.maceLaunchActive &&
            !bot.isOnGround()) {
            return true;
        }

        if (isSpear(item) &&
            trait.spearChargeStartedAt > 0L &&
            now - trait.spearChargeStartedAt >= spearChargeDelayMs()) {
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

        if (item.getType() == Material.MACE) {
            return tryMace(bot, target, trait, now);
        }

        return trySpear(bot, target, trait, now);
    }

    private void tickMace(Player bot, Player target, BotTrait trait, long now) {
        if (maceTargetChanged(trait, target)) {
            trait.maceLaunchActive = false;
            trait.maceLaunchStartedAt = 0L;
        }

        trait.modernWeaponTarget = target.getUniqueId();

        if (!bot.isOnGround()) {
            return;
        }

        double distance = bot.getLocation().distance(target.getLocation());
        if (distance < 2.5D || distance > plugin.getConfig().getDouble("modern-weapons.mace.launch-max-distance", 4.5D)) {
            return;
        }

        if (now < trait.modernWeaponCooldownUntil ||
            now - trait.lastAttackTime < attackCooldownMs(bot) ||
            bot.hasPotionEffect(PotionEffectType.SLOW_FALLING)) {
            return;
        }

        double minDistance = plugin.getConfig().getDouble("modern-weapons.mace.launch-min-distance", 2.8D);
        if (distance < minDistance) {
            return;
        }

        Vector toTarget = target.getLocation().toVector().subtract(bot.getLocation().toVector());
        toTarget.setY(0.0D);
        if (toTarget.lengthSquared() < 1.0E-5D) {
            return;
        }
        toTarget.normalize();

        boolean launchedWithWindCharge = consumeWindCharge(bot);
        double launchVelocity;

        if (launchedWithWindCharge) {
            double desiredHeight = plugin.getConfig().getDouble(
                "modern-weapons.mace.wind-charge-launch-height", 7.0D
            );
            launchVelocity = velocityForHeight(desiredHeight);
        } else {
            if (!plugin.getConfig().getBoolean(
                "modern-weapons.mace.assisted-jump-without-wind-charge", true
            )) {
                return;
            }
            launchVelocity = 0.52D;
        }

        Vector velocity = bot.getVelocity();
        velocity.setX(velocity.getX() * 0.35D + toTarget.getX() * 0.15D);
        velocity.setZ(velocity.getZ() * 0.35D + toTarget.getZ() * 0.15D);
        velocity.setY(Math.max(velocity.getY(), launchVelocity));
        bot.setVelocity(velocity);
        bot.setSprinting(true);

        trait.maceLaunchActive = true;
        trait.maceLaunchStartedAt = now;
        trait.modernWeaponCooldownUntil = now + 220L;
    }

    private boolean tryMace(Player bot, Player target, BotTrait trait, long now) {
        double distance = bot.getLocation().distance(target.getLocation());

        if (distance > MACE_MAX_REACH + 0.15D) {
            return true;
        }

        if (maceTargetChanged(trait, target)) {
            trait.maceLaunchActive = false;
            trait.maceLaunchStartedAt = 0L;
        }

        trait.modernWeaponTarget = target.getUniqueId();

        boolean falling = !bot.isOnGround() && bot.getVelocity().getY() < -0.02D;
        double fallDistance = Math.max(0.0D, bot.getFallDistance());

        if (falling && fallDistance > MACE_MIN_SMASH_FALL &&
            !bot.hasPotionEffect(PotionEffectType.SLOW_FALLING)) {
            if (isShieldUp(target)) {
                shieldBlock(target);
                return true;
            }

            performMaceSmash(bot, target, trait, fallDistance, now);
            return true;
        }

        if (!bot.isOnGround() ||
            now < trait.modernWeaponCooldownUntil ||
            now - trait.lastAttackTime < attackCooldownMs(bot)) {
            return true;
        }

        if (isShieldUp(target)) {
            shieldBlock(target);
            trait.lastAttackTime = now;
            trait.modernWeaponCooldownUntil = now + attackCooldownMs(bot);
            return true;
        }

        performMaceHit(bot, target, trait, now);
        return true;
    }

    private void performMaceHit(Player bot, Player target, BotTrait trait, long now) {
        bot.swingMainHand();

        double damage = itemAttackDamage(bot) + enchantBonus(
            bot.getInventory().getItemInMainHand(), "sharpness"
        );

        target.damage(damage, bot);
        applyKnockback(target, bot, 0.45D, 0.32D);

        plugin.addCombatTag(target);

        trait.lastAttackTime = now;
        trait.modernWeaponCooldownUntil = now + attackCooldownMs(bot);

        bot.getWorld().playSound(
            target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.9F, 0.8F
        );
    }

    private void performMaceSmash(
        Player bot, Player target, BotTrait trait, double fallDistance, long now
    ) {
        bot.swingMainHand();

        ItemStack mace = bot.getInventory().getItemInMainHand();
        int density = enchantLevel(mace, "density");
        int breach = enchantLevel(mace, "breach");

        double damage = itemAttackDamage(bot)
            + maceSmashBonus(fallDistance)
            + (0.5D * density * fallDistance);

        damage *= plugin.getConfig().getDouble("pvp.crit-multiplier", 1.5D);

        if (breach > 0) {
            damage = compensateForBreach(damage, target, breach);
        }

        target.damage(Math.max(0.0D, damage), bot);
        applyKnockback(
            target,
            bot,
            fallDistance > 5.0D ? 1.0D : 0.55D,
            Math.min(0.9D, 0.30D + fallDistance * 0.035D)
        );

        applyMaceAreaKnockback(target, bot, fallDistance);

        bot.setFallDistance(0.0F);

        bot.getWorld().spawnParticle(
            Particle.CRIT,
            target.getLocation().add(0.0D, 1.0D, 0.0D),
            12,
            0.35D, 0.35D, 0.35D, 0.1D
        );
        bot.getWorld().playSound(
            target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0F, 0.65F
        );

        plugin.addCombatTag(target);

        int windBurst = enchantLevel(mace, "wind_burst");
        if (windBurst > 0) {
            Vector velocity = bot.getVelocity();
            velocity.setY(velocityForHeight(7.0D * windBurst));
            velocity.setX(velocity.getX() * 0.35D);
            velocity.setZ(velocity.getZ() * 0.35D);
            bot.setVelocity(velocity);
            bot.setFallDistance(0.0F);
        }

        trait.lastAttackTime = now;
        trait.modernWeaponCooldownUntil = now + 300L;
        trait.maceLaunchActive = windBurst > 0;
        trait.maceLaunchStartedAt = now;
    }

    private void tickSpear(Player bot, Player target, BotTrait trait, long now) {
        if (spearTargetChanged(trait, target)) {
            trait.spearChargeStartedAt = 0L;
            trait.spearLastHitAt = 0L;
        }

        trait.modernWeaponTarget = target.getUniqueId();

        double distance = bot.getLocation().distance(target.getLocation());

        if (distance < SPEAR_MIN_REACH + 0.05D) {
            Vector away = bot.getLocation().toVector().subtract(target.getLocation().toVector());
            away.setY(0.0D);

            if (away.lengthSquared() > 1.0E-5D) {
                away.normalize();
                Vector velocity = bot.getVelocity();
                velocity.setX(velocity.getX() * 0.55D + away.getX() * 0.12D);
                velocity.setZ(velocity.getZ() * 0.55D + away.getZ() * 0.12D);
                bot.setVelocity(velocity);
            }

            trait.spearChargeStartedAt = 0L;
            return;
        }

        if (!plugin.getConfig().getBoolean("modern-weapons.spear.charge-enabled", true) ||
            !bot.isOnGround() ||
            distance < SPEAR_MIN_REACH + 0.1D ||
            distance > SPEAR_MAX_REACH) {
            return;
        }

        if (trait.spearChargeStartedAt == 0L) {
            trait.spearChargeStartedAt = now;
        }

        bot.setSprinting(true);
    }

    private boolean trySpear(Player bot, Player target, BotTrait trait, long now) {
        double distance = bot.getLocation().distance(target.getLocation());

        if (distance < SPEAR_MIN_REACH - 0.05D ||
            distance > SPEAR_MAX_REACH + 0.15D) {
            return true;
        }

        if (spearTargetChanged(trait, target)) {
            trait.spearChargeStartedAt = 0L;
            trait.spearLastHitAt = 0L;
        }

        trait.modernWeaponTarget = target.getUniqueId();

        boolean charged = trait.spearChargeStartedAt > 0L &&
            now - trait.spearChargeStartedAt >= spearChargeDelayMs();

        if (charged && now >= trait.modernWeaponCooldownUntil) {
            double relativeSpeed = relativeSpeedAlongView(bot, target);
            double minimum = plugin.getConfig().getDouble(
                "modern-weapons.spear.min-relative-speed-bps", 3.0D
            );

            if (relativeSpeed >= minimum && !isShieldUp(target)) {
                performSpearCharge(bot, target, trait, now, relativeSpeed);
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
            } else {
                performSpearJab(bot, target, trait, now);
            }
        }

        return true;
    }

    private void performSpearJab(Player bot, Player target, BotTrait trait, long now) {
        bot.setSprinting(false);
        bot.swingMainHand();

        double damage = itemAttackDamage(bot) +
            enchantBonus(bot.getInventory().getItemInMainHand(), "sharpness");

        target.damage(Math.max(0.0D, damage), bot);
        applyKnockback(target, bot, 0.45D, 0.28D);
        applyLunge(bot);

        plugin.addCombatTag(target);

        trait.lastAttackTime = now;
        trait.modernWeaponCooldownUntil = now + attackCooldownMs(bot);
        trait.spearChargeStartedAt = 0L;

        bot.getWorld().playSound(
            target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.85F, 1.2F
        );
    }

    private void performSpearCharge(
        Player bot, Player target, BotTrait trait, long now, double relativeSpeedBps
    ) {
        bot.swingMainHand();

        ItemStack spear = bot.getInventory().getItemInMainHand();
        double multiplier = plugin.getConfig().getDouble(
            "modern-weapons.spear.charge-velocity-multiplier", 1.0D
        );
        double viewFactor = viewFactor(bot, target);

        double kineticDamage = Math.floor(
            relativeSpeedBps * multiplier * Math.max(0.0D, Math.min(1.0D, viewFactor))
        );

        double damage = Math.max(0.0D, kineticDamage)
            + itemAttackDamage(bot)
            + enchantBonus(spear, "sharpness");

        boolean engaged = now - trait.spearChargeStartedAt <= 1300L;

        target.damage(damage, bot);

        if (engaged) {
            applyKnockback(
                target, bot,
                Math.min(0.9D, 0.38D + relativeSpeedBps * 0.045D),
                0.28D
            );
        }

        plugin.addCombatTag(target);

        trait.spearLastHitAt = now;
        trait.modernWeaponCooldownUntil = now + 500L;

        bot.getWorld().playSound(
            target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.9F, 1.0F
        );
    }

    private void applyLunge(Player bot) {
        int lunge = enchantLevel(bot.getInventory().getItemInMainHand(), "lunge");

        if (lunge <= 0 || !bot.isOnGround() || bot.isInWater() ||
            bot.getFoodLevel() < 6) {
            return;
        }

        Vector direction = bot.getLocation().getDirection().clone();
        direction.setY(0.0D);

        if (direction.lengthSquared() < 1.0E-5D) {
            return;
        }

        direction.normalize();

        double strength = 0.42D + Math.min(0.36D, lunge * 0.12D);
        Vector velocity = bot.getVelocity();
        velocity.setX(direction.getX() * strength);
        velocity.setZ(direction.getZ() * strength);
        bot.setVelocity(velocity);

        bot.setFoodLevel(Math.max(0, bot.getFoodLevel() - lunge));
    }

    private void applyMaceAreaKnockback(Player target, Player attacker, double fallDistance) {
        double strength = fallDistance > 5.0D ? 1.0D : 0.55D;

        for (Entity entity : target.getWorld().getNearbyEntities(
            target.getLocation(), 3.5D, 2.0D, 3.5D
        )) {
            if (!(entity instanceof LivingEntity living) ||
                entity.equals(target) ||
                entity.equals(attacker) ||
                living.isDead()) {
                continue;
            }

            Vector direction = living.getLocation().toVector()
                .subtract(target.getLocation().toVector());
            direction.setY(0.0D);

            if (direction.lengthSquared() < 1.0E-5D) {
                continue;
            }

            direction.normalize();
            Vector current = living.getVelocity();

            living.setVelocity(new Vector(
                current.getX() * 0.5D + direction.getX() * strength * 0.75D,
                Math.max(current.getY(), 0.22D),
                current.getZ() * 0.5D + direction.getZ() * strength * 0.75D
            ));
        }
    }

    private void applyKnockback(
        LivingEntity target, Player attacker, double horizontal, double vertical
    ) {
        Vector direction = target.getLocation().toVector()
            .subtract(attacker.getLocation().toVector());
        direction.setY(0.0D);

        if (direction.lengthSquared() < 1.0E-5D) {
            direction = attacker.getLocation().getDirection().clone();
            direction.setY(0.0D);
        }

        direction.normalize();

        Vector current = target.getVelocity();
        target.setVelocity(new Vector(
            current.getX() * 0.5D + direction.getX() * horizontal,
            Math.max(current.getY() * 0.5D, vertical),
            current.getZ() * 0.5D + direction.getZ() * horizontal
        ));
    }

    private boolean consumeWindCharge(Player bot) {
        if (!plugin.getConfig().getBoolean("modern-weapons.mace.wind-charge-enabled", true)) {
            return false;
        }

        PlayerInventory inventory = bot.getInventory();

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);

            if (item != null &&
                item.getType() == Material.WIND_CHARGE &&
                item.getAmount() > 0) {
                item.setAmount(item.getAmount() - 1);

                if (item.getAmount() <= 0) {
                    inventory.setItem(slot, null);
                }

                return true;
            }
        }

        return false;
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
            target.getLocation(), Sound.ITEM_SHIELD_BLOCK, 0.8F, 1.0F
        );
    }

    private double itemAttackDamage(Player bot) {
        AttributeInstance attribute = bot.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        return attribute == null ? 1.0D : Math.max(1.0D, attribute.getValue());
    }

    private long attackCooldownMs(Player bot) {
        AttributeInstance attribute = bot.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        double speed = attribute == null ? 4.0D : attribute.getValue();

        if (speed < 0.01D) {
            speed = 4.0D;
        }

        return Math.max(50L, Math.round(1000.0D / speed));
    }

    private double relativeSpeedAlongView(Player attacker, Player target) {
        Vector look = attacker.getEyeLocation().getDirection().normalize();
        Vector relative = attacker.getVelocity().clone().subtract(target.getVelocity());
        return Math.max(0.0D, relative.dot(look) * 20.0D);
    }

    private double viewFactor(Player attacker, Player target) {
        Vector look = attacker.getEyeLocation().getDirection().clone();
        Vector delta = target.getLocation().toVector().subtract(attacker.getLocation().toVector());

        if (look.lengthSquared() < 1.0E-5D || delta.lengthSquared() < 1.0E-5D) {
            return 1.0D;
        }

        look.normalize();
        delta.normalize();

        return Math.max(0.0D, Math.min(1.0D, look.dot(delta)));
    }

    private int enchantLevel(ItemStack item, String key) {
        if (item == null || !item.hasItemMeta()) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return 0;
        }

        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            String enchantKey = entry.getKey().getKey().getKey();
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
            + Math.min(Math.max(fallDistance - 3.0D, 0.0D), 5.0D) * 2.0D
            + Math.max(fallDistance - 8.0D, 0.0D);
    }

    static double spearChargeDamage(
        double baseDamage, double relativeSpeedBps, double multiplier, double viewFactor
    ) {
        double v = Math.max(0.0D, Math.min(1.0D, viewFactor));
        double kinetic = Math.floor(relativeSpeedBps * multiplier * v);
        return Math.max(baseDamage, kinetic);
    }

    private double compensateForBreach(
        double desiredDamage, Player target, int breachLevel
    ) {
        AttributeInstance armorAttr = target.getAttribute(Attribute.GENERIC_ARMOR);
        AttributeInstance toughnessAttr = target.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);

        double armor = armorAttr == null ? 0.0D : Math.max(0.0D, armorAttr.getValue());
        double toughness = toughnessAttr == null ? 0.0D : Math.max(0.0D, toughnessAttr.getValue());

        if (armor <= 0.0D || breachLevel <= 0) {
            return desiredDamage;
        }

        double breachedArmor = armor * Math.max(0.0D, 1.0D - 0.15D * breachLevel);
        double desiredAfterArmor = vanillaArmorReduced(desiredDamage, breachedArmor, toughness);

        double low = desiredDamage;
        double high = Math.max(desiredDamage * 2.0D, desiredDamage + 10.0D);

        for (int i = 0; i < 24 && vanillaArmorReduced(high, armor, toughness) < desiredAfterArmor; i++) {
            high *= 1.5D;
        }

        for (int i = 0; i < 48; i++) {
            double mid = (low + high) * 0.5D;
            if (vanillaArmorReduced(mid, armor, toughness) >= desiredAfterArmor) {
                high = mid;
            } else {
                low = mid;
            }
        }

        return high;
    }

    private double vanillaArmorReduced(double damage, double armor, double toughness) {
        double reduction = Math.min(20.0D,
            Math.max(armor / 5.0D, armor - damage / (2.0D + toughness / 4.0D))
        );
        return damage * (1.0D - reduction / 25.0D);
    }

    private long spearChargeDelayMs() {
        return Math.max(
            0L,
            plugin.getConfig().getLong(
                "modern-weapons.spear.charge-delay-ms",
                DEFAULT_SPEAR_CHARGE_DELAY_MS
            )
        );
    }

    private boolean isSpear(ItemStack item) {
        return item != null && item.getType().name().endsWith("_SPEAR");
    }

    private boolean maceTargetChanged(BotTrait trait, Player target) {
        return trait.modernWeaponTarget != null &&
            !trait.modernWeaponTarget.equals(target.getUniqueId());
    }

    private boolean spearTargetChanged(BotTrait trait, Player target) {
        return trait.modernWeaponTarget != null &&
            !trait.modernWeaponTarget.equals(target.getUniqueId());
    }

    private void reset(BotTrait trait) {
        if (trait == null) {
            return;
        }

        trait.modernWeaponCooldownUntil = 0L;
        trait.maceLaunchStartedAt = 0L;
        trait.maceLaunchActive = false;
        trait.spearChargeStartedAt = 0L;
        trait.spearLastHitAt = 0L;
        trait.modernWeaponTarget = null;
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
            velocity = (velocity - 0.08D) * 0.98D;

            if (tick > 3 && velocity < 0.0D && y < maxY - 0.25D) {
                break;
            }
        }

        return maxY;
    }
}
