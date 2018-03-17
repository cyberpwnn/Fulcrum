package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundMudPickup extends CustomSound
{
	public SoundMudPickup()
	{
		super("material.mud.step");
		setSuggestedVolume(1f);
		setSuggestedPitch(1.25f);
		setSubtitle("Mud");
		addSound("blocks/mud_wander$", "dirt/mud_wander$", 1, 4);
	}
}
