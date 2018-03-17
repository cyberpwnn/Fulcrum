package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundRubbleStep extends CustomSound
{
	public SoundRubbleStep()
	{
		super("material.rubble.step");
		setSuggestedVolume(1f);
		setSuggestedPitch(1.2f);
		setSubtitle("Rubble");
		addSound("blocks/rubble_wander$", "gravel/gravel_wander$", 1, 3);
	}
}
