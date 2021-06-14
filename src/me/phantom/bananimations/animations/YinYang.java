package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.ArmorStandBuilder;
import me.phantom.bananimations.utils.RepeatingTaskHelper;
import me.phantom.bananimations.utils.Task;

public class YinYang extends Animation {
   private ItemStack whiteWool;
   private ItemStack blackWool;
   private ArmorStandBuilder whiteAB;
   private ArmorStandBuilder blackAB;
   private final float radius;

   public YinYang() {
      super("yinyang");
      this.whiteWool = new ItemStack(Material.WHITE_WOOL);
      this.blackWool = new ItemStack(Material.BLACK_WOOL);
      this.whiteAB = (new ArmorStandBuilder(this.getPlugin(), (Location)null)).withInvisible().withNoGravity().withHelmet(this.whiteWool);
      this.blackAB = (new ArmorStandBuilder(this.getPlugin(), (Location)null)).withInvisible().withNoGravity().withHelmet(this.blackWool);
      this.radius = 1.5F;
   }

public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
      super.freeze(target);
      Location targetLocation = target.getLocation();
      World world = targetLocation.getWorld();
      ArmorStand[] stands = new ArmorStand[2];
      Location[] locations = this.getSpawnLocations(targetLocation);
      stands[0] = this.whiteAB.withLocation(locations[0]).spawn();
      stands[1] = this.blackAB.withLocation(locations[1]).spawn();
      List<Item> items = new ArrayList<Item>();
      RepeatingTaskHelper taskHelper = new RepeatingTaskHelper();
      //taskHelper.setTaskID(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.getPlugin(), new YinYang$1(this, taskHelper, items, stands, world, targetLocation, sender, target, type, reason), 0L, 1L));
      Task.scheduleSyncRepeatingTask(new YinYang$1(this, taskHelper, items, stands, world, targetLocation, sender, target, type, reason), 0L, 1L);
   }

   private Location[] getSpawnLocations(Location location) {
      World world = location.getWorld();
      double x = location.getX();
      double y = location.getY();
      double z = location.getZ();
      Location[] locations = new Location[]{new Location(world, x, y, z), new Location(world, x, y, z)};
      return locations;
   }
   static boolean finish(YinYang animation, CommandSender sender, Player player, AnimationType aniType, String reason) {
      return animation.finish(sender, player, aniType, reason);
   }
   static float getRadius(YinYang animation) {
      return animation.radius;
   }
   static ItemStack getBlackWool(YinYang animation) {
      return animation.blackWool;
   }
   static ItemStack getWhiteWool(YinYang animation) {
      return animation.whiteWool;
   }
}