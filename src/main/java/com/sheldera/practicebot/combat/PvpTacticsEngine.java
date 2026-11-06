package com.sheldera.practicebot.combat;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.MaceDifficultyTuning;
import com.sheldera.practicebot.TemplateDifficultyTuning;
import com.sheldera.practicebot.PracticeBotPlugin;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.WindCharge;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public final class PvpTacticsEngine {
    private final PracticeBotPlugin plugin;
    private final PvpEnvironment environment;
    private final CartPvpController cartController;
    private final BowPvpController bowController;

    public PvpTacticsEngine(PracticeBotPlugin plugin) {
        this.plugin = plugin;
        this.environment = new PvpEnvironment(plugin);
        this.cartController = new CartPvpController(plugin);
        this.bowController = new BowPvpController(plugin);

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

    public boolean tick(NPC npc, Player bot, Player target, BotTrait trait, long now) {
        if (bot == null || target == null || trait == null ||
            bot.isDead() || target.isDead() ||
            !bot.getWorld().equals(target.getWorld())) {
            if (bot != null) bowController.cancel(bot);
            return false;
        }

        initializeLatencyProfile(trait);

        if ("bow".equals(trait.getWeaponProfile())) {
            bowController.tick(npc, bot, target, trait, now);
            return true;
        }

        bowController.cancel(bot);

        if (now < trait.tacticNextDecisionAt) {
            return false;
        }

        trait.tacticNextDecisionAt = now + nextDecisionDelay(trait);
        updateHumanAim(bot, target, trait);

        if (tryPearlDisengage(bot, target, trait, now)) {
            return false;
        }

        String profile = trait.getWeaponProfile();

        if (profile.startsWith("spear")) {
            // A Spear-only bot may use the fall-reset tactic. Hybrid Spear/Mace
            // must preserve its fall so it can convert the launch into a smash.
            if (!profile.equals("spear_mace") &&
                tryWindChargeReset(bot, target, trait, now)) return false;
            if (trySpearWindChargeApproach(bot, target, trait, now)) return false;
        }

        if (profile.endsWith("_elytra") &&
            tryElytraApproach(bot, target, trait, now, profile)) {
            return false;
        }

        if ("cart".equals(profile) || "cart_elytra".equals(profile)) {
            cartController.tick(bot, target, trait, now);
            return false;
        }

        applyHumanMovementNoise(bot, target, trait, now);
        return false;
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

    private boolean tryPearlDisengage(
        Player bot,
        Player target,
        BotTrait trait,
        long now
    ) {
        if (!TemplateDifficultyTuning.getBoolean(plugin, trait, "pearl-enabled", true, "tactics.pearl.enabled")) {
            return false;
        }

        if (now < trait.tacticCooldownUntil) {
            return false;
        }

        Attribute maxHealth = Attribute.GENERIC_MAX_HEALTH;
        var maxHealthAttribute = bot.getAttribute(maxHealth);
        double maxHealthValue = maxHealthAttribute == null ? 20.0D : maxHealthAttribute.getValue();
        double healthRatio = maxHealthValue <= 0.0D ? 1.0D : bot.getHealth() / maxHealthValue;

        if (healthRatio > TemplateDifficultyTuning.getDouble(plugin, trait, "pearl-low-health-threshold", 0.35D, "tactics.pearl.low-health-threshold")) {
            return false;
        }

        double distance = bot.getLocation().distance(target.getLocation());
        if (distance < TemplateDifficultyTuning.getDouble(plugin, trait, "pearl-min-distance", 2.5D, "tactics.pearl.min-distance") || distance > TemplateDifficultyTuning.getDouble(plugin, trait, "pearl-max-distance", 12.0D, "tactics.pearl.max-distance")) {
            return false;
        }

        if (ThreadLocalRandom.current().nextDouble(100.0D) >= TemplateDifficultyTuning.getDouble(plugin, trait, "pearl-chance-percent", 45.0D, "tactics.pearl.chance-percent")) {
            return false;
        }

        ItemStack pearl = find(bot, Material.ENDER_PEARL);
        if (pearl == null || pearl.getAmount() <= 0) {
            return false;
        }

        Vector away = bot.getLocation().toVector()
            .subtract(target.getLocation().toVector());
        away.setY(0.12D);

        if (away.lengthSquared() < 1.0E-5D) {
            return false;
        }

        away.normalize();

        try {
            bot.launchProjectile(EnderPearl.class, away);
        } catch (Throwable ex) {
            plugin.debugLog(() -> "Pearl disengage failed: " + ex.getMessage());
            return false;
        }

        pearl.setAmount(pearl.getAmount() - 1);

        trait.lastTactic = "pearl-disengage";
        trait.tacticCooldownUntil = now + TemplateDifficultyTuning.getLong(plugin, trait, "pearl-cooldown-ms", 7000L, "tactics.pearl.cooldown-ms");

        return true;
    }

    private void initializeLatencyProfile(BotTrait trait) {
        int min = Math.max(
            0,
            TemplateDifficultyTuning.getInt(plugin, trait, "ping-min-ms", 35, "tactics.humanization.ping.min-ms")
        );
        int max = Math.max(
            min,
            TemplateDifficultyTuning.getInt(plugin, trait, "ping-max-ms", 180, "tactics.humanization.ping.max-ms")
        );

        if (trait.simulatedPingMs <= 0) {
            // Mean-of-two sampling creates a realistic concentration around the
            // middle rather than producing the same number for every bot.
            int a = ThreadLocalRandom.current().nextInt(min, max + 1);
            int b = ThreadLocalRandom.current().nextInt(min, max + 1);
            trait.simulatedPingMs =
                Math.max(min, Math.min(max, (a + b) / 2));
            trait.lastPingDriftAt = System.currentTimeMillis();
            return;
        }

        long now = System.currentTimeMillis();
        long driftInterval = TemplateDifficultyTuning.getLong(plugin, trait, "ping-drift-interval-ms", 7000L, "tactics.humanization.ping.drift-interval-ms");

        if (driftInterval <= 0L ||
            now - trait.lastPingDriftAt < driftInterval) {
            return;
        }

        int drift = Math.max(
            0,
            TemplateDifficultyTuning.getInt(plugin, trait, "ping-drift-ms", 18, "tactics.humanization.ping.drift-ms")
        );

        int delta = ThreadLocalRandom.current().nextInt(
            -drift, drift + 1
        );

        trait.simulatedPingMs =
            Math.max(min, Math.min(max, trait.simulatedPingMs + delta));
        trait.lastPingDriftAt = now;
    }

    private long nextDecisionDelay(BotTrait trait) {
        int jitter = TemplateDifficultyTuning.getInt(plugin, trait, "ping-jitter-ms", 8, "tactics.humanization.ping.jitter-ms");

        double networkFraction = TemplateDifficultyTuning.getDouble(plugin, trait, "ping-action-latency-fraction", 0.45D, "tactics.humanization.ping.action-latency-fraction");

        double reactionMin = TemplateDifficultyTuning.getDouble(plugin, trait, "reaction-min-ms", 85.0D, "tactics.humanization.reaction.min-ms");

        double reactionMax = TemplateDifficultyTuning.getDouble(plugin, trait, "reaction-max-ms", 210.0D, "tactics.humanization.reaction.max-ms");

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

        double minimumLead = TemplateDifficultyTuning.getDouble(
            plugin, trait, "aim-min-lead-seconds", 0.04D
        );
        double leadFactor = TemplateDifficultyTuning.getDouble(
            plugin, trait, "aim-lead-ping-factor", 0.65D,
            "tactics.humanization.aim.lead-ping-factor"
        );
        double leadSeconds = Math.min(
            0.30D,
            Math.max(
                Math.max(0.0D, minimumLead),
                (trait.simulatedPingMs / 1000.0D) * Math.max(0.0D, leadFactor)
            )
        );

        Vector predicted = target.getEyeLocation().toVector()
            .add(target.getVelocity().clone().multiply(leadSeconds));

        // Human aim is not perfectly centered. Small vertical/horizontal aim
        // offsets represent real target tracking rather than a ray-lock.
        double aimNoise = TemplateDifficultyTuning.getDouble(plugin, trait, "aim-target-offset", 0.10D, "tactics.humanization.aim.target-offset");

        predicted.add(new Vector(
            randomSigned(aimNoise),
            randomSigned(aimNoise * 0.5D),
            randomSigned(aimNoise)
        ));

        Vector delta = predicted.subtract(bot.getEyeLocation().toVector());

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

        double maxTurn = TemplateDifficultyTuning.getDouble(plugin, trait, "aim-max-degrees-per-update", 24.0D, "tactics.humanization.aim.max-degrees-per-update");

        yawDelta = (float) Math.max(-maxTurn, Math.min(maxTurn, yawDelta));

        double pitchDelta = desiredPitch - current.getPitch();
        pitchDelta = Math.max(-maxTurn, Math.min(maxTurn, pitchDelta));

        double jitter = TemplateDifficultyTuning.getDouble(plugin, trait, "aim-jitter-degrees", 0.6D, "tactics.humanization.aim.jitter-degrees");

        float yaw = current.getYaw()
            + (float) (yawDelta * smoothing)
            + (float) randomSigned(jitter);

        float pitch = current.getPitch()
            + (float) (pitchDelta * smoothing)
            + (float) randomSigned(jitter * 0.5D);

        bot.setRotation(yaw, Math.max(-90.0F, Math.min(90.0F, pitch)));
    }

    private void applyHumanMovementNoise(
        Player bot, Player target, BotTrait trait, long now
    ) {
        if (!TemplateDifficultyTuning.getBoolean(plugin, trait, "movement-enabled", true, "tactics.humanization.movement-enabled")) {
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

        long switchEvery = TemplateDifficultyTuning.getLong(plugin, trait, "strafe-switch-ms", 380L, "tactics.humanization.strafe-switch-ms");

        if (now - trait.lastStrafeSwitchAt > switchEvery) {
            if (ThreadLocalRandom.current().nextDouble() < TemplateDifficultyTuning.getDouble(plugin, trait, "strafe-direction-change-chance", 0.22D)) {
                trait.strafeDirection *= -1;
            }
            trait.lastStrafeSwitchAt = now;
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

        double amount = TemplateDifficultyTuning.getDouble(plugin, trait, "movement-jitter", 0.015D, "tactics.humanization.movement-jitter");

        Vector current = bot.getVelocity();
        current.setX(current.getX() + tangent.getX() * amount);
        current.setZ(current.getZ() + tangent.getZ() * amount);
        bot.setVelocity(current);
    }

    private boolean trySpearWindChargeApproach(
        Player bot, Player target, BotTrait trait, long now
    ) {
        if (!plugin.getConfig().getBoolean(
            "modern-weapons.spear.wind-charge-approach.enabled", true
        ) || !bot.isOnGround() || bot.isGliding() ||
            now < trait.windChargeResetCooldownUntil) return false;

        ItemStack held = bot.getInventory().getItemInMainHand();
        if (held == null || !held.getType().name().endsWith("_SPEAR")) return false;

        double distance = bot.getLocation().distance(target.getLocation());
        double minimum = plugin.getConfig().getDouble(
            "modern-weapons.spear.wind-charge-approach.min-distance", 5.0D);
        double maximum = plugin.getConfig().getDouble(
            "modern-weapons.spear.wind-charge-approach.max-distance", 10.0D);
        if (distance < minimum || distance > maximum) return false;

        ItemStack charges = find(bot, Material.WIND_CHARGE);
        if (charges == null || charges.getAmount() <= 0) return false;

        Vector toward = target.getLocation().toVector()
            .subtract(bot.getLocation().toVector());
        toward.setY(0.0D);
        if (toward.lengthSquared() < 1.0E-5D) return false;
        toward.normalize();

        // Put the blast behind the bot so vanilla wind knockback carries it
        // forward into spear charge/Jab range.
        Vector launch = toward.clone().multiply(-0.32D)
            .add(new Vector(0.0D, -1.0D, 0.0D)).normalize();
        Location original = bot.getLocation().clone();
        try {
            bot.setRotation(original.getYaw(), 78.0F);
            WindCharge projectile = bot.launchProjectile(WindCharge.class, launch);
            if (projectile == null || !projectile.isValid()) {
                bot.setRotation(original.getYaw(), original.getPitch());
                return false;
            }
            charges.setAmount(charges.getAmount() - 1);
            trait.spearWindChargeLaunchedAt = now;
        } catch (Throwable ex) {
            bot.setRotation(original.getYaw(), original.getPitch());
            plugin.debugLog(() -> "Spear wind-charge approach failed: " + ex.getMessage());
            return false;
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (bot.isValid() && !bot.isDead()) {
                bot.setRotation(original.getYaw(), original.getPitch());
            }
        }, 1L);
        trait.windChargeResetCooldownUntil = now + plugin.getConfig().getLong(
            "modern-weapons.spear.wind-charge-approach.cooldown-ms", 1800L);
        trait.tacticCooldownUntil = now + 250L;
        trait.lastTactic = "spear-wind-charge-approach";
        bot.setSprinting(true);
        return true;
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
            WindCharge projectile = bot.launchProjectile(WindCharge.class, downward);
            if (projectile == null || !projectile.isValid()) {
                bot.setRotation(yaw, originalPitch);
                return false;
            }
            trait.spearWindChargeLaunchedAt = now;
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
            !TemplateDifficultyTuning.getBoolean(plugin, trait, "elytra-enabled", true, "tactics.elytra.enabled")) {
            return false;
        }

        ItemStack chest = bot.getInventory().getChestplate();
        if (chest == null || chest.getType() != Material.ELYTRA) {
            return false;
        }

        double distance = bot.getLocation().distance(target.getLocation());

        if (!bot.isGliding()) {
            if (now < trait.elytraFlightUntil ||
                distance < TemplateDifficultyTuning.getDouble(plugin, trait, "elytra-start-min-distance", 9.0D, "tactics.elytra.start-min-distance") ||
                distance > TemplateDifficultyTuning.getDouble(plugin, trait, "elytra-start-max-distance", 28.0D, "tactics.elytra.start-max-distance")) {
                return false;
            }

            if (!bot.isOnGround()) {
                return false;
            }

            setLookDirection(bot, target.getEyeLocation());

            Vector velocity = bot.getVelocity();
            velocity.setY(TemplateDifficultyTuning.getDouble(plugin, trait, "elytra-launch-y", 0.58D, "tactics.elytra.launch-y"));
            bot.setVelocity(velocity);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (bot.isValid() && !bot.isDead() && bot.getInventory().getChestplate() != null &&
                    bot.getInventory().getChestplate().getType() == Material.ELYTRA) {
                    bot.setGliding(true);
                }
            }, 1L);

            trait.elytraFlightUntil = now +
                TemplateDifficultyTuning.getLong(plugin, trait, "elytra-flight-window-ms", 4200L, "tactics.elytra.flight-window-ms");
            trait.lastTactic = profile.startsWith("mace")
                ? "elytra-mace-launch"
                : "elytra-spear-launch";
            return true;
        }

        setLookDirection(bot, target.getEyeLocation());

        if (now - trait.lastRocketBoostAt >
            TemplateDifficultyTuning.getLong(plugin, trait, "elytra-rocket-interval-ms", 650L, "tactics.elytra.rocket-interval-ms")) {
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

        if (distance <= TemplateDifficultyTuning.getDouble(plugin, trait, "elytra-dive-distance", 4.25D, "tactics.elytra.dive-distance")) {
            Vector dive = target.getLocation().toVector()
                .subtract(bot.getLocation().toVector());

            if (dive.lengthSquared() > 1.0E-5D) {
                dive.normalize();

                double downward = profile.startsWith("mace")
                    ? TemplateDifficultyTuning.getDouble(plugin, trait, "elytra-mace-dive-y", -1.45D, "tactics.elytra.mace-dive-y")
                    : profile.startsWith("spear")
                        ? TemplateDifficultyTuning.getDouble(plugin, trait, "elytra-spear-dive-y", -0.85D, "tactics.elytra.spear-dive-y")
                        : TemplateDifficultyTuning.getDouble(plugin, trait, "elytra-cart-dive-y", -0.85D);

                bot.setGliding(false);

                Vector velocity = bot.getVelocity();
                velocity.setX(dive.getX() * 1.5D);
                velocity.setZ(dive.getZ() * 1.5D);
                velocity.setY(downward);
                bot.setVelocity(velocity);

                if (profile.startsWith("mace")) {
                    bot.setFallDistance(0.0F);
                    trait.maceLaunchActive = true;
                    trait.maceWindChargePending = false;
                    trait.maceAttemptedThisFall = false;
                    trait.maceMissThisAttack = ThreadLocalRandom.current().nextDouble(100.0D) <
                        MaceDifficultyTuning.getDouble(plugin, trait, "miss-chance-percent", 10.0D);
                    double missOffset = Math.max(0.0D, MaceDifficultyTuning.getDouble(plugin, trait, "miss-offset-degrees", 6.0D));
                    trait.maceMissOffsetDegrees = trait.maceMissThisAttack
                        ? randomSigned(missOffset) : 0.0D;
                    trait.maceLaunchStartedAt = now;
                    trait.maceLaunchOriginY = bot.getLocation().getY();
                    trait.macePeakY = bot.getLocation().getY();
                } else if (profile.startsWith("spear")) {
                    trait.spearChargeStartedAt = now -
                        TemplateDifficultyTuning.getLong(plugin, trait, "spear-charge-delay-ms", 400L, "modern-weapons.spear.charge-delay-ms");
                }

                if (profile.startsWith("mace")) {
                    trait.lastTactic = "elytra-mace-dive";
                } else if (profile.startsWith("spear")) {
                    trait.lastTactic = "elytra-spear-dive";
                } else {
                    trait.lastTactic = "elytra-cart-dive";
                }
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

    private static double randomSigned(double magnitude) {
        double safeMagnitude = Math.max(0.0D, magnitude);
        return safeMagnitude == 0.0D
            ? 0.0D
            : ThreadLocalRandom.current().nextDouble(-safeMagnitude, safeMagnitude);
    }

    private float wrapDegrees(float value) {
        value %= 360.0F;

        if (value >= 180.0F) value -= 360.0F;
        if (value < -180.0F) value += 360.0F;

        return value;
    }
}
