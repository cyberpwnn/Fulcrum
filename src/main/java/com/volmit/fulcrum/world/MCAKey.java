package com.volmit.fulcrum.world;

import org.bukkit.Chunk;
import org.bukkit.World;

public class MCAKey
{
	private String world;
	private int x;
	private int z;

	public MCAKey(String world, int x, int z)
	{
		this.world = world;
		this.x = x;
		this.z = z;
	}

	public MCAKey(Chunk c)
	{
		this(c.getWorld(), c.getX() >> 5, c.getZ() >> 5);
	}

	public boolean contains(Chunk c)
	{
		return contains(c.getWorld().getName(), c.getX(), c.getZ());
	}

	public boolean contains(String world, int cx, int cz)
	{
		return cx >> 5 == x && cz >> 5 == z && world.equals(getWorld());
	}

	public MCAKey(World world, int x, int z)
	{
		this.world = world.getName();
		this.x = x;
		this.z = z;
	}

	public String getWorld()
	{
		return world;
	}

	public void setWorld(String world)
	{
		this.world = world;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getZ()
	{
		return z;
	}

	public void setZ(int z)
	{
		this.z = z;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((world == null) ? 0 : world.hashCode());
		result = prime * result + x;
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
		MCAKey other = (MCAKey) obj;
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
		if(z != other.z)
		{
			return false;
		}
		return true;
	}

}
