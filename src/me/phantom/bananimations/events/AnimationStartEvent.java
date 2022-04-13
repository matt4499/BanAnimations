package me.phantom.bananimations.events;

import me.phantom.bananimations.AnimationType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

@SuppressWarnings("ALL")
public class AnimationStartEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private final CommandSender sender;
   private final Player target;
   private final AnimationType type;
   private final String reason;

   public AnimationStartEvent(CommandSender sender, Player target, AnimationType type, String reason) {
      this.sender = sender;
      this.target = target;
      this.type = type;
      this.reason = reason;
   }

   public CommandSender getSender() {
      return this.sender;
   }

   public Player getTarget() {
      return this.target;
   }

   public AnimationType getType() {
      return this.type;
   }

   public String getReason() {
      return this.reason;
   }

   public @Nonnull HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}