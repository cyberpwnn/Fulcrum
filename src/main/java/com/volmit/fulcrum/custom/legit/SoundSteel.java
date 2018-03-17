package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundSteel extends CustomSound
{
	public SoundSteel()
	{
		super("material.steel");
		setSuggestedVolume(1f);
		setSuggestedPitch(1f);
		setSubtitle("Steel");
		addSound("blocks/steel$", "metal/metalbar_walk$", 1, 11);
	}
}
