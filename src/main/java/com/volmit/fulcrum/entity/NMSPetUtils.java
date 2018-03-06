package com.volmit.fulcrum.entity;

import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

import com.google.common.collect.Sets;

import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;

public class NMSPetUtils
{
	private static Field gsa;
	private static Field goalSelector;
	private static Field targetSelector;

	static
	{
		try
		{
			gsa = PathfinderGoalSelector.class.getDeclaredField("b");
			gsa.setAccessible(true);
			goalSelector = EntityInsentient.class.getDeclaredField("goalSelector");
			goalSelector.setAccessible(true);
			targetSelector = EntityInsentient.class.getDeclaredField("targetSelector");
			targetSelector.setAccessible(true);
		}
		catch(Exception localException)
		{
			localException.printStackTrace();
		}
	}

	public NMSPetUtils()
	{
	}

	@SuppressWarnings("deprecation")
	public void setToFollow(LivingEntity paramLivingEntity, UUID paramUUID, double paramDouble)
	{
		try
		{
			EntityLiving localEntityLiving = ((CraftLivingEntity) paramLivingEntity).getHandle();

			if((localEntityLiving instanceof EntityInsentient))
			{
				PathfinderGoalSelector localPathfinderGoalSelector1 = (PathfinderGoalSelector) goalSelector.get(localEntityLiving);
				PathfinderGoalSelector localPathfinderGoalSelector2 = (PathfinderGoalSelector) targetSelector.get(localEntityLiving);
				gsa.set(localPathfinderGoalSelector1, Sets.newLinkedHashSet());
				gsa.set(localPathfinderGoalSelector2, Sets.newLinkedHashSet());
				localPathfinderGoalSelector1.a(0, new PathfinderGoalFloat((EntityInsentient) localEntityLiving));
				localPathfinderGoalSelector1.a(1, new PetGoalWalkToOwner((EntityInsentient) localEntityLiving, paramUUID, paramDouble));
			}

			else
			{
				throw new IllegalArgumentException(paramLivingEntity.getType().getName() + " is not an instance of an EntityInsentient.");
			}
		}

		catch(Exception localException)
		{
			localException.printStackTrace();
		}
	}
}
