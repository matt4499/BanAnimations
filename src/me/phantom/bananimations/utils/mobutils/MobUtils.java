package me.phantom.bananimations.utils.mobutils;

import org.bukkit.entity.Entity;

import me.phantom.bananimations.BanAnimations;

/**
 * Access point for mob utilities, delegating to version-specific implementations.
 */
public class MobUtils {
    private final MobUtil mobUtil;

    public MobUtils(BanAnimations plugin) {
        // In a real multi-version plugin, this would verify version and instantiate appropriate class.
        // Currently hardcoded to MobUtilsNewer.
        this.mobUtil = new MobUtilsNewer();
    }

    public Entity setDefaultTags(Entity entity) {
        this.mobUtil.setDefaultAttributes(entity);
        return entity;
    }

    public Entity setInvulnerable(Entity entity) {
        this.mobUtil.setInvulnerable(entity);
        return entity;
    }

    public Entity setTags(Entity entity, String... tags) {
        this.mobUtil.setTags(entity, tags);
        return entity;
    }
}