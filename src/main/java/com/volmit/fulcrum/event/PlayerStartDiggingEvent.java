package com.volmit.fulcrum.event;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class PlayerStartDiggingEvent extends PlayerBlockEvent
{
	public PlayerStartDiggingEvent(Player player, Block block, BlockFace f)
	{
		super(player, block, f);
	}
}
