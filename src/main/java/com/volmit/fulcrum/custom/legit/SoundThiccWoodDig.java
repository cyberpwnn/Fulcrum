package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundThiccWoodDig extends CustomSound
{
	public SoundThiccWoodDig()
	{
		super("material.hardwood.dig");
		setSuggestedVolume(0.25f);
		setSuggestedPitch(1.24f);
		setSubtitle("Hard Dig");
		addSound("dig/wood$", "wood/bluntwood_run$", 1, 11);
	}
}
