package com.volmit.fulcrum.custom;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.volmit.fulcrum.sfx.Audible;

public interface ICustomBlock
{
	public Audible getBreakSound();

	public Audible getPlaceSound();

	public Audible getStepSound();

	public Audible getDigSound();

	public String getName();

	public String getId();

	public void setDurabilityLock(short d);

	public short getDurabilityLock();

	public ItemStack getItem();

	public void set(Location location);

	public BlockRenderType getRenderType();
}
