package com.volmit.fulcrum.entity.pets;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.volmit.dumpster.M;
import com.volmit.fulcrum.bukkit.Items;
import com.volmit.fulcrum.entity.FulcrumPet;
import com.volmit.fulcrum.entity.Pet;
import com.volmit.fulcrum.sfx.Audio;
import com.volmit.fulcrum.vfx.particle.ParticleVillagerEmote;

public class PetTim extends FulcrumPet
{
	public PetTim(Player owner, Location location, String name) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		super(owner, location, Items.getSkull("http://textures.minecraft.net/texture/96ba225e2c37fdfc9f96a1b21bfde42179f599194d1cf89f9f462231591d069"), name);
	}

	@Override
	protected void onAmbient(Pet p)
	{
		new ParticleVillagerEmote().setAngry(true).play(p.getLocation());

		if(M.r(0.25))
		{
			new Audio().s(Sound.ENTITY_EVOCATION_ILLAGER_PREPARE_WOLOLO).vp(1f, 0.78f).c(SoundCategory.NEUTRAL).play(p.getLocation());
		}

		else
		{
			new Audio().s(Sound.ENTITY_ILLUSION_ILLAGER_AMBIENT).vp(1f, 0.89f).c(SoundCategory.NEUTRAL).play(p.getLocation());
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
		new ParticleVillagerEmote().setAngry(true).play(p.getLocation());
		setTarget(who);
	}

	@Override
	protected void onDamagedByEntity(Pet p, Entity damager, double damage, boolean cancelled)
	{
		if(damager instanceof LivingEntity)
		{
			spitAt(((LivingEntity) damager));
		}

		new ParticleVillagerEmote().setAngry(true).play(p.getLocation());
		new Audio().s(Sound.ENTITY_ILLUSION_ILLAGER_AMBIENT).vp(0.5f, 1.1f).c(SoundCategory.NEUTRAL).play(p.getLocation());
		setTarget(damager);
		setAttackTarget(true);
	}

	@Override
	protected void onDamaged(Pet p, double damage, boolean cancelled)
	{
		new ParticleVillagerEmote().setAngry(true).play(p.getLocation());
		new Audio().s(Sound.ENTITY_ILLUSION_ILLAGER_AMBIENT).vp(0.5f, 1.1f).c(SoundCategory.NEUTRAL).play(p.getLocation());
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
