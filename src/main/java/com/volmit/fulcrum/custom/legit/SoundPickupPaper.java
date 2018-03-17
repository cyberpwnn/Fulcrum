package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundPickupPaper extends CustomSound
{
	public SoundPickupPaper()
	{
		super("pickup.paper");
		setSuggestedVolume(1f);
		setSuggestedPitch(1.4f);
		setSubtitle("Pickup Paper");
		addSound("pickup/paperprint$", "craft/newsprint$", 1, 2);
		addSound("pickup/paperflip$", "craft/pageflip$", 1, 3);
		addSound("pickup/paperflipheavy$", "craft/pageflipheavy$", 1, 3);
	}
}
