package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.ArmorStandBuilder;
import me.phantom.bananimations.utils.RepeatingTaskHelper;
import me.phantom.bananimations.utils.Sounds;
import me.phantom.bananimations.utils.Utils;

/**
 * YinYang Animation.
 * Spawns rotating ArmorStands (white/black wool) and drops items.
 */
public class YinYang extends Animation {
    
    private final ItemStack whiteWool;
    private final ItemStack blackWool;
    private final ArmorStandBuilder whiteAB;
    private final ArmorStandBuilder blackAB;
    private final float radius;

    public YinYang() {
        super("yinyang");
        this.whiteWool = new ItemStack(Material.WHITE_WOOL);
        this.blackWool = new ItemStack(Material.BLACK_WOOL);
        this.whiteAB = (new ArmorStandBuilder(this.getPlugin(), null))
                .withInvisible()
                .withNoGravity()
                .withHelmet(this.whiteWool);
        this.blackAB = (new ArmorStandBuilder(this.getPlugin(), null))
                .withInvisible()
                .withNoGravity()
                .withHelmet(this.blackWool);
        this.radius = 1.5F;
    }

    @Override
    public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        super.freeze(target);
        Location targetLocation = target.getLocation();
        World world = targetLocation.getWorld();
        ArmorStand[] stands = new ArmorStand[2];
        Location[] locations = this.getSpawnLocations(targetLocation);
        
        stands[0] = this.whiteAB.withLocation(locations[0]).spawn();
        stands[1] = this.blackAB.withLocation(locations[1]).spawn();
        
        List<Item> items = new ArrayList<>();
        RepeatingTaskHelper taskHelper = new RepeatingTaskHelper();
        
        new YinYangTask(this, taskHelper, items, stands, world, targetLocation, sender, target, type, reason)
                .runTaskTimer(getPlugin(), 0L, 1L);
    }

    private Location[] getSpawnLocations(Location location) {
        World world = location.getWorld();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        return new Location[]{
            new Location(world, x, y, z), 
            new Location(world, x, y, z)
        };
    }

    /**
     * YinYangTask Runnable.
     * Rotating armorstands animation.
     */
    private static class YinYangTask extends BukkitRunnable {
        
        float radPerSec;
        double yDif;
        boolean up;
        final RepeatingTaskHelper taskHelper;
        final List<Item> items;
        final ArmorStand[] stands;
        final World world;
        final Location targetLocation;
        final CommandSender sender;
        final Player target;
        final AnimationType type;
        final String reason;
        final YinYang animation;

        YinYangTask(YinYang animation, RepeatingTaskHelper taskHelper, List<Item> items, ArmorStand[] stands,
                World world, Location targetLocation, CommandSender sender, Player target, 
                AnimationType type, String reason) {
            this.animation = animation;
            this.taskHelper = taskHelper;
            this.items = items;
            this.stands = stands;
            this.world = world;
            this.targetLocation = targetLocation;
            this.sender = sender;
            this.target = target;
            this.type = type;
            this.reason = reason;
            this.radPerSec = 1.0F;
            this.yDif = 0.0D;
            this.up = true;
        }

        @Override
        public void run() {
            if (this.taskHelper.getCounter() == 150) {
                if (this.world != null) {
                    this.world.playEffect(this.targetLocation, Effect.valueOf("SMOKE"), 1);
                    this.world.playSound(this.targetLocation, Sounds.ENTITY_GENERIC_EXPLODE.get(), 1.0F, 1.0F);
                }
                this.animation.finish(this.sender, this.target, this.type, this.reason);
            }
            
            if (this.taskHelper.getCounter() >= 160) {
                this.items.forEach(Entity::remove);
                for (ArmorStand stand : this.stands) {
                    if (stand != null) stand.remove();
                }
                this.cancel();
            } else {
                int count = 0;
                for (ArmorStand stand : this.stands) {
                    if (stand != null) {
                        Location nextPoint = Utils.getLocationAroundCircle(this.targetLocation, 
                                this.animation.radius, 
                                this.radPerSec / 20.0F * (float) this.taskHelper.getCounter() + (float) count);
                        
                        double currentDiff = (count == 0) ? this.yDif : -this.yDif;
                        stand.teleport(new Location(this.world, nextPoint.getX(), 
                                nextPoint.getY() + currentDiff, nextPoint.getZ()));
                    }
                    count += 3;
                }

                this.radPerSec = (float) ((double) this.radPerSec + 0.06D);
                if (this.yDif >= 0.7D) {
                    this.up = false;
                } else if (this.yDif <= -0.7D) {
                    this.up = true;
                }

                if (this.up) {
                    this.yDif += 0.01D;
                } else {
                    this.yDif -= 0.01D;
                }

                Item item;
                if (this.animation.getRandom().nextInt(2) == 0) {
                    item = this.target.getWorld().dropItemNaturally(this.target.getEyeLocation(), 
                            this.animation.blackWool);
                } else {
                    item = this.target.getWorld().dropItemNaturally(this.target.getEyeLocation(), 
                            this.animation.whiteWool);
                }

                Utils.setLore(item.getItemStack(), this.animation.getRandom().nextDouble() + "");
                item.setPickupDelay(1000);
                item.setVelocity(new Vector(
                        this.animation.getRandom().nextDouble() * 0.2D - 0.1D, 
                        0.8D, 
                        this.animation.getRandom().nextDouble() * 0.2D - 0.1D));
                this.items.add(item);
                
                if (this.items.size() % 40 == 0 && this.items.size() > 0) {
                    this.items.get(0).remove();
                    this.items.remove(0);
                }
                this.taskHelper.increment();
            }
        }
    }
}