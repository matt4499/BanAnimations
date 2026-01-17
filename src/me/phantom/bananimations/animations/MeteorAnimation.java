package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.ArmorStandBuilder;
import me.phantom.bananimations.utils.RepeatingTaskHelper;
import me.phantom.bananimations.utils.Sounds;
import me.phantom.bananimations.utils.Task;

/**
 * Meteor Animation.
 * Summons meteors (ArmorStands) to crash into the player.
 */
public class MeteorAnimation extends Animation {
    
    private final ArmorStandBuilder smallMeteor;
    private final ArmorStandBuilder largeMeteor;

    public MeteorAnimation() {
        super("meteor");
        this.largeMeteor = (new ArmorStandBuilder(this.getPlugin(), null))
                .withHelmet(new ItemStack(Material.COAL_BLOCK))
                .withInvisible();
        this.smallMeteor = (new ArmorStandBuilder(this.getPlugin(), null))
                .withHelmet(new ItemStack(Material.COAL_BLOCK))
                .withInvisible()
                .withSmall();
    }

    @Override
    public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        super.freeze(target);
        World world = target.getWorld();
        ArrayList<ArmorStand> stands = new ArrayList<>();
        Location[] locations = this.getRandomLocations(target.getLocation());
        RepeatingTaskHelper taskHelper = new RepeatingTaskHelper();
        int trailID = this.playMeteorTrail(stands);
        
        taskHelper.setTaskID(Task.scheduleSyncRepeatingTask(() -> {
            if (stands.size() > 4) {
                world.playEffect(stands.get(0).getEyeLocation(), Effect.valueOf("SMOKE"), 1);
                world.playSound(stands.get(0).getEyeLocation(), Sounds.ENTITY_GENERIC_EXPLODE.get(), 0.3F, 1.0F);
                stands.get(0).remove();
                stands.remove(0);
            }

            ArmorStand stand;
            if (taskHelper.getCounter() > 29) {
                if (taskHelper.getCounter() == 33) {
                    stand = stands.get(0);
                    world.playEffect(stand.getLocation(), Effect.valueOf("SMOKE"), 1);
                    world.playSound(stand.getEyeLocation(), Sounds.ENTITY_GENERIC_EXPLODE.get(), 0.3F, 1.0F);
                    stand.remove();
                    super.finish(sender, target, type, reason);
                    Bukkit.getScheduler().cancelTask(trailID);
                    taskHelper.cancel();
                }

                stands.get(0).getWorld().playEffect(stands.get(0).getEyeLocation(), Effect.valueOf("SMOKE"), 1);
                stands.get(0).remove();
                stands.remove(0);
            } else if (taskHelper.getCounter() == 29) {
                stand = (ArmorStand) this.getPlugin().getMobUtils().setDefaultTags(
                        this.largeMeteor.withLocation(locations[taskHelper.getCounter()]).spawn());
                stand.setVelocity(this.getVelocity(target, stand));
                stands.add(stand);
            } else {
                stand = (ArmorStand) this.getPlugin().getMobUtils().setDefaultTags(
                        this.smallMeteor.withLocation(locations[taskHelper.getCounter()]).spawn());
                stand.setVelocity(this.getVelocity(target, stand).multiply(0.7D));
                stands.add(stand);
            }

            taskHelper.increment();
        }, 0L, 2L));
    }

    private Vector getVelocity(Player target, ArmorStand stand) {
        Location targetLocation = target.getLocation();
        Location standLocation = stand.getEyeLocation();
        Vector velocity = targetLocation.toVector().subtract(standLocation.toVector());
        velocity.normalize();
        return velocity;
    }

    private int playMeteorTrail(List<ArmorStand> stands) {
        return Task.scheduleSyncRepeatingTask(() -> stands.forEach(
                (stand) -> stand.getWorld().playEffect(stand.getLocation(), Effect.SMOKE, 2)), 0L, 2L);
    }

    private Location[] getRandomLocations(Location targetLocation) {
        Location[] locations = new Location[30];
        World world = targetLocation.getWorld();
        double x = targetLocation.getX();
        double y = targetLocation.getY() + 10.0D;
        double z = targetLocation.getZ();

        for (int i = 0; i < 30; ++i) {
            locations[i] = new Location(world, x + (this.getRandom().nextDouble() * 10.0D - 5.0D), 
                    y, z + (this.getRandom().nextDouble() * 10.0D - 5.0D));
        }

        return locations;
    }
}