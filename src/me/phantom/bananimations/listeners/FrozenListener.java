package me.phantom.bananimations.listeners;

import me.phantom.bananimations.BanAnimations;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

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
          assert to != null;
          if (to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ()) {
            event.getPlayer().teleport(from.setDirection(to.getDirection()));
         }
      }

   }

   @EventHandler
   public void onDamage(EntityDamageEvent e) {
      if (e.getEntity() instanceof Player && this.plugin.isFrozen((Player)e.getEntity())) {
         e.setCancelled(true);
      }

   }
}