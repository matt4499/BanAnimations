package me.phantom.bananimations.api;

import java.util.Random;
import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.BanAnimations;
import me.phantom.bananimations.events.AnimationStartEvent;
import me.phantom.bananimations.utils.Sounds;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public abstract class Animation {
   private String name;
   private Random random;
   private BanAnimations plugin;

   public Animation(String name) {
      this.name = name;
      this.plugin = (BanAnimations) Bukkit.getPluginManager().getPlugin("BanAnimations");
      this.random = new Random();
   }

   public void callAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
      Bukkit.getServer().getPluginManager().callEvent(new AnimationStartEvent(sender, target, type, reason, this.name));
      this.playAnimation(sender, target, type, reason);
   }

   public abstract void playAnimation(CommandSender var1, Player var2, AnimationType var3, String var4);

   public boolean punishPlayer(CommandSender sender, Player target, AnimationType type, String reason) {
      if (type != AnimationType.TEST) {
         Bukkit.dispatchCommand(sender, type + " " + target.getName() + " " + reason);
         target.playSound(target.getEyeLocation(), Sounds.ENTITY_WITHER_AMBIENT.get(), 0.1F, 2.0F);
         this.playSound(target, sender, Sounds.ENTITY_WITHER_AMBIENT.get(), 0.1F, 2.0F);
      }

      this.unFreeze(target);
      return true;
   }

   public void playSound(Player target, CommandSender sender, Sound sound, float volume, float pitch) {
      target.playSound(target.getEyeLocation(), sound, volume, pitch);
      if (sender instanceof Player) {
         Player player = (Player) sender;
         player.playSound(player.getEyeLocation(), sound, volume, pitch);
      }

   }

   public void freeze(Player target) {
      target.setVelocity(new Vector(0, 0, 0));
      if (target.getLocation().add(0.0D, -1.0D, 0.0D).getBlock().getType() == Material.AIR) {
         Block barrier = target.getLocation().add(0.0D, -1.0D, 0.0D).getBlock();
         if (barrier.getType() == Material.AIR) {
            barrier.setType(Material.BARRIER);
            barrier.setMetadata("bananimations_barrier", new FixedMetadataValue(this.plugin, ""));
         }
         Location targetLocation = target.getLocation();
         Location teleportLocation = new Location(targetLocation.getWorld(), targetLocation.getX(),
               (double) targetLocation.getBlockY(), targetLocation.getZ());
         teleportLocation.setDirection(targetLocation.getDirection());
         target.teleport(targetLocation);
      }

      this.plugin.freeze(target);
   }

   private void unFreeze(Player target) {
      if (target.getLocation().add(0.0D, -1.0D, 0.0D).getBlock().hasMetadata("bananimations_barrier")) {
         Block barrier = target.getLocation().add(0.0D, -1.0D, 0.0D).getBlock();
         barrier.removeMetadata("bananimations_barrier", this.plugin);
         barrier.setType(Material.AIR);
      }
      this.plugin.unFreeze(target);
   }

   public BanAnimations getPlugin() {
      return this.plugin;
   }

   public Random getRandom() {
      return this.random;
   }

   public String getName() {
      return this.name;
   }

   public void hook() {
      try {
         this.plugin.registerAnimation(this, this.name);
      } catch (NumberFormatException var2) {
         Bukkit.getLogger()
               .severe("The animation " + this.name + "'s version depend is an incorrect version! Hook aborted!");
      }

   }
}