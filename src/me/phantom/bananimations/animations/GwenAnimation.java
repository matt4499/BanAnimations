package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;
import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.utils.Task;
import me.phantom.bananimations.api.Animation;
// Removed ArmorStandBuilder import as it's no longer needed for this animation
import me.phantom.bananimations.utils.RepeatingTaskHelper;
import me.phantom.bananimations.utils.Utils; // Keep Utils for location calculations
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
// Removed ArmorStand import
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector; // Import Vector

public class GwenAnimation extends Animation {

   protected static final double GUARDIAN_ROTATION_RADIUS = 3.0;
   protected static final double GUARDIAN_CENTER_Y_OFFSET = 6.0;
   private static final double DEGREES_PER_TICK = 5.0;
   protected static final double ROTATION_SPEED_RAD_PER_TICK = Math.toRadians(DEGREES_PER_TICK);
   protected static final Vector UP_VECTOR = new Vector(0, 1, 0);

   /**
    * Constructor for the GwenAnimation.
    * Registers the animation with the name "gwen".
    */
   public GwenAnimation() {
      super("gwen");
   }

   /**
    * Plays the Gwen animation sequence on the target player.
    * Spawns guardians that rotate around the player using velocity control.
    *
    * @param sender The CommandSender who initiated the animation.
    * @param target The player to play the animation on.
    * @param type   The type of punishment associated (used by finish).
    * @param reason The reason for the punishment (used by finish).
    */
   @Override
   public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
      super.freeze(target);

      Location targetLocation = target.getLocation();
      World world = targetLocation.getWorld();

      if (world == null) {
         getPlugin().logger.warning("[BanAnimations] Cannot play GwenAnimation: Target player's world is null.");
         if (getPlugin().isFrozen(target)) {
            super.finish(sender, target, type, reason);
         }
         return;
      }
      Location guardianCenter = targetLocation.clone().add(0.0D, GUARDIAN_CENTER_Y_OFFSET, 0.0D);
      List<Guardian> guardians = spawnGuardians(world, guardianCenter, target);

      if (guardians.isEmpty()) {
         getPlugin().logger.warning("[BanAnimations] Failed to spawn any Guardians for GwenAnimation.");
         if (getPlugin().isFrozen(target)) {
            super.finish(sender, target, type, reason);
         }
         return;
      }
      
      RepeatingTaskHelper taskHelper = new RepeatingTaskHelper();
      
      int taskId = Task.scheduleSyncRepeatingTask(

              new GwenAnimation$1(this, taskHelper, guardians, sender, target, type, reason,
                      guardianCenter, world, targetLocation), // Removed ArmorStand, Added Locations
              10L, // Initial delay (ticks)
              1L   // Repeat every tick
      );


      if (taskId != -1) {
         taskHelper.setTaskID(taskId);
      } else {
         getPlugin().logger.severe("[BanAnimations] Failed to schedule GwenAnimation task!");
         cleanupEntitiesOnError(guardians);

         super.finish(sender, target, type, reason);
      }
   }

   /**
    * Spawns the four Guardians around the center point, targeting the player directly.
    *
    * @param world           The world to spawn guardians in.
    * @param guardianCenter The calculated center location for rotation.
    * @param actualTarget    The player the guardians should target.
    * @return A list of the spawned Guardian entities.
    */
   private List<Guardian> spawnGuardians(World world, Location guardianCenter, Player actualTarget) {
      List<Guardian> spawnedGuardians = new ArrayList<>();

      Location[] initialLocations = getInitialGuardianLocations(guardianCenter);

      for (Location loc : initialLocations) {
         try {
            Guardian guardian = (Guardian) world.spawnEntity(loc, EntityType.GUARDIAN);


            guardian.setTarget(actualTarget);
            getPlugin().getMobUtils().setDefaultTags(guardian);

            guardian.setGravity(false);
            guardian.setCustomNameVisible(false);
            guardian.setCustomName("bananimations-guardian");

            spawnedGuardians.add(guardian);
         } catch (Exception e) {
            getPlugin().logger.severe("[BanAnimations] Error spawning Guardian for GwenAnimation at " + loc + ": " + e.getMessage());
            e.printStackTrace();
         }
      }
      return spawnedGuardians;
   }

   /**
    * Helper method to safely remove spawned entities.
    *
    * @param guardians The list of guardians to remove.
    */
   private void cleanupEntitiesOnError(List<Guardian> guardians) {
      if (guardians != null) {
         for (Guardian g : guardians) {
            if (g != null && !g.isDead()) {
               g.remove();
            }
         }
         guardians.clear();
      }
   }

   /**
    * Calculates the initial spawn locations for the guardians in a cross pattern
    * relative to the center point.
    *
    * @param center The central location around which guardians spawn.
    * @return An array of four locations for the guardians.
    */
   Location[] getInitialGuardianLocations(Location center) {
      return new Location[]{
              // Spawn at 45, 135, 225, 315 degrees around the center
              Utils.getLocationAroundCircle(center, GUARDIAN_ROTATION_RADIUS, Math.toRadians(45)),
              Utils.getLocationAroundCircle(center, GUARDIAN_ROTATION_RADIUS, Math.toRadians(135)),
              Utils.getLocationAroundCircle(center, GUARDIAN_ROTATION_RADIUS, Math.toRadians(225)),
              Utils.getLocationAroundCircle(center, GUARDIAN_ROTATION_RADIUS, Math.toRadians(315))
      };
   }



   /**
    * Static helper method called by GwenAnimation$1 to finish the animation.
    * This ensures the correct `finish` method from the base Animation class is called,
    * handling unfreezing and punishment execution according to the original plugin logic.
    *
    * @param animation The instance of GwenAnimation running the task.
    * @param sender    The CommandSender who initiated the animation.
    * @param player    The target player.
    * @param aniType   The AnimationType defining the punishment.
    * @param reason    The reason for the punishment.
    */
   static void finish(GwenAnimation animation, CommandSender sender, Player player, AnimationType aniType, String reason) {

      animation.finish(sender, player, aniType, reason);
   }


}