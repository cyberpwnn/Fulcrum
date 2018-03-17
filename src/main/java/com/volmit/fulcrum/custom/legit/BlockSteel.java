package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.BlockRenderType;
import com.volmit.fulcrum.custom.CustomBlock;
import com.volmit.fulcrum.custom.ToolType;

public class BlockSteel extends CustomBlock
{
	public BlockSteel()
	{
		super("steel");
		setName("Steel");
		setRenderType(BlockRenderType.ALL);
		setBreakSound(new SoundSteel());
		setPlaceSound(new SoundSteel());
		setStepSound(new SoundSteelStep());
		setDigSound(new SoundSteelDig());
		setPickupSound(new SoundSteelStep());
		setHardness(3);
		setMinimumToolLevel(1);
		setToolType(ToolType.PICKAXE);
	}
}
