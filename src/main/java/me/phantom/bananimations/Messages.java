package me.phantom.bananimations;

import me.phantom.bananimations.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public enum Messages {
   ANIMATION_START_MESSAGE("animation_start"),
   CONFIG_RELOAD("reload_config"),
   NO_PERMS("no_permission"),
   NO_PERMS_BAN("no_permission_ban"),
   NO_PERMS_KICK("no_permission_kick"),
   NO_PERMS_MUTE("no_permission_mute"),
   NO_PERMS_TEMP_BAN("no_permission_tempban"),
   NO_PERMS_IP_BAN("no_permission_ipban"),
   NO_PERMS_TEMP_MUTE("no_permission_tempmute"),
   NO_PERMS_TEST("no_permission_mute"),
   ERROR_PLAYER_NOT_ONLINE("player_not_online"),
   ERROR_PLAYER_ALREADY_IN_ANIMATION("player_already_in_animation"),
   ERROR_INVALID_ANIMATION("invalid_animation_name"),
   ERROR_CANT_PLAY_ANIMATION_ON_PLAYER("player_animation_bypass_enabled");

   private String location;
   private static FileConfiguration file;

   private Messages(String location) {
      this.location = location;
   }

   public static void setFile(FileConfiguration fileConfiguration) {
      file = fileConfiguration;
   }

   public String toString() {
      return Utils.color(file.getString("Messages." + this.location));
   }

   public void send(CommandSender sender) {
      String message = Utils.color(file.getString("Messages." + this.location));
      sender.sendMessage(message);
   }

   public void send(CommandSender sender, String... replacements) {
      String message = Utils.color(file.getString("Messages." + this.location));

      for(int i = 0; i < replacements.length; ++i) {
         message = message.replace("{" + i + "}", replacements[i]);
      }

      sender.sendMessage(message);
   }
}