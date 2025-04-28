package me.phantom.bananimations.animations;

import java.util.List;
import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.utils.RepeatingTaskHelper;
import me.phantom.bananimations.utils.Sounds;
import me.phantom.bananimations.utils.Utils; // Keep Utils if needed

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
// Removed ArmorStand import - not used here directly
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector; // Keep Vector for velocity calculations

/**
 * GwenAnimation$1 Runnable Task (Adapted & Timings Adjusted)
 * Handles the frame-by-frame updates for the GwenAnimation.
 * Uses velocity control for smooth rotation while AI handles laser targeting.
 * Extends rotation duration and triggers explosion/finish immediately at the end.
 */
class GwenAnimation$1 extends BukkitRunnable {

   private final GwenAnimation animation;
   private final RepeatingTaskHelper taskHelper;
   private final List<Guardian> guardians;
   private final CommandSender sender;
   private final Player target;
   private final AnimationType aniType;
   private final String reason;

   private final Location guardianCenter;
   private final World world;
   private final Location targetLocation; // Store player's location for effects

   // Total duration in ticks before explosion/cleanup
   private static final int TOTAL_ANIMATION_TICKS = 130; // Approx 6.5 seconds total runtime remains the same

   /**
    * Creates the runnable task for the Gwen Animation.
    *
    * @param animation      The parent GwenAnimation instance.
    * @param taskHelper     Helper for managing task repetition.
    * @param guardians      The list of spawned Guardian entities.
    * @param sender         The command sender initiating the animation.
    * @param target         The target player.
    * @param aniType        The type of punishment (for finish).
    * @param reason         The reason for punishment (for finish).
    * @param guardianCenter The calculated center for guardian rotation.
    * @param world          The world where the animation occurs.
    * @param targetLocation The specific location of the target player (for effects).
    */
   GwenAnimation$1(GwenAnimation animation, RepeatingTaskHelper taskHelper, List<Guardian> guardians,
                   CommandSender sender, Player target, AnimationType aniType, String reason,
                   Location guardianCenter, World world, Location targetLocation) {
      this.animation = animation;
      this.taskHelper = taskHelper;
      this.guardians = guardians;
      this.sender = sender;
      this.target = target;
      this.aniType = aniType;
      this.reason = reason;
      this.guardianCenter = guardianCenter;
      this.world = world;
      this.targetLocation = targetLocation;
   }

   /**
    * Executes each tick of the animation.
    * Handles guardian rotation via velocity until the final tick,
    * then triggers explosion, finish, and cleanup.
    */
   @Override
   public void run() {
      try {

         if (target == null || !target.isOnline()) {
            animation.getPlugin().logger.warning("[BanAnimations] GwenAnimation target player logged off or invalid mid-animation.");
            cleanupAndCancel();
            return;
         }

         if (guardians == null || guardians.isEmpty()) {
            animation.getPlugin().logger.warning("[BanAnimations] GwenAnimation guardians list is null or empty mid-animation.");
            cleanupAndCancel();
            return;
         }

         int counter = this.taskHelper.getCounter();


         if (counter >= TOTAL_ANIMATION_TICKS) {

            playExplosionEffect();


            GwenAnimation.finish(this.animation, this.sender, this.target, this.aniType, this.reason);


            cleanupAndCancel();
            return;
         }



         rotateGuardians();

         this.taskHelper.increment(); // Increment the tick counter

      } catch (Exception e) {
         this.animation.getPlugin().logger.severe("[BanAnimations] Error during GwenAnimation task: " + e.getMessage());
         e.printStackTrace();

         cleanupGuardians();
         this.taskHelper.cancel();

         GwenAnimation.finish(this.animation, this.sender, this.target, this.aniType, this.reason);
      }
   }

   /**
    * Plays the explosion effect and sound at the target's location.
    */
   private void playExplosionEffect() {
      if (this.world != null && this.targetLocation != null) {
         try {

            this.world.createExplosion(this.targetLocation.getX(), this.targetLocation.getY(),
                    this.targetLocation.getZ(), 1.0F, false, false); // Small, no fire, no block damage
            // Play explosion sound
            this.world.playSound(this.targetLocation, Sounds.ENTITY_GENERIC_EXPLODE.get(), 1.0F, 1.0F);
         } catch (Exception e) {
            animation.getPlugin().logger.warning("[BanAnimations] Could not play explosion/sound for GwenAnimation: " + e.getMessage());
         }
      } else {
         animation.getPlugin().logger.warning("[BanAnimations] World or TargetLocation is null, cannot play explosion for GwenAnimation.");
      }
   }


   /**
    * Cleans up guardian entities and cancels the BukkitRunnable task.
    */
   private void cleanupAndCancel() {
      cleanupGuardians();
      this.taskHelper.cancel();
   }


   /**
    * Handles the rotation of guardians using calculated velocity vectors
    * for smooth circular motion. AI remains enabled for laser targeting.
    */
   private void rotateGuardians() {
      double speedPerTick = GwenAnimation.GUARDIAN_ROTATION_RADIUS * GwenAnimation.ROTATION_SPEED_RAD_PER_TICK;

      if (target == null || !target.isOnline()) return;

      for (Guardian guardian : this.guardians) {
         if (guardian == null || guardian.isDead() || guardian.getWorld() == null || this.guardianCenter == null) {
            continue;
         }

         guardian.setTarget(this.target);

         Vector radialVector = guardian.getLocation().toVector().subtract(this.guardianCenter.toVector());
         if (radialVector.lengthSquared() < 0.001) {
            guardian.setVelocity(new Vector(0, 0, 0));
            continue;
         }
         Vector tangentVector = GwenAnimation.UP_VECTOR.getCrossProduct(radialVector).normalize();
         Vector desiredVelocity = tangentVector.multiply(speedPerTick);

         guardian.setVelocity(desiredVelocity);
      }
   }

   /** Safely removes all guardian entities from the world and clears the list. */
   private void cleanupGuardians() {
      if (this.guardians != null) {
         // Use iterator to safely remove while iterating if needed, though direct removal is fine here
         for (Guardian guardian : this.guardians) {
            if (guardian != null && !guardian.isDead()) {
               guardian.remove(); // Remove from world
            }
         }
         this.guardians.clear();
      }
   }
}