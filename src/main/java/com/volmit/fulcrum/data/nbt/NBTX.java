package com.volmit.fulcrum.data.nbt;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class NBTX
{
	public NBTTagCompound read(Entity e)
	{
		net.minecraft.server.v1_12_R1.Entity entity = ((CraftEntity) e).getHandle();
		NBTTagCompound compound = new NBTTagCompound();
		entity.c(compound);
		return compound;
	}

	public void write(Entity e, NBTTagCompound compound)
	{
		((CraftEntity) e).getHandle().f(compound);
	}
}
