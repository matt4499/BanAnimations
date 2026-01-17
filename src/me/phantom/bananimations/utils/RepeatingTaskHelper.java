package me.phantom.bananimations.utils;

import org.bukkit.Bukkit;

/**
 * Helper class to manage state within repeating tasks, specifically counting and cancellation.
 */
public class RepeatingTaskHelper {
    private int taskID;
    private int counter;

    /**
     * Increments the internal counter.
     */
    public void increment() {
        ++this.counter;
    }

    public int getCounter() {
        return this.counter;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    /**
     * Cancels the associated task.
     */
    public void cancel() {
        Bukkit.getScheduler().cancelTask(this.taskID);
    }
}