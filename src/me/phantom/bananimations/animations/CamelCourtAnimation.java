package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Camel;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.Sounds;

public class CamelCourtAnimation extends Animation {

    private static final int TOTAL_TICKS = 100;

    public CamelCourtAnimation() {
        super("camelcourt");
    }

    @Override
    public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        super.freeze(target);

        World world = target.getWorld();
        Location center = target.getLocation();
        if (world == null) {
            super.finish(sender, target, type, reason);
            return;
        }

        List<Camel> camels = this.spawnCamels(world, center);
        if (camels.isEmpty()) {
            super.finish(sender, target, type, reason);
            return;
        }

        new BukkitRunnable() {
            private int tick;

            @Override
            public void run() {
                if (target == null || !target.isOnline()) {
                    cleanup(camels);
                    this.cancel();
                    return;
                }

                if (tick >= TOTAL_TICKS) {
                    world.createExplosion(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), 0.0F, false, false);
                    world.playSound(target.getLocation(), Sounds.BLOCK_ANVIL_LAND.get(), 1.0F, 0.75F);
                    cleanup(camels);
                    CamelCourtAnimation.this.finish(sender, target, type, reason);
                    this.cancel();
                    return;
                }

                Location targetLoc = target.getLocation();
                for (Camel camel : camels) {
                    if (camel == null || camel.isDead()) {
                        continue;
                    }

                    Location lookAt = camel.getLocation().setDirection(targetLoc.toVector().subtract(camel.getLocation().toVector()));
                    camel.teleport(lookAt);
                    camel.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
                }

                if (tick % 20 == 0) {
                    world.playSound(targetLoc, Sounds.ENTITY_WITHER_AMBIENT.get(), 0.15F, 1.25F);
                }

                tick++;
            }
        }.runTaskTimer(this.getPlugin(), 0L, 1L);
    }

    private List<Camel> spawnCamels(World world, Location center) {
        List<Camel> camels = new ArrayList<>();
        double[][] offsets = {
                {3.5D, 0.0D},
                {-3.5D, 0.0D},
                {0.0D, 3.5D},
                {0.0D, -3.5D}
        };

        for (double[] offset : offsets) {
            Location spawn = center.clone().add(offset[0], 0.0D, offset[1]);
            Camel camel = (Camel) world.spawnEntity(spawn, EntityType.CAMEL);
            camel.setAI(false);
            camel.setGravity(false);
            camel.setInvulnerable(true);
            camel.setCollidable(false);
            this.getPlugin().getMobUtils().setDefaultTags(camel);
            camels.add(camel);
        }

        return camels;
    }

    private static void cleanup(List<Camel> camels) {
        for (Camel camel : camels) {
            if (camel != null && !camel.isDead()) {
                camel.remove();
            }
        }
        camels.clear();
    }
}
