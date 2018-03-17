package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundThiccWood extends CustomSound
{
	public SoundThiccWood()
	{
		super("material.hardwood");
		setSuggestedVolume(1f);
		setSuggestedPitch(1f);
		setSubtitle("Hard Wood");
		addSound("blocks/hardwood$", "wood/wood_walk$", 1, 11);
	}
}
