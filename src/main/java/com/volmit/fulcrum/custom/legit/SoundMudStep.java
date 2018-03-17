package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundMudStep extends CustomSound
{
	public SoundMudStep()
	{
		super("material.mud.step");
		setSuggestedVolume(1f);
		setSuggestedPitch(0.65f);
		setSubtitle("Mud");
		addSound("blocks/mud_wander$", "dirt/mud_wander$", 1, 4);
	}
}
