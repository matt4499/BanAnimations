package me.phantom.bananimations.utils.mobutils;

import me.phantom.bananimations.BanAnimations;
import org.bukkit.entity.Entity;

public class MobUtils {
   private MobUtil mobUtil;

   public MobUtils(BanAnimations plugin) {
      this.mobUtil = new MobUtilsNewer();
   }

   public Entity setDefaultTags(Entity entity) {
      this.mobUtil.setDefaultAttributes(entity);
      return entity;
   }

   public Entity setInvulnerable(Entity entity) {
      this.mobUtil.setInvulnerable(entity);
      return entity;
   }

   public Entity setTags(Entity entity, String... tags) {
      this.mobUtil.setTags(entity, tags);
      return entity;
   }
}