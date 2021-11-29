package me.phantom.bananimations.animations;

import java.util.List;
import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.utils.RepeatingTaskHelper;
import me.phantom.bananimations.utils.Sounds;
import me.phantom.bananimations.utils.Utils;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

class YinYang$1 extends BukkitRunnable {
   float radPerSec;
   double yDif;
   boolean up;
   final RepeatingTaskHelper taskHelper;
   final List<Item> items;
   final ArmorStand[] stands;
   final World world;
   final Location targetLocation;
   final CommandSender sender;
   final Player target;
   final AnimationType type;
   final String reason;
   final YinYang animation;

   YinYang$1(YinYang animation, RepeatingTaskHelper var2, List<Item> var3, ArmorStand[] var4, World var5, Location var6, CommandSender var7, Player var8, AnimationType var9, String var10) {
      this.animation = animation;
      this.taskHelper = var2;
      this.items = var3;
      this.stands = var4;
      this.world = var5;
      this.targetLocation = var6;
      this.sender = var7;
      this.target = var8;
      this.type = var9;
      this.reason = var10;
      this.radPerSec = 1.0F;
      this.yDif = 0.0D;
      this.up = true;
   }

   public void run() {
      int var3;
      if(this.taskHelper.getCounter() == 150) {
    	 this.world.playEffect(this.targetLocation, Effect.valueOf("SMOKE"), 1);
         this.world.playSound(this.targetLocation, Sounds.ENTITY_GENERIC_EXPLODE.get(), 1.0F, 1.0F);
         YinYang.finish(this.animation, this.sender, this.target, this.type, this.reason);
    	  }
         if(this.taskHelper.getCounter() == 160) {
         this.items.forEach((itemx) -> {
            itemx.remove();
         });
         ArmorStand[] var1 = this.stands;
         int var2 = var1.length;

         for(var3 = 0; var3 < var2; ++var3) {
            ArmorStand stand = var1[var3];
            stand.remove();
         }
      } else {
         int count = 0;
         ArmorStand[] var8 = this.stands;
         var3 = var8.length;

         for(int var10 = 0; var10 < var3; ++var10) {
            ArmorStand stand = var8[var10];
            Location nextPoint = Utils.getLocationAroundCircle(this.targetLocation, (double)YinYang.getRadius(this.animation), (double)(this.radPerSec / 20.0F * (float)this.taskHelper.getCounter() + (float)count));
            if (count == 0) {
               stand.teleport(new Location(this.world, nextPoint.getX(), nextPoint.getY() + this.yDif, nextPoint.getZ()));
            } else {
               stand.teleport(new Location(this.world, nextPoint.getX(), nextPoint.getY() - this.yDif, nextPoint.getZ()));
            }

            count += 3;
         }

         this.radPerSec = (float)((double)this.radPerSec + 0.06D);
         if (this.yDif >= 0.7D) {
            this.up = false;
         } else if (this.yDif <= -0.7D) {
            this.up = true;
         }

         if (this.up) {
            this.yDif += 0.01D;
         } else {
            this.yDif -= 0.01D;
         }

         Item item;
         if (this.animation.getRandom().nextInt(2) == 0) {
            item = this.target.getWorld().dropItemNaturally(this.target.getEyeLocation(), YinYang.getBlackWool(this.animation));
         } else {
            item = this.target.getWorld().dropItemNaturally(this.target.getEyeLocation(), YinYang.getWhiteWool(this.animation));
         }

         Utils.setLore(item.getItemStack(), this.animation.getRandom().nextDouble() + "");
         item.setPickupDelay(1000);
         item.setVelocity(new Vector(this.animation.getRandom().nextDouble() * 0.2D - 0.1D, 0.8D, this.animation.getRandom().nextDouble() * 0.2D - 0.1D));
         this.items.add(item);
         if (this.items.size() % 40 == 0) {
            ((Item)this.items.get(0)).remove();
            this.items.remove(0);
         }
         this.taskHelper.increment();
      }

   }
}