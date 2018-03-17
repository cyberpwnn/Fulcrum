package com.volmit.fulcrum.custom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.adapter.IAdapter;
import com.volmit.fulcrum.lang.GList;
import com.volmit.fulcrum.sfx.Audible;
import com.volmit.fulcrum.sfx.Audio;

public class ContentManager
{
	public static ContentRegistry r()
	{
		return Fulcrum.contentRegistry;
	}

	public static IAdapter a()
	{
		return Fulcrum.adapter;
	}

	public static boolean isUsed(Material m)
	{
		return r().ass().getNormalAllocations().k().contains(m);
	}

	public static boolean isUsed(Material m, short durability)
	{
		return isUsed(m) && r().ass().getNormalAllocations().get(m).isUsed(durability);
	}

	public static CustomItem getItem(ItemStack item)
	{
		return item.getItemMeta().isUnbreakable() ? getItem(item.getType(), item.getDurability()) : null;
	}

	public static Audible getMetalBreakSound()
	{
		return new Audio().s("m.block.metal.break").setPitch(1.2f);
	}

	public static Audible getMetalFallSound()
	{
		return new Audio().s("m.block.metal.fall").setPitch(1.2f).v(0.5f);
	}

	public static Audible getMetalHitSound()
	{
		return new Audio().s("m.block.metal.hit").setPitch(1.2f).v(0.5f);
	}

	public static Audible getMetalStepSound()
	{
		return new Audio().s("m.block.metal.step").setPitch(1.2f).v(0.5f);
	}

	public static Audible getHoeTillSound()
	{
		return new Audio().s("m.item.hoe.till");
	}

	public static Audible getLeatherArmorEquipSound()
	{
		return new Audio().s("m.item.armor.equip_leather").v(0.7f);
	}

	public static Audible getPickupSound()
	{
		return new Audio().s("m.entity.item.pickup").vp(0.5f, 1.5f);
	}

	public static CustomItem getItem(Material m, short durability)
	{
		if(isUsed(m, durability))
		{
			for(CustomItem i : r().getItems())
			{
				if(i.getDurability() == durability && i.getType().equals(m))
				{
					return i;
				}
			}
		}

		return null;
	}

	public static void addToInventory(Inventory inv, ItemStack is)
	{
		ItemStack[] iss = inv.getContents();
		CustomItem it = getItem(is);
		CustomBlock bt = getBlock(is);
		int left = is.getAmount();
		int z = -1;

		if(it != null)
		{
			z = it.getStackSize();
		}

		if(bt != null)
		{
			z = bt.getStackSize();
		}

		if(it == null && bt == null)
		{
			System.out.println("??? What not custom?");
			inv.addItem(is);
			return;
		}

		for(int i = 0; i < iss.length; i++)
		{
			if(left == 0)
			{
				break;
			}

			if(iss[i] != null)
			{
				ItemStack ic = iss[i].clone();

				if(ic.getType().equals(is.getType()) && ic.getDurability() == is.getDurability() && ic.getItemMeta().isUnbreakable())
				{
					while(ic.getAmount() < z && left > 0)
					{
						ic.setAmount(ic.getAmount() + 1);
						left--;
					}

					iss[i] = ic.clone();
				}
			}
		}

		inv.setContents(iss);

		while(left > 0)
		{
			ItemStack ix = is.clone();
			ix.setAmount(Math.min(left, z));
			left = left - Math.min(left, z);
			inv.addItem(ix);
		}
	}

	public static CustomItem getItem(Item item)
	{
		return getItem(item.getItemStack());
	}

	public static boolean isCustom(Item item)
	{
		if(item == null)
		{
			return false;
		}

		return isCustom(item.getItemStack());
	}

	public static boolean isCustom(ItemStack item)
	{
		if(item == null)
		{
			return false;
		}

		if(!item.getItemMeta().isUnbreakable())
		{
			return false;
		}

		return isUsed(item.getType(), item.getDurability());
	}

	public static boolean isCustom(Block block)
	{
		if(block == null)
		{
			return false;
		}

		return block.getType().equals(Material.MOB_SPAWNER) && getBlock(block) != null;
	}

	public static Inventory createInventory(CustomInventory i)
	{
		if(i == null)
		{
			return null;
		}

		Inventory inv = Bukkit.createInventory(null, 27, " ");
		inv.setItem(0, i.getTop());
		inv.setItem(18, i.getBottom());

		return inv;
	}

	public static Inventory createInventory(String inventory)
	{
		return createInventory(getInventory(inventory));
	}

	public static CustomInventory getInventory(String id)
	{
		for(CustomInventory i : getInventories())
		{
			if(i.getId().equals(id))
			{
				return i;
			}
		}

		return null;
	}

	public static CustomItem getItem(String id)
	{
		for(CustomItem i : getItems())
		{
			if(i.getId().equals(id))
			{
				return i;
			}
		}

		return null;
	}

	public static CustomBlock getBlock(String id)
	{
		for(CustomBlock i : getBlocks())
		{
			if(i.getId().equals(id))
			{
				return i;
			}
		}

		return null;
	}

	public static CustomBlock getBlock(ItemStack is)
	{
		if(!isCustom(is))
		{
			return null;
		}

		for(CustomBlock i : getBlocks())
		{
			ItemStack iv = i.getItem();

			if(iv.getType().equals(is.getType()) && iv.getDurability() == is.getDurability())
			{
				return i;
			}
		}

		return null;
	}

	public static void setBlock(Block block, CustomBlock c)
	{
		a().setSpawnerType(block.getLocation(), c.getSuperID());
	}

	public static void setBlock(Block block, int superId)
	{
		setBlock(block, getBlock(superId));
	}

	public static void stepBlock(Block block)
	{
		playBlockStep(block);
	}

	public static void placeBlock(Block block, CustomBlock c)
	{
		setBlock(block, c);
		playBlockPlace(block, c);
	}

	public static void playBlockStep(Block block)
	{
		if(isCustom(block))
		{
			CustomBlock cb = getBlock(block);

			if(cb != null)
			{
				cb.getStepSound().play(block.getLocation().clone().add(0.5, 0.5, 0.5));
			}
		}
	}

	public static void playBlockPlace(Block block, CustomBlock c)
	{
		if(isCustom(block))
		{
			CustomBlock cb = getBlock(block);

			if(cb != null)
			{
				cb.getPlaceSound().play(block.getLocation().clone().add(0.5, 0.5, 0.5));
			}
		}
	}

	public static void playBlockBreak(Block block)
	{
		if(isCustom(block))
		{
			CustomBlock cb = getBlock(block);

			if(cb != null)
			{
				cb.getBreakSound().play(block.getLocation().clone().add(0.5, 0.5, 0.5));
			}
		}
	}

	public static boolean isRestrictedSlot(int slot)
	{
		return slot == 0 || slot == 18;
	}

	public static void breakBlock(Block block)
	{
		breakBlock(block, false);
	}

	public static void breakBlock(Block block, boolean drop)
	{
		if(isCustom(block))
		{
			CustomBlock cb = getBlock(block);

			if(cb != null)
			{
				playBlockBreak(block);
				block.setType(Material.AIR);

				if(drop)
				{
					ItemStack i = cb.getItem();
					block.getWorld().dropItemNaturally(block.getLocation().clone().add(0.5, 0.5, 0.5), i);
				}
			}
		}
	}

	public static GList<CustomItem> getItems()
	{
		return r().getItems().copy();
	}

	public static GList<CustomBlock> getBlocks()
	{
		return r().getBlocks().copy();
	}

	public static GList<CustomInventory> getInventories()
	{
		return r().getInventories().copy();
	}

	public static CustomBlock getBlock(Block block)
	{
		return getBlock(a().getSpawnerType(block.getLocation()));
	}

	public static CustomBlock getBlock(int superId)
	{
		return r().getSuperBlocks().get(superId);
	}

	public static CustomItem getItem(int superId)
	{
		return r().getSuperItems().get(superId);
	}

	public static CustomInventory getInventory(int superId)
	{
		return r().getSuperInventories().get(superId);
	}

	public static double getCapacityUsage()
	{
		return (double) getUsedCapacity() / (double) getMaxCapacity();
	}

	public static int getMaxCapacity()
	{
		return r().ass().getNormalCapacity();
	}

	public static int getUsedCapacity()
	{
		return r().ass().getNormalUse();
	}
}
