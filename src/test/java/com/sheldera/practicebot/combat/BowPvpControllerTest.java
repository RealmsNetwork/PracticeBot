package com.sheldera.practicebot.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;

class BowPvpControllerTest {
    @Test
    void bowChargeUsesVanillaChargeCurveAndCapsAtFullPower() {
        assertEquals(0.0D, BowPvpController.bowLaunchSpeed(0), 1.0E-9D);
        assertEquals(1.25D, BowPvpController.bowLaunchSpeed(10), 1.0E-9D);
        assertEquals(3.0D, BowPvpController.bowLaunchSpeed(20), 1.0E-9D);
        assertEquals(3.0D, BowPvpController.bowLaunchSpeed(30), 1.0E-9D);
    }

    @Test
    void ballisticAimCompensatesForArrowGravityAtMediumRange() {
        Vector origin = new Vector(0.0D, 1.6D, 0.0D);
        Vector target = new Vector(0.0D, 1.6D, 30.0D);
        BowPvpController.AimSolution aim = BowPvpController.solveAim(
            origin, target, new Vector(), new Vector(), BowPvpController.bowLaunchSpeed(20));

        assertNotNull(aim);
        assertTrue(aim.pitch() < 0.0F, "The bow should aim above a level target to compensate for gravity");
        assertTrue(aim.flightTicks() > 1);
        assertTrue(aim.speedError() < 0.15D);
    }

    @Test
    void ballisticAimReachesThePredictedFuturePositionWithinAReasonableTolerance() {
        Vector origin = new Vector(0.0D, 1.6D, 0.0D);
        Vector target = new Vector(0.0D, 1.6D, 24.0D);
        Vector targetVelocity = new Vector(0.12D, 0.0D, 0.0D);
        double speed = BowPvpController.bowLaunchSpeed(20);
        BowPvpController.AimSolution aim = BowPvpController.solveAim(
            origin, target, targetVelocity, new Vector(), speed);

        assertNotNull(aim);
        double yaw = Math.toRadians(aim.yaw());
        double pitch = Math.toRadians(aim.pitch());
        Vector velocity = new Vector(-Math.sin(yaw) * Math.cos(pitch),
            -Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch)).multiply(speed);
        Vector position = origin.clone();
        Vector movingTarget = target.clone();
        double closestDistance = Double.POSITIVE_INFINITY;
        for (int tick = 0; tick < aim.flightTicks() + 2; tick++) {
            Vector relativeStart = position.clone().subtract(movingTarget);
            position.add(velocity);
            movingTarget.add(targetVelocity);
            Vector relativeEnd = position.clone().subtract(movingTarget);
            Vector segment = relativeEnd.clone().subtract(relativeStart);
            double lengthSquared = segment.lengthSquared();
            double fraction = lengthSquared < 1.0E-9D ? 0.0D
                : Math.max(0.0D, Math.min(1.0D, -relativeStart.dot(segment) / lengthSquared));
            closestDistance = Math.min(closestDistance,
                relativeStart.add(segment.multiply(fraction)).length());
            velocity.multiply(0.99D);
            velocity.setY(velocity.getY() - 0.05D);
        }
        assertTrue(closestDistance < 0.35D,
            "The swept arrow path should intersect the predicted moving-target hitbox");
    }

    @Test
    void ballisticAimCompensatesForTheShootersMovementInheritance() {
        Vector origin = new Vector(0.0D, 1.6D, 0.0D);
        Vector target = new Vector(0.0D, 1.6D, 24.0D);
        Vector shooterVelocity = new Vector(0.2D, 0.0D, 0.0D);
        BowPvpController.AimSolution aim = BowPvpController.solveAim(
            origin, target, new Vector(), shooterVelocity, BowPvpController.bowLaunchSpeed(20));

        assertNotNull(aim);
        assertTrue(aim.yaw() > 0.0F,
            "The bow should aim against positive-X shooter momentum to keep the arrow on target");
    }

    @Test
    void ballisticAimLeadsALaterallyMovingTarget() {
        Vector origin = new Vector(0.0D, 1.6D, 0.0D);
        Vector target = new Vector(0.0D, 1.6D, 24.0D);
        Vector targetVelocity = new Vector(0.12D, 0.0D, 0.0D);
        BowPvpController.AimSolution aim = BowPvpController.solveAim(
            origin, target, targetVelocity, new Vector(), BowPvpController.bowLaunchSpeed(20));

        assertNotNull(aim);
        assertTrue(aim.yaw() < 0.0F,
            "The aim should lead toward the target's future positive-X position");
        assertTrue(aim.speedError() < 0.15D);
    }
}
