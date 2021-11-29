package me.phantom.bananimations.listeners;

import java.util.HashMap;
import java.util.Iterator;
import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.BanAnimations;
import me.phantom.bananimations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class AutoAnimationListener implements Listener {
   private BanAnimations plugin;
   private HashMap<AnimationType, String> commandsWithAnimations = new HashMap<AnimationType, String>();

   public AutoAnimationListener(BanAnimations plugin) {
      this.plugin = plugin;
      this.loadConfig();
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onCommand(PlayerCommandPreprocessEvent event) {
      String cmd = event.getMessage().toLowerCase();
      Player sender = event.getPlayer();
      AnimationType type;
      if (cmd.startsWith("/ban ")) {
         if (!sender.hasPermission("bananimations.ban")) {
            return;
         }

         type = AnimationType.BAN;
      } else if (cmd.startsWith("/ipban ")) {
         if (!sender.hasPermission("bananimations.ipban")) {
            return;
         }

         type = AnimationType.IP_BAN;
      } else if (cmd.startsWith("/tempban ")) {
         if (!sender.hasPermission("bananimations.tempban")) {
            return;
         }

         type = AnimationType.TEMP_BAN;
      } else if (cmd.startsWith("/kick ")) {
         if (!sender.hasPermission("bananimations.kick")) {
            return;
         }

         type = AnimationType.KICK;
      } else if (cmd.startsWith("/mute ")) {
         if (!sender.hasPermission("bananimations.mute")) {
            return;
         }

         type = AnimationType.MUTE;
      } else {
         if (!cmd.startsWith("/tempmute ")) {
            return;
         }

         if (!sender.hasPermission("bananimations.tempmute")) {
            return;
         }

         type = AnimationType.TEMP_MUTE;
      }

      if (this.commandsWithAnimations.containsKey(type)) {
         String animationName = (String)this.commandsWithAnimations.get(type);
         if (!this.plugin.isValidAnimation(animationName)) {
            Bukkit.getLogger().severe("[Ban Animations] Invalid animation is config! Animation " + animationName + "!");
            Bukkit.getLogger().severe("[Ban Animations] Player is being punished without an animation!");
         } else {
            String[] args = cmd.split(" ");
            if (args.length >= 2) {
               if (Bukkit.getPlayer(args[1]) != null) {
                  Player target = Bukkit.getPlayer(args[1]);
                  if (!target.hasPermission("bananimations.bypass")) {
                     String reason = "";

                     for(int i = 2; i < args.length; ++i) {
                        reason = reason + args[i] + " ";
                     }

                     event.setCancelled(true);
                     Messages.ANIMATION_START_MESSAGE.send(event.getPlayer(), new String[]{animationName, target.getName()});
                     this.activateAnimation(event.getPlayer(), target, animationName, type, reason);
                  }
               }
            }
         }
      }
   }

   private void activateAnimation(CommandSender sender, Player target, String animationName, AnimationType type, String reason) {
      if (animationName.equalsIgnoreCase("random")) {
         this.plugin.getRandomAnimation().callAnimation(sender, target, type, reason);
      } else {
         this.plugin.getAnimation(animationName).callAnimation(sender, target, type, reason);
      }

   }

   private void loadConfig() {
      ConfigurationSection configSection = this.plugin.getConfig().getConfigurationSection("play_animation_on");
      Iterator<?> var2 = configSection.getKeys(false).iterator();

      while(var2.hasNext()) {
         String key = (String)var2.next();

         try {
            if (configSection.getBoolean(key + ".enabled")) {
               String animationName = configSection.getString(key + ".animation").toLowerCase();
               this.commandsWithAnimations.put(AnimationType.valueOf(key), animationName);
            }
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }

   }
}