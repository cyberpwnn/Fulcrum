package com.volmit.fulcrum;

import java.util.Collections;

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
import org.bukkit.plugin.java.JavaPlugin;

import com.volmit.fulcrum.adapter.Adapter12;
import com.volmit.fulcrum.adapter.IAdapter;
import com.volmit.fulcrum.bukkit.BlockType;
import com.volmit.fulcrum.bukkit.P;
import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.fulcrum.bukkit.Task;
import com.volmit.fulcrum.bukkit.TaskLater;
import com.volmit.fulcrum.lang.F;
import com.volmit.fulcrum.lang.GList;
import com.volmit.fulcrum.lang.GMap;
import com.volmit.fulcrum.lang.GSet;
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

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command.getName().equalsIgnoreCase("fulcrum"))
		{
			Player p = P.getAnyPlayer();
			Location ll = P.targetBlock(p, 332);

			if(args[0].equalsIgnoreCase("sing"))
			{
				adapter.setBlock(ll, new BlockType(Material.SPONGE));
				return true;
			}

			if(args[0].equalsIgnoreCase("mul"))
			{
				for(int i = 0; i < 4; i++)
				{
					for(int j = 0; j < 4; j++)
					{
						adapter.setBlock(ll.getChunk().getBlock(i, ll.getBlockY(), j).getLocation(), new BlockType(Material.SPONGE));
					}
				}

				return true;
			}

			if(args[0].equalsIgnoreCase("map"))
			{
				for(int i = 0; i < 16; i++)
				{
					for(int j = 0; j < 16; j++)
					{
						for(int k = 0; k < 256; k++)
						{
							if(ll.getChunk().getBlock(i, k, j).getType().isSolid())
							{
								adapter.setBlock(ll.getChunk().getBlock(i, k, j).getLocation(), new BlockType(Material.SPONGE));
							}
						}
					}
				}

				return true;
			}

			if(args[0].equalsIgnoreCase("reload"))
			{
				adapter.sendReload(ll.getChunk());
			}

			if(args[0].equalsIgnoreCase("drop"))
			{
				adapter.sendUnload(ll.getChunk());
			}

			if(args[0].equalsIgnoreCase("refresh"))
			{
				int[] k = {0};
				int[] s = {0};
				GMap<Integer, GSet<Chunk>> g = new GMap<Integer, GSet<Chunk>>();
				GList<Chunk> order = new GList<Chunk>();
				for(Chunk i : ((Player) sender).getWorld().getLoadedChunks())
				{
					if(P.isWithinViewDistance((Player) sender, i))
					{
						int dist = Math.abs(i.getX() - ((Player) sender).getLocation().getChunk().getX()) + Math.abs(i.getZ() - ((Player) sender).getLocation().getChunk().getZ());
						if(!g.containsKey(dist))
						{
							g.put(dist, new GSet<Chunk>());
						}

						g.get(dist).add(i);
					}
				}

				GList<Integer> vx = g.k();
				Collections.sort(vx);

				for(int i : vx)
				{
					order.addAll(g.get(i));
				}

				order.removeDuplicates();

				for(Chunk i : order)
				{
					k[0]++;

					new TaskLater(k[0])
					{
						@Override
						public void run()
						{
							adapter.sendUnload(i, ((Player) sender));
							s[0]++;

							if(TICK.tick % 24 == 0)
							{
								((Player) sender).sendMessage("Refreshing: " + F.pc((double) s[0] / (double) k[0]));
							}

							if(k[0] == s[0])
							{
								((Player) sender).sendMessage("Refreshed " + F.f(k[0]) + " chunks");
							}

							new TaskLater(20)
							{
								@Override
								public void run()
								{
									adapter.makeFullyDirty(i);
								}
							};
						}
					};
				}
			}

			return true;
		}

		return false;
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
