package com.volmit.fulcrum;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.volmit.fulcrum.adapter.Adapter12;
import com.volmit.fulcrum.adapter.IAdapter;
import com.volmit.fulcrum.bukkit.P;
import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.fulcrum.bukkit.Task;
import com.volmit.fulcrum.fx.EffectCauldronAcceptItem;
import com.volmit.fulcrum.fx.EffectCauldronAcceptRecipe;
import com.volmit.fulcrum.fx.EffectCauldronBubble;
import com.volmit.fulcrum.fx.EffectCauldronRejectRecipe;
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

			if(args[0].equalsIgnoreCase("fx"))
			{
				if(args.length == 2)
				{
					if(args[1].equalsIgnoreCase("brew-bubble"))
					{
						new Task("", 0, 200)
						{
							@Override
							public void run()
							{
								new EffectCauldronBubble().play(ll);
							}
						};
					}

					if(args[1].equalsIgnoreCase("brew-accept"))
					{
						new EffectCauldronAcceptItem().play(ll);
					}
					if(args[1].equalsIgnoreCase("brew-bad"))
					{
						new EffectCauldronRejectRecipe().play(ll);
					}

					if(args[1].equalsIgnoreCase("brew-good"))
					{
						new EffectCauldronAcceptRecipe().play(ll);
					}
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
