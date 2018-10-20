package com.volmit.fulcrum.util;

public class ItemModel implements ResourceLocation
{
	private final String id;

	public ItemModel(String id)
	{
		this.id = id;
	}

	@Override
	public String getAbsolute()
	{
		return "assets/minecraft/models/item/" + id + ".json";
	}

	@Override
	public String getProject()
	{
		return "/fulcrum/model/item/" + id + ".json";
	}

	@Override
	public String getId()
	{
		return id;
	}
}
