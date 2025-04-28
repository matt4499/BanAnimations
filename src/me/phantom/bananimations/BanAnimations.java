package me.phantom.bananimations;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import me.phantom.bananimations.animations.*;
import me.phantom.bananimations.api.Animation;
import me.phantom.bananimations.commands.BATabCompletion;
import me.phantom.bananimations.commands.BanAnimationsCommand;
import me.phantom.bananimations.listeners.AnimationListeners;
import me.phantom.bananimations.listeners.AutoAnimationListener;
import me.phantom.bananimations.listeners.FrozenListener;
import me.phantom.bananimations.utils.Task;
import me.phantom.bananimations.utils.mobutils.MobUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BanAnimations extends JavaPlugin {
   public static final HashMap<String, Animation> animations = new HashMap<>();
   private final ArrayList<Player> frozenPlayers = new ArrayList<>();
   private Random random;
   private MobUtils mobUtils;
   private final Config config = new Config(this);
   public Logger logger;


   /** Stores the lowercase names of animations allowed in the 'random' selection pool, loaded from config. */
   private List<String> randomAnimationPool = new ArrayList<>();



   @Override
   public void onEnable() {
      PluginDescriptionFile pdfFile = this.getDescription();
      logger = this.getLogger();

      this.config.loadDefaultConfig();
      Messages.setFile(this.getConfig());

      this.mobUtils = new MobUtils(this);
      this.random = new Random();
      new Task(this);

      this.animationHooks();


      this.loadRandomAnimationPool();


      this.registerCommands();
      this.registerEvents();

      logger.info(String.format("BanAnimations (%s) has been enabled!", pdfFile.getVersion()));
   }

   @Override
   public void onDisable() {
      animations.clear();
      frozenPlayers.clear();
      randomAnimationPool.clear();
      logger.info("BanAnimations has been disabled!");
   }


   public void registerCommands() {
      Objects.requireNonNull(getCommand("bananimations")).setExecutor(new BanAnimationsCommand(this));
      Objects.requireNonNull(getCommand("bananimations")).setTabCompleter(new BATabCompletion());
   }

   public void registerEvents() {
      PluginManager pm = Bukkit.getPluginManager();
      pm.registerEvents(new FrozenListener(this), this);
      pm.registerEvents(new AnimationListeners(), this);
      pm.registerEvents(new AutoAnimationListener(this), this);
   }

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
    * Gets a random animation, respecting the RandomAnimationPool from config.yml.
    * If the pool is empty or contains only invalid animations, it falls back to
    * selecting from ALL available animations.
    *
    * @return A random Animation instance, or null if no animations are available at all.
    */
   public Animation getRandomAnimation() {

      if (!this.randomAnimationPool.isEmpty()) {

         List<Animation> allowedAnimations = this.randomAnimationPool.stream()
                 .map(animations::get)
                 .filter(Objects::nonNull)
                 .collect(Collectors.toList());


         if (!allowedAnimations.isEmpty()) {
            return allowedAnimations.get(this.random.nextInt(allowedAnimations.size()));
         } else {

            logger.warning("All animations listed in RandomAnimationPool were invalid. Falling back to selecting from ALL animations.");
         }
      }

      Object[] allValues = animations.values().toArray();
      if (allValues.length == 0) {
         logger.severe("No animations available to select for 'random'!");
         return null;
      }
      return (Animation) allValues[this.random.nextInt(allValues.length)];
   }

   /**
    * Loads the list of allowed animation names for the 'random' pool from config.yml.
    * It validates that the listed animations actually exist in the loaded 'animations' map.
    */
   private void loadRandomAnimationPool() {
      this.randomAnimationPool.clear();
      List<String> configuredPool = getConfig().getStringList("RandomAnimationPool");


      if (configuredPool == null || configuredPool.isEmpty()) {
         logger.info("RandomAnimationPool is not defined or is empty in config.yml. 'Random' will select from all animations.");
         return;
      }

      int loadedCount = 0;
      for (String name : configuredPool) {
         if (name == null || name.trim().isEmpty()) continue;

         String lowerName = name.toLowerCase().trim(); // Standardize to lowercase

         // Check if this animation name is actually registered
         if (animations.containsKey(lowerName)) {
            this.randomAnimationPool.add(lowerName); // Add valid name to the internal pool
            loadedCount++;
         } else {
            // Log a warning for animation names listed but not found
            logger.warning("Animation '" + name + "' listed in RandomAnimationPool does not exist and will be ignored.");
         }
      }
      logger.info("Loaded " + loadedCount + " valid animations into the RandomAnimationPool.");
   }


   public MobUtils getMobUtils() {
      return this.mobUtils;
   }


}