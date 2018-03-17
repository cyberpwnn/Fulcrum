package com.volmit.fulcrum.custom;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomInventory
{
	private Material typeTop;
	private Material typeBottom;
	private short durabilityTop;
	private short durabilityBottom;
	private String id;
	private boolean enchantedTop;
	private boolean enchantedBottom;
	private boolean top;
	private boolean bottom;

	public CustomInventory(String id)
	{
		this.id = id;
		enchantedTop = false;
		enchantedBottom = false;
		top = false;
		bottom = false;
	}

	public ItemStack getBottom()
	{
		ItemStack is = new ItemStack(getTypeBottom());
		is.setDurability(getDurabilityBottom());
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(" ");
		im.setUnbreakable(true);

		if(isEnchantedBottom())
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

	public ItemStack getTop()
	{
		ItemStack is = new ItemStack(getTypeTop());
		is.setDurability(getDurabilityTop());
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(" ");
		im.setUnbreakable(true);

		if(isEnchantedTop())
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

	public boolean hasTop()
	{
		return top;
	}

	public void setTop(boolean top)
	{
		this.top = top;
	}

	public boolean hasBottom()
	{
		return bottom;
	}

	public void setBottom(boolean bottom)
	{
		this.bottom = bottom;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public Material getTypeTop()
	{
		return typeTop;
	}

	public void setTypeTop(Material typeTop)
	{
		this.typeTop = typeTop;
	}

	public Material getTypeBottom()
	{
		return typeBottom;
	}

	public void setTypeBottom(Material typeBottom)
	{
		this.typeBottom = typeBottom;
	}

	public short getDurabilityTop()
	{
		return durabilityTop;
	}

	public void setDurabilityTop(short durabilityTop)
	{
		this.durabilityTop = durabilityTop;
	}

	public short getDurabilityBottom()
	{
		return durabilityBottom;
	}

	public void setDurabilityBottom(short durabilityBottom)
	{
		this.durabilityBottom = durabilityBottom;
	}

	public boolean isEnchantedTop()
	{
		return enchantedTop;
	}

	public void setEnchantedTop(boolean enchantedTop)
	{
		this.enchantedTop = enchantedTop;
	}

	public boolean isEnchantedBottom()
	{
		return enchantedBottom;
	}

	public void setEnchantedBottom(boolean enchantedBottom)
	{
		this.enchantedBottom = enchantedBottom;
	}
}
