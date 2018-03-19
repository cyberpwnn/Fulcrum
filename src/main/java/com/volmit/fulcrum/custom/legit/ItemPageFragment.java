package com.volmit.fulcrum.custom.legit;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

import com.volmit.fulcrum.custom.CustomItem;

public class ItemPageFragment extends CustomItem
{
	public ItemPageFragment()
	{
		super("page_fragment");
		setName("Page Fragment");
		setStackSize(16);
		setPickupSound(new SoundPickupPaper());
	}

	@Override
	public void onPickedUp(Player p, Item item, boolean cancelled)
	{

	}

	@Override
	public void onUsed(Player p, EquipmentSlot hand, Action action, Block block, BlockFace face, boolean cancelled)
	{

	}
}
