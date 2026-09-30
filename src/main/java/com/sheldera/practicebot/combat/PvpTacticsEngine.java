package com.sheldera.practicebot.combat;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public final class PvpTacticsEngine {
    private final PracticeBotPlugin plugin;
    private final PvpEnvironment environment;
    private final CartPvpController cartController;

    public PvpTacticsEngine(PracticeBotPlugin plugin) {
        this.plugin = plugin;
        this.environment = new PvpEnvironment(plugin);
        this.cartController = new CartPvpController(plugin);

        plugin.debugLog(() -> "PvP environment: " + environment.summary());

        if (environment.isFastCrystalsEnabled()) {
            plugin.debugLog("FastCrystals detected. Crystal timing is being treated as server-authoritative fast-crystal combat.");
        }

        if (environment.isWindChargeResetEnabled()) {
            plugin.debugLog("WindChargeReset detected. Bots can use the plugin's server-side mid-air reset mechanic.");
        }

        if (environment.isAttributeSwapAvailable()) {
            plugin.debugLog("Paper attribute swapping is available because update-equipment-on-player-actions=false.");
        }
    }

    public PvpEnvironment environment() {
        return environment;
    }

    public void tick(Player bot, Player target, BotTrait trait, long now) {
        if (bot == null || target == null || trait == null ||
            bot.isDead() || target.isDead() ||
            !bot.getWorld().equals(target.getWorld())) {
            return;
        }

        initializeLatencyProfile(trait);

        if (now < trait.tacticNextDecisionAt) {
            return;
        }

        trait.tacticNextDecisionAt = now + nextDecisionDelay(trait);
        updateHumanAim(bot, target, trait);

        String profile = trait.getWeaponProfile();

        if (profile.startsWith("mace") || profile.startsWith("spear")) {
            if (tryWindChargeReset(bot, target, trait, now)) {
                return;
            }

            if (tryElytraApproach(bot, target, trait, now, profile)) {
                return;
            }
        }

        if ("cart".equals(profile) || "cart_elytra".equals(profile)) {
            cartController.tick(bot, target, trait, now);
            return;
        }

        applyHumanMovementNoise(bot, target, trait, now);
    }

    public boolean shouldUseAttributeSwap(BotTrait trait, long now) {
        if (!environment.isAttributeSwapAvailable()) {
            return false;
        }

        if (!plugin.getConfig().getBoolean("tactics.attribute-swap.enabled", true)) {
            return false;
        }

        if (now < trait.tacticCooldownUntil) {
            return false;
        }

        double chance = plugin.getConfig().getDouble(
            "tactics.attribute-swap.chance-percent", 18.0D
        );

        return ThreadLocalRandom.current().nextDouble(100.0D) < Math.max(0.0D, Math.min(100.0D, chance));
    }

    public boolean fastCrystalsDetected() {
        return environment.isFastCrystalsEnabled();
    }

    private void initializeLatencyProfile(BotTrait trait) {
        if (trait.simulatedPingMs > 0) {
            return;
        }

        int min = plugin.getConfig().getInt("tactics.humanization.ping.min-ms", 35);
        int max = plugin.getConfig().getInt("tactics.humanization.ping.max-ms", 180);

        min = Math.max(0, min);
        max = Math.max(min, max);

        // Mean-of-two sampling creates more typical middle-range latency and fewer
        // artificial extremes than a flat random distribution.
        int a = ThreadLocalRandom.current().nextInt(min, max + 1);
        int b = ThreadLocalRandom.current().nextInt(min, max + 1);
        trait.simulatedPingMs = Math.max(min, Math.min(max, (a + b) / 2));
    }

    private long nextDecisionDelay(BotTrait trait) {
        int jitter = plugin.getConfig().getInt(
            "tactics.humanization.ping.jitter-ms", 8
        );

        double networkFraction = plugin.getConfig().getDouble(
            "tactics.humanization.ping.action-latency-fraction", 0.45D
        );

        double reactionMin = plugin.getConfig().getDouble(
            "tactics.humanization.reaction.min-ms", 85.0D
        );

        double reactionMax = plugin.getConfig().getDouble(
            "tactics.humanization.reaction.max-ms", 210.0D
        );

        double reaction = ThreadLocalRandom.current().nextDouble(
            Math.min(reactionMin, reactionMax),
            Math.max(reactionMin, reactionMax) + 0.01D
        );

        double network = trait.simulatedPingMs * Math.max(0.0D, networkFraction);
        double noise = ThreadLocalRandom.current().nextDouble(
            -Math.max(0, jitter), Math.max(0, jitter) + 0.01D
        );

        return Math.max(25L, Math.round(reaction + network + noise));
    }

    private void updateHumanAim(Player bot, Player target, BotTrait trait) {
        Location current = bot.getLocation();

        Vector delta = target.getEyeLocation().toVector()
            .subtract(bot.getEyeLocation().toVector());

        if (delta.lengthSquared() < 1.0E-5D) {
            return;
        }

        double horizontal = Math.sqrt(delta.getX() * delta.getX() + delta.getZ() * delta.getZ());
        float desiredYaw = (float) Math.toDegrees(Math.atan2(-delta.getX(), delta.getZ()));
        float desiredPitch = (float) -Math.toDegrees(Math.atan2(delta.getY(), horizontal));

        float yawDelta = wrapDegrees(desiredYaw - current.getYaw());

        double ping = trait.simulatedPingMs;
        double smoothing = 0.34D - Math.min(0.16D, ping / 1200.0D);
        smoothing = Math.max(0.16D, smoothing);

        double maxTurn = plugin.getConfig().getDouble(
            "tactics.humanization.aim.max-degrees-per-update", 24.0D
        );

        yawDelta = (float) Math.max(-maxTurn, Math.min(maxTurn, yawDelta));

        double pitchDelta = desiredPitch - current.getPitch();
        pitchDelta = Math.max(-maxTurn, Math.min(maxTurn, pitchDelta));

        double jitter = plugin.getConfig().getDouble(
            "tactics.humanization.aim.jitter-degrees", 0.6D
        );

        float yaw = current.getYaw()
            + (float) (yawDelta * smoothing)
            + (float) ThreadLocalRandom.current().nextDouble(-jitter, jitter);

        float pitch = current.getPitch()
            + (float) (pitchDelta * smoothing)
            + (float) ThreadLocalRandom.current().nextDouble(-jitter * 0.5D, jitter * 0.5D);

        bot.setRotation(yaw, Math.max(-90.0F, Math.min(90.0F, pitch)));
    }

    private void applyHumanMovementNoise(
        Player bot, Player target, BotTrait trait, long now
    ) {
        if (!plugin.getConfig().getBoolean(
            "tactics.humanization.movement-enabled", true
        )) {
            return;
        }

        if (!bot.isOnGround() || bot.isGliding() ||
            bot.getVelocity().lengthSquared() < 0.0005D) {
            return;
        }

        double distance = bot.getLocation().distance(target.getLocation());
        if (distance < 2.2D || distance > 8.0D) {
            return;
        }

        long switchEvery = plugin.getConfig().getLong(
            "tactics.humanization.strafe-switch-ms", 380L
        );

        if (now - trait.lastCartActionAt > switchEvery) {
            if (ThreadLocalRandom.current().nextDouble() < 0.22D) {
                trait.strafeDirection *= -1;
            }
        }

        Vector toTarget = target.getLocation().toVector()
            .subtract(bot.getLocation().toVector());
        toTarget.setY(0.0D);

        if (toTarget.lengthSquared() < 1.0E-5D) {
            return;
        }

        toTarget.normalize();

        Vector tangent = new Vector(-toTarget.getZ(), 0.0D, toTarget.getX())
            .multiply(trait.strafeDirection);

        double amount = plugin.getConfig().getDouble(
            "tactics.humanization.movement-jitter", 0.015D
        );

        Vector current = bot.getVelocity();
        current.setX(current.getX() + tangent.getX() * amount);
        current.setZ(current.getZ() + tangent.getZ() * amount);
        bot.setVelocity(current);
    }

    private boolean tryWindChargeReset(
        Player bot, Player target, BotTrait trait, long now
    ) {
        if (!plugin.getConfig().getBoolean(
            "tactics.wind-charge-reset.enabled", true
        )) {
            return false;
        }

        if (now < trait.windChargeResetCooldownUntil ||
            bot.isOnGround() ||
            bot.isGliding() ||
            bot.getVelocity().getY() > plugin.getConfig().getDouble(
                "tactics.wind-charge-reset.max-upward-velocity", -0.55D
            )) {
            return false;
        }

        double fallDistance = bot.getFallDistance();
        double requiredFall = plugin.getConfig().getDouble(
            "tactics.wind-charge-reset.min-fall-distance", 2.25D
        );

        if (fallDistance < requiredFall) {
            return false;
        }

        ItemStack windCharges = find(bot, Material.WIND_CHARGE);
        if (windCharges == null || windCharges.getAmount() <= 0) {
            return false;
        }

        double maxDistance = plugin.getConfig().getDouble(
            "tactics.wind-charge-reset.max-target-distance", 18.0D
        );

        if (bot.getLocation().distance(target.getLocation()) > maxDistance) {
            return false;
        }

        Location original = bot.getLocation().clone();
        float yaw = original.getYaw();
        float originalPitch = original.getPitch();

        float pluginPitch = (float) plugin.getConfig().getDouble(
            "tactics.wind-charge-reset.plugin-pitch", 82.0D
        );

        bot.setRotation(yaw, Math.max(75.0F, Math.min(90.0F, pluginPitch)));

        Vector downward = new Vector(0.0D, -1.0D, 0.0D);

        try {
            bot.launchProjectile(WindCharge.class, downward);
        } catch (Throwable ex) {
            bot.setRotation(yaw, originalPitch);
            return false;
        }

        windCharges.setAmount(windCharges.getAmount() - 1);

        trait.windChargeResetCooldownUntil = now +
            plugin.getConfig().getLong(
                "tactics.wind-charge-reset.cooldown-ms", 650L
            );

        trait.lastTactic = environment.isWindChargeResetEnabled()
            ? "wind-charge-reset-plugin"
            : "wind-charge-reset-fallback";

        if (environment.isWindChargeResetEnabled()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!bot.isValid() || bot.isDead()) {
                    return;
                }

                // WindChargeReset is server-side and may have already cleared the
                // vertical velocity. Only apply the fallback if the reset did not.
                if (bot.getVelocity().getY() < -0.35D) {
                    Vector velocity = bot.getVelocity();
                    velocity.setY(0.0D);
                    bot.setVelocity(velocity);
                    bot.setFallDistance(0.0F);
                }
            }, 1L);
        } else if (plugin.getConfig().getBoolean(
            "tactics.wind-charge-reset.local-fallback", true
        )) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!bot.isValid() || bot.isDead()) {
                    return;
                }

                Vector velocity = bot.getVelocity();
                if (velocity.getY() < -0.35D) {
                    velocity.setY(0.0D);
                    bot.setVelocity(velocity);
                }
                bot.setFallDistance(0.0F);
            }, 1L);
        }

        return true;
    }

    private boolean tryElytraApproach(
        Player bot, Player target, BotTrait trait, long now, String profile
    ) {
        if (!profile.endsWith("_elytra") ||
            !plugin.getConfig().getBoolean("tactics.elytra.enabled", true)) {
            return false;
        }

        ItemStack chest = bot.getInventory().getChestplate();
        if (chest == null || chest.getType() != Material.ELYTRA) {
            return false;
        }

        double distance = bot.getLocation().distance(target.getLocation());

        if (!bot.isGliding()) {
            if (now < trait.elytraFlightUntil ||
                distance < plugin.getConfig().getDouble("tactics.elytra.start-min-distance", 9.0D) ||
                distance > plugin.getConfig().getDouble("tactics.elytra.start-max-distance", 28.0D)) {
                return false;
            }

            if (!bot.isOnGround()) {
                return false;
            }

            setLookDirection(bot, target.getEyeLocation());

            Vector velocity = bot.getVelocity();
            velocity.setY(plugin.getConfig().getDouble("tactics.elytra.launch-y", 0.58D));
            bot.setVelocity(velocity);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (bot.isValid() && !bot.isDead() && bot.getInventory().getChestplate() != null &&
                    bot.getInventory().getChestplate().getType() == Material.ELYTRA) {
                    bot.setGliding(true);
                }
            }, 1L);

            trait.elytraFlightUntil = now +
                plugin.getConfig().getLong("tactics.elytra.flight-window-ms", 4200L);
            trait.lastTactic = profile.startsWith("mace")
                ? "elytra-mace-launch"
                : "elytra-spear-launch";
            return true;
        }

        setLookDirection(bot, target.getEyeLocation());

        if (now - trait.lastRocketBoostAt >
            plugin.getConfig().getLong("tactics.elytra.rocket-interval-ms", 650L)) {
            ItemStack rocket = find(bot, Material.FIREWORK_ROCKET);

            if (rocket != null && rocket.getAmount() > 0) {
                try {
                    ItemStack boostItem = new ItemStack(Material.FIREWORK_ROCKET);
                    boostItem.setAmount(1);
                    bot.fireworkBoost(boostItem);
                    rocket.setAmount(rocket.getAmount() - 1);
                    trait.lastRocketBoostAt = now;
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        if (distance <= plugin.getConfig().getDouble(
            "tactics.elytra.dive-distance", 4.25D
        )) {
            Vector dive = target.getLocation().toVector()
                .subtract(bot.getLocation().toVector());

            if (dive.lengthSquared() > 1.0E-5D) {
                dive.normalize();

                double downward = profile.startsWith("mace")
                    ? plugin.getConfig().getDouble("tactics.elytra.mace-dive-y", -1.45D)
                    : plugin.getConfig().getDouble("tactics.elytra.spear-dive-y", -0.85D);

                bot.setGliding(false);

                Vector velocity = bot.getVelocity();
                velocity.setX(dive.getX() * 1.5D);
                velocity.setZ(dive.getZ() * 1.5D);
                velocity.setY(downward);
                bot.setVelocity(velocity);

                if (profile.startsWith("mace")) {
                    bot.setFallDistance(Math.max(
                        bot.getFallDistance(),
                        (float) plugin.getConfig().getDouble("tactics.elytra.mace-min-smash-fall", 2.0D)
                    ));
                }

                trait.lastTactic = profile.startsWith("mace")
                    ? "elytra-mace-dive"
                    : "elytra-spear-dive";
            }
        }

        if (now > trait.elytraFlightUntil) {
            bot.setGliding(false);
            trait.lastTactic = "elytra-disengage";
        }

        return true;
    }

    private void setLookDirection(Player player, Location target) {
        Vector direction = target.toVector()
            .subtract(player.getEyeLocation().toVector());

        if (direction.lengthSquared() < 1.0E-5D) {
            return;
        }

        direction.normalize();
        Location view = player.getLocation().clone();
        view.setDirection(direction);
        player.setRotation(view.getYaw(), view.getPitch());
    }

    private ItemStack find(Player player, Material material) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material && item.getAmount() > 0) {
                return item;
            }
        }
        return null;
    }

    private float wrapDegrees(float value) {
        value %= 360.0F;

        if (value >= 180.0F) value -= 360.0F;
        if (value < -180.0F) value += 360.0F;

        return value;
    }
}
