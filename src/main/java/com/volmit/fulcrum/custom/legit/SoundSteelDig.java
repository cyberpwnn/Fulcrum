package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundSteelDig extends CustomSound
{
	public SoundSteelDig()
	{
		super("material.steel.dig");
		setSuggestedVolume(1f);
		setSuggestedPitch(1f);
		setSubtitle("Steel");
		addSound("blocks/steel_wander$", "metal/metalbar_wander$", 1, 6);
	}
}
