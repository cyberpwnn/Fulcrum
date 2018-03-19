package com.volmit.fulcrum.custom.legit;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import com.volmit.fulcrum.custom.BlockRenderType;
import com.volmit.fulcrum.custom.CustomBlock;
import com.volmit.fulcrum.custom.ToolType;

public class BlockRubble extends CustomBlock
{
	public BlockRubble()
	{
		super("rubble");
		setName("Rubble");
		setRenderType(BlockRenderType.ALL);
		setSound(new SoundRubble());
		setHardness(0.7);
		setToolType(ToolType.SHOVEL);
	}

	@Override
	public void onPickedUp(Player player, Item item, boolean cancel)
	{

	}

	@Override
	public void onPlaced(Player player, Block block, Block against, BlockFace on, boolean cancel)
	{

	}

	@Override
	public void onBroke(Player player, Block block, boolean cancel)
	{

	}

	@Override
	public void onStartDig(Player player, Block block, boolean cancel)
	{

	}

	@Override
	public void onCancelDig(Player player, Block block)
	{

	}
}
