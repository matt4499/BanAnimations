package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Allay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.Sounds;
import me.phantom.bananimations.utils.Utils;

public class AllayVerdictAnimation extends Animation {

    private static final int TOTAL_TICKS = 90;
    private static final double START_RADIUS = 3.2D;
    private static final double END_RADIUS = 0.9D;

    public AllayVerdictAnimation() {
        super("allayverdict");
    }

    @Override
    public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        super.freeze(target);

        World world = target.getWorld();
        Location center = target.getLocation().clone().add(0.0D, 1.2D, 0.0D);
        List<Allay> allays = this.spawnAllays(world, center, 4, target);

        if (allays.isEmpty()) {
            super.finish(sender, target, type, reason);
            return;
        }

        new BukkitRunnable() {
            private int tick;

            @Override
            public void run() {
                if (target == null || !target.isOnline()) {
                    cleanup(allays);
                    this.cancel();
                    return;
                }

                if (tick >= TOTAL_TICKS) {
                    world.createExplosion(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), 0.0F, false, false);
                    world.playSound(target.getLocation(), Sounds.ENTITY_GENERIC_EXPLODE.get(), 0.9F, 1.3F);
                    cleanup(allays);
                    AllayVerdictAnimation.this.finish(sender, target, type, reason);
                    this.cancel();
                    return;
                }

                double progress = tick / (double) TOTAL_TICKS;
                double eased = Math.pow(progress, 2.1D);
                double radius = START_RADIUS + ((END_RADIUS - START_RADIUS) * eased);
                double speed = 4.0D + (14.0D * eased);

                Location dynamicCenter = target.getLocation().clone().add(0.0D, 1.2D, 0.0D);
                for (int i = 0; i < allays.size(); i++) {
                    Allay allay = allays.get(i);
                    if (allay == null || allay.isDead()) {
                        continue;
                    }

                    double baseAngle = 90.0D * i;
                    double angle = Math.toRadians(baseAngle + (tick * speed));
                    Location next = Utils.getLocationAroundCircle(dynamicCenter, radius, angle);
                    allay.teleport(next);
                    allay.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
                    allay.setTarget(target);
                }

                if (tick % 18 == 0) {
                    world.playSound(dynamicCenter, Sounds.ENTITY_WITHER_AMBIENT.get(), 0.08F, 2.0F);
                }

                tick++;
            }
        }.runTaskTimer(this.getPlugin(), 0L, 1L);
    }

    private List<Allay> spawnAllays(World world, Location center, int amount, Player target) {
        List<Allay> allays = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            double angle = Math.toRadians(90.0D * i);
            Location spawnLoc = Utils.getLocationAroundCircle(center, START_RADIUS, angle);
            Allay allay = (Allay) world.spawnEntity(spawnLoc, EntityType.ALLAY);
            allay.setAI(false);
            allay.setInvulnerable(true);
            allay.setGravity(false);
            allay.setCollidable(false);
            allay.setTarget(target);
            this.getPlugin().getMobUtils().setDefaultTags(allay);
            allays.add(allay);
        }
        return allays;
    }

    private static void cleanup(List<Allay> allays) {
        for (Allay allay : allays) {
            if (allay != null && !allay.isDead()) {
                allay.remove();
            }
        }
        allays.clear();
    }
}
