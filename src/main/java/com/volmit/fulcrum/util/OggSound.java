package com.volmit.fulcrum.util;

public class OggSound implements ResourceLocation
{
	private final String id;

	public OggSound(String id)
	{
		this.id = id;
	}

	@Override
	public String getAbsolute()
	{
		return "assets/minecraft/sounds/" + id + ".ogg";
	}

	@Override
	public String getProject()
	{
		return "/fulcrum/sound/" + id + ".ogg";
	}

	@Override
	public String getId()
	{
		return id;
	}
}
