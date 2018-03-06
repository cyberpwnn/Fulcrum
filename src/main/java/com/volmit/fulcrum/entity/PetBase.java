package com.volmit.fulcrum.entity;

import net.minecraft.server.v1_12_R1.AxisAlignedBB;
import net.minecraft.server.v1_12_R1.EntityZombie;
import net.minecraft.server.v1_12_R1.World;

public class PetBase extends EntityZombie
{
	public PetBase(World world)
	{
		super(world);
	}

	@Override
	public void recalcPosition()
	{
		AxisAlignedBB vv = getBoundingBox();

		locX = ((vv.a + vv.d) / 2.0D);
		locZ = ((vv.c + vv.f) / 2.0D);
		locY = (vv.b - (isBaby() ? 0.75D : 1.5D));
	}
}
