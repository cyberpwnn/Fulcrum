package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.MultiCustomSound;

public class SoundMud extends MultiCustomSound
{
	public SoundMud()
	{
		super("material.mud");
		add("h", "dirt/mud_walk$", 1, 6);
		add("s", "dirt/mud_wander$", 1, 4);
		expandToBlock("h", "s");
	}
}
