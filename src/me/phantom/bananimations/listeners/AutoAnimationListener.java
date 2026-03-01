package me.phantom.bananimations.listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.BanAnimations;
import me.phantom.bananimations.Messages;

/**
 * Listener that intercepts standard punishment commands to play an animation instead.
 */
public class AutoAnimationListener implements Listener {
    private final BanAnimations plugin;
    private final HashMap<AnimationType, String> commandsWithAnimations = new HashMap<>();

    public AutoAnimationListener(BanAnimations plugin) {
        this.plugin = plugin;
        this.loadConfig();
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage();
        String[] args = msg.split(" ");
        if (args.length < 1) return;
        
        String command = args[0].toLowerCase();
        // Remove slash
        if (command.startsWith("/")) command = command.substring(1);
        
        Player sender = event.getPlayer();
        AnimationType type = null;

        switch (command) {
            case "ban": type = AnimationType.BAN; break;
            case "ipban": type = AnimationType.IP_BAN; break;
            case "tempban": type = AnimationType.TEMP_BAN; break;
            case "kick": type = AnimationType.KICK; break;
            case "mute": type = AnimationType.MUTE; break;
            case "tempmute": type = AnimationType.TEMP_MUTE; break;
        }

        if (type == null) return;
        if (!sender.hasPermission(this.plugin.getPermissionForType(type))) return;

        if (this.commandsWithAnimations.containsKey(type)) {
            String animationName = this.commandsWithAnimations.get(type);
            
            // Fix: Check if animation is valid OR random. If neither, it's invalid.
            boolean isRandom = animationName.equalsIgnoreCase("random");
            if (!this.plugin.isValidAnimation(animationName) && !isRandom) {
                Bukkit.getLogger().severe("[Ban Animations] Invalid animation in config: " + animationName + "!");
                Bukkit.getLogger().severe("[Ban Animations] Player is being punished without an animation!");
                return;
            }

            if (args.length >= 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    return;
                }

                if (!target.hasPermission(this.plugin.getPermissionNode("bypass"))) {
                    String reason = "";
                    if (args.length > 2) {
                        reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                    }

                    event.setCancelled(true);
                    Messages.ANIMATION_START_MESSAGE.send(event.getPlayer(), animationName, target.getName());
                    this.activateAnimation(event.getPlayer(), target, animationName, type, reason);
                }
            }
        }
    }

    private void activateAnimation(CommandSender sender, Player target, String animationName, AnimationType type, String reason) {
        if (animationName.equalsIgnoreCase("random")) {
            me.phantom.bananimations.api.Animation animation = this.plugin.getRandomAnimation();
            if (animation != null) {
                animation.callAnimation(sender, target, type, reason);
            }
        } else {
             me.phantom.bananimations.api.Animation animation = this.plugin.getAnimation(animationName);
             if (animation != null) {
                 animation.callAnimation(sender, target, type, reason);
             }
        }
    }

    private void loadConfig() {
        ConfigurationSection configSection = this.plugin.getConfig().getConfigurationSection("play_animation_on");
        if (configSection != null) {
            for (String key : configSection.getKeys(false)) {
                try {
                    if (configSection.getBoolean(key + ".enabled")) {
                        String animationName = Objects.requireNonNull(configSection.getString(key + ".animation")).toLowerCase();
                        this.commandsWithAnimations.put(AnimationType.valueOf(key), animationName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}