package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.MultiCustomSound;

public class SoundRubble extends MultiCustomSound
{
	public SoundRubble()
	{
		super("material.rubble");
		addHard("gravel/gravel_walk$", 1, 11);
		addSoft("gravel/gravel_wander$", 1, 3);
		expandToBlock();
	}
}
