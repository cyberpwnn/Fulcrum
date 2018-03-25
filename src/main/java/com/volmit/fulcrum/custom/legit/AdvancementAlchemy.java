package com.volmit.fulcrum.custom.legit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.volmit.fulcrum.custom.CustomAdvancement;
import com.volmit.fulcrum.custom.FrameType;

public class AdvancementAlchemy extends CustomAdvancement
{
	public AdvancementAlchemy()
	{
		super("alchemy");
		setTitle("Alchemy");
		setDescription("The fine art of chucking, stirring, fermenting, waiting and chugging.");
		setToast(false);
		setHidden(false);
		setFrameType(FrameType.TASK);
		setIcon(new ItemStack(Material.BREWING_STAND));
	}
}
