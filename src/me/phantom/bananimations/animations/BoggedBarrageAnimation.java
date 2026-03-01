package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bogged;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.Sounds;

public class BoggedBarrageAnimation extends Animation {

    private static final int TOTAL_TICKS = 85;

    public BoggedBarrageAnimation() {
        super("boggedbarrage");
    }

    @Override
    public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        super.freeze(target);

        Location center = target.getLocation();
        World world = center.getWorld();
        if (world == null) {
            super.finish(sender, target, type, reason);
            return;
        }

        List<Bogged> boggeds = this.spawnBoggedRing(world, center, 4.0D);
        List<Arrow> arrows = new ArrayList<>();

        if (boggeds.isEmpty()) {
            super.finish(sender, target, type, reason);
            return;
        }

        new BukkitRunnable() {
            private int tick;

            @Override
            public void run() {
                if (target == null || !target.isOnline()) {
                    cleanupBogged(boggeds);
                    cleanupArrows(arrows);
                    this.cancel();
                    return;
                }

                if (tick >= TOTAL_TICKS) {
                    world.createExplosion(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), 0.0F, false, false);
                    world.playSound(target.getLocation(), Sounds.ENTITY_GENERIC_EXPLODE.get(), 1.0F, 0.95F);
                    cleanupBogged(boggeds);
                    cleanupArrows(arrows);
                    BoggedBarrageAnimation.this.finish(sender, target, type, reason);
                    this.cancel();
                    return;
                }

                for (Bogged bogged : boggeds) {
                    if (bogged == null || bogged.isDead()) {
                        continue;
                    }

                    bogged.teleport(bogged.getLocation().setDirection(target.getEyeLocation().toVector().subtract(bogged.getEyeLocation().toVector())));
                    bogged.setTarget(target);
                    bogged.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
                }

                if (tick % 8 == 0 && tick > 10) {
                    for (Bogged bogged : boggeds) {
                        if (bogged == null || bogged.isDead()) {
                            continue;
                        }

                        Location eye = bogged.getEyeLocation();
                        Vector velocity = target.getEyeLocation().toVector().subtract(eye.toVector()).normalize().multiply(1.4D);
                        Arrow arrow = world.spawnArrow(eye.add(0.0D, -0.1D, 0.0D), velocity, 1.4F, 0.0F);
                        arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
                        arrow.setShooter(bogged);
                        arrows.add(arrow);
                    }
                    world.playSound(target.getLocation(), Sounds.ENTITY_TNT_PRIMED.get(), 0.2F, 2.0F);
                }

                tick++;
            }
        }.runTaskTimer(this.getPlugin(), 0L, 1L);
    }

    private List<Bogged> spawnBoggedRing(World world, Location center, double radius) {
        List<Bogged> boggeds = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            double angle = Math.toRadians(90.0D * i);
            Location spawn = center.clone().add(Math.cos(angle) * radius, 0.0D, Math.sin(angle) * radius);
            Bogged bogged = (Bogged) world.spawnEntity(spawn, EntityType.BOGGED);
            bogged.setAI(false);
            bogged.setGravity(false);
            bogged.setInvulnerable(true);
            bogged.setCollidable(false);
            this.getPlugin().getMobUtils().setDefaultTags(bogged);
            boggeds.add(bogged);
        }
        return boggeds;
    }

    private static void cleanupBogged(List<Bogged> boggeds) {
        for (Bogged bogged : boggeds) {
            if (bogged != null && !bogged.isDead()) {
                bogged.remove();
            }
        }
        boggeds.clear();
    }

    private static void cleanupArrows(List<Arrow> arrows) {
        for (Arrow arrow : arrows) {
            if (arrow != null && !arrow.isDead()) {
                arrow.remove();
            }
        }
        arrows.clear();
    }
}
