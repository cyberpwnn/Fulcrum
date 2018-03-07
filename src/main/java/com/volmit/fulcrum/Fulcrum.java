package com.volmit.fulcrum;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.volmit.fulcrum.adapter.Adapter12;
import com.volmit.fulcrum.adapter.IAdapter;
import com.volmit.fulcrum.bukkit.Items;
import com.volmit.fulcrum.bukkit.P;
import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.fulcrum.bukkit.Task;
import com.volmit.fulcrum.entity.Pet;
import com.volmit.fulcrum.entity.PetHand;
import com.volmit.fulcrum.entity.pets.PetDan;
import com.volmit.fulcrum.entity.pets.PetGay;
import com.volmit.fulcrum.entity.pets.PetMoobark;
import com.volmit.fulcrum.entity.pets.PetTim;
import com.volmit.fulcrum.entity.pets.PetWither;
import com.volmit.fulcrum.fx.EffectCauldronAcceptItem;
import com.volmit.fulcrum.fx.EffectCauldronAcceptRecipe;
import com.volmit.fulcrum.fx.EffectCauldronBubble;
import com.volmit.fulcrum.fx.EffectCauldronRejectRecipe;
import com.volmit.fulcrum.lang.F;
import com.volmit.fulcrum.lang.Profiler;
import com.volmit.fulcrum.world.FastBlock;
import com.volmit.fulcrum.world.FastBlock12;
import com.volmit.fulcrum.world.FastChunk;
import com.volmit.fulcrum.world.FastChunk12;
import com.volmit.fulcrum.world.FastWorld;
import com.volmit.fulcrum.world.FastWorld12;

public class Fulcrum extends JavaPlugin implements CommandExecutor
{
	public static Fulcrum instance;
	public static IAdapter adapter;

	@Override
	public void onLoad()
	{
		instance = this;
	}

	public static FastBlock faster(Block b)
	{
		return new FastBlock12(b);
	}

	public static FastChunk faster(Chunk c)
	{
		return new FastChunk12(c);
	}

	public static FastWorld faster(String world)
	{
		return new FastWorld12(Bukkit.getWorld(world));
	}

	public static FastWorld faster(World world)
	{
		return new FastWorld12(world);
	}

	@Override
	public void onEnable()
	{
		getCommand("fulcrum").setExecutor(this);

		new Task(0)
		{
			@Override
			public void run()
			{
				onTick();
			}
		};

		adapter = new Adapter12();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command.getName().equalsIgnoreCase("fulcrum"))
		{
			Player p = (Player) sender;
			Location ll = P.targetBlock(p, 332);
			Profiler px = new Profiler();
			px.begin();
			if(args[0].equalsIgnoreCase("fast"))
			{

				faster(ll.getWorld()).createExplosion(ll, 40f, false);
				sender.sendMessage("Using FastWorld Adapter");
			}

			if(args[0].equalsIgnoreCase("slow"))
			{
				ll.getWorld().createExplosion(ll, 40f, false);
				sender.sendMessage("Using Bukkit");
			}

			if(args[0].equalsIgnoreCase("fx"))
			{
				if(args.length == 2)
				{
					if(args[1].equalsIgnoreCase("bubble"))
					{
						System.out.println("bub");
						new Task(0)
						{
							@Override
							public void run()
							{
								new EffectCauldronBubble().play(ll);
							}
						};
					}

					if(args[1].equalsIgnoreCase("accept"))
					{
						new EffectCauldronAcceptItem().play(ll);
					}

					if(args[1].equalsIgnoreCase("bad"))
					{
						new EffectCauldronRejectRecipe().play(ll);
					}

					if(args[1].equalsIgnoreCase("good"))
					{
						new EffectCauldronAcceptRecipe().play(ll);
					}
				}
			}

			if(args[0].equalsIgnoreCase("petdan"))
			{
				try
				{
					Pet pet = new PetDan(p, ll.clone().add(0, 1, 0), "Dan");
				}

				catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}

			if(args[0].equalsIgnoreCase("petgay"))
			{
				try
				{
					Pet pet = new PetGay(p, ll.clone().add(0, 1, 0), "Gay");
				}

				catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}

			if(args[0].equalsIgnoreCase("petmoo"))
			{
				try
				{
					Pet pet = new PetMoobark(p, ll.clone().add(0, 1, 0), "Moobark");
					pet.setHeldItem(PetHand.LEFT_HAND_ABOVE, new ItemStack(Material.DIAMOND_SWORD));
					pet.setHeldItem(PetHand.LEFT_HAND_HEAD, new ItemStack(Material.MAGMA_CREAM));
					pet.setHeldItem(PetHand.LEFT_HAND_FORWARD, new ItemStack(Material.GOLD_PICKAXE));
					pet.setHeldItem(PetHand.RIGHT_HAND_ABOVE, new ItemStack(Material.IRON_AXE));
					pet.setHeldItem(PetHand.RIGHT_HAND_HEAD, new ItemStack(Material.COOKED_BEEF));
					pet.setHeldItem(PetHand.RIGHT_HAND_FORWARD, new ItemStack(Material.STONE_HOE));
				}

				catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}

			if(args[0].equalsIgnoreCase("pettim"))
			{
				try
				{
					Pet pet = new PetTim(p, ll.clone().add(0, 1, 0), "Tim");
				}

				catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}

			if(args[0].equalsIgnoreCase("petwither"))
			{
				try
				{
					Pet pet = new PetWither(p, ll.clone().add(0, 1, 0), "Wither");
				}

				catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}

			if(args[0].equalsIgnoreCase("skull"))
			{
				try
				{
					ItemStack is = Items.getSkull("http://textures.minecraft.net/texture/154a93cf60e2f7ffb21750628f693d4d125c80c1f78454a562bee20254cac90");
					p.getInventory().addItem(is);
				}

				catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}

			px.end();
			sender.sendMessage("Took: " + F.time(px.getMilliseconds(), 5));

			return true;
		}

		return false;
	}

	public static void register(Listener l)
	{
		Bukkit.getPluginManager().registerEvents(l, Fulcrum.instance);
	}

	public static void unregister(Listener l)
	{
		HandlerList.unregisterAll(l);
	}

	@Override
	public void onDisable()
	{

	}

	public void onTick()
	{
		TICK.tick++;
	}

	public int startTask(int delay, Runnable r)
	{
		return Bukkit.getScheduler().scheduleSyncDelayedTask(instance, r, delay);
	}

	public int startRepeatingTask(int delay, int interval, Runnable r)
	{
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, r, delay, interval);
	}

	public void stopTask(int id)
	{
		Bukkit.getScheduler().cancelTask(id);
	}
}
