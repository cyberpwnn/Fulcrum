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
import com.volmit.fulcrum.images.ImageBakery;
import com.volmit.fulcrum.images.PluginUtil;
import com.volmit.fulcrum.lang.M;
import com.volmit.fulcrum.resourcepack.PackNode;
import com.volmit.fulcrum.resourcepack.ResourcePack;
import com.volmit.fulcrum.resourcepack.TextureType;
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

	@Override
	public void onEnable()
	{
		instance = this;
		cache = new MCACache();
		adapter = new Adapter12();
		scmmgr = new SCMManager();
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
				if(args.length == 2 && args[0].equalsIgnoreCase("block"))
				{
					String[] k = args[1].split(":");

					if(k.length == 2)
					{
						adapter.setSpawnerType(l, k[0], Short.valueOf(k[1]));
					}

					else
					{
						sender.sendMessage("/fulcrum block iron_hoe:0 (material:dura)");
					}
				}

				else if(args.length == 2 && args[0].equalsIgnoreCase("rcdn"))
				{
					adapter.sendResourcePack((Player) sender, "http://cdn.volmit.com/r/" + args[1] + ".zip");
				}

				else if(args.length == 2 && args[0].equalsIgnoreCase("rurl"))
				{
					adapter.sendResourcePack((Player) sender, args[1]);
				}

				else if(args[0].equalsIgnoreCase("dyn"))
				{
					ResourcePack pack = new ResourcePack();
					pack.getMeta().setPackIcon(Fulcrum.class.getResource("/unknown.png"));
					pack.setResource(PackNode.texture(TextureType.ITEMS, "diamond.png"), Fulcrum.class.getResource("/smalllogo.png"));
					pack.getMeta().setPackDescription("This is desc");

					adapter.sendResourcePack(p, pack);
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
