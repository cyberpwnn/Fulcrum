package com.volmit.fulcrum.entity.pets;

import java.awt.Color;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.volmit.fulcrum.bukkit.Items;
import com.volmit.fulcrum.entity.FulcrumPet;
import com.volmit.fulcrum.entity.Pet;
import com.volmit.fulcrum.sfx.Audio;
import com.volmit.fulcrum.vfx.particle.ParticleHeart;
import com.volmit.fulcrum.vfx.particle.ParticleNote;
import com.volmit.fulcrum.vfx.particle.ParticleSnowShovel;
import com.volmit.fulcrum.vfx.particle.ParticleVillagerEmote;
import com.volmit.volume.math.M;

public class PetMoobark extends FulcrumPet
{
	public PetMoobark(Player owner, Location location, String name) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		super(owner, location, Items.getSkull("http://textures.minecraft.net/texture/154a93cf60e2f7ffb21750628f693d4d125c80c1f78454a562bee20254cac90"), name);
	}

	@Override
	protected void onAmbient(Pet p)
	{
		if(M.r(0.25))
		{
			new ParticleHeart().play(p.getLocation());
			new Audio().s(Sound.ENTITY_WOLF_AMBIENT).vp(1f, 1.8f).c(SoundCategory.NEUTRAL).play(p.getLocation());
		}

		else
		{
			new ParticleSnowShovel().play(p.getLocation().clone().add(p.getLocation().getDirection()));
			new Audio().s(Sound.ENTITY_WOLF_PANT).vp(1f, 1.8f).c(SoundCategory.NEUTRAL).play(p.getLocation());
		}
	}

	@Override
	protected void onTick(Pet p)
	{

	}

	@Override
	protected void onTeleported(Pet p)
	{

	}

	@Override
	protected void onInteract(Pet p, Player who)
	{
		new ParticleNote().setColor(Color.red).play(p.getLocation());
		new Audio().s(Sound.ENTITY_WOLF_HOWL).vp(0.2f, 1.8f).c(SoundCategory.NEUTRAL).play(p.getLocation());
	}

	@Override
	protected void onDamagedByEntity(Pet p, Entity damager, double damage, boolean cancelled)
	{
		if(damager instanceof LivingEntity)
		{
			spitAt(((LivingEntity) damager));
		}

		new ParticleVillagerEmote().setAngry(true).play(p.getLocation());
		new Audio().s(Sound.ENTITY_WOLF_GROWL).vp(0.5f, 1.8f).c(SoundCategory.NEUTRAL).play(p.getLocation());
		setTarget(damager);
		setAttackTarget(true);
	}

	@Override
	protected void onDamaged(Pet p, double damage, boolean cancelled)
	{
		new ParticleVillagerEmote().setAngry(true).play(p.getLocation());
		new Audio().s(Sound.ENTITY_WOLF_GROWL).vp(0.5f, 1.8f).c(SoundCategory.NEUTRAL).play(p.getLocation());
	}

	@Override
	protected void onKilledByPlayer(Pet pet, Player killer)
	{

	}

	@Override
	protected void onKilled(Pet pet)
	{

	}
}
