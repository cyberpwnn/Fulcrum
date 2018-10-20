package com.volmit.fulcrum.util;

public class ItemTexture implements ResourceLocation
{
	private final String id;

	public ItemTexture(String id)
	{
		this.id = id;
	}

	@Override
	public String getAbsolute()
	{
		return "assets/minecraft/textures/items/" + id + ".png";
	}

	@Override
	public String getProject()
	{
		return "/fulcrum/texture/item/" + id + ".png";
	}

	@Override
	public String getId()
	{
		return id;
	}
}
