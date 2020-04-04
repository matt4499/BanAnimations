package me.phantom.bananimations.utils.mobutils;

import org.bukkit.entity.Entity;

public interface MobUtil {
   Entity setDefaultAttributes(Entity var1);

   Entity setInvulnerable(Entity var1);

   Entity setTags(Entity var1, String... var2);
}