package me.phantom.bananimations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.phantom.bananimations.animations.CageAnimation;
import me.phantom.bananimations.animations.CarFallAnimation;
import me.phantom.bananimations.animations.ExplodeAnimation;
import me.phantom.bananimations.animations.FreezeAnimation;
import me.phantom.bananimations.animations.GwenAnimation;
import me.phantom.bananimations.animations.LightningAnimation;
import me.phantom.bananimations.animations.MeteorAnimation;
import me.phantom.bananimations.animations.PigAnimation;
import me.phantom.bananimations.animations.SnapTrapAnimation;
import me.phantom.bananimations.animations.SpitAnimation;
import me.phantom.bananimations.animations.SwordFallAnimation;
import me.phantom.bananimations.animations.YinYang;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.commands.BATabCompletion;
import me.phantom.bananimations.commands.BanAnimationsCommand;
import me.phantom.bananimations.listeners.AnimationListeners;
import me.phantom.bananimations.listeners.AutoAnimationListener;
import me.phantom.bananimations.listeners.FrozenListener;
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
    public Logger logger;
    public static JavaPlugin instance;

    @Override
    public void onEnable() {
      instance = this;
        PluginDescriptionFile pdfFile = this.getDescription();
        logger = this.getLogger();

        this.config.loadDefaultConfig();
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
}