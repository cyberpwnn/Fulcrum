package com.volmit.fulcrum;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.volmit.fulcrum.adapter.Adapter12;
import com.volmit.fulcrum.adapter.IAdapter;
import com.volmit.fulcrum.bukkit.P;
import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.fulcrum.bukkit.Task;
import com.volmit.fulcrum.custom.BlockRegistry;
import com.volmit.fulcrum.custom.BlockRenderType;
import com.volmit.fulcrum.custom.CustomBlock;
import com.volmit.fulcrum.images.ImageBakery;
import com.volmit.fulcrum.images.PluginUtil;
import com.volmit.fulcrum.lang.M;
import com.volmit.fulcrum.resourcepack.ResourcePack;
import com.volmit.fulcrum.webserver.ShittyWebserver;
import com.volmit.fulcrum.world.FastBlock;
import com.volmit.fulcrum.world.FastBlock12;
import com.volmit.fulcrum.world.FastChunk;
import com.volmit.fulcrum.world.FastChunk12;
import com.volmit.fulcrum.world.FastWorld;
import com.volmit.fulcrum.world.FastWorld12;
import com.volmit.fulcrum.world.MCACache;

public class Fulcrum extends JavaPlugin implements CommandExecutor, Listener
{
	public static Fulcrum instance;
	public static IAdapter adapter;
	public static MCACache cache;
	public static long ms = M.ms();
	public static SCMManager scmmgr;
	public static ShittyWebserver server;
	public static BlockRegistry blockRegistry;
	public static ResourcePack rsp;

	@Override
	public void onEnable()
	{
		instance = this;
		cache = new MCACache();
		adapter = new Adapter12();
		scmmgr = new SCMManager();
		blockRegistry = new BlockRegistry();
		server = new ShittyWebserver(8193, new File(getDataFolder(), "web"));

		try
		{
			ImageBakery.scan(new File(getDataFolder().getParentFile(), PluginUtil.getPluginFileName(getName())), "fulcrum");
			System.out.println("Loaded images");
			server.start();
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}

		catch(Exception e)
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

		rsp = new ResourcePack();
		blockRegistry.registerBlock(new CustomBlock("black"));
		blockRegistry.registerBlock(new CustomBlock("blue"));
		blockRegistry.registerBlock(new CustomBlock("brown"));
		blockRegistry.registerBlock(new CustomBlock("cyan"));
		blockRegistry.registerBlock(new CustomBlock("gray"));
		blockRegistry.registerBlock(new CustomBlock("green"));
		blockRegistry.registerBlock(new CustomBlock("light_blue"));
		blockRegistry.registerBlock(new CustomBlock("lime"));
		blockRegistry.registerBlock(new CustomBlock("magenta"));
		blockRegistry.registerBlock(new CustomBlock("orange"));
		blockRegistry.registerBlock(new CustomBlock("pink"));
		blockRegistry.registerBlock(new CustomBlock("purple"));
		blockRegistry.registerBlock(new CustomBlock("red"));
		blockRegistry.registerBlock(new CustomBlock("silver"));
		blockRegistry.registerBlock(new CustomBlock("white"));
		blockRegistry.registerBlock(new CustomBlock("yellow"));
		blockRegistry.registerBlock(new CustomBlock("bricks2"));
		blockRegistry.registerBlock(new CustomBlock("stone3"));
		blockRegistry.registerBlock(new CustomBlock("acacia2"));
		blockRegistry.registerBlock(new CustomBlock("birch2"));
		blockRegistry.registerBlock(new CustomBlock("dark_oak2"));
		blockRegistry.registerBlock(new CustomBlock("jungle2"));
		blockRegistry.registerBlock(new CustomBlock("oak2"));
		blockRegistry.registerBlock(new CustomBlock("spruce2"));
		blockRegistry.registerBlock(new CustomBlock("black2"));
		blockRegistry.registerBlock(new CustomBlock("blue2"));
		blockRegistry.registerBlock(new CustomBlock("brown2"));
		blockRegistry.registerBlock(new CustomBlock("cyan2"));
		blockRegistry.registerBlock(new CustomBlock("gray2"));
		blockRegistry.registerBlock(new CustomBlock("green2"));
		blockRegistry.registerBlock(new CustomBlock("light_blue2"));
		blockRegistry.registerBlock(new CustomBlock("lime2"));
		blockRegistry.registerBlock(new CustomBlock("magenta2"));
		blockRegistry.registerBlock(new CustomBlock("orange2"));
		blockRegistry.registerBlock(new CustomBlock("pink2"));
		blockRegistry.registerBlock(new CustomBlock("purple2"));
		blockRegistry.registerBlock(new CustomBlock("red2"));
		blockRegistry.registerBlock(new CustomBlock("silver2"));
		blockRegistry.registerBlock(new CustomBlock("white2"));
		blockRegistry.registerBlock(new CustomBlock("yellow2"));
		blockRegistry.registerBlock(new CustomBlock("test_manual", BlockRenderType.MANUAL));
		blockRegistry.registerBlock(new CustomBlock("test_top", BlockRenderType.TOP));
		blockRegistry.registerBlock(new CustomBlock("test_bottom_top", BlockRenderType.TOP_BOTTOM));
		blockRegistry.registerBlock(new CustomBlock("test_column", BlockRenderType.COLUMN));
		blockRegistry.registerBlock(new CustomBlock("blue_ped", BlockRenderType.PEDISTAL));

		try
		{
			blockRegistry.compileResources(rsp);
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable()
	{
		server.stop();
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
			Location l = P.targetBlock(p, 32);

			if(args.length > 0)
			{
				if(args.length == 2 && args[0].equalsIgnoreCase("rcdn"))
				{
					adapter.sendResourcePack((Player) sender, "http://cdn.volmit.com/r/" + args[1] + ".zip");
				}

				else if(args.length == 2 && args[0].equalsIgnoreCase("rurl"))
				{
					adapter.sendResourcePack((Player) sender, args[1]);
				}

				else if(args.length == 2 && args[0].equalsIgnoreCase("give"))
				{
					p.getInventory().addItem(blockRegistry.getItem(args[1], 64));
				}

				else if(args[0].equalsIgnoreCase("dyn"))
				{
					adapter.sendResourcePack(p, rsp);
				}

				else if(args[0].equalsIgnoreCase("list"))
				{
					for(String i : blockRegistry.getIdblocks().k())
					{
						sender.sendMessage(i);
					}
				}
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
		scmmgr.onTick();
	}

	public static void callEvent(Event e)
	{
		Bukkit.getPluginManager().callEvent(e);
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
