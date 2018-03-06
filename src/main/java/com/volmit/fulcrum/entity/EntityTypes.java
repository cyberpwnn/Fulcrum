package com.volmit.fulcrum.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import net.minecraft.server.v1_12_R1.Entity;

public enum EntityTypes
{
	EPET_BASE("EPetBase", 54, PetBase.class);

	private EntityTypes(String name, int id, Class<? extends Entity> custom)
	{
		addToMaps(custom, name, id);
	}

	public static org.bukkit.entity.Entity spawnEntity(Entity entity, Location loc)
	{
		entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		((CraftWorld) loc.getWorld()).getHandle().addEntity(entity);
		return entity.getBukkitEntity();
	}

	public static Object getPrivateField(String fieldName, Class<?> clazz, Object object)
	{
		Field field;
		Object o = null;

		try
		{
			field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			o = field.get(object);
		}

		catch(NoSuchFieldException e)
		{
			e.printStackTrace();
		}

		catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}

		return o;
	}

	private static void addToMaps(Class<? extends Entity> clazz, String name, int id)
	{
		try
		{
			Method m = net.minecraft.server.v1_12_R1.EntityTypes.class.getDeclaredMethod("a", int.class, String.class, Class.class, String.class);
			m.setAccessible(true);
			m.invoke(null, id, name.replaceAll(" ", "_").toLowerCase(), clazz, name);
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
}