package com.volmit.fulcrum.custom;

public enum ModelType
{
	CUBE_ALL(""),
	CUBE_MANUAL("up", "down", "north", "east", "west", "south"),
	CUBE_TOP("side", "top"),
	CUBE_BOTTOM_TOP("bottom", "side", "top"),
	CUBE_COLUMN("end", "side"),
	CUBE_CASED("inside", "outside"),
	CUBE_FRAMED("inside", "outside"),
	CAULDRON("inside", "outside"),
	PEDESTAL("top", "bottom", "pillar");

	private String[] requiredTextures;
	private String mc;

	private ModelType(String... requiredTextures)
	{
		this.requiredTextures = requiredTextures;
		this.mc = "";
	}

	public String getMc()
	{
		return mc;
	}

	public void setMc(String mc)
	{
		this.mc = mc;
	}

	public String[] getRequiredTextures()
	{
		return requiredTextures;
	}

	public String getModelContent()
	{
		return mc;
	}
}
