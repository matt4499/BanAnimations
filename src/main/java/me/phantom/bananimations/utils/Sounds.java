package me.phantom.bananimations.utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

public enum Sounds {
   ENTITY_WITHER_AMBIENT("ENTITY_WITHER_AMBIENT", "WITHER_IDLE"),
   ENTITY_PLAYER_HURT("ENTITY_PLAYER_HURT", "HURT_FLESH"),
   BLOCK_GLASS_BREAK("BLOCK_GLASS_BREAK", "GLASS"),
   ENTITY_PIG_AMBIENT("ENTITY_PIG_AMBIENT", "PIG_IDLE"),
   BLOCK_ANVIL_LAND("BLOCK_ANVIL_LAND", "ANVIL_LAND"),
   ENTITY_TNT_PRIMED("ENTITY_TNT_PRIMED", "FUSE"),
   ENTITY_GENERIC_EXPLODE("ENTITY_GENERIC_EXPLODE", "EXPLODE"),
   ENTITY_PIG_DEATH("ENTITY_PIG_DEATH", "PIG_DEATH");

   private String newSound;
   private String oldSound;
   private boolean isNewSound = !Bukkit.getVersion().contains("1.8");

   private Sounds(String newSound, String oldSound) {
      this.newSound = newSound;
      this.oldSound = oldSound;
   }

   public Sound get() {
      return Sound.valueOf(this.isNewSound ? this.newSound : this.oldSound);
   }
}