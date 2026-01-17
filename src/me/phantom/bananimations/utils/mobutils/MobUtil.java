package me.phantom.bananimations.utils.mobutils;

import org.bukkit.entity.Entity;

/**
 * Interface for version-independent mob utility methods.
 */
public interface MobUtil {
    void setDefaultAttributes(Entity entity);

    void setInvulnerable(Entity entity);

    void setTags(Entity entity, String... tags);
}