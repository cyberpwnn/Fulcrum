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
import com.volmit.fulcrum.vfx.particle.ParticleVillagerEmote;

public class PetFred extends FulcrumPet
{
	public PetFred(Player owner, Location location, String name) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		super(owner, location, Items.getSkull("http://textures.minecraft.net/texture/c9398f7857c5886131334dc0922bdaecc52a20335749baecadec6d8d0135a"), name);
	}

	@Override
	protected void onAmbient(Pet p)
	{
		new ParticleVillagerEmote().setAngry(true).play(p.getLocation());
		new Audio().s(Sound.ENTITY_ENDERDRAGON_AMBIENT).vp(1f, 3f).c(SoundCategory.NEUTRAL).play(p.getLocation());
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
		new Audio().s(Sound.UI_BUTTON_CLICK).vp(1f, 1.3f).c(SoundCategory.NEUTRAL).play(p.getLocation());
	}

	@Override
	protected void onDamagedByEntity(Pet p, Entity damager, double damage, boolean cancelled)
	{
		if(damager instanceof LivingEntity)
		{
			spitAt(((LivingEntity) damager));
		}

		new ParticleVillagerEmote().setAngry(true).play(p.getLocation());
		new Audio().s(Sound.ENTITY_ENDERMEN_STARE).vp(0.5f, 0.9f).c(SoundCategory.NEUTRAL).play(p.getLocation());
		setTarget(damager);
		setAttackTarget(true);
	}

	@Override
	protected void onDamaged(Pet p, double damage, boolean cancelled)
	{
		new ParticleVillagerEmote().setAngry(true).play(p.getLocation());
		new Audio().s(Sound.ENTITY_PLAYER_BURP).vp(0.5f, 1.1f).c(SoundCategory.NEUTRAL).play(p.getLocation());
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
