package me.phantom.bananimations.listeners;

import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

/**
 * Listeners for general entities involved in animations.
 */
public class AnimationListeners implements Listener {

    @EventHandler
    public void onFallingBlockLand(EntityChangeBlockEvent event) {
        if (event.getEntity().getCustomName() != null && event.getEntity().getCustomName().equalsIgnoreCase("banAnimations")) {
            event.getEntity().remove();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ArmorStand) {
            if ("ba-stand".equals(event.getRightClicked().getCustomName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageEntityEvent(EntityDamageByEntityEvent event) {
        String customName = event.getDamager().getCustomName();
        if (customName != null && (customName.equals("ba-fangs") || customName.equalsIgnoreCase("bananimations-guardian"))) {
            event.setCancelled(true);
        }
    }
}