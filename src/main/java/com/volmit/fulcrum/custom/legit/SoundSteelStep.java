package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundSteelStep extends CustomSound
{
	public SoundSteelStep()
	{
		super("material.steel.step");
		setSuggestedVolume(1f);
		setSuggestedPitch(1.2f);
		setSubtitle("Steel");
		addSound("blocks/steel_wander$", "metal/metalbar_wander$", 1, 6);
	}
}
