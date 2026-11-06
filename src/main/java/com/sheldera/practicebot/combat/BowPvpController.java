package com.sheldera.practicebot.combat;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import com.sheldera.practicebot.TemplateDifficultyTuning;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/** Native bow-use controller with humanized draw timing, predictive ballistic aim and misses. */
public final class BowPvpController {
    private static final double ARROW_DRAG = 0.99D;
    private static final double ARROW_GRAVITY = 0.05D;
    private static final int MAX_FLIGHT_TICKS = 80;
    private static final double[] DRAG_SUM = new double[MAX_FLIGHT_TICKS + 1];
    private static final double[] GRAVITY_OFFSET = new double[MAX_FLIGHT_TICKS + 1];

    static {
        double drag = 1.0D;
        double verticalVelocity = 0.0D;
        for (int ticks = 1; ticks <= MAX_FLIGHT_TICKS; ticks++) {
            DRAG_SUM[ticks] = DRAG_SUM[ticks - 1] + drag;
            GRAVITY_OFFSET[ticks] = GRAVITY_OFFSET[ticks - 1] + verticalVelocity;
            verticalVelocity = verticalVelocity * ARROW_DRAG - ARROW_GRAVITY;
            drag *= ARROW_DRAG;
        }
    }

    private final PracticeBotPlugin plugin;
    private final Map<UUID, BowSession> sessions = new ConcurrentHashMap<>();
    private final Map<UUID, Long> navigationRefreshAt = new ConcurrentHashMap<>();

    public BowPvpController(PracticeBotPlugin plugin) {
        this.plugin = plugin;
    }

    public void tick(NPC npc, Player bot, Player target, BotTrait trait, long now) {
        if (bot == null || target == null || trait == null || bot.isDead() || target.isDead() ||
            !bot.isValid() || !target.isValid() || !bot.getWorld().equals(target.getWorld())) {
            if (bot != null) {
                cancel(bot);
                navigationRefreshAt.remove(bot.getUniqueId());
            }
            return;
        }

        UUID botId = bot.getUniqueId();
        BowSession session = sessions.get(botId);
        double distance = bot.getLocation().distance(target.getLocation());
        double minRange = configDouble("tactics.bow.min-distance", 5.0D, 2.0D, 20.0D);
        double maxRange = configDouble("tactics.bow.max-distance", 32.0D, 8.0D, 64.0D);
        double preferredRange = configDouble("tactics.bow.preferred-distance", 16.0D, minRange, maxRange);

        if (session != null && session.drawing &&
            (!bot.hasActiveItem() || bot.getActiveItem().getType() != Material.BOW)) {
            restoreBowSlot(bot, session);
            session.drawing = false;
            session.cooldownUntil = now + 250L;
            trait.lastTactic = "bow-draw-interrupted";
        }

        if (distance < minRange) {
            moveAway(npc, bot, target, preferredRange, now);
        } else if (distance > maxRange || !bot.hasLineOfSight(target)) {
            moveCloser(npc, bot, target, preferredRange, now);
        } else {
            strafe(npc, bot, target, trait, distance, now);
        }

        if (session != null && session.drawing) {
            aim(bot, target, session, trait);
            if (bot.getActiveItemUsedTime() >= session.drawTicks) {
                release(bot, trait, session, now);
            }
            return;
        }

        if (session != null && now < session.cooldownUntil) return;
        if (distance < minRange || distance > maxRange || !bot.hasLineOfSight(target)) return;

        int bowSlot = findBowSlot(bot.getInventory());
        if (bowSlot < 0 || !hasArrow(bot.getInventory())) return;
        int previousSlot = bot.getInventory().getHeldItemSlot();
        if (!selectBow(bot, bowSlot)) return;

        int minDraw = configInt("tactics.bow.draw-min-ticks", 18, 10, 40);
        int maxDraw = configInt("tactics.bow.draw-max-ticks", 24, minDraw, 60);
        int drawTicks;
        if (distance >= 20.0D && maxDraw >= 20 && minDraw < 20) {
            drawTicks = randomInt(20, maxDraw);
        } else if (distance < 20.0D && minDraw < 20) {
            drawTicks = randomInt(minDraw, Math.min(maxDraw, 20));
        } else {
            drawTicks = randomInt(minDraw, maxDraw);
        }
        double missChance = TemplateDifficultyTuning.getDouble(plugin, trait, "bow-miss-chance-percent", 14.0D,
            "tactics.bow.miss-chance-percent");
        missChance = Double.isFinite(missChance) ? Math.max(0.0D, Math.min(100.0D, missChance)) : 14.0D;
        boolean intentionalMiss = ThreadLocalRandom.current().nextDouble(100.0D) < missChance;
        double missOffset = TemplateDifficultyTuning.getDouble(plugin, trait, "bow-miss-offset-degrees", 2.6D,
            "tactics.bow.miss-offset-degrees");
        missOffset = Double.isFinite(missOffset) ? Math.max(0.0D, Math.min(15.0D, missOffset)) : 2.6D;
        double missYaw = intentionalMiss ? randomSigned(missOffset) : 0.0D;
        double missPitch = intentionalMiss ? randomSigned(missOffset * 0.55D) : 0.0D;

        BowSession created = new BowSession(drawTicks, previousSlot, bowSlot >= 9 ? bowSlot : -1,
            intentionalMiss, missYaw, missPitch);
        try {
            bot.startUsingItem(EquipmentSlot.HAND);
            if (!bot.hasActiveItem() || bot.getActiveItem().getType() != Material.BOW) {
                throw new IllegalStateException("Paper did not start using the held bow");
            }
        } catch (Throwable failure) {
            try {
                bot.clearActiveItem();
            } catch (Throwable ignored) {
            }
            restoreBowSlot(bot, created);
            created.cooldownUntil = now + 1000L;
            sessions.put(botId, created);
            plugin.debugLog(() -> "PracticeBot native bow draw failed: " + failure.getMessage());
            return;
        }
        created.drawing = true;
        sessions.put(botId, created);
        trait.lastTactic = "bow-draw-start";
    }

    public void cancel(Player bot) {
        if (bot == null) return;
        BowSession session = sessions.remove(bot.getUniqueId());
        if (session != null && session.drawing) {
            if (bot.isValid()) {
                try {
                    bot.clearActiveItem();
                } catch (Throwable ignored) {
                }
            }
            restoreBowSlot(bot, session);
        }
        navigationRefreshAt.remove(bot.getUniqueId());
    }

    private void aim(Player bot, Player target, BowSession session, BotTrait trait) {
        double speed = bowLaunchSpeed(session.drawTicks);
        AimSolution solution = solveAim(bot.getEyeLocation().toVector(), target.getEyeLocation().toVector(),
            target.getVelocity(), bot.getVelocity(), speed);
        if (solution == null) return;

        Location current = bot.getLocation();
        double maxTurn = configDouble("tactics.bow.max-turn-degrees-per-tick", 12.0D, 1.0D, 45.0D);
        double jitter = configDouble("tactics.bow.aim-jitter-degrees", 0.22D, 0.0D, 3.0D)
            + Math.max(0, trait.simulatedPingMs) / 1000.0D * 0.12D;
        float desiredYaw = solution.yaw() + (float) session.missYaw;
        float desiredPitch = solution.pitch() + (float) session.missPitch;
        float yawDelta = wrapDegrees(desiredYaw - current.getYaw());
        float pitchDelta = desiredPitch - current.getPitch();
        float yaw = current.getYaw() + clamp(yawDelta, -maxTurn, maxTurn) + (float) randomSigned(jitter);
        float pitch = current.getPitch() + clamp(pitchDelta, -maxTurn, maxTurn) + (float) randomSigned(jitter * 0.5D);
        bot.setRotation(yaw, Math.max(-90.0F, Math.min(90.0F, pitch)));
    }

    private void release(Player bot, BotTrait trait, BowSession session, long now) {
        boolean released = false;
        try {
            bot.completeUsingActiveItem();
            released = !bot.hasActiveItem();
            if (!released) {
                try {
                    bot.clearActiveItem();
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable failure) {
            plugin.debugLog(() -> "PracticeBot native bow release failed: " + failure.getMessage());
            try {
                bot.clearActiveItem();
            } catch (Throwable ignored) {
            }
        }
        restoreBowSlot(bot, session);
        session.drawing = false;
        long networkDelay = Math.round(Math.max(0, trait.simulatedPingMs) * 0.20D);
        session.cooldownUntil = now + randomLong(
            configLong("tactics.bow.shot-cooldown-min-ms", 650L, 150L, 5000L) + networkDelay,
            configLong("tactics.bow.shot-cooldown-max-ms", 1050L, 150L, 7000L) + networkDelay);
        trait.lastTactic = session.intentionalMiss ? "bow-shot-miss" : "bow-shot";
        if (!released) {
            trait.lastTactic = "bow-release-failed";
            plugin.debugLog("PracticeBot native bow release did not clear the active item state.");
        }
        // The server's native bow release owns arrow creation, enchantments,
        // durability, ammo consumption, velocity, gravity and shoot events.
    }

    private boolean selectBow(Player bot, int bowSlot) {
        PlayerInventory inventory = bot.getInventory();
        int selected = inventory.getHeldItemSlot();
        if (bowSlot >= 0 && bowSlot <= 8) {
            inventory.setHeldItemSlot(bowSlot);
            return true;
        }
        if (bowSlot < 9 || bowSlot >= inventory.getStorageContents().length) return false;
        ItemStack selectedItem = inventory.getItem(selected);
        ItemStack bowItem = inventory.getItem(bowSlot);
        inventory.setItem(selected, bowItem == null ? null : bowItem.clone());
        inventory.setItem(bowSlot, selectedItem == null ? null : selectedItem.clone());
        return true;
    }

    private void restoreBowSlot(Player bot, BowSession session) {
        if (bot == null || session == null) return;
        PlayerInventory inventory = bot.getInventory();
        if (session.swappedSlot >= 9) {
            int selected = session.previousSlot;
            ItemStack currentBow = inventory.getItem(selected);
            ItemStack storedItem = inventory.getItem(session.swappedSlot);
            inventory.setItem(session.swappedSlot, currentBow == null ? null : currentBow.clone());
            inventory.setItem(selected, storedItem == null ? null : storedItem.clone());
        }
        if (session.previousSlot >= 0 && session.previousSlot <= 8) {
            inventory.setHeldItemSlot(session.previousSlot);
        }
    }

    private void moveCloser(NPC npc, Player bot, Player target, double preferredRange, long now) {
        if (npc == null || !npc.isSpawned()) return;
        Navigator navigator = npc.getNavigator();
        navigator.getLocalParameters().range((float) Math.max(2.0D, preferredRange - 1.0D)).speedModifier(1.0F);
        if (shouldRefreshNavigation(bot.getUniqueId(), navigator, now)) {
            navigator.cancelNavigation();
            navigator.setTarget(target, true);
        }
    }

    private void moveAway(NPC npc, Player bot, Player target, double preferredRange, long now) {
        if (npc == null || !npc.isSpawned()) return;
        Navigator navigator = npc.getNavigator();
        Vector away = bot.getLocation().toVector().subtract(target.getLocation().toVector());
        away.setY(0.0D);
        if (away.lengthSquared() < 1.0E-5D) away = new Vector(1.0D, 0.0D, 0.0D);
        away.normalize().multiply(Math.max(2.0D, preferredRange - bot.getLocation().distance(target.getLocation())));
        Location retreat = bot.getLocation().clone().add(away);
        navigator.getLocalParameters().range(0.5F).speedModifier(1.0F);
        if (shouldRefreshNavigation(bot.getUniqueId(), navigator, now)) {
            navigator.cancelNavigation();
            navigator.setTarget(retreat);
        }
    }

    private boolean shouldRefreshNavigation(UUID botId, Navigator navigator, long now) {
        Long next = navigationRefreshAt.get(botId);
        if (!navigator.isNavigating() || next == null || now >= next) {
            navigationRefreshAt.put(botId, now + 750L);
            return true;
        }
        return false;
    }

    private void strafe(NPC npc, Player bot, Player target, BotTrait trait, double distance, long now) {
        if (npc != null && npc.isSpawned()) npc.getNavigator().cancelNavigation();
        if (now - trait.lastStrafeSwitchAt > configLong("tactics.bow.strafe-switch-ms", 1100L, 250L, 5000L)) {
            if (ThreadLocalRandom.current().nextDouble() < 0.28D) trait.strafeDirection *= -1;
            trait.lastStrafeSwitchAt = now;
        }
        Vector toward = target.getLocation().toVector().subtract(bot.getLocation().toVector());
        toward.setY(0.0D);
        if (toward.lengthSquared() < 1.0E-5D) return;
        toward.normalize();
        Vector tangent = new Vector(-toward.getZ(), 0.0D, toward.getX()).multiply(trait.strafeDirection);
        double radial = distance < 13.0D ? -0.035D : distance > 20.0D ? 0.025D : 0.0D;
        Vector desired = tangent.multiply(configDouble("tactics.bow.strafe-speed", 0.12D, 0.02D, 0.35D))
            .add(toward.multiply(radial));
        Vector velocity = bot.getVelocity();
        velocity.setX(velocity.getX() * 0.55D + desired.getX() * 0.45D);
        velocity.setZ(velocity.getZ() * 0.55D + desired.getZ() * 0.45D);
        bot.setVelocity(velocity);
    }

    static double bowLaunchSpeed(int drawTicks) {
        double charge = Math.max(0.0D, Math.min(1.0D, drawTicks / 20.0D));
        double force = (charge * charge + 2.0D * charge) / 3.0D;
        return force * 3.0D;
    }

    static AimSolution solveAim(Vector origin, Vector target, Vector targetVelocity,
                                Vector shooterVelocity, double launchSpeed) {
        if (origin == null || target == null || targetVelocity == null || shooterVelocity == null ||
            !Double.isFinite(launchSpeed) || launchSpeed <= 0.05D) return null;

        AimSolution best = null;
        double bestSpeedError = Double.POSITIVE_INFINITY;
        for (int ticks = 1; ticks <= MAX_FLIGHT_TICKS; ticks++) {
            Vector future = target.clone().add(targetVelocity.clone().multiply(ticks));
            Vector delta = future.subtract(origin);
            double horizontal = Math.hypot(delta.getX(), delta.getZ());
            double dragSum = DRAG_SUM[ticks];
            double gravityOffset = GRAVITY_OFFSET[ticks];
            if (dragSum <= 0.0D) continue;
            // Vanilla arrows inherit 10% of the shooter's current velocity.
            // Remove that contribution before solving the bow's own launch vector.
            double vx = delta.getX() / dragSum - shooterVelocity.getX() * 0.1D;
            double vz = delta.getZ() / dragSum - shooterVelocity.getZ() * 0.1D;
            double vy = (delta.getY() - gravityOffset) / dragSum - shooterVelocity.getY() * 0.1D;
            double requiredSpeed = Math.sqrt(vx * vx + vy * vy + vz * vz);
            double speedError = Math.abs(requiredSpeed - launchSpeed);
            if (speedError < bestSpeedError) {
                bestSpeedError = speedError;
                double yaw = Math.toDegrees(Math.atan2(-vx, vz));
                double pitch = -Math.toDegrees(Math.atan2(vy, horizontal <= 1.0E-8D ? 0.0D : Math.hypot(vx, vz)));
                best = new AimSolution((float) yaw, (float) pitch, ticks, speedError);
            }
        }
        return best;
    }

    private int findBowSlot(PlayerInventory inventory) {
        for (int slot = 0; slot < inventory.getStorageContents().length; slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item != null && item.getType() == Material.BOW && item.getAmount() > 0) return slot;
        }
        return -1;
    }

    private boolean hasArrow(PlayerInventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getAmount() <= 0) continue;
            Material type = item.getType();
            if (type == Material.ARROW || type == Material.TIPPED_ARROW || type == Material.SPECTRAL_ARROW) return true;
        }
        return false;
    }

    private int configInt(String path, int fallback, int min, int max) {
        return Math.max(min, Math.min(max, plugin.getConfig().getInt(path, fallback)));
    }

    private long configLong(String path, long fallback, long min, long max) {
        return Math.max(min, Math.min(max, plugin.getConfig().getLong(path, fallback)));
    }

    private double configDouble(String path, double fallback, double min, double max) {
        double value = plugin.getConfig().getDouble(path, fallback);
        return Double.isFinite(value) ? Math.max(min, Math.min(max, value)) : fallback;
    }

    private static int randomInt(int min, int max) {
        return min >= max ? min : ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private static long randomLong(long min, long max) {
        return min >= max ? min : ThreadLocalRandom.current().nextLong(min, max + 1L);
    }

    private static double randomSigned(double magnitude) {
        return magnitude <= 0.0D ? 0.0D : ThreadLocalRandom.current().nextDouble(-magnitude, magnitude);
    }

    private static float clamp(float value, double min, double max) {
        return (float) Math.max(min, Math.min(max, value));
    }

    private static float wrapDegrees(float value) {
        value %= 360.0F;
        if (value >= 180.0F) value -= 360.0F;
        if (value < -180.0F) value += 360.0F;
        return value;
    }

    record AimSolution(float yaw, float pitch, int flightTicks, double speedError) { }

    private static final class BowSession {
        private final int drawTicks;
        private final int previousSlot;
        private final int swappedSlot;
        private final boolean intentionalMiss;
        private final double missYaw;
        private final double missPitch;
        private boolean drawing;
        private long cooldownUntil;

        private BowSession(int drawTicks, int previousSlot, int swappedSlot,
                           boolean intentionalMiss, double missYaw, double missPitch) {
            this.drawTicks = drawTicks;
            this.previousSlot = previousSlot;
            this.swappedSlot = swappedSlot;
            this.intentionalMiss = intentionalMiss;
            this.missYaw = missYaw;
            this.missPitch = missPitch;
        }
    }
}
