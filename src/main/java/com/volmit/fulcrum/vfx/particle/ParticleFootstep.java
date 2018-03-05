package com.volmit.fulcrum.vfx.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.volmit.fulcrum.bukkit.ParticleEffect;
import com.volmit.fulcrum.vfx.ParticleBase;

public class ParticleFootstep extends ParticleBase
{
	@Override
	public void play(Location l, double range)
	{
		ParticleEffect.FOOTSTEP.display(0f, 1, l, range);
	}

	@Override
	public void play(Location l, Player p)
	{
		ParticleEffect.FOOTSTEP.display(0f, 1, l, p);
	}
}