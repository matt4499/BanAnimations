package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;

public class EndermanAnimation extends Animation {

    private static final int TOTAL_TICKS = 92;

    public EndermanAnimation() {
        super("enderman");
    }

    @Override
    public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        super.freeze(target);

        World world = target.getWorld();
        if (world == null) {
            super.finish(sender, target, type, reason);
            return;
        }

        List<Enderman> endermen = this.spawnEndermen(world, target.getLocation(), 4);
        if (endermen.isEmpty()) {
            super.finish(sender, target, type, reason);
            return;
        }

        new BukkitRunnable() {
            private int tick;

            @Override
            public void run() {
                if (!target.isOnline()) {
                    cleanup(endermen);
                    this.cancel();
                    return;
                }

                if (tick >= TOTAL_TICKS) {
                    Location end = target.getLocation();
                    world.createExplosion(end.getX(), end.getY(), end.getZ(), 0.0F, false, false);
                    world.playSound(end, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.7F);
                    cleanup(endermen);
                    EndermanAnimation.this.finish(sender, target, type, reason);
                    this.cancel();
                    return;
                }

                Location center = target.getLocation();
                for (int i = 0; i < endermen.size(); i++) {
                    Enderman enderman = endermen.get(i);
                    if (enderman == null || enderman.isDead()) {
                        continue;
                    }

                    if (tick % 6 == 0) {
                        double angle = Math.toRadians((90.0D * i) + (tick * (6.0D + (tick / 24.0D))));
                        double radius = 3.4D - Math.min(2.2D, tick * 0.02D);
                        Location next = center.clone().add(Math.cos(angle) * radius, 0.0D, Math.sin(angle) * radius);
                        enderman.teleport(next.setDirection(center.toVector().subtract(next.toVector())));
                        world.playSound(next, Sound.ENTITY_ENDERMAN_TELEPORT, 0.35F, 1.3F);
                    }

                    enderman.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
                }

                tick++;
            }
        }.runTaskTimer(this.getPlugin(), 0L, 1L);
    }

    private List<Enderman> spawnEndermen(World world, Location center, int count) {
        List<Enderman> endermen = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = Math.toRadians((360.0D / count) * i);
            Location spawn = center.clone().add(Math.cos(angle) * 3.4D, 0.0D, Math.sin(angle) * 3.4D);
            Enderman enderman = (Enderman) world.spawnEntity(spawn, EntityType.ENDERMAN);
            enderman.setAI(false);
            enderman.setGravity(false);
            enderman.setInvulnerable(true);
            enderman.setCollidable(false);
            this.getPlugin().getMobUtils().setDefaultTags(enderman);
            endermen.add(enderman);
        }
        return endermen;
    }

    private static void cleanup(List<Enderman> endermen) {
        for (Enderman enderman : endermen) {
            if (enderman != null && !enderman.isDead()) {
                enderman.remove();
            }
        }
        endermen.clear();
    }
}
