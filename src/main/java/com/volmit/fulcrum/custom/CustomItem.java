package com.volmit.fulcrum.custom;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.volmit.fulcrum.sfx.Audible;
import com.volmit.fulcrum.sfx.Audio;

public class CustomItem implements ICustom
{
	private Material type;
	private short durability;
	private String id;
	private String name;
	private Audible pickupSound;
	private boolean enchanted;
	private int layers;
	private int stackSize;
	private int superID;

	public CustomItem(String id)
	{
		layers = 1;
		this.id = id;
		this.name = "";
		this.enchanted = false;
		this.stackSize = 64;
		this.pickupSound = ContentManager.getPickupSound();

		CustomItem ci = ContentManager.getItem(id);

		if(ci != null)
		{
			setType(ci.getType());
			setDurability(ci.getDurability());
			setSuperID(ci.getSuperID());
		}
	}

	public void onPickedUp(Player p, Item item, boolean cancelled)
	{

	}

	public void onUsed(Player p, EquipmentSlot hand, Action action, Block block, BlockFace face, boolean cancelled)
	{

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

	public int getSuperID()
	{
		return superID;
	}

	public void setSuperID(int superID)
	{
		this.superID = superID;
	}

	public int getStackSize()
	{
		return stackSize;
	}

	public void setStackSize(int stackSize)
	{
		this.stackSize = stackSize;
	}

	public int getLayers()
	{
		return layers;
	}

	public void setLayers(int layers)
	{
		this.layers = layers;
	}

	public ItemStack getItem(int count)
	{
		ItemStack is = new ItemStack(getType());
		is.setAmount(count);
		is.setDurability(getDurability());
		ItemMeta im = is.getItemMeta();
		im.setUnbreakable(true);
		im.setDisplayName(getName());

		if(enchanted)
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

	public ItemStack getItem()
	{
		return getItem(1);
	}

	public Material getType()
	{
		return type;
	}

	public void setType(Material type)
	{
		this.type = type;
	}

	public short getDurability()
	{
		return durability;
	}

	public void setDurability(short durability)
	{
		this.durability = durability;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isEnchanted()
	{
		return enchanted;
	}

	public void setEnchanted(boolean enchanted)
	{
		this.enchanted = enchanted;
	}
}
