package me.phantom.bananimations.utils;

import org.bukkit.Bukkit;

public class RepeatingTaskHelper {
   private int taskID;
   private int counter;

   public void increment() {
      ++this.counter;
   }

   public int getCounter() {
      return this.counter;
   }

   public void setTaskID(int taskID) {
      this.taskID = taskID;
   }

   public void cancel() {
      Bukkit.getScheduler().cancelTask(this.taskID);
   }
}