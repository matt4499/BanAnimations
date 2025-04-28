package me.phantom.bananimations;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
   private final BanAnimations plugin;

   public Config(BanAnimations plugin) {
      this.plugin = plugin;
   }

   public void loadDefaultConfig() {
      FileConfiguration configuration = this.plugin.getConfig();
      configuration.addDefault("play_animation_on.BAN.enabled", true);
      configuration.addDefault("play_animation_on.BAN.animation", "YINYANG");
      configuration.addDefault("play_animation_on.IP_BAN.enabled", true);
      configuration.addDefault("play_animation_on.IP_BAN.animation", "EXPLODE");
      configuration.addDefault("play_animation_on.TEMP_BAN.enabled", true);
      configuration.addDefault("play_animation_on.TEMP_BAN.animation", "SWORDFALL");
      configuration.addDefault("play_animation_on.KICK.enabled", true);
      configuration.addDefault("play_animation_on.KICK.animation", "SNAPTRAP");
      configuration.addDefault("play_animation_on.MUTE.enabled", true);
      configuration.addDefault("play_animation_on.MUTE.animation", "RANDOM");
      configuration.addDefault("play_animation_on.TEMP_MUTE.enabled", true);
      configuration.addDefault("play_animation_on.TEMP_MUTE.animation", "METEOR");
      configuration.addDefault("Messages.animation_start", "&cStarted animation {0} on {1}!");
      configuration.addDefault("Messages.no_permission", "&cYou do not have permission to use this command!");
      configuration.addDefault("Messages.no_permission_ban", "&cYou do not have permission to use this command!");
      configuration.addDefault("Messages.no_permission_ipban", "&cYou do not have permission to ip-ban users");
      configuration.addDefault("Messages.no_permission_tempban", "&cYou do not have permission to temp-ban users!");
      configuration.addDefault("Messages.no_permission_kick", "&cYou do not have permission to kick users!");
      configuration.addDefault("Messages.no_permission_mute", "&cYou do not have permission to mute users!");
      configuration.addDefault("Messages.no_permission_tempmute", "&cYou do not have permission to temp-mute users!");
      configuration.addDefault("Messages.no_permission_test", "&You do not have permission to test animations!");
      configuration.addDefault("Messages.player_not_online", "&c&l{0} &cis not online!");
      configuration.addDefault("Messages.player_animation_bypass_enabled", "&cYou can not play an animation on {0}!");
      configuration.addDefault("Messages.player_already_in_animation", "&c&l{0} is already in an animation!");
      configuration.addDefault("Messages.invalid_animation_name", "&cThere is no animation called &c&l{0}&c!");
      configuration.addDefault("RandomAnimationPool", new ArrayList<String>());
      configuration.options().copyDefaults(true);
      this.plugin.saveConfig();
      this.plugin.reloadConfig();
   }
}
