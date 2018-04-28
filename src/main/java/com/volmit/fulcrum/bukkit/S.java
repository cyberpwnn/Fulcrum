package com.volmit.fulcrum.bukkit;

import org.bukkit.Bukkit;

import com.volmit.fulcrum.Fulcrum;

public abstract class S implements Runnable
{
	public static ParallelPoolManager mgr;

	public S()
	{
		if(Bukkit.isPrimaryThread())
		{
			run();
		}

		else
		{
			Bukkit.getScheduler().scheduleSyncDelayedTask(Fulcrum.instance, this);
		}
	}
}
