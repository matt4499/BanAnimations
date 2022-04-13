package me.phantom.bananimations.animations;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.ArmorStandBuilder;
import me.phantom.bananimations.utils.RepeatingTaskHelper;
import me.phantom.bananimations.utils.Sounds;
import me.phantom.bananimations.utils.Task;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SwordFallAnimation extends Animation {
   private final ArmorStandBuilder ab;

   public SwordFallAnimation() {
      super("swordfall");
      this.ab = (new ArmorStandBuilder(this.getPlugin(), null)).withInvisible().holding(new ItemStack(Material.DIAMOND_SWORD));
   }

   public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
      super.freeze(target);
      List<ArmorStand> stands = this.spawnStands(this.getRandomLocations(target.getLocation().add(0.0D, 10.0D, 0.0D)), target.getLocation());
      Task.runTaskLater(() -> {

         for (ArmorStand stand : stands) {
            stand.remove();
         }

         super.finish(sender, target, type, reason);
      }, 3L, TimeUnit.SECONDS);
   }

   private List<ArmorStand> spawnStands(Location[] locations, Location targetLocation) {
      ArrayList<ArmorStand> stands = new ArrayList<>();
      World world = targetLocation.getWorld();
      double x = targetLocation.getX();
      double y = targetLocation.getY() + 1.0D;
      double z = targetLocation.getZ();
      RepeatingTaskHelper taskHelper = new RepeatingTaskHelper();
      taskHelper.setTaskID(Task.scheduleSyncRepeatingTask(() -> {
         if (taskHelper.getCounter() >= 19) {
            taskHelper.cancel();
         } else {
            ArmorStand stand = (ArmorStand)this.getPlugin().getMobUtils().setDefaultTags(this.ab.withLocation(locations[taskHelper.getCounter()]).spawn());
            stand.setRightArmPose(new EulerAngle(1.38D, this.getRandom().nextDouble() * 2.0D, 0.0D));
            stand.setVelocity(new Vector(0, -3, 0));
            stands.add(stand);
         }

         taskHelper.increment();
         if (stands.size() > 4) {
            stands.get(0).getWorld().playSound(stands.get(0).getLocation(), Sounds.ENTITY_PLAYER_HURT.get(), 0.3F, 1.0F);
            stands.get(0).remove();
            stands.remove(0);
            if (taskHelper.getCounter() % 4 == 0) {
               assert world != null;
               world.playEffect(new Location(world, x + this.getRandom().nextDouble() - 0.5D, y, z + this.getRandom().nextDouble() - 0.5D), Effect.STEP_SOUND, 152);
            }
         }

      }, 0L, 3L));
      return stands;
   }

   private Location[] getRandomLocations(Location targetLocation) {
      Location[] locations = new Location[20];
      Random random = new Random();
      World world = targetLocation.getWorld();
      double x = targetLocation.getX();
      double y = targetLocation.getY() + 10.0D;
      double z = targetLocation.getZ();

      for(int i = 0; i < 20; ++i) {
         locations[i] = new Location(world, x + (random.nextDouble() * 2.0D - 1.0D), y, z + (random.nextDouble() * 2.0D - 1.0D));
      }

      return locations;
   }
}