package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundMud extends CustomSound
{
	public SoundMud()
	{
		super("material.mud");
		setSuggestedVolume(1f);
		setSuggestedPitch(1f);
		setSubtitle("Mud");
		addSound("blocks/mud$", "dirt/mud_walk$", 1, 6);
	}
}
