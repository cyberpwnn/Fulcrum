package com.volmit.fulcrum.custom.legit;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import com.volmit.fulcrum.custom.BlockRenderType;
import com.volmit.fulcrum.custom.CustomBlock;
import com.volmit.fulcrum.custom.ToolLevel;
import com.volmit.fulcrum.custom.ToolType;

public class BlockSteel extends CustomBlock
{
	public BlockSteel()
	{
		super("steel");
		setName("Steel");
		setRenderType(BlockRenderType.ALL);
		setSound(new SoundSteel());
		setHardness(3);
		setMinimumToolLevel(ToolLevel.IRON);
		setToolType(ToolType.PICKAXE);
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
