package com.sheldera.practicebot.combat;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.PotionEffectType;
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
   private static final double MACE_MIN_SMASH_FALL = 1.5;
   private static final double MACE_MAX_REACH = 3.0;
   private static final double SPEAR_MIN_REACH = 2.0;
   private static final double SPEAR_MAX_REACH = 4.5;
   private static final long SPEAR_CONTACT_COOLDOWN_MS = 500L;

   private final PracticeBotPlugin plugin;

   public ModernWeaponCombat(PracticeBotPlugin plugin) {
      this.plugin = plugin;
   }

   public boolean isModernWeapon(Player player) {
      return player != null && isModernWeapon(player.getInventory().getItemInMainHand());
   }

   public boolean isModernWeapon(ItemStack item) {
      if (item == null || item.getType().isAir()) return false;
      String name = item.getType().name();
      return "MACE".equals(name) || name.endsWith("_SPEAR");
   }

   public double attackReach(Player player, BotTrait trait, double configuredReach) {
      ItemStack item = player == null ? null : player.getInventory().getItemInMainHand();
      if (item == null || item.getType().isAir()) return configuredReach;
      if (item.getType() == Material.MACE) return MACE_MAX_REACH;
      if (isSpear(item)) return Math.max(configuredReach, SPEAR_MAX_REACH);
      return configuredReach;
   }

   public void prepare(Player bot, Player target, BotTrait trait, long now) {
      if (bot == null || target == null || trait == null || !target.isOnline() || target.isDead()) {
         resetModernState(trait);
         return;
      }

      ItemStack item = bot.getInventory().getItemInMainHand();
      if (item == null || item.getType().isAir()) {
         resetModernState(trait);
         return;
      }

      if (item.getType() == Material.MACE) prepareMace(bot, target, trait, now);
      else if (isSpear(item)) prepareSpear(bot, target, trait, now);
      else resetModernState(trait);
   }

   public boolean canAttemptAttack(Player bot, Player target, BotTrait trait, long now) {
      ItemStack item = bot.getInventory().getItemInMainHand();
      if (!isModernWeapon(item)) return false;
      if (bot.getGameMode() == GameMode.SPECTATOR || !target.isOnline() || target.isDead()) return false;
      if (!bot.getWorld().equals(target.getWorld())) return false;

      if (item.getType() == Material.MACE && trait.maceLaunchActive && !bot.isOnGround()) return true;

      if (isSpear(item) && trait.spearChargeStartedAt > 0L &&
          now - trait.spearChargeStartedAt >= spearChargeDelayMs()) {
         return now >= trait.modernWeaponCooldownUntil;
      }

      return now - trait.lastAttackTime >= attackCooldownMs(bot) &&
         now >= trait.modernWeaponCooldownUntil;
   }

   public boolean tryAttack(Player bot, Player target, BotTrait trait, long now) {
      if (!bot.isValid() || target == null || !target.isOnline() || target.isDead()) return true;

      ItemStack item = bot.getInventory().getItemInMainHand();
      if (!isModernWeapon(item)) return false;
      if (bot.getGameMode() == GameMode.SPECTATOR || target.getGameMode() == GameMode.SPECTATOR) return true;
      if (!bot.getWorld().equals(target.getWorld())) return true;

      if (item.getType() == Material.MACE) return tryMace(bot, target, trait, now);
      return trySpear(bot, target, trait, now);
   }

   private boolean tryMace(Player bot, Player target, BotTrait trait, long now) {
      double distance = bot.getLocation().distance(target.getLocation());
      if (distance > MACE_MAX_REACH + 0.15) return true;

      if (maceTargetChanged(trait, target)) {
         trait.maceLaunchActive = false;
         trait.maceLaunchStartedAt = 0L;
      }
      trait.modernWeaponTarget = target.getUniqueId();

      boolean falling = !bot.isOnGround() && bot.getVelocity().getY() < -0.02;
      double fallDistance = Math.max(0.0, bot.getFallDistance());
      boolean slowFalling = bot.hasPotionEffect(PotionEffectType.SLOW_FALLING);

      if (falling && fallDistance > MACE_MIN_SMASH_FALL && !slowFalling) {
         if (isShieldUp(target)) {
            playShieldBlock(target);
            return true;
         }
         performMaceSmash(bot, target, trait, fallDistance, now);
         return true;
      }

      if (trait.maceLaunchActive && !bot.isOnGround()) return true;
      if (!bot.isOnGround()) return true;
      if (now < trait.modernWeaponCooldownUntil ||
          now - trait.lastAttackTime < attackCooldownMs(bot)) return true;

      if (isShieldUp(target)) {
         playShieldBlock(target);
         trait.lastAttackTime = now;
         trait.modernWeaponCooldownUntil = now + attackCooldownMs(bot);
         return true;
      }

      performMaceGroundHit(bot, target, trait, now);
      return true;
   }

   private boolean trySpear(Player bot, Player target, BotTrait trait, long now) {
      double distance = bot.getLocation().distance(target.getLocation());
      if (distance < SPEAR_MIN_REACH - 0.05 || distance > SPEAR_MAX_REACH + 0.15) return true;

      if (spearTargetChanged(trait, target)) {
         trait.spearChargeStartedAt = 0L;
         trait.spearLastHitAt = 0L;
      }
      trait.modernWeaponTarget = target.getUniqueId();

      boolean chargeReady = trait.spearChargeStartedAt > 0L &&
         now - trait.spearChargeStartedAt >= spearChargeDelayMs();

      if (chargeReady && now >= trait.modernWeaponCooldownUntil) {
         double relativeSpeed = relativeSpeedAlongView(bot, target);
         double minSpeed = config().getDouble("modern-weapons.spear.min-relative-speed-bps", 3.0);

         if (relativeSpeed >= minSpeed && !isShieldUp(target)) {
            performSpearCharge(bot, target, trait, now, relativeSpeed);
            return true;
         }

         if (distance > 3.15) return true;
         trait.spearChargeStartedAt = 0L;
      }

      if (bot.isOnGround() && now >= trait.modernWeaponCooldownUntil &&
          now - trait.lastAttackTime >= attackCooldownMs(bot)) {
         if (!isShieldUp(target)) performSpearJab(bot, target, trait, now);
         else {
            playShieldBlock(target);
            trait.lastAttackTime = now;
            trait.modernWeaponCooldownUntil = now + attackCooldownMs(bot);
         }
      }
      return true;
   }

   private void prepareMace(Player bot, Player target, BotTrait trait, long now) {
      if (maceTargetChanged(trait, target)) {
         trait.maceLaunchActive = false;
         trait.maceLaunchStartedAt = 0L;
      }
      trait.modernWeaponTarget = target.getUniqueId();

      if (!bot.isOnGround()) return;

      double distance = bot.getLocation().distance(target.getLocation());
      if (distance < 2.5) return;
      if (now - trait.lastAttackTime < attackCooldownMs(bot) ||
          now < trait.modernWeaponCooldownUntil) return;

      double minDistance = config().getDouble("modern-weapons.mace.launch-min-distance", 2.8);
      double maxDistance = config().getDouble("modern-weapons.mace.launch-max-distance", 4.5);
      if (distance < minDistance || distance > maxDistance) return;
      if (bot.hasPotionEffect(PotionEffectType.SLOW_FALLING)) return;

      Vector direction = target.getLocation().toVector().subtract(bot.getLocation().toVector());
      direction.setY(0.0);
      if (direction.lengthSquared() < 1.0E-4) return;
      direction.normalize();

      boolean usedWindCharge = consumeWindCharge(bot);
      double launchY;
      if (usedWindCharge) {
         launchY = velocityForMinecraftHeight(
            config().getDouble("modern-weapons.mace.wind-charge-launch-height", 7.0)
         );
      } else {
         if (!config().getBoolean("modern-weapons.mace.assisted-jump-without-wind-charge", true)) return;
         launchY = 0.52;
      }

      Vector velocity = bot.getVelocity();
      velocity.setY(Math.max(velocity.getY(), launchY));
      velocity.setX(velocity.getX() * 0.35 + direction.getX() * 0.15);
      velocity.setZ(velocity.getZ() * 0.35 + direction.getZ() * 0.15);
      bot.setVelocity(velocity);
      bot.setSprinting(true);

      trait.maceLaunchActive = true;
      trait.maceLaunchStartedAt = now;
      trait.modernWeaponCooldownUntil = now + 220L;
   }

   private void prepareSpear(Player bot, Player target, BotTrait trait, long now) {
      if (spearTargetChanged(trait, target)) {
         trait.spearChargeStartedAt = 0L;
         trait.spearLastHitAt = 0L;
      }
      trait.modernWeaponTarget = target.getUniqueId();

      double distance = bot.getLocation().distance(target.getLocation());
      if (distance < SPEAR_MIN_REACH + 0.05) {
         Vector away = bot.getLocation().toVector().subtract(target.getLocation().toVector());
         away.setY(0.0);
         if (away.lengthSquared() > 1.0E-4) {
            away.normalize();
            Vector velocity = bot.getVelocity();
            velocity.setX(velocity.getX() * 0.55 + away.getX() * 0.12);
            velocity.setZ(velocity.getZ() * 0.55 + away.getZ() * 0.12);
            bot.setVelocity(velocity);
         }
         trait.spearChargeStartedAt = 0L;
         return;
      }

      if (!config().getBoolean("modern-weapons.spear.charge-enabled", true) ||
          !bot.isOnGround() || distance > SPEAR_MAX_REACH) return;

      if (trait.spearChargeStartedAt == 0L) trait.spearChargeStartedAt = now;
      bot.setSprinting(true);
   }

   private void performMaceGroundHit(Player bot, Player target, BotTrait trait, long now) {
      bot.swingMainHand();
      double damage = attackDamage(bot) + sharpnessBonus(bot.getInventory().getItemInMainHand());
      applyDamage(target, bot, damage, true);
      applyKnockback(target, bot, 0.45, 0.32);
      traitTagCombat(target);
      trait.lastAttackTime = now;
      trait.modernWeaponCooldownUntil = now + attackCooldownMs(bot);
      bot.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.9F, 0.8F);
   }

   private void performMaceSmash(Player bot, Player target, BotTrait trait, double fallDistance, long now) {
      bot.swingMainHand();
      ItemStack mace = bot.getInventory().getItemInMainHand();

      int density = enchantLevel(mace, "density");
      int breach = enchantLevel(mace, "breach");

      double damage = attackDamage(bot)
         + maceSmashBonus(fallDistance)
         + 0.5 * density * fallDistance
         + sharpnessBonus(mace);

      damage *= 1.5;

      applyDamage(target, bot, damage, breach > 0);
      double knockback = fallDistance > 5.0 ? 1.0 : 0.55;
      applyKnockback(target, bot, knockback, Math.min(0.9, 0.30 + fallDistance * 0.035));
      applyNearbyMaceKnockback(target, bot, knockback * 0.9);

      bot.setFallDistance(0.0F);
      bot.getWorld().spawnParticle(Particle.CRIT,
         target.getLocation().add(0.0, 1.0, 0.0), 12, 0.35, 0.35, 0.35, 0.1);
      bot.getWorld().playSound(target.getLocation(),
         Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0F, 0.65F);
      traitTagCombat(target);

      int windBurst = enchantLevel(mace, "wind_burst");
      if (windBurst > 0) {
         Vector velocity = bot.getVelocity();
         velocity.setY(velocityForMinecraftHeight(7.0 * windBurst));
         bot.setVelocity(velocity);
         bot.setFallDistance(0.0F);
         bot.getWorld().spawnParticle(Particle.CLOUD,
            bot.getLocation().add(0.0, 0.4, 0.0), 10, 0.2, 0.15, 0.2, 0.04);
      }

      trait.lastAttackTime = now;
      trait.modernWeaponCooldownUntil = now + 300L;
      trait.maceLaunchActive = windBurst > 0;
      trait.maceLaunchStartedAt = now;
   }

   private void performSpearJab(Player bot, Player target, BotTrait trait, long now) {
      bot.setSprinting(false);
      bot.swingMainHand();

      double damage = attackDamage(bot) + sharpnessBonus(bot.getInventory().getItemInMainHand());
      applyDamage(target, bot, damage, false);
      applyKnockback(target, bot, 0.45, 0.28);
      applyLunge(bot);
      traitTagCombat(target);

      trait.lastAttackTime = now;
      trait.modernWeaponCooldownUntil = now + attackCooldownMs(bot);
      trait.spearChargeStartedAt = 0L;
      bot.getWorld().playSound(target.getLocation(),
         Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.8F, 1.3F);
   }

   private void performSpearCharge(Player bot, Player target, BotTrait trait, long now, double relativeSpeed) {
      bot.swingMainHand();

      ItemStack spear = bot.getInventory().getItemInMainHand();
      SpearStats stats = spearStats(spear.getType().name());

      double damage = spearChargeDamage(
         attackDamage(bot),
         relativeSpeed,
         stats.velocityMultiplier(),
         viewFactor(bot, target)
      ) + sharpnessBonus(spear);

      boolean engaged = now - trait.spearChargeStartedAt <= 1300L;
      applyDamage(target, bot, damage, false);

      if (engaged) {
         applyKnockback(target, bot,
            Math.min(0.9, 0.38 + relativeSpeed * 0.045), 0.28);
      }

      traitTagCombat(target);
      trait.spearLastHitAt = now;
      trait.modernWeaponCooldownUntil = now + SPEAR_CONTACT_COOLDOWN_MS;
      bot.getWorld().playSound(target.getLocation(),
         Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.9F, 1.0F);
   }

   private void applyDamage(Player target, Player attacker, double damage, boolean applyBreach) {
      double raw = Math.max(0.0, damage);
      if (applyBreach) {
         raw = rawDamageForBreachedArmor(
            raw, target,
            enchantLevel(attacker.getInventory().getItemInMainHand(), "breach")
         );
      }
      target.damage(raw, attacker);
   }

   private void applyNearbyMaceKnockback(Player target, Player attacker, double strength) {
      for (Entity entity : target.getWorld().getNearbyEntities(
         target.getLocation(), 3.5, 2.0, 3.5)) {
         if (!(entity instanceof LivingEntity living) ||
             entity.equals(target) || entity.equals(attacker) || living.isDead()) continue;

         Vector direction = living.getLocation().toVector()
            .subtract(target.getLocation().toVector());
         direction.setY(0.0);
         if (direction.lengthSquared() < 1.0E-5) continue;
         direction.normalize();

         Vector current = living.getVelocity();
         living.setVelocity(new Vector(
            current.getX() * 0.5 + direction.getX() * strength * 0.75,
            Math.max(current.getY(), 0.22),
            current.getZ() * 0.5 + direction.getZ() * strength * 0.75
         ));
      }
   }

   private void applyKnockback(LivingEntity target, Player attacker, double strength, double vertical) {
      Vector direction = target.getLocation().toVector()
         .subtract(attacker.getLocation().toVector());
      direction.setY(0.0);

      if (direction.lengthSquared() < 1.0E-5) {
         direction = attacker.getLocation().getDirection().clone();
         direction.setY(0.0);
      }
      if (direction.lengthSquared() < 1.0E-5) return;

      direction.normalize();
      Vector current = target.getVelocity();
      target.setVelocity(new Vector(
         current.getX() * 0.5 + direction.getX() * strength,
         Math.max(current.getY() * 0.5, vertical),
         current.getZ() * 0.5 + direction.getZ() * strength
      ));
   }

   private void playShieldBlock(Player target) {
      target.getWorld().playSound(target.getLocation(), Sound.ITEM_SHIELD_BLOCK, 0.8F, 1.0F);
   }

   private boolean isShieldUp(Player player) {
      if (!player.isHandRaised()) return false;
      ItemStack active = player.getActiveItem();
      return active != null && active.getType() == Material.SHIELD &&
         player.getCooldown(Material.SHIELD) == 0;
   }

   private boolean consumeWindCharge(Player bot) {
      if (!config().getBoolean("modern-weapons.mace.wind-charge-enabled", true)) return false;

      PlayerInventory inventory = bot.getInventory();
      for (int slot = 0; slot < inventory.getSize(); slot++) {
         ItemStack item = inventory.getItem(slot);
         if (item != null && item.getType() == Material.WIND_CHARGE && item.getAmount() > 0) {
            item.setAmount(item.getAmount() - 1);
            if (item.getAmount() <= 0) inventory.setItem(slot, null);
            return true;
         }
      }
      return false;
   }

   private void applyLunge(Player bot) {
      int lunge = enchantLevel(bot.getInventory().getItemInMainHand(), "lunge");
      if (lunge <= 0 || !bot.isOnGround() || bot.isInWater() || bot.getFoodLevel() < 6) return;

      Vector direction = bot.getLocation().getDirection().clone();
      direction.setY(0.0);
      if (direction.lengthSquared() < 1.0E-5) return;
      direction.normalize();

      double strength = 0.42 + Math.min(0.36, lunge * 0.12);
      Vector velocity = bot.getVelocity();
      velocity.setX(direction.getX() * strength);
      velocity.setZ(direction.getZ() * strength);
      bot.setVelocity(velocity);
      bot.setFoodLevel(Math.max(0, bot.getFoodLevel() - Math.min(3, lunge)));
   }

   private double relativeSpeedAlongView(Player attacker, Player target) {
      Vector look = attacker.getEyeLocation().getDirection().clone().normalize();
      Vector relative = attacker.getVelocity().clone().subtract(target.getVelocity());
      return Math.max(0.0, relative.dot(look) * 20.0);
   }

   private double viewFactor(Player attacker, Player target) {
      Vector look = attacker.getEyeLocation().getDirection().clone();
      look.setY(0.0);
      Vector delta = target.getLocation().toVector()
         .subtract(attacker.getLocation().toVector());
      delta.setY(0.0);

      if (look.lengthSquared() < 1.0E-5 || delta.lengthSquared() < 1.0E-5) return 1.0;
      look.normalize();
      delta.normalize();
      return Math.max(0.0, Math.min(1.0, look.dot(delta) * 0.5 + 0.5));
   }

   private double attackDamage(Player bot) {
      AttributeInstance attribute = bot.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
      return attribute == null ? 1.0 : Math.max(1.0, attribute.getValue());
   }

   private long attackCooldownMs(Player bot) {
      AttributeInstance attribute = bot.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
      double speed = attribute == null ? 4.0 : attribute.getValue();
      if (speed < 0.01) speed = 4.0;
      return Math.max(50L, Math.round(1000.0 / speed));
   }

   private double sharpnessBonus(ItemStack item) {
      int level = enchantLevel(item, "sharpness");
      return level > 0 ? 0.5 + level * 0.5 : 0.0;
   }

   private int enchantLevel(ItemStack item, String key) {
      if (item == null || !item.hasItemMeta()) return 0;
      ItemMeta meta = item.getItemMeta();
      if (meta == null) return 0;

      for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
         String enchantKey = entry.getKey().getKey().getKey();
         if (key.equalsIgnoreCase(enchantKey)) return Math.max(0, entry.getValue());
      }
      return 0;
   }

   static double maceSmashBonus(double fallDistance) {
      if (fallDistance <= MACE_MIN_SMASH_FALL) return 0.0;
      double blocks = Math.max(0.0, fallDistance);
      return Math.min(blocks, 3.0) * 4.0
         + Math.min(Math.max(blocks - 3.0, 0.0), 5.0) * 2.0
         + Math.max(blocks - 8.0, 0.0);
   }

   static double spearChargeDamage(double baseDamage, double relativeSpeedBps,
                                   double multiplier, double viewFactor) {
      return Math.max(baseDamage,
         relativeSpeedBps * multiplier * Math.max(0.0, Math.min(1.0, viewFactor)));
   }

   private double rawDamageForBreachedArmor(double desiredDamage, Player target, int breachLevel) {
      AttributeInstance armorAttribute = target.getAttribute(Attribute.GENERIC_ARMOR);
      AttributeInstance toughnessAttribute = target.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
      double armor = armorAttribute == null ? 0.0 : Math.max(0.0, armorAttribute.getValue());
      double toughness = toughnessAttribute == null ? 0.0 : Math.max(0.0, toughnessAttribute.getValue());

      if (armor <= 0.0 || breachLevel <= 0) return desiredDamage;

      double breachedArmor = armor * Math.max(0.0, 1.0 - 0.15 * breachLevel);
      double desiredAfterArmor = vanillaArmorReduced(desiredDamage, breachedArmor, toughness);

      double low = desiredDamage;
      double high = Math.max(desiredDamage * 2.0, desiredDamage + 10.0);

      for (int i = 0; i < 24 && vanillaArmorReduced(high, armor, toughness) < desiredAfterArmor; i++) {
         high *= 1.5;
      }

      for (int i = 0; i < 48; i++) {
         double mid = (low + high) * 0.5;
         if (vanillaArmorReduced(mid, armor, toughness) >= desiredAfterArmor) high = mid;
         else low = mid;
      }
      return high;
   }

   private double vanillaArmorReduced(double damage, double armor, double toughness) {
      double reduction = Math.min(20.0,
         Math.max(armor / 5.0, armor - damage / (2.0 + toughness / 4.0)));
      return damage * (1.0 - reduction / 25.0);
   }

   private double velocityForMinecraftHeight(double targetHeight) {
      double low = 0.0;
      double high = 2.5;

      for (int i = 0; i < 36; i++) {
         double mid = (low + high) * 0.5;
         if (simulatedJumpHeight(mid) < targetHeight) low = mid;
         else high = mid;
      }
      return high;
   }

   private double simulatedJumpHeight(double initialVelocity) {
      double y = 0.0;
      double velocity = initialVelocity;
      double maxY = 0.0;

      for (int tick = 0; tick < 200; tick++) {
         y += velocity;
         maxY = Math.max(maxY, y);
         velocity = (velocity - 0.08) * 0.98;
         if (tick > 3 && velocity < 0.0 && y < maxY - 0.25) break;
      }
      return maxY;
   }

   private SpearStats spearStats(String materialName) {
      String name = materialName == null ? "" : materialName;
      if (name.startsWith("NETHERITE_")) return new SpearStats(5.0, 0.869565, 1.20);
      if (name.startsWith("DIAMOND_")) return new SpearStats(4.0, 0.952381, 1.075);
      if (name.startsWith("IRON_")) return new SpearStats(3.0, 1.052632, 0.95);
      if (name.startsWith("COPPER_")) return new SpearStats(2.0, 1.176471, 0.82);
      if (name.startsWith("STONE_")) return new SpearStats(2.0, 1.333333, 0.82);
      return new SpearStats(1.0, 1.538462, 0.70);
   }

   private long spearChargeDelayMs() {
      return Math.max(0L,
         config().getLong("modern-weapons.spear.charge-delay-ms", 400L));
   }

   private boolean isSpear(ItemStack item) {
      return item != null && item.getType().name().endsWith("_SPEAR");
   }

   private boolean spearTargetChanged(BotTrait trait, Player target) {
      return trait.modernWeaponTarget != null &&
         !trait.modernWeaponTarget.equals(target.getUniqueId());
   }

   private boolean maceTargetChanged(BotTrait trait, Player target) {
      return trait.modernWeaponTarget != null &&
         !trait.modernWeaponTarget.equals(target.getUniqueId());
   }

   private void resetModernState(BotTrait trait) {
      if (trait == null) return;
      trait.maceLaunchStartedAt = 0L;
      trait.maceLaunchActive = false;
      trait.spearChargeStartedAt = 0L;
      trait.spearLastHitAt = 0L;
      trait.modernWeaponTarget = null;
   }

   private void traitTagCombat(Player target) {
      if (target != null) plugin.addCombatTag(target);
   }

   private org.bukkit.configuration.file.FileConfiguration config() {
      return plugin.getConfig();
   }

   private record SpearStats(double baseDamage, double attackSpeed, double velocityMultiplier) {
   }
}
