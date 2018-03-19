package com.volmit.fulcrum.custom.legit;

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
}
