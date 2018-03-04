package com.volmit.fulcrum.bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public class BlockType
{
	private Material material;
	private Byte data;

	/**
	 * Create a BlockType
	 *
	 * @param material
	 *            the material
	 * @param data
	 *            the data
	 */
	public BlockType(Material material, Byte data)
	{
		this.material = material;
		this.data = data;
	}

	public BlockType(Material material)
	{
		this.material = material;
		data = 0;
	}

	public BlockType(Location location)
	{
		this(location.getBlock());
	}

	@SuppressWarnings("deprecation")
	public BlockType(BlockState state)
	{
		material = state.getType();
		data = state.getData().getData();
	}

	@SuppressWarnings("deprecation")
	public BlockType(Block block)
	{
		material = block.getType();
		data = block.getData();
	}

	public BlockType()
	{
		material = Material.AIR;
		data = 0;
	}

	public Material getMaterial()
	{
		return material;
	}

	public void setMaterial(Material material)
	{
		this.material = material;
	}

	public Byte getData()
	{
		return data;
	}

	public void setData(Byte data)
	{
		this.data = data;
	}

	@Override
	public String toString()
	{
		if(getData() == 0)
		{
			return getMaterial().toString();
		}

		return getMaterial().toString() + ":" + getData();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((material == null) ? 0 : material.hashCode());
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

		BlockType other = (BlockType) obj;

		if(data == null)
		{
			if(other.data != null)
			{
				return false;
			}
		}

		else if(!data.equals(other.data))
		{
			return false;
		}

		if(material != other.material)
		{
			return false;
		}

		return true;
	}
}
