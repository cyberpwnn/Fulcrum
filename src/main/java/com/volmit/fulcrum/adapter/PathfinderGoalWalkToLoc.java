package com.volmit.fulcrum.adapter;

import org.bukkit.Location;

import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.NavigationAbstract;
import net.minecraft.server.v1_12_R1.PathEntity;
import net.minecraft.server.v1_12_R1.PathfinderGoal;

public class PathfinderGoalWalkToLoc extends PathfinderGoal
{
	private double speed;

	private EntityInsentient entity;

	private Location loc;

	private NavigationAbstract navigation;

	public PathfinderGoalWalkToLoc(EntityInsentient entity, Location loc, double speed)
	{
		this.entity = entity;
		this.loc = loc;
		this.navigation = this.entity.getNavigation();
		this.speed = speed;
	}

	@Override
	public void c()
	{
		PathEntity pathEntity = this.navigation.a(loc.getX(), loc.getY(), loc.getZ());

		this.navigation.a(pathEntity, speed);
	}

	@Override
	public boolean a()
	{
		return true;
	}
}