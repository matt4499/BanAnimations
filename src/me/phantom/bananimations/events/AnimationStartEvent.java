package me.phantom.bananimations.events;

import me.phantom.bananimations.AnimationType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AnimationStartEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private CommandSender sender;
   private Player target;
   private AnimationType type;
   private String reason;
   private String animationName;

   public AnimationStartEvent(CommandSender sender, Player target, AnimationType type, String reason, String animationName) {
      this.sender = sender;
      this.target = target;
      this.type = type;
      this.reason = reason;
      this.animationName = animationName;
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

   public String getAnimationName() {
      return this.animationName;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}