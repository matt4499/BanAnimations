package me.phantom.bananimations;

import java.util.*;
import java.util.logging.Logger;
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

   public void onEnable() {
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

   public void onDisable() {
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
      this.frozenPlayers.add(player);
   }

   public void unFreeze(Player player) {
      this.frozenPlayers.remove(player);
   }

   public void registerAnimation(Animation animation, String name) {
      animations.put(name, animation);
      logger.info("Animation " + name + " has been loaded!");
   }

   public boolean isValidAnimation(String animation) {
      if (animation == null) {
         return true;
      } else {
         return !animation.equals("random") && !animations.containsKey(animation);
      }
   }

   public Animation getAnimation(String animationName) {
      return animations.get(animationName);
   }

   public Set<String> getAnimationNames() {
      return animations.keySet();
   }

   public Animation getRandomAnimation() {
      Object[] values = animations.values().toArray();
      return (Animation) values[this.random.nextInt(values.length)];
   }

   public MobUtils getMobUtils() {
      return this.mobUtils;
   }
}
