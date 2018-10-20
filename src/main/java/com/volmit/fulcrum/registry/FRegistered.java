package com.volmit.fulcrum.registry;

public class FRegistered implements Registered
{
	private final String id;

	public FRegistered(String id)
	{
		this.id = id;
	}

	@Override
	public String getId()
	{
		return id;
	}
}
