package me.phantom.bananimations.api;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.BanAnimations;
import me.phantom.bananimations.events.AnimationStartEvent;
import me.phantom.bananimations.utils.Sounds;

/**
 * Abstract class representing a punishment animation.
 */
public abstract class Animation {
    private final String name;
    private final Random random;
    private final BanAnimations plugin;

    /**
     * @param name The name of the animation.
     */
    public Animation(String name) {
        this.name = name;
        this.plugin = (BanAnimations) Bukkit.getPluginManager().getPlugin("BanAnimations");
        this.random = new Random();
    }

    /**
     * Triggers the animation sequence.
     *
     * @param sender The command sender initiating the ban.
     * @param target The player being banned/punished.
     * @param type   The type of punishment.
     * @param reason The reason for the punishment.
     */
    public void callAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        Bukkit.getServer().getPluginManager().callEvent(new AnimationStartEvent(sender, target, type, reason));
        this.playAnimation(sender, target, type, reason);
    }

    /**
     * Implementation of the specific animation logic.
     *
     * @param sender The command sender.
     * @param target The target player.
     * @param type   The punishment type.
     * @param reason The reason string.
     */
    public abstract void playAnimation(CommandSender sender, Player target, AnimationType type, String reason);

    /**
     * Finalizes the animation, executes the punishment command, and unfreezes the player.
     *
     * @param sender The command sender.
     * @param target The target player.
     * @param type   The punishment type.
     * @param reason The reason string.
     * @return Always returns true.
     */
    public boolean finish(CommandSender sender, Player target, AnimationType type, String reason) {
        if (type != AnimationType.TEST) {
            try {
                Bukkit.dispatchCommand(sender, type + " " + target.getName() + " " + reason);
                target.playSound(target.getEyeLocation(), Sounds.ENTITY_WITHER_AMBIENT.get(), 0.1F, 2.0F);
                this.playSound(target, sender, Sounds.ENTITY_WITHER_AMBIENT.get(), 0.1F, 2.0F);
            } catch (CommandException e) {
                e.printStackTrace();
            }
        }

        this.unFreeze(target);
        return true;
    }

    /**
     * Plays a sound for the target and the sender (if the sender is a player).
     *
     * @param target The target player.
     * @param sender The command sender.
     * @param sound  The sound to play.
     * @param volume The volume.
     * @param pitch  The pitch.
     */
    public void playSound(Player target, CommandSender sender, Sound sound, float volume, float pitch) {
        target.playSound(target.getEyeLocation(), sound, volume, pitch);
        if (sender instanceof Player player) {
            player.playSound(player.getEyeLocation(), sound, volume, pitch);
        }
    }

    /**
     * Freezes the player in place, optionally creating a barrier block below them if they are in air.
     *
     * @param target The player to freeze.
     */
    public void freeze(Player target) {
        target.setVelocity(new Vector(0, 0, 0));
        Block blockBelow = target.getLocation().add(0.0D, -1.0D, 0.0D).getBlock();
        if (blockBelow.getType() == Material.AIR) {
            blockBelow.setType(Material.BARRIER);
            blockBelow.setMetadata("bananimations_barrier", new FixedMetadataValue(this.plugin, ""));
            // Teleport to current location to sync
            target.teleport(target.getLocation());
        }

        this.plugin.freeze(target);
    }

    /**
     * Unfreezes the player and removes any temporary barrier blocks.
     *
     * @param target The player to unfreeze.
     */
    private void unFreeze(Player target) {
        Block blockBelow = target.getLocation().add(0.0D, -1.0D, 0.0D).getBlock();
        if (blockBelow.hasMetadata("bananimations_barrier")) {
            blockBelow.removeMetadata("bananimations_barrier", this.plugin);
            blockBelow.setType(Material.AIR);
        }
        this.plugin.unFreeze(target);
    }

    public BanAnimations getPlugin() {
        return this.plugin;
    }

    public Random getRandom() {
        return this.random;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Registers this animation with the plugin.
     */
    public void hook() {
        this.plugin.registerAnimation(this, this.name);
    }
}