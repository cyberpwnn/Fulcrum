package com.volmit.fulcrum;

import org.bukkit.event.Listener;

import com.volmit.fulcrum.lang.GMap;
import com.volmit.fulcrum.world.multiblock.Multiblock;

public class MultiblockManager implements Listener
{
	private GMap<String, Class<? extends Multiblock>> handlers;

	public MultiblockManager()
	{
		Fulcrum.register(this);
	}

	public void onTick()
	{

	}
}
