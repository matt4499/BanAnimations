package me.phantom.bananimations.listeners;

import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class AnimationListeners implements Listener {
   @EventHandler
   public void onFallingBlockland(EntityChangeBlockEvent event) {
      if (event.getEntity().getCustomName() != null && event.getEntity().getCustomName().equalsIgnoreCase("banAnimations")) {
         event.getEntity().remove();
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
      if (event.getRightClicked() instanceof ArmorStand && event.getRightClicked().getName() != null && event.getRightClicked().getName().equals("ba-stand")) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onEntityDamageEntityEvent(EntityDamageByEntityEvent event) {
      if (event.getDamager().getCustomName() != null && (event.getDamager().getCustomName().equals("ba-fangs") || event.getDamager().getCustomName().equalsIgnoreCase("bananimations-guardian"))) {
         event.setCancelled(true);
      }

   }
}