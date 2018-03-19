package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.MultiCustomSound;

public class SoundThiccWood extends MultiCustomSound
{
	public SoundThiccWood()
	{
		super("material.hardwood");
		addHard("wood/wood_walk$", 1, 11);
		addSoft("wood/deckwood_run$", 1, 11);
		expandToBlock();
	}
}
