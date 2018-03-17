package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundRubbleDig extends CustomSound
{
	public SoundRubbleDig()
	{
		super("material.rubble.dig");
		setSuggestedVolume(1f);
		setSuggestedPitch(0.4f);
		setSubtitle("Rubble");
		addSound("blocks/rubble_wander$", "gravel/gravel_wander$", 1, 3);
	}
}
