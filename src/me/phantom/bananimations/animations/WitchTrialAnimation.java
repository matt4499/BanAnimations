package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SplashPotion;
import org.bukkit.entity.Witch;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;

public class WitchTrialAnimation extends Animation {

    private static final int TOTAL_TICKS = 96;
    private static final double STANDOFF_RADIUS = 3.3D;

    public WitchTrialAnimation() {
        super("witchtrial");
    }

    @Override
    public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        super.freeze(target);

        World world = target.getWorld();
        if (world == null) {
            super.finish(sender, target, type, reason);
            return;
        }

        List<Witch> witches = this.spawnWitches(world, target.getLocation(), 4);
        if (witches.isEmpty()) {
            super.finish(sender, target, type, reason);
            return;
        }

        new BukkitRunnable() {
            private int tick;

            @Override
            public void run() {
                if (!target.isOnline()) {
                    cleanup(witches);
                    this.cancel();
                    return;
                }

                if (tick >= TOTAL_TICKS) {
                    Location end = target.getLocation();
                    world.createExplosion(end.getX(), end.getY(), end.getZ(), 0.0F, false, false);
                    world.playSound(end, Sound.ENTITY_WITCH_CELEBRATE, 1.0F, 1.0F);
                    cleanup(witches);
                    WitchTrialAnimation.this.finish(sender, target, type, reason);
                    this.cancel();
                    return;
                }

                Location center = target.getLocation().clone().add(0.0D, 0.1D, 0.0D);

                for (int i = 0; i < witches.size(); i++) {
                    Witch witch = witches.get(i);
                    if (witch == null || witch.isDead()) {
                        continue;
                    }

                    double baseAngle = Math.toRadians(90.0D * i);
                    double sway = Math.sin((tick * 0.17D) + (i * 1.2D)) * 0.35D;
                    double x = (Math.cos(baseAngle) * STANDOFF_RADIUS) + sway;
                    double z = (Math.sin(baseAngle) * STANDOFF_RADIUS) - sway;
                    Location next = center.clone().add(x, 0.0D, z);
                    witch.teleport(next.setDirection(center.toVector().subtract(next.toVector())));
                    witch.setVelocity(new Vector(0.0D, 0.0D, 0.0D));

                    if (tick > 10 && tick % 14 == i % 2) {
                        WitchTrialAnimation.this.throwPotion(world, witch, target);
                    }
                }

                if (tick % 12 == 0) {
                    world.playSound(center, Sound.ENTITY_WITCH_THROW, 0.65F, 1.2F);
                }

                tick++;
            }
        }.runTaskTimer(this.getPlugin(), 0L, 1L);
    }

    private List<Witch> spawnWitches(World world, Location center, int count) {
        List<Witch> witches = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = Math.toRadians((360.0D / count) * i);
            Location spawn = center.clone().add(Math.cos(angle) * STANDOFF_RADIUS, 0.0D, Math.sin(angle) * STANDOFF_RADIUS);
            Witch witch = (Witch) world.spawnEntity(spawn, EntityType.WITCH);
            witch.setAI(false);
            witch.setGravity(false);
            witch.setInvulnerable(true);
            witch.setCollidable(false);
            this.getPlugin().getMobUtils().setDefaultTags(witch);
            witches.add(witch);
        }
        return witches;
    }

    private void throwPotion(World world, Witch witch, Player target) {
        Location eye = witch.getEyeLocation().clone().add(0.0D, -0.1D, 0.0D);
        SplashPotion potion = (SplashPotion) world.spawnEntity(eye, EntityType.SPLASH_POTION);
        potion.setItem(new ItemStack(Material.SPLASH_POTION));
        potion.setShooter(witch);

        Vector velocity = target.getEyeLocation().toVector().subtract(eye.toVector()).normalize().multiply(0.82D);
        velocity.setY(velocity.getY() + 0.18D);
        potion.setVelocity(velocity);
    }

    private static void cleanup(List<Witch> witches) {
        for (Witch witch : witches) {
            if (witch != null && !witch.isDead()) {
                witch.remove();
            }
        }
        witches.clear();
    }
}
