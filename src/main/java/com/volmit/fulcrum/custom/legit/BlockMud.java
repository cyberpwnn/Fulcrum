package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.BlockRenderType;
import com.volmit.fulcrum.custom.CustomBlock;
import com.volmit.fulcrum.custom.ToolType;

public class BlockMud extends CustomBlock
{
	public BlockMud()
	{
		super("mud");
		setName("Mud");
		setRenderType(BlockRenderType.ALL);
		setSound(new SoundMud());
		setHardness(0.5);
		setToolType(ToolType.SHOVEL);
	}
}
