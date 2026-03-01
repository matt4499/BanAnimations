package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.ArmorStandBuilder;
import me.phantom.bananimations.utils.Sounds;
import me.phantom.bananimations.utils.Utils;

public class CreakingCurseAnimation extends Animation {

    private static final int TOTAL_TICKS = 96;

    public CreakingCurseAnimation() {
        super("creakingcurse");
    }

    @Override
    public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        super.freeze(target);

        World world = target.getWorld();
        if (world == null) {
            super.finish(sender, target, type, reason);
            return;
        }

        Location center = target.getLocation().clone().add(0.0D, 0.2D, 0.0D);
        List<ArmorStand> stands = this.spawnCursedTotems(center, 4);

        new BukkitRunnable() {
            private int tick;

            @Override
            public void run() {
                if (target == null || !target.isOnline()) {
                    cleanup(stands);
                    this.cancel();
                    return;
                }

                if (tick >= TOTAL_TICKS) {
                    world.createExplosion(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), 0.0F, false, false);
                    world.playSound(target.getLocation(), Sounds.ENTITY_GENERIC_EXPLODE.get(), 0.7F, 0.6F);
                    cleanup(stands);
                    CreakingCurseAnimation.this.finish(sender, target, type, reason);
                    this.cancel();
                    return;
                }

                Location targetLoc = target.getLocation().clone().add(0.0D, 0.3D, 0.0D);
                double jitter = 0.08D + (0.0014D * tick);

                for (int i = 0; i < stands.size(); i++) {
                    ArmorStand stand = stands.get(i);
                    if (stand == null || stand.isDead()) {
                        continue;
                    }

                    double angle = Math.toRadians((i * 90.0D) + (tick * (3.0D + (tick * 0.04D))));
                    Location next = Utils.getLocationAroundCircle(targetLoc, 2.1D, angle)
                            .add((Math.random() - 0.5D) * jitter, 0.0D, (Math.random() - 0.5D) * jitter);
                    stand.teleport(next);
                    stand.setHeadPose(new EulerAngle(0.0D, angle, 0.0D));
                    stand.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
                }

                if (tick % 12 == 0) {
                    world.playSound(targetLoc, Sounds.ENTITY_WITHER_AMBIENT.get(), 0.09F, 0.65F);
                }

                tick++;
            }
        }.runTaskTimer(this.getPlugin(), 0L, 1L);
    }

    private List<ArmorStand> spawnCursedTotems(Location center, int count) {
        List<ArmorStand> stands = new ArrayList<>();
        ArmorStandBuilder builder = new ArmorStandBuilder(this.getPlugin(), null)
                .withNoGravity()
                .withNoArms()
                .withInvisible()
                .withHelmet(new ItemStack(Material.DARK_OAK_LOG));

        for (int i = 0; i < count; i++) {
            double angle = Math.toRadians((360.0D / count) * i);
            Location spawn = Utils.getLocationAroundCircle(center, 2.1D, angle);
            ArmorStand stand = (ArmorStand) this.getPlugin().getMobUtils().setDefaultTags(builder.withLocation(spawn).spawn());
            stands.add(stand);
        }

        return stands;
    }

    private static void cleanup(List<ArmorStand> stands) {
        for (ArmorStand stand : stands) {
            if (stand != null && !stand.isDead()) {
                stand.remove();
            }
        }
        stands.clear();
    }
}
