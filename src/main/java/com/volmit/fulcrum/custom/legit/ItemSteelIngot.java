package com.volmit.fulcrum.custom.legit;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

import com.volmit.fulcrum.custom.CustomItem;

public class ItemSteelIngot extends CustomItem
{
	public ItemSteelIngot()
	{
		super("steel_ingot");
		setName("Steel Ingot");
		setPickupSound(new SoundSteel().get("pickup"));
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
