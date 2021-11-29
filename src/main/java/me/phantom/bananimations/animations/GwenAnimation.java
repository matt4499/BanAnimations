package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;
import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.utils.Task;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.ArmorStandBuilder;
import me.phantom.bananimations.utils.RepeatingTaskHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;

public class GwenAnimation extends Animation {
   private ArmorStandBuilder ab = (new ArmorStandBuilder(this.getPlugin(), null)).withInvisible().withNoGravity();
   private float radius = 4.5F;

   public GwenAnimation() {
      super("gwen");
   }

   public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
      super.freeze(target);
      Location targetLocation = target.getLocation();
      World world = targetLocation.getWorld();
      ArmorStand stand = this.ab.withLocation(targetLocation).spawn();
      Location guardianCenter = targetLocation.clone().add(0.0D, 8.0D, 0.0D);
      List<Guardian> guardians = new ArrayList<Guardian>();
      Location[] locationsCross = this.getLocationsCross(guardianCenter);
      int locationCrossLength = locationsCross.length;

      for (int i = 0; i < locationCrossLength; ++i) {
         Location location = locationsCross[i];
         Guardian guardian = (Guardian) world.spawnEntity(location, EntityType.GUARDIAN);
         guardian.setTarget(null);
         guardian.setTarget(stand);
         this.getPlugin().getMobUtils().setDefaultTags(guardian);
         guardian.setGravity(false);
         guardian.setInvulnerable(true);
         guardians.add(guardian);
      }

      RepeatingTaskHelper taskHelper = new RepeatingTaskHelper();
      Task.scheduleSyncRepeatingTask(new GwenAnimation$1(this, taskHelper, guardians, sender, target, type, reason,
            stand, guardianCenter, world, targetLocation), 10L, 1L);

   }

   private Location[] getLocationsCross(Location center) {
      World world = center.getWorld();
      double x = center.getX();
      double y = center.getY();
      double z = center.getZ();
      Location[] cross = new Location[] { new Location(world, x + this.radius, y, z), new Location(world, x, y, z + this.radius), new Location(world, x - this.radius, y, z), new Location(world, x, y, z - this.radius) };
      return cross;
   }

   static boolean finish(GwenAnimation animation, CommandSender sender, Player player, AnimationType aniType, String reason) {
      return animation.finish(sender, player, aniType, reason);
   }

   static Location[] getLocationCross(GwenAnimation animation, Location x1) {
      return animation.getLocationsCross(x1);
   }

   static float getRadius(GwenAnimation animation) {
      return animation.radius;
   }
}