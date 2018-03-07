package com.volmit.fulcrum.world;

import org.bukkit.World;

public class WorldKey
{
	private String world;
	private String category;

	public WorldKey(String world, String category)
	{
		this.world = world;
		this.category = category;
	}

	public WorldKey(World world, String category)
	{
		this(world.getName(), category);
	}

	public String getWorld()
	{
		return world;
	}

	public void setWorld(String world)
	{
		this.world = world;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((world == null) ? 0 : world.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
		{
			return true;
		}

		if(obj == null)
		{
			return false;
		}

		if(getClass() != obj.getClass())
		{
			return false;
		}

		WorldKey other = (WorldKey) obj;

		if(category == null)
		{
			if(other.category != null)
			{
				return false;
			}
		}

		else if(!category.equals(other.category))
		{
			return false;
		}

		if(world == null)
		{
			if(other.world != null)
			{
				return false;
			}
		}

		else if(!world.equals(other.world))
		{
			return false;
		}

		return true;
	}
}
