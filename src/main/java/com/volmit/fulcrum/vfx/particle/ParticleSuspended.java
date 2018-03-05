package com.volmit.fulcrum.vfx.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.volmit.fulcrum.bukkit.ParticleEffect;
import com.volmit.fulcrum.vfx.ParticleBase;

public class ParticleSuspended extends ParticleBase
{
	private boolean depth;

	public ParticleSuspended()
	{
		depth = false;
	}

	public ParticleSuspended setDeep(boolean d)
	{
		this.depth = d;
		return this;
	}

	public boolean isDeep()
	{
		return depth;
	}

	@Override
	public void play(Location l, double range)
	{
		(isDeep() ? ParticleEffect.SUSPENDED_DEPTH : ParticleEffect.SUSPENDED).display(0f, 1, l, range);
	}

	@Override
	public void play(Location l, Player p)
	{
		(isDeep() ? ParticleEffect.SUSPENDED_DEPTH : ParticleEffect.SUSPENDED).display(0f, 1, l, p);
	}
}