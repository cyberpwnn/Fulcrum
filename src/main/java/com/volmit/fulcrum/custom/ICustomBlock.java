package com.volmit.fulcrum.custom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.volmit.fulcrum.sfx.Audible;

public interface ICustomBlock
{
	public Audible getBreakSound();

	public Audible getPlaceSound();

	public Audible getStepSound();

	public String getName();

	public String getId();

	public void setDurabilityLock(short d);

	public short getDurabilityLock();

	public Material getType();

	public void setType(Material type);

	public ItemStack getItem();

	public void set(Location location);

	public BlockRenderType getRenderType();

	public boolean isShaded();

	public int getSuperID();

	public void setSuperID(int f);

	public String getMatt();

	public void setMatt(String matt);

	public void setEnchanted(boolean boolean1);

	public boolean isEnchanted();
}
