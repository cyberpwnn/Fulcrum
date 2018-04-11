package com.volmit.fulcrum.custom;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.volmit.dumpster.GList;
import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.adapter.IAdapter;
import com.volmit.fulcrum.bukkit.BlockType;
import com.volmit.fulcrum.bukkit.W;
import com.volmit.fulcrum.sfx.Audible;
import com.volmit.fulcrum.sfx.Audio;

/**
 * The content manager assists with interacting with registered content
 *
 * @author cyberpwn
 */
public class ContentManager
{
	public static boolean reload = false;

	public static CustomBlock getAny(Block b)
	{
		return isCustom(b) ? getBlock(b) : getOverrided(b);
	}

	public static boolean isOverrided(String id)
	{
		for(CustomBlock i : getBlocks())
		{
			if(i.getId().equals(id) && isOverrided(i))
			{
				return true;
			}
		}

		return false;
	}

	public static CustomBlock getOverrided(String b)
	{
		for(CustomBlock i : getBlocks())
		{
			if(i.getId().equals(b) && isOverrided(i))
			{
				return i;
			}
		}

		return null;
	}

	public static boolean isCustom(String id)
	{
		for(CustomBlock i : getBlocks())
		{
			if(i.getId().equals(id) && !isOverrided(i))
			{
				return true;
			}
		}

		return false;
	}

	public static CustomBlock getAny(String id)
	{
		return isCustom(id) ? getBlock(id) : getOverrided(id);
	}

	public static boolean isTool(CustomItem it)
	{
		return it instanceof CustomTool;
	}

	public static boolean isTool(ItemStack is)
	{
		if(is == null)
		{
			return false;
		}

		return isCustom(is) && isTool(getItem(is));
	}

	public static CustomTool getTool(CustomItem it)
	{
		return ((CustomTool) it);
	}

	public static CustomTool getTool(ItemStack is)
	{
		return isTool(is) ? getTool(getItem(is)) : null;
	}

	/**
	 * Request the content manager reload itself and re-call registry events
	 */
	public static void reloadContentManager()
	{
		reload = true;
	}

	/**
	 * Get the content registry
	 *
	 * @return the content registry
	 */
	public static ContentRegistry r()
	{
		return Fulcrum.contentRegistry;
	}

	/**
	 * Grant an advancement
	 *
	 * @param p
	 *            the player
	 * @param a
	 *            the custom advancement
	 */
	public static void grantAdvancement(Player p, CustomAdvancement a)
	{
		a.grant(p);
	}

	/**
	 * Grant an advancement
	 *
	 * @param p
	 *            the player
	 * @param a
	 *            the custom advancement
	 */
	public static void grantAdvancement(Player p, String id)
	{
		grantAdvancement(p, getAdvancement(id));
	}

	/**
	 * Revoke an advancement
	 *
	 * @param p
	 *            the player
	 * @param a
	 *            the custom advancement
	 */
	public static void revokeAdvancement(Player p, CustomAdvancement a)
	{
		a.revoke(p);
	}

	/**
	 * Revoke an advancement
	 *
	 * @param p
	 *            the player
	 * @param a
	 *            the custom advancement
	 */
	public static void revokeAdvancement(Player p, String id)
	{
		revokeAdvancement(p, getAdvancement(id));
	}

	/**
	 * Get a custom advancement by id
	 *
	 * @param id
	 *            the id
	 * @return the advancement or null
	 */
	public static CustomAdvancement getAdvancement(String id)
	{
		for(CustomAdvancement i : r().getAdvancements())
		{
			if(i.getId().equals(id))
			{
				return i;
			}
		}

		return null;
	}

	/**
	 * Get the fulcrum adapter
	 *
	 * @return the adapter
	 */
	public static IAdapter a()
	{
		return Fulcrum.adapter;
	}

	/**
	 * Get all custom block (locations) in the given chunk (EXPENSIVE
	 *
	 * @param c
	 *            the chunk
	 * @return the locations
	 */
	public static GList<Location> getBlocks(Chunk c)
	{
		GList<Location> lx = a().getSpawners(c);

		for(Location i : lx.copy())
		{
			if(!isCustom(i.getBlock()))
			{
				lx.remove(i);
			}
		}

		return lx;
	}

	/**
	 * Get custom blocks within a sphere
	 *
	 * @param l
	 *            the center
	 * @param distance
	 *            the radius
	 * @param c
	 *            the radius of chunks to visit
	 * @return the list of locations
	 */
	public static GList<Location> getBlocks(Location l, double distance, int c)
	{
		GList<Location> lx = new GList<Location>();

		for(Chunk i : W.chunkRadius(l.getChunk(), c))
		{
			for(Location j : a().getSpawners(i))
			{
				if(j.distanceSquared(l) < Math.pow(distance, 2) && isCustom(j.getBlock()))
				{
					lx.add(j);
				}
			}
		}

		return lx;
	}

	/**
	 * Get custom blocks within a sphere
	 *
	 * @param l
	 *            the center
	 * @param distance
	 *            the radius
	 * @return the list of locations
	 */
	public static GList<Location> getBlocks(Location l, double distance)
	{
		return getBlocks(l, distance, 1 + (int) (distance / 16.0));
	}

	/**
	 * Stack an itemstack in a given inventory
	 *
	 * @param ist
	 *            the item
	 * @param inv
	 *            the inventory
	 * @param e
	 *            the entity who wants to merge
	 * @param hintSlot
	 *            the hinted slot to merge at
	 */
	public static void stack(ItemStack ist, Inventory inv, HumanEntity e, int hintSlot)
	{
		int count = 0;
		ItemStack[] isx = inv.getContents();
		ItemStack demo = ist.clone();

		for(int i = 0; i < isx.length; i++)
		{
			ItemStack is = isx[i];

			if(is != null && is.getType().equals(ist.getType()) && is.getDurability() == ist.getDurability() && ist.getItemMeta().isUnbreakable() && is.getItemMeta().isUnbreakable())
			{
				count += is.getAmount();
				inv.setItem(i, new ItemStack(Material.AIR));
			}
		}

		while(count > 0)
		{
			int a = Math.min(count, 64);
			demo.setAmount(a);
			count -= a;
			addToInventory(inv, demo.clone(), hintSlot);
		}

		if(e instanceof Player)
		{
			((Player) e).updateInventory();
		}

	}

	/**
	 * Check if a given material is used by the allocation space
	 *
	 * @param m
	 *            the material
	 * @return true if it is
	 */
	public static boolean isUsed(Material m)
	{
		return r().ass().getNormalAllocations().k().contains(m);
	}

	/**
	 * Check if a given material is used by the allocation space
	 *
	 * @param m
	 *            the material
	 * @param durability
	 *            the durability value
	 * @return true if it is
	 */
	public static boolean isUsed(Material m, short durability)
	{
		return isUsed(m) && r().ass().getNormalAllocations().get(m).isUsed(durability);
	}

	/**
	 * Get the custom item type for the given itemstack
	 *
	 * @param item
	 *            the itemstack
	 * @return the custom item or null
	 */
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
		return new Audio().s("m.block.metal.step").setPitch(1.2f).v(0.3f);
	}

	public static Audible getStoneBreakSound()
	{
		return new Audio().s("m.block.stone.break").setPitch(0.8f);
	}

	public static Audible getStoneFallSound()
	{
		return new Audio().s("m.block.stone.fall").setPitch(0.8f).v(0.5f);
	}

	public static Audible getStoneHitSound()
	{
		return new Audio().s("m.block.stone.hit").setPitch(0.8f).v(0.5f);
	}

	public static Audible getStoneStepSound()
	{
		return new Audio().s("m.block.stone.step").setPitch(0.8f).v(0.3f);
	}

	public static Audible getGlassBreakSound()
	{
		return new Audio().s("m.block.glass.break").setPitch(0.8f);
	}

	public static Audible getGlassPlaceSound()
	{
		return new Audio().s("m.block.glass.place").setPitch(0.8f);
	}

	public static Audible getGlassFallSound()
	{
		return new Audio().s("m.block.glass.fall").setPitch(0.8f).v(0.5f);
	}

	public static Audible getGlassHitSound()
	{
		return new Audio().s("m.block.glass.hit").setPitch(0.8f).v(0.5f);
	}

	public static Audible getGlassStepSound()
	{
		return new Audio().s("m.block.glass.step").setPitch(0.8f).v(0.3f);
	}

	public static Audible getClothBreakSound()
	{
		return new Audio().s("m.block.cloth.break").setPitch(0.8f);
	}

	public static Audible getClothFallSound()
	{
		return new Audio().s("m.block.cloth.fall").setPitch(0.8f).v(0.5f);
	}

	public static Audible getClothHitSound()
	{
		return new Audio().s("m.block.cloth.hit").setPitch(0.8f).v(0.5f);
	}

	public static Audible getClothStepSound()
	{
		return new Audio().s("m.block.cloth.step").setPitch(0.8f).v(0.5f);
	}

	/**
	 * Get the remapped sound
	 *
	 * @return the sound
	 */
	public static Audible getHoeTillSound()
	{
		return new Audio().s("m.item.hoe.till");
	}

	/**
	 * Get the remapped sound
	 *
	 * @return the sound
	 */
	public static Audible getLeatherArmorEquipSound()
	{
		return new Audio().s("m.item.armor.equip_leather").v(0.7f);
	}

	/**
	 * Get the remapped sound
	 *
	 * @return the sound
	 */
	public static Audible getPickupSound()
	{
		return new Audio().s("m.entity.item.pickup").vp(0.5f, 1.5f);
	}

	/**
	 * Get a custom item from material and durability
	 *
	 * @param m
	 *            the material
	 * @param durability
	 *            the durability
	 * @return the item or null
	 */
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

	/**
	 * Add an item to the inventory force stacking if its custom
	 *
	 * @param inv
	 *            the inventory
	 * @param is
	 *            the item
	 */
	public static void addToInventory(Inventory inv, ItemStack is)
	{
		addToInventory(inv, is, 0);
	}

	/**
	 * Add an item to the inventory force stacking if its needed
	 *
	 * @param inv
	 *            the inventory
	 * @param is
	 *            the item
	 * @param hint
	 *            the slot hint
	 */
	public static void addToInventory(Inventory inv, ItemStack is, int hint)
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

			if(inv.getContents()[hint] == null)
			{
				inv.setItem(hint, ix);
			}

			else
			{
				inv.addItem(ix);
			}
		}
	}

	/**
	 * Get a custom item for the drop
	 *
	 * @param item
	 *            the item entity
	 * @return the custom item or null
	 */
	public static CustomItem getItem(Item item)
	{
		return getItem(item.getItemStack());
	}

	/**
	 * Check if the given item entity is custom (block or item)
	 *
	 * @param item
	 *            the item entity
	 * @return true if it is
	 */
	public static boolean isCustom(Item item)
	{
		if(item == null)
		{
			return false;
		}

		return isCustom(item.getItemStack());
	}

	/**
	 * Check if the given itemstack is custom block or item
	 *
	 * @param item
	 *            the item
	 * @return true if it is
	 */
	public static boolean isCustom(ItemStack item)
	{
		if(item == null)
		{
			return false;
		}

		if(item.getItemMeta() == null)
		{
			return false;
		}

		if(!item.getItemMeta().isUnbreakable())
		{
			return false;
		}

		return isUsed(item.getType(), item.getDurability());
	}

	/**
	 * Check if the given block is custom blocks only
	 *
	 * @param block
	 *            the block
	 * @return true if it is
	 */
	public static boolean isCustom(Block block)
	{
		if(block == null)
		{
			return false;
		}

		return block.getType().equals(Material.MOB_SPAWNER) && getBlock(block) != null;
	}

	public static boolean isOverrided(Block block)
	{
		if(block == null)
		{
			return false;
		}

		return r().getOass().getAllocated().containsKey(new BlockType(block));
	}

	public static boolean isOverrided(BlockType block)
	{
		if(block == null)
		{
			return false;
		}

		return r().getOass().getAllocated().containsKey(block);
	}

	@SuppressWarnings("deprecation")
	public static boolean isOverrided(ItemStack block)
	{
		if(block == null)
		{
			return false;
		}

		byte v = block.getData().getData() < 0 ? 0 : block.getData().getData();
		BlockType vv = new BlockType(block.getType(), v);
		return r().getOass().getAllocated().containsKey(vv);
	}

	/**
	 * Create a custom inventory
	 *
	 * @param i
	 *            the inventory type (custom)
	 * @return the inventory object or null
	 */
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

	/**
	 * Create a custom inventory
	 *
	 * @param inventory
	 *            the custom inventory id
	 * @return the custom inventory or null
	 */
	public static Inventory createInventory(String inventory)
	{
		return createInventory(getInventory(inventory));
	}

	/**
	 * Get a custom inventory
	 *
	 * @param id
	 *            the id
	 * @return the custom inventory
	 */
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

	/**
	 * Get a custom item
	 *
	 * @param id
	 *            the id
	 * @return the item or null
	 */
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

	/**
	 * Get a custom block
	 *
	 * @param id
	 *            the id
	 * @return the block or null
	 */
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

	/**
	 * Get a custom block
	 *
	 * @param is
	 *            the itemstack
	 * @return the block or null
	 */
	public static CustomBlock getBlock(ItemStack is)
	{
		if(isOverrided(is))
		{
			@SuppressWarnings("deprecation")
			BlockType bt = new BlockType(is.getType(), is.getData().getData());
			return r().getOass().getAllocated().get(bt);
		}

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

	public static boolean isOverrided(CustomBlock c)
	{
		return c.getBlockType().equals(BlockRegistryType.BUILDING_BLOCK);
	}

	/**
	 * Set the block to a custom block
	 *
	 * @param block
	 *            the block
	 * @param c
	 *            the custom block
	 */
	public static void setBlock(Block block, CustomBlock c)
	{
		setBlock(block, c, true);
	}

	public static void setBlock(Block block, CustomBlock c, boolean ph)
	{
		if(isOverrided(c))
		{
			c.set(block.getLocation(), ph);
		}

		else
		{
			a().setSpawnerType(block.getLocation(), c.getSuperID());
		}
	}

	/**
	 * Set the block to a custom block
	 *
	 * @param block
	 *            the block
	 * @param superId
	 *            the superid
	 */
	public static void setBlock(Block block, int superId)
	{
		setBlock(block, getBlock(superId));
	}

	/**
	 * Play the step sound for the given block (if its custom)
	 *
	 * @param block
	 *            the block
	 */
	public static void stepBlock(Block block)
	{
		playBlockStep(block);
	}

	/**
	 * Place a block setting it and playing sound
	 *
	 * @param block
	 *            the block
	 * @param c
	 *            the custom type
	 */
	public static void placeBlock(Block block, CustomBlock c)
	{
		setBlock(block, c);
		playBlockPlace(block, c);
	}

	/**
	 * Play block step sound
	 *
	 * @param block
	 *            the block to play
	 */
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

	/**
	 * Play block place (just the effect sound)
	 *
	 * @param block
	 *            the block
	 * @param c
	 *            the custom block
	 */
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

	/**
	 * Play a block break effect
	 *
	 * @param block
	 *            the block
	 */
	public static void playBlockBreak(Block block)
	{
		CustomBlock cb = getBlock(block);

		if(cb == null && isOverrided(block))
		{
			cb = getOverrided(block);
		}

		if(cb != null)
		{
			cb.getBreakSound().play(block.getLocation().clone().add(0.5, 0.5, 0.5));
		}
	}

	/**
	 * Check if the given slot is a restricted slot in a custom inventory
	 *
	 * @param slot
	 *            the slot
	 * @return true if the slot is restricted
	 */
	public static boolean isRestrictedSlot(int slot)
	{
		return slot == 0 || slot == 18;
	}

	/**
	 * Break a block.
	 *
	 * @param block
	 *            the block
	 */
	public static void breakBlock(Block block)
	{
		breakBlock(block, false);
	}

	/**
	 * Break a custom block
	 *
	 * @param block
	 *            the block
	 * @param drop
	 *            should the item be dropped
	 */
	public static void breakBlock(Block block, boolean drop)
	{
		CustomBlock cb = getBlock(block);

		if(cb == null && isOverrided(block))
		{
			cb = getOverrided(block);
		}

		if(cb != null)
		{
			playBlockBreak(block);
			block.setType(Material.AIR);

			if(drop)
			{
				ItemStack i = cb.onDrop();
				block.getWorld().dropItemNaturally(block.getLocation().clone().add(0.5, 0.5, 0.5), i);
			}
		}
	}

	/**
	 * Get all registered custom items
	 *
	 * @return the items
	 */
	public static GList<CustomItem> getItems()
	{
		return r().getItems().copy();
	}

	/**
	 * Get all custom blocks
	 *
	 * @return the blocks
	 */
	public static GList<CustomBlock> getBlocks()
	{
		return r().getBlocks().copy();
	}

	/**
	 * Get all custom inventories
	 *
	 * @return the inventories
	 */
	public static GList<CustomInventory> getInventories()
	{
		return r().getInventories().copy();
	}

	public static CustomBlock getOverrided(Block b)
	{
		return r().getOass().getAllocated().get(new BlockType(b));
	}

	@SuppressWarnings("deprecation")
	public static CustomBlock getOverrided(ItemStack block)
	{
		byte v = block.getData().getData() < 0 ? 0 : block.getData().getData();
		BlockType vv = new BlockType(block.getType(), v);
		return r().getOass().getAllocated().get(vv);
	}

	/**
	 * Get the custom block
	 *
	 * @param block
	 *            the custom block
	 * @return the custom block or null
	 */
	public static CustomBlock getBlock(Block block)
	{
		return getBlock(a().getSpawnerType(block.getLocation()));
	}

	/**
	 * Get the custom block
	 *
	 * @param superId
	 *            the superid
	 * @return the block or null
	 */
	public static CustomBlock getBlock(int superId)
	{
		return r().getSuperBlocks().get(superId);
	}

	/**
	 * Get the custom item
	 *
	 * @param superId
	 *            the superid
	 * @return the custom item or null
	 */
	public static CustomItem getItem(int superId)
	{
		return r().getSuperItems().get(superId);
	}

	/**
	 * Get the custom inventory
	 *
	 * @param superId
	 *            the superid
	 * @return the inventory or null
	 */
	public static CustomInventory getInventory(int superId)
	{
		return r().getSuperInventories().get(superId);
	}

	/**
	 * Get the percent usage of item predicates
	 *
	 * @return the capacity
	 */
	public static double getCapacityUsage()
	{
		return (double) getUsedCapacity() / (double) getMaxCapacity();
	}

	/**
	 * Get the maximum capacity
	 *
	 * @return the capacity
	 */
	public static int getMaxCapacity()
	{
		return r().ass().getNormalCapacity();
	}

	/**
	 * Get the used capacity
	 *
	 * @return the used capacity
	 */
	public static int getUsedCapacity()
	{
		return r().ass().getNormalUse();
	}

	/**
	 * Transfer a clicked slot from the inventories
	 *
	 * @param clickedInventory
	 *            the clicked inventory
	 * @param move
	 *            the inventory to move to
	 * @param clickedSlot
	 *            the clicked slot
	 */
	public static void transfer(Inventory clickedInventory, Inventory move, int clickedSlot)
	{
		ItemStack is = clickedInventory.getItem(clickedSlot).clone();
		addToInventory(move, is);
		clickedInventory.setItem(clickedSlot, new ItemStack(Material.AIR));
	}
}
