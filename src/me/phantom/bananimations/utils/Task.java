package me.phantom.bananimations.utils;

import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class Task {
   private static JavaPlugin plugin;
   public Task(JavaPlugin plugin) {
      Task.plugin = plugin;
      plugin.getServer().getScheduler();
   }

   public static BukkitTask runTaskLater(Runnable run, long delay, TimeUnit unit) {
      return Bukkit.getScheduler().runTaskLater(plugin, run, unit.toSeconds(delay) * 20L);
   }

   public static BukkitTask runTaskTimer(Runnable run, long start, long repeat) {
      return Bukkit.getScheduler().runTaskTimer(plugin, run, start, repeat);
   }

   public static int scheduleSyncRepeatingTask(Runnable run, long start, long repeat) {
      return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, run, start, repeat);
   }
}