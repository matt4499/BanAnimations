package me.phantom.bananimations.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

/**
 * Utility class containing general helper methods.
 */
public class Utils {

    /**
     * Translates alternate color codes in a string.
     * @param txt The string to colorize.
     * @return The colorized string.
     */
   public static String color(String txt) {
      return ChatColor.translateAlternateColorCodes('&', txt);
   }

    /**
     * Sets a lore line on an item stack with a "ba-" prefix.
     * @param item The item stack to modify.
     * @param s The lore string suffix.
     */
   public static void setLore(ItemStack item, String s) {
      ItemMeta meta = item.getItemMeta();
      List<String> lore = new ArrayList<>();
      lore.add("ba-" + s);
      if (meta != null) {
          meta.setLore(lore);
          item.setItemMeta(meta);
      }
   }

    /**
     * Calculates a location around a circle.
     * @param center The center location.
     * @param radius The radius from the center.
     * @param angleInRadian The angle in radians.
     * @return The calculated location facing the center.
     */
   public static Location getLocationAroundCircle(Location center, double radius, double angleInRadian) {
      double x = center.getX() + radius * Math.cos(angleInRadian);
      double z = center.getZ() + radius * Math.sin(angleInRadian);
      double y = center.getY();
      Location loc = new Location(center.getWorld(), x, y, z);
      Vector difference = center.toVector().clone().subtract(loc.toVector());
      loc.setDirection(difference);
      return loc;
   }

    /**
     * Generates a list of locations forming a circle.
     * @param center The center location.
     * @param radius The radius of the circle.
     * @param amount The number of points to generate.
     * @return A list of locations.
     */
   public static ArrayList<Location> getCircle(Location center, double radius, int amount) {
      World world = center.getWorld();
      double increment = (2 * Math.PI) / (double)amount;
      ArrayList<Location> locations = new ArrayList<>();

      for(int i = 0; i < amount; ++i) {
         double angle = (double)i * increment;
         double x = center.getX() + radius * Math.cos(angle);
         double z = center.getZ() + radius * Math.sin(angle);
         locations.add(new Location(world, x, center.getY(), z));
      }

      return locations;
   }
}