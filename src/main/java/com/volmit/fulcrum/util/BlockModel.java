package com.volmit.fulcrum.util;

public class BlockModel implements ResourceLocation
{
	private final String id;

	public BlockModel(String id)
	{
		this.id = id;
	}

	@Override
	public String getAbsolute()
	{
		return "assets/minecraft/models/block/" + id + ".json";
	}

	@Override
	public String getProject()
	{
		return "/fulcrum/model/block/" + id + ".json";
	}

	@Override
	public String getId()
	{
		return id;
	}
}
