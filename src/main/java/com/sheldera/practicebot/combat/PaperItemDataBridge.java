package com.sheldera.practicebot.combat;

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class PaperItemDataBridge {
    public record KineticData(
        long delayTicks,
        long contactCooldownTicks,
        double damageMultiplier,
        Condition damage,
        Condition knockback,
        Condition dismount,
        double forwardMovement
    ) {}

    public record Condition(
        long maxDurationTicks,
        double minSpeed,
        double minRelativeSpeed
    ) {}

    public record AttackRangeData(
        double minReach,
        double maxReach,
        double minCreativeReach,
        double maxCreativeReach,
        double hitboxMargin,
        double mobFactor
    ) {}

    private static final Method GET_DATA;
    private static final Object KINETIC_TYPE;
    private static final Object ATTACK_RANGE_TYPE;

    static {
        Method getData = null;
        Object kinetic = null;
        Object attackRange = null;

        try {
            Class<?> componentType = Class.forName(
                "io.papermc.paper.datacomponent.DataComponentType$Valued"
            );

            getData = ItemStack.class.getMethod(
                "getData", componentType
            );

            Class<?> types = Class.forName(
                "io.papermc.paper.datacomponent.DataComponentTypes"
            );

            Field kineticField = types.getField("KINETIC_WEAPON");
            Field rangeField = types.getField("ATTACK_RANGE");

            kinetic = kineticField.get(null);
            attackRange = rangeField.get(null);
        } catch (Throwable ignored) {
            // Paper 1.21.1 and older do not expose the modern component API.
        }

        GET_DATA = getData;
        KINETIC_TYPE = kinetic;
        ATTACK_RANGE_TYPE = attackRange;
    }

    private PaperItemDataBridge() {
    }

    public static boolean available() {
        return GET_DATA != null &&
            KINETIC_TYPE != null &&
            ATTACK_RANGE_TYPE != null;
    }

    public static KineticData kineticWeapon(ItemStack item) {
        if (GET_DATA == null || KINETIC_TYPE == null || item == null) {
            return null;
        }

        try {
            Object kinetic = GET_DATA.invoke(item, KINETIC_TYPE);
            if (kinetic == null) {
                return null;
            }

            long delay = number(kinetic, "delayTicks");
            long cooldown = number(kinetic, "contactCooldownTicks");
            double multiplier = decimal(kinetic, "damageMultiplier");
            double forwardMovement = decimal(kinetic, "forwardMovement");

            Condition damage = condition(kinetic, "damageConditions");
            Condition knockback = condition(kinetic, "knockbackConditions");
            Condition dismount = condition(kinetic, "dismountConditions");

            return new KineticData(
                delay,
                cooldown,
                multiplier,
                damage,
                knockback,
                dismount,
                forwardMovement
            );
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static AttackRangeData attackRange(ItemStack item) {
        if (GET_DATA == null || ATTACK_RANGE_TYPE == null || item == null) {
            return null;
        }

        try {
            Object range = GET_DATA.invoke(item, ATTACK_RANGE_TYPE);
            if (range == null) {
                return null;
            }

            return new AttackRangeData(
                decimal(range, "minReach"),
                decimal(range, "maxReach"),
                decimal(range, "minCreativeReach"),
                decimal(range, "maxCreativeReach"),
                decimal(range, "hitboxMargin"),
                decimal(range, "mobFactor")
            );
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Condition condition(
        Object kinetic,
        String methodName
    ) throws ReflectiveOperationException {
        Method method = kinetic.getClass().getMethod(methodName);
        Object value = method.invoke(kinetic);

        if (value == null) {
            return null;
        }

        return new Condition(
            number(value, "maxDurationTicks"),
            decimal(value, "minSpeed"),
            decimal(value, "minRelativeSpeed")
        );
    }

    private static long number(
        Object instance,
        String methodName
    ) throws ReflectiveOperationException {
        Object value = instance.getClass()
            .getMethod(methodName)
            .invoke(instance);

        if (value instanceof Number number) {
            return number.longValue();
        }

        throw new IllegalStateException(
            "Expected numeric component value from " + methodName
        );
    }

    private static double decimal(
        Object instance,
        String methodName
    ) throws ReflectiveOperationException {
        Object value = instance.getClass()
            .getMethod(methodName)
            .invoke(instance);

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        throw new IllegalStateException(
            "Expected numeric component value from " + methodName
        );
    }
}
