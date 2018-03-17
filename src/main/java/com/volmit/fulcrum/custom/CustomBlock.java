package com.volmit.fulcrum.custom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.sfx.Audible;
import com.volmit.fulcrum.sfx.Audio;

public class CustomBlock implements ICustom
{
	private Audible breakSound;
	private Audible placeSound;
	private Audible digSound;
	private Audible pickupSound;
	private Audible stepSound;
	private BlockRenderType renderType;
	private String name;
	private final String id;
	private short durabilityLock;
	private Material material;
	private boolean shaded;
	private int sid;
	private String matt;
	private boolean ee;
	private int stackSize;
	private String toolType;
	private int minimumToolLevel;
	private double hardness;

	public CustomBlock(String id)
	{
		hardness = 0.5;
		toolType = ToolType.HAND;
		minimumToolLevel = ToolLevel.HAND;
		stackSize = 64;
		ee = false;
		this.id = id;
		sid = 0;
		setName("fulcrum:" + id);
		shaded = false;
		matt = "";
		renderType = BlockRenderType.ALL;
		pickupSound = ContentManager.getPickupSound();
	}

	public String getToolType()
	{
		return toolType;
	}

	public void setToolType(String toolType)
	{
		this.toolType = toolType;
	}

	public int getMinimumToolLevel()
	{
		return minimumToolLevel;
	}

	public void setMinimumToolLevel(int minimumToolLevel)
	{
		this.minimumToolLevel = minimumToolLevel;
	}

	public double getHardness()
	{
		return hardness;
	}

	public void setHardness(double hardness)
	{
		this.hardness = hardness;
	}

	public Audible getDigSound()
	{
		return digSound;
	}

	public void setDigSound(Audible digSound)
	{
		this.digSound = digSound;
	}

	public void setDigSound(CustomSound s)
	{
		setDigSound(new Audio(s));
	}

	public Audible getPickupSound()
	{
		return pickupSound;
	}

	public void setPickupSound(Audible pickupSound)
	{
		this.pickupSound = pickupSound;
	}

	public void setPickupSound(CustomSound pickupSound)
	{
		setPickupSound(new Audio(pickupSound));
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

	public void setBreakSound(CustomSound breakSound)
	{
		setBreakSound(new Audio(breakSound));
	}

	public void setPlaceSound(CustomSound placeSound)
	{
		setPlaceSound(new Audio(placeSound));
	}

	public void setStepSound(CustomSound stepSound)
	{
		setStepSound(new Audio(stepSound));
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Audible getBreakSound()
	{
		return breakSound;
	}

	public Audible getPlaceSound()
	{
		return placeSound;
	}

	public Audible getStepSound()
	{
		return stepSound;
	}

	public String getName()
	{
		return name;
	}

	public String getId()
	{
		return id;
	}

	public void setDurabilityLock(short d)
	{
		this.durabilityLock = d;
	}

	public short getDurabilityLock()
	{
		return durabilityLock;
	}

	public ItemStack getItem()
	{
		ItemStack is = new ItemStack(getType());
		is.setDurability(getDurabilityLock());
		ItemMeta im = is.getItemMeta();
		im.setUnbreakable(true);
		im.setDisplayName(getName());

		if(ee)
		{
			im.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, true);
		}

		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		im.addItemFlags(ItemFlag.HIDE_DESTROYS);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		im.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		is.setItemMeta(im);

		return is;
	}

	public void set(Location location)
	{
		Fulcrum.adapter.setSpawnerType(location, getMatt(), getDurabilityLock(), ee);
	}

	public BlockRenderType getRenderType()
	{
		return renderType;
	}

	public void setRenderType(BlockRenderType renderType)
	{
		this.renderType = renderType;
	}

	public Material getType()
	{
		return material;
	}

	public void setType(Material type)
	{
		material = type;
	}

	public boolean isShaded()
	{
		return shaded;
	}

	public Material getMaterial()
	{
		return material;
	}

	public void setMaterial(Material material)
	{
		this.material = material;
	}

	public void setShaded(boolean shaded)
	{
		this.shaded = shaded;
	}

	public int getSuperID()
	{
		return sid;
	}

	public void setSuperID(int f)
	{
		sid = f;
	}

	public String getMatt()
	{
		return matt;
	}

	public void setMatt(String matt)
	{
		this.matt = matt;
	}

	public void setEnchanted(boolean boolean1)
	{
		ee = boolean1;
	}

	public boolean isEnchanted()
	{
		return ee;
	}

	public void setStackSize(int size)
	{
		this.stackSize = size;
	}

	public int getStackSize()
	{
		return stackSize;
	}
}
