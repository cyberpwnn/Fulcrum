package com.volmit.fulcrum.world.scm;

import org.bukkit.Material;

import com.volmit.fulcrum.bukkit.BlockType;

public interface IVariableBlockType
{
	public boolean isValid(BlockType type);

	public static IVariableBlockType anything()
	{
		return new IVariableBlockType()
		{
			@Override
			public boolean isValid(BlockType type)
			{
				return true;
			}
		};
	}

	public static IVariableBlockType anythingExcept(BlockType... t)
	{
		return new IVariableBlockType()
		{
			@Override
			public boolean isValid(BlockType type)
			{
				for(BlockType i : t)
				{
					if(type.equals(i))
					{
						return false;
					}
				}

				return true;
			}
		};
	}

	public static IVariableBlockType anyVariantOf(BlockType... t)
	{
		return new IVariableBlockType()
		{
			@Override
			public boolean isValid(BlockType type)
			{
				for(BlockType i : t)
				{
					if(type.getMaterial().equals(i.getMaterial()))
					{
						return true;
					}
				}

				return false;
			}
		};
	}

	public static IVariableBlockType anyVariantOf(Material... t)
	{
		return new IVariableBlockType()
		{
			@Override
			public boolean isValid(BlockType type)
			{
				for(Material i : t)
				{
					if(type.getMaterial().equals(i))
					{
						return true;
					}
				}

				return false;
			}
		};
	}

	public static IVariableBlockType of(BlockType... t)
	{
		return new IVariableBlockType()
		{
			@Override
			public boolean isValid(BlockType type)
			{
				for(BlockType i : t)
				{
					if(type.equals(i))
					{
						return true;
					}
				}

				return false;
			}
		};
	}

	public static IVariableBlockType air()
	{
		return new IVariableBlockType()
		{
			@Override
			public boolean isValid(BlockType type)
			{
				return type.getMaterial().equals(Material.AIR);
			}
		};
	}
}
