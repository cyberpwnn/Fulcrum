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
import com.volmit.fulcrum.sfx.Audio;
import com.volmit.fulcrum.vfx.particle.ParticleEnchantmentTable;

public class PetWither extends FulcrumPet
{
	public PetWither(Player owner, Location location, String name) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		super(owner, location, Items.getSkull("http://textures.minecraft.net/texture/233b41fa79cd53a230e2db942863843183a70404533bbc01fab744769bcb"), name);
	}

	@Override
	protected void onAmbient(Pet p)
	{
		for(int i = 0; i < 20; i++)
		{
			new ParticleEnchantmentTable().setSpread(6f).play(p.getLocation());
		}

		new Audio().s(Sound.ENTITY_WITHER_AMBIENT).vp(1f, 1.1f).c(SoundCategory.NEUTRAL).play(p.getLocation());
	}

	@Override
	protected void onTick(Pet p)
	{
		new ParticleEnchantmentTable().setSpread(6f).play(p.getLocation());
	}

	@Override
	protected void onTeleported(Pet p)
	{

	}

	@Override
	protected void onInteract(Pet p, Player who)
	{
		for(int i = 0; i < 20; i++)
		{
			new ParticleEnchantmentTable().setSpread(6f).play(p.getLocation());
		}

		new Audio().s(Sound.ENTITY_WITHER_SKELETON_STEP).vp(1f, 1.3f).c(SoundCategory.NEUTRAL).play(p.getLocation());
	}

	@Override
	protected void onDamagedByEntity(Pet p, Entity damager, double damage, boolean cancelled)
	{
		if(damager instanceof LivingEntity)
		{
			spitAt(((LivingEntity) damager));
		}

		new Audio().s(Sound.ENTITY_WITHER_HURT).vp(0.5f, 0.9f).c(SoundCategory.NEUTRAL).play(p.getLocation());
		setTarget(damager);
		setAttackTarget(true);
	}

	@Override
	protected void onDamaged(Pet p, double damage, boolean cancelled)
	{
		new Audio().s(Sound.ENTITY_WITHER_SKELETON_HURT).vp(0.5f, 1.1f).c(SoundCategory.NEUTRAL).play(p.getLocation());
	}

	@Override
	protected void onKilledByPlayer(Pet pet, Player killer)
	{
		new Audio().s(Sound.ENTITY_WITHER_DEATH).vp(0.5f, 1.1f).c(SoundCategory.NEUTRAL).play(pet.getLocation());
	}

	@Override
	protected void onKilled(Pet pet)
	{

	}
}
