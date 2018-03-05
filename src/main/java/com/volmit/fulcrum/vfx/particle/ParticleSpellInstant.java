package com.volmit.fulcrum.vfx.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.volmit.fulcrum.bukkit.ParticleEffect;
import com.volmit.fulcrum.vfx.MotionEffect;
import com.volmit.fulcrum.vfx.ParticleBase;

public class ParticleSpellInstant extends ParticleBase implements MotionEffect
{
	private double speed;

	public ParticleSpellInstant()
	{
		this.speed = 0;
	}

	@Override
	public ParticleSpellInstant setSpeed(double s)
	{
		this.speed = s;
		return this;
	}

	@Override
	public double getSpeed()
	{
		return speed;
	}

	@Override
	public void play(Location l, double range)
	{
		ParticleEffect.SPELL_INSTANT.display((float) getSpeed(), 1, l, range);
	}

	@Override
	public void play(Location l, Player p)
	{
		ParticleEffect.SPELL_INSTANT.display((float) getSpeed(), 1, l, p);
	}
}
