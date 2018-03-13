package com.volmit.fulcrum.custom;

public enum BlockRenderType
{
	ALL("cube_all", ""),
	MANUAL("cube", "up", "down", "north", "east", "west", "south"),
	TOP("cube_top", "side", "top"),
	TOP_BOTTOM("cube_bottom_top", "bottom", "side", "top"),
	PEDISTAL("cube_pedistal", "top", "bottom", "pillar"),
	COLUMN("cube_column", "end", "side");

	private String[] requiredTextures;
	private String defaulted;
	private String mc;

	private BlockRenderType(String defaulted, String... requiredTextures)
	{
		this.defaulted = defaulted;
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

	public String getDefaulted()
	{
		return "default_" + defaulted;
	}

	public String getFulcrumed()
	{
		return "fulcrum_" + defaulted;
	}

	public String getModelContent()
	{
		return mc;
	}
}
