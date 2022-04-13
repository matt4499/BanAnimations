package me.phantom.bananimations.utils.mobutils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class MobUtilsNewer implements MobUtil {
   public void setDefaultAttributes(Entity entity) {
      entity.setInvulnerable(true);
      entity.setSilent(true);
   }

   public void setInvulnerable(Entity entity) {
      entity.setInvulnerable(true);
   }

   public void setTags(Entity entity, String... tags) {
      for (String tag : tags) {
         if (tag.equalsIgnoreCase("Silent")) {
            entity.setSilent(true);
         } else if (tag.equalsIgnoreCase("Invulnerable")) {
            entity.setInvulnerable(true);
         } else if (tag.equalsIgnoreCase("NoAI") && entity instanceof LivingEntity) {
            ((LivingEntity) entity).setAI(false);
         }
      }

   }
}