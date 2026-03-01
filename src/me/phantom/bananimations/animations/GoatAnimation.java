package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Goat;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;

public class GoatAnimation extends Animation {

    private static final int TOTAL_TICKS = 84;
    private static final double STALK_RADIUS = 3.8D;
    private static final double DASH_MIN_RADIUS = 1.0D;

    public GoatAnimation() {
        super("goat");
    }

    @Override
    public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        super.freeze(target);

        World world = target.getWorld();
        if (world == null) {
            super.finish(sender, target, type, reason);
            return;
        }

        List<Goat> goats = this.spawnGoats(world, target.getLocation());
        if (goats.isEmpty()) {
            super.finish(sender, target, type, reason);
            return;
        }

        new BukkitRunnable() {
            private int tick;

            @Override
            public void run() {
                if (!target.isOnline()) {
                    cleanup(goats);
                    this.cancel();
                    return;
                }

                if (tick >= TOTAL_TICKS) {
                    Location end = target.getLocation();
                    world.createExplosion(end.getX(), end.getY(), end.getZ(), 0.0F, false, false);
                    world.playSound(end, Sound.ENTITY_GOAT_RAM_IMPACT, 1.0F, 0.9F);
                    cleanup(goats);
                    GoatAnimation.this.finish(sender, target, type, reason);
                    this.cancel();
                    return;
                }

                Location center = target.getLocation().clone().add(0.0D, 0.1D, 0.0D);
                int phaseTick = Math.max(0, tick - 24);
                int cycle = phaseTick / 12;
                int activeGoatIndex = cycle % goats.size();
                int cycleTick = phaseTick % 12;

                for (int i = 0; i < goats.size(); i++) {
                    Goat goat = goats.get(i);
                    if (goat == null || goat.isDead()) {
                        continue;
                    }

                    double baseAngle = Math.toRadians(90.0D * i);
                    double jitterX = Math.sin((tick * 0.16D) + i) * 0.18D;
                    double jitterZ = Math.cos((tick * 0.12D) + (i * 0.7D)) * 0.18D;
                    double radius = STALK_RADIUS;

                    if (tick >= 24 && i == activeGoatIndex) {
                        if (cycleTick <= 5) {
                            double t = cycleTick / 5.0D;
                            radius = STALK_RADIUS + ((DASH_MIN_RADIUS - STALK_RADIUS) * t);
                        } else {
                            double t = (cycleTick - 6) / 5.0D;
                            radius = DASH_MIN_RADIUS + (((STALK_RADIUS - 0.5D) - DASH_MIN_RADIUS) * t);
                        }
                    }

                    Location next = center.clone().add((Math.cos(baseAngle) * radius) + jitterX, 0.0D,
                            (Math.sin(baseAngle) * radius) + jitterZ);
                    goat.teleport(next.setDirection(center.toVector().subtract(next.toVector())));
                    goat.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
                }

                if (tick % 12 == 0) {
                    world.playSound(center, Sound.ENTITY_GOAT_HORN_BREAK, 0.2F, 1.9F);
                }

                if (tick >= 24 && cycleTick == 2) {
                    world.playSound(center, Sound.ENTITY_GOAT_RAM_IMPACT, 0.6F, 1.25F);
                }

                tick++;
            }
        }.runTaskTimer(this.getPlugin(), 0L, 1L);
    }

    private List<Goat> spawnGoats(World world, Location center) {
        List<Goat> goats = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            double angle = Math.toRadians(90.0D * i);
            Location spawn = center.clone().add(Math.cos(angle) * STALK_RADIUS, 0.0D, Math.sin(angle) * STALK_RADIUS);
            Goat goat = (Goat) world.spawnEntity(spawn, EntityType.GOAT);
            goat.setAI(false);
            goat.setGravity(false);
            goat.setInvulnerable(true);
            goat.setCollidable(false);
            this.getPlugin().getMobUtils().setDefaultTags(goat);
            goats.add(goat);
        }
        return goats;
    }

    private static void cleanup(List<Goat> goats) {
        for (Goat goat : goats) {
            if (goat != null && !goat.isDead()) {
                goat.remove();
            }
        }
        goats.clear();
    }
}
