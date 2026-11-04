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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Cart PvP setup and ignition. Unlike the old controller this never creates a
 * minecart in mid-air and never drops kit items: it places a real rail, spawns
 * the TNT minecart on that rail, then uses a Flint-and-Steel fire lane with a
 * crossbow or a Flame bow. Carting is deliberately a close-range tactic.
 */
public final class CartPvpController {
    private final PracticeBotPlugin plugin;

    public CartPvpController(PracticeBotPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean tick(Player bot, Player target, BotTrait trait, long now) {
        if (bot == null || target == null || trait == null || bot.isDead() ||
            target.isDead() || !bot.isValid() || !target.isValid() ||
            !bot.getWorld().equals(target.getWorld())) {
            return false;
        }
        if (!plugin.getConfig().getBoolean("tactics.cart-pvp.enabled", true)) return false;

        double distance = bot.getLocation().distance(target.getLocation());
        selectCombatItem(bot, distance);
        long cooldown = TemplateDifficultyTuning.getLong(plugin, trait,
            "cart-cooldown-ms", 4500L, "tactics.cart-pvp.cooldown-ms");
        if (now - trait.lastCartActionAt < cooldown) return false;

        double minDistance = TemplateDifficultyTuning.getDouble(plugin, trait,
            "cart-min-distance", 2.5D, "tactics.cart-pvp.min-distance");
        double maxDistance = TemplateDifficultyTuning.getDouble(plugin, trait,
            "cart-max-distance", 7.5D, "tactics.cart-pvp.max-distance");
        if (!shouldAttemptCart(distance, minDistance, maxDistance,
            bot.isOnGround(), target.isOnGround(), bot.hasLineOfSight(target))) return false;

        ItemStack carts = find(bot, Material.TNT_MINECART);
        if (carts == null) return false;

        Vector towardTarget = target.getLocation().toVector()
            .subtract(bot.getLocation().toVector());
        towardTarget.setY(0.0D);
        if (towardTarget.lengthSquared() < 1.0E-5D) return false;
        towardTarget.normalize();

        // Put the cart just beyond the opponent, so normal forward knockback
        // carries them toward the rail instead of placing the trap behind us.
        Vector side = new Vector(-towardTarget.getZ(), 0.0D, towardTarget.getX());
        Location desiredRail = target.getLocation().clone()
            .add(towardTarget.clone().multiply(1.1D))
            .add(side.multiply(1.05D));
        desiredRail.setY(target.getLocation().getY());
        RailPlacement placement = findOrPlaceRail(bot, desiredRail, towardTarget, trait);
        if (placement == null) return false;
        Block railBlock = placement.block();
        if (!railBlock.getRelative(0, 1, 0).isPassable()) {
            rollbackRail(bot, placement);
            return false;
        }

        Location spawn = railBlock.getLocation().add(0.5D, 0.12D, 0.5D);
        ExplosiveMinecart cart;
        try {
            if (!(bot.getWorld().spawnEntity(spawn, EntityType.TNT_MINECART)
                instanceof ExplosiveMinecart spawned)) {
                rollbackRail(bot, placement);
                return false;
            }
            cart = spawned;
        } catch (Throwable ex) {
            rollbackRail(bot, placement);
            plugin.debugLog(() -> "CartPvP minecart placement failed: " + ex.getMessage());
            return false;
        }

        // Keep the cart on its rail. The burning arrow's own velocity drives
        // the TNT minecart explosion strength when it collides with the cart.
        cart.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
        if (!bot.hasLineOfSight(cart)) {
            cart.remove();
            rollbackRail(bot, placement);
            return false;
        }
        consume(carts);
        Block fireBlock = ignite(bot, cart, spawn, trait);

        // Clean up only blocks/entities created by this attempt. Existing arena
        // rails are never touched, and a missed cart cannot litter the map.
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (cart.isValid() && !cart.isDead()) cart.remove();
            if (placement.placed() && railBlock.getType() == Material.RAIL) {
                railBlock.setType(Material.AIR, false);
            }
            if (fireBlock != null && fireBlock.getType() == Material.FIRE) {
                fireBlock.setType(Material.AIR, false);
            }
        }, 50L);

        // Keep the bot advancing from the opposite side of the target. Its
        // normal attack routine supplies the actual melee hit/knockback.
        Vector approach = target.getLocation().toVector()
            .subtract(bot.getLocation().toVector());
        approach.setY(0.0D);
        if (approach.lengthSquared() > 1.0E-5D) {
            approach.normalize().multiply(0.18D);
            Vector velocity = bot.getVelocity();
            velocity.setX(velocity.getX() * 0.72D + approach.getX());
            velocity.setZ(velocity.getZ() * 0.72D + approach.getZ());
            bot.setVelocity(velocity);
            bot.setSprinting(true);
        }

        trait.lastCartActionAt = now;
        trait.tacticCooldownUntil = now + TemplateDifficultyTuning.getLong(plugin, trait,
            "cart-followup-delay-ms", 900L);
        trait.lastTactic = "cart-rail-place-ignite";
        return true;
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

    private Block ignite(Player bot, ExplosiveMinecart cart, Location spawn, BotTrait trait) {
        ItemStack crossbow = find(bot, Material.CROSSBOW);
        ItemStack flintSteel = find(bot, Material.FLINT_AND_STEEL);
        ItemStack bow = find(bot, Material.BOW);
        ItemStack arrows = find(bot, Material.ARROW);
        boolean crossbowCart = crossbow != null && flintSteel != null && arrows != null &&
            ThreadLocalRandom.current().nextDouble(100.0D) >= plugin.getConfig().getDouble(
                "tactics.cart-pvp.flame-bow-chance-percent", 35.0D);
        Block fireBlock = null;

        if (crossbowCart) {
            fireBlock = placeFireLane(bot, spawn, flintSteel);
            if (fireBlock == null) crossbowCart = false;
        }

        ItemStack shooter = crossbowCart ? crossbow : bow;
        boolean flameBow = bow != null && bow.getEnchantmentLevel(Enchantment.FLAME) > 0;
        if (shooter == null || arrows == null || (!crossbowCart && !flameBow)) {
            if (fireBlock != null && fireBlock.getType() == Material.FIRE) fireBlock.setType(Material.AIR, false);
            int fallbackFuse = (int) TemplateDifficultyTuning.getLong(plugin, trait,
                "cart-fuse-ticks", 35L, "tactics.cart-pvp.fuse-ticks");
            cart.setFuseTicks(Math.max(5, Math.min(80, fallbackFuse)));
            trait.lastTactic = "cart-native-ignition-fallback";
            return null;
        }

        ItemStack oldHand = bot.getInventory().getItemInMainHand();
        bot.getInventory().setItemInMainHand(shooter.clone());
        Vector aim = spawn.toVector().add(new Vector(0.0D, 0.35D, 0.0D))
            .subtract(bot.getEyeLocation().toVector());
        if (aim.lengthSquared() > 1.0E-5D) {
            aim.normalize().multiply(3.15D);
            bot.swingMainHand();
            try {
                Arrow arrow = bot.launchProjectile(Arrow.class, aim);
                arrow.setFireTicks(100);
                consume(arrows);
                if (crossbowCart) {
                    trait.lastTactic = "cart-crossbow-fire-rail";
                } else {
                    trait.lastTactic = "cart-flame-bow";
                }
            } catch (Throwable ex) {
                plugin.debugLog(() -> "CartPvP burning arrow failed: " + ex.getMessage());
                int fallbackFuse = (int) TemplateDifficultyTuning.getLong(plugin, trait,
                    "cart-fuse-ticks", 35L, "tactics.cart-pvp.fuse-ticks");
                cart.setFuseTicks(Math.max(5, Math.min(80, fallbackFuse)));
                if (fireBlock != null && fireBlock.getType() == Material.FIRE) fireBlock.setType(Material.AIR, false);
                fireBlock = null;
            }
        } else {
            cart.setFuseTicks(35);
        }
        bot.getInventory().setItemInMainHand(oldHand);
        return fireBlock;
    }

    private Block placeFireLane(Player bot, Location cartLocation, ItemStack flintSteel) {
        Vector line = cartLocation.toVector().subtract(bot.getEyeLocation().toVector());
        line.setY(0.0D);
        if (line.lengthSquared() < 1.0E-5D) return null;
        line.normalize();
        Block fire = bot.getLocation().clone().add(line.multiply(1.25D)).getBlock();
        Block support = fire.getRelative(0, -1, 0);
        if (!fire.getType().isAir() || !support.getType().isSolid() ||
            support.getType().isFlammable() || fire.isLiquid() || hasFlammableNeighbor(fire)) return null;
        ItemStack oldHand = bot.getInventory().getItemInMainHand();
        try {
            bot.getInventory().setItemInMainHand(flintSteel.clone());
            fire.setType(Material.FIRE, false);
            bot.swingMainHand();
            damageFlintAndSteel(flintSteel);
            bot.getInventory().setItemInMainHand(oldHand);
            return fire;
        } catch (Throwable ex) {
            bot.getInventory().setItemInMainHand(oldHand);
            if (fire.getType() == Material.FIRE) fire.setType(Material.AIR, false);
            plugin.debugLog(() -> "CartPvP flint-and-steel fire placement failed: " + ex.getMessage());
            return null;
        }
    }

    private boolean hasFlammableNeighbor(Block fire) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    if (fire.getRelative(x, y, z).getType().isFlammable()) return true;
                }
            }
        }
        return false;
    }

    private void rollbackRail(Player bot, RailPlacement placement) {
        if (placement == null || !placement.placed()) return;
        Block rail = placement.block();
        if (rail.getType() == Material.RAIL) {
            rail.setType(Material.AIR, false);
            bot.getInventory().addItem(new ItemStack(Material.RAIL, 1));
        }
    }

    private void damageFlintAndSteel(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;
        var meta = item.getItemMeta();
        if (!(meta instanceof Damageable damageable)) return;
        int nextDamage = damageable.getDamage() + 1;
        if (nextDamage >= item.getType().getMaxDurability()) {
            item.setAmount(item.getAmount() - 1);
            return;
        }
        damageable.setDamage(nextDamage);
        item.setItemMeta(meta);
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
