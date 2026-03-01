package me.phantom.bananimations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.phantom.bananimations.animations.AllayVerdictAnimation;
import me.phantom.bananimations.animations.ArmadilloAnimation;
import me.phantom.bananimations.animations.BoggedBarrageAnimation;
import me.phantom.bananimations.animations.CageAnimation;
import me.phantom.bananimations.animations.CamelCourtAnimation;
import me.phantom.bananimations.animations.CarFallAnimation;
import me.phantom.bananimations.animations.CreakingCurseAnimation;
import me.phantom.bananimations.animations.EndermanAnimation;
import me.phantom.bananimations.animations.ExplodeAnimation;
import me.phantom.bananimations.animations.FreezeAnimation;
import me.phantom.bananimations.animations.GoatAnimation;
import me.phantom.bananimations.animations.GwenAnimation;
import me.phantom.bananimations.animations.LightningAnimation;
import me.phantom.bananimations.animations.MeteorAnimation;
import me.phantom.bananimations.animations.PhantomAnimation;
import me.phantom.bananimations.animations.PigAnimation;
import me.phantom.bananimations.animations.SnapTrapAnimation;
import me.phantom.bananimations.animations.SnifferTribunalAnimation;
import me.phantom.bananimations.animations.SpitAnimation;
import me.phantom.bananimations.animations.SwordFallAnimation;
import me.phantom.bananimations.animations.WitchTrialAnimation;
import me.phantom.bananimations.animations.YinYang;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.commands.BATabCompletion;
import me.phantom.bananimations.commands.BanAnimationsCommand;
import me.phantom.bananimations.listeners.AnimationListeners;
import me.phantom.bananimations.listeners.AutoAnimationListener;
import me.phantom.bananimations.listeners.FrozenListener;
import me.phantom.bananimations.listeners.PunishGuiListener;
import me.phantom.bananimations.utils.Task;
import me.phantom.bananimations.utils.mobutils.MobUtils;

/**
 * Main class for the BanAnimations plugin.
 */
public class BanAnimations extends JavaPlugin {
    public static final HashMap<String, Animation> animations = new HashMap<>();
    private final ArrayList<Player> frozenPlayers = new ArrayList<>();
    private Random random;
    private MobUtils mobUtils;
    private final Config config = new Config(this);
    private PunishGuiListener punishGuiListener;
    private final ConcurrentHashMap<UUID, PendingPunishment> pendingPunishments = new ConcurrentHashMap<>();
    public Logger logger;
    public static JavaPlugin instance;

    @Override
    public void onEnable() {
      instance = this;
        PluginDescriptionFile pdfFile = this.getDescription();
        logger = this.getLogger();

        this.config.loadDefaultConfig();
        if (!this.config.validateConfig()) {
            logger.severe("BanAnimations configuration validation failed. Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Messages.setFile(this.getConfig());

        this.mobUtils = new MobUtils(this);
        this.random = new Random();
        new Task(this);

        this.animationHooks();
        this.registerCommands();
        this.registerEvents();

        logger.info(String.format("BanAnimations (%s) has been enabled!", pdfFile.getVersion()));
    }

    @Override
    public void onDisable() {
        animations.clear();
        frozenPlayers.clear();
        logger.info("BanAnimations has been disabled!");
    }

    /**
     * Registers commands for the plugin.
     */
    public void registerCommands() {
        Objects.requireNonNull(getCommand("bananimations")).setExecutor(new BanAnimationsCommand(this));
        Objects.requireNonNull(getCommand("bananimations")).setTabCompleter(new BATabCompletion());
    }

    /**
     * Registers event listeners for the plugin.
     */
    public void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new FrozenListener(this), this);
        pm.registerEvents(new AnimationListeners(), this);
        pm.registerEvents(new AutoAnimationListener(this), this);
        this.punishGuiListener = new PunishGuiListener(this);
        pm.registerEvents(this.punishGuiListener, this);
    }

    /**
     * Registers all available animations.
     */
    public void animationHooks() {
        (new CageAnimation()).hook();
        (new CarFallAnimation()).hook();
        (new ExplodeAnimation()).hook();
        (new FreezeAnimation()).hook();
        (new GwenAnimation()).hook();
        (new LightningAnimation()).hook();
        (new SpitAnimation()).hook();
        (new MeteorAnimation()).hook();
        (new PigAnimation()).hook();
        (new SwordFallAnimation()).hook();
        (new SnapTrapAnimation()).hook();
        (new YinYang()).hook();
        (new AllayVerdictAnimation()).hook();
        (new BoggedBarrageAnimation()).hook();
        (new SnifferTribunalAnimation()).hook();
        (new ArmadilloAnimation()).hook();
        (new CamelCourtAnimation()).hook();
        (new CreakingCurseAnimation()).hook();
        (new GoatAnimation()).hook();
        (new PhantomAnimation()).hook();
        (new WitchTrialAnimation()).hook();
        (new EndermanAnimation()).hook();
    }

    public boolean isFrozen(Player player) {
        return this.frozenPlayers.contains(player);
    }

    public void freeze(Player player) {
        if (player != null && !this.frozenPlayers.contains(player)) {
            this.frozenPlayers.add(player);
        }
    }

    public void unFreeze(Player player) {
        if (player != null) {
            this.frozenPlayers.remove(player);
        }
    }

    /**
     * Registers a new animation.
     * @param animation The animation to register.
     * @param name The name of the animation.
     */
    public void registerAnimation(Animation animation, String name) {
        String lowerCaseName = name.toLowerCase();
        if (animations.containsKey(lowerCaseName)) {
            logger.warning("Attempted to register animation with duplicate name: '" + lowerCaseName + "'. Overwriting.");
        }
        animations.put(lowerCaseName, animation);
        logger.info("Animation " + lowerCaseName + " has been loaded!");
    }

    public boolean isValidAnimation(String animationName) {
        if (animationName == null) {
            return false;
        }
        String lowerCaseName = animationName.toLowerCase();
        return !lowerCaseName.equals("random") && animations.containsKey(lowerCaseName);
    }

    public Animation getAnimation(String animationName) {
        if (animationName == null) return null;
        return animations.get(animationName.toLowerCase());
    }

    public Set<String> getAnimationNames() {
        return animations.keySet();
    }

    /**
     * Gets a random animation from all available animations.
     *
     * @return A random Animation instance, or null if no animations are available at all.
     */
    public Animation getRandomAnimation() {
        Object[] allValues = animations.values().toArray();
        if (allValues.length == 0) {
            logger.severe("No animations available to select for 'random'!");
            return null;
        }
        return (Animation) allValues[this.random.nextInt(allValues.length)];
      }

    public MobUtils getMobUtils() {
        return this.mobUtils;
    }

    public PunishGuiListener getPunishGuiListener() {
        return this.punishGuiListener;
    }

    public String getPermissionNode(String key) {
        return this.getConfig().getString("permissions." + key, "");
    }

    public String getPermissionForType(AnimationType type) {
        if (type == null) {
            return "";
        }

        switch (type) {
            case BAN:
                return this.getPermissionNode("ban");
            case KICK:
                return this.getPermissionNode("kick");
            case MUTE:
                return this.getPermissionNode("mute");
            case TEMP_BAN:
                return this.getPermissionNode("tempban");
            case IP_BAN:
                return this.getPermissionNode("ipban");
            case TEMP_MUTE:
                return this.getPermissionNode("tempmute");
            case TEST:
                return this.getPermissionNode("test");
            default:
                return "";
        }
    }

    public void setPendingPunishment(Player target, AnimationType type, String duration, String reason, String animationName) {
        if (target == null || type == null) {
            return;
        }
        this.pendingPunishments.put(target.getUniqueId(), new PendingPunishment(type, duration, reason, animationName));
    }

    public PendingPunishment takePendingPunishment(Player target) {
        if (target == null) {
            return null;
        }
        return this.pendingPunishments.remove(target.getUniqueId());
    }

    public static class PendingPunishment {
        private final AnimationType type;
        private final String duration;
        private final String reason;
        private final String animationName;

        public PendingPunishment(AnimationType type, String duration, String reason, String animationName) {
            this.type = type;
            this.duration = duration;
            this.reason = reason;
            this.animationName = animationName;
        }

        public AnimationType getType() {
            return this.type;
        }

        public String getDuration() {
            return this.duration;
        }

        public String getReason() {
            return this.reason;
        }

        public String getAnimationName() {
            return this.animationName;
        }
    }
}