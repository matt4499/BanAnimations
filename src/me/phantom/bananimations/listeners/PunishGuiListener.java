package me.phantom.bananimations.listeners;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.phantom.bananimations.AnimationType;
import me.phantom.bananimations.BanAnimations;
import me.phantom.bananimations.Messages;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.utils.Utils;

public class PunishGuiListener implements Listener {
    private static final int INVENTORY_SIZE = 54;

    private final BanAnimations plugin;
    private final Map<UUID, PunishSession> sessions = new HashMap<>();
    private final NamespacedKey actionKey;
    private final NamespacedKey valueKey;

    public PunishGuiListener(BanAnimations plugin) {
        this.plugin = plugin;
        this.actionKey = new NamespacedKey(plugin, "punish_action");
        this.valueKey = new NamespacedKey(plugin, "punish_value");
    }

    public boolean openPunishMenu(Player staff, String targetName) {
        if (!this.plugin.getConfig().getBoolean("punish_gui.enabled", true)) {
            staff.sendMessage(Utils.color("&cPunish GUI is disabled in config."));
            return false;
        }

        if (targetName == null || targetName.trim().isEmpty()) {
            staff.sendMessage(Utils.color("&cUsage: /ba punish [name]"));
            return false;
        }

        if (this.plugin.getConfig().getBoolean("punish_gui.open_requires_target_online", true)
                && Bukkit.getPlayerExact(targetName) == null) {
            Messages.ERROR_PLAYER_NOT_ONLINE.send(staff, targetName);
            return false;
        }

        PunishSession session = new PunishSession(targetName);
        this.sessions.put(staff.getUniqueId(), session);
        this.openTypeMenu(staff, session);
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        PunishSession session = this.sessions.get(player.getUniqueId());
        if (session == null) {
            return;
        }

        String openTitle = event.getView().getTitle();
        if (!this.isPunishGuiTitle(openTitle)) {
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = event.getCurrentItem().getItemMeta();
        if (meta == null) {
            return;
        }

        String action = meta.getPersistentDataContainer().get(this.actionKey, PersistentDataType.STRING);
        String value = meta.getPersistentDataContainer().get(this.valueKey, PersistentDataType.STRING);
        if (action == null) {
            return;
        }

        switch (action) {
            case "cancel":
                this.sessions.remove(player.getUniqueId());
                player.closeInventory();
                break;
            case "back":
                this.openPreviousPage(player, session);
                break;
            case "type":
                this.handleTypeSelection(player, session, value);
                break;
            case "duration":
                session.setDuration(value);
                session.setPage(PunishPage.REASON);
                this.openReasonMenu(player, session);
                break;
            case "reason":
                session.setReason(value);
                session.setPage(PunishPage.ANIMATION);
                this.openAnimationMenu(player, session);
                break;
            case "animation":
                this.handleAnimationSelection(player, session, value);
                break;
            case "animation_random":
                this.handleRandomAnimationSelection(player, session);
                break;
            default:
                break;
        }
    }

    private void handleTypeSelection(Player player, PunishSession session, String value) {
        AnimationType selectedType = this.mapType(value);
        if (selectedType == null) {
            player.sendMessage(Utils.color("&cInvalid punishment type selected."));
            return;
        }

        session.setType(selectedType);
        if (this.shouldShowDuration(selectedType)) {
            session.setPage(PunishPage.DURATION);
            this.openDurationMenu(player, session);
            return;
        }

        session.setDuration("permanent");
        if (this.shouldShowReason(selectedType)) {
            session.setPage(PunishPage.REASON);
            this.openReasonMenu(player, session);
            return;
        }

        session.setReason("");
        session.setPage(PunishPage.ANIMATION);
        this.openAnimationMenu(player, session);
    }

    private void handleAnimationSelection(Player player, PunishSession session, String animationName) {
        Animation animation = this.plugin.getAnimation(animationName);
        if (animation == null) {
            player.sendMessage(Utils.color("&cThat animation is no longer available."));
            this.openAnimationMenu(player, session);
            return;
        }

        this.executePunishment(player, session, animation, animation.getName());
    }

    private void handleRandomAnimationSelection(Player player, PunishSession session) {
        Animation animation = this.plugin.getRandomAnimation();
        if (animation == null) {
            player.sendMessage(Utils.color("&cNo animations are available for random selection."));
            return;
        }

        this.executePunishment(player, session, animation, animation.getName());
    }

    private void executePunishment(Player staff, PunishSession session, Animation animation, String animationName) {
        Player target = Bukkit.getPlayerExact(session.getTargetName());
        if (target == null) {
            Messages.ERROR_PLAYER_NOT_ONLINE.send(staff, session.getTargetName());
            this.sessions.remove(staff.getUniqueId());
            staff.closeInventory();
            return;
        }

        AnimationType effectiveType = this.resolveEffectiveType(session.getType(), session.getDuration());

        if (effectiveType != AnimationType.TEST && target.hasPermission(this.plugin.getPermissionNode("bypass"))) {
            Messages.ERROR_CANT_PLAY_ANIMATION_ON_PLAYER.send(staff, target.getName());
            return;
        }

        if (this.plugin.isFrozen(target)) {
            Messages.ERROR_PLAYER_ALREADY_IN_ANIMATION.send(staff, target.getName());
            return;
        }

        String reason = session.getReason() == null ? "" : session.getReason();
        String duration = session.getDuration() == null ? "permanent" : session.getDuration();

        this.plugin.setPendingPunishment(target, effectiveType, duration, reason, animationName);
        Messages.ANIMATION_START_MESSAGE.send(staff, animationName, target.getName());
        animation.callAnimation(staff, target, effectiveType, reason);

        this.sessions.remove(staff.getUniqueId());
        staff.closeInventory();
    }

    private void openPreviousPage(Player player, PunishSession session) {
        switch (session.getPage()) {
            case DURATION:
                session.setPage(PunishPage.TYPE);
                this.openTypeMenu(player, session);
                break;
            case REASON:
                if (this.shouldShowDuration(session.getType())) {
                    session.setPage(PunishPage.DURATION);
                    this.openDurationMenu(player, session);
                } else {
                    session.setPage(PunishPage.TYPE);
                    this.openTypeMenu(player, session);
                }
                break;
            case ANIMATION:
                if (this.shouldShowReason(session.getType())) {
                    session.setPage(PunishPage.REASON);
                    this.openReasonMenu(player, session);
                } else if (this.shouldShowDuration(session.getType())) {
                    session.setPage(PunishPage.DURATION);
                    this.openDurationMenu(player, session);
                } else {
                    session.setPage(PunishPage.TYPE);
                    this.openTypeMenu(player, session);
                }
                break;
            default:
                break;
        }
    }

    private void openTypeMenu(Player player, PunishSession session) {
        session.setPage(PunishPage.TYPE);
        Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, this.colored(this.plugin.getConfig().getString("punish_gui.titles.type", "&cPunish: Select Type")));

        ConfigurationSection section = this.plugin.getConfig().getConfigurationSection("punish_gui.types");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                AnimationType mappedType = this.mapType(key);
                if (mappedType == null) {
                    continue;
                }

                if (!player.hasPermission(this.plugin.getPermissionForType(mappedType))) {
                    continue;
                }

                int slot = this.plugin.getConfig().getInt("punish_gui.types." + key + ".slot", -1);
                if (slot < 0 || slot >= INVENTORY_SIZE) {
                    continue;
                }
                inventory.setItem(slot, this.createConfigButton("punish_gui.types." + key, "type", key));
            }
        }

        this.applyCancelButton(inventory);
        player.openInventory(inventory);
    }

    private void openDurationMenu(Player player, PunishSession session) {
        Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, this.colored(this.plugin.getConfig().getString("punish_gui.titles.duration", "&cPunish: Select Duration")));

        List<DurationOption> durationOptions = this.getDurationOptions();
        for (int i = 0; i < durationOptions.size(); i++) {
            DurationOption option = durationOptions.get(i);
            inventory.setItem(option.getSlot(), this.createDurationButton(option));
        }

        this.applyBackButton(inventory);
        this.applyCancelButton(inventory);
        player.openInventory(inventory);
    }

    private void openReasonMenu(Player player, PunishSession session) {
        Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, this.colored(this.plugin.getConfig().getString("punish_gui.titles.reason", "&cPunish: Select Reason")));

        List<ReasonOption> reasonOptions = this.getReasonOptions();
        for (int i = 0; i < reasonOptions.size(); i++) {
            ReasonOption option = reasonOptions.get(i);
            inventory.setItem(option.getSlot(), this.createReasonButton(option));
        }

        this.applyBackButton(inventory);
        this.applyCancelButton(inventory);
        player.openInventory(inventory);
    }

    private void openAnimationMenu(Player player, PunishSession session) {
        FileConfiguration configuration = this.plugin.getConfig();
        Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, this.colored(configuration.getString("punish_gui.titles.animation", "&cPunish: Select Animation")));
        Map<String, AnimationMetadata> metadata = this.getAnimationMetadata();

        Set<String> animationNames = this.plugin.getAnimationNames();
        List<String> sortedAnimations = new ArrayList<>(animationNames);
        sortedAnimations.sort(Comparator.comparing(name -> this.getAnimationDisplayName(name, metadata).toLowerCase(Locale.ENGLISH)));

        List<Integer> availableSlots = this.getAnimationSlots(configuration);
        int maxAnimationItems = Math.max(0, availableSlots.size() - 1);
        int shownAnimationCount = 0;
        for (int i = 0; i < sortedAnimations.size() && i < maxAnimationItems; i++) {
            String animationName = sortedAnimations.get(i);
            int slot = availableSlots.get(i);
            inventory.setItem(slot, this.createAnimationItem(animationName, metadata));
            shownAnimationCount++;
        }

        if (!availableSlots.isEmpty()) {
            int randomIndex = Math.min(shownAnimationCount, availableSlots.size() - 1);
            int randomSlot = availableSlots.get(randomIndex);
            inventory.setItem(randomSlot, this.createConfigButton("punish_gui.buttons.random_animation", "animation_random", "random"));
        }

        this.applyBackButton(inventory);
        this.applyCancelButton(inventory);
        player.openInventory(inventory);
    }

    private ItemStack createAnimationItem(String animationName, Map<String, AnimationMetadata> metadata) {
        FileConfiguration configuration = this.plugin.getConfig();
        String description = this.getAnimationDescription(animationName, metadata);
        String displayName = this.getAnimationDisplayName(animationName, metadata);

        Material material = this.material(configuration.getString("punish_gui.buttons.animation_template.material", "BOOK"), Material.BOOK);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String displayNameTemplate = configuration.getString("punish_gui.buttons.animation_template.name", "&b%animation%");
            meta.setDisplayName(this.colored(displayNameTemplate
                    .replace("%animation%", displayName)
                    .replace("%display_name%", displayName)
                    .replace("%internal_name%", animationName)));

            List<String> lore = new ArrayList<>();
            lore.add(this.colored("&8" + description));
            meta.setLore(lore);

            meta.getPersistentDataContainer().set(this.actionKey, PersistentDataType.STRING, "animation");
            meta.getPersistentDataContainer().set(this.valueKey, PersistentDataType.STRING, animationName.toLowerCase(Locale.ENGLISH));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createConfigButton(String path, String action, String value) {
        FileConfiguration configuration = this.plugin.getConfig();

        Material material = this.material(configuration.getString(path + ".material", "BARRIER"), Material.BARRIER);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(this.colored(configuration.getString(path + ".name", "&cUnnamed")));

            List<String> rawLore = configuration.getStringList(path + ".lore");
            if (!rawLore.isEmpty()) {
                List<String> coloredLore = new ArrayList<>();
                for (String line : rawLore) {
                    coloredLore.add(this.colored(line));
                }
                meta.setLore(coloredLore);
            }

            meta.getPersistentDataContainer().set(this.actionKey, PersistentDataType.STRING, action);
            meta.getPersistentDataContainer().set(this.valueKey, PersistentDataType.STRING, value);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createDurationButton(DurationOption option) {
        ItemStack item = new ItemStack(option.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(this.colored(option.getName()));

            if (!option.getLore().isEmpty()) {
                List<String> coloredLore = new ArrayList<>();
                for (String line : option.getLore()) {
                    coloredLore.add(this.colored(line));
                }
                meta.setLore(coloredLore);
            }

            meta.getPersistentDataContainer().set(this.actionKey, PersistentDataType.STRING, "duration");
            meta.getPersistentDataContainer().set(this.valueKey, PersistentDataType.STRING, option.getTime());
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createReasonButton(ReasonOption option) {
        ItemStack item = new ItemStack(option.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(this.colored(option.getName()));

            if (!option.getLore().isEmpty()) {
                List<String> coloredLore = new ArrayList<>();
                for (String line : option.getLore()) {
                    coloredLore.add(this.colored(line));
                }
                meta.setLore(coloredLore);
            }

            meta.getPersistentDataContainer().set(this.actionKey, PersistentDataType.STRING, "reason");
            meta.getPersistentDataContainer().set(this.valueKey, PersistentDataType.STRING, option.getReason());
            item.setItemMeta(meta);
        }

        return item;
    }

    private List<DurationOption> getDurationOptions() {
        FileConfiguration configuration = this.plugin.getConfig();
        List<DurationOption> options = new ArrayList<>();

        List<?> rawOptions = configuration.getList("punish_gui.duration_options");
        if (rawOptions != null && !rawOptions.isEmpty()) {
            for (Object raw : rawOptions) {
                if (!(raw instanceof Map<?, ?> map)) {
                    continue;
                }

                String name = this.stringValue(map.get("name"));
                String time = this.stringValue(map.get("time"));
                Material material = this.material(this.stringValue(map.get("material")));
                Integer slot = this.intValue(map.get("slot"));
                List<String> lore = this.stringListValue(map.get("lore"));

                if (name == null || time == null || slot == null || material == null) {
                    continue;
                }

                if (slot < 0 || slot >= INVENTORY_SIZE) {
                    continue;
                }

                options.add(new DurationOption(name, time, material, slot, lore));
            }
        }

        return options;
    }

    private List<ReasonOption> getReasonOptions() {
        FileConfiguration configuration = this.plugin.getConfig();
        List<ReasonOption> options = new ArrayList<>();

        List<?> rawOptions = configuration.getList("punish_gui.reason_options");
        if (rawOptions != null && !rawOptions.isEmpty()) {
            for (Object raw : rawOptions) {
                if (!(raw instanceof Map<?, ?> map)) {
                    continue;
                }

                String name = this.stringValue(map.get("name"));
                String reason = this.stringValue(map.get("reason"));
                Material material = this.material(this.stringValue(map.get("material")));
                Integer slot = this.intValue(map.get("slot"));
                List<String> lore = this.stringListValue(map.get("lore"));

                if (name == null || reason == null || slot == null || material == null) {
                    continue;
                }

                if (slot < 0 || slot >= INVENTORY_SIZE) {
                    continue;
                }

                options.add(new ReasonOption(name, reason, material, slot, lore));
            }
        }

        return options;
    }

    private String stringValue(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value);
        return text.isEmpty() ? null : text;
    }

    private Integer intValue(Object value) {
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

    private List<String> stringListValue(Object value) {
        List<String> output = new ArrayList<>();
        if (value instanceof List<?> values) {
            for (Object element : values) {
                if (element != null) {
                    output.add(String.valueOf(element));
                }
            }
        }
        return output;
    }

    private void applyCancelButton(Inventory inventory) {
        int slot = this.plugin.getConfig().getInt("punish_gui.buttons.cancel.slot", 49);
        if (slot >= 0 && slot < INVENTORY_SIZE) {
            inventory.setItem(slot, this.createConfigButton("punish_gui.buttons.cancel", "cancel", "cancel"));
        }
    }

    private void applyBackButton(Inventory inventory) {
        int slot = this.plugin.getConfig().getInt("punish_gui.buttons.back.slot", 45);
        if (slot >= 0 && slot < INVENTORY_SIZE) {
            inventory.setItem(slot, this.createConfigButton("punish_gui.buttons.back", "back", "back"));
        }
    }

    private List<Integer> getAnimationSlots(FileConfiguration configuration) {
        List<Integer> slots = new ArrayList<>();
        int backSlot = configuration.getInt("punish_gui.buttons.back.slot", 45);
        int cancelSlot = configuration.getInt("punish_gui.buttons.cancel.slot", 49);
        int randomSlot = configuration.getInt("punish_gui.buttons.random_animation.slot", 49);

        for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
            if (slot == backSlot || slot == cancelSlot || slot == randomSlot) {
                continue;
            }
            slots.add(slot);
        }

        return slots;
    }

    private boolean isPunishGuiTitle(String title) {
        FileConfiguration cfg = this.plugin.getConfig();
        String type = this.colored(cfg.getString("punish_gui.titles.type", "&cPunish: Select Type"));
        String duration = this.colored(cfg.getString("punish_gui.titles.duration", "&cPunish: Select Duration"));
        String reason = this.colored(cfg.getString("punish_gui.titles.reason", "&cPunish: Select Reason"));
        String animation = this.colored(cfg.getString("punish_gui.titles.animation", "&cPunish: Select Animation"));
        return title.equals(type) || title.equals(duration) || title.equals(reason) || title.equals(animation);
    }

    private AnimationType mapType(String key) {
        if (key == null) {
            return null;
        }

        switch (key.toLowerCase(Locale.ENGLISH)) {
            case "ban":
                return AnimationType.BAN;
            case "kick":
                return AnimationType.KICK;
            case "mute":
                return AnimationType.MUTE;
            case "ipban":
                return AnimationType.IP_BAN;
            case "test":
                return AnimationType.TEST;
            default:
                return null;
        }
    }

    private AnimationType resolveEffectiveType(AnimationType selectedType, String duration) {
        if (selectedType == null) {
            return AnimationType.BAN;
        }

        String normalizedDuration = duration == null ? "permanent" : duration.trim().toLowerCase(Locale.ENGLISH);
        boolean permanent = normalizedDuration.isEmpty() || normalizedDuration.equals("permanent") || normalizedDuration.equals("perm");

        switch (selectedType) {
            case BAN:
                return permanent ? AnimationType.BAN : AnimationType.TEMP_BAN;
            case MUTE:
                return permanent ? AnimationType.MUTE : AnimationType.TEMP_MUTE;
            case IP_BAN:
                return permanent ? AnimationType.IP_BAN : AnimationType.TEMP_BAN;
            default:
                return selectedType;
        }
    }

    private boolean shouldShowDuration(AnimationType type) {
        return type == AnimationType.BAN || type == AnimationType.MUTE || type == AnimationType.IP_BAN;
    }

    private boolean shouldShowReason(AnimationType type) {
        return type != AnimationType.TEST;
    }

    private Material material(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Material.matchMaterial(value);
    }

    private Material material(String value, Material fallback) {
        Material parsed = this.material(value);
        return parsed == null ? fallback : parsed;
    }

    private String colored(String text) {
        return Utils.color(text == null ? "" : text);
    }

    private String getAnimationDescription(String animationName, Map<String, AnimationMetadata> metadata) {
        String key = animationName == null ? "" : animationName.toLowerCase(Locale.ENGLISH);
        AnimationMetadata entry = metadata.get(key);
        if (entry != null) {
            return entry.getDescription();
        }
        return "Play this animation as part of the punishment.";
    }

    private String getAnimationDisplayName(String animationName, Map<String, AnimationMetadata> metadata) {
        String key = animationName == null ? "" : animationName.toLowerCase(Locale.ENGLISH);
        AnimationMetadata entry = metadata.get(key);
        if (entry != null) {
            return entry.getDisplayName();
        }
        return animationName;
    }

    private Map<String, AnimationMetadata> getAnimationMetadata() {
        Map<String, AnimationMetadata> metadata = new LinkedHashMap<>();
        List<?> rawOptions = this.plugin.getConfig().getList("punish_gui.animation_metadata");
        if (rawOptions == null) {
            return metadata;
        }

        for (Object raw : rawOptions) {
            if (!(raw instanceof Map<?, ?> map)) {
                continue;
            }

            String internalName = this.stringValue(map.get("internal_name"));
            String displayName = this.stringValue(map.get("display_name"));
            String description = this.stringValue(map.get("description"));

            if (internalName == null || displayName == null || description == null) {
                continue;
            }

            metadata.put(internalName.toLowerCase(Locale.ENGLISH), new AnimationMetadata(displayName, description));
        }

        return metadata;
    }

    private static class DurationOption {
        private final String name;
        private final String time;
        private final Material material;
        private final int slot;
        private final List<String> lore;

        private DurationOption(String name, String time, Material material, int slot, List<String> lore) {
            this.name = name;
            this.time = time;
            this.material = material;
            this.slot = slot;
            this.lore = lore == null ? new ArrayList<>() : lore;
        }

        public String getName() {
            return this.name;
        }

        public String getTime() {
            return this.time;
        }

        public Material getMaterial() {
            return this.material;
        }

        public int getSlot() {
            return this.slot;
        }

        public List<String> getLore() {
            return this.lore;
        }
    }

    private static class ReasonOption {
        private final String name;
        private final String reason;
        private final Material material;
        private final int slot;
        private final List<String> lore;

        private ReasonOption(String name, String reason, Material material, int slot, List<String> lore) {
            this.name = name;
            this.reason = reason;
            this.material = material;
            this.slot = slot;
            this.lore = lore == null ? new ArrayList<>() : lore;
        }

        public String getName() {
            return this.name;
        }

        public String getReason() {
            return this.reason;
        }

        public Material getMaterial() {
            return this.material;
        }

        public int getSlot() {
            return this.slot;
        }

        public List<String> getLore() {
            return this.lore;
        }
    }

    private static class AnimationMetadata {
        private final String displayName;
        private final String description;

        private AnimationMetadata(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public String getDescription() {
            return this.description;
        }
    }

    private enum PunishPage {
        TYPE,
        DURATION,
        REASON,
        ANIMATION
    }

    private static class PunishSession {
        private final String targetName;
        private PunishPage page;
        private AnimationType type;
        private String duration;
        private String reason;

        private PunishSession(String targetName) {
            this.targetName = targetName;
            this.page = PunishPage.TYPE;
            this.duration = "permanent";
            this.reason = "Rule Violation";
        }

        public String getTargetName() {
            return this.targetName;
        }

        public PunishPage getPage() {
            return this.page;
        }

        public void setPage(PunishPage page) {
            this.page = page;
        }

        public AnimationType getType() {
            return this.type;
        }

        public void setType(AnimationType type) {
            this.type = type;
        }

        public String getDuration() {
            return this.duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getReason() {
            return this.reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
