package com.volmit.fulcrum.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Pet
{
	public void spitAt(LivingEntity l);

	public void teleport(Location location);

	public Location getLocation();

	public void setEquipment(PetEquipment slot, ItemStack is);

	public void setHeldItem(PetHand slot, ItemStack is);

	public ItemStack getEquipment(PetEquipment slot);

	public ItemStack getHeldItem(PetHand slot);

	public void setFollowOwner(boolean follow);

	public boolean isFollowingOwner();

	public boolean isFollowingOwnerSet();

	public void setOwner(Player owner);

	public Player getOwner();

	public void setTarget(Entity e);

	public Entity getTarget();

	public void clearTarget();

	public void setMaxTargetFromOwner(double max);

	public double getMaxTargetFromOwner();

	public void setAttackTarget(boolean attacking);

	public boolean isAttackingTarget();
}
