package com.volmit.fulcrum.custom.legit;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import com.volmit.fulcrum.custom.BlockRegistryType;
import com.volmit.fulcrum.custom.BlockRenderType;
import com.volmit.fulcrum.custom.CustomBlock;

public class FullBlock extends CustomBlock
{
	public FullBlock()
	{
		super("full_block");
		setBlockRenderType(BlockRenderType.NORMAL);
		setBlockType(BlockRegistryType.BUILDING_BLOCK);
	}

	@Override
	public void onViewTicked(Player player, Block block)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPickedUp(Player player, Item item, boolean cancel)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlaced(Player player, Block block, Block against, BlockFace on, boolean cancel)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onBroke(Player player, Block block, boolean cancel)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartDig(Player player, Block block, boolean cancel)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onCancelDig(Player player, Block block)
	{
		// TODO Auto-generated method stub

	}
}
