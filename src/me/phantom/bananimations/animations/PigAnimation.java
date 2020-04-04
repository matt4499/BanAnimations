package me.phantom.bananimations.animations;

import java.util.concurrent.TimeUnit;
import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.Sounds;
import me.phantom.bananimations.utils.Task;
import me.phantom.bananimations.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PigAnimation extends Animation {
   private ItemStack itemPork;

   public PigAnimation() {
      super("pig", (String)null);
      this.itemPork = new ItemStack(Material.BEEF);
   }

   public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
      super.freeze(target);
      Location location = target.getLocation();
      location.setDirection(target.getLocation().getDirection());
      Pig pig = (Pig)target.getWorld().spawnEntity(location, EntityType.PIG);
      this.getPlugin().getMobUtils().setTags(pig, new String[]{"Invulnerable", "Silent", "NoAI"});
      pig.teleport(location);
      pig.setAdult();
      pig.setCustomName(target.getName());
      pig.setCustomNameVisible(true);
      target.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
      Item[] items = new Item[4];
      this.playSound(target, sender, Sounds.ENTITY_PIG_AMBIENT.get(), 0.5F, 1.0F);
      Task.runTaskLater(() -> {
         for(int i = 0; i < 4; ++i) {
            items[i] = target.getWorld().dropItemNaturally(target.getLocation(), this.itemPork);
            Utils.setLore(items[i].getItemStack(), i + "");
            items[i].setPickupDelay(1000);
            items[i].setVelocity(new Vector(this.getRandom().nextDouble() * 0.2D - 0.1D, 0.6D, this.getRandom().nextDouble() * 0.2D - 0.1D));
         }

         pig.remove();
         this.playSound(target, sender, Sounds.ENTITY_PIG_DEATH.get(), 0.5F, 1.0F);
      }, 1L, TimeUnit.SECONDS);
      Task.runTaskLater(() -> {
         Item[] var6 = items;
         int var7 = items.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Item item = var6[var8];
            item.remove();
         }

         super.punishPlayer(sender, target, type, reason);
      }, 3L, TimeUnit.SECONDS);
   }
}