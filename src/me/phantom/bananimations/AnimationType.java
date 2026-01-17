package me.phantom.bananimations;

/**
 * Represents the type of punishment or animation trigger.
 */
public enum AnimationType {
    BAN("ban"),
    KICK("kick"),
    TEST("test"),
    MUTE("mute"),
    IP_BAN("ipban"),
    TEMP_BAN("tempban"),
    TEMP_MUTE("tempmute");

    private final String command;

    /**
     * @param command The command associated with this animation type.
     */
    AnimationType(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return this.command;
    }
}