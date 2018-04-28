package com.volmit.fulcrum.custom;

import org.bukkit.Material;

import com.volmit.fulcrum.Fulcrum;

public class BlockHardness
{
	public static double getHardness(Material m)
	{
		return Fulcrum.adapter.getHardness(m);
	}

	public static String getEffectiveTool(Material m)
	{
		return Fulcrum.adapter.getEffectiveTool(m);
	}
}
