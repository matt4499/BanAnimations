package me.phantom.bananimations.animations;

import java.util.Iterator;
import java.util.List;
import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.BanAnimations;
import me.phantom.bananimations.utils.RepeatingTaskHelper;
import me.phantom.bananimations.utils.Sounds;
import me.phantom.bananimations.utils.Utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

class GwenAnimation$1 extends BukkitRunnable {
   double radPerSec;
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

   GwenAnimation$1(GwenAnimation animation, RepeatingTaskHelper taskHelper, List<Guardian> guardians,
         CommandSender sender, Player target, AnimationType aniType, String reason, ArmorStand stand, Location location,
         World world, Location targetLocation) {
      this.animation = animation;
      this.taskHelper = taskHelper;
      this.guardians = guardians;
      this.sender = sender;
      this.target = target;
      this.aniType = aniType;
      this.reason = reason;
      this.stand = stand;
      this.guardianCenter = location;
      this.world = world;
      this.targetLocation = targetLocation;
      this.radPerSec = 0.0D;
   }

   public void run() {
      if (this.taskHelper.getCounter() == 115) {
         Location[] guardians = GwenAnimation.getLocationCross(this.animation, this.guardianCenter);
         int guardsLength = guardians.length;
         for (int target = 0; target < guardsLength; ++target) {
            Guardian guardian = this.guardians.get(target);
            guardian.remove();
         }
         this.taskHelper.cancel();
      } else if (this.taskHelper.getCounter() >= 77) {
         if (this.taskHelper.getCounter() == 77) {
            GwenAnimation.finish(this.animation, this.sender, this.target, this.aniType, this.reason);
            this.world.spawnParticle(Particle.EXPLOSION_HUGE, this.targetLocation.getX(), this.targetLocation.getY() + 1D, this.targetLocation.getZ(), 1);
            this.world.playSound(this.targetLocation, Sounds.ENTITY_GENERIC_EXPLODE.get(), 1.0F, 1.0F);
         }

         Location standLocation = this.stand.getEyeLocation();
         this.stand.remove();
         Location[] guardians = GwenAnimation.getLocationCross(this.animation, this.guardianCenter);
         int sender = guardians.length;

         for (int target = 0; target < sender; ++target) {
            Location location = guardians[target];
            location.setDirection(standLocation.clone().toVector().subtract(location.toVector()));
            Guardian guardian = this.guardians.get(target);
            guardian.setTarget(null);
            guardian.setTarget(this.stand);
            guardian.setVelocity(new Vector(0, 0, 0));
            guardian.teleport(location);
         }
      } else {
         double radSpot = 0.0D;

         for (Iterator<Guardian> location = this.guardians.iterator(); location.hasNext(); radSpot+=Math.toRadians(90)) {
            Guardian guardian = location.next();
            guardian.teleport(
                  Utils.getLocationAroundCircle(this.guardianCenter, GwenAnimation.getRadius(this.animation),
                        (this.radPerSec * (double) this.taskHelper.getCounter()) + radSpot));
            guardian.setTarget(this.stand);
         }

         this.radPerSec = this.radPerSec + Math.toRadians(0.25F);
      }

      this.taskHelper.increment();
   }
}
