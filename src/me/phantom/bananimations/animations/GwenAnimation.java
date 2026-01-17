package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.RepeatingTaskHelper;
import me.phantom.bananimations.utils.Sounds;
import me.phantom.bananimations.utils.Utils;

/**
 * Gwen Animation.
 * Spawns guardians that rotate around the player and then explode.
 */
public class GwenAnimation extends Animation {

    protected static final double GUARDIAN_ROTATION_RADIUS = 3.0;
    protected static final double GUARDIAN_CENTER_Y_OFFSET = 6.0;
    protected static final double DEGREES_PER_TICK = 5.0;

    public GwenAnimation() {
        super("gwen");
    }

    @Override
    public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        super.freeze(target);

        Location targetLocation = target.getLocation();
        World world = targetLocation.getWorld();

        if (world == null) {
            getPlugin().getLogger().warning("[BanAnimations] Cannot play GwenAnimation: Target player's world is null.");
            if (getPlugin().isFrozen(target)) {
                super.finish(sender, target, type, reason);
            }
            return;
        }
        
        Location guardianCenter = targetLocation.clone().add(0.0D, GUARDIAN_CENTER_Y_OFFSET, 0.0D);
        List<Guardian> guardians = spawnGuardians(world, guardianCenter, target);

        if (guardians.isEmpty()) {
            getPlugin().getLogger().warning("[BanAnimations] Failed to spawn any Guardians for GwenAnimation.");
            if (getPlugin().isFrozen(target)) {
                super.finish(sender, target, type, reason);
            }
            return;
        }

        RepeatingTaskHelper taskHelper = new RepeatingTaskHelper();
        
        new GwenTask(this, taskHelper, guardians, sender, target, type, reason, guardianCenter, world, targetLocation)
                .runTaskTimer(getPlugin(), 10L, 1L);
    }

    private List<Guardian> spawnGuardians(World world, Location guardianCenter, Player actualTarget) {
        List<Guardian> spawnedGuardians = new ArrayList<>();
        Location[] initialLocations = getInitialGuardianLocations(guardianCenter);

        for (Location loc : initialLocations) {
            try {
                Guardian guardian = (Guardian) world.spawnEntity(loc, EntityType.GUARDIAN);
                guardian.setCollidable(false);
                guardian.setTarget(actualTarget);
                getPlugin().getMobUtils().setDefaultTags(guardian);
                guardian.setGravity(false);
                guardian.setCustomNameVisible(false);
                guardian.setCustomName("bananimations-guardian");
                spawnedGuardians.add(guardian);
            } catch (Exception e) {
                getPlugin().getLogger().severe("[BanAnimations] Error spawning Guardian: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return spawnedGuardians;
    }

    Location[] getInitialGuardianLocations(Location center) {
        return new Location[]{
            Utils.getLocationAroundCircle(center, GUARDIAN_ROTATION_RADIUS, Math.toRadians(45)),
            Utils.getLocationAroundCircle(center, GUARDIAN_ROTATION_RADIUS, Math.toRadians(135)),
            Utils.getLocationAroundCircle(center, GUARDIAN_ROTATION_RADIUS, Math.toRadians(225)),
            Utils.getLocationAroundCircle(center, GUARDIAN_ROTATION_RADIUS, Math.toRadians(315))
        };
    }

    /**
     * GwenTask Runnable.
     * Handles the frame-by-frame updates for the GwenAnimation.
     */
    private static class GwenTask extends BukkitRunnable {

        private final GwenAnimation animation;
        private final RepeatingTaskHelper taskHelper;
        private final List<Guardian> guardians;
        private final CommandSender sender;
        private final Player target;
        private final AnimationType aniType;
        private final String reason;
        private final Location guardianCenter;
        private final World world;
        private final Location targetLocation;
        private static final int TOTAL_ANIMATION_TICKS = 130;

        GwenTask(GwenAnimation animation, RepeatingTaskHelper taskHelper, List<Guardian> guardians,
                CommandSender sender, Player target, AnimationType aniType, String reason,
                Location guardianCenter, World world, Location targetLocation) {
            this.animation = animation;
            this.taskHelper = taskHelper;
            this.guardians = guardians;
            this.sender = sender;
            this.target = target;
            this.aniType = aniType;
            this.reason = reason;
            this.guardianCenter = guardianCenter;
            this.world = world;
            this.targetLocation = targetLocation;
        }

        @Override
        public void run() {
            try {
                if (target == null || !target.isOnline()) {
                    cleanupAndCancel();
                    return;
                }

                if (guardians == null || guardians.isEmpty()) {
                    cleanupAndCancel();
                    return;
                }

                int counter = this.taskHelper.getCounter();

                if (counter >= TOTAL_ANIMATION_TICKS) {
                    playExplosionEffect();
                    animation.finish(sender, target, aniType, reason);
                    cleanupAndCancel();
                    return;
                }

                rotateGuardians();
                this.taskHelper.increment();

            } catch (Exception e) {
                animation.getPlugin().getLogger().severe("[BanAnimations] Error during GwenTask: " + e.getMessage());
                e.printStackTrace();
                cleanupGuardians();
                this.taskHelper.cancel();
                animation.finish(sender, target, aniType, reason);
                this.cancel();
            }
        }

        private void playExplosionEffect() {
            if (this.world != null && this.targetLocation != null) {
                try {
                    this.world.createExplosion(this.targetLocation.getX(), this.targetLocation.getY(),
                            this.targetLocation.getZ(), 1.0F, false, false);
                    this.world.playSound(this.targetLocation, Sounds.ENTITY_GENERIC_EXPLODE.get(), 1.0F, 1.0F);
                } catch (Exception e) {
                    animation.getPlugin().getLogger().warning("Effect error: " + e.getMessage());
                }
            }
        }

        private void cleanupAndCancel() {
            cleanupGuardians();
            this.taskHelper.cancel();
            this.cancel();
        }

        private void rotateGuardians() {
            if (target == null || !target.isOnline()) return;

            // Angles corresponding to the 4 spawn positions defined in getInitialGuardianLocations
            double[] startAngles = {45.0, 135.0, 225.0, 315.0};
            double currentTick = this.taskHelper.getCounter();

            for (int i = 0; i < this.guardians.size(); i++) {
                Guardian guardian = this.guardians.get(i);

                if (guardian == null || guardian.isDead() || guardian.getWorld() == null || this.guardianCenter == null) {
                    continue;
                }

                guardian.setTarget(this.target);

                // Calculate next position on the circle based on time (tick) to ensure consistent rotation
                // Use teleport instead of velocity to allow noclip/passing through blocks
                double angleDeg = startAngles[i % 4] + (currentTick * GwenAnimation.DEGREES_PER_TICK);
                Location nextLoc = Utils.getLocationAroundCircle(
                        this.guardianCenter,
                        GwenAnimation.GUARDIAN_ROTATION_RADIUS,
                        Math.toRadians(angleDeg)
                );

                guardian.teleport(nextLoc);
                guardian.setVelocity(new Vector(0, 0, 0));
            }
        }

        private void cleanupGuardians() {
            if (this.guardians != null) {
                for (Guardian guardian : this.guardians) {
                    if (guardian != null && !guardian.isDead()) {
                        guardian.remove();
                    }
                }
                this.guardians.clear();
            }
        }
    }
}