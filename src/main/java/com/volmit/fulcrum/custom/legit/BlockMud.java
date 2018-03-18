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
		setBreakSound(new SoundMud());
		setPlaceSound(new SoundMud());
		setStepSound(new SoundMudStep());
		setDigSound(new SoundMudStep());
		setPickupSound(new SoundMudPickup());
		setHardness(0.5);
		setToolType(ToolType.SHOVEL);
	}
}
