package com.volmit.fulcrum.registries;

import com.volmit.fulcrum.registry.FRegistered;

public class FMaterial extends FRegistered
{
	private FSound stepSound;
	private FSound placeSound;
	private FSound breakSound;
	private FSound digSound;
	private FSound pickupSound;

	public FMaterial(String id)
	{
		super(id);
	}

	public FSound getStepSound()
	{
		return stepSound;
	}

	public void setStepSound(FSound stepSound)
	{
		this.stepSound = stepSound;
	}

	public FSound getPlaceSound()
	{
		return placeSound;
	}

	public void setPlaceSound(FSound placeSound)
	{
		this.placeSound = placeSound;
	}

	public FSound getBreakSound()
	{
		return breakSound;
	}

	public void setBreakSound(FSound breakSound)
	{
		this.breakSound = breakSound;
	}

	public FSound getDigSound()
	{
		return digSound;
	}

	public void setDigSound(FSound digSound)
	{
		this.digSound = digSound;
	}

	public FSound getPickupSound()
	{
		return pickupSound;
	}

	public void setPickupSound(FSound pickupSound)
	{
		this.pickupSound = pickupSound;
	}
}
