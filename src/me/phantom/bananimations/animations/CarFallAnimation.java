package me.phantom.bananimations.animations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.Sounds;
import me.phantom.bananimations.utils.Task;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

public class CarFallAnimation extends Animation {
   private Material[] typeOrderBase = new Material[5];
   private Material[] typeOrderFace = new Material[3];

   public CarFallAnimation() {
      super("carfall", (String)null);
      this.loadTypes();
   }

   @SuppressWarnings("deprecation")
public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
      super.freeze(target);
      World world = target.getWorld();
      Location targetLocation = target.getLocation();
      List<FallingBlock> blocks = new ArrayList<FallingBlock>();
      Location[] blockLocations = this.getLocationsBase(targetLocation.add(0.0D, 10.0D, 0.0D));

      int i;
      for(i = 0; i < 15; ++i) {
         blocks.add(this.setAttributes(world.spawnFallingBlock(blockLocations[i], this.typeOrderBase[i % 5], (byte)14)));
      }

      blockLocations = this.getLocationsFace(targetLocation.add(0.0D, 2.0D, -1.0D));

      for(i = 0; i < 9; ++i) {
         blocks.add(this.setAttributes(world.spawnFallingBlock(blockLocations[i], this.typeOrderFace[i % 3], (byte)14)));
      }

      Task.runTaskLater(() -> {
         blocks.forEach((fallingBlock) -> {
            fallingBlock.remove();
         });
         world.playEffect(target.getLocation(), Effect.valueOf("SMOKE"), 1);
         world.playSound(target.getLocation(), Sounds.ENTITY_GENERIC_EXPLODE.get(), 0.5F, 1.0F);
         super.punishPlayer(sender, target, type, reason);
      }, 2L, TimeUnit.SECONDS);
   }

   private Location[] getLocationsBase(Location startLocations) {
      Location[] locations = new Location[15];
      World world = startLocations.getWorld();
      double x = startLocations.getX() - 2.0D;
      double y = startLocations.getY() + 1.0D;
      double z = startLocations.getZ();
      int count = 0;

      for(int xn = 0; xn < 3; ++xn) {
         for(int yn = 0; yn < 5; ++yn) {
            locations[count] = new Location(world, x + (double)xn, y + (double)yn, z);
            ++count;
         }
      }

      return locations;
   }

   private Location[] getLocationsFace(Location startLocations) {
      Location[] locations = new Location[9];
      World world = startLocations.getWorld();
      double x = startLocations.getX() - 2.0D;
      double y = startLocations.getY();
      double z = startLocations.getZ();
      int count = 0;

      for(int xn = 0; xn < 3; ++xn) {
         for(int yn = 0; yn < 3; ++yn) {
            locations[count] = new Location(world, x + (double)xn, y + (double)yn, z);
            ++count;
         }
      }

      return locations;
   }

   private FallingBlock setAttributes(FallingBlock block) {
      block.setCustomName("banAnimations");
      block.setDropItem(false);
      return block;
   }

   private void loadTypes() {
      this.typeOrderBase[0] = Material.GRAY_TERRACOTTA;
      this.typeOrderBase[1] = Material.COAL_BLOCK;
      this.typeOrderBase[2] = Material.GRAY_TERRACOTTA;
      this.typeOrderBase[3] = Material.COAL_BLOCK;
      this.typeOrderBase[4] = Material.GRAY_TERRACOTTA;
      this.typeOrderFace[0] = Material.QUARTZ_BLOCK;
      this.typeOrderFace[1] = Material.GRAY_TERRACOTTA;
      this.typeOrderFace[2] = Material.GRAY_TERRACOTTA;
   }
}