package me.phantom.bananimations.animations;

import java.util.concurrent.TimeUnit;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.Sounds;
import me.phantom.bananimations.utils.Task;

/**
 * Animation causing TNT to fall and explode on the player.
 */
public class ExplodeAnimation extends Animation {
    public ExplodeAnimation() {
        super("explode");
    }

    @Override
    public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        super.freeze(target);
        Location tntSpawnLocation = target.getLocation().add(0.0D, 10.0D, 0.0D);
        TNTPrimed tnt = target.getWorld().spawn(tntSpawnLocation, TNTPrimed.class);
        target.getWorld().playSound(target.getEyeLocation(), Sounds.ENTITY_TNT_PRIMED.get(), 1.0F, 1.0F);
        tnt.setFuseTicks(200);
        
        Task.runTaskLater(() -> {
            tnt.remove();
            try {
                target.getWorld().playEffect(target.getLocation(), Effect.valueOf("SMOKE"), 1);
            } catch (IllegalArgumentException ignored) {
                // Fallback or ignore for newer/older versions
            }
            target.getWorld().playSound(target.getEyeLocation(), Sounds.ENTITY_GENERIC_EXPLODE.get(), 1.0F, 1.0F);
            super.finish(sender, target, type, reason);
        }, 5L, TimeUnit.SECONDS);
    }
}