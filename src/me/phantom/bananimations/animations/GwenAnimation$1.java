package me.phantom.bananimations.animations;

import java.util.Iterator;
import java.util.List;
import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.utils.RepeatingTaskHelper;
import me.phantom.bananimations.utils.Sounds;
import me.phantom.bananimations.utils.Utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

class GwenAnimation$1 extends BukkitRunnable {
   float radPerSec;
   final RepeatingTaskHelper taskHelper;
   final List<Guardian> guardians;
   final CommandSender sender;
   final Player target;
   final AnimationType aniType;
   final String reason;
   final ArmorStand stand;
   final Location guardianCenter;
   final World world;
   final Location targetLocation;
   final GwenAnimation animation;

   GwenAnimation$1(GwenAnimation animation, RepeatingTaskHelper taskHelper, List<Guardian> guardians, CommandSender sender, Player target, AnimationType aniType, String reason, ArmorStand stand, Location location, World world, Location targetLocation) {
      this.animation = animation;
      this.taskHelper = taskHelper;
      this.guardians = guardians;
      this.sender = sender;
      this.target = target;
      this.aniType = aniType;
      this.reason =reason ;
      this.stand = stand;
      this.guardianCenter = location;
      this.world = world;
      this.targetLocation = targetLocation;
      this.radPerSec = 1.0F;
   }

   public void run() {
      if (this.taskHelper.getCounter() == 130) {
    	  int count = 0;
    	  Location[] guardians = GwenAnimation.getLocationCross(this.animation, this.guardianCenter);
    	  int guardsLength = guardians.length;
    	  for(int target = 0; target < guardsLength; ++target) {
    	  Guardian guardian = (Guardian)this.guardians.get(count);
    	  guardian.remove();
    	  ++count;
    	  }
         this.taskHelper.cancel();
      } else if (this.taskHelper.getCounter() >= 110) {
         if (this.taskHelper.getCounter() == 110) {
            GwenAnimation.finish(this.animation, this.sender, this.target, this.aniType, this.reason);
            this.world.createExplosion(this.targetLocation.getX(), this.targetLocation.getY(), this.targetLocation.getZ(), 1.0F, false, false);
            this.world.playSound(this.targetLocation, Sounds.ENTITY_GENERIC_EXPLODE.get(), 1.0F, 1.0F);
         }

         int count = 0;
         Location standLocation = this.stand.getEyeLocation();
         this.stand.remove();
         Location[] guardians = GwenAnimation.getLocationCross(this.animation, this.guardianCenter);
         int sender = guardians.length;

         for(int target = 0; target < sender; ++target) {
            Location location = guardians[target];
            Guardian guardian = (Guardian)this.guardians.get(count);
            guardian.setTarget((LivingEntity)null);
            guardian.setTarget(this.stand);
            guardian.setVelocity(new Vector(0, 0, 0));
            location.setDirection(standLocation.clone().toVector().subtract(location.toVector()));
            guardian.teleport(location);
            ++count;
         }
      } else {
         double radSpot = 0.0D;

         for(Iterator<Guardian> location = this.guardians.iterator(); location.hasNext(); ++radSpot) {
            Guardian guardian = (Guardian)location.next();
            guardian.teleport(Utils.getLocationAroundCircle(this.guardianCenter, (double)GwenAnimation.getRadius(this.animation), (double)(this.radPerSec / 20.0F * (float)this.taskHelper.getCounter()) + radSpot));
            guardian.setTarget(this.stand);
         }

         this.radPerSec = (float)((double)this.radPerSec + 0.05D);
      }

      this.taskHelper.increment();
   }
}