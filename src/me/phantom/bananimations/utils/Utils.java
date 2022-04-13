package me.phantom.bananimations.utils;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class Utils {
   public static String color(String txt) {
      return ChatColor.translateAlternateColorCodes('&', txt);
   }

   public static void setLore(ItemStack item, String s) {
      ItemMeta meta = item.getItemMeta();
      List<String> lore = new ArrayList<String>();
      lore.add("ba-" + s);
      assert meta != null;
      meta.setLore(lore);
      item.setItemMeta(meta);
   }

   public static Location getLocationAroundCircle(Location center, double radius, double angleInRadian) {
      double x = center.getX() + radius * Math.cos(angleInRadian);
      double z = center.getZ() + radius * Math.sin(angleInRadian);
      double y = center.getY();
      Location loc = new Location(center.getWorld(), x, y, z);
      Vector difference = center.toVector().clone().subtract(loc.toVector());
      loc.setDirection(difference);
      return loc;
   }

   public static ArrayList<Location> getCircle(Location center, double radius, int amount) {
      World world = center.getWorld();
      double increment = 6.283185307179586D / (double)amount;
      ArrayList<Location> locations = new ArrayList<Location>();

      for(int i = 0; i < amount; ++i) {
         double angle = (double)i * increment;
         double x = center.getX() + radius * Math.cos(angle);
         double z = center.getZ() + radius * Math.sin(angle);
         locations.add(new Location(world, x, center.getY(), z));
      }

      return locations;
   }
}