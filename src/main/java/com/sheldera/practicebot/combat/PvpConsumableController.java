package com.sheldera.practicebot.combat;

import com.sheldera.practicebot.BotTrait;
import com.sheldera.practicebot.PracticeBotPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/** Native food and potion use for Citizens player NPCs. */
public final class PvpConsumableController {
    private static final int FOOD_USE_TICKS = 32;
    private static final int POTION_USE_TICKS = 32;
    private static final List<PotionEffectType> BUFFS = List.of(
        PotionEffectType.SPEED, PotionEffectType.STRENGTH, PotionEffectType.FIRE_RESISTANCE,
        PotionEffectType.RESISTANCE, PotionEffectType.REGENERATION, PotionEffectType.ABSORPTION,
        PotionEffectType.JUMP_BOOST
    );
    private static final List<PotionEffectType> DEBUFFS = List.of(
        PotionEffectType.POISON, PotionEffectType.SLOWNESS, PotionEffectType.WEAKNESS,
        PotionEffectType.INSTANT_DAMAGE
    );

    private final PracticeBotPlugin plugin;
    private final Map<UUID, UseSession> sessions = new ConcurrentHashMap<>();
    private final Map<UUID, Long> potionCooldowns = new ConcurrentHashMap<>();

    public PvpConsumableController(PracticeBotPlugin plugin) {
        this.plugin = plugin;
    }

    /** Returns true while a consumable is being used or an instant potion was thrown. */
    public boolean tick(Player bot, Player target, BotTrait trait, long now) {
        if (bot == null || trait == null || !bot.isValid() || bot.isDead()) {
            if (bot != null) cancel(bot);
            return false;
        }
        if (!plugin.getConfig().getBoolean("tactics.consumables.enabled", true)) return false;

        UUID id = bot.getUniqueId();
        UseSession active = sessions.get(id);
        if (active != null) return tickActiveUse(bot, trait, active, now);

        // Do not interrupt a bow draw, spear charge, shield block, or another
        // native item action owned by the combat controller.
        if (bot.hasActiveItem()) return false;

        if (tryGoldenApple(bot, trait, now)) return true;
        if (tryHealingPotion(bot, trait, now)) return true;
        if (tryBuffPotion(bot, target, trait, now)) return true;
        if (tryDebuffPotion(bot, target, trait, now)) return true;
        return tryEat(bot, trait, now);
    }

    public void cancel(Player bot) {
        if (bot == null) return;
        UseSession session = sessions.remove(bot.getUniqueId());
        if (session != null) {
            if (bot.isValid()) NmsCombatBridge.stopUsingItem(bot);
            restoreSlot(bot, session);
        }
        potionCooldowns.remove(bot.getUniqueId());
    }

    private boolean tickActiveUse(Player bot, BotTrait trait, UseSession session, long now) {
        if (!bot.hasActiveItem() || bot.getActiveItem() == null ||
            bot.getActiveItem().getType() != session.material) {
            finishUse(bot, session);
            return true;
        }
        if (bot.getActiveItemUsedTime() >= session.useTicks) {
            try {
                bot.completeUsingActiveItem();
            } catch (Throwable failure) {
                plugin.debugLog(() -> "PracticeBot could not complete " + session.kind + " use: " + failure.getMessage());
                bot.clearActiveItem();
            }
            finishUse(bot, session);
            trait.lastTactic = session.kind + "-used";
            return true;
        }
        return true;
    }

    private boolean tryEat(Player bot, BotTrait trait, long now) {
        int foodThreshold = Math.max(1, Math.min(19,
            plugin.getConfig().getInt("tactics.consumables.food-level-threshold", 14)));
        double saturationThreshold = Math.max(0.0D,
            plugin.getConfig().getDouble("tactics.consumables.saturation-threshold", 1.0D));
        if (!PvpConsumablePolicy.shouldEat(bot.getFoodLevel(), bot.getSaturation(), foodThreshold, saturationThreshold)) return false;

        int slot = findSlot(bot, item -> isEdible(item) &&
            item.getType() != Material.GOLDEN_APPLE && item.getType() != Material.ENCHANTED_GOLDEN_APPLE);
        if (slot < 0) return false;
        return beginTimedUse(bot, slot, FOOD_USE_TICKS, "eat", trait, now, 900L);
    }

    private boolean tryGoldenApple(Player bot, BotTrait trait, long now) {
        double maxHealth = maxHealth(bot);
        double ratio = maxHealth <= 0.0D ? 1.0D : bot.getHealth() / maxHealth;
        if (ratio > plugin.getConfig().getDouble("tactics.consumables.golden-apple-health-ratio", 0.72D)) return false;
        if (bot.hasPotionEffect(PotionEffectType.ABSORPTION) && bot.hasPotionEffect(PotionEffectType.REGENERATION)) return false;
        int slot = findSlot(bot, item -> item.getType() == Material.ENCHANTED_GOLDEN_APPLE || item.getType() == Material.GOLDEN_APPLE);
        return slot >= 0 && beginTimedUse(bot, slot, FOOD_USE_TICKS, "golden-apple", trait, now, 2500L);
    }

    private boolean tryHealingPotion(Player bot, BotTrait trait, long now) {
        double maxHealth = maxHealth(bot);
        double ratio = maxHealth <= 0.0D ? 1.0D : bot.getHealth() / maxHealth;
        double threshold = plugin.getConfig().getDouble("tactics.consumables.heal-health-ratio", 0.62D);
        if (ratio > threshold || now < potionCooldowns.getOrDefault(bot.getUniqueId(), 0L)) return false;

        for (int slot = 0; slot < 36; slot++) {
            ItemStack item = bot.getInventory().getItem(slot);
            if (!isPotion(item)) continue;
            List<PotionEffect> effects = potionEffects(item);
            boolean heals = effects.stream().anyMatch(effect ->
                effect.getType().equals(PotionEffectType.INSTANT_HEALTH) ||
                effect.getType().equals(PotionEffectType.REGENERATION));
            if (!heals) continue;
            if (item.getType() == Material.SPLASH_POTION) {
                if (throwPotion(bot, slot, bot.getLocation().clone().add(0.0D, 0.15D, 0.0D), 82.0F, "potion-heal", trait)) {
                    potionCooldowns.put(bot.getUniqueId(), now + 1800L);
                    return true;
                }
            } else if (item.getType() == Material.POTION &&
                beginTimedUse(bot, slot, POTION_USE_TICKS, "potion-heal", trait, now, 1800L)) {
                return true;
            }
        }
        return false;
    }

    private boolean tryBuffPotion(Player bot, Player target, BotTrait trait, long now) {
        if (!bot.isOnGround() || (target != null && bot.getLocation().distance(target.getLocation()) <
            plugin.getConfig().getDouble("tactics.consumables.buff-safe-distance", 8.0D))) return false;
        if (now < potionCooldowns.getOrDefault(bot.getUniqueId(), 0L)) return false;

        for (int slot = 0; slot < 36; slot++) {
            ItemStack item = bot.getInventory().getItem(slot);
            if (!isPotion(item)) continue;
            List<PotionEffect> effects = potionEffects(item);
            boolean useful = effects.stream().anyMatch(effect ->
                BUFFS.contains(effect.getType()) && !bot.hasPotionEffect(effect.getType()));
            if (!useful) continue;
            if (item.getType() == Material.POTION &&
                beginTimedUse(bot, slot, POTION_USE_TICKS, "potion-buff", trait, now, 5000L)) return true;
            if (item.getType() == Material.SPLASH_POTION &&
                throwPotion(bot, slot, bot.getLocation().clone().add(0.0D, 0.15D, 0.0D), 82.0F, "potion-buff", trait)) {
                potionCooldowns.put(bot.getUniqueId(), now + 5000L);
                return true;
            }
        }
        return false;
    }

    private boolean tryDebuffPotion(Player bot, Player target, BotTrait trait, long now) {
        if (target == null || target.isDead() || !target.isValid() ||
            !bot.getWorld().equals(target.getWorld()) || !bot.hasLineOfSight(target)) return false;
        double distance = bot.getLocation().distance(target.getLocation());
        if (distance < 3.5D || distance > plugin.getConfig().getDouble("tactics.consumables.debuff-max-distance", 8.0D)) return false;
        if (now < potionCooldowns.getOrDefault(bot.getUniqueId(), 0L)) return false;
        if (ThreadLocalRandom.current().nextDouble(100.0D) >=
            plugin.getConfig().getDouble("tactics.consumables.debuff-chance-percent", 35.0D)) return false;

        for (int slot = 0; slot < 36; slot++) {
            ItemStack item = bot.getInventory().getItem(slot);
            if (item == null || item.getType() != Material.SPLASH_POTION) continue;
            boolean useful = potionEffects(item).stream().anyMatch(effect ->
                DEBUFFS.contains(effect.getType()) && !target.hasPotionEffect(effect.getType()));
            if (!useful) continue;
            Location impact = target.getLocation().clone().add(0.0D, Math.max(0.7D, target.getHeight() * 0.55D), 0.0D);
            if (throwPotion(bot, slot, impact, null, "potion-debuff", trait)) {
                potionCooldowns.put(bot.getUniqueId(), now + 6500L);
                return true;
            }
        }
        return false;
    }

    private boolean beginTimedUse(Player bot, int itemSlot, int useTicks, String kind,
                                  BotTrait trait, long now, long cooldown) {
        UseSession session = selectIntoMainHand(bot, itemSlot, useTicks, kind);
        if (session == null) return false;
        if (!NmsCombatBridge.startUsingMainHand(bot) || !bot.hasActiveItem()) {
            restoreSlot(bot, session);
            plugin.debugLog(() -> "PracticeBot failed to start native " + kind + " item use: " + NmsCombatBridge.lastFailure());
            return false;
        }
        sessions.put(bot.getUniqueId(), session);
        if (kind.startsWith("potion")) potionCooldowns.put(bot.getUniqueId(), now + cooldown);
        trait.lastTactic = kind + "-start";
        return true;
    }

    private boolean throwPotion(Player bot, int itemSlot, Location aimAt, Float forcedPitch,
                               String kind, BotTrait trait) {
        Location original = bot.getLocation().clone();
        UseSession session = selectIntoMainHand(bot, itemSlot, 0, kind);
        if (session == null) return false;
        Location aim = bot.getLocation().clone();
        if (forcedPitch != null) {
            aim.setPitch(forcedPitch);
        } else {
            Vector direction = aimAt.toVector().subtract(bot.getEyeLocation().toVector());
            if (direction.lengthSquared() < 1.0E-5D) {
                restoreSlot(bot, session);
                return false;
            }
            aim.setDirection(direction);
        }
        bot.setRotation(aim.getYaw(), aim.getPitch());
        bot.swingMainHand();
        boolean used = NmsCombatBridge.useMainHand(bot);
        bot.setRotation(original.getYaw(), original.getPitch());
        restoreSlot(bot, session);
        if (used) trait.lastTactic = kind;
        else plugin.debugLog(() -> "PracticeBot failed to throw " + kind + ": " + NmsCombatBridge.lastFailure());
        return used;
    }

    private UseSession selectIntoMainHand(Player bot, int itemSlot, int useTicks, String kind) {
        if (itemSlot < 0 || itemSlot >= 36) return null;
        PlayerInventory inventory = bot.getInventory();
        int previousSlot = inventory.getHeldItemSlot();
        ItemStack wanted = inventory.getItem(itemSlot);
        if (wanted == null || wanted.getType().isAir()) return null;
        boolean swapped = itemSlot >= 9;
        if (swapped) {
            ItemStack displaced = inventory.getItem(previousSlot);
            inventory.setItem(previousSlot, wanted);
            inventory.setItem(itemSlot, displaced);
        } else {
            inventory.setHeldItemSlot(itemSlot);
        }
        return new UseSession(previousSlot, itemSlot, swapped, inventory.getItemInMainHand().getType(), useTicks, kind);
    }

    private void finishUse(Player bot, UseSession session) {
        sessions.remove(bot.getUniqueId());
        restoreSlot(bot, session);
        bot.updateInventory();
    }

    private void restoreSlot(Player bot, UseSession session) {
        if (bot == null || session == null || !bot.isValid()) return;
        PlayerInventory inventory = bot.getInventory();
        if (session.swapped) {
            ItemStack consumedOrRemaining = inventory.getItem(session.previousSlot);
            ItemStack displaced = inventory.getItem(session.itemSlot);
            inventory.setItem(session.itemSlot, consumedOrRemaining);
            inventory.setItem(session.previousSlot, displaced);
        } else {
            inventory.setHeldItemSlot(session.previousSlot);
        }
    }

    private int findSlot(Player bot, java.util.function.Predicate<ItemStack> predicate) {
        ItemStack[] contents = bot.getInventory().getStorageContents();
        int selected = -1;
        int bestRank = Integer.MIN_VALUE;
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack item = contents[slot];
            if (item == null || item.getType().isAir() || !predicate.test(item)) continue;
            int rank = foodRank(item);
            if (rank > bestRank) {
                selected = slot;
                bestRank = rank;
            }
        }
        return selected;
    }

    private boolean isEdible(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;
        if (item.getType().isEdible()) return true;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasFood();
    }

    private int foodRank(ItemStack item) {
        Material material = item.getType();
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasFood()) {
            FoodComponent food = meta.getFood();
            return 50 + food.getNutrition() * 2 + Math.round(food.getSaturation() * 5.0F);
        }
        return switch (material) {
            case ENCHANTED_GOLDEN_APPLE -> 100;
            case GOLDEN_APPLE -> 95;
            case GOLDEN_CARROT -> 90;
            case COOKED_BEEF, COOKED_PORKCHOP -> 85;
            case COOKED_MUTTON, COOKED_SALMON, COOKED_COD -> 80;
            case BAKED_POTATO, BREAD, COOKED_CHICKEN -> 70;
            default -> 10;
        };
    }

    private boolean isPotion(ItemStack item) {
        return item != null && (item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION)
            && item.getItemMeta() instanceof PotionMeta;
    }

    private List<PotionEffect> potionEffects(ItemStack item) {
        if (!(item.getItemMeta() instanceof PotionMeta meta)) return List.of();
        java.util.ArrayList<PotionEffect> effects = new java.util.ArrayList<>();
        PotionType baseType = meta.getBasePotionType();
        if (baseType != null) effects.addAll(baseType.getPotionEffects());
        effects.addAll(meta.getCustomEffects());
        return effects;
    }

    private double maxHealth(Player bot) {
        AttributeInstance attribute = bot.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        return attribute == null ? 20.0D : attribute.getValue();
    }

    private record UseSession(int previousSlot, int itemSlot, boolean swapped,
                              Material material, int useTicks, String kind) { }
}
