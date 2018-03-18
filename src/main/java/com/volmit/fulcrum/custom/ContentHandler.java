package com.volmit.fulcrum.custom;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.fulcrum.bukkit.Task;
import com.volmit.fulcrum.event.CustomBlockPlaceEvent;
import com.volmit.fulcrum.event.PlayerCancelledDiggingEvent;
import com.volmit.fulcrum.event.PlayerFinishedDiggingEvent;
import com.volmit.fulcrum.event.PlayerStartDiggingEvent;
import com.volmit.fulcrum.lang.GList;
import com.volmit.fulcrum.lang.GMap;
import com.volmit.fulcrum.lang.M;
import com.volmit.fulcrum.sfx.Audible;
import com.volmit.fulcrum.sfx.Audio;

public class ContentHandler implements Listener
{
	public GMap<Block, Double> digging;
	public GMap<Block, Player> lastDug;
	public GMap<Player, Integer> vdel;
	public GList<Block> stopped;

	public ContentHandler()
	{
		digging = new GMap<Block, Double>();
		lastDug = new GMap<Block, Player>();
		stopped = new GList<Block>();
		vdel = new GMap<Player, Integer>();
		Fulcrum.register(this);

		new Task(0)
		{
			@Override
			public void run()
			{
				tick();
			}
		};
	}

	public void tick()
	{
		for(Player i : vdel.k())
		{
			vdel.put(i, vdel.get(i) - 1);

			if(vdel.get(i) <= 0)
			{
				vdel.remove(i);
			}
		}

		for(Block i : digging.k())
		{
			if(vdel.containsKey(lastDug.get(i)))
			{
				continue;
			}

			ContentManager.a().damageBlock(i, digging.get(i));
			double progress = ContentManager.a().getBreakProgress(i);
			double inc = digging.get(i);

			if(progress > 1 + (inc * 20))
			{
				ContentManager.a().brokedBlock(i);
				stopped.add(i);
				continue;
			}

			if(((int) (progress * 9.0)) != ((int) ((progress - inc) * 9.0)))
			{
				ContentManager.a().sendCrack(i, M.clip(progress, 0D, 1D));
			}

			if(progress > 1)
			{
				Fulcrum.callEvent(new BlockBreakEvent(i, lastDug.get(i)));
				stopped.add(i);
			}

			CustomBlock cb = ContentManager.getBlock(i);

			if(cb != null && TICK.tick % 5 == 0 && cb.getDigSound() != null)
			{
				cb.getDigSound().osc(0.35).play(i.getLocation().clone().add(0.5, 0.5, 0.5));
			}
		}

		for(Block i : stopped)
		{
			digging.remove(i);
			lastDug.remove(i);
			ContentManager.a().brokedBlock(i);
			ContentManager.a().sendCrack(i, 100);
		}

		stopped.clear();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerStartDiggingEvent e)
	{
		CustomBlock cb = ContentManager.getBlock(e.getBlock());

		if(cb != null)
		{
			ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
			String type = ToolType.getType(is);
			double speed = ToolLevel.getMiningSpeed(cb, is);

			if(!type.equals(cb.getToolType()))
			{
				speed = ToolLevel.getMiningSpeed(cb, null);
			}

			digging.put(e.getBlock(), speed);
			lastDug.put(e.getBlock(), e.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerFinishedDiggingEvent e)
	{
		CustomBlock cb = ContentManager.getBlock(e.getBlock());

		if(cb != null)
		{
			stopped.add(e.getBlock());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerCancelledDiggingEvent e)
	{
		CustomBlock cb = ContentManager.getBlock(e.getBlock());

		if(cb != null)
		{
			stopped.add(e.getBlock());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(EntityPickupItemEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();

			if(ContentManager.isCustom(e.getItem()))
			{
				ItemStack is = e.getItem().getItemStack().clone();
				e.setCancelled(true);
				Audible picksound = ContentManager.getPickupSound();
				CustomItem ci = ContentManager.getItem(is);
				CustomBlock cb = ContentManager.getBlock(is);

				if(ci != null)
				{
					picksound = ci.getPickupSound();
				}

				if(cb != null)
				{
					picksound = cb.getPickupSound();
				}

				float osc = (float) (0.15 * Math.sin(Math.random()));
				Audio a = new Audio(picksound);
				a.p(a.getPitch() + osc);
				a.play(e.getItem().getLocation());
				ContentManager.addToInventory(p.getInventory(), is);
				ContentManager.a().pickup(p, e.getItem());
				e.getItem().remove();
			}

			else
			{
				ContentManager.getPickupSound().play(e.getItem().getLocation());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(BlockPlaceEvent e)
	{
		if(e.isCancelled())
		{
			return;
		}

		if(ContentManager.a().isMetal(e.getBlock().getType()))
		{
			if(ContentManager.isCustom(e.getBlock()))
			{
				return;
			}

			ContentManager.getMetalBreakSound().play(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(BlockBreakEvent e)
	{
		if(e.isCancelled())
		{
			return;
		}

		CustomBlock cb = ContentManager.getBlock(e.getBlock());

		if(cb != null)
		{
			stopped.add(e.getBlock());
			ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
			double hardness = cb.getHardness();
			String toolType = ToolType.getType(is);
			int level = ToolLevel.getToolLevel(is);
			double speed = ToolLevel.getMiningSpeed(cb, is);
			boolean shouldDrop = !e.getPlayer().getGameMode().equals(GameMode.CREATIVE);
			boolean instantBreak = false;
			boolean toolsMatch = toolType.equals(cb.getToolType());

			if(level < cb.getMinimumToolLevel())
			{
				shouldDrop = false;
			}

			if(cb.getMinimumToolLevel() > ToolLevel.HAND && !toolsMatch)
			{
				shouldDrop = false;
			}

			if((speed / 20D) * 30D > hardness && toolsMatch)
			{
				instantBreak = true;
			}

			ContentManager.breakBlock(e.getBlock(), shouldDrop);
			e.setDropItems(false);
			e.setExpToDrop(0);

			if(!instantBreak)
			{
				vdel.put(e.getPlayer(), 5);
			}

			else
			{
				vdel.put(e.getPlayer(), 1);
			}
		}

		else if(ContentManager.a().isMetal(e.getBlock().getType()))
		{
			ContentManager.getMetalBreakSound().play(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerInteractEvent e)
	{
		if(e.isCancelled())
		{
			return;
		}

		if(e.getItem() == null)
		{
			return;
		}

		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			ItemStack is = e.getItem();
			CustomBlock cb = ContentManager.getBlock(is);

			if(cb != null)
			{
				Block target = null;
				Block clicked = e.getClickedBlock();

				if(clicked.getType().isSolid())
				{
					target = clicked.getRelative(e.getBlockFace());
				}

				else
				{
					target = clicked;
				}

				if(e.getPlayer().getEyeLocation().getBlock().equals(target) || e.getPlayer().getLocation().getBlock().equals(target) || e.getPlayer().getLocation().getBlock().getRelative(BlockFace.UP).equals(target))
				{
					e.setCancelled(true);
					return;
				}

				EquipmentSlot hand = is.equals(e.getPlayer().getInventory().getItemInMainHand()) ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
				BlockState state = target.getState();
				ContentManager.setBlock(target, cb);
				BlockPlaceEvent bp = new BlockPlaceEvent(target, state, clicked, is, e.getPlayer(), true, hand);
				Fulcrum.callEvent(bp);
				CustomBlockPlaceEvent ee = new CustomBlockPlaceEvent(target, cb, e.getPlayer());
				Fulcrum.callEvent(ee);

				if(ee.isCancelled() || bp.isCancelled())
				{
					target.setType(state.getType(), false);
					target.getState().setData(state.getData());
					return;
				}

				e.setCancelled(true);
				ContentManager.placeBlock(target, cb);

				if(!target.equals(clicked))
				{
					Block bb = target.getRelative(e.getBlockFace().getOppositeFace());

					if(ContentManager.isCustom(bb))
					{
						CustomBlock cc = ContentManager.getBlock(bb);

						if(cc != null)
						{
							if(cc.getPlaceSound() != null)
							{
								new Audio(cc.getPlaceSound()).v(0.9f).play(bb.getLocation().clone().add(0.5, 0.5, 0.5));
							}
						}
					}
				}

				if(!e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
				{
					ItemStack result = is.clone();

					if(is.getAmount() <= 1)
					{
						result = new ItemStack(Material.AIR);
					}

					else
					{
						result.setAmount(is.getAmount() - 1);
					}

					if(hand.equals(EquipmentSlot.HAND))
					{
						e.getPlayer().getInventory().setItemInMainHand(result);
					}

					else
					{
						e.getPlayer().getInventory().setItemInOffHand(result);
					}
				}
			}
		}
	}
}
