package me.phantom.bananimations.utils;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import me.phantom.bananimations.BanAnimations;

/**
 * Builder pattern implementation for creating and customizing ArmorStands.
 */
public class ArmorStandBuilder {
    private final BanAnimations plugin;
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

    public ArmorStandBuilder holding(ItemStack holding) {
        this.holding = holding;
        return this;
    }

    public ArmorStandBuilder withSmall() {
        this.small = true;
        return this;
    }

    /**
     * Spawns the ArmorStand with the configured properties.
     * @return The spawned ArmorStand entity.
     */
    public ArmorStand spawn() {
        ArmorStand stand = (ArmorStand) Objects.requireNonNull(this.location.getWorld())
                .spawnEntity(this.location, EntityType.ARMOR_STAND);
        stand.setGravity(this.gravity);
        stand.setVisible(this.visible);
        this.plugin.getMobUtils().setTags(stand, "Silent");
        stand.setCustomName("ba-stand");
        stand.setCustomNameVisible(false);
        if (stand.getEquipment() != null) {
            stand.getEquipment().setHelmet(this.helmet);
            stand.getEquipment().setChestplate(this.chestplate);
            stand.getEquipment().setLeggings(this.leggings);
            stand.getEquipment().setBoots(this.boots);
            stand.getEquipment().setItemInMainHand(this.holding);
        }
        stand.setArms(this.arms);
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