package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Armadillo;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.Sounds;
import me.phantom.bananimations.utils.Utils;

public class ArmadilloAnimation extends Animation {

    private static final int TOTAL_TICKS = 90;
    private static final double START_RADIUS = 3.6D;
    private static final double END_RADIUS = 1.2D;

    public ArmadilloAnimation() {
        super("armadillo");
    }

    @Override
    public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        super.freeze(target);

        World world = target.getWorld();
        if (world == null) {
            super.finish(sender, target, type, reason);
            return;
        }

        List<Armadillo> armadillos = this.spawnArmadillos(world, target.getLocation(), 6);
        if (armadillos.isEmpty()) {
            super.finish(sender, target, type, reason);
            return;
        }

        new BukkitRunnable() {
            private int tick;

            @Override
            public void run() {
                if (target == null || !target.isOnline()) {
                    cleanup(armadillos);
                    this.cancel();
                    return;
                }

                if (tick >= TOTAL_TICKS) {
                    Location fin = target.getLocation();
                    world.createExplosion(fin.getX(), fin.getY(), fin.getZ(), 0.0F, false, false);
                    world.playSound(fin, Sounds.ENTITY_GENERIC_EXPLODE.get(), 0.7F, 1.25F);
                    cleanup(armadillos);
                    ArmadilloAnimation.this.finish(sender, target, type, reason);
                    this.cancel();
                    return;
                }

                double progress = tick / (double) TOTAL_TICKS;
                double radius = START_RADIUS + ((END_RADIUS - START_RADIUS) * progress);
                double speed = 3.0D + (7.0D * progress);
                Location center = target.getLocation().clone().add(0.0D, 0.2D, 0.0D);

                for (int i = 0; i < armadillos.size(); i++) {
                    Armadillo armadillo = armadillos.get(i);
                    if (armadillo == null || armadillo.isDead()) {
                        continue;
                    }

                    double angle = Math.toRadians((360.0D / armadillos.size()) * i + (tick * speed));
                    Location next = Utils.getLocationAroundCircle(center, radius, angle);
                    armadillo.teleport(next);
                    armadillo.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
                }

                if (tick % 16 == 0) {
                    world.playSound(center, Sounds.BLOCK_ANVIL_LAND.get(), 0.18F, 1.8F);
                }

                tick++;
            }
        }.runTaskTimer(this.getPlugin(), 0L, 1L);
    }

    private List<Armadillo> spawnArmadillos(World world, Location center, int count) {
        List<Armadillo> armadillos = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = Math.toRadians((360.0D / count) * i);
            Location spawn = Utils.getLocationAroundCircle(center, START_RADIUS, angle);
            Armadillo armadillo = (Armadillo) world.spawnEntity(spawn, EntityType.ARMADILLO);
            armadillo.setAI(false);
            armadillo.setGravity(false);
            armadillo.setInvulnerable(true);
            armadillo.setCollidable(false);
            this.getPlugin().getMobUtils().setDefaultTags(armadillo);
            armadillos.add(armadillo);
        }
        return armadillos;
    }

    private static void cleanup(List<Armadillo> armadillos) {
        for (Armadillo armadillo : armadillos) {
            if (armadillo != null && !armadillo.isDead()) {
                armadillo.remove();
            }
        }
        armadillos.clear();
    }
}
