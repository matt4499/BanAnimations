package me.phantom.bananimations.utils.mobutils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class MobUtilsNewer implements MobUtil {
   public Entity setDefaultAttributes(Entity entity) {
      entity.setInvulnerable(true);
      entity.setSilent(true);
      return entity;
   }

   public Entity setInvulnerable(Entity entity) {
      entity.setInvulnerable(true);
      return entity;
   }

   public Entity setTags(Entity entity, String... tags) {
      String[] var3 = tags;
      int var4 = tags.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String tag = var3[var5];
         if (tag.equalsIgnoreCase("Silent")) {
            entity.setSilent(true);
         } else if (tag.equalsIgnoreCase("Invulnerable")) {
            entity.setInvulnerable(true);
         } else if (tag.equalsIgnoreCase("NoAI") && entity instanceof LivingEntity) {
            ((LivingEntity)entity).setAI(false);
         }
      }

      return entity;
   }
}