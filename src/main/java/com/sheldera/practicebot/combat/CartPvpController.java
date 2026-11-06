package com.sheldera.practicebot.combat;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import com.sheldera.practicebot.TemplateDifficultyTuning;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rail;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cart PvP setup and ignition. Unlike the old controller this never creates a
 * minecart in mid-air and never drops kit items: it places a real rail, spawns
 * the TNT minecart on that rail, then uses a Flint-and-Steel fire lane with a
 * crossbow or a Flame bow. Carting is deliberately a close-range tactic.
 */
public final class CartPvpController {
    private final PracticeBotPlugin plugin;
    private final Map<UUID, CartPlan> plans = new ConcurrentHashMap<>();

    public CartPvpController(PracticeBotPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean tick(Player bot, Player target, BotTrait trait, long now) {
        if (bot == null || target == null || trait == null || bot.isDead() ||
            target.isDead() || !bot.isValid() || !target.isValid() ||
            !bot.getWorld().equals(target.getWorld())) {
            if (bot != null) cleanupPlan(bot, plans.remove(bot.getUniqueId()));
            return false;
        }
        if (!plugin.getConfig().getBoolean("tactics.cart-pvp.enabled", true)) {
            cleanupPlan(bot, plans.remove(bot.getUniqueId()));
            return false;
        }

        UUID botId = bot.getUniqueId();
        CartPlan plan = plans.get(botId);
        if (plan != null && (!plan.targetId().equals(target.getUniqueId()) ||
            now - plan.startedAt() > configLong("tactics.cart-pvp.plan-timeout-ms", 6500L, 1000L, 20000L))) {
            cleanupPlan(bot, plan);
            plans.remove(botId);
            plan = null;
        }
        if (plan != null) {
            return advancePlan(bot, target, trait, plan, now);
        }

        double distance = bot.getLocation().distance(target.getLocation());
        selectCombatItem(bot, distance);
        long cooldown = TemplateDifficultyTuning.getLong(plugin, trait,
            "cart-cooldown-ms", 4500L, "tactics.cart-pvp.cooldown-ms");
        if (now - trait.lastCartActionAt < cooldown) return false;

        double minDistance = TemplateDifficultyTuning.getDouble(plugin, trait,
            "cart-min-distance", 5.5D, "tactics.cart-pvp.min-distance");
        double maxDistance = TemplateDifficultyTuning.getDouble(plugin, trait,
            "cart-max-distance", 8.0D, "tactics.cart-pvp.max-distance");
        if (!shouldAttemptCart(distance, minDistance, maxDistance,
            bot.isOnGround(), target.isOnGround(), bot.hasLineOfSight(target))) return false;

        ItemStack carts = find(bot, Material.TNT_MINECART);
        ItemStack rails = find(bot, Material.RAIL);
        ItemStack bow = find(bot, Material.BOW);
        ItemStack arrows = find(bot, Material.ARROW);
        boolean flameBow = bow != null && bow.getEnchantmentLevel(Enchantment.FLAME) > 0;
        if (carts == null || rails == null || arrows == null || !flameBow) return false;

        Vector towardTarget = target.getLocation().toVector()
            .subtract(bot.getLocation().toVector());
        towardTarget.setY(0.0D);
        if (towardTarget.lengthSquared() < 1.0E-5D) return false;
        towardTarget.normalize();
        Vector side = new Vector(-towardTarget.getZ(), 0.0D, towardTarget.getX());
        Location desiredRail = target.getLocation().clone()
            .add(towardTarget.clone().multiply(1.1D))
            .add(side.multiply(configDouble("tactics.cart-pvp.rail-side-offset", 1.05D, 0.0D, 3.0D)));
        desiredRail.setY(target.getLocation().getY());

        // Never begin a detonation sequence unless the bot has room to back away.
        // TNT minecart explosions are server-authoritative and can kill the shooter.
        double safeDistance = configDouble("tactics.cart-pvp.self-safe-distance", 6.0D, 3.0D, 12.0D);
        if (!isOutsideSelfSafetyRadius(bot.getLocation().distance(desiredRail), safeDistance)) return false;
        CartPlan created = new CartPlan(target.getUniqueId(), desiredRail, now,
            now + configLong("tactics.cart-pvp.look-down-delay-ms", 250L, 0L, 2000L)
                + configLong("tactics.cart-pvp.reaction-delay-ms", 180L, 0L, 1500L));
        plans.put(botId, created);
        aimAt(bot, desiredRail.clone().add(0.5D, 0.0D, 0.5D));
        trait.lastTactic = "cart-aim-ground";
        return true;
    }

    private boolean advancePlan(Player bot, Player target, BotTrait trait, CartPlan plan, long now) {
        // The shared human-aim routine runs before this controller each tick.
        // Reassert the current action's look target while waiting so the bot
        // visibly keeps looking down, at the rail, and then at the cart.
        if (plan.stage() == 0) {
            aimAt(bot, plan.railLocation().clone().add(0.5D, 0.0D, 0.5D));
        } else if (plan.stage() == 1 && plan.rail() != null) {
            aimAt(bot, plan.rail().block().getLocation().add(0.5D, 0.15D, 0.5D));
        } else if ((plan.stage() == 2 || plan.stage() == 3) && plan.cart() != null && plan.cart().isValid()) {
            aimAt(bot, plan.cart().getLocation().add(0.0D, 0.35D, 0.0D));
        }
        if (now < plan.nextAt()) return true;
        switch (plan.stage()) {
            case 0 -> {
                RailPlacement placement = findOrPlaceRail(bot, plan.railLocation(),
                    target.getLocation().toVector().subtract(bot.getLocation().toVector()), trait);
                if (placement == null || !placement.block().getRelative(0, 1, 0).isPassable()) {
                    if (placement != null) rollbackRail(bot, placement);
                    plans.remove(bot.getUniqueId());
                    return true;
                }
                plan.rail(placement);
                plan.stage(1);
                plan.nextAt(now + configLong("tactics.cart-pvp.rail-look-delay-ms", 220L, 0L, 2000L));
                aimAt(bot, placement.block().getLocation().add(0.5D, 0.15D, 0.5D));
                trait.lastTactic = "cart-look-at-rail";
                return true;
            }
            case 1 -> {
                if (plan.rail() == null || !isRail(plan.rail().block().getType())) {
                    plans.remove(bot.getUniqueId());
                    return true;
                }
                Location spawn = plan.rail().block().getLocation().add(0.5D, 0.12D, 0.5D);
                double safeDistance = configDouble("tactics.cart-pvp.self-safe-distance", 6.0D, 3.0D, 12.0D);
                if (!isOutsideSelfSafetyRadius(bot.getLocation().distance(spawn), safeDistance)) {
                    cleanupPlan(bot, plan);
                    plans.remove(bot.getUniqueId());
                    trait.lastTactic = "cart-abort-self-safety";
                    return true;
                }
                try {
                    if (!(bot.getWorld().spawnEntity(spawn, EntityType.TNT_MINECART) instanceof ExplosiveMinecart cart)) {
                        cleanupPlan(bot, plan);
                        plans.remove(bot.getUniqueId());
                        return true;
                    }
                    plan.cart(cart);
                    cart.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
                    consume(find(bot, Material.TNT_MINECART));
                    plan.stage(2);
                    plan.nextAt(now + configLong("tactics.cart-pvp.arrow-aim-delay-ms", 850L, 100L, 3000L));
                    ItemStack shooter = find(bot, Material.BOW);
                    if (shooter != null) {
                        plan.previousMainHand(bot.getInventory().getItemInMainHand() == null
                            ? null : bot.getInventory().getItemInMainHand().clone());
                        bot.getInventory().setItemInMainHand(shooter.clone());
                    }
                    aimAt(bot, spawn.clone().add(0.0D, 0.35D, 0.0D));
                    trait.lastTactic = "cart-hold-aim-arrow";
                    return true;
                } catch (Throwable ex) {
                    cleanupPlan(bot, plan);
                    plans.remove(bot.getUniqueId());
                    plugin.debugLog(() -> "CartPvP staged minecart placement failed: " + ex.getMessage());
                    return true;
                }
            }
            case 2 -> {
                ExplosiveMinecart cart = plan.cart();
                if (cart == null || !cart.isValid() || cart.isDead()) {
                    cleanupPlan(bot, plan);
                    plans.remove(bot.getUniqueId());
                    return true;
                }
                Location cartLocation = cart.getLocation();
                if (!isOutsideSelfSafetyRadius(bot.getLocation().distance(cartLocation), configDouble(
                    "tactics.cart-pvp.self-safe-distance", 6.0D, 3.0D, 12.0D))) {
                    cleanupPlan(bot, plan);
                    plans.remove(bot.getUniqueId());
                    trait.lastTactic = "cart-abort-self-safety";
                    return true;
                }
                ItemStack arrows = find(bot, Material.ARROW);
                ItemStack bow = find(bot, Material.BOW);
                if (arrows == null || bow == null || bow.getEnchantmentLevel(Enchantment.FLAME) == 0) {
                    cleanupPlan(bot, plan);
                    plans.remove(bot.getUniqueId());
                    return true;
                }
                aimAt(bot, cartLocation.clone().add(0.0D, 0.35D, 0.0D));
                Vector aim = cartLocation.clone().add(0.0D, 0.35D, 0.0D).toVector()
                    .subtract(bot.getEyeLocation().toVector());
                if (aim.lengthSquared() < 1.0E-5D || !bot.hasLineOfSight(cart)) {
                    cleanupPlan(bot, plan);
                    plans.remove(bot.getUniqueId());
                    trait.lastTactic = "cart-abort-no-shot-line";
                    return true;
                }
                aim.normalize().multiply(configDouble("tactics.cart-pvp.arrow-speed", 3.15D, 1.0D, 4.0D));
                try {
                    bot.swingMainHand();
                    Arrow arrow = bot.launchProjectile(Arrow.class, aim);
                    arrow.setFireTicks(100);
                    consume(arrows);
                    restoreMainHand(bot, plan);
                    plan.stage(3);
                    plan.nextAt(now + 2500L);
                    trait.lastTactic = "cart-fire-aimed-arrow";
                    trait.lastCartActionAt = now;
                    trait.tacticCooldownUntil = now + configLong("tactics.cart-pvp.followup-delay-ms", 900L, 0L, 5000L);
                    return true;
                } catch (Throwable ex) {
                    cleanupPlan(bot, plan);
                    plans.remove(bot.getUniqueId());
                    plugin.debugLog(() -> "CartPvP staged arrow launch failed: " + ex.getMessage());
                    return true;
                }
            }
            default -> {
                cleanupPlan(bot, plan);
                plans.remove(bot.getUniqueId());
                return true;
            }
        }
    }

    private void aimAt(Player bot, Location target) {
        Vector direction = target.toVector().subtract(bot.getEyeLocation().toVector());
        if (direction.lengthSquared() < 1.0E-6D) return;
        direction.normalize();
        float yaw = (float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
        float pitch = (float) Math.toDegrees(-Math.asin(Math.max(-1.0D, Math.min(1.0D, direction.getY()))));
        bot.setRotation(yaw, Math.max(-90.0F, Math.min(90.0F, pitch)));
    }

    private void cleanupPlan(Player bot, CartPlan plan) {
        if (plan == null) return;
        if (plan.cart() != null && plan.cart().isValid() && !plan.cart().isDead()) plan.cart().remove();
        if (plan.rail() != null) rollbackRail(bot, plan.rail());
        restoreMainHand(bot, plan);
    }

    private void restoreMainHand(Player bot, CartPlan plan) {
        if (bot != null && plan != null && plan.previousMainHand() != null) {
            bot.getInventory().setItemInMainHand(plan.previousMainHand().clone());
            plan.previousMainHand(null);
        }
    }

    private long configLong(String path, long fallback, long min, long max) {
        return Math.max(min, Math.min(max, plugin.getConfig().getLong(path, fallback)));
    }

    private double configDouble(String path, double fallback, double min, double max) {
        double value = plugin.getConfig().getDouble(path, fallback);
        return Double.isFinite(value) ? Math.max(min, Math.min(max, value)) : fallback;
    }

    private static final class CartPlan {
        private final UUID targetId;
        private final Location railLocation;
        private final long startedAt;
        private int stage;
        private long nextAt;
        private RailPlacement rail;
        private ExplosiveMinecart cart;
        private ItemStack previousMainHand;
        CartPlan(UUID targetId, Location railLocation, long startedAt, long nextAt) {
            this.targetId = targetId; this.railLocation = railLocation.clone(); this.startedAt = startedAt;
            this.nextAt = nextAt;
        }
        UUID targetId() { return targetId; }
        Location railLocation() { return railLocation.clone(); }
        long startedAt() { return startedAt; }
        int stage() { return stage; }
        void stage(int value) { stage = value; }
        long nextAt() { return nextAt; }
        void nextAt(long value) { nextAt = value; }
        RailPlacement rail() { return rail; }
        void rail(RailPlacement value) { rail = value; }
        ExplosiveMinecart cart() { return cart; }
        void cart(ExplosiveMinecart value) { cart = value; }
        ItemStack previousMainHand() { return previousMainHand; }
        void previousMainHand(ItemStack value) { previousMainHand = value; }
    }

    private RailPlacement findOrPlaceRail(Player bot, Location desired, Vector direction, BotTrait trait) {
        Block center = desired.getBlock();
        int radius = Math.max(0, Math.min(3, TemplateDifficultyTuning.getInt(plugin,
            trait, "cart-rail-search-radius", 2, "tactics.cart-pvp.rail-search-radius")));
        Block existing = null;
        double bestDistance = Double.MAX_VALUE;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block candidate = center.getRelative(x, 0, z);
                if (!isRail(candidate.getType())) continue;
                double score = candidate.getLocation().distanceSquared(desired);
                if (score < bestDistance) {
                    bestDistance = score;
                    existing = candidate;
                }
            }
        }
        if (existing != null) return new RailPlacement(existing, false);

        // Only place a rail into a clear, supported block. No block breaking,
        // floating rails, or item drops if the arena has no valid surface.
        Block support = center.getRelative(0, -1, 0);
        if (!center.getType().isAir() || !support.getType().isSolid() ||
            center.isLiquid() || !center.getWorld().getWorldBorder().isInside(center.getLocation())) {
            return null;
        }
        ItemStack rails = find(bot, Material.RAIL);
        if (rails == null) return null;

        try {
            center.setType(Material.RAIL, false);
            BlockData data = center.getBlockData();
            if (data instanceof Rail railData) {
                railData.setShape(Math.abs(direction.getX()) > Math.abs(direction.getZ())
                    ? Rail.Shape.EAST_WEST : Rail.Shape.NORTH_SOUTH);
                center.setBlockData(railData, false);
            }
            consume(rails);
            return new RailPlacement(center, true);
        } catch (Throwable ex) {
            if (center.getType() == Material.RAIL) center.setType(Material.AIR, false);
            plugin.debugLog(() -> "CartPvP rail placement failed: " + ex.getMessage());
            return null;
        }
    }

    private void rollbackRail(Player bot, RailPlacement placement) {
        if (placement == null || !placement.placed()) return;
        Block rail = placement.block();
        if (rail.getType() == Material.RAIL) {
            rail.setType(Material.AIR, false);
            if (bot != null) bot.getInventory().addItem(new ItemStack(Material.RAIL, 1));
        }
    }

    private void selectCombatItem(Player bot, double distance) {
        Material preferred = distance <= 3.25D ? Material.NETHERITE_SWORD : Material.BOW;
        int slot = -1;
        for (int index = 0; index < 9; index++) {
            ItemStack item = bot.getInventory().getItem(index);
            if (item != null && item.getType() == preferred) {
                slot = index;
                break;
            }
        }
        if (slot >= 0 && bot.getInventory().getHeldItemSlot() != slot) {
            bot.getInventory().setHeldItemSlot(slot);
        }
    }

    private record RailPlacement(Block block, boolean placed) {}

    private ItemStack find(Player player, Material material) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material && item.getAmount() > 0) return item;
        }
        return null;
    }

    private void consume(ItemStack item) {
        if (item == null || item.getAmount() <= 0) return;
        item.setAmount(item.getAmount() - 1);
    }

    static boolean isOutsideSelfSafetyRadius(double distance, double minimumDistance) {
        return Double.isFinite(distance) && Double.isFinite(minimumDistance) &&
            minimumDistance >= 0.0D && distance >= minimumDistance;
    }

    static boolean shouldAttemptCart(double distance, double minDistance, double maxDistance,
                                     boolean botGrounded, boolean targetGrounded, boolean lineOfSight) {
        return Double.isFinite(distance) && distance >= minDistance && distance <= maxDistance &&
            botGrounded && targetGrounded && lineOfSight;
    }

    private boolean isRail(Material material) {
        return material == Material.RAIL || material == Material.POWERED_RAIL ||
            material == Material.DETECTOR_RAIL || material == Material.ACTIVATOR_RAIL;
    }
}
