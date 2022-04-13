package me.phantom.bananimations.commands;

import java.util.Iterator;
import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.BanAnimations;
import me.phantom.bananimations.Messages;
import me.phantom.bananimations.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class BanAnimationsCommand implements CommandExecutor {
   private final BanAnimations plugin;

   public BanAnimationsCommand(BanAnimations plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, String label, @Nonnull String[] args) {
      if (label.equalsIgnoreCase("ba") || label.equalsIgnoreCase("bananimations")) {
         if (!sender.hasPermission("bananimations.ban") || !sender.hasPermission("bananimations.kick") || !sender.hasPermission("bananimations.mute")) {
            Messages.NO_PERMS.send(sender);
            return false;
         }

         if (args.length >= 1) {
            String animationName;
            if (args.length >= 3) {
               AnimationType type;
               if (args[0].equalsIgnoreCase("ban")) {
                  if (!sender.hasPermission("bananimations.ban")) {
                     Messages.NO_PERMS_BAN.send(sender);
                     return false;
                  }

                  type = AnimationType.BAN;
               } else if (args[0].equalsIgnoreCase("kick")) {
                  if (!sender.hasPermission("bananimations.kick")) {
                     Messages.NO_PERMS_KICK.send(sender);
                     return false;
                  }

                  type = AnimationType.KICK;
               } else if (args[0].equalsIgnoreCase("mute")) {
                  if (!sender.hasPermission("bananimations.mute")) {
                     Messages.NO_PERMS_MUTE.send(sender);
                     return false;
                  }

                  type = AnimationType.MUTE;
               } else if (args[0].equalsIgnoreCase("ipban")) {
                  if (!sender.hasPermission("bananimations.ipban")) {
                     Messages.NO_PERMS_IP_BAN.send(sender);
                     return false;
                  }

                  type = AnimationType.IP_BAN;
               } else if (args[0].equalsIgnoreCase("tempban")) {
                  if (!sender.hasPermission("bananimations.tempban")) {
                     Messages.NO_PERMS_TEMP_BAN.send(sender);
                     return false;
                  }

                  type = AnimationType.TEMP_BAN;
               } else if (args[0].equalsIgnoreCase("tempmute")) {
                  if (!sender.hasPermission("bananimations.tempmute")) {
                     Messages.NO_PERMS_TEMP_MUTE.send(sender);
                     return false;
                  }

                  type = AnimationType.TEMP_MUTE;
               } else {
                  if (!args[0].equalsIgnoreCase("test")) {
                     sender.sendMessage(getHelp());
                     return false;
                  }

                  if (!sender.hasPermission("bananimations.test")) {
                     Messages.NO_PERMS_TEST.send(sender);
                     return false;
                  }

                  type = AnimationType.TEST;
               }

               if (Bukkit.getPlayer(args[1]) == null) {
                  Messages.ERROR_PLAYER_NOT_ONLINE.send(sender, args[1]);
                  return false;
               }

               Player player = Bukkit.getPlayer(args[1]);
               assert player != null;
               if (player.hasPermission("bananimations.bypass")) {
                  Messages.ERROR_CANT_PLAY_ANIMATION_ON_PLAYER.send(sender, player.getName());
                  return false;
               }

               if (this.plugin.isFrozen(player)) {
                  Messages.ERROR_PLAYER_ALREADY_IN_ANIMATION.send(sender, args[1]);
                  return false;
               }

               animationName = args[2].toLowerCase();
               if (this.plugin.isValidAnimation(animationName)) {
                  Messages.ERROR_INVALID_ANIMATION.send(sender, animationName);
                  return false;
               }

               StringBuilder reason = new StringBuilder();
               if (args.length > 3) {
                  for(int i = 3; i < args.length; ++i) {
                     reason.append(args[i]).append(" ");
                  }
               }

               Messages.ANIMATION_START_MESSAGE.send(sender, animationName, player.getName());
               this.activateAnimation(sender, player, animationName, type, reason.toString());
               return true;
            }

            if (args[0].equalsIgnoreCase("list")) {
               String listMessage = "&c&lAnimations:";

               for(Iterator<?> var6 = this.plugin.getAnimationNames().iterator(); var6.hasNext(); listMessage = listMessage + " &f" + animationName + "&c,") {
                  animationName = (String)var6.next();
               }

               sender.sendMessage(Utils.color(listMessage + " &frandom"));
               return true;
            }
         }

         sender.sendMessage(getHelp());
      }

      return false;
   }

   private static String getHelp() {
      return Utils.color("&c&l&m---&f&l&m-----&r[&c&lBan&f&lAnimations &c&lhelp&r]&f&l&m-----&c&l&m---\n  &c/ba &fban &f[&cplayer&f] [&canimation&f] [&cban args.&f]\n  &c/ba &ftempban &f[&cplayer&f] [&canimation&f] [&cban args.&f]\n  &c/ba &fipban &f[&cplayer&f] [&canimation&f] [&cban args.&f]\n  &c/ba &fkick &f[&cplayer&f] [&canimation&f] [&ckick args.&f]\n  &c/ba &fmute [&cplayer&f] [&canimation&f] [&cmute args.&f]\n  &c/ba &ftempmute [&cplayer&f] [&canimation&f] [&cmute args.&f]\n  &c/ba &ftest [&cplayer&f] [&canimation&f]\n  &c/ba &flist\n  &c/ba &fhelp\n&c&l&m---&f&l&m-----&r[&c&lBan&f&lAnimations &c&lhelp&r]&f&l&m-----&c&l&m---");
   }

   private void activateAnimation(CommandSender sender, Player target, String animationName, AnimationType type, String reason) {
      if (animationName.equalsIgnoreCase("random")) {
         this.plugin.getRandomAnimation().callAnimation(sender, target, type, reason);
      } else {
         this.plugin.getAnimation(animationName).callAnimation(sender, target, type, reason);
      }

   }
}