package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.MultiCustomSound;

public class SoundSteel extends MultiCustomSound
{
	public SoundSteel()
	{
		super("material.steel");
		addHard("metal/metalbar_walk$", 1, 11);
		addSoft("metal/metalbar_wander$", 1, 6);
		expandToBlock();
	}
}
