package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sniffer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.Sounds;
import me.phantom.bananimations.utils.Utils;

public class SnifferTribunalAnimation extends Animation {

    private static final int TOTAL_TICKS = 110;
    private static final double START_RADIUS = 4.0D;
    private static final double END_RADIUS = 1.5D;

    public SnifferTribunalAnimation() {
        super("sniffertribunal");
    }

    @Override
    public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        super.freeze(target);

        World world = target.getWorld();
        if (world == null) {
            super.finish(sender, target, type, reason);
            return;
        }

        List<Sniffer> sniffers = this.spawnSniffers(world, target.getLocation().clone(), 3);

        if (sniffers.isEmpty()) {
            super.finish(sender, target, type, reason);
            return;
        }

        new BukkitRunnable() {
            private int tick;

            @Override
            public void run() {
                if (target == null || !target.isOnline()) {
                    cleanup(sniffers);
                    this.cancel();
                    return;
                }

                if (tick >= TOTAL_TICKS) {
                    Location fin = target.getLocation();
                    world.createExplosion(fin.getX(), fin.getY(), fin.getZ(), 0.0F, false, false);
                    world.playSound(fin, Sounds.BLOCK_ANVIL_LAND.get(), 1.0F, 0.8F);
                    world.playSound(fin, Sounds.ENTITY_GENERIC_EXPLODE.get(), 0.8F, 0.7F);
                    cleanup(sniffers);
                    SnifferTribunalAnimation.this.finish(sender, target, type, reason);
                    this.cancel();
                    return;
                }

                double progress = tick / (double) TOTAL_TICKS;
                double radius = START_RADIUS + ((END_RADIUS - START_RADIUS) * progress);
                double speed = 2.0D + (5.0D * progress);
                Location center = target.getLocation();

                for (int i = 0; i < sniffers.size(); i++) {
                    Sniffer sniffer = sniffers.get(i);
                    if (sniffer == null || sniffer.isDead()) {
                        continue;
                    }

                    double base = i * 120.0D;
                    double angle = Math.toRadians(base + (tick * speed));
                    Location next = Utils.getLocationAroundCircle(center, radius, angle).add(0.0D, 0.05D, 0.0D);
                    sniffer.teleport(next);
                    sniffer.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
                    sniffer.setTarget(target);
                }

                if (tick % 22 == 0) {
                    world.playSound(center, Sounds.BLOCK_ANVIL_LAND.get(), 0.45F, 1.5F);
                }

                tick++;
            }
        }.runTaskTimer(this.getPlugin(), 0L, 1L);
    }

    private List<Sniffer> spawnSniffers(World world, Location center, int count) {
        List<Sniffer> sniffers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = Math.toRadians((360.0D / count) * i);
            Location spawn = Utils.getLocationAroundCircle(center, START_RADIUS, angle);
            Sniffer sniffer = (Sniffer) world.spawnEntity(spawn, EntityType.SNIFFER);
            sniffer.setAI(false);
            sniffer.setGravity(false);
            sniffer.setInvulnerable(true);
            sniffer.setCollidable(false);
            this.getPlugin().getMobUtils().setDefaultTags(sniffer);
            sniffers.add(sniffer);
        }
        return sniffers;
    }

    private static void cleanup(List<Sniffer> sniffers) {
        for (Sniffer sniffer : sniffers) {
            if (sniffer != null && !sniffer.isDead()) {
                sniffer.remove();
            }
        }
        sniffers.clear();
    }
}
