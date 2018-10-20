package com.volmit.fulcrum.util;

public class BlockTexture implements ResourceLocation
{
	private final String id;

	public BlockTexture(String id)
	{
		this.id = id;
	}

	@Override
	public String getAbsolute()
	{
		return "assets/minecraft/textures/blocks/" + id + ".png";
	}

	@Override
	public String getProject()
	{
		return "/fulcrum/texture/block/" + id + ".png";
	}

	@Override
	public String getId()
	{
		return id;
	}
}
