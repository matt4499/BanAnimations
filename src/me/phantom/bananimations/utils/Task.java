package me.phantom.bananimations.utils;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Utility class for scheduling tasks with the Bukkit scheduler.
 */
public final class Task {
    private static JavaPlugin plugin;

    public Task(JavaPlugin plugin) {
        Task.plugin = plugin;
    }

    /**
     * Runs a task after a specified delay.
     * @param run The runnable to execute.
     * @param delay The delay amount.
     * @param unit The time unit of the delay.
     */
    public static void runTaskLater(Runnable run, long delay, TimeUnit unit) {
        Bukkit.getScheduler().runTaskLater(plugin, run, unit.toSeconds(delay) * 20L);
    }

    /**
     * Runs a repeating task.
     * @param run The runnable to execute.
     * @param start The initial delay in ticks.
     * @param repeat The repeat interval in ticks.
     * @return The resulting BukkitTask.
     */
    public static BukkitTask runTaskTimer(Runnable run, long start, long repeat) {
        return Bukkit.getScheduler().runTaskTimer(plugin, run, start, repeat);
    }

    /**
     * Schedules a synchronous repeating task.
     * @param run The runnable to execute.
     * @param start The initial delay in ticks.
     * @param repeat The repeat interval in ticks.
     * @return The task ID.
     */
    public static int scheduleSyncRepeatingTask(Runnable run, long start, long repeat) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, run, start, repeat);
    }
}