package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.Task;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;

public class SpitAnimation extends Animation {
   public SpitAnimation() {
      super("spit");
   }

   public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
      super.freeze(target);
      ArrayList<Llama> llamas = new ArrayList<Llama>();
      Location targetLocation = target.getLocation();
      Location[] var7 = this.getLocationsCross(targetLocation);
      int var8 = var7.length;

      for (int var9 = 0; var9 < var8; ++var9) {
         Location location = var7[var9];
         Llama llama = (Llama) target.getWorld().spawnEntity(location, EntityType.LLAMA);
         llama.setAdult();
         llama.setAI(false);
         llama.setGravity(false);
         llama.setInvulnerable(true);
         llama.teleport(
               llama.getLocation().setDirection(targetLocation.clone().subtract(llama.getLocation()).toVector()));
         llamas.add(llama);
      }

      Task.runTaskLater(() -> {
         World world = targetLocation.getWorld();

         for (int i = 0; i < 4; ++i) {
            Location location = target.getEyeLocation().clone().subtract(((Llama) llamas.get(i)).getEyeLocation());
            if (location.getX() != 0.0D) {
               location.subtract(0.5D * location.getX(), 0.0D, 0.0D);
            } else {
               location.subtract(0.0D, 0.0D, 0.5D * location.getZ());
            }

            location.add(((Llama) llamas.get(i)).getEyeLocation());
            world.spawnEntity(location, EntityType.LLAMA_SPIT);
         }

         world.playSound(targetLocation, Sound.ENTITY_LLAMA_SPIT, 1.0F, 1.0F);
         super.finish(sender, target, type, reason);
      }, 2L, TimeUnit.SECONDS);
      Task.runTaskLater(() -> {
         llamas.forEach((llama) -> {
            llama.remove();
         });
      }, 3L, TimeUnit.SECONDS);
   }

   private Location[] getLocationsCross(Location center) {
      World world = center.getWorld();
      double x = center.getX();
      double y = center.getY();
      double z = center.getZ();
      Location[] cross = new Location[] { new Location(world, x - 1.5D, y, z), new Location(world, x, y, z + 1.5D),
            new Location(world, x, y, z - 1.5D), new Location(world, x + 1.5D, y, z) };
      return cross;
   }
}