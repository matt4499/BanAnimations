package me.phantom.bananimations;

public enum AnimationType {
   BAN("ban"),
   KICK("kick"),
   TEST("test"),
   MUTE("mute"),
   IP_BAN("ipban"),
   TEMP_BAN("tempban"),
   TEMP_MUTE("tempmute");

   private final String command;

   private AnimationType(String command) {
      this.command = command;
   }

   public String toString() {
      return this.command;
   }
}