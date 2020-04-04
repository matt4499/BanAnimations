package me.phantom.bananimations.utils;

import me.phantom.bananimations.BanAnimations;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class ArmorStandBuilder {
   private BanAnimations plugin;
   private Location location;
   private ItemStack helmet;
   private ItemStack chestplate;
   private ItemStack leggings;
   private ItemStack boots;
   private boolean gravity;
   private boolean visible;
   private boolean arms;
   private ItemStack holding;
   private EulerAngle headPose;
   private EulerAngle bodyPose;
   private EulerAngle leftArmPose;
   private EulerAngle rightArmPose;
   private EulerAngle leftLegPose;
   private EulerAngle rightLegPose;
   private boolean small;
   private boolean base;

   public ArmorStandBuilder(BanAnimations plugin, Location location) {
      this.plugin = plugin;
      this.location = location;
      this.gravity = true;
      this.visible = true;
      this.arms = true;
      this.headPose = new EulerAngle(0.0D, 0.0D, 0.0D);
      this.bodyPose = new EulerAngle(0.0D, 0.0D, 0.0D);
      this.leftArmPose = new EulerAngle(0.0D, 0.0D, 0.0D);
      this.rightArmPose = new EulerAngle(0.0D, 0.0D, 0.0D);
      this.leftLegPose = new EulerAngle(0.0D, 0.0D, 0.0D);
      this.rightLegPose = new EulerAngle(0.0D, 0.0D, 0.0D);
      this.small = false;
      this.base = true;
   }

   public ArmorStandBuilder withHelmet(ItemStack helmet) {
      this.helmet = helmet;
      return this;
   }

   public ArmorStandBuilder withChestplate(ItemStack chestplate) {
      this.chestplate = chestplate;
      return this;
   }

   public ArmorStandBuilder withLeggings(ItemStack leggings) {
      this.leggings = leggings;
      return this;
   }

   public ArmorStandBuilder withBoots(ItemStack boots) {
      this.boots = boots;
      return this;
   }

   public ArmorStandBuilder withNoGravity() {
      this.gravity = false;
      return this;
   }

   public ArmorStandBuilder withInvisible() {
      this.visible = false;
      return this;
   }

   public ArmorStandBuilder withNoArms() {
      this.arms = false;
      return this;
   }

   public ArmorStandBuilder withLocation(Location location) {
      this.location = location;
      return this;
   }

   public ArmorStandBuilder withHeadPose(EulerAngle headPose) {
      this.headPose = headPose;
      return this;
   }

   public ArmorStandBuilder withBodyPose(EulerAngle bodyPose) {
      this.bodyPose = bodyPose;
      return this;
   }

   public ArmorStandBuilder withLeftArmPose(EulerAngle leftArmPose) {
      this.leftArmPose = leftArmPose;
      return this;
   }

   public ArmorStandBuilder withRightArmPose(EulerAngle rightArmPose) {
      this.rightArmPose = rightArmPose;
      return this;
   }

   public ArmorStandBuilder withLeftLegPose(EulerAngle leftArmPose) {
      this.leftArmPose = leftArmPose;
      return this;
   }

   public ArmorStandBuilder withRightLegPose(EulerAngle rightLegPose) {
      this.rightLegPose = rightLegPose;
      return this;
   }

   public ArmorStandBuilder withNoBase() {
      this.base = false;
      return this;
   }

   public ArmorStandBuilder holding(ItemStack holding) {
      this.holding = holding;
      return this;
   }

   public ArmorStandBuilder withSmall() {
      this.small = true;
      return this;
   }

   public ArmorStand spawn() {
      ArmorStand stand = (ArmorStand)this.location.getWorld().spawnEntity(new Location(this.location.getWorld(), this.location.getX(), this.location.getY(), this.location.getZ()), EntityType.ARMOR_STAND);
      stand.setGravity(this.gravity);
      stand.setVisible(this.visible);
      this.plugin.getMobUtils().setTags(stand, new String[]{"Silent"});
      stand.setCustomName("ba-stand");
      stand.setCustomNameVisible(false);
      stand.setHelmet(this.helmet);
      stand.setChestplate(this.chestplate);
      stand.setLeggings(this.leggings);
      stand.setBoots(this.boots);
      stand.setArms(this.arms);
      stand.setItemInHand(this.holding);
      stand.setHeadPose(this.headPose);
      stand.setBodyPose(this.bodyPose);
      stand.setLeftArmPose(this.leftArmPose);
      stand.setBasePlate(this.base);
      stand.setRightArmPose(this.rightArmPose);
      stand.setLeftLegPose(this.leftLegPose);
      stand.setRightLegPose(this.rightLegPose);
      stand.setSmall(this.small);
      return stand;
   }
}