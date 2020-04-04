package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;
import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.animations.GwenAnimation$1;
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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class GwenAnimation extends Animation {
   private ArmorStandBuilder ab = (new ArmorStandBuilder(this.getPlugin(), (Location)null)).withInvisible().withNoGravity();
   private final float radius = 3.0F;

   public GwenAnimation() {
      super("gwen", "1.8");
   }

   public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
      super.freeze(target);
      Location targetLocation = target.getLocation();
      World world = targetLocation.getWorld();
      ArmorStand stand = this.ab.withLocation(targetLocation).spawn();
      Location guardianCenter = targetLocation.clone().add(0.0D, 7.0D, 0.0D);
      List<Guardian> guardians = new ArrayList<Guardian>();
      Location[] var10 = this.getLocationsCross(guardianCenter);
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         Location location = var10[var12];
         Guardian guardian = (Guardian)world.spawnEntity(location, EntityType.GUARDIAN);
         guardian.setTarget((LivingEntity)null);
         guardian.setTarget(stand);
         this.getPlugin().getMobUtils().setDefaultTags(guardian);
         guardian.setGravity(false);
         guardian.setCustomNameVisible(false);
         guardian.setCustomName("bananimations-guardian");
         guardians.add(guardian);
      }

      RepeatingTaskHelper taskHelper = new RepeatingTaskHelper();
      //setTaskID(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.getPlugin(), new GwenAnimation$1(this, taskHelper, guardians, sender, target, type, reason, stand, guardianCenter, world, targetLocation), 10L, 1L));
      Task.scheduleSyncRepeatingTask(new GwenAnimation$1(this, taskHelper, guardians, sender, target, type, reason, stand, guardianCenter, world, targetLocation), 10L, 1L);
      
   }

   private Location[] getLocationsCross(Location center) {
      World world = center.getWorld();
      double x = center.getX();
      double y = center.getY();
      double z = center.getZ();
      Location[] cross = new Location[]{new Location(world, x + 3.0D, y, z), new Location(world, x, y, z + 3.0D), new Location(world, x - 3.0D, y, z), new Location(world, x, y, z - 3.0D)};
      return cross;
   }

   // $FF: synthetic method
   static boolean access$001(GwenAnimation x0, CommandSender x1, Player x2, AnimationType x3, String x4) {
      return x0.punishPlayer(x1, x2, x3, x4);
   }

   // $FF: synthetic method
   static Location[] access$100(GwenAnimation x0, Location x1) {
      return x0.getLocationsCross(x1);
   }

   // $FF: synthetic method
   static float access$200(GwenAnimation x0) {
      return x0.radius;
   }
}