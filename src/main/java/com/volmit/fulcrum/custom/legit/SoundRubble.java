package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundRubble extends CustomSound
{
	public SoundRubble()
	{
		super("material.rubble");
		setSuggestedVolume(1f);
		setSuggestedPitch(1f);
		setSubtitle("Rubble");
		addSound("blocks/rubble$", "gravel/gravel_walk$", 1, 11);
	}
}
