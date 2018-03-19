package com.volmit.fulcrum.custom.legit;

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
}
