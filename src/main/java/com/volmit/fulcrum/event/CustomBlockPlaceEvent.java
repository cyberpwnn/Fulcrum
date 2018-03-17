package com.volmit.fulcrum.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.volmit.fulcrum.custom.CustomBlock;

public class CustomBlockPlaceEvent extends FulcrumCancellableEvent
{
	private final Block block;
	private final CustomBlock customBlock;
	private final Player player;

	public CustomBlockPlaceEvent(Block block, CustomBlock customBlock, Player player)
	{
		this.block = block;
		this.customBlock = customBlock;
		this.player = player;
	}

	public Block getBlock()
	{
		return block;
	}

	public CustomBlock getCustomBlock()
	{
		return customBlock;
	}

	public Player getPlayer()
	{
		return player;
	}
}
