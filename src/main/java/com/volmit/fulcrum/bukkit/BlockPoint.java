package com.volmit.fulcrum.bukkit;

import org.bukkit.Location;

public class BlockPoint
{
	private Location location;
	private BlockType type;

	public BlockPoint(Location location, BlockType type)
	{
		this.location = location;
		this.type = type;
	}

	public Location getLocation()
	{
		return location;
	}

	public void setLocation(Location location)
	{
		this.location = location;
	}

	public BlockType getType()
	{
		return type;
	}

	public void setType(BlockType type)
	{
		this.type = type;
	}
}
