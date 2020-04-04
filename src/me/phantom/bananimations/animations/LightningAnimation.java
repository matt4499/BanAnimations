package me.phantom.bananimations.animations;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.RepeatingTaskHelper;
import me.phantom.bananimations.utils.Task;
import me.phantom.bananimations.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class LightningAnimation extends Animation {
   private ItemStack itemBeef;

   public LightningAnimation() {
      super("lightning", (String)null);
      this.itemBeef = new ItemStack(Material.BEEF);
   }

   public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
      super.freeze(target);
      World world = target.getWorld();
      Location[] locations = this.getRandomLocations(target.getLocation());
      RepeatingTaskHelper taskHelper = new RepeatingTaskHelper();
      Item[] items = new Item[20];
      taskHelper.setTaskID(Task.scheduleSyncRepeatingTask(() -> {
         if (taskHelper.getCounter() >= 4) {
            int i;
            for(i = 0; i < 3; ++i) {
               world.strikeLightningEffect(target.getLocation());
            }

            for(i = 0; i < 20; ++i) {
               items[i] = target.getWorld().dropItemNaturally(target.getLocation(), this.itemBeef);
               Utils.setLore(items[i].getItemStack(), i + "");
               items[i].setPickupDelay(1000);
               items[i].setVelocity(new Vector(this.getRandom().nextDouble() * 0.8D - 0.4D, 0.6D, this.getRandom().nextDouble() * 0.8D - 0.4D));
            }

            super.punishPlayer(sender, target, type, reason);
            taskHelper.cancel();
         } else if (taskHelper.getCounter() < 3) {
            world.strikeLightningEffect(locations[taskHelper.getCounter()]);
         }

         taskHelper.increment();
      }, 0L, 20L));
      Task.runTaskLater(() -> {
         Item[] var1 = items;
         int var2 = items.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Item item = var1[var3];
            item.remove();
         }

      }, 5L, TimeUnit.SECONDS);
   }

   private Location[] getRandomLocations(Location targetLocation) {
      Location[] locations = new Location[3];
      Random random = new Random();
      World world = targetLocation.getWorld();
      double x = targetLocation.getX();
      double y = targetLocation.getY();
      double z = targetLocation.getZ();

      for(int i = 0; i < 3; ++i) {
         locations[i] = new Location(world, x + (random.nextDouble() * 8.0D - 4.0D), y, z + (random.nextDouble() * 8.0D - 4.0D));
      }

      return locations;
   }
}