package me.phantom.bananimations.animations;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.Player;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.RepeatingTaskHelper;
import me.phantom.bananimations.utils.Task;
import me.phantom.bananimations.utils.Utils;

/**
 * SnapTrap Animation.
 * Summons evoker fangs in a circle closing in on the player.
 */
public class SnapTrapAnimation extends Animation {
    
    public SnapTrapAnimation() {
        super("snaptrap");
    }

    @Override
    public void playAnimation(CommandSender sender, Player target, AnimationType type, String reason) {
        super.freeze(target);
        World world = target.getWorld();
        Location targetLocation = target.getLocation();
        RepeatingTaskHelper taskHelper = new RepeatingTaskHelper();
        
        taskHelper.setTaskID(Task.scheduleSyncRepeatingTask(() -> {
            if (taskHelper.getCounter() == 3) {
                EvokerFangs fangs = (EvokerFangs) world.spawnEntity(targetLocation, EntityType.EVOKER_FANGS);
                fangs.setCustomNameVisible(false);
                fangs.setCustomName("ba-fangs");
                super.finish(sender, target, type, reason);
                taskHelper.cancel();
            }

            Utils.getCircle(targetLocation, 3 - taskHelper.getCounter(), (3 - taskHelper.getCounter()) * 5)
                    .forEach((location) -> {
                        EvokerFangs fangs = (EvokerFangs) world.spawnEntity(location, EntityType.EVOKER_FANGS);
                        fangs.setCustomNameVisible(false);
                        fangs.setCustomName("ba-fangs");
                    });
            taskHelper.increment();
        }, 0L, 20L));
    }
}