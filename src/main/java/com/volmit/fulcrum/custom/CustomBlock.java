package com.volmit.fulcrum.custom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.sfx.Audible;

public class CustomBlock implements ICustomBlock
{
	private Audible breakSound;
	private Audible placeSound;
	private Audible stepSound;
	private Audible digSound;
	private BlockRenderType renderType;
	private String name;
	private final String id;
	private short durabilityLock;

	public CustomBlock(String id)
	{
		this.id = id;
		setName("fulcrum:" + id);
		renderType = BlockRenderType.ALL;
	}

	public CustomBlock(String id, BlockRenderType renderType)
	{
		this(id);
		this.renderType = renderType;
	}

	public void setBreakSound(Audible breakSound)
	{
		this.breakSound = breakSound;
	}

	public void setPlaceSound(Audible placeSound)
	{
		this.placeSound = placeSound;
	}

	public void setStepSound(Audible stepSound)
	{
		this.stepSound = stepSound;
	}

	public void setDigSound(Audible digSound)
	{
		this.digSound = digSound;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public Audible getBreakSound()
	{
		return breakSound;
	}

	@Override
	public Audible getPlaceSound()
	{
		return placeSound;
	}

	@Override
	public Audible getStepSound()
	{
		return stepSound;
	}

	@Override
	public Audible getDigSound()
	{
		return digSound;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public void setDurabilityLock(short d)
	{
		this.durabilityLock = d;
	}

	@Override
	public short getDurabilityLock()
	{
		return durabilityLock;
	}

	@Override
	public ItemStack getItem()
	{
		ItemStack is = new ItemStack(Material.DIAMOND_HOE);
		is.setDurability(getDurabilityLock());
		ItemMeta im = is.getItemMeta();
		im.setUnbreakable(true);
		im.setDisplayName(getName());
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		im.addItemFlags(ItemFlag.HIDE_DESTROYS);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		im.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		is.setItemMeta(im);

		return is;
	}

	@Override
	public void set(Location location)
	{
		Fulcrum.adapter.setSpawnerType(location, "diamond_hoe", getDurabilityLock());
	}

	@Override
	public BlockRenderType getRenderType()
	{
		return renderType;
	}

	@Override
	public void breakParticles(Location l)
	{

	}

	public void setRenderType(BlockRenderType renderType)
	{
		this.renderType = renderType;
	}
}
