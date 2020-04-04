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
   // $FF: synthetic field
   final RepeatingTaskHelper val$taskHelper;
   // $FF: synthetic field
   final List<Guardian> val$guardians;
   // $FF: synthetic field
   final CommandSender val$sender;
   // $FF: synthetic field
   final Player val$target;
   // $FF: synthetic field
   final AnimationType val$type;
   // $FF: synthetic field
   final String val$reason;
   // $FF: synthetic field
   final ArmorStand val$stand;
   // $FF: synthetic field
   final Location val$guardianCenter;
   // $FF: synthetic field
   final World val$world;
   // $FF: synthetic field
   final Location val$targetLocation;
   // $FF: synthetic field
   final GwenAnimation this$0;

   GwenAnimation$1(GwenAnimation this$0, RepeatingTaskHelper var2, List<Guardian> var3, CommandSender var4, Player var5, AnimationType var6, String var7, ArmorStand var8, Location var9, World var10, Location var11) {
      this.this$0 = this$0;
      this.val$taskHelper = var2;
      this.val$guardians = var3;
      this.val$sender = var4;
      this.val$target = var5;
      this.val$type = var6;
      this.val$reason = var7;
      this.val$stand = var8;
      this.val$guardianCenter = var9;
      this.val$world = var10;
      this.val$targetLocation = var11;
      this.radPerSec = 1.0F;
   }

   public void run() {
      if (this.val$taskHelper.getCounter() == 130) {
    	  int count = 0;
    	  Location[] var3 = GwenAnimation.access$100(this.this$0, this.val$guardianCenter);
    	  int var4 = var3.length;
    	  for(int var5 = 0; var5 < var4; ++var5) {
    	  Guardian guardian = (Guardian)this.val$guardians.get(count);
    	  guardian.remove();
    	  ++count;
    	  }
         this.val$taskHelper.cancel();
      } else if (this.val$taskHelper.getCounter() >= 110) {
         if (this.val$taskHelper.getCounter() == 110) {
            GwenAnimation.access$001(this.this$0, this.val$sender, this.val$target, this.val$type, this.val$reason);
            this.val$world.createExplosion(this.val$targetLocation.getX(), this.val$targetLocation.getY(), this.val$targetLocation.getZ(), 1.0F, false, false);
            this.val$world.playSound(this.val$targetLocation, Sounds.ENTITY_GENERIC_EXPLODE.get(), 1.0F, 1.0F);
         }

         int count = 0;
         Location standLocation = this.val$stand.getEyeLocation();
         this.val$stand.remove();
         Location[] var3 = GwenAnimation.access$100(this.this$0, this.val$guardianCenter);
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Location location = var3[var5];
            Guardian guardian = (Guardian)this.val$guardians.get(count);
            guardian.setTarget((LivingEntity)null);
            guardian.setTarget(this.val$stand);
            guardian.setVelocity(new Vector(0, 0, 0));
            location.setDirection(standLocation.clone().toVector().subtract(location.toVector()));
            guardian.teleport(location);
            ++count;
         }
      } else {
         double radSpot = 0.0D;

         for(Iterator<Guardian> var9 = this.val$guardians.iterator(); var9.hasNext(); ++radSpot) {
            Guardian guardian = (Guardian)var9.next();
            guardian.teleport(Utils.getLocationAroundCircle(this.val$guardianCenter, (double)GwenAnimation.access$200(this.this$0), (double)(this.radPerSec / 20.0F * (float)this.val$taskHelper.getCounter()) + radSpot));
            guardian.setTarget(this.val$stand);
         }

         this.radPerSec = (float)((double)this.radPerSec + 0.05D);
      }

      this.val$taskHelper.increment();
   }
}