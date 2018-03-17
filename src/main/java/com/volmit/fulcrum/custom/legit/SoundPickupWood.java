package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundPickupWood extends CustomSound
{
	public SoundPickupWood()
	{
		super("pickup.wood");
		setSuggestedVolume(0.4f);
		setSuggestedPitch(1.6f);
		setSubtitle("Pickup Wood");
		addSound("pickup/wood$", "wood/bluntwood_walk$", 1, 11);
	}
}
