package com.volmit.fulcrum.custom.legit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.volmit.fulcrum.custom.CustomAdvancement;
import com.volmit.fulcrum.custom.FrameType;

public class AdvancementAlchemyMakeWater extends CustomAdvancement
{
	public AdvancementAlchemyMakeWater()
	{
		super("alchemy_make_water");
		setTitle("Water!");
		setDescription("Make something very useful, and yet very useless at the same time.");
		setToast(true);
		setHidden(false);
		setParent(new AdvancementAlchemy());
		setFrameType(FrameType.GOAL);
		setIcon(new ItemStack(Material.WATER_BUCKET));
	}
}
