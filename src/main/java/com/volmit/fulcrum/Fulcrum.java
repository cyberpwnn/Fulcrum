package com.volmit.fulcrum;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.volmit.fulcrum.adapter.Adapter12;
import com.volmit.fulcrum.adapter.IAdapter;
import com.volmit.fulcrum.command.CommandFulcrum;
import com.volmit.fulcrum.custom.CompilerFlag;
import com.volmit.fulcrum.custom.ContentHandler;
import com.volmit.fulcrum.custom.ContentManager;
import com.volmit.fulcrum.custom.ContentRegistry;
import com.volmit.fulcrum.net.NetworkManager;
import com.volmit.fulcrum.webserver.ShittyWebserver;
import com.volmit.fulcrum.world.FastBlock;
import com.volmit.fulcrum.world.FastBlock12;
import com.volmit.fulcrum.world.FastChunk;
import com.volmit.fulcrum.world.FastChunk12;
import com.volmit.fulcrum.world.FastWorld;
import com.volmit.fulcrum.world.FastWorld12;
import com.volmit.fulcrum.world.MCACache;
import com.volmit.volume.bukkit.VolumePlugin;
import com.volmit.volume.bukkit.command.Command;
import com.volmit.volume.bukkit.command.CommandTag;
import com.volmit.volume.bukkit.pawn.Async;
import com.volmit.volume.bukkit.pawn.Start;
import com.volmit.volume.bukkit.pawn.Stop;
import com.volmit.volume.bukkit.pawn.Tick;
import com.volmit.volume.lang.collections.GSet;
import com.volmit.volume.math.M;

@CommandTag("&c[&8Fulcrum&c]&7: ")
public class Fulcrum extends VolumePlugin implements Listener
{
	@Command
	public CommandFulcrum fu;

	public static Fulcrum instance;
	public static IAdapter adapter;
	public static MCACache cache;
	public static long ms = M.ms();
	public static SCMManager scmmgr;
	public static ShittyWebserver server;
	public static ContentRegistry contentRegistry;
	public static ContentHandler contentHandler;
	public static NetworkManager net;
	public static boolean registered = false;
	private static GSet<CompilerFlag> flags;
	private int icd = 1;

	@Start
	public void start()
	{
		instance = this;
		net = new NetworkManager();
		cache = new MCACache();
		adapter = new Adapter12();
		scmmgr = new SCMManager();
		contentRegistry = new ContentRegistry();
		contentHandler = new ContentHandler();
		flags = new GSet<CompilerFlag>();
		flags.add(CompilerFlag.PREDICATE_MINIFICATION);
		flags.add(CompilerFlag.PREDICATE_CYCLING);
		flags.add(CompilerFlag.VERBOSE);
		flags.add(CompilerFlag.OVERBOSE);
		flags.add(CompilerFlag.CONCURRENT_REGISTRY);
		flags.add(CompilerFlag.REGISTER_DEBUG_ITEMS);
		flags.add(CompilerFlag.JSON_MINIFICATION);

		register(this);
	}

	@Async
	@Start
	public void startWebServer()
	{
		server = new ShittyWebserver(8193, new File(getDataFolder(), "web"));

		try
		{
			server.start();
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Stop
	public void stop()
	{
		net.close();
		server.stop();
		flushCache();
	}

	public void setAsChildServer()
	{
		flags.add(CompilerFlag.DONT_SEND_PACK);
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

	public static void register(Listener l)
	{
		Bukkit.getPluginManager().registerEvents(l, Fulcrum.instance);
	}

	public static void unregister(Listener l)
	{
		HandlerList.unregisterAll(l);
	}

	@Tick(0)
	public void onTick() throws InterruptedException
	{
		if(icd > 0)
		{
			icd--;
		}

		cache.tick();
		scmmgr.onTick();

		if(ContentManager.reload && icd <= 0)
		{
			ContentManager.reload = false;
			icd = 1;
			registerNow();
		}
	}

	private void registerNow() throws InterruptedException
	{
		registered = false;
		Thread t = new Thread("Fulcrum Resource Compiler")
		{
			@Override
			public void run()
			{
				try
				{
					contentRegistry.compileResources(flags.toArray(new CompilerFlag[flags.size()]));
				}

				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		};

		t.start();
		t.join();
		registered = true;
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
