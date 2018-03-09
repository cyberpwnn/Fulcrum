package com.volmit.fulcrum.vfx.particle;

import java.awt.Color;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.volmit.fulcrum.bukkit.ParticleEffect;
import com.volmit.fulcrum.bukkit.ParticleEffect.ParticleColor;
import com.volmit.fulcrum.vfx.ColoredEffect;
import com.volmit.fulcrum.vfx.ParticleBase;

public class ParticleRedstone extends ParticleBase implements ColoredEffect
{
	private Color color;

	public ParticleRedstone()
	{
		this.color = Color.WHITE;
	}

	@Override
	public void play(Location l, double range)
	{
		ParticleColor c = new ParticleEffect.OrdinaryColor(getColor().getRed(), getColor().getGreen(), getColor().getBlue());
		ParticleEffect.REDSTONE.display(c, l, range);
	}

	@Override
	public void play(Location l, Player p)
	{
		ParticleColor c = new ParticleEffect.OrdinaryColor(getColor().getRed(), getColor().getGreen(), getColor().getBlue());
		ParticleEffect.REDSTONE.display(c, l, p);
	}

	@Override
	public ParticleRedstone setColor(Color color)
	{
		this.color = color;
		return this;
	}

	@Override
	public Color getColor()
	{
		return color;
	}
}
