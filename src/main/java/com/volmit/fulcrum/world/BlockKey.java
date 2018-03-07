package com.volmit.fulcrum.world;

import org.bukkit.block.Block;

public class BlockKey
{
	private int x;
	private int y;
	private int z;
	private String world;
	private String category;

	public BlockKey(int x, int y, int z, String world, String category)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.category = category;
	}

	public BlockKey(Block block, String category)
	{
		this(block.getX(), block.getY(), block.getZ(), block.getWorld().getName(), category);
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getZ()
	{
		return z;
	}

	public String getWorld()
	{
		return world;
	}

	public String getCategory()
	{
		return category;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((world == null) ? 0 : world.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
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
		BlockKey other = (BlockKey) obj;
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
		if(x != other.x)
		{
			return false;
		}
		if(y != other.y)
		{
			return false;
		}
		if(z != other.z)
		{
			return false;
		}
		return true;
	}
}
