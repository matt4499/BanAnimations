package me.phantom.bananimations.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.BanAnimations;
import me.phantom.bananimations.Messages;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.Utils;

/**
 * Command Executor for the main plugin command.
 */
public class BanAnimationsCommand implements CommandExecutor {
    private final BanAnimations plugin;

    public BanAnimationsCommand(BanAnimations plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean isTestCommand = args.length > 0 && args[0].equalsIgnoreCase("test");

        if (args.length >= 1 && (args[0].equalsIgnoreCase("punish") || args[0].equalsIgnoreCase("p"))) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Utils.color("&cOnly players can use the punish GUI."));
                return true;
            }

            if (!sender.hasPermission(this.plugin.getPermissionNode("punish"))) {
                Messages.NO_PERMS.send(sender);
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(Utils.color("&cUsage: /ba punish [name]"));
                return true;
            }

            this.plugin.getPunishGuiListener().openPunishMenu(player, args[1]);
            return true;
        }

        if (!isTestCommand && !sender.hasPermission(this.plugin.getPermissionNode("use"))) {
            Messages.NO_PERMS.send(sender);
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(getHelp());
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (!sender.hasPermission(this.plugin.getPermissionNode("list"))) {
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
                type = AnimationType.BAN;
                break;
            case "kick":
                type = AnimationType.KICK;
                break;
            case "mute":
                type = AnimationType.MUTE;
                break;
            case "ipban":
                type = AnimationType.IP_BAN;
                break;
            case "tempban":
                type = AnimationType.TEMP_BAN;
                break;
            case "tempmute":
                type = AnimationType.TEMP_MUTE;
                break;
            case "test":
                type = AnimationType.TEST;
                break;
            default:
                sender.sendMessage(getHelp());
                return true;
        }

        requiredPermission = this.plugin.getPermissionForType(type);

        if (type != AnimationType.TEST && !sender.hasPermission(requiredPermission)) {
            sendPermissionError(sender, type);
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Messages.ERROR_PLAYER_NOT_ONLINE.send(sender, args[1]);
            return true;
        }

        if (!type.equals(AnimationType.TEST) && target.hasPermission(this.plugin.getPermissionNode("bypass"))) {
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

        String duration = "permanent";
        String reason = "";
        if (type == AnimationType.TEMP_BAN || type == AnimationType.TEMP_MUTE) {
            if (args.length > 3) {
                duration = args[3];
            }
            if (args.length > 4) {
                reason = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
            }
        } else if (args.length > 3) {
            reason = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        }

        this.plugin.setPendingPunishment(target, type, duration, reason, animationName);

        Messages.ANIMATION_START_MESSAGE.send(sender, animationName, target.getName());
        this.activateAnimation(sender, target, animationName, type, reason);

        return true;
    }

    private void displayAnimationList(CommandSender sender) {
        StringBuilder listMessage = new StringBuilder("&c&lAnimations: ");
        listMessage.append(String.join(", ", this.plugin.getAnimationNames()));
        listMessage.append(", random");
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
        return Utils.color(
                "&c&l&m---&f&l&m-----&r[&c&lBan&f&lAnimations &c&lHelp&r]&f&l&m-----&c&l&m---\n" +
                "  &c/ba &fban &f[&cplayer&f] [&canimation&f] [&creason...&f]\n" +
                "  &c/ba &ftempban &f[&cplayer&f] [&canimation&f] [&cduration&f] [&creason...&f]\n" +
                "  &c/ba &fipban &f[&cplayer&f] [&canimation&f] [&creason...&f]\n" +
                "  &c/ba &fkick &f[&cplayer&f] [&canimation&f] [&creason...&f]\n" +
                "  &c/ba &fmute &f[&cplayer&f] [&canimation&f] [&creason...&f]\n" +
                "  &c/ba &ftempmute &f[&cplayer&f] [&canimation&f] [&cduration&f] [&creason...&f]\n" +
                "  &c/ba &ftest &f[&cplayer&f] [&canimation&f]\n" +
                "  &c/ba &fpunish &f[&cplayer&f]\n" +
                "  &c/ba &flist\n" +
                "  &c/ba &fhelp\n" +
                "&c&l&m---&f&l&m-----&r[&c&lBan&f&lAnimations &c&lHelp&r]&f&l&m-----&c&l&m---"
        );
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