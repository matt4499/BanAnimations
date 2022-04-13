package me.phantom.bananimations.utils.mobutils;

import org.bukkit.entity.Entity;

public interface MobUtil {
   void setDefaultAttributes(Entity var1);

   void setInvulnerable(Entity var1);

   void setTags(Entity var1, String... var2);
}