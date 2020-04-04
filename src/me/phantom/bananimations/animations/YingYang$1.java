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
   // $FF: synthetic field
   final RepeatingTaskHelper val$taskHelper;
   // $FF: synthetic field
   final List<Item> val$items;
   // $FF: synthetic field
   final ArmorStand[] val$stands;
   // $FF: synthetic field
   final World val$world;
   // $FF: synthetic field
   final Location val$targetLocation;
   // $FF: synthetic field
   final CommandSender val$sender;
   // $FF: synthetic field
   final Player val$target;
   // $FF: synthetic field
   final AnimationType val$type;
   // $FF: synthetic field
   final String val$reason;
   // $FF: synthetic field
   final YinYang this$0;

   YinYang$1(YinYang this$0, RepeatingTaskHelper var2, List<Item> var3, ArmorStand[] var4, World var5, Location var6, CommandSender var7, Player var8, AnimationType var9, String var10) {
      this.this$0 = this$0;
      this.val$taskHelper = var2;
      this.val$items = var3;
      this.val$stands = var4;
      this.val$world = var5;
      this.val$targetLocation = var6;
      this.val$sender = var7;
      this.val$target = var8;
      this.val$type = var9;
      this.val$reason = var10;
      this.radPerSec = 1.0F;
      this.yDif = 0.0D;
      this.up = true;
   }

   public void run() {
      int var3;
      if(this.val$taskHelper.getCounter() == 150) {
    	 this.val$world.playEffect(this.val$targetLocation, Effect.valueOf("SMOKE"), 1);
         this.val$world.playSound(this.val$targetLocation, Sounds.ENTITY_GENERIC_EXPLODE.get(), 1.0F, 1.0F);
         YinYang.access$001(this.this$0, this.val$sender, this.val$target, this.val$type, this.val$reason);
    	  }
         if(this.val$taskHelper.getCounter() == 160) {
         this.val$items.forEach((itemx) -> {
            itemx.remove();
         });
         ArmorStand[] var1 = this.val$stands;
         int var2 = var1.length;

         for(var3 = 0; var3 < var2; ++var3) {
            ArmorStand stand = var1[var3];
            stand.remove();
         }
      } else {
         int count = 0;
         ArmorStand[] var8 = this.val$stands;
         var3 = var8.length;

         for(int var10 = 0; var10 < var3; ++var10) {
            ArmorStand stand = var8[var10];
            Location nextPoint = Utils.getLocationAroundCircle(this.val$targetLocation, (double)YinYang.access$100(this.this$0), (double)(this.radPerSec / 20.0F * (float)this.val$taskHelper.getCounter() + (float)count));
            if (count == 0) {
               stand.teleport(new Location(this.val$world, nextPoint.getX(), nextPoint.getY() + this.yDif, nextPoint.getZ()));
            } else {
               stand.teleport(new Location(this.val$world, nextPoint.getX(), nextPoint.getY() - this.yDif, nextPoint.getZ()));
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
         if (this.this$0.getRandom().nextInt(2) == 0) {
            item = this.val$target.getWorld().dropItemNaturally(this.val$target.getEyeLocation(), YinYang.access$200(this.this$0));
         } else {
            item = this.val$target.getWorld().dropItemNaturally(this.val$target.getEyeLocation(), YinYang.access$300(this.this$0));
         }

         Utils.setLore(item.getItemStack(), this.this$0.getRandom().nextDouble() + "");
         item.setPickupDelay(1000);
         item.setVelocity(new Vector(this.this$0.getRandom().nextDouble() * 0.2D - 0.1D, 0.8D, this.this$0.getRandom().nextDouble() * 0.2D - 0.1D));
         this.val$items.add(item);
         if (this.val$items.size() % 40 == 0) {
            ((Item)this.val$items.get(0)).remove();
            this.val$items.remove(0);
         }
         this.val$taskHelper.increment();
      }

   }
}