package me.phantom.bananimations.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.phantom.bananimations.BanAnimations;

/**
 * Listener that enforces the freeze state of players undergoing animation.
 */
public class FrozenListener implements Listener {
    private final BanAnimations plugin;

    public FrozenListener(BanAnimations plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (this.plugin.isFrozen(event.getPlayer())) {
            Location to = event.getTo();
            Location from = event.getFrom();
            if (to == null) return;
            
            // Allow looking around (rotation), but prevent movement (x, y, z change)
            if (to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ()) {
                Location back = from.clone();
                back.setDirection(to.getDirection()); // Keep the player's new look direction
                event.getPlayer().teleport(back);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && this.plugin.isFrozen((Player) e.getEntity())) {
            e.setCancelled(true);
        }
    }
}