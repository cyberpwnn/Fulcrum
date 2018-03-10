package com.volmit.fulcrum.bukkit;

import org.bukkit.Bukkit;

import com.volmit.fulcrum.Fulcrum;

public abstract class A implements Runnable
{
	@SuppressWarnings("deprecation")
	public A()
	{
		Bukkit.getScheduler().scheduleAsyncDelayedTask(Fulcrum.instance, this);
	}
}
