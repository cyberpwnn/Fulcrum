package com.volmit.fulcrum.entity.pets;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.volmit.fulcrum.bukkit.Items;
import com.volmit.fulcrum.entity.FulcrumPet;
import com.volmit.fulcrum.entity.Pet;
import com.volmit.fulcrum.lang.M;
import com.volmit.fulcrum.sfx.Audio;
import com.volmit.fulcrum.vfx.particle.ParticleVillagerEmote;

public class PetGay extends FulcrumPet
{
	public PetGay(Player owner, Location location, String name) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		super(owner, location, Items.getSkull("http://textures.minecraft.net/texture/2e445eb723c7dff81866c2054a0deb1e0adb2d31f8b8dee1b7a6abb7923eeac"), name);
	}

	@Override
	protected void onAmbient(Pet p)
	{
		new ParticleVillagerEmote().setAngry(true).play(p.getLocation());

		if(M.r(0.25))
		{
			new Audio().s(Sound.ENTITY_LLAMA_AMBIENT).vp(1f, 1.38f).c(SoundCategory.NEUTRAL).play(p.getLocation());
		}

		else
		{
			new Audio().s(Sound.ENTITY_LLAMA_SWAG).vp(1f, 1.49f).c(SoundCategory.NEUTRAL).play(p.getLocation());
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
		new Audio().s(Sound.ENTITY_LLAMA_ANGRY).vp(0.5f, 1.1f).c(SoundCategory.NEUTRAL).play(p.getLocation());
		setTarget(damager);
		setAttackTarget(true);
	}

	@Override
	protected void onDamaged(Pet p, double damage, boolean cancelled)
	{
		new ParticleVillagerEmote().setAngry(true).play(p.getLocation());
		new Audio().s(Sound.ENTITY_LLAMA_ANGRY).vp(0.5f, 1.1f).c(SoundCategory.NEUTRAL).play(p.getLocation());
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
