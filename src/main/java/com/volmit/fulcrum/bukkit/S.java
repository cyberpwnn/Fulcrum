package com.volmit.fulcrum.bukkit;

import org.bukkit.Bukkit;

import com.volmit.fulcrum.Fulcrum;

public abstract class S implements Runnable
{
	public static ParallelPoolManager mgr;

	public S()
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(Fulcrum.instance, this);
	}
}
