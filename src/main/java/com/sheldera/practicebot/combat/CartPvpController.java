package com.sheldera.practicebot.combat;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public final class CartPvpController {
    private final PracticeBotPlugin plugin;

    public CartPvpController(PracticeBotPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean tick(Player bot, Player target, BotTrait trait, long now) {
        if (bot == null || target == null || trait == null ||
            bot.isDead() || target.isDead() ||
            !bot.getWorld().equals(target.getWorld())) {
            return false;
        }

        if (!plugin.getConfig().getBoolean("tactics.cart-pvp.enabled", true)) {
            return false;
        }

        if (now - trait.lastCartActionAt <
            plugin.getConfig().getLong("tactics.cart-pvp.cooldown-ms", 3500L)) {
            return false;
        }

        double distance = bot.getLocation().distance(target.getLocation());
        double minDistance = plugin.getConfig().getDouble("tactics.cart-pvp.min-distance", 5.0D);
        double maxDistance = plugin.getConfig().getDouble("tactics.cart-pvp.max-distance", 18.0D);

        if (distance < minDistance || distance > maxDistance) {
            return false;
        }

        Block rail = findLaunchRail(bot, target);
        if (rail == null) {
            return false;
        }

        World world = bot.getWorld();
        Location spawn = rail.getLocation().add(0.5D, 0.05D, 0.5D);
        ExplosiveMinecart cart;

        try {
            if (!(world.spawnEntity(spawn, EntityType.TNT_MINECART) instanceof ExplosiveMinecart spawned)) {
                return false;
            }
            cart = spawned;
        } catch (Exception ex) {
            plugin.debugLog(() -> "CartPvP minecart spawn failed: " + ex.getMessage());
            return false;
        }

        Vector direction = target.getLocation().toVector()
            .subtract(spawn.toVector());
        direction.setY(0.0D);

        if (direction.lengthSquared() < 1.0E-5D) {
            cart.remove();
            return false;
        }

        direction.normalize();

        double speed = plugin.getConfig().getDouble("tactics.cart-pvp.launch-speed", 0.8D);
        double jitter = plugin.getConfig().getDouble("tactics.humanization.movement-jitter", 0.015D);
        double randomJitter = ThreadLocalRandom.current().nextDouble(-jitter, jitter);

        cart.setVelocity(new Vector(
            direction.getX() * (speed + randomJitter),
            0.0D,
            direction.getZ() * (speed + randomJitter)
        ));


        int fuse = (int) plugin.getConfig().getLong("tactics.cart-pvp.fuse-ticks", 45L);
        cart.setFuseTicks(Math.max(5, Math.min(200, fuse)));
        try {
            cart.ignite();
        } catch (Throwable ignored) {
            // Older Paper builds may not expose the helper; the cart remains harmless.
        }

        trait.lastCartActionAt = now;
        trait.tacticCooldownUntil = now + 700L;
        trait.lastTactic = "cart-pressure";
        return true;
    }

    private Block findLaunchRail(Player bot, Player target) {
        Location origin = bot.getLocation();
        Vector direction = target.getLocation().toVector()
            .subtract(origin.toVector());
        direction.setY(0.0D);

        if (direction.lengthSquared() < 1.0E-5D) {
            return null;
        }

        direction.normalize();

        int radius = plugin.getConfig().getInt("tactics.cart-pvp.rail-search-radius", 6);
        Block best = null;
        double bestScore = Double.MAX_VALUE;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = origin.getBlock().getRelative(x, y, z);
                    if (!isRail(block.getType())) {
                        continue;
                    }

                    Vector fromBot = block.getLocation().add(0.5D, 0.0D, 0.5D)
                        .toVector().subtract(origin.toVector());
                    fromBot.setY(0.0D);

                    double forward = fromBot.dot(direction);
                    if (forward < -1.0D) {
                        continue;
                    }

                    double score = fromBot.lengthSquared() - forward * 1.5D;
                    if (score < bestScore) {
                        bestScore = score;
                        best = block;
                    }
                }
            }
        }

        return best;
    }

    private boolean isRail(Material material) {
        return material == Material.RAIL ||
            material == Material.POWERED_RAIL ||
            material == Material.DETECTOR_RAIL ||
            material == Material.ACTIVATOR_RAIL;
    }
}
