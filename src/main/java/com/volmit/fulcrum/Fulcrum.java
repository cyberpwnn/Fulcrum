package com.volmit.fulcrum;

import java.io.File;
import java.io.IOException;

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
import org.bukkit.plugin.java.JavaPlugin;

import com.volmit.fulcrum.adapter.Adapter12;
import com.volmit.fulcrum.adapter.IAdapter;
import com.volmit.fulcrum.bukkit.BlockType;
import com.volmit.fulcrum.bukkit.Dimension;
import com.volmit.fulcrum.bukkit.Direction;
import com.volmit.fulcrum.bukkit.P;
import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.fulcrum.bukkit.Task;
import com.volmit.fulcrum.images.ImageBakery;
import com.volmit.fulcrum.images.PluginUtil;
import com.volmit.fulcrum.lang.M;
import com.volmit.fulcrum.world.FastBlock;
import com.volmit.fulcrum.world.FastBlock12;
import com.volmit.fulcrum.world.FastChunk;
import com.volmit.fulcrum.world.FastChunk12;
import com.volmit.fulcrum.world.FastWorld;
import com.volmit.fulcrum.world.FastWorld12;
import com.volmit.fulcrum.world.MCACache;
import com.volmit.fulcrum.world.scm.IVariableBlockType;
import com.volmit.fulcrum.world.scm.IVariableChooser;
import com.volmit.fulcrum.world.scm.IVolume;
import com.volmit.fulcrum.world.scm.MappedVolume;

public class Fulcrum extends JavaPlugin implements CommandExecutor, Listener
{
	public static Fulcrum instance;
	public static IAdapter adapter;
	public static MCACache cache;
	public static long ms = M.ms();

	@Override
	public void onEnable()
	{
		instance = this;
		cache = new MCACache();
		adapter = new Adapter12();

		try
		{
			ImageBakery.scan(new File(getDataFolder().getParentFile(), PluginUtil.getPluginFileName(getName())), "fulcrum");
			System.out.println("Loaded images");
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}

		getServer().getPluginManager().registerEvents(this, this);
		getCommand("fulcrum").setExecutor(this);

		new Task(0)
		{
			@Override
			public void run()
			{
				onTick();
			}
		};
	}

	@Override
	public void onDisable()
	{
		flushCache();
	}

	public static int getCacheSize()
	{
		return cache.size();
	}

	public static void flushCache()
	{
		try
		{
			cache.flush();
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}
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
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command.getName().equalsIgnoreCase("fulcrum"))
		{
			Player p = (Player) sender;
			Location l = P.targetBlock(p, 100);
			IVolume v = new MappedVolume(new Dimension(5, 5, 5));
			v.clear(IVariableBlockType.anythingExcept(new BlockType(Material.STONE)));
			v.set(0, 0, 0, IVariableBlockType.anyVariantOf(Material.STONE));
			v.set(0, 1, 0, IVariableBlockType.anyVariantOf(Material.STONE));
			v.set(0, 2, 0, IVariableBlockType.anyVariantOf(Material.STONE));
			v.set(4, 4, 4, IVariableBlockType.anyVariantOf(Material.STONE));
			v.set(4, 3, 4, IVariableBlockType.anyVariantOf(Material.STONE));
			v.set(4, 2, 4, IVariableBlockType.anyVariantOf(Material.STONE));

			int k = 0;

			for(Direction i : Direction.values())
			{
				k++;

				v.rotate(Direction.N, i).place(l.clone().add(0, (v.getDimension().getHeight() + 1) * k, 0), new IVariableChooser()
				{
					@Override
					public BlockType realize(IVariableBlockType type)
					{
						if(type.isValid(new BlockType(Material.STONE)))
						{
							return new BlockType(Material.STONE);
						}

						else
						{
							return new BlockType(Material.GLASS);
						}
					}
				});
			}

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

	public void onTick()
	{
		TICK.tick++;
		cache.tick();
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
