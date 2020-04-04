package me.phantom.bananimations.utils.mobutils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class MobUtils1_8Less implements MobUtil {
   private Class<?> nmsEntityClass;
   private Class<?> nbtTagCompoundClass;
   private Class<?> craftEntityClass;
   private Method getNBTTag;

   public MobUtils1_8Less() {
      this.loadClasses();
   }

   private void loadClasses() {
      try {
         this.nmsEntityClass = this.getNMSClass("Entity");
         this.nbtTagCompoundClass = this.getNMSClass("NBTTagCompound");
         this.craftEntityClass = this.getCraftEntity();
         this.getNBTTag = this.nmsEntityClass.getMethod("getNBTTag");
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   public Entity setSilent(Entity entity) {
      this.setTags(entity, "Silent");
      return entity;
   }

   public Entity setInvulnerable(Entity entity) {
      this.setTags(entity, "Invulnerable");
      return entity;
   }

   public Entity setDefaultAttributes(Entity entity) {
      this.setTags(entity, "Silent", "Invulnerable");
      return entity;
   }

   public Entity setTags(Entity entity, String... tags) {
      try {
         Class<?> entityClass = this.craftEntityClass.cast(entity).getClass();
         Object nmsEntity = entityClass.getMethod("getHandle").invoke(entity);
         Object nbtTagCompound = this.getNBTTag.invoke(nmsEntity);
         if (nbtTagCompound == null) {
            nbtTagCompound = this.nbtTagCompoundClass.newInstance();
         }

         Class<?> nbtTagCompoundClass = nbtTagCompound.getClass();
         this.nmsEntityClass.getMethod("c", nbtTagCompoundClass).invoke(nmsEntity, nbtTagCompound);
         Method setInt = nbtTagCompoundClass.getMethod("setInt", String.class, Integer.TYPE);
         String[] var8 = tags;
         int var9 = tags.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            String tag = var8[var10];
            setInt.invoke(nbtTagCompound, tag, 1);
         }

         this.nmsEntityClass.getMethod("f", nbtTagCompoundClass).invoke(nmsEntity, nbtTagCompound);
      } catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException var12) {
         var12.printStackTrace();
      }

      return entity;
   }

   private String getVersion() {
      String name = Bukkit.getServer().getClass().getPackage().getName();
      return name.substring(name.lastIndexOf(46) + 1) + ".";
   }

   private Class<?> getCraftEntity() throws ClassNotFoundException {
      String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
      return Class.forName("org.bukkit.craftbukkit." + version + "entity.CraftEntity");
   }

   private Class<?> getNMSClass(String className) {
      String nmsClassLocation = "net.minecraft.server." + this.getVersion() + className;
      Class<?> nms = null;

      try {
         nms = Class.forName(nmsClassLocation);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      return nms;
   }
}