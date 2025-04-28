package me.phantom.bananimations.commands;

import java.util.Iterator;
import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.BanAnimations;
import me.phantom.bananimations.Messages;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanAnimationsCommand implements CommandExecutor {
   private final BanAnimations plugin;

   public BanAnimationsCommand(BanAnimations plugin) {
      this.plugin = plugin;
   }

   @Override
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!label.equalsIgnoreCase("ba") && !label.equalsIgnoreCase("bananimations")) {
         return false;
      }

      if (!sender.hasPermission("bananimations.use")) {
         Messages.NO_PERMS.send(sender);
         return true;
      }

      if (args.length < 1) {
         sender.sendMessage(getHelp());
         return true;
      }

      if (args[0].equalsIgnoreCase("list")) {
         if (!sender.hasPermission("bananimations.list")) {
            Messages.NO_PERMS.send(sender);
            return true;
         }
         displayAnimationList(sender);
         return true;
      }

      if (args[0].equalsIgnoreCase("help")) {
         sender.sendMessage(getHelp());
         return true;
      }


      if (args.length < 3) {
         sender.sendMessage(getHelp());
         return true;
      }

      AnimationType type;
      String requiredPermission;

      switch (args[0].toLowerCase()) {
         case "ban":
            requiredPermission = "bananimations.ban";
            type = AnimationType.BAN;
            break;
         case "kick":
            requiredPermission = "bananimations.kick";
            type = AnimationType.KICK;
            break;
         case "mute":
            requiredPermission = "bananimations.mute";
            type = AnimationType.MUTE;
            break;
         case "ipban":
            requiredPermission = "bananimations.ipban";
            type = AnimationType.IP_BAN;
            break;
         case "tempban":
            requiredPermission = "bananimations.tempban";
            type = AnimationType.TEMP_BAN;
            break;
         case "tempmute":
            requiredPermission = "bananimations.tempmute";
            type = AnimationType.TEMP_MUTE;
            break;
         case "test":
            requiredPermission = "bananimations.test";
            type = AnimationType.TEST;
            break;
         default:
            sender.sendMessage(getHelp());
            return true;
      }

      if (!sender.hasPermission(requiredPermission)) {
         sendPermissionError(sender, type);
         return true;
      }

      Player target = Bukkit.getPlayer(args[1]);
      if (target == null) {
         Messages.ERROR_PLAYER_NOT_ONLINE.send(sender, args[1]);
         return true;
      }

      if (target.hasPermission("bananimations.bypass")) {
         Messages.ERROR_CANT_PLAY_ANIMATION_ON_PLAYER.send(sender, target.getName());
         return true;
      }

      if (this.plugin.isFrozen(target)) {
         Messages.ERROR_PLAYER_ALREADY_IN_ANIMATION.send(sender, target.getName());
         return true;
      }

      String animationName = args[2].toLowerCase();

      if (!animationName.equalsIgnoreCase("random") && !this.plugin.isValidAnimation(animationName)) {
         Messages.ERROR_INVALID_ANIMATION.send(sender, args[2]);
         return true;
      }

      StringBuilder reason = new StringBuilder();
      if (args.length > 3) {
         for (int i = 3; i < args.length; ++i) {
            reason.append(args[i]).append(" ");
         }
      }
      String reasonString = reason.length() > 0 ? reason.substring(0, reason.length() - 1) : "";


      Messages.ANIMATION_START_MESSAGE.send(sender, animationName, target.getName());
      this.activateAnimation(sender, target, animationName, type, reasonString);

      return true;
   }

   private void displayAnimationList(CommandSender sender) {
      StringBuilder listMessage = new StringBuilder("&c&lAnimations: ");
      boolean first = true;
      for (String animName : this.plugin.getAnimationNames()) {
         if (!first) {
            listMessage.append("&c,");
         }
         listMessage.append(" &f").append(animName);
         first = false;
      }
      if (!first) {
         listMessage.append("&c,");
      }
      listMessage.append(" &frandom");

      sender.sendMessage(Utils.color(listMessage.toString()));
   }

   private void sendPermissionError(CommandSender sender, AnimationType type) {
      switch (type) {
         case BAN:       Messages.NO_PERMS_BAN.send(sender); break;
         case KICK:      Messages.NO_PERMS_KICK.send(sender); break;
         case MUTE:      Messages.NO_PERMS_MUTE.send(sender); break;
         case IP_BAN:    Messages.NO_PERMS_IP_BAN.send(sender); break;
         case TEMP_BAN:  Messages.NO_PERMS_TEMP_BAN.send(sender); break;
         case TEMP_MUTE: Messages.NO_PERMS_TEMP_MUTE.send(sender); break;
         case TEST:      Messages.NO_PERMS_TEST.send(sender); break;
         default:        Messages.NO_PERMS.send(sender);
      }
   }

   private static String getHelp() {
      StringBuilder help = new StringBuilder();
      help.append("&c&l&m---&f&l&m-----&r[&c&lBan&f&lAnimations &c&lHelp&r]&f&l&m-----&c&l&m---\n");
      help.append("  &c/ba &fban &f[&cplayer&f] [&canimation&f] [&creason...&f]\n");
      help.append("  &c/ba &ftempban &f[&cplayer&f] [&canimation&f] [&cduration&f] [&creason...&f]\n");
      help.append("  &c/ba &fipban &f[&cplayer&f] [&canimation&f] [&creason...&f]\n");
      help.append("  &c/ba &fkick &f[&cplayer&f] [&canimation&f] [&creason...&f]\n");
      help.append("  &c/ba &fmute &f[&cplayer&f] [&canimation&f] [&creason...&f]\n");
      help.append("  &c/ba &ftempmute &f[&cplayer&f] [&canimation&f] [&cduration&f] [&creason...&f]\n");
      help.append("  &c/ba &ftest &f[&cplayer&f] [&canimation&f]\n");
      help.append("  &c/ba &flist\n");
      help.append("  &c/ba &fhelp\n");
      help.append("&c&l&m---&f&l&m-----&r[&c&lBan&f&lAnimations &c&lHelp&r]&f&l&m-----&c&l&m---");
      return Utils.color(help.toString());
   }

   private void activateAnimation(CommandSender sender, Player target, String animationName, AnimationType type, String reason) {
      Animation animToPlay;
      if (animationName.equalsIgnoreCase("random")) {
         animToPlay = this.plugin.getRandomAnimation();
         if (animToPlay == null) {
            Messages.ERROR_INVALID_ANIMATION.send(sender, "random (No animations available!)");
            plugin.logger.severe("Could not select a random animation - none are registered or available!");
            return;
         }
      } else {
         animToPlay = this.plugin.getAnimation(animationName);
         if (animToPlay == null) {
            Messages.ERROR_INVALID_ANIMATION.send(sender, animationName + " (Not found after validation!)");
            plugin.logger.severe("Animation '" + animationName + "' passed validation but could not be retrieved!");
            return;
         }
      }

      animToPlay.callAnimation(sender, target, type, reason);
   }
}