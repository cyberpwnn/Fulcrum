package com.volmit.fulcrum;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.volmit.dumpster.GList;
import com.volmit.dumpster.M;
import com.volmit.fulcrum.adapter.Adapter12;
import com.volmit.fulcrum.adapter.IAdapter;
import com.volmit.fulcrum.bukkit.P;
import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.fulcrum.bukkit.Task;
import com.volmit.fulcrum.bukkit.TaskLater;
import com.volmit.fulcrum.custom.CompilerFlag;
import com.volmit.fulcrum.custom.ContentHandler;
import com.volmit.fulcrum.custom.ContentManager;
import com.volmit.fulcrum.custom.ContentRegistry;
import com.volmit.fulcrum.custom.CustomAdvancement;
import com.volmit.fulcrum.custom.CustomBlock;
import com.volmit.fulcrum.custom.CustomInventory;
import com.volmit.fulcrum.custom.CustomItem;
import com.volmit.fulcrum.event.ContentRecipeRegistryEvent;
import com.volmit.fulcrum.event.ContentRegistryEvent;
import com.volmit.fulcrum.lang.C;
import com.volmit.fulcrum.net.NetworkManager;
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
	public static ContentRegistry contentRegistry;
	public static ContentHandler contentHandler;
	public static NetworkManager net;
	private int icd = 10;

	@Override
	public void onEnable()
	{
		instance = this;
		net = new NetworkManager();
		cache = new MCACache();
		adapter = new Adapter12();
		scmmgr = new SCMManager();
		contentRegistry = new ContentRegistry();
		contentHandler = new ContentHandler();
		server = new ShittyWebserver(8193, new File(getDataFolder(), "web"));
		icd = 10;

		try
		{
			server.start();
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}

		register(this);
		getCommand("fulcrum").setExecutor(this);

		new Task(0)
		{
			@Override
			public void run()
			{
				onTick();

				if(icd > 0)
				{
					icd--;
				}
			}
		};

		new TaskLater()
		{
			@Override
			public void run()
			{
				ContentManager.reloadContentManager();
			}
		};
	}

	@EventHandler
	public void on(ContentRegistryEvent e)
	{

	}

	@EventHandler
	public void on(ContentRecipeRegistryEvent e)
	{

	}

	@Override
	public void onDisable()
	{
		net.close();
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

				else if(args.length == 2 && args[0].equalsIgnoreCase("block"))
				{
					p.getInventory().addItem(ContentManager.getAny(args[1]).getItem());
				}

				else if(args.length == 2 && args[0].equalsIgnoreCase("sound"))
				{
					p.playSound(p.getLocation(), args[1], 1f, 1f);
				}

				else if(args.length == 2 && args[0].equalsIgnoreCase("item"))
				{
					p.getInventory().addItem(ContentManager.getItem(args[1]).getItem());
				}

				else if(args.length == 2 && args[0].equalsIgnoreCase("inventory"))
				{
					p.openInventory(ContentManager.createInventory(args[1]));
				}

				else if(args.length == 2 && args[0].equalsIgnoreCase("adv"))
				{
					ContentManager.getAdvancement(args[1]).grant((Player) sender);
				}

				else if(args[0].equalsIgnoreCase("qadv"))
				{
					new Task(2, 5)
					{
						@Override
						public void run()
						{
							if(M.r(0.25))
							{
								adapter.sendAdvancementIntense((Player) sender, new ItemStack(new GList<Material>(Material.values()).pickRandom()), C.UNDERLINE + "Intense Test\nAllows up to\n" + C.GREEN + "Three Lines im sure" + C.MAGIC + ".");
							}

							else
							{
								adapter.sendAdvancementSubtle((Player) sender, new ItemStack(new GList<Material>(Material.values()).pickRandom()), C.UNDERLINE + "Subtle Test\nAllows up to\n" + C.GREEN + "Three Lines im sure" + C.MAGIC + ".");
							}
						}
					};
				}

				else if(args[0].equalsIgnoreCase("pull"))
				{
					String rid = contentRegistry.getRid();
					p.sendMessage("Merging with #" + rid);
					Fulcrum.adapter.sendResourcePackWeb(p, rid + ".zip");
				}

				else if(args[0].equalsIgnoreCase("fix"))
				{
					adapter.resetSpawnerRotation(P.targetBlock(p, 8));
				}

				else if(args[0].equalsIgnoreCase("list"))
				{
					for(CustomInventory i : ContentManager.getInventories())
					{
						sender.sendMessage("INVENTORY: " + i.getId());
					}

					for(CustomBlock i : ContentManager.getBlocks())
					{
						sender.sendMessage("BLOCK: " + i.getId());
					}

					for(CustomItem i : ContentManager.getItems())
					{
						sender.sendMessage("ITEM: " + i.getId());
					}

					for(CustomAdvancement i : contentRegistry.getAdvancements())
					{
						sender.sendMessage("ADV: " + i.getId());
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

		if(ContentManager.reload && icd <= 0)
		{
			ContentManager.reload = false;
			icd = 10;

			new Thread("Fulcrum Resource Compiler")
			{
				@Override
				public void run()
				{
					try
					{
						//@fuckboy:on
						contentRegistry.compileResources(
								CompilerFlag.PREDICATE_MINIFICATION,
								CompilerFlag.PREDICATE_CYCLING,
								CompilerFlag.VERBOSE,
								CompilerFlag.OVERBOSE,
								CompilerFlag.CONCURRENT_REGISTRY,
								CompilerFlag.REGISTER_DEBUG_ITEMS,
								CompilerFlag.JSON_MINIFICATION);
						//@fuckboy:off
					}

					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}.start();
		}
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
