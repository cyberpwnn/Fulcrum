package com.volmit.fulcrum.registries;

import com.volmit.fulcrum.registry.FRegistered;
import com.volmit.fulcrum.util.BlockModel;
import com.volmit.fulcrum.util.BlockTexture;

public class FBlock extends FRegistered
{
	private BlockTexture texture;
	private BlockModel model;
	private FMaterial material;
	private float hardness;
	private int maxStackSize;
	private String displayName;

	public FBlock(String id)
	{
		super(id);
	}

	public BlockTexture getTexture()
	{
		return texture;
	}

	public void setTexture(BlockTexture texture)
	{
		this.texture = texture;
	}

	public BlockModel getModel()
	{
		return model;
	}

	public void setModel(BlockModel model)
	{
		this.model = model;
	}

	public FMaterial getMaterial()
	{
		return material;
	}

	public void setMaterial(FMaterial material)
	{
		this.material = material;
	}

	public float getHardness()
	{
		return hardness;
	}

	public void setHardness(float hardness)
	{
		this.hardness = hardness;
	}

	public int getMaxStackSize()
	{
		return maxStackSize;
	}

	public void setMaxStackSize(int maxStackSize)
	{
		this.maxStackSize = maxStackSize;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}
}
