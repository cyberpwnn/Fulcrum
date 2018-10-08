package com.volmit.fulcrum.entity;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.bukkit.BlockType;
import com.volmit.fulcrum.bukkit.PE;
import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.fulcrum.bukkit.Task;
import com.volmit.fulcrum.bukkit.VectorMath;
import com.volmit.fulcrum.sfx.Audio;
import com.volmit.fulcrum.vfx.particle.ParticleCloud;
import com.volmit.fulcrum.vfx.particle.ParticleTexture;
import com.volmit.volume.math.M;

public abstract class FulcrumPet implements Listener, Pet
{
	private Zombie z;
	private Zombie z2;
	private Zombie z3;
	private Snowball s;
	private Snowball s2;
	private boolean following;
	private boolean follow;
	private Player owner;
	private String name;
	private boolean sprinting;
	private int sprintAmp;
	private double teleportRange;
	private double targetRange;
	private double followRange;
	private double sprintRange;
	private Entity target;
	private boolean ag;

	@SuppressWarnings("deprecation")
	public FulcrumPet(Player owner, Location location, ItemStack skull, String name)
	{
		targetRange = 18;
		target = owner;
		follow = true;
		following = false;
		z = (Zombie) EntityTypes.spawnEntity(new PetBase(((CraftWorld) location.getWorld()).getHandle()), location);
		z2 = (Zombie) EntityTypes.spawnEntity(new PetBase(((CraftWorld) location.getWorld()).getHandle()), location);
		z3 = (Zombie) EntityTypes.spawnEntity(new PetBase(((CraftWorld) location.getWorld()).getHandle()), location);
		z.getEquipment().setHelmet(skull);
		Fulcrum.register(this);
		followRange = 5;
		sprintRange = 12;
		teleportRange = 32;
		s = (Snowball) z.getWorld().spawnEntity(z.getLocation().clone().add(0, 1, 0), EntityType.SNOWBALL);
		s2 = (Snowball) z.getWorld().spawnEntity(z.getLocation().clone().add(0, 1, 0), EntityType.SNOWBALL);
		s2.setCustomName(name);
		s2.setCustomNameVisible(true);
		s.setPassenger(s2);
		z.setPassenger(s);
		this.name = name;
		PE.INVISIBILITY.a(0).d(10000000).c(z);
		PE.INVISIBILITY.a(0).d(10000000).c(z2);
		PE.INVISIBILITY.a(0).d(10000000).c(z3);
		this.owner = owner;
		z.setSilent(true);
		z2.setSilent(true);
		z3.setSilent(true);
		z.setBaby(false);
		z2.setBaby(true);
		z3.setBaby(true);
		z.addPassenger(z2);
		s.addPassenger(z3);
		ag = false;

		new Task(0)
		{
			@Override
			public void run()
			{
				if(z.isDead() || z2.isDead() || z3.isDead())
				{
					Fulcrum.unregister(FulcrumPet.this);
					cancel();
					z3.remove();
					z2.remove();
					z.remove();
					s.remove();
					s2.remove();

					return;
				}

				if(target == null || target.isDead())
				{
					clearTarget();
					return;
				}

				if(!follow)
				{
					z.setVelocity(new Vector(0, -1, 0));
				}

				else
				{
					z2.setAI(true);
					z3.setAI(true);
					z.setAI(true);

					if(isFollowing())
					{
						if(TICK.tick % 12 == 0)
						{
							new Audio().s(Sound.BLOCK_GRASS_STEP).vp(1f, 1.6f).c(SoundCategory.NEUTRAL).play(z.getEyeLocation());
						}

						if(sprinting)
						{
							BlockType t = new BlockType(z.getEyeLocation().getBlock().getRelative(BlockFace.DOWN));

							if(!t.getMaterial().equals(org.bukkit.Material.AIR))
							{
								new ParticleTexture().setType(t).setDirection(VectorMath.reverse(z.getVelocity()).clone().setY(0.25)).play(z.getEyeLocation().clone().add(0, 0.2, 0));
							}

							PE.SPEED.a(2).d(10).apply(z);
						}
					}
				}

				if(follow != following)
				{
					if(follow)
					{
						new NMSPetUtils().setToFollow(z, target.getUniqueId(), 1f);
						new NMSPetUtils().setToFollow(z2, target.getUniqueId(), 1f);
						new NMSPetUtils().setToFollow(z3, target.getUniqueId(), 1f);

						if(!target.getWorld().equals(z.getWorld()))
						{
							clearTarget();
						}

						if(!owner.getWorld().equals(z.getWorld()))
						{
							z.teleport(owner.getLocation());
							onTeleported(FulcrumPet.this);
						}
					}

					else
					{
						new NMSPetUtils().setToFollow(z, target.getUniqueId(), 0f);
						new NMSPetUtils().setToFollow(z2, target.getUniqueId(), 0f);
						new NMSPetUtils().setToFollow(z3, target.getUniqueId(), 0f);
						z2.setAI(false);
						z3.setAI(false);
					}

					following = follow;
				}

				if(target.getLocation().distanceSquared(z.getLocation()) < getMaxTargetFromOwner() * getMaxTargetFromOwner())
				{
					if(isAttackingTarget())
					{
						if(M.r(0.034))
						{
							if(target instanceof LivingEntity)
							{
								spitAt(((LivingEntity) target));
							}
						}
					}
				}

				PE.INVISIBILITY.a(0).d(10000000).c(z);
				s2.setCustomName(getName());
				if(!target.getWorld().equals(z.getWorld()))
				{
					clearTarget();
				}
				if(!owner.getWorld().equals(z.getWorld()))
				{
					z.teleport(owner);
				}

				else if(target.getLocation().distanceSquared(z.getEyeLocation()) < followRange * followRange)
				{
					follow = false;
				}

				else
				{
					follow = true;
				}

				if(target.getLocation().distanceSquared(z.getEyeLocation()) > (sprintRange * sprintRange))
				{
					sprinting = true;
				}

				else
				{
					sprinting = false;
				}

				if(target.getLocation().distanceSquared(z.getEyeLocation()) > (teleportRange * teleportRange))
				{
					if(target.equals(owner))
					{
						Location l = owner.getLocation();
						l.clone().add(VectorMath.reverseXZ(owner.getLocation().getDirection()).clone().multiply(2).clone().setY(1));
						z.teleport(l);
						s.teleport(l);
						s2.teleport(l);
					}

					else
					{
						clearTarget();
					}
				}

				onTick(FulcrumPet.this);

				if(M.r(0.01))
				{
					onAmbient(FulcrumPet.this);
				}
			}
		};
	}

	protected abstract void onAmbient(Pet p);

	protected abstract void onTick(Pet p);

	protected abstract void onTeleported(Pet p);

	protected abstract void onInteract(Pet p, Player who);

	protected abstract void onDamagedByEntity(Pet p, Entity damager, double damage, boolean cancelled);

	protected abstract void onDamaged(Pet p, double damage, boolean cancelled);

	protected abstract void onKilledByPlayer(Pet miniPet, Player killer);

	protected abstract void onKilled(Pet miniPet);

	public void destroy()
	{
		z3.remove();
		z2.remove();
		z.remove();
		s.remove();
		s2.remove();
	}

	@EventHandler
	public void on(EntityDamageEvent e)
	{
		if(e.getEntity().getEntityId() == z.getEntityId() || e.getEntity().getEntityId() == z2.getEntityId() || e.getEntity().getEntityId() == z3.getEntityId())
		{
			double dmg = e.getDamage();
			boolean cancelled = false;
			onDamaged(this, dmg, cancelled);
			e.setDamage(dmg);
			e.setCancelled(cancelled);
		}
	}

	@EventHandler
	public void on(EntityDeathEvent e)
	{
		e.getDrops().clear();

		if(e.getEntity().getEntityId() == z.getEntityId() || e.getEntity().getEntityId() == z2.getEntityId() || e.getEntity().getEntityId() == z3.getEntityId())
		{
			if(e.getEntity().getKiller() != null)
			{
				onKilledByPlayer(this, e.getEntity().getKiller());
			}

			else
			{
				onKilled(this);
			}
		}
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent e)
	{
		if(e.getEntity().getEntityId() == z.getEntityId() || e.getEntity().getEntityId() == z2.getEntityId() || e.getEntity().getEntityId() == z3.getEntityId())
		{
			double dmg = e.getDamage();
			boolean cancelled = false;
			Entity damager = e.getDamager();
			onDamagedByEntity(this, damager, dmg, cancelled);
			e.setDamage(dmg);
			e.setCancelled(cancelled);
		}
	}

	@EventHandler
	public void on(PlayerInteractAtEntityEvent e)
	{
		if(e.getRightClicked().getEntityId() == z.getEntityId() && e.getPlayer().equals(owner))
		{
			onInteract(this, e.getPlayer());
		}

		if(e.getRightClicked().getEntityId() == z2.getEntityId() && e.getPlayer().equals(owner))
		{
			onInteract(this, e.getPlayer());
		}

		if(e.getRightClicked().getEntityId() == z3.getEntityId() && e.getPlayer().equals(owner))
		{
			onInteract(this, e.getPlayer());
		}
	}

	public boolean isFollow()
	{
		return follow;
	}

	public void setFollow(boolean follow)
	{
		this.follow = follow;
	}

	public LivingEntity getEntity()
	{
		return z;
	}

	public boolean isFollowing()
	{
		return following;
	}

	@Override
	public Player getOwner()
	{
		return owner;
	}

	public Snowball getS()
	{
		return s;
	}

	public Snowball getS2()
	{
		return s2;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getSprintAmp()
	{
		return sprintAmp;
	}

	public void setSprintAmp(int sprintAmp)
	{
		this.sprintAmp = sprintAmp;
	}

	public double getTeleportRange()
	{
		return teleportRange;
	}

	public void setTeleportRange(double teleportRange)
	{
		this.teleportRange = teleportRange;
	}

	public double getFollowRange()
	{
		return followRange;
	}

	public void setFollowRange(double followRange)
	{
		this.followRange = followRange;
	}

	public double getSprintRange()
	{
		return sprintRange;
	}

	public void setSprintRange(double sprintRange)
	{
		this.sprintRange = sprintRange;
	}

	public boolean isSprinting()
	{
		return sprinting;
	}

	@Override
	public void setOwner(Player owner)
	{
		this.owner = owner;
	}

	@Override
	public void teleport(Location location)
	{
		getEntity().teleport(location);
	}

	@Override
	public Location getLocation()
	{
		return getEntity().getEyeLocation();
	}

	@Override
	public void setHeldItem(PetHand slot, ItemStack is)
	{
		EntityEquipment ee = null;
		switch(slot)
		{
			case LEFT_HAND_ABOVE:
				ee = z3.getEquipment();
				break;
			case LEFT_HAND_FORWARD:
				ee = z.getEquipment();
				break;
			case LEFT_HAND_HEAD:
				ee = z2.getEquipment();
				break;
			case RIGHT_HAND_ABOVE:
				ee = z3.getEquipment();
				break;
			case RIGHT_HAND_FORWARD:
				ee = z.getEquipment();
				break;
			case RIGHT_HAND_HEAD:
				ee = z2.getEquipment();
				break;
			default:
				break;
		}

		if(ee == null)
		{
			return;
		}

		if(slot.name().contains("LEFT"))
		{
			ee.setItemInOffHand(is);
		}

		if(slot.name().contains("RIGHT"))
		{
			ee.setItemInMainHand(is);
		}
	}

	@Override
	public ItemStack getHeldItem(PetHand slot)
	{
		EntityEquipment ee = null;
		switch(slot)
		{
			case LEFT_HAND_ABOVE:
				ee = z3.getEquipment();
				break;
			case LEFT_HAND_FORWARD:
				ee = z.getEquipment();
				break;
			case LEFT_HAND_HEAD:
				ee = z2.getEquipment();
				break;
			case RIGHT_HAND_ABOVE:
				ee = z3.getEquipment();
				break;
			case RIGHT_HAND_FORWARD:
				ee = z.getEquipment();
				break;
			case RIGHT_HAND_HEAD:
				ee = z2.getEquipment();
				break;
			default:
				break;
		}

		if(ee == null)
		{
			return null;
		}

		if(slot.name().contains("LEFT"))
		{
			return ee.getItemInOffHand();
		}

		if(slot.name().contains("RIGHT"))
		{
			return ee.getItemInMainHand();
		}

		return null;
	}

	@Override
	public ItemStack getEquipment(PetEquipment slot)
	{
		EntityEquipment ee = null;
		switch(slot)
		{
			case BOOTS_ABOVE:
				ee = z3.getEquipment();
				break;
			case BOOTS_BELOW:
				ee = z3.getEquipment();
				break;
			case BOOTS_HEAD:
				ee = z3.getEquipment();
				break;
			case CHEST_ABOVE:
				ee = z3.getEquipment();
				break;
			case CHEST_BELOW:
				ee = z3.getEquipment();
				break;
			case CHEST_HEAD:
				ee = z3.getEquipment();
				break;
			case HELMET_ABOVE:
				ee = z3.getEquipment();
				break;
			case HELMET_HEAD:
				ee = z3.getEquipment();
				break;
			case LEGGINGS_ABOVE:
				ee = z3.getEquipment();
				break;
			case LEGGINGS_BELOW:
				ee = z3.getEquipment();
				break;
			case LEGGINGS_HEAD:
				ee = z3.getEquipment();
				break;
			case SKULL_BASE:
				ee = z3.getEquipment();
				break;
			default:
				break;
		}
		if(ee == null)
		{
			return null;
		}

		if(slot.name().contains("HELMET") || slot.name().contains("SKULL"))
		{
			return ee.getHelmet();
		}

		if(slot.name().contains("CHEST"))
		{
			return ee.getChestplate();
		}

		if(slot.name().contains("LEGGINGS"))
		{
			return ee.getLeggings();
		}

		if(slot.name().contains("BOOTS"))
		{
			return ee.getBoots();
		}

		return null;
	}

	@Override
	public void setEquipment(PetEquipment slot, ItemStack is)
	{
		EntityEquipment ee = null;
		switch(slot)
		{
			case BOOTS_ABOVE:
				ee = z3.getEquipment();
				break;
			case BOOTS_BELOW:
				ee = z3.getEquipment();
				break;
			case BOOTS_HEAD:
				ee = z3.getEquipment();
				break;
			case CHEST_ABOVE:
				ee = z3.getEquipment();
				break;
			case CHEST_BELOW:
				ee = z3.getEquipment();
				break;
			case CHEST_HEAD:
				ee = z3.getEquipment();
				break;
			case HELMET_ABOVE:
				ee = z3.getEquipment();
				break;
			case HELMET_HEAD:
				ee = z3.getEquipment();
				break;
			case LEGGINGS_ABOVE:
				ee = z3.getEquipment();
				break;
			case LEGGINGS_BELOW:
				ee = z3.getEquipment();
				break;
			case LEGGINGS_HEAD:
				ee = z3.getEquipment();
				break;
			case SKULL_BASE:
				ee = z3.getEquipment();
				break;
			default:
				break;
		}
		if(ee == null)
		{
			return;
		}

		if(slot.name().contains("HELMET") || slot.name().contains("SKULL"))
		{
			ee.setHelmet(is);
		}

		if(slot.name().contains("CHEST"))
		{
			ee.setChestplate(is);
		}

		if(slot.name().contains("LEGGINGS"))
		{
			ee.setLeggings(is);
		}

		if(slot.name().contains("BOOTS"))
		{
			ee.setBoots(is);
		}
	}

	@Override
	public void setFollowOwner(boolean follow)
	{
		setFollow(follow);
	}

	@Override
	public boolean isFollowingOwner()
	{
		return isFollowing();
	}

	@Override
	public boolean isFollowingOwnerSet()
	{
		return isFollow();
	}

	@Override
	public void spitAt(LivingEntity l)
	{
		Location s = getLocation().clone().add(getLocation().getDirection().clone().multiply(0.8).clone().add(new Vector(0, 0.2, 0)));
		Vector v = VectorMath.direction(s, l.getEyeLocation());
		s = s.setDirection(v);
		LlamaSpit spit = (LlamaSpit) s.getWorld().spawnEntity(s, EntityType.LLAMA_SPIT);
		spit.setGravity(true);
		spit.setVelocity(v.multiply(1));
		new Audio().s(Sound.ENTITY_LLAMA_SPIT).vp(1f, 1.35f).play(s);
		for(int i = 0; i < 12; i++)
		{
			new ParticleCloud().setDirection(v).setSpeed(0.5 * Math.random()).play(s);
		}

		new Task(0)
		{
			@Override
			public void run()
			{
				if(spit == null || spit.isDead() || spit.isOnGround())
				{
					cancel();
					return;
				}

				if(spit.getLocation().distanceSquared(l.getEyeLocation()) < 2)
				{
					l.damage(1.5, spit);
					cancel();
				}
			}
		};
	}

	@Override
	public void setTarget(Entity e)
	{
		target = e;
	}

	@Override
	public Entity getTarget()
	{
		return target == null ? owner : target;
	}

	@Override
	public void clearTarget()
	{
		target = owner;
		ag = false;
	}

	@Override
	public void setMaxTargetFromOwner(double max)
	{
		targetRange = max;
	}

	@Override
	public double getMaxTargetFromOwner()
	{
		return targetRange;
	}

	@Override
	public void setAttackTarget(boolean attacking)
	{
		ag = attacking;
	}

	@Override
	public boolean isAttackingTarget()
	{
		return ag;
	}
}
