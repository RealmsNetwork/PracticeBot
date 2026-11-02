package com.sheldera.practicebot.combat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Dispatches the same serverbound packet handlers used by vanilla clients for
 * entity attacks, spear Jabs, and item use. Reflection keeps the plugin
 * compatible with Paper builds whose Mojang-mapped NMS signatures evolve.
 */
public final class NmsCombatBridge {
    private static volatile String lastFailure;

    private NmsCombatBridge() {
    }

    /** Runs the vanilla entity-attack packet handler (mace damage/smash included). */
    public static boolean attack(Player attacker, Entity target) {
        if (attacker == null || target == null || attacker.isDead() || target.isDead()) return false;
        Object playerHandle = null;
        Object targetHandle = null;
        Throwable packetFailure = null;
        try {
            playerHandle = handle(attacker);
            targetHandle = handle(target);
            Object connection = connection(playerHandle);
            ClassLoader loader = playerHandle.getClass().getClassLoader();
            Class<?> packetType = Class.forName("net.minecraft.network.protocol.game.ServerboundInteractPacket", true, loader);
            Method factory = findStaticAttackPacketFactory(packetType, targetHandle);
            if (factory == null) throw new NoSuchMethodException("ServerboundInteractPacket.createAttackPacket(Entity, boolean)");
            Object packet = factory.invoke(null, targetHandle, attacker.isSneaking());
            invokeCompatible(connection, "handleInteract", packet);
            lastFailure = null;
            return true;
        } catch (Throwable throwable) {
            packetFailure = throwable;
        }

        // Citizens NPC connections can be packetless on some server forks.
        // In that case retain vanilla Player.attack() as a native fallback;
        // callers still perform their own ray/reach checks before invoking us.
        try {
            if (playerHandle == null) playerHandle = handle(attacker);
            if (targetHandle == null) targetHandle = handle(target);
            Method attack = findCompatibleMethod(playerHandle.getClass(), "attack", targetHandle);
            if (attack == null) throw new NoSuchMethodException("ServerPlayer.attack(Entity)");
            attack.invoke(playerHandle, targetHandle);
            lastFailure = null;
            return true;
        } catch (Throwable fallbackFailure) {
            lastFailure = rootMessage(fallbackFailure == null ? packetFailure : fallbackFailure);
            return false;
        }
    }

    /**
     * Sends the vanilla STAB action. This is deliberately not Player.attack:
     * 1.21.11 routes piercing weapons through ServerboundPlayerActionPacket.STAB
     * and Player.stabAttack(), which owns reach, piercing, Lunge and effects.
     */
    public static boolean stabAttack(Player player) {
        if (player == null || player.isDead()) return false;
        try {
            Object handle = handle(player);
            Object connection = connection(handle);
            dispatchPlayerAction(connection, handle.getClass().getClassLoader(), "STAB");
            lastFailure = null;
            return true;
        } catch (Throwable throwable) {
            lastFailure = rootMessage(throwable);
            return false;
        }
    }

    /** Starts right-click item use through the server's normal packet handler. */
    public static boolean startUsingMainHand(Player player) {
        if (player == null || player.isDead()) return false;
        try {
            Object handle = handle(player);
            if (isUsingItem(handle)) return true;
            Object connection = connection(handle);
            ClassLoader loader = handle.getClass().getClassLoader();
            Class<?> handType = Class.forName("net.minecraft.world.InteractionHand", true, loader);
            @SuppressWarnings({"rawtypes", "unchecked"})
            Object mainHand = Enum.valueOf((Class<? extends Enum>) handType.asSubclass(Enum.class), "MAIN_HAND");
            Location location = player.getLocation();
            Object packet = createUseItemPacket(loader, mainHand, location.getYaw(), location.getPitch());
            invokeCompatible(connection, "handleUseItem", packet);

            // A few NPC connection implementations do not forward packets to
            // their listener. Fall back to the same ServerPlayerGameMode method
            // that the vanilla handler calls, not a custom spear simulation.
            if (!isUsingItem(handle)) startUsingViaGameMode(handle, mainHand);
            if (!isUsingItem(handle)) throw new IllegalStateException("Native item-use handler did not enter the using-item state.");
            lastFailure = null;
            return true;
        } catch (Throwable throwable) {
            lastFailure = rootMessage(throwable);
            return false;
        }
    }

    /** Releases the held item through RELEASE_USE_ITEM, then the native release method if needed. */
    public static boolean stopUsingItem(Player player) {
        if (player == null) return false;
        try {
            Object handle = handle(player);
            if (!isUsingItem(handle)) return true;
            try {
                dispatchPlayerAction(connection(handle), handle.getClass().getClassLoader(), "RELEASE_USE_ITEM");
            } catch (Throwable ignored) {
            }
            if (isUsingItem(handle)) {
                Method release = findNoArgsMethod(handle.getClass(), "releaseUsingItem");
                if (release == null) throw new NoSuchMethodException("LivingEntity.releaseUsingItem()");
                release.invoke(handle);
            }
            lastFailure = null;
            return !isUsingItem(handle);
        } catch (Throwable throwable) {
            lastFailure = rootMessage(throwable);
            return false;
        }
    }

    public static boolean isUsingItem(Player player) {
        if (player == null) return false;
        try {
            return isUsingItem(handle(player));
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static String lastFailure() {
        return lastFailure;
    }

    private static void dispatchPlayerAction(Object connection, ClassLoader loader, String actionName) throws ReflectiveOperationException {
        Class<?> packetType = Class.forName("net.minecraft.network.protocol.game.ServerboundPlayerActionPacket", true, loader);
        Class<?> actionType = Class.forName("net.minecraft.network.protocol.game.ServerboundPlayerActionPacket$Action", true, loader);
        @SuppressWarnings({"rawtypes", "unchecked"})
        Object action = Enum.valueOf((Class<? extends Enum>) actionType.asSubclass(Enum.class), actionName);
        Class<?> blockPosType = Class.forName("net.minecraft.core.BlockPos", true, loader);
        Class<?> directionType = Class.forName("net.minecraft.core.Direction", true, loader);
        Object zero = blockPosType.getField("ZERO").get(null);
        @SuppressWarnings({"rawtypes", "unchecked"})
        Object down = Enum.valueOf((Class<? extends Enum>) directionType.asSubclass(Enum.class), "DOWN");
        Object packet = constructPlayerActionPacket(packetType, action, zero, down);
        invokeCompatible(connection, "handlePlayerAction", packet);
    }

    private static Object constructPlayerActionPacket(Class<?> packetType, Object action, Object zero, Object down) throws ReflectiveOperationException {
        for (Constructor<?> constructor : packetType.getConstructors()) {
            Class<?>[] p = constructor.getParameterTypes();
            if (p.length == 4 && p[0].isInstance(action) && p[1].isInstance(zero) && p[2].isInstance(down) && p[3] == int.class) {
                return constructor.newInstance(action, zero, down, 0);
            }
        }
        for (Constructor<?> constructor : packetType.getConstructors()) {
            Class<?>[] p = constructor.getParameterTypes();
            if (p.length == 3 && p[0].isInstance(action) && p[1].isInstance(zero) && p[2].isInstance(down)) {
                return constructor.newInstance(action, zero, down);
            }
        }
        throw new NoSuchMethodException("ServerboundPlayerActionPacket(Action, BlockPos, Direction[, int])");
    }

    private static Object createUseItemPacket(ClassLoader loader, Object hand, float yaw, float pitch) throws ReflectiveOperationException {
        Class<?> packetType = Class.forName("net.minecraft.network.protocol.game.ServerboundUseItemPacket", true, loader);
        for (Constructor<?> constructor : packetType.getConstructors()) {
            Class<?>[] p = constructor.getParameterTypes();
            if (p.length == 4 && p[0].isInstance(hand) && p[1] == int.class && p[2] == float.class && p[3] == float.class) {
                return constructor.newInstance(hand, 0, yaw, pitch);
            }
        }
        for (Constructor<?> constructor : packetType.getConstructors()) {
            Class<?>[] p = constructor.getParameterTypes();
            if (p.length == 2 && p[0].isInstance(hand) && p[1] == int.class) {
                return constructor.newInstance(hand, 0);
            }
        }
        throw new NoSuchMethodException("ServerboundUseItemPacket(InteractionHand, int[, float, float])");
    }

    private static Method findStaticAttackPacketFactory(Class<?> packetType, Object targetHandle) {
        for (Method method : packetType.getMethods()) {
            Class<?>[] p = method.getParameterTypes();
            if (Modifier.isStatic(method.getModifiers()) && method.getName().equals("createAttackPacket") &&
                p.length == 2 && p[0].isInstance(targetHandle) && p[1] == boolean.class) return method;
        }
        for (Method method : packetType.getDeclaredMethods()) {
            Class<?>[] p = method.getParameterTypes();
            if (Modifier.isStatic(method.getModifiers()) && method.getName().equals("createAttackPacket") &&
                p.length == 2 && p[0].isInstance(targetHandle) && p[1] == boolean.class) {
                method.setAccessible(true);
                return method;
            }
        }
        return null;
    }

    private static Object connection(Object playerHandle) throws ReflectiveOperationException {
        for (Class<?> type = playerHandle.getClass(); type != null; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (field.getName().equals("connection") || field.getType().getSimpleName().contains("ServerGamePacketListenerImpl")) {
                    field.setAccessible(true);
                    Object value = field.get(playerHandle);
                    if (value != null) return value;
                }
            }
        }
        throw new NoSuchFieldException("ServerPlayer.connection (ServerGamePacketListenerImpl)");
    }

    private static void startUsingViaGameMode(Object handle, Object hand) throws ReflectiveOperationException {
        Object stack = invokeCompatible(handle, "getItemInHand", hand);
        Object level = invokeNoArgs(handle, "level");
        Object gameMode = findGameMode(handle);
        Method useItem = findUseItemMethod(gameMode.getClass(), handle, level, stack, hand);
        if (useItem == null) throw new NoSuchMethodException("ServerPlayerGameMode.useItem(ServerPlayer, Level, ItemStack, InteractionHand)");
        useItem.invoke(gameMode, handle, level, stack, hand);
    }

    private static Object handle(Entity entity) throws ReflectiveOperationException {
        Method getHandle = findNoArgsMethod(entity.getClass(), "getHandle");
        if (getHandle == null) throw new NoSuchMethodException(entity.getClass().getName() + ".getHandle()");
        return getHandle.invoke(entity);
    }

    private static Object findGameMode(Object playerHandle) throws ReflectiveOperationException {
        for (Class<?> type = playerHandle.getClass(); type != null; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (field.getType().getSimpleName().contains("ServerPlayerGameMode")) {
                    field.setAccessible(true);
                    Object value = field.get(playerHandle);
                    if (value != null) return value;
                }
            }
        }
        throw new NoSuchFieldException("ServerPlayerGameMode field on " + playerHandle.getClass().getName());
    }

    private static Method findUseItemMethod(Class<?> type, Object player, Object level, Object stack, Object hand) {
        for (Method method : type.getMethods()) {
            Class<?>[] p = method.getParameterTypes();
            if (!method.getName().equals("useItem") || p.length != 4) continue;
            if (p[0].isInstance(player) && p[1].isInstance(level) && p[2].isInstance(stack) && p[3].isInstance(hand)) return method;
        }
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            for (Method method : current.getDeclaredMethods()) {
                Class<?>[] p = method.getParameterTypes();
                if (!method.getName().equals("useItem") || p.length != 4) continue;
                if (p[0].isInstance(player) && p[1].isInstance(level) && p[2].isInstance(stack) && p[3].isInstance(hand)) {
                    method.setAccessible(true);
                    return method;
                }
            }
        }
        return null;
    }

    private static Method findCompatibleMethod(Class<?> type, String name, Object argument) {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            for (Method method : current.getDeclaredMethods()) {
                Class<?>[] p = method.getParameterTypes();
                if (method.getName().equals(name) && p.length == 1 && p[0].isInstance(argument)) {
                    method.setAccessible(true);
                    return method;
                }
            }
        }
        return null;
    }

    private static Method findNoArgsMethod(Class<?> type, String name) {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            for (Method method : current.getDeclaredMethods()) {
                if (method.getName().equals(name) && method.getParameterCount() == 0) {
                    method.setAccessible(true);
                    return method;
                }
            }
        }
        return null;
    }

    private static Object invokeNoArgs(Object target, String name) throws ReflectiveOperationException {
        Method method = findNoArgsMethod(target.getClass(), name);
        if (method == null) throw new NoSuchMethodException(name + "()");
        return method.invoke(target);
    }

    private static Object invokeCompatible(Object target, String name, Object argument) throws ReflectiveOperationException {
        Method method = findCompatibleMethod(target.getClass(), name, argument);
        if (method == null) throw new NoSuchMethodException(name + "(" + argument.getClass().getName() + ")");
        return method.invoke(target, argument);
    }

    private static boolean isUsingItem(Object handle) throws ReflectiveOperationException {
        Object value = invokeNoArgs(handle, "isUsingItem");
        return Boolean.TRUE.equals(value);
    }

    private static String rootMessage(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null) root = root.getCause();
        return root.getMessage() == null ? root.getClass().getSimpleName() : root.getMessage();
    }
}
