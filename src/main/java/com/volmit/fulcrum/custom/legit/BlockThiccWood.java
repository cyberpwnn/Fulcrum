package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.BlockRenderType;
import com.volmit.fulcrum.custom.CustomBlock;
import com.volmit.fulcrum.custom.ToolType;

public class BlockThiccWood extends CustomBlock
{
	public BlockThiccWood()
	{
		super("thicc_wood");
		setName("Thicc Wood");
		setRenderType(BlockRenderType.ALL);
		setSound(new SoundThiccWood());
		setHardness(2);
		setToolType(ToolType.AXE);
	}
}
