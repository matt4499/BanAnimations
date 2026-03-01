package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;

public class PhantomAnimation extends Animation {

    private static final int TOTAL_TICKS = 92;

    public PhantomAnimation() {
        super("phantom");
    }

    @Override
    public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        super.freeze(target);

        World world = target.getWorld();
        if (world == null) {
            super.finish(sender, target, type, reason);
            return;
        }

        List<Phantom> phantoms = this.spawnPhantoms(world, target.getLocation(), 3);
        if (phantoms.isEmpty()) {
            super.finish(sender, target, type, reason);
            return;
        }

        Location[] destinations = new Location[phantoms.size()];

        new BukkitRunnable() {
            private int tick;

            @Override
            public void run() {
                if (!target.isOnline()) {
                    cleanup(phantoms);
                    this.cancel();
                    return;
                }

                if (tick >= TOTAL_TICKS) {
                    Location end = target.getLocation();
                    world.createExplosion(end.getX(), end.getY(), end.getZ(), 0.0F, false, false);
                    world.playSound(end, Sound.ENTITY_PHANTOM_BITE, 1.0F, 0.9F);
                    cleanup(phantoms);
                    PhantomAnimation.this.finish(sender, target, type, reason);
                    this.cancel();
                    return;
                }

                Location center = target.getLocation().clone();
                double progress = tick / (double) TOTAL_TICKS;
                double baseHeight = 6.8D - (progress * 3.8D);

                for (int i = 0; i < phantoms.size(); i++) {
                    Phantom phantom = phantoms.get(i);
                    if (phantom == null || phantom.isDead()) {
                        continue;
                    }

                    if (destinations[i] == null || tick % 10 == 0) {
                        double targetXOffset = (PhantomAnimation.this.getRandom().nextDouble() - 0.5D) * 5.2D;
                        double targetZOffset = (PhantomAnimation.this.getRandom().nextDouble() - 0.5D) * 5.2D;
                        double targetYOffset = baseHeight + (PhantomAnimation.this.getRandom().nextDouble() * 1.4D);
                        destinations[i] = center.clone().add(targetXOffset, targetYOffset, targetZOffset);
                    }

                    Vector movement = destinations[i].toVector().subtract(phantom.getLocation().toVector());
                    double moveSpeed = 0.55D + (progress * 0.35D);
                    Location next;
                    if (movement.lengthSquared() > (moveSpeed * moveSpeed)) {
                        next = phantom.getLocation().clone().add(movement.normalize().multiply(moveSpeed));
                    } else {
                        next = destinations[i].clone();
                    }

                    if (tick % 24 >= 16 && tick % 24 <= 22) {
                        next.add(0.0D, -0.25D, 0.0D);
                    }

                    phantom.teleport(next.setDirection(center.toVector().subtract(next.toVector())));
                    phantom.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
                    phantom.setTarget(target);
                }

                if (tick % 16 == 0) {
                    world.playSound(center, Sound.ENTITY_PHANTOM_FLAP, 0.35F, 1.1F);
                }

                tick++;
            }
        }.runTaskTimer(this.getPlugin(), 0L, 1L);
    }

    private List<Phantom> spawnPhantoms(World world, Location center, int count) {
        List<Phantom> phantoms = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = Math.toRadians((360.0D / count) * i);
            Location spawn = center.clone().add(Math.cos(angle) * 5.2D, 7.0D, Math.sin(angle) * 5.2D);
            Phantom phantom = (Phantom) world.spawnEntity(spawn, EntityType.PHANTOM);
            phantom.setAI(false);
            phantom.setGravity(false);
            phantom.setInvulnerable(true);
            phantom.setCollidable(false);
            this.getPlugin().getMobUtils().setDefaultTags(phantom);
            phantoms.add(phantom);
        }
        return phantoms;
    }

    private static void cleanup(List<Phantom> phantoms) {
        for (Phantom phantom : phantoms) {
            if (phantom != null && !phantom.isDead()) {
                phantom.remove();
            }
        }
        phantoms.clear();
    }
}
