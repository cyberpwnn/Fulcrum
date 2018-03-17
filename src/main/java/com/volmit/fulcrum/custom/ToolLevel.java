package com.volmit.fulcrum.custom;

import org.bukkit.inventory.ItemStack;

public class ToolLevel
{
	public static final int HAND = 0;
	public static final int WOOD = 1;
	public static final int STONE = 2;
	public static final int IRON = 3;
	public static final int DIAMOND = 5;
	public static final int GOLD = 6;

	public static double getMiningSpeed(double h, int l)
	{
		double m = 1;

		if(l > 0)
		{
			m = l * 2D;
		}

		return (h * 1.5) / m;
	}

	public static int getToolLevel(ItemStack is)
	{
		if(ToolType.getType(is).equals(ToolType.HAND))
		{
			return HAND;
		}

		if(is.getType().toString().startsWith("DIAMOND"))
		{
			return DIAMOND;
		}

		if(is.getType().toString().startsWith("IRON"))
		{
			return IRON;
		}

		if(is.getType().toString().startsWith("GOLD"))
		{
			return GOLD;
		}

		if(is.getType().toString().startsWith("WOOD"))
		{
			return WOOD;
		}

		if(is.getType().toString().startsWith("STONE"))
		{
			return STONE;
		}

		return HAND;
	}
}
