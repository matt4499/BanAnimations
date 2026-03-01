package me.phantom.bananimations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Handles the loading and default configuration for the plugin.
 */
public class Config {
    private final BanAnimations plugin;

    public Config(BanAnimations plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads the default configuration values if they are not already set.
     */
    public void loadDefaultConfig() {
        FileConfiguration configuration = this.plugin.getConfig();
        configuration.addDefault("play_animation_on.BAN.enabled", true);
        configuration.addDefault("play_animation_on.BAN.animation", "YINYANG");
        configuration.addDefault("play_animation_on.IP_BAN.enabled", true);
        configuration.addDefault("play_animation_on.IP_BAN.animation", "EXPLODE");
        configuration.addDefault("play_animation_on.TEMP_BAN.enabled", true);
        configuration.addDefault("play_animation_on.TEMP_BAN.animation", "SWORDFALL");
        configuration.addDefault("play_animation_on.KICK.enabled", true);
        configuration.addDefault("play_animation_on.KICK.animation", "SNAPTRAP");
        configuration.addDefault("play_animation_on.MUTE.enabled", true);
        configuration.addDefault("play_animation_on.MUTE.animation", "RANDOM");
        configuration.addDefault("play_animation_on.TEMP_MUTE.enabled", true);
        configuration.addDefault("play_animation_on.TEMP_MUTE.animation", "METEOR");
        configuration.addDefault("Messages.animation_start", "&cStarted animation {0} on {1}!");
        configuration.addDefault("Messages.no_permission", "&cYou do not have permission to use this command!");
        configuration.addDefault("Messages.no_permission_ban", "&cYou do not have permission to use this command!");
        configuration.addDefault("Messages.no_permission_ipban", "&cYou do not have permission to ip-ban users");
        configuration.addDefault("Messages.no_permission_tempban", "&cYou do not have permission to temp-ban users!");
        configuration.addDefault("Messages.no_permission_kick", "&cYou do not have permission to kick users!");
        configuration.addDefault("Messages.no_permission_mute", "&cYou do not have permission to mute users!");
        configuration.addDefault("Messages.no_permission_tempmute", "&cYou do not have permission to temp-mute users!");
        configuration.addDefault("Messages.no_permission_test", "&You do not have permission to test animations!");
        configuration.addDefault("Messages.player_not_online", "&c&l{0} &cis not online!");
        configuration.addDefault("Messages.player_animation_bypass_enabled", "&cYou can not play an animation on {0}!");
        configuration.addDefault("Messages.player_already_in_animation", "&c&l{0} is already in an animation!");
        configuration.addDefault("Messages.invalid_animation_name", "&cThere is no animation called &c&l{0}&c!");

        configuration.addDefault("punishment_commands.ban", "ban %player% %reason%");
        configuration.addDefault("punishment_commands.kick", "kick %player% %reason%");
        configuration.addDefault("punishment_commands.mute", "mute %player% %reason%");
        configuration.addDefault("punishment_commands.tempban", "tempban %player% %time% %reason%");
        configuration.addDefault("punishment_commands.ipban", "ipban %player% %reason%");
        configuration.addDefault("punishment_commands.tempmute", "tempmute %player% %time% %reason%");
        configuration.addDefault("punishment_commands.test", "");

        configuration.addDefault("permissions.use", "bananimations.use");
        configuration.addDefault("permissions.list", "bananimations.list");
        configuration.addDefault("permissions.punish", "bananimations.punish");
        configuration.addDefault("permissions.ban", "bananimations.ban");
        configuration.addDefault("permissions.kick", "bananimations.kick");
        configuration.addDefault("permissions.mute", "bananimations.mute");
        configuration.addDefault("permissions.tempban", "bananimations.tempban");
        configuration.addDefault("permissions.ipban", "bananimations.ipban");
        configuration.addDefault("permissions.tempmute", "bananimations.tempmute");
        configuration.addDefault("permissions.test", "bananimations.test");
        configuration.addDefault("permissions.bypass", "bananimation.bypass");

        configuration.addDefault("punish_gui.enabled", true);
        configuration.addDefault("punish_gui.open_requires_target_online", true);

        configuration.addDefault("punish_gui.titles.type", "&cPunish: Select Type");
        configuration.addDefault("punish_gui.titles.duration", "&cPunish: Select Duration");
        configuration.addDefault("punish_gui.titles.reason", "&cPunish: Select Reason");
        configuration.addDefault("punish_gui.titles.animation", "&cPunish: Select Animation");

        configuration.addDefault("punish_gui.buttons.cancel.slot", 49);
        configuration.addDefault("punish_gui.buttons.cancel.material", "BARRIER");
        configuration.addDefault("punish_gui.buttons.cancel.name", "&cCancel");
        configuration.addDefault("punish_gui.buttons.cancel.lore", java.util.Arrays.asList("&7Close this menu without punishing."));

        configuration.addDefault("punish_gui.buttons.back.slot", 45);
        configuration.addDefault("punish_gui.buttons.back.material", "ARROW");
        configuration.addDefault("punish_gui.buttons.back.name", "&eBack");
        configuration.addDefault("punish_gui.buttons.back.lore", java.util.Arrays.asList("&7Go to the previous page."));

        configuration.addDefault("punish_gui.buttons.random_animation.slot", 48);
        configuration.addDefault("punish_gui.buttons.random_animation.material", "NETHER_STAR");
        configuration.addDefault("punish_gui.buttons.random_animation.name", "&dRandom Animation");
        configuration.addDefault("punish_gui.buttons.random_animation.lore", java.util.Arrays.asList("&7Use any loaded animation at random."));

        configuration.addDefault("punish_gui.buttons.animation_template.material", "BOOK");
        configuration.addDefault("punish_gui.buttons.animation_template.name", "&b%animation%");
        configuration.addDefault("punish_gui.buttons.animation_template.lore", new ArrayList<>());

        configuration.addDefault("punish_gui.types.ban.slot", 10);
        configuration.addDefault("punish_gui.types.ban.material", "REDSTONE_BLOCK");
        configuration.addDefault("punish_gui.types.ban.name", "&cBan");
        configuration.addDefault("punish_gui.types.ban.lore", java.util.Arrays.asList("&7Permanent ban command."));

        configuration.addDefault("punish_gui.types.kick.slot", 12);
        configuration.addDefault("punish_gui.types.kick.material", "IRON_BOOTS");
        configuration.addDefault("punish_gui.types.kick.name", "&eKick");
        configuration.addDefault("punish_gui.types.kick.lore", java.util.Arrays.asList("&7Kick command."));

        configuration.addDefault("punish_gui.types.mute.slot", 14);
        configuration.addDefault("punish_gui.types.mute.material", "PAPER");
        configuration.addDefault("punish_gui.types.mute.name", "&6Mute");
        configuration.addDefault("punish_gui.types.mute.lore", java.util.Arrays.asList("&7Mute command."));

        configuration.addDefault("punish_gui.types.ipban.slot", 16);
        configuration.addDefault("punish_gui.types.ipban.material", "TNT");
        configuration.addDefault("punish_gui.types.ipban.name", "&4IP Ban");
        configuration.addDefault("punish_gui.types.ipban.lore", java.util.Arrays.asList("&7IP ban command."));

        configuration.addDefault("punish_gui.types.test.slot", 31);
        configuration.addDefault("punish_gui.types.test.material", "BLAZE_POWDER");
        configuration.addDefault("punish_gui.types.test.name", "&bTest");
        configuration.addDefault("punish_gui.types.test.lore", java.util.Arrays.asList("&7Play animation only; no punishment command."));

        List<Map<String, Object>> durationDefaults = new ArrayList<>();
        durationDefaults.add(this.buildDurationOption("&f1 Hour", "1h", "CLOCK", 10));
        durationDefaults.add(this.buildDurationOption("&f4 Hours", "4h", "CLOCK", 11));
        durationDefaults.add(this.buildDurationOption("&f8 Hours", "8h", "CLOCK", 12));
        durationDefaults.add(this.buildDurationOption("&f1 Day", "1d", "CLOCK", 13));
        durationDefaults.add(this.buildDurationOption("&f5 Days", "5d", "CLOCK", 14));
        durationDefaults.add(this.buildDurationOption("&f1 Week", "1w", "CLOCK", 15));
        durationDefaults.add(this.buildDurationOption("&f2 Weeks", "2w", "CLOCK", 16));
        durationDefaults.add(this.buildDurationOption("&f1 Month", "1mo", "CLOCK", 19));
        durationDefaults.add(this.buildDurationOption("&cPermanent", "permanent", "BEDROCK", 22));
        configuration.addDefault("punish_gui.duration_options", durationDefaults);

        List<Map<String, Object>> reasonDefaults = new ArrayList<>();
        reasonDefaults.add(this.buildReasonOption("&cHacking", "Hacking", "DIAMOND_SWORD", 10));
        reasonDefaults.add(this.buildReasonOption("&cXray", "Xray", "DIAMOND_ORE", 12));
        reasonDefaults.add(this.buildReasonOption("&eBehavior", "Bad Behavior", "BOOK", 14));
        reasonDefaults.add(this.buildReasonOption("&eSpam", "Spam", "PAPER", 16));
        reasonDefaults.add(this.buildReasonOption("&6Advertising", "Advertising", "OAK_SIGN", 28));
        reasonDefaults.add(this.buildReasonOption("&fOther", "Rule Violation", "NAME_TAG", 30));
        configuration.addDefault("punish_gui.reason_options", reasonDefaults);

        List<Map<String, Object>> animationMetadataDefaults = new ArrayList<>();
        animationMetadataDefaults.add(this.buildAnimationMetadata("allayverdict", "Allay Verdict", "Allays spiral inward and deliver a final verdict burst."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("armadillo", "Armadillo", "Armadillos form a tightening ring before impact."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("boggedbarrage", "Bogged Barrage", "Bogged marksmen surround and launch a poison volley."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("cage", "Cage", "An iron-like prison slams shut around the player."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("camelcourt", "Camel Court", "Camels form a solemn court around the target."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("carfall", "Car Fall", "A crushing vehicle drops from above."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("creakingcurse", "Creaking Curse", "Cursed totems jitter and close in with dark energy."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("enderman", "Enderman", "Endermen blink in and out around the target."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("explode", "Explode", "A dramatic explosion effect finishes the sequence."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("freeze", "Freeze", "A frozen prison locks the player in place."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("goat", "Goat", "Goats circle and ram inward for the final hit."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("gwen", "Gwen", "Guardians orbit faster and faster before detonation."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("lightning", "Lightning", "Lightning converges for a sudden final strike."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("meteor", "Meteor", "Meteors rain down in rapid succession."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("phantom", "Phantom", "Phantoms descend in a tightening aerial spiral."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("pig", "Pig", "A chaotic pig-themed punishment rushes in."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("snaptrap", "Snap Trap", "A snapping trap sequence clamps down."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("sniffertribunal", "Sniffer Tribunal", "Sniffers circle and close in to deliver judgment."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("spit", "Spit", "Llamas line up and spit from all sides."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("swordfall", "Sword Fall", "Swords rain from above toward the target."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("witchtrial", "Witch Trial", "Witches convene and cast a rapid trial barrage."));
        animationMetadataDefaults.add(this.buildAnimationMetadata("yinyang", "Yin Yang", "A symbolic yin-yang ritual concludes the punishment."));
        configuration.addDefault("punish_gui.animation_metadata", animationMetadataDefaults);

        configuration.options().copyDefaults(true);
        this.plugin.saveConfig();
        this.plugin.reloadConfig();
    }

    public boolean validateConfig() {
        FileConfiguration configuration = this.plugin.getConfig();

        String[] requiredPermissionKeys = new String[] {
                "use", "list", "punish", "ban", "kick", "mute", "tempban", "ipban", "tempmute", "test", "bypass"
        };
        for (String key : requiredPermissionKeys) {
            String path = "permissions." + key;
            String node = configuration.getString(path);
            if (node == null || node.trim().isEmpty()) {
                this.plugin.getLogger().severe("Invalid config: '" + path + "' must be configured and non-empty.");
                return false;
            }
        }

        String[] requiredCommandKeys = new String[] {"ban", "kick", "mute", "tempban", "ipban", "tempmute"};
        for (String key : requiredCommandKeys) {
            String path = "punishment_commands." + key;
            String command = configuration.getString(path);
            if (command == null || command.trim().isEmpty()) {
                this.plugin.getLogger().severe("Invalid config: '" + path + "' must be configured and non-empty.");
                return false;
            }
        }

        List<?> durationOptions = configuration.getList("punish_gui.duration_options");

        if (durationOptions == null || durationOptions.isEmpty()) {
            this.plugin.getLogger().severe("Invalid config: 'punish_gui.duration_options' must contain at least one entry.");
            return false;
        }

        Set<Integer> usedSlots = new HashSet<>();
        for (int i = 0; i < durationOptions.size(); i++) {
            Object raw = durationOptions.get(i);
            if (!(raw instanceof Map<?, ?> entry)) {
                this.plugin.getLogger().severe("Invalid config: 'punish_gui.duration_options[" + i + "]' must be a map/object.");
                return false;
            }

            String name = this.requiredString(entry, "name");
            String time = this.requiredString(entry, "time");
            String material = this.requiredString(entry, "material");
            Integer slot = this.requiredInt(entry, "slot");

            if (name == null || time == null || material == null || slot == null) {
                this.plugin.getLogger().severe("Invalid config: duration option at index " + i + " is missing required keys (name,time,material,slot).");
                return false;
            }

            if (slot < 0 || slot > 53) {
                this.plugin.getLogger().severe("Invalid config: duration option at index " + i + " has slot " + slot + " (must be between 0 and 53).");
                return false;
            }

            if (Material.matchMaterial(material) == null) {
                this.plugin.getLogger().severe("Invalid config: duration option at index " + i + " has unknown material '" + material + "'.");
                return false;
            }

            if (!usedSlots.add(slot)) {
                this.plugin.getLogger().severe("Invalid config: duplicate duration slot " + slot + " in 'punish_gui.duration_options'.");
                return false;
            }
        }

        List<?> reasonOptions = configuration.getList("punish_gui.reason_options");
        if (reasonOptions == null || reasonOptions.isEmpty()) {
            this.plugin.getLogger().severe("Invalid config: 'punish_gui.reason_options' must contain at least one entry.");
            return false;
        }

        usedSlots.clear();
        for (int i = 0; i < reasonOptions.size(); i++) {
            Object raw = reasonOptions.get(i);
            if (!(raw instanceof Map<?, ?> entry)) {
                this.plugin.getLogger().severe("Invalid config: 'punish_gui.reason_options[" + i + "]' must be a map/object.");
                return false;
            }

            String name = this.requiredString(entry, "name");
            String reason = this.requiredString(entry, "reason");
            String material = this.requiredString(entry, "material");
            Integer slot = this.requiredInt(entry, "slot");

            if (name == null || reason == null || material == null || slot == null) {
                this.plugin.getLogger().severe("Invalid config: reason option at index " + i + " is missing required keys (name,reason,material,slot).");
                return false;
            }

            if (slot < 0 || slot > 53) {
                this.plugin.getLogger().severe("Invalid config: reason option at index " + i + " has slot " + slot + " (must be between 0 and 53).");
                return false;
            }

            if (Material.matchMaterial(material) == null) {
                this.plugin.getLogger().severe("Invalid config: reason option at index " + i + " has unknown material '" + material + "'.");
                return false;
            }

            if (!usedSlots.add(slot)) {
                this.plugin.getLogger().severe("Invalid config: duplicate reason slot " + slot + " in 'punish_gui.reason_options'.");
                return false;
            }
        }

        List<?> animationMetadata = configuration.getList("punish_gui.animation_metadata");
        if (animationMetadata == null || animationMetadata.isEmpty()) {
            this.plugin.getLogger().severe("Invalid config: 'punish_gui.animation_metadata' must contain at least one entry.");
            return false;
        }

        Set<String> usedAnimationNames = new HashSet<>();
        for (int i = 0; i < animationMetadata.size(); i++) {
            Object raw = animationMetadata.get(i);
            if (!(raw instanceof Map<?, ?> entry)) {
                this.plugin.getLogger().severe("Invalid config: 'punish_gui.animation_metadata[" + i + "]' must be a map/object.");
                return false;
            }

            String internalName = this.requiredString(entry, "internal_name");
            String displayName = this.requiredString(entry, "display_name");
            String description = this.requiredString(entry, "description");

            if (internalName == null || displayName == null || description == null) {
                this.plugin.getLogger().severe("Invalid config: animation metadata at index " + i + " is missing required keys (internal_name,display_name,description).");
                return false;
            }

            String normalized = internalName.toLowerCase();
            if (!usedAnimationNames.add(normalized)) {
                this.plugin.getLogger().severe("Invalid config: duplicate animation metadata for internal_name '" + internalName + "'.");
                return false;
            }
        }

        return true;
    }

    private Map<String, Object> buildDurationOption(String name, String time, String material, int slot) {
        Map<String, Object> option = new LinkedHashMap<>();
        option.put("name", name);
        option.put("time", time);
        option.put("material", material);
        option.put("slot", slot);
        return option;
    }

    private Map<String, Object> buildReasonOption(String name, String reason, String material, int slot) {
        Map<String, Object> option = new LinkedHashMap<>();
        option.put("name", name);
        option.put("reason", reason);
        option.put("material", material);
        option.put("slot", slot);
        return option;
    }

    private Map<String, Object> buildAnimationMetadata(String internalName, String displayName, String description) {
        Map<String, Object> option = new LinkedHashMap<>();
        option.put("internal_name", internalName);
        option.put("display_name", displayName);
        option.put("description", description);
        return option;
    }

    private String requiredString(Map<?, ?> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        String stringValue = String.valueOf(value);
        return stringValue.isEmpty() ? null : stringValue;
    }

    private Integer requiredInt(Map<?, ?> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value != null) {
            try {
                return Integer.parseInt(String.valueOf(value));
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
}
