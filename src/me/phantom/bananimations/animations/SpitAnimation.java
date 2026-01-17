package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.Task;

/**
 * Spit Animation.
 * Spawns llamas that spit at the player.
 */
public class SpitAnimation extends Animation {
    
    public SpitAnimation() {
        super("spit");
    }

    @Override
    public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        super.freeze(target);
        List<Llama> llamas = new ArrayList<>();
        Location targetLocation = target.getLocation();
        Location[] locationsCross = this.getLocationsCross(targetLocation);

        for (Location location : locationsCross) {
            Llama llama = (Llama) target.getWorld().spawnEntity(location, EntityType.LLAMA);
            llama.setAdult();
            llama.setAI(false);
            llama.setGravity(false);
            llama.setInvulnerable(true);
            llama.teleport(llama.getLocation()
                    .setDirection(targetLocation.clone().subtract(llama.getLocation()).toVector()));
            llamas.add(llama);
        }

        Task.runTaskLater(() -> {
            World world = targetLocation.getWorld();

            for (int i = 0; i < 4; ++i) {
                Location location = target.getEyeLocation().clone()
                        .subtract(((Llama) llamas.get(i)).getEyeLocation());
                if (location.getX() != 0.0D) {
                    location.subtract(0.5D * location.getX(), 0.0D, 0.0D);
                } else {
                    location.subtract(0.0D, 0.0D, 0.5D * location.getZ());
                }

                location.add(((Llama) llamas.get(i)).getEyeLocation());
                if (world != null) {
                    world.spawnEntity(location, EntityType.LLAMA_SPIT);
                }
            }

            world.playSound(targetLocation, Sound.ENTITY_LLAMA_SPIT, 1.0F, 1.0F);
            super.finish(sender, target, type, reason);
        }, 2L, TimeUnit.SECONDS);

        Task.runTaskLater(() -> {
            llamas.forEach(Entity::remove);
        }, 3L, TimeUnit.SECONDS);
    }

    private Location[] getLocationsCross(Location center) {
        World world = center.getWorld();
        double x = center.getX();
        double y = center.getY();
        double z = center.getZ();
        return new Location[] {
            new Location(world, x - 1.5D, y, z),
            new Location(world, x, y, z + 1.5D),
            new Location(world, x, y, z - 1.5D),
            new Location(world, x + 1.5D, y, z)
        };
    }
}