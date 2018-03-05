package com.volmit.fulcrum.vfx.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.volmit.fulcrum.bukkit.ParticleEffect;
import com.volmit.fulcrum.vfx.ParticleBase;

public class ParticleDrip extends ParticleBase
{
	private boolean lava;

	public ParticleDrip()
	{
		lava = false;
	}

	public ParticleDrip setLava(boolean d)
	{
		this.lava = d;
		return this;
	}

	public boolean isLava()
	{
		return lava;
	}

	@Override
	public void play(Location l, double range)
	{
		(isLava() ? ParticleEffect.DRIP_LAVA : ParticleEffect.DRIP_WATER).display(0f, 1, l, range);
	}

	@Override
	public void play(Location l, Player p)
	{
		(isLava() ? ParticleEffect.DRIP_LAVA : ParticleEffect.DRIP_WATER).display(0f, 1, l, p);
	}
}