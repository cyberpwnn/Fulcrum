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
		setBreakSound(new SoundThiccWood());
		setPlaceSound(new SoundThiccWood());
		setStepSound(new SoundThiccWoodStep());
		setDigSound(new SoundThiccWoodStep());
		setPickupSound(new SoundPickupWood());
		setHardness(2);
		setMinimumToolLevel(1);
		setToolType(ToolType.AXE);
	}
}
